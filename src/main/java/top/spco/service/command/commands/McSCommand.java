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
package top.spco.service.command.commands;

import com.google.gson.JsonObject;
import top.spco.api.Bot;
import top.spco.api.Group;
import top.spco.api.Interactive;
import top.spco.api.User;
import top.spco.api.message.Message;
import top.spco.events.MessageEvents;
import top.spco.service.command.*;
import top.spco.service.command.util.PermissionsValidator;
import top.spco.service.mcs.McS;
import top.spco.service.mcs.McSManager;
import top.spco.service.mcs.Payload;
import top.spco.user.BotUser;
import top.spco.util.tuple.Pair;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @author SpCo
 * @version 2.0.8
 * @since 2.0.3
 */
@CommandMarker
public class McSCommand extends GroupAbstractCommand {
    private static McSManager manager;

    @Override
    public void init() {
        manager = McSManager.getInstance();
        MessageEvents.GROUP_MESSAGE.register((bot, source, sender, message, time) -> {
            if (message.isCommandMessage()) {
                return;
            }
            McS mcS = manager.getMcS(source);
            if (mcS != null) {
                JsonObject data = new JsonObject();
                data.addProperty("type", "GROUP_MESSAGE");
                data.addProperty("sender_name", sender.getNick());
                data.addProperty("message", message.toMessageContext());
                mcS.send(new Payload(5, data, "DISPATCH"));
            }
        });

    }

    @Override
    public String[] getLabels() {
        return new String[]{"mcs"};
    }

    @Override
    public String getDescriptions() {
        return "Minecraft服务器绑定和操作";
    }

    @Override
    public List<CommandUsage> getUsages() {
        return List.of(
                new CommandUsage(getLabels()[0], "查看此群所绑定的服务器"),
                new CommandUsage(getLabels()[0], "将某个服务器绑定到此群",
                        new CommandParam("操作类型", CommandParam.ParamType.REQUIRED, CommandParam.ParamContent.SELECTION, "bind"),
                        new CommandParam("IP", CommandParam.ParamType.REQUIRED, CommandParam.ParamContent.TEXT),
                        new CommandParam("端口", CommandParam.ParamType.OPTIONAL, CommandParam.ParamContent.INTEGER)),
                new CommandUsage(getLabels()[0], "将某个服务器从此群解绑",
                        new CommandParam("操作类型", CommandParam.ParamType.REQUIRED, CommandParam.ParamContent.SELECTION, "unbind")),
                new CommandUsage(getLabels()[0], "向该群绑定的服务器发送命令",
                        new CommandParam("操作类型", CommandParam.ParamType.REQUIRED, CommandParam.ParamContent.SELECTION, "execute"),
                        new CommandParam("命令", CommandParam.ParamType.REQUIRED, CommandParam.ParamContent.TEXT)),
                new CommandUsage(getLabels()[0], "连接到该群已绑定的服务器",
                        new CommandParam("操作类型", CommandParam.ParamType.REQUIRED, CommandParam.ParamContent.SELECTION, "connect")),
                new CommandUsage(getLabels()[0], "获取服务器在线玩家",
                        new CommandParam("操作类型", CommandParam.ParamType.REQUIRED, CommandParam.ParamContent.SELECTION, "online")),
                new CommandUsage(getLabels()[0], "断开与服务器的连接",
                        new CommandParam("操作类型", CommandParam.ParamType.REQUIRED, CommandParam.ParamContent.SELECTION, "disconnect")),
                new CommandUsage(getLabels()[0], "开关调试模式",
                        new CommandParam("操作类型", CommandParam.ParamType.REQUIRED, CommandParam.ParamContent.SELECTION, "debug"))
        );
    }

    @Override
    public void onCommand(Bot<?> bot, Interactive<?> from, User<?> sender, BotUser user, Message<?> message, int time, CommandMeta meta, String usageName) {
        Group<?> group = (Group<?>) from;
        switch (usageName) {
            case "查看此群所绑定的服务器" -> {
                if (!PermissionsValidator.isMemberAdmin(from, user, message)) {
                    return;
                }
                if (manager.isBound(group)) {
                    Pair<String, Integer> hP = manager.getServer(group);
                    from.quoteReply(message, "该群绑定的服务器为：" + hP.getKey() + ":" + hP.getValue());
                } else {
                    from.quoteReply(message, "该群尚未绑定服务器");
                }
            }
            case "将某个服务器绑定到此群" -> {
                if (!PermissionsValidator.isMemberAdmin(from, user, message)) {
                    return;
                }
                String host = meta.argument(1);
                int port;
                if (meta.getArgs().length == 3) {
                    port = meta.integerArgument(2);
                } else {
                    port = 58964;
                }
                try {
                    manager.bind(group, host, port);
                    from.quoteReply(message, "绑定成功");
                } catch (SQLException e) {
                    from.handleException(message, e);
                }
            }
            case "将某个服务器从此群解绑" -> {
                if (!PermissionsValidator.isMemberAdmin(from, user, message)) {
                    return;
                }
                try {
                    if (manager.unbind(group) < 1) {
                        from.quoteReply(message, "该群尚未绑定服务器");
                    } else {
                        from.quoteReply(message, "解绑成功");
                    }
                } catch (SQLException e) {
                    from.handleException(message, e);
                }
            }
            case "向该群绑定的服务器发送命令" -> {
                if (!PermissionsValidator.isMemberAdmin(from, user, message)) {
                    return;
                }
                if (!manager.isBound(group)) {
                    from.quoteReply(message, "该群尚未绑定服务器");
                    return;
                }
                McS mcS = manager.getMcS(group);
                if (mcS == null) {
                    from.quoteReply(message, "尚未与服务器建立连接");
                    return;
                }
                mcS.executeCommand(meta.argument(1), message);
            }
            case "连接到该群已绑定的服务器" -> {
                if (!PermissionsValidator.isMemberAdmin(from, user, message)) {
                    return;
                }
                if (manager.isBound(group)) {
                    try {
                        manager.connect(group, message);
                    } catch (IOException e) {
                        from.handleException(message, "连接时发生异常", e);
                    }
                } else {
                    from.quoteReply(message, "该群尚未绑定服务器");
                }
            }
            case "获取服务器在线玩家" -> {
                if (manager.isBound(group)) {
                    McS mcS = manager.getMcS(group);
                    if (mcS == null) {
                        from.quoteReply(message, "尚未与服务器建立连接");
                        return;
                    }
                    mcS.executeCommand("list", message);
                } else {
                    from.quoteReply(message, "该群尚未绑定服务器");
                }
            }
            case "断开与服务器的连接" -> {
                if (!PermissionsValidator.isMemberAdmin(from, user, message)) {
                    return;
                }
                if (manager.isBound(group)) {
                    McS mcS = manager.getMcS(group);
                    if (mcS == null) {
                        from.quoteReply(message, "尚未与服务器建立连接");
                        return;
                    }
                    mcS.close(false);
                } else {
                    from.quoteReply(message, "该群尚未绑定服务器");
                }
            }
            case "开关调试模式" -> {
                if (!PermissionsValidator.isMemberAdmin(from, user, message)) {
                    return;
                }
                if (manager.isBound(group)) {
                    McS mcS = manager.getMcS(group);
                    if (mcS == null) {
                        from.quoteReply(message, "尚未与服务器建立连接");
                        return;
                    }
                    boolean debug = mcS.toggleDebug();
                    from.quoteReply(message, "调试模式：" + (debug ? "已开启" : "已关闭"));
                } else {
                    from.quoteReply(message, "该群尚未绑定服务器");
                }
            }
        }
    }
}