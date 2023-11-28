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

import top.spco.SpCoBot;
import top.spco.api.*;
import top.spco.api.message.Message;
import top.spco.service.command.AbstractCommand;
import top.spco.service.command.CommandMeta;
import top.spco.service.command.CommandScope;
import top.spco.service.command.CommandSyntaxException;
import top.spco.user.BotUser;
import top.spco.user.UserPermission;

/**
 * @author SpCo
 * @version 0.3.4
 * @since 0.3.3
 */
public class KickCommand extends AbstractCommand {
    @Override
    public String[] getLabels() {
        return new String[]{"kick"};
    }

    @Override
    public String getDescriptions() {
        return "踢出一名群成员";
    }

    @Override
    public UserPermission needPermission() {
        return UserPermission.ADMINISTRATOR;
    }

    @Override
    public CommandScope getScope() {
        return CommandScope.ONLY_GROUP;
    }

    @Override
    public void onCommand(Bot bot, Interactive from, User sender, BotUser user, Message message, int time, String command, String label, String[] args, CommandMeta meta) {
        try {
            meta.max(1);
            if (from instanceof Group group) {
                if (!group.botPermission().isOperator()) {
                    from.quoteReply(message, "机器人权限不足");
                    return;
                }
                long id;
                if (args.length == 0) {
                    var quote = SpCoBot.getInstance().getMessageService().getQuote(message);
                    id = quote.getLeft().getFromId();
                } else {
                    id = meta.userIdArgument(0);
                }
                NormalMember target = group.getMember(id);
                if (target.getPermission().getLevel() >= group.botPermission().getLevel()) {
                    from.quoteReply(message, "大佬，惹不起");
                    return;
                }
                target.kick("您被管理员移出了本群", false);
                from.quoteReply(message, "已将 " + target.getNick() + "(" + target.getId() + ")" + " 移出本群");
            }
        } catch (CommandSyntaxException e) {
            from.handleException(message, e.getMessage());
        } catch (NullPointerException e) {
            from.quoteReply(message, "该用户不存在");
        }
    }
}