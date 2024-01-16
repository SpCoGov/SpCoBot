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
import top.spco.service.command.CommandMeta;
import top.spco.user.BotUser;
import top.spco.user.UserOperationException;

/**
 * @author SpCo
 * @version 2.0.0
 * @since 0.1.0
 */
public class SignCommand extends AbstractCommand {
    @Override
    public String[] getLabels() {
        return new String[]{"sign"};
    }

    @Override
    public String getDescriptions() {
        return "签到";
    }

    @Override
    public void onCommand(Bot<?> bot, Interactive<?> from, User<?> sender, BotUser user, Message<?> message, int time, String command, String label, String[] args, CommandMeta meta, String usageName) {
        try {
            int i = user.sign();
            if (i == -1) {
                from.quoteReply(message, "签到失败。您今天已经签到过了。");
            } else {
                from.quoteReply(message, String.format("签到成功。您今天签到获得了%d海绵山币，您现在拥有%d海绵山币。", i, user.getSMFCoin()));
            }
        } catch (UserOperationException e) {
            from.handleException(message, "签到失败", e);
        }
    }
}