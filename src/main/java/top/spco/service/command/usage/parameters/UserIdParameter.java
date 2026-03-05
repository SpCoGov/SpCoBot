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
import top.spco.service.command.Parser;
import top.spco.service.command.exceptions.BuiltInExceptions;
import top.spco.service.command.exceptions.CommandSyntaxException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 指向一位用户的命令参数
 *
 * @author SpCo
 * @version 4.1.0
 * @since 3.0.0
 */
public class UserIdParameter extends Parameter<String> {
    public UserIdParameter(String name, boolean isOptional, String defaultValue) {
        super(name, isOptional, defaultValue);
    }

    @Override
    public String parse(Parser parser) throws CommandSyntaxException {
        final int start = parser.getCursor();
        String value = parser.readUnquotedString();
        String at = SpCoBot.getInstance().getMessageService().getFirstMentioned(parser.getMessage(), value);
        if (at == null) {
            parser.setCursor(start);
            throw BuiltInExceptions.createWithContext("需要用户ID或@一位用户", parser);
        } else {
            return at;
        }
    }
}