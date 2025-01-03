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
package top.spco.service.command.commands;

import com.google.gson.JsonObject;
import top.spco.SpCoBot;
import top.spco.api.Bot;
import top.spco.api.Group;
import top.spco.api.Interactive;
import top.spco.api.User;
import top.spco.api.message.Message;
import top.spco.events.BotEvents;
import top.spco.events.MessageEvents;
import top.spco.service.command.CommandMarker;
import top.spco.service.command.CommandMeta;
import top.spco.service.command.GroupAbstractCommand;
import top.spco.service.command.usage.Usage;
import top.spco.service.command.usage.UsageBuilder;
import top.spco.service.command.usage.parameters.BooleanParameter;
import top.spco.service.command.usage.parameters.HostParameter;
import top.spco.service.command.usage.parameters.IntegerParameter;
import top.spco.service.command.usage.parameters.StringParameter;
import top.spco.service.command.util.PermissionsValidator;
import top.spco.service.command.util.SpecifiedParameterHelper;
import top.spco.service.command.util.SpecifiedParameterSet;
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
 * @version 3.2.2
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
            McS mcS = manager.getMcS(source.getId());
            if (mcS != null) {
                JsonObject data = new JsonObject();
                data.addProperty("type", "GROUP_MESSAGE");
                data.addProperty("sender_name", sender.getNameCard());
                data.addProperty("message", message.toMessageContext());
                mcS.send(new Payload(5, data, "DISPATCH"));
            }
        });
        BotEvents.ONLINE_TICK.register(bot -> {
            try {
                for (Long groupId : SpCoBot.getInstance().getDataBase().getLongFieldValues("group_id", "mcs")) {
                    if (!manager.isConnected(groupId)) {
                        try {
                            manager.connect(bot.getGroup(groupId), null);
                        } catch (IOException e) {
                            bot.getGroup(groupId).handleException("自动连接McS", e);
                        }
                    }
                }
            } catch (SQLException e) {
                bot.getFriend(SpCoBot.getInstance().botOwnerId).handleException("获取McS列表绑定", e);
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
    public List<Usage> getUsages() {
        SpecifiedParameterSet specifiedParameters = new SpecifiedParameterHelper("操作类型", false).add("bind", "unbind", "execute", "connect", "online", "disconnect", "debug").build();
        return List.of(
                new UsageBuilder(getLabels()[0], "查看此群所绑定的服务器").build(),
                new UsageBuilder(getLabels()[0], "将某个服务器绑定到此群")
                        .add(specifiedParameters.get("bind"))
                        .add(new HostParameter("主机地址", false, null))
                        .add(new IntegerParameter("端口", true, 58964, 1024, 65535)).build(),
                new UsageBuilder(getLabels()[0], "将某个服务器从此群解绑")
                        .add(specifiedParameters.get("unbind")).build(),
                new UsageBuilder(getLabels()[0], "向该群绑定的服务器发送命令")
                        .add(specifiedParameters.get("execute"))
                        .add(new StringParameter("命令", false, null, StringParameter.StringType.GREEDY_PHRASE)).build(),
                new UsageBuilder(getLabels()[0], "连接到该群已绑定的服务器")
                        .add(specifiedParameters.get("connect"))
                        .add(new BooleanParameter("调试模式", true, false)).build(),
                new UsageBuilder(getLabels()[0], "获取服务器在线玩家")
                        .add(specifiedParameters.get("online")).build(),
                new UsageBuilder(getLabels()[0], "断开与服务器的连接")
                        .add(specifiedParameters.get("disconnect")).build(),
                new UsageBuilder(getLabels()[0], "开关调试模式")
                        .add(specifiedParameters.get("debug")).build()
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
                if (manager.isBound(group.getId())) {
                    Pair<String, Integer> hP = manager.getServer(group.getId());
                    from.quoteReply(message, "该群绑定的服务器为：" + hP.getKey() + ":" + hP.getValue());
                } else {
                    from.quoteReply(message, "该群尚未绑定服务器");
                }
            }
            case "将某个服务器绑定到此群" -> {
                if (!PermissionsValidator.isMemberAdmin(from, user, message)) {
                    return;
                }
                if (manager.isBound(group.getId())) {
                    from.quoteReply(message, "该群已绑定服务器，请先解绑后再次尝试");
                    return;
                }
                String host = (String) meta.getParams().get("主机地址");
                int port = (Integer) meta.getParams().get("端口");

                try {
                    manager.bind(group.getId(), host, port);
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
                    if (manager.unbind(group.getId()) < 1) {
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
                if (!manager.isBound(group.getId())) {
                    from.quoteReply(message, "该群尚未绑定服务器");
                    return;
                }
                McS mcS = manager.getMcS(group.getId());
                if (mcS == null) {
                    from.quoteReply(message, "尚未与服务器建立连接");
                    return;
                }
                mcS.executeCommand((String) meta.getParams().get("命令"), message);
            }
            case "连接到该群已绑定的服务器" -> {
                if (!PermissionsValidator.isMemberAdmin(from, user, message)) {
                    return;
                }
                if (manager.isBound(group.getId())) {
                    try {
                        boolean debug = (Boolean) meta.getParams().get("调试模式");
                        manager.connect(group, message).setDebug(debug);
                    } catch (IOException e) {
                        from.handleException(message, "连接时发生异常", e);
                    }
                } else {
                    from.quoteReply(message, "该群尚未绑定服务器");
                }
            }
            case "获取服务器在线玩家" -> {
                if (manager.isBound(group.getId())) {
                    McS mcS = manager.getMcS(group.getId());
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
                if (manager.isBound(group.getId())) {
                    McS mcS = manager.getMcS(group.getId());
                    if (mcS == null) {
                        from.quoteReply(message, "尚未与服务器建立连接");
                        return;
                    }
                    mcS.close(false, "手动关闭", true);
                } else {
                    from.quoteReply(message, "该群尚未绑定服务器");
                }
            }
            case "开关调试模式" -> {
                if (!PermissionsValidator.isMemberAdmin(from, user, message)) {
                    return;
                }
                if (manager.isBound(group.getId())) {
                    McS mcS = manager.getMcS(group.getId());
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