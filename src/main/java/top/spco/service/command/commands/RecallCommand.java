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
import top.spco.api.NormalMember;
import top.spco.api.User;
import top.spco.api.message.Message;
import top.spco.api.message.MessageSource;
import top.spco.service.command.CommandMarker;
import top.spco.service.command.CommandMeta;
import top.spco.service.command.GroupAbstractCommand;
import top.spco.service.command.exceptions.CommandSyntaxException;
import top.spco.service.command.util.PermissionsValidator;
import top.spco.user.BotUser;
import top.spco.util.tuple.ImmutablePair;

/**
 * 撤回一条消息
 *
 * @author SpCo
 * @version 3.0.2
 * @since 3.0.0
 */
@CommandMarker
public class RecallCommand extends GroupAbstractCommand {
    @Override
    public String[] getLabels() {
        return new String[]{"recall", "c"};
    }

    @Override
    public String getDescriptions() {
        return "撤回一条消息";
    }

    @Override
    public void onCommand(Bot<?> bot, Interactive<?> from, User<?> sender, BotUser user, Message<?> message, int time, CommandMeta meta, String usageName) throws CommandSyntaxException {
        if (PermissionsValidator.isMemberAdmin(from, user, message)) {
            ImmutablePair<MessageSource<?>, Message<?>> quote = SpCoBot.getInstance().getMessageService().getQuote(message);
            if (quote == null) {
                from.quoteReply(message, "请在回复消息时使用该命令");
                return;
            }
            if (PermissionsValidator.verifyBotPermissions(from, message, (NormalMember<?>) sender, false)) {
                SpCoBot.getInstance().getMessageService().recall(quote.getLeft());
                from.quoteReply(message, "已撤回");
            } else if (quote.getLeft().getFromId() == SpCoBot.getInstance().botId) {
                SpCoBot.getInstance().getMessageService().recall(quote.getLeft());
                from.quoteReply(message, "已撤回");
            } else {
                from.quoteReply(message, "机器人权限不足");
            }
        }
    }
}