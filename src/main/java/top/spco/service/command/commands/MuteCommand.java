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
import top.spco.service.command.util.PermissionsValidator;
import top.spco.user.BotUser;

import java.util.List;

/**
 * @author SpCo
 * @version 2.0.0
 * @since 0.3.0
 */
public class MuteCommand extends GroupAbstractCommand {
    @Override
    public String[] getLabels() {
        return new String[]{"mute"};
    }

    @Override
    public String getDescriptions() {
        return "禁言一位群员";
    }

    @Override
    public List<CommandUsage> getUsages() {
        return List.of(new CommandUsage("mute", "禁言一位群员", new CommandParam("目标用户", CommandParam.ParamType.REQUIRED, CommandParam.ParamContent.TARGET_USER_ID),
                new CommandParam("禁言时间", CommandParam.ParamType.REQUIRED, CommandParam.ParamContent.INTEGER)));
    }

    @Override
    public void onCommand(Bot<?> bot, Interactive<?> from, User<?> sender, BotUser user, Message<?> message, int time, CommandMeta meta, String usageName) {
        try {
            long id = meta.targetUserIdArgument(0);
            NormalMember<?> target = PermissionsValidator.verifyMemberPermissions(from, user, message, id);
            if (target != null) {
                int duration = meta.integerArgument(1);
                target.mute(duration);
                from.quoteReply(message, "已将 " + target.getNameCard() + "(" + target.getId() + ")" + " 禁言" + duration + "秒");
            }
        } catch (CommandSyntaxException e) {
            from.handleException(message, e.getMessage());
        }
    }
}