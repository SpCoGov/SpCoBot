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
package top.spco.core.config;

import top.spco.SpCoBot;

/**
 * 本配置组表示当前配置的版本，包含以下配置项：
 * <table border="1">
 *   <tr>
 *     <th>配置项名</th>
 *     <th>描述</th>
 *   </tr>
 *   <tr>
 *     <td>config_version</td>
 *     <td>配置文件最后一次修改时对应的机器人版本。</td>
 *   </tr>
 * </table>
 *
 * @author SpCo
 * @version 0.2.1
 * @since 0.2.1
 */
public enum SettingsVersion implements SettingsGroup {
    CONFIG_VERSION("config_version", SpCoBot.MAIN_VERSION);

    private final String key;
    private final Object defaultValue;

    SettingsVersion(String key, Object defaultValue) {
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
        return "Version";
    }
}