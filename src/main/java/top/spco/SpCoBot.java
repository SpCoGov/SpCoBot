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

import top.spco.base.api.Bot;
import top.spco.base.api.Logger;
import top.spco.base.api.message.service.MessageService;
import top.spco.service.chat.ChatManager;
import top.spco.service.chat.ChatType;
import top.spco.service.command.Command;
import top.spco.service.command.CommandMeta;
import top.spco.service.command.CommandSystem;
import top.spco.events.*;
import top.spco.service.CAATP;
import top.spco.service.DataBase;
import top.spco.service.GroupMessageRecorder;
import top.spco.service.statistics.StatisticsManager;
import top.spco.user.BotUser;
import top.spco.user.UserFetchException;

import java.io.File;
import java.sql.SQLException;

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
 * @version 1.1
 * @since 1.0
 */
public class SpCoBot {
    private static SpCoBot instance;
    public static Logger logger;
    public static File dataFolder;
    public final long BOT_ID = 2758532041L;
    public final long BOT_OWNER_ID = 2247381667L;
    public final CommandSystem commandSystem = CommandSystem.getInstance();
    public final ChatManager chatManager = ChatManager.getInstance();
    public final StatisticsManager statisticsManager = StatisticsManager.getInstance();
    private MessageService messageService;
    private DataBase dataBase = null;
    private Bot bot = null;
    private CAATP caatp = null;
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
    public static final String MAIN_VERSION = "0.1.1";
    public static final String VERSION = "v" + MAIN_VERSION + "-rc";
    public static final String UPDATED_TIME = "2023-11-07 05:53";

    private SpCoBot() {
        initEvents();
    }

    public void initOthers() {
        this.dataBase = new DataBase();
        this.caatp = CAATP.getInstance();
        new GroupMessageRecorder();
    }

    private void initEvents() {
        if (registered) {
            return;
        }
        registered = true;
        // 插件启用时
        PluginEvents.ENABLE_PLUGIN_TICK.register(this::onEnable);
        // 插件禁用时
        PluginEvents.DISABLE_PLUGIN_TICK.register(this::onDisable);
        // 机器人被拍一拍时的提示
        BotEvents.NUDGED_TICK.register((from, target, subject, action, suffix) -> {
            if (target.getId() == this.BOT_ID) {
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
            CommandMeta meta = new CommandMeta(context);
            if (this.chatManager.isInChat(sender, ChatType.FRIEND)) {
                this.chatManager.onMessage(ChatType.FRIEND, bot, sender, sender, message, time);
                return;
            }
            if (meta.getArgs() != null) {
                CommandEvents.COMMAND.invoker().onCommand(bot, sender, sender, message, time, meta.getCommand(), meta.getLabel(), meta.getArgs());
                CommandEvents.FRIEND_COMMAND.invoker().onFriendCommand(bot, sender, message, time, meta.getCommand(), meta.getLabel(), meta.getArgs());
            }
        });
        // 处理群聊命令
        MessageEvents.GROUP_MESSAGE.register((bot, source, sender, message, time) -> {
            String context = message.toMessageContext();
            CommandMeta meta = new CommandMeta(context);
            if (this.chatManager.isInChat(source, ChatType.GROUP)) {
                this.chatManager.onMessage(ChatType.GROUP, bot, source, sender, message, time);
                return;
            }
            if (meta.getArgs() != null) {
                CommandEvents.COMMAND.invoker().onCommand(bot, sender, sender, message, time, meta.getCommand(), meta.getLabel(), meta.getArgs());
                CommandEvents.GROUP_COMMAND.invoker().onGroupCommand(bot, source, sender, message, time, meta.getCommand(), meta.getLabel(), meta.getArgs());
            }
            if (context.equals("签到")) {
                Command command = this.commandSystem.getGroupCommand("sign");
                try {
                    BotUser botUser = BotUser.getOrCreate(sender.getId());
                    if (command.hasPermission(botUser)) {
                        command.onCommand(bot, source, botUser, message, time, context, "sign", new String[]{});
                    }
                } catch (UserFetchException | SQLException e) {
                    source.handleException(message, "SpCoBot获取用户时失败", e);
                }
                return;
            }
            if (context.equals("个人信息")) {
                Command command = this.commandSystem.getGroupCommand("getme");
                try {
                    BotUser botUser = BotUser.getOrCreate(sender.getId());
                    if (command.hasPermission(botUser)) {
                        command.onCommand(bot, source, botUser, message, time, context, "getme", new String[]{});
                    }
                } catch (UserFetchException | SQLException  e) {
                    source.handleException(message, "SpCoBot获取用户时失败", e);
                }
                return;
            }
        });
        // 处理群临时消息命令
        MessageEvents.GROUP_TEMP_MESSAGE.register((bot, source, sender, message, time) -> {
            String context = message.toMessageContext();
            CommandMeta meta = new CommandMeta(context);
            if (this.chatManager.isInChat(source, ChatType.GROUP_TEMP)) {
                this.chatManager.onMessage(ChatType.GROUP_TEMP, bot, source, sender, message, time);
                return;
            }
            if (meta.getArgs() != null) {
                CommandEvents.COMMAND.invoker().onCommand(bot, sender, sender, message, time, meta.getCommand(), meta.getLabel(), meta.getArgs());
                CommandEvents.GROUP_TEMP_COMMAND.invoker().onGroupTempCommand(bot, source, message, time, meta.getCommand(), meta.getLabel(), meta.getArgs());
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

    public MessageService getMessageService() {
        return messageService;
    }

    public static SpCoBot getInstance() {
        if (instance == null) {
            instance = new SpCoBot();
        }
        return instance;
    }
}