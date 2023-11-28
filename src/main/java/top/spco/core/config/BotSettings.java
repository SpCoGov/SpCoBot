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
 * @author SpCo
 * @version 0.3.2
 * @since 0.2.0
 */
public enum BotSettings implements SettingsGroup {
    BOT_ID("bot_id", 0L),
    OWNER_ID("owner_id", 0L),
    TEST_GROUP("test_group", 0L);
    private final String key;
    private final Object defaultValue;

    BotSettings(String key, Object defaultValue) {
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
        return "Bot";
    }
}