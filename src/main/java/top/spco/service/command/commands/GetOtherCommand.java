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

import top.spco.api.Bot;
import top.spco.api.Interactive;
import top.spco.api.User;
import top.spco.api.message.Message;
import top.spco.service.command.*;
import top.spco.user.BotUser;
import top.spco.user.BotUsers;
import top.spco.user.UserFetchException;
import top.spco.user.UserPermission;

import java.util.List;

/**
 * @author SpCo
 * @version 2.0.4
 * @since 0.3.0
 */
@CommandMarker
public class GetOtherCommand extends AbstractCommand {
    @Override
    public String[] getLabels() {
        return new String[]{"getother"};
    }

    @Override
    public String getDescriptions() {
        return "获取其他人的个人信息";
    }

    @Override
    public UserPermission needPermission() {
        return UserPermission.ADMINISTRATOR;
    }

    @Override
    public List<CommandUsage> getUsages() {
        return List.of(new CommandUsage(getLabels()[0], getDescriptions(), new CommandParam("目标用户", CommandParam.ParamType.REQUIRED, CommandParam.ParamContent.TARGET_USER_ID)));
    }

    @Override
    public void onCommand(Bot<?> bot, Interactive<?> from, User<?> sender, BotUser user1, Message<?> message, int time, CommandMeta meta, String usageName) {
        try {
            BotUser user = BotUsers.getOrCreate(meta.targetUserIdArgument(0));
            from.quoteReply(message, user.toString());
        } catch (CommandSyntaxException e) {
            from.handleException(message, e.getMessage());
        } catch (UserFetchException e) {
            from.handleException(message, "获取机器人用户时发生异常", e);
        }
    }
}