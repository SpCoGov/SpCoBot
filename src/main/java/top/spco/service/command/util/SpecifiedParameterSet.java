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

import top.spco.service.command.usage.parameters.SpecifiedParameter;

import java.util.Map;

/**
 * 由 {@link SpecifiedParameterHelper} 创建的参数集合。
 *
 * @author SpCo
 * @version 3.0.3
 * @since 3.0.3
 */
public class SpecifiedParameterSet {
    private final Map<String, SpecifiedParameter> params;

    SpecifiedParameterSet(Map<String, SpecifiedParameter> params) {
        this.params = params;
    }

    public SpecifiedParameter get(String text) {
        SpecifiedParameter parameter = params.get(text);
        if (parameter == null) {
            throw new IllegalArgumentException("无法找到对应参数");
        }
        return parameter;
    }
}