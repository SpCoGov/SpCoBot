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
 * 浮点型数据的命令参数
 *
 * @author SpCo
 * @version 3.0.0
 * @since 3.0.0
 */
public class FloatParameter extends NumberParameter<Float> {
    public FloatParameter(String name, boolean isOptional, Float defaultValue, Float minimum, Float maximum) {
        super(name, isOptional, defaultValue, minimum, maximum);
    }

    public FloatParameter(String name, boolean isOptional, Float defaultValue, Float minimum) {
        this(name, isOptional, defaultValue, minimum, Float.MAX_VALUE);
    }

    public FloatParameter(String name, boolean isOptional, Float defaultValue) {
        this(name, isOptional, defaultValue, Float.MIN_VALUE);
    }


    @Override
    public Float parse(Parser parser) throws CommandSyntaxException {
        final int start = parser.getCursor();
        final float result = parser.readFloat();
        if (result < minimum) {
            parser.setCursor(start);
            throw BuiltInExceptions.floatTooLow(parser, result, minimum);
        }
        if (result > maximum) {
            parser.setCursor(start);
            throw BuiltInExceptions.floatTooHigh(parser, result, maximum);
        }
        return result;
    }
}