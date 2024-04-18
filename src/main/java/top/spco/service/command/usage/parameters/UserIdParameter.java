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
 * @version 3.0.0
 * @since 3.0.0
 */
public class UserIdParameter extends Parameter<Long> {
    public UserIdParameter(String name, boolean isOptional, Long defaultValue) {
        super(name, isOptional, defaultValue);
    }

    @Override
    public Long parse(Parser parser) throws CommandSyntaxException {
        final int start = parser.getCursor();
        String value = parser.readString();
        Matcher atMatcher = Pattern.compile("@(\\w+)").matcher(value);
        if (SpCoBot.getInstance().getMessageService().isAtFormat(value)) {
            Pattern pattern = Pattern.compile(SpCoBot.getInstance().getMessageService().getAtRegex());
            Matcher matcher = pattern.matcher(value);
            return Long.parseLong(matcher.group(1));
        } else if (atMatcher.find()) {
            try {
                String id = atMatcher.group(1);
                return Long.parseLong(id);
            } catch (NumberFormatException e) {
                parser.setCursor(start);
                throw BuiltInExceptions.createWithContext("需要用户ID或@一位用户", parser);
            }
        } else {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                parser.setCursor(start);
                throw BuiltInExceptions.createWithContext("需要用户ID或@一位用户", parser);
            }
        }
    }
}