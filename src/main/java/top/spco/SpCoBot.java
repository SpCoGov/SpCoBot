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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.spco.api.Bot;
import top.spco.api.Friend;
import top.spco.api.Group;
import top.spco.api.NormalMember;
import top.spco.api.message.service.MessageService;
import top.spco.core.CAATP;
import top.spco.core.config.BotSettings;
import top.spco.core.config.Settings;
import top.spco.core.config.SettingsVersion;
import top.spco.core.database.DataBase;
import top.spco.core.module.ModuleManager;
import top.spco.events.*;
import top.spco.modules.AutoSign;
import top.spco.modules.CustomReply;
import top.spco.modules.EchoMute;
import top.spco.modules.ValorantResponder;
import top.spco.service.chat.ChatDispatcher;
import top.spco.service.chat.ChatType;
import top.spco.service.command.Command;
import top.spco.service.command.CommandDispatcher;
import top.spco.service.command.commands.SignCommand;
import top.spco.service.dashscope.DashScopeDispatcher;
import top.spco.service.statistics.StatisticsDispatcher;
import top.spco.user.BotUser;
import top.spco.user.BotUsers;
import top.spco.util.ExceptionUtils;

import java.io.File;

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
 * @version 3.0.0
 * @since 0.1.0
 */
public class SpCoBot {
    private static SpCoBot instance;
    public static final Logger LOGGER = LogManager.getLogger("SpCoBot");
    public static File dataFolder;
    public static File configFolder;
    public static File pluginFile;
    public long botId;
    public long botOwnerId;
    public long testGroupId;
    private CommandDispatcher commandDispatcher;
    public final ChatDispatcher chatDispatcher = ChatDispatcher.getInstance();
    public final StatisticsDispatcher statisticsDispatcher = StatisticsDispatcher.getInstance();
    public final DashScopeDispatcher dashScopeDispatcher = DashScopeDispatcher.getInstance();
    public final ModuleManager moduleManager = ModuleManager.getInstance();
    private Settings settings;
    private MessageService messageService;
    private DataBase dataBase;
    private Bot<?> bot;
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
    public static final String MAIN_VERSION = "3.0.0";
    public static final String VERSION = "v" + MAIN_VERSION + "-2";
    public static final String UPDATED_TIME = "2023-04-04 11:25";
    public static final String OLDEST_SUPPORTED_CONFIG_VERSION = "0.3.2";

    private SpCoBot() {
        initEvents();
    }

    public void initOthers() {
        this.dataBase = new DataBase();
        this.caatp = CAATP.getInstance();
        initModules();
        this.settings = new Settings(configFolder.getAbsolutePath() + File.separator + "config.yaml");
        if (expired(settings.getStringProperty(SettingsVersion.CONFIG_VERSION))) {
            LOGGER.error("配置版本过时，请备份配置后删除配置重新启动机器人以生成新配置。");
            System.exit(-2);
        }
        botId = settings.getLongProperty(BotSettings.BOT_ID);
        botOwnerId = settings.getLongProperty(BotSettings.OWNER_ID);
        testGroupId = settings.getLongProperty(BotSettings.TEST_GROUP);
        this.commandDispatcher = CommandDispatcher.getInstance();
    }

    private void initModules() {
        moduleManager.add(new AutoSign(), true);
        moduleManager.add(new EchoMute(), true);
        moduleManager.add(new ValorantResponder(), true);
        moduleManager.add(new CustomReply(), true);
    }

    private void initEvents() {
        if (registered) {
            return;
        }
        registered = true;
        MessageEvents.FRIEND_MESSAGE_RECALL.register((bot1, sender, operator, recalledMessage) -> LOGGER.info("{}({})撤回了一条自己的消息", operator.getRemark(), operator.getId()));
        MessageEvents.GROUP_MESSAGE_RECALL.register((bot1, source, sender, operator, recalledMessage) -> LOGGER.info("{}({})在{}({})撤回了一条{}({})的消息", operator.getNick(), operator.getId(), source.getName(), source.getId(), sender.getNick(), sender.getId()));
        BotEvents.ONLINE_TICK.register(bot1 -> {
            long id = bot1.getId();
            LOGGER.info("机器人({})上线。", id);
            if (id != botId) {
                LOGGER.error("登录的账号与配置项不匹配。登录的账号: {}, 配置的账号: {}", id, botId);
                System.exit(-2);
            }
        });
        BotEvents.OFFLINE_TICK.register(bot1 -> LOGGER.info("机器人({})下线。", bot1.getId()));
        // 机器人被拍一拍时的提示
        UserEvents.NUDGED_TICK.register((bot, from, target, interactive, action, suffix) -> {
            if (interactive instanceof Friend<?>) {
                LOGGER.info("好友{}({}){}{}({}){}", (from instanceof Bot<?> ? "机器人" : ((Friend<?>) from).getNick()), from.getId(), action, (target instanceof Bot<?> ? "机器人" : ((Friend<?>) target).getNick()), target.getId(), suffix);
            } else if (interactive instanceof Group<?> group) {
                LOGGER.info("{}({})在{}({}){}{}({}){}", (from instanceof Bot<?> ? "机器人" : ((NormalMember<?>) from).getNick()), from.getId(), group.getName(), group.getId(), action, (target instanceof Bot<?> ? "机器人" : ((NormalMember<?>) target).getNick()), target.getId(), suffix);
            } else if (interactive instanceof NormalMember<?>) {
                LOGGER.info("{}({}){}{}({}){}", (from instanceof Bot<?> ? "机器人" : ((NormalMember<?>) from).getNick()), from.getId(), action, (target instanceof Bot<?> ? "机器人" : ((NormalMember<?>) target).getNick()), target.getId(), suffix);
            }
            if (target.getId() == this.botId) {
                interactive.sendMessage("机器人正常运行中。");
            }
        });
        // 自动接受好友请求
        FriendEvents.REQUESTED_AS_FRIEND.register((eventId, message, fromId, fromGroupId, fromGroup, behavior) -> {
            LOGGER.info("收到了{}的好友请求。", fromId);
            behavior.accept();
        });
        // 自动接受群邀请
        GroupEvents.INVITED_JOIN_GROUP.register((eventId, invitorId, groupId, invitor, behavior) -> {
            LOGGER.info("收到了{}({})的加入群{}的请求。", invitor.getNick(), invitorId, groupId);
            behavior.accept();
        });
        // 处理好友消息
        MessageEvents.FRIEND_MESSAGE.register((bot, sender, message, time) -> {
            String context = message.toMessageContext();
            LOGGER.info("收到了{}({})的好友消息: {}", sender.getNick(), sender.getId(), context);
            if (this.chatDispatcher.isInChat(sender, ChatType.FRIEND)) {
                this.chatDispatcher.onMessage(ChatType.FRIEND, bot, sender, sender, message, time);
                return;
            }
            if (context.startsWith(CommandDispatcher.COMMAND_START_SYMBOL)) {
                CommandEvents.COMMAND.invoker().onCommand(bot, sender, sender, message, time);
                CommandEvents.FRIEND_COMMAND.invoker().onFriendCommand(bot, sender, message, time);
            }
        });
        // 处理群聊消息
        MessageEvents.GROUP_MESSAGE.register((bot, source, sender, message, time) -> {
            String context = message.toMessageContext();
            LOGGER.info("在{}({})收到了{}({})的消息: {}", source.getName(), source.getId(), sender.getNick(), sender.getId(), context);
            if (this.chatDispatcher.isInChat(source, ChatType.GROUP)) {
                this.chatDispatcher.onMessage(ChatType.GROUP, bot, source, sender, message, time);
                return;
            }
            if (context.startsWith(CommandDispatcher.COMMAND_START_SYMBOL)) {
                CommandEvents.COMMAND.invoker().onCommand(bot, sender, sender, message, time);
                CommandEvents.GROUP_COMMAND.invoker().onGroupCommand(bot, source, sender, message, time);

            }
            if (context.equals("签到")) {
                Command command = this.commandDispatcher.getGroupCommand("sign");
                try {
                    BotUser botUser = BotUsers.getOrCreate(sender.getId());
                    if (command.hasPermission(botUser)) {
                        SignCommand.sign(source, botUser, message);
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
                        source.quoteReply(message, botUser.toString());
                    }
                } catch (Exception e) {
                    source.quoteReply(message, "SpCoBot获取用户时失败: \n" + ExceptionUtils.getStackTraceAsString(e));
                }
                return;
            }

        });
        // 处理群临时消息消息
        MessageEvents.GROUP_TEMP_MESSAGE.register((bot, source, sender, message, time) -> {
            String context = message.toMessageContext();
            LOGGER.info("收到了{}({})的{}({})的群临时消息: {}", sender.getNick(), sender.getId(), sender.getGroup().getName(), sender.getGroup().getId(), context);
            if (this.chatDispatcher.isInChat(source, ChatType.GROUP_TEMP)) {
                this.chatDispatcher.onMessage(ChatType.GROUP_TEMP, bot, source, sender, message, time);
                return;
            }
            if (context.startsWith(CommandDispatcher.COMMAND_START_SYMBOL)) {
                CommandEvents.COMMAND.invoker().onCommand(bot, sender, sender, message, time);
                CommandEvents.GROUP_TEMP_COMMAND.invoker().onGroupTempCommand(bot, source, message, time);
            }
        });
    }

    public CAATP getCAATP() {
        return caatp;
    }

    public void setBot(Bot<?> bot) {
        this.bot = bot;
    }

    public Bot<?> getBot() {
        return bot;
    }

    public DataBase getDataBase() {
        return dataBase;
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

    public synchronized static SpCoBot getInstance() {
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
            if (cv[i] > rv[i]) {
                return false;
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