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
import top.spco.service.command.AbstractCommand;
import top.spco.service.command.CommandMeta;
import top.spco.service.command.CommandSyntaxException;
import top.spco.service.command.CommandScope;
import top.spco.user.BotUser;

import java.security.SecureRandom;

/**
 * @author SpCo
 * @version 1.0.0
 * @since 0.1.0
 */
public class BanmeCommand extends AbstractCommand {
    @Override
    public String[] getLabels() {
        return new String[]{"banme"};
    }

    @Override
    public String getDescriptions() {
        return "禁言我";
    }

    @Override
    public CommandScope getScope() {
        return CommandScope.ONLY_GROUP;
    }

    @Override
    public void onCommand(Bot bot, Interactive from, User sender, BotUser user, Message message, int time, String command, String label, String[] args, CommandMeta meta, String usageName) {
        if (usageName.equals("禁言我")) {
            try {
                meta.max(0);
                if (from instanceof Group group) {
                    if (!group.botPermission().isOperator()) {
                        from.quoteReply(message, "机器人权限不足");
                        return;
                    }
                    if (sender instanceof Member member) {
                        if (member.getPermission().getLevel() >= group.botAsMember().getPermission().getLevel()) {
                            from.quoteReply(message, "大佬，惹不起");
                            return;
                        }
                        int d = new SecureRandom().nextInt(1, 61);
                        member.mute(d);
                        from.quoteReply(message, "恭喜，您已被禁言" + d + "秒");
                    }
                }
            } catch (CommandSyntaxException e) {
                from.handleException(message, e.getMessage());
            }
        }
    }
}