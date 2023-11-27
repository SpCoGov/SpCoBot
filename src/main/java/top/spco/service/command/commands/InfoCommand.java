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
import top.spco.api.Bot;
import top.spco.api.Interactive;
import top.spco.api.User;
import top.spco.api.message.Message;
import top.spco.service.command.AbstractCommand;
import top.spco.service.command.CommandMeta;
import top.spco.user.BotUser;
import top.spco.user.UserPermission;

/**
 * @author SpCo
 * @version 0.3.0
 * @since 0.1.0
 */
public final class InfoCommand extends AbstractCommand {
    @Override
    public String[] getLabels() {
        return new String[]{"info", "i"};
    }

    @Override
    public String getDescriptions() {
        return "获取机器人运行状态";
    }

    @Override
    public void onCommand(Bot bot, Interactive from, User sender, BotUser user, Message message, int time, String command, String label, String[] args, CommandMeta meta) {
        if (user.toUserPermission() == UserPermission.OWNER) {
            from.quoteReply(message, "机器人运行状态: 正常\n" + (SpCoBot.getInstance().getCAATP().isConnected() ? "CAATP连接状态: 已连接" : "CAATP连接状态: 连接断开"));
        } else {
            from.quoteReply(message, "机器人正常运行中");
        }
    }
}