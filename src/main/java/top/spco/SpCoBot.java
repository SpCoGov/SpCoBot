/*
 * Copyright 2025 SpCo
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
import top.spco.api.message.service.MessageService;
import top.spco.config.Configs;
import top.spco.core.CAATP;
import top.spco.core.database.DataBase;
import top.spco.core.module.ModuleManager;
import top.spco.events.*;
import top.spco.modules.AutoSign;
import top.spco.modules.EchoMute;
import top.spco.modules.ValorantResponder;
import top.spco.modules.reply.CustomReplyModule;
import top.spco.service.chat.ChatDispatcher;
import top.spco.service.chat.ChatType;
import top.spco.service.command.Command;
import top.spco.service.command.CommandDispatcher;
import top.spco.service.command.commands.SignCommand;
import top.spco.service.dashscope.DashScopeDispatcher;
import top.spco.service.statistics.StatisticsDispatcher;
import top.spco.statistics.GroupStatistics;
import top.spco.statistics.Statistic;
import top.spco.trade.RechargeSystem;
import top.spco.user.BotUser;
import top.spco.user.BotUsers;
import top.spco.util.ExceptionUtil;

import java.io.File;
import java.util.Objects;

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
 * @version 5.0.0
 * @since 0.1.0
 */
public class SpCoBot {
    private static SpCoBot instance;
    public static final Logger LOGGER = LogManager.getLogger("SpCoBot");
    public static File dataFolder;
    public static File configFolder;
    public static File cacheFolder;
    public static File jarFile;
    public String botId;
    public String botOwnerId;
    public String testGroupId;
    private CommandDispatcher commandDispatcher;
    public final ChatDispatcher chatDispatcher = ChatDispatcher.getInstance();
    public final StatisticsDispatcher statisticsDispatcher = StatisticsDispatcher.getInstance();
    public final DashScopeDispatcher dashScopeDispatcher = DashScopeDispatcher.getInstance();
    public final ModuleManager moduleManager = ModuleManager.getInstance();
    private RechargeSystem rechargeSystem;
    private MessageService messageService;
    private DataBase dataBase;
    private Bot bot;
    private CAATP caatp;
    private final Statistic runtimeStatistic = new Statistic();
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
    public static final String MAIN_VERSION = "5.0.0";
    public static final String VERSION = "v" + MAIN_VERSION + "-1";
    public static final String UPDATED_TIME = "2026-02-12 04:09";

    private SpCoBot() {
        GroupStatistics receiveMessageGroup = new GroupStatistics("收到消息");
        receiveMessageGroup.start("群消息", "条");
        receiveMessageGroup.start("好友消息", "条");
        receiveMessageGroup.start("群临时消息", "条");
        runtimeStatistic.add(receiveMessageGroup);
        GroupStatistics sendMessageGroup = new GroupStatistics("发出消息");
        sendMessageGroup.start("群消息", "条");
        sendMessageGroup.start("好友消息", "条");
        sendMessageGroup.start("群临时消息", "条");
        runtimeStatistic.add(sendMessageGroup);
        initEvents();
    }

    public void initOthers() {
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            throw new IllegalArgumentException("Failed to create data folder: " + dataFolder.getAbsolutePath());
        }
        if (!configFolder.exists() && !configFolder.mkdirs()) {
            throw new IllegalArgumentException("Failed to create config folder: " + configFolder.getAbsolutePath());
        }
        if (!jarFile.exists() && !jarFile.mkdirs()) {
            throw new IllegalArgumentException("Failed to create jar folder: " + jarFile.getAbsolutePath());
        }
        if (!cacheFolder.exists() && !cacheFolder.mkdirs()) {
            throw new IllegalArgumentException("Failed to create cache folder: " + cacheFolder.getAbsolutePath());
        }
        this.dataBase = new DataBase();
        this.caatp = CAATP.getInstance();
        Configs.init();
        if (Configs.BOT.isEnableRechargeSystem()) {
            try {
                rechargeSystem = RechargeSystem.getInstance();
            } catch (Exception e) {
                LOGGER.error("创建充值系统失败。", e);
            }
        }
        botId = Configs.BOT.getBotId();
        botOwnerId = Configs.BOT.getOwnerId();
        testGroupId = Configs.BOT.getTestGroup();
        commandDispatcher = CommandDispatcher.getInstance();
        initModules();
    }

    private void initModules() {
        moduleManager.register(new AutoSign(), true);
        moduleManager.register(new EchoMute(), true);
        moduleManager.register(new ValorantResponder(), false);
        moduleManager.register(new CustomReplyModule(), false);
        //moduleManager.register(new WikiRender(), false);
    }

    private void initEvents() {
        if (registered) {
            return;
        }
        registered = true;
        MessageEvents.PRIVATE_MESSAGE_RECALL.register((bot1, sender, operator, recalledMessage) -> LOGGER.info("{}({})撤回了一条自己的消息", operator.getNick(), operator.getId()));
        MessageEvents.GROUP_MESSAGE_RECALL.register((bot1, source, sender, operator, recalledMessage) -> LOGGER.info("{}({})在{}({})撤回了一条{}({})的消息", operator.getNick(), operator.getId(), source.getName(), source.getId(), sender.getNick(), sender.getId()));
        BotEvents.ONLINE_TICK.register(bot1 -> {
            String id = bot1.getId();
            LOGGER.info("机器人({})上线。", id);
            if (!Objects.equals(id, botId)) {
                LOGGER.error("登录的账号与配置项不匹配。登录的账号: {}, 配置的账号: {}", id, botId);
                System.exit(-2);
            }
        });
        BotEvents.OFFLINE_TICK.register(bot1 -> LOGGER.info("机器人({})下线。", bot1.getId()));
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
        // 自动接收入群邀请
        GroupEvents.REQUEST_JOIN_GROUP.register((eventId, fromId, group, behavior) -> {
            LOGGER.info("{}申请加入群{}({})。", fromId, group.getName(), group.getId());
        });
        // 处理私聊消息
        MessageEvents.PRIVATE_MESSAGE.register((bot, sender, message, time) -> {
            String context = message.toMessageContext();
            LOGGER.info("收到了{}({})的私聊消息: {}", sender.getNick(), sender.getId(), context);
            if (this.chatDispatcher.isInChat(sender, ChatType.FRIEND)) {
                this.chatDispatcher.onMessage(ChatType.FRIEND, bot, sender, sender, message, time);
                return;
            }
            if (context.startsWith(CommandDispatcher.COMMAND_START_SYMBOL)) {
                CommandEvents.COMMAND.invoker().onCommand(bot, sender, sender, message, time);
                CommandEvents.FRIEND_COMMAND.invoker().onPrivateCommand(bot, sender, message, time);
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
                    source.quoteReply(message, "SpCoBot获取用户时失败: \n" + ExceptionUtil.getStackTraceAsString(e));
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
                    source.quoteReply(message, "SpCoBot获取用户时失败: \n" + ExceptionUtil.getStackTraceAsString(e));
                }
                return;
            }
        });
    }

    public Statistic getRuntimeStatistic() {
        return runtimeStatistic;
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

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    public CommandDispatcher getCommandDispatcher() {
        return commandDispatcher;
    }

    public MessageService getMessageService() {
        return messageService;
    }

    public synchronized static SpCoBot getInstance() {
        if (instance == null) {
            instance = new SpCoBot();
        }
        return instance;
    }

    public RechargeSystem getRechargeSystem() {
        return rechargeSystem;
    }
}