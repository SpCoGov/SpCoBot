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
import top.spco.service.command.AbstractCommand;
import top.spco.service.command.CommandMarker;
import top.spco.service.command.CommandMeta;
import top.spco.user.BotUser;

/**
 * @author SpCo
 * @version 2.0.4
 * @since 0.1.0
 */
@CommandMarker
public class GetMeCommand extends AbstractCommand {
    @Override
    public String[] getLabels() {
        return new String[]{"getme"};
    }

    @Override
    public String getDescriptions() {
        return "获取个人信息";
    }

    @Override
    public void onCommand(Bot<?> bot, Interactive<?> from, User<?> sender, BotUser user, Message<?> message, int time, CommandMeta meta, String usageName) {
        if (usageName.equals("获取个人信息")) {
            from.quoteReply(message, user.toString());
        }
    }
}