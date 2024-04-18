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
package top.spco.service.command.usage;

import top.spco.core.Builder;
import top.spco.service.command.usage.parameters.Parameter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 命令用法构建器
 *
 * @author SpCo
 * @version 3.0.0
 * @since 3.0.0
 */
public class UsageBuilder implements Builder<Usage> {
    private final String label;
    private final String name;
    private final List<Parameter<?>> params = new ArrayList<>();

    public UsageBuilder(String label,String name) {
        this.label = label;
        this.name = name;
    }

    public UsageBuilder add(Parameter<?> param) {
        params.add(param);
        return this;
    }

    public UsageBuilder addAll(Collection<? extends Parameter<?>> c) {
        params.addAll(c);
        return this;
    }

    @Override
    public Usage build() {
        return new Usage(label, name, params);
    }
}