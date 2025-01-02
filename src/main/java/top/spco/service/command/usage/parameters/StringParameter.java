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

import top.spco.service.command.Parser;
import top.spco.service.command.exceptions.CommandSyntaxException;

/**
 * 字符串类型的命令参数
 *
 * @author SpCo
 * @version 3.0.0
 * @since 3.0.0
 */
public class StringParameter extends Parameter<String> {
    private final StringType type;

    public StringParameter(String name, boolean isOptional, String defaultValue, StringType type) {
        super(name, isOptional, defaultValue);
        this.type = type;
    }

    @Override
    public String parse(Parser parser) throws CommandSyntaxException {
        if (type == StringType.GREEDY_PHRASE) {
            final String text = parser.getRemaining();
            parser.setCursor(parser.getTotalLength());
            return text;
        } else if (type == StringType.SINGLE_WORD) {
            return parser.readUnquotedString();
        } else {
            return parser.readString();
        }
    }

    public enum StringType {
        SINGLE_WORD("word", "words_with_underscores"),
        QUOTABLE_PHRASE("\"quoted phrase\"", "word", "\"\""),
        GREEDY_PHRASE("word", "words with spaces", "\"and symbols\""),
        ;

        StringType(final String... ignored) {
        }
    }
}