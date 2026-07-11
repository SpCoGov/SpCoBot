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
import top.spco.modules.reply.CustomReplyModule;
import top.spco.permission.PermissionService;
import top.spco.service.chat.ChatDispatcher;
import top.spco.service.chat.ChatType;
import top.spco.service.command.Command;
import top.spco.service.command.CommandDispatcher;
import top.spco.service.command.commands.SignCommand;
import top.spco.service.dashscope.DashScopeDispatcher;
import top.spco.service.statistics.StatisticsDispatcher;
import top.spco.statistics.GroupStatistics;
import top.spco.statistics.Statistic;
import top.spco.user.BotUser;
import top.spco.user.BotUsers;
import top.spco.util.ExceptionUtil;

import java.io.File;
import java.util.Objects;

/**
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
    private MessageService messageService;
    private DataBase dataBase;
    private Bot bot;
    private CAATP caatp;
    private final Statistic runtimeStatistic = new Statistic();
    private static boolean registered = false;
    /**
     * 版本号格式采用语义版本号(X.Y.Z)
     * <ul>
     * <li>X: major version</li>
     * <li>Y: minor version</li>
     * <li>Z: patch version</li>
     * </ul>
     * <b>Keep build.gradle version in sync when updating core version.</b>
     */
    public static final String MAIN_VERSION = "5.0.0";
    public static final String VERSION = "v" + MAIN_VERSION + "-1";
    public static final String UPDATED_TIME = "2026-02-12 04:09";

    private SpCoBot() {
        GroupStatistics receiveMessageGroup = new GroupStatistics("收到消息");
        receiveMessageGroup.start("Group messages", "count");
        receiveMessageGroup.start("Private messages", "count");
        receiveMessageGroup.start("Channel messages", "count");
        runtimeStatistic.add(receiveMessageGroup);
        GroupStatistics sendMessageGroup = new GroupStatistics("发出消息");
        sendMessageGroup.start("Group messages", "count");
        sendMessageGroup.start("Private messages", "count");
        sendMessageGroup.start("Channel messages", "count");
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
        botId = Configs.BOT.getBotId();
        botOwnerId = Configs.BOT.getOwnerId();
        testGroupId = Configs.BOT.getTestGroup();
        try {
            PermissionService.getInstance().initialize();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize permission service.", e);
        }
        commandDispatcher = CommandDispatcher.getInstance();
        initModules();
    }

    private void initModules() {
        moduleManager.register(new AutoSign(), true);
        moduleManager.register(new EchoMute(), true);
        moduleManager.register(new CustomReplyModule(), false);
        //moduleManager.register(new WikiRender(), false);
    }

    private void initEvents() {
        if (registered) {
            return;
        }
        registered = true;
        MessageEvents.PRIVATE_MESSAGE_RECALL.register((bot1, sender, operator, recalledMessage) -> LOGGER.info("{}({}) recalled a private message", operator.getName(), operator.getId()));
        MessageEvents.GROUP_MESSAGE_RECALL.register((bot1, source, sender, operator, recalledMessage) -> LOGGER.info("{}({}) recalled a message from {}({}) in {}({})", operator.getName(), operator.getId(), sender.getName(), sender.getId(), source.getName(), source.getId()));
        BotEvents.ONLINE_TICK.register(bot1 -> {
            String id = bot1.getId();
            LOGGER.info("Bot({}) online.", id);
            if (!Objects.equals(id, botId)) {
                LOGGER.error("Logged in account does not match config. logged: {}, configured: {}", id, botId);
                System.exit(-2);
            }
        });
        BotEvents.OFFLINE_TICK.register(bot1 -> LOGGER.info("Bot({}) offline.", bot1.getId()));
        FriendEvents.REQUESTED_AS_FRIEND.register((eventId, message, fromId, fromGroupId, fromGroup, behavior) -> {
            LOGGER.info("Received friend request from {}.", fromId);
            behavior.accept();
        });
        // Automatically accept group invitations
        GroupEvents.INVITED_JOIN_GROUP.register((eventId, invitorId, groupId, invitor, behavior) -> {
            LOGGER.info("Received group invitation from {}({}) for group {}.", invitor.getName(), invitorId, groupId);
            behavior.accept();
        });
        // Log group join requests
        GroupEvents.REQUEST_JOIN_GROUP.register((eventId, fromId, group, behavior) -> {
            LOGGER.info("{} requested to join group {}({}).", fromId, group.getName(), group.getId());
        });
        MessageEvents.PRIVATE_MESSAGE.register((bot, sender, message, time) -> {
            String context = message.toMessageContext();
            LOGGER.info("Received private message from {}({}): {}", sender.getName(), sender.getId(), context);
            if (this.chatDispatcher.isInChat(sender, ChatType.PRIVATE)) {
                this.chatDispatcher.onMessage(ChatType.PRIVATE, bot, sender, sender, message, time);
                return;
            }
            if (context.startsWith(CommandDispatcher.COMMAND_START_SYMBOL)) {
                CommandEvents.COMMAND.invoker().onCommand(bot, sender, sender, message, time);
                CommandEvents.PRIVATE_COMMAND.invoker().onPrivateCommand(bot, sender, message, time);
            }
        });
        // 处理群聊消息
        MessageEvents.GROUP_MESSAGE.register((bot, source, sender, message, time) -> {
            String context = message.toMessageContext();
            LOGGER.info("Received group message in {}({}) from {}({}): {}", source.getName(), source.getId(), sender.getName(), sender.getId(), context);
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
                    source.quoteReply(message, "SpCoBot failed to get user.\n" + ExceptionUtil.getStackTraceAsString(e));
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
                    source.quoteReply(message, "SpCoBot failed to get user.\n" + ExceptionUtil.getStackTraceAsString(e));
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
}