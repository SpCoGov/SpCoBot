/*
 * Copyright 2024 SpCo
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
import top.spco.service.command.exceptions.CommandSyntaxException;
import top.spco.user.BotUser;
import top.spco.user.UserPermission;

/**
 * 让机器人执行垃圾回收
 *
 * @author SpCo
 * @version 3.0.0
 * @since 3.0.0
 */
@CommandMarker
public class GCCommand extends AbstractCommand {
    @Override
    public String[] getLabels() {
        return new String[]{"gc"};
    }

    @Override
    public String getDescriptions() {
        return "执行垃圾回收";
    }

    @Override
    public UserPermission needPermission() {
        return UserPermission.ADMINISTRATOR;
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public void onCommand(Bot<?> bot, Interactive<?> from, User<?> sender, BotUser user, Message<?> message, int time, CommandMeta meta, String usageName) throws CommandSyntaxException {
        System.gc();
        from.quoteReply(message, "完成");
    }
}