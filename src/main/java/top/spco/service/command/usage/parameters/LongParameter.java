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

import top.spco.service.command.Parser;
import top.spco.service.command.exceptions.BuiltInExceptions;
import top.spco.service.command.exceptions.CommandSyntaxException;

/**
 * 长整型数据的命令参数
 *
 * @author SpCo
 * @version 3.0.0
 * @since 3.0.0
 */
public class LongParameter extends NumberParameter<Long> {
    public LongParameter(String name, boolean isOptional, Long defaultValue, Long minimum, Long maximum) {
        super(name, isOptional, defaultValue, minimum, maximum);
    }

    public LongParameter(String name, boolean isOptional, Long defaultValue, Long minimum) {
        this(name, isOptional, defaultValue, minimum, Long.MAX_VALUE);
    }

    public LongParameter(String name, boolean isOptional, Long defaultValue) {
        this(name, isOptional, defaultValue, Long.MIN_VALUE);
    }

    @Override
    public Long parse(Parser parser) throws CommandSyntaxException {
        final int start = parser.getCursor();
        final long result = parser.readLong();
        if (result < minimum) {
            parser.setCursor(start);
            throw BuiltInExceptions.longTooLow(parser, result, minimum);
        }
        if (result > maximum) {
            parser.setCursor(start);
            throw BuiltInExceptions.longTooHigh(parser, result, maximum);
        }
        return result;
    }
}