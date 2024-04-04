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
package top.spco.service.command.usage.parameters;

import org.jetbrains.annotations.NotNull;
import top.spco.service.command.CommandDispatcher;
import top.spco.service.command.Parser;
import top.spco.service.command.exceptions.BuiltInExceptions;
import top.spco.service.command.exceptions.CommandSyntaxException;

import java.util.Arrays;

/**
 * 固定的命令参数
 *
 * @author SpCo
 * @version 3.0.0
 * @since 3.0.0
 */
public class SpecifiedParameter extends StringParameter {
    private final String[] range;

    public SpecifiedParameter(String name, boolean isOptional, @NotNull String need, @NotNull String... range) {
        super(name, isOptional, need, StringType.SINGLE_WORD);
        this.range = range;
        for (String s : range) {
            if (!Parser.isSingleWord(s)) {
                throw new IllegalArgumentException("指定值只能由数字、字母、下划线、负号、正号和小数点组成");
            }
        }
        if (Arrays.stream(range).noneMatch(target -> target.equals(need))) {
            throw new IllegalArgumentException("指定值“" + need + "”必须包含在指定范围" + Arrays.toString(range) + "中");
        }
    }

    @Override
    public String parse(Parser parser) throws CommandSyntaxException {
        final int start = parser.getCursor();
        String value = parser.readUnquotedString();
        if (value.isEmpty()) {
            parser.setCursor(start);
            throw BuiltInExceptions.createWithContext("需要" + name, parser);
        }
        if (!value.equals(defaultValue)) {
            parser.setCursor(start);
            if (match(value)) {
                throw BuiltInExceptions.createWithContext("后续参数与该" + name + "不匹配", parser);
            } else {
                throw BuiltInExceptions.createWithContext("需要" + need() + "却出现了“" + value + "”", parser);
            }
        }
        return value;
    }

    public boolean match(String s) {
        return Arrays.stream(range).anyMatch(target -> target.equals(s));
    }

    private String need() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < range.length - 1; i++) {
            sb.append("“").append(range[i]).append("”").append("或");
        }
        sb.append("“").append(range[range.length - 1]).append("”");
        return sb.toString();
    }

    @Override
    public String toString() {
        if (isOptional) {
            return CommandDispatcher.USAGE_OPTIONAL_OPEN + defaultValue + CommandDispatcher.USAGE_OPTIONAL_CLOSE;
        } else {
            return defaultValue;
        }
    }
}