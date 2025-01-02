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

import top.spco.service.command.CommandDispatcher;
import top.spco.service.command.Parser;
import top.spco.service.command.exceptions.BuiltInExceptions;
import top.spco.service.command.exceptions.CommandSyntaxException;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * 带有选项的命令参数
 *
 * @author SpCo
 * @version 3.0.4
 * @since 3.0.0
 */
public class SelectionParameter extends StringParameter {
    private final LinkedHashSet<String> options = new LinkedHashSet<>();
    private String optionsTable = null;

    public SelectionParameter(String name, boolean isOptional, String defaultValue, String... options) {
        super(name, isOptional, defaultValue, StringType.SINGLE_WORD);
        if (options.length < 2) {
            throw new IllegalArgumentException("至少要有两个选项");
        }
        for (String option : options) {
            if (!Parser.isSingleWord(option)) {
                throw new IllegalArgumentException("默认值只能由数字、字母、下划线、负号、正号和小数点组成");
            }
        }
        if (isOptional) {
            boolean flag = false;
            for (String option : options) {
                if (option.equals(defaultValue)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                throw new IllegalArgumentException("参数的默认值必须是选项的其中一个");
            }
        }
        this.options.addAll(List.of(options));
    }

    @Override
    public String parse(Parser parser) throws CommandSyntaxException {
        final int start = parser.getCursor();
        if (!parser.canRead()) {
            parser.setCursor(start);
            throw BuiltInExceptions.createWithContext("需要" + name, parser);
        }
        while (parser.canRead() && Parser.isAllowedInUnquotedString(parser.peek())) {
            parser.skip();
        }
        String value = parser.getString().substring(start, parser.getCursor());
        if (options.contains(value)) {
            return value;
        } else {
            parser.setCursor(start);
            throw BuiltInExceptions.createWithContext("无效的" + name + "，需要“" + getOptionsTable() + "”却出现了“" + value + "”", parser);
        }
    }

    public String getOptionsTable() {
        if (optionsTable == null) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (String option : options) {
                if (!first) {
                    sb.append(CommandDispatcher.USAGE_OR);
                }
                sb.append(option);
                first = false;
            }
            optionsTable = sb.toString();
        }
        return optionsTable;
    }

    @Override
    public String toString() {
        if (isOptional) {
            return CommandDispatcher.USAGE_OPTIONAL_OPEN + getOptionsTable() + CommandDispatcher.USAGE_OPTIONAL_CLOSE;
        } else {
            return CommandDispatcher.USAGE_REQUIRED_OPEN + getOptionsTable() + CommandDispatcher.USAGE_REQUIRED_CLOSE;
        }
    }
}