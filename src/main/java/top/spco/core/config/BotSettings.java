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

import top.spco.user.BotUser;
import top.spco.user.UserPermission;

/**
 * 本配置组包含了机器人的基本设置，包含以下配置项：
 * <table border="1">
 *   <tr>
 *     <th>配置项名</th>
 *     <th>描述</th>
 *   </tr>
 *   <tr>
 *     <td>bot_id</td>
 *     <td>机器人所使用的账号ID。<p>
 *         当机器人创建以此配置项的值为ID的 {@link BotUser} 时，会自动将该用户的权限设置为 {@link UserPermission#OWNER 机器人主人}。</td>
 *   </tr>
 *   <tr>
 *     <td>owner_id</td>
 *     <td>机器人主人所使用的账号ID。<p>
 *         当机器人创建以此配置项的值为ID的 {@link BotUser} 时，会自动将该用户的权限设置为 {@link UserPermission#OWNER 机器人主人}。<p>
 *         和其他仅拥有 {@link UserPermission#OWNER 机器人主人} 权限的用户不同的是，一些通知信息或报错信息只会发给以此处设置的账号。</td>
 *   </tr>
 *   <tr>
 *     <td>test_group</td>
 *     <td>用于机器人功能的群ID。<p>
 *         一些通知信息或报错信息会发送至此处设置的群。</td>
 *   </tr>
 * </table>
 *
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