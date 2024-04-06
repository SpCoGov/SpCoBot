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
package top.spco.service.command.util;

import top.spco.core.Builder;
import top.spco.service.command.usage.parameters.SpecifiedParameter;

import java.util.*;

/**
 * 创建 {@link SpecifiedParameter} 的工具，
 *
 * @author SpCo
 * @version 3.0.3
 * @see SpecifiedParameter
 * @since 3.0.3
 */
public class SpecifiedParameterHelper implements Builder<SpecifiedParameterSet> {
    private final String name;
    private final boolean optional;
    private final Set<String> texts = new HashSet<>();

    public SpecifiedParameterHelper(String name, boolean optional) {
        this.name = name;
        this.optional = optional;
    }

    public SpecifiedParameterHelper add(String text) {
        texts.add(text);
        return this;
    }

    public SpecifiedParameterHelper add(String... texts) {
        this.texts.addAll(Arrays.asList(texts));
        return this;
    }

    @Override
    public SpecifiedParameterSet build() {
        Map<String, SpecifiedParameter> params = new HashMap<>();
        String[] range = texts.toArray(new String[0]);
        for (String text : texts) {
            params.put(text, new SpecifiedParameter(name, optional, text, range));
        }
        return new SpecifiedParameterSet(params);
    }
}