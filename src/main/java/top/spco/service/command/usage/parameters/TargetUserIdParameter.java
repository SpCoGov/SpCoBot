/*
 * Copyright 2025 SpCo
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
package top.spco.service.command.usage.parameters;

import top.spco.SpCoBot;
import top.spco.service.command.CommandDispatcher;
import top.spco.service.command.Parser;
import top.spco.service.command.exceptions.BuiltInExceptions;
import top.spco.service.command.exceptions.CommandSyntaxException;

/**
 * 指向一位用户的命令参数。通常用于表示命令执行的对象。
 *
 * @author SpCo
 * @version 4.1.0
 * @since 3.0.0
 */
public class TargetUserIdParameter extends UserIdParameter {
    public TargetUserIdParameter(String name, boolean isOptional, Long defaultValue) {
        super(name, isOptional, defaultValue);
    }

    @Override
    public Long parse(Parser parser) throws CommandSyntaxException {
        var quote = SpCoBot.getInstance().getMessageService().getQuote(parser.getMessage());
        if (quote == null) {
            final int start = parser.getCursor();
            String value = parser.readUnquotedString();
            try {
                long at = SpCoBot.getInstance().getMessageService().getFirstMentioned(parser.getMessage(), value);
                if (at != -1) {
                    return at;
                } else {
                    throw BuiltInExceptions.createWithContext("需要用户ID或@一位用户", parser);
                }
            } catch (CommandSyntaxException e) {
                parser.setCursor(start);
                throw BuiltInExceptions.createWithContext("需要用户ID或@一位用户或在回复一条消息时发送该命令", parser);
            }
        } else {
            return quote.getLeft().getSenderId();
        }
    }

    @Override
    public String toString() {
        return CommandDispatcher.USAGE_TARGET_USER_ID_OPEN + name + CommandDispatcher.USAGE_TARGET_USER_ID_CLOSE;
    }
}