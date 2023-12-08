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

import top.spco.api.*;
import top.spco.api.message.Message;
import top.spco.service.command.*;
import top.spco.user.BotUser;
import top.spco.user.UserPermission;

import java.util.List;

/**
 * @author SpCo
 * @version 1.0.1
 * @since 0.3.0
 */
public class UnmuteCommand extends AbstractCommand {
    @Override
    public String[] getLabels() {
        return new String[]{"unmute"};
    }

    @Override
    public String getDescriptions() {
        return "解除禁言一位群员";
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
    public List<CommandUsage> getUsages() {
        return List.of(new CommandUsage("unmute", "解除禁言一位群员", new CommandParam("目标用户", CommandParam.ParamType.REQUIRED, CommandParam.ParamContent.TARGET_USER_ID)));
    }

    @Override
    public void onCommand(Bot bot, Interactive from, User sender, BotUser user, Message message, int time, String command, String label, String[] args, CommandMeta meta, String usageName) {
        try {
            if (from instanceof Group group) {
                if (!group.botPermission().isOperator()) {
                    from.quoteReply(message, "机器人权限不足");
                    return;
                }
                long id = meta.targetUserIdArgument(0, message);
                NormalMember target = group.getMember(id);
                if (target.getPermission().getLevel() >= group.botPermission().getLevel()) {
                    from.quoteReply(message, "大佬，惹不起");
                    return;
                }
                target.unmute();
                from.quoteReply(message, "已将 " + target.getNameCard() + "(" + target.getId() + ")" + " 解除禁言");
            }
        } catch (CommandSyntaxException e) {
            from.handleException(message, e.getMessage());
        } catch (NullPointerException e) {
            from.quoteReply(message, "该用户不存在");
        }
    }
}