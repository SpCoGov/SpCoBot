/*
 * Copyright 2023 SpCo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.spco;

import top.spco.api.Bot;
import top.spco.api.Logger;
import top.spco.api.message.service.MessageService;
import top.spco.core.CAATP;
import top.spco.core.config.BotSettings;
import top.spco.core.config.Settings;
import top.spco.core.config.SettingsVersion;
import top.spco.core.database.DataBase;
import top.spco.events.*;
import top.spco.service.AutoAgreeValorant;
import top.spco.service.AutoSign;
import top.spco.service.GroupMessageRecorder;
import top.spco.service.chat.ChatDispatcher;
import top.spco.service.chat.ChatType;
import top.spco.service.command.Command;
import top.spco.service.command.CommandDispatcher;
import top.spco.service.command.CommandMeta;
import top.spco.service.command.CommandSyntaxException;
import top.spco.service.dashscope.DashScopeDispatcher;
import top.spco.service.statistics.StatisticsDispatcher;
import top.spco.user.BotUser;
import top.spco.user.BotUsers;
import top.spco.util.ExceptionUtils;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 *                   _oo0oo_
 *                  o8888888o
 *                  88" . "88
 *                  (| -_- |)
 *                  0\  =  /0
 *                ___/`---'\___
 *              .' \\|     |// '.
 *             / \\|||  :  |||// \
 *            / _||||| -:- |||||- \
 *           |   | \\\  -  /// |   |
 *           | \_|  ''\---/''  |_/ |
 *           \  .-\__  '-'  ___/-. /
 *         ___'. .'  /--.--\  `. .'___
 *      ."" '<  `.___\_<|>_/___.' >' "".
 *     | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *     \  \ `_.   \_ __\ /__ _/   .-` /  /
 * =====`-.____`.___ \_____/___.-`___.-'=====
 *                   `=---='
 *            佛祖保佑机器人不被腾讯风控
 * </pre>
 *
 * @author SpCo
 * @version 1.2.6
 * @since 0.1.0
 */
public class SpCoBot {
    private static SpCoBot instance;
    public static Logger logger;
    public static File dataFolder;
    public static File configFolder;
    public long botId;
    public long botOwnerId;
    public long testGroupId;
    private CommandDispatcher commandDispatcher;
    public final ChatDispatcher chatDispatcher = ChatDispatcher.getInstance();
    public final StatisticsDispatcher statisticsDispatcher = StatisticsDispatcher.getInstance();
    public final DashScopeDispatcher dashScopeDispatcher = DashScopeDispatcher.getInstance();
    private Settings settings;
    private MessageService messageService;
    private DataBase dataBase;
    private Bot bot;
    private CAATP caatp;
    private static boolean registered = false;
    /**
     * 版本号格式采用语义版本号(X.Y.Z)
     * <ul>
     * <li>X: 主版本号 (表示重大的、不兼容的变更)</li>
     * <li>Y: 次版本号 (表示向后兼容的新功能或改进)</li>
     * <li>Z: 修订号 (表示向后兼容的错误修复或小的改进)</li>
     * </ul>
     * <b>更新版本号(仅限核心的 Feature)时请不要忘记在 build.gradle 中同步修改版本号</b>
     */
    public static final String MAIN_VERSION = "1.2.6";
    public static final String VERSION = "v" + MAIN_VERSION + "-1";
    public static final String UPDATED_TIME = "2023-12-31 17:55";
    public static final String OLDEST_SUPPORTED_CONFIG_VERSION = "0.3.2";

    private SpCoBot() {
        initEvents();
    }

    public void initOthers() {
        this.dataBase = new DataBase();
        this.caatp = CAATP.getInstance();
        new GroupMessageRecorder();
        new AutoSign();
        new AutoAgreeValorant();
        this.settings = new Settings(configFolder.getAbsolutePath() + File.separator + "config.yaml");
        if (expired(settings.getStringProperty(SettingsVersion.CONFIG_VERSION))) {
            logger.error("配置版本过时，请备份配置后删除配置重新启动机器人以生成新配置。");
            System.exit(-2);
        }
        botId = settings.getLongProperty(BotSettings.BOT_ID);
        botOwnerId = settings.getLongProperty(BotSettings.OWNER_ID);
        testGroupId = settings.getLongProperty(BotSettings.TEST_GROUP);
        this.commandDispatcher = CommandDispatcher.getInstance();
    }

    private void initEvents() {
        if (registered) {
            return;
        }
        registered = true;
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        // 每秒钟调用一次
        scheduler.scheduleAtFixedRate(() -> PeriodicSchedulerEvents.SECOND_TICK.invoker().onSecondTick(), 0, 1, TimeUnit.SECONDS);
        // 每分钟调用一次
        scheduler.scheduleAtFixedRate(() -> PeriodicSchedulerEvents.MINUTE_TICK.invoker().onMinuteTick(), 0, 1, TimeUnit.MINUTES);
        // 插件启用时
        PluginEvents.ENABLE_PLUGIN_TICK.register(this::onEnable);
        // 插件禁用时
        PluginEvents.DISABLE_PLUGIN_TICK.register(this::onDisable);
        // 机器人被拍一拍时的提示
        BotEvents.NUDGED_TICK.register((from, target, subject, action, suffix) -> {
            if (target.getId() == this.botId) {
                subject.sendMessage("机器人正常运行中");
            }
        });
        // 自动接受好友请求
        FriendEvents.REQUESTED_AS_FRIEND.register((eventId, message, fromId, fromGroupId, fromGroup, behavior) -> behavior.accept());
        // 自动接受群邀请
        GroupEvents.INVITED_JOIN_GROUP.register((eventId, invitorId, groupId, invitor, behavior) -> behavior.accept());
        // 处理好友命令
        MessageEvents.FRIEND_MESSAGE.register((bot, sender, message, time) -> {
            String context = message.toMessageContext();
            if (this.chatDispatcher.isInChat(sender, ChatType.FRIEND)) {
                this.chatDispatcher.onMessage(ChatType.FRIEND, bot, sender, sender, message, time);
                return;
            }
            if (context.startsWith(CommandDispatcher.COMMAND_START_SYMBOL)) {
                try {
                    CommandMeta meta = new CommandMeta(context, message);
                    if (meta.getArgs() != null) {
                        CommandEvents.COMMAND.invoker().onCommand(bot, sender, sender, message, time, meta.getCommand(), meta.getLabel(), meta.getArgs(), meta);
                        CommandEvents.FRIEND_COMMAND.invoker().onFriendCommand(bot, sender, message, time, meta.getCommand(), meta.getLabel(), meta.getArgs(), meta);
                    }
                } catch (CommandSyntaxException e) {
                    sender.handleException(message, e.getMessage());
                }
            }
        });
        // 处理群聊命令
        MessageEvents.GROUP_MESSAGE.register((bot, source, sender, message, time) -> {
            String context = message.toMessageContext();
            if (this.chatDispatcher.isInChat(source, ChatType.GROUP)) {
                this.chatDispatcher.onMessage(ChatType.GROUP, bot, source, sender, message, time);
                return;
            }
            if (context.startsWith(CommandDispatcher.COMMAND_START_SYMBOL)) {
                try {
                    CommandMeta meta = new CommandMeta(context, message);
                    if (meta.getArgs() != null) {
                        CommandEvents.COMMAND.invoker().onCommand(bot, sender, sender, message, time, meta.getCommand(), meta.getLabel(), meta.getArgs(), meta);
                        CommandEvents.GROUP_COMMAND.invoker().onGroupCommand(bot, source, sender, message, time, meta.getCommand(), meta.getLabel(), meta.getArgs(), meta);
                    }
                } catch (CommandSyntaxException e) {
                    source.handleException(message, e.getMessage());
                }

            }
            if (context.equals("签到")) {
                Command command = this.commandDispatcher.getGroupCommand("sign");
                try {
                    BotUser botUser = BotUsers.getOrCreate(sender.getId());
                    if (command.hasPermission(botUser)) {
                        command.onCommand(bot, source, sender, botUser, message, time, context, "sign", new String[]{}, new CommandMeta(context, message), command.getUsages().get(0).name);
                    }
                } catch (Exception e) {
                    source.quoteReply(message, "SpCoBot获取用户时失败: \n" + ExceptionUtils.getStackTraceAsString(e));
                }
                return;
            }
            if (context.equals("个人信息")) {
                Command command = this.commandDispatcher.getGroupCommand("getme");
                try {
                    BotUser botUser = BotUsers.getOrCreate(sender.getId());
                    if (command.hasPermission(botUser)) {
                        command.onCommand(bot, source, sender, botUser, message, time, context, "getme", new String[]{}, new CommandMeta(context, message), command.getUsages().get(0).name);
                    }
                } catch (Exception e) {
                    source.quoteReply(message, "SpCoBot获取用户时失败: \n" + ExceptionUtils.getStackTraceAsString(e));
                }
                return;
            }
        });
        // 处理群临时消息命令
        MessageEvents.GROUP_TEMP_MESSAGE.register((bot, source, sender, message, time) -> {
            String context = message.toMessageContext();
            if (this.chatDispatcher.isInChat(source, ChatType.GROUP_TEMP)) {
                this.chatDispatcher.onMessage(ChatType.GROUP_TEMP, bot, source, sender, message, time);
                return;
            }
            if (context.startsWith(CommandDispatcher.COMMAND_START_SYMBOL)) {
                try {
                    CommandMeta meta = new CommandMeta(context, message);
                    if (meta.getArgs() != null) {
                        CommandEvents.COMMAND.invoker().onCommand(bot, sender, sender, message, time, meta.getCommand(), meta.getLabel(), meta.getArgs(), meta);
                        CommandEvents.GROUP_TEMP_COMMAND.invoker().onGroupTempCommand(bot, source, message, time, meta.getCommand(), meta.getLabel(), meta.getArgs(), meta);
                    }
                } catch (CommandSyntaxException e) {
                    source.handleException(message, e.getMessage());
                }
            }
        });
    }

    public CAATP getCAATP() {
        return caatp;
    }

    public void setBot(Bot bot) {
        this.bot = bot;
    }

    public Bot getBot() {
        return bot;
    }

    public DataBase getDataBase() {
        return dataBase;
    }

    private void onEnable() {
        logger.info("SpCoBot已上线");
    }

    private void onDisable() {
        logger.info("SpCoBot已下线");
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    public CommandDispatcher getCommandDispatcher() {
        return commandDispatcher;
    }

    public MessageService getMessageService() {
        return messageService;
    }

    public Settings getSettings() {
        return settings;
    }

    public static SpCoBot getInstance() {
        if (instance == null) {
            instance = new SpCoBot();
        }
        return instance;
    }

    private static boolean expired(String currentVersion) {
        if (!isValidVersion(currentVersion) || !isValidVersion(SpCoBot.OLDEST_SUPPORTED_CONFIG_VERSION)) {
            return true;
        }
        var cv = getVersionNumber(currentVersion);
        var rv = getVersionNumber(SpCoBot.OLDEST_SUPPORTED_CONFIG_VERSION);
        for (int i = 0; i < 3; i++) {
            if (cv[i] < rv[i]){
                return true;
            }
        }
        return false;
    }

    private static boolean isValidVersion(String version) {
        String regex = "\\d+\\.\\d+\\.\\d+";
        return version.matches(regex);
    }

    private static int[] getVersionNumber(String version) {
        String[] v = version.split("\\.");
        int[] vn = new int[3];
        for (int i = 0; i < 3; i++) {
            vn[i] = Integer.parseInt(v[i]);
        }
        return vn;
    }
}