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

import top.spco.SpCoBot;
import top.spco.api.Bot;
import top.spco.api.Interactive;
import top.spco.api.User;
import top.spco.api.message.Message;
import top.spco.service.command.AbstractCommand;
import top.spco.service.command.CommandMarker;
import top.spco.service.command.CommandMeta;
import top.spco.service.command.exceptions.CommandSyntaxException;
import top.spco.user.BotUser;

/**
 * @author SpCo
 * @version 3.2.2
 * @since 3.2.2
 */
@CommandMarker
public class QuoteCommand extends AbstractCommand {
    @Override
    public String[] getLabels() {
        return new String[]{"quote"};
    }

    @Override
    public String getDescriptions() {
        return "获取消息的引用信息";
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public void onCommand(Bot<?> bot, Interactive<?> from, User<?> sender, BotUser user, Message<?> message, int time, CommandMeta meta, String usageName) throws CommandSyntaxException {
        var quote = SpCoBot.getInstance().getMessageService().getQuote(message);
        if (quote == null) {
            from.quoteReply(message, "该消息没有引用一条消息");
        } else {
            from.quoteReply(message,
                    "引用消息的发送者: " + quote.getKey().getSenderId() +
                            "\n引用消息的来源: " + quote.getKey().getFromId() +
                            "\n引用消息的内容: " + quote.getRight().toMessageContext());
        }
    }
}