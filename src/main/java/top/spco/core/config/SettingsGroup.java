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

/**
 * {@code SettingsGroup} 接口用于标识配置项的分组。
 * 每个实现了该接口的枚举类型代表一个配置项分组，
 * 其中包含多个枚举常量，每个常量对应一个配置项。
 * <p>
 * 示例用法:
 * <pre>
 * {@code
 * public enum BotSettings implements SettingsGroup {
 *     TIMEOUT("timeout", 5000),
 *     LOG_LEVEL("log_level", "INFO");
 *
 *     private final String key;
 *     private final Object defaultValue;
 *
 *     BotSettings(String key, Object defaultValue) {
 *         this.key = key;
 *         this.defaultValue = defaultValue;
 *     }
 *
 *     {@literal @}Override
 *     public String groupName() {
 *         return "Bot";
 *     }
 *
 *     {@literal @}Override
 *     public Object defaultValue() {
 *         return defaultValue;
 *     }
 * }
 * }
 * </pre>
 * </p>
 *
 * @author SpCo
 * @version 2.0
 * @since 2.0
 */
public interface SettingsGroup {
    /**
     * 获取配置项分组的名称。
     *
     * @return 配置项分组的名称
     */
    String groupName();

    /**
     * 获取配置项的默认值。
     *
     * @return 配置项的默认值
     */
    Object defaultValue();
}