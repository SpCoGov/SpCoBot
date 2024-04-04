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

import top.spco.service.command.CommandDispatcher;
import top.spco.service.command.Parser;
import top.spco.service.command.exceptions.CommandSyntaxException;

import java.util.Objects;

/**
 * 表示命令参数
 *
 * @param <T> 改参数对应的数据类型
 * @author SpCo
 * @version 3.0.0
 * @since 3.0.0
 */
public abstract class Parameter<T> {
    protected final String name;
    protected final boolean isOptional;
    protected final T defaultValue;

    protected Parameter(String name, boolean isOptional, T defaultValue) {
        this.name = name;
        this.isOptional = isOptional;
        this.defaultValue = defaultValue;
    }

    public abstract T parse(Parser parser) throws CommandSyntaxException;

    public String toString() {
        if (isOptional) {
            return CommandDispatcher.USAGE_OPTIONAL_OPEN + name + CommandDispatcher.USAGE_OPTIONAL_CLOSE;
        } else {
            return CommandDispatcher.USAGE_REQUIRED_OPEN + name + CommandDispatcher.USAGE_REQUIRED_CLOSE;
        }
    }

    public final String getName() {
        return name;
    }

    public final T getDefaultValue() {
        return defaultValue;
    }

    public boolean isOptional() {
        return isOptional;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parameter<?> param = (Parameter<?>) o;
        return param.toString().equals(this.toString()) && param.isOptional == isOptional && param.defaultValue == defaultValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this + defaultValue.toString());
    }
}