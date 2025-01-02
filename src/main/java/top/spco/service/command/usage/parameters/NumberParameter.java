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

/**
 * 数字类型数据的命令参数
 *
 * @author SpCo
 * @version 3.0.0
 * @since 3.0.0
 */
public abstract class NumberParameter<T extends Number> extends Parameter<T> {
    protected final T minimum;
    protected final T maximum;

    protected NumberParameter(String name, boolean isOptional, T defaultValue, T minimum, T maximum) {
        super(name, isOptional, defaultValue);
        this.minimum = minimum;
        this.maximum = maximum;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NumberParameter<?> that)) return false;
        return super.equals(o) && maximum == that.maximum && minimum == that.minimum;
    }

    @Override
    public int hashCode() {
        return 31 * (super.hashCode() + maximum.intValue() + minimum.intValue());
    }
}