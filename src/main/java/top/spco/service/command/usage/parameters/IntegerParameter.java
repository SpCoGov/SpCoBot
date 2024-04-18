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

import top.spco.service.command.Parser;
import top.spco.service.command.exceptions.BuiltInExceptions;
import top.spco.service.command.exceptions.CommandSyntaxException;

/**
 * 整型数据的命令参数
 *
 * @author SpCo
 * @version 3.0.0
 * @since 3.0.0
 */
public class IntegerParameter extends NumberParameter<Integer> {
    public IntegerParameter(String name, boolean isOptional, Integer defaultValue, final int minimum, final int maximum) {
        super(name, isOptional, defaultValue, minimum, maximum);
    }

    public IntegerParameter(String name, boolean isOptional, Integer defaultValue, final int minimum) {
        this(name, isOptional, defaultValue, minimum, Integer.MAX_VALUE);
    }

    public IntegerParameter(String name, boolean isOptional, Integer defaultValue) {
        this(name, isOptional, defaultValue, Integer.MIN_VALUE);
    }

    @Override
    public Integer parse(Parser parser) throws CommandSyntaxException {
        final int start = parser.getCursor();
        final int result = parser.readInt();
        if (result < minimum) {
            parser.setCursor(start);
            throw BuiltInExceptions.integerTooLow(parser, result, minimum);
        }
        if (result > maximum) {
            parser.setCursor(start);
            throw BuiltInExceptions.integerTooHigh(parser, result, maximum);
        }
        return result;
    }
}