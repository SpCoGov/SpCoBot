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
package top.spco.core.config;

import top.spco.service.command.commands.DashScopeCommand;

/**
 * 本配置组包含了 {@link DashScopeCommand} 的相关配置，包含以下配置项：
 * <table border="1">
 *   <tr>
 *     <th>配置项名</th>
 *     <th>描述</th>
 *   </tr>
 *   <tr>
 *     <td>api_key</td>
 *     <td>调用DashScope模型所需的API KEY。</td>
 *   </tr>
 * </table>
 *
 * @author SpCo
 * @version 0.2.1
 * @see DashScopeCommand
 * @since 0.2.1
 */
public enum DashScopeSettings implements SettingsGroup {
    API_KET("api_key", "");

    private final String key;
    private final Object defaultValue;

    DashScopeSettings(String key, Object defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return key;
    }

    @Override
    public Object defaultValue() {
        return defaultValue;
    }

    @Override
    public String groupName() {
        return "DashScope";
    }
}