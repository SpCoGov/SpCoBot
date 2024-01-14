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
import top.spco.service.command.commands.util.PermissionsValidator;
import top.spco.user.BotUser;

import java.util.List;

/**
 * @author SpCo
 * @version 2.0.0
 * @since 0.3.3
 */
public class KickCommand extends GroupAbstractCommand {
    @Override
    public String[] getLabels() {
        return new String[]{"kick"};
    }

    @Override
    public String getDescriptions() {
        return "踢出一名群成员";
    }

    @Override
    public List<CommandUsage> getUsages() {
        return List.of(new CommandUsage("kick", "踢出一名群成员", new CommandParam("目标用户", CommandParam.ParamType.REQUIRED, CommandParam.ParamContent.TARGET_USER_ID)));
    }

    @Override
    public void onCommand(Bot<?> bot, Interactive<?> from, User<?> sender, BotUser user, Message<?> message, int time, String command, String label, String[] args, CommandMeta meta, String usageName) {
        try {
            long id = meta.targetUserIdArgument(0);
            NormalMember<?> target = PermissionsValidator.verifyMemberPermissions(from, user, message, id);
            if (target != null) {
                target.kick("您被管理员移出了本群", false);
                from.quoteReply(message, "已将 " + target.getNick() + "(" + target.getId() + ")" + " 移出本群");
            }
        } catch (CommandSyntaxException e) {
            from.handleException(message, e.getMessage());
        }
    }
}