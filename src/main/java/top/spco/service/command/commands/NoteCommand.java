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
import top.spco.service.FileManipulation;
import top.spco.service.command.AbstractCommand;
import top.spco.service.command.CommandMeta;
import top.spco.user.BotUser;
import top.spco.user.UserPermission;

import java.io.File;
import java.util.Objects;

/**
 * @author SpCo
 * @version 2.0.0
 * @since 0.3.2
 */
public class NoteCommand extends AbstractCommand {
    @Override
    public String[] getLabels() {
        return new String[]{"note", "n"};
    }

    @Override
    public String getDescriptions() {
        return "记录一条文本";
    }

    @Override
    public UserPermission needPermission() {
        return UserPermission.OWNER;
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public void onCommand(Bot<?> bot, Interactive<?> from, User<?> sender, BotUser user, Message<?> message, int time, CommandMeta meta, String usageName) {
        try {
            String context = Objects.requireNonNull(SpCoBot.getInstance().getMessageService().getQuote(message)).getRight().toMessageContext();
            if (!context.endsWith("，不打")) {
                context += "，不打";
            }
            new FileManipulation(SpCoBot.configFolder + File.separator + "valorant.spco").writeToFile(context + "\n");
            from.quoteReply(message, "已记录「" + context + "」");
        } catch (NullPointerException e) {
            from.quoteReply(message, "请在回复消息时使用此命令");
        }
    }
}