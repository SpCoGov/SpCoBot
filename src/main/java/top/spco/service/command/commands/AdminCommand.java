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

import top.spco.SpCoBot;
import top.spco.api.*;
import top.spco.api.message.Message;
import top.spco.service.command.CommandMarker;
import top.spco.service.command.CommandMeta;
import top.spco.service.command.GroupAbstractCommand;
import top.spco.service.command.exceptions.CommandSyntaxException;
import top.spco.user.BotUser;

/**
 * 呼叫群管理员
 *
 * @author SpCo
 * @version 3.1.0
 * @since 3.1.0
 */
@CommandMarker
public class AdminCommand extends GroupAbstractCommand {
    @Override
    public String[] getLabels() {
        return new String[]{"admin"};
    }

    @Override
    public String getDescriptions() {
        return "呼叫群管理员";
    }

    @Override
    public void onCommand(Bot<?> bot, Interactive<?> from, User<?> sender, BotUser user, Message<?> message, int time, CommandMeta meta, String usageName) throws CommandSyntaxException {
        Group<?> group = (Group<?>) from;
        InteractiveList<NormalMember<?>> members = group.getMembers();
        InteractiveList<NormalMember<?>> admins = new InteractiveList<>();
        for (var member : members) {
            if (member.getPermission().isOperator() && member.getId() != SpCoBot.getInstance().botId) {
                admins.add(member);
            }
        }
        Message<?> adminCallMessage = SpCoBot.getInstance().getMessageService().asMessage(sender.getNick() + "呼叫群管理员" + "\n");
        for (var admin : admins) {
            adminCallMessage.append("\n").append(SpCoBot.getInstance().getMessageService().at(admin.getId()));
        }

        from.quoteReply(message, adminCallMessage);
    }
}