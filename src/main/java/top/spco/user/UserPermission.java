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
package top.spco.user;

/**
 * 机器人用户的权限
 *
 * @author SpCo
 * @version 0.1.2
 * @since 0.1.0
 */
public enum UserPermission implements Comparable<UserPermission> {
    /**
     * 被封禁用户<p>ordinal = 0
     */
    BANNED,

    /**
     * 普通用户<p>ordinal = 1
     */
    NORMAL,

    /**
     * 管理员<p>ordinal = 2
     */
    ADMINISTRATOR,

    /**
     * 机器人主人<p>ordinal = 3
     */
    OWNER;

    /**
     * 权限等级. {@link #OWNER} 为 3, {@link #ADMINISTRATOR} 为 2, {@link #NORMAL} 为 1, {@link #BANNED} 为 0
     */
    public int getLevel() {
        return ordinal();
    }

    /**
     * 判断权限是否为机器人主人
     */
    public boolean isOwner() {
        return this == OWNER;
    }

    /**
     * 判断权限是否为管理员
     */
    public boolean isAdministrator() {
        return this == ADMINISTRATOR;
    }

    /**
     * 判断权限是否为管理员或机器人主人
     */
    public boolean isOperator() {
        return isAdministrator() || isOwner();
    }

    /**
     * 判断权限是否为已被封禁
     */
    public boolean isBanned() {
        return this == BANNED;
    }

    public static UserPermission byLevel(int level) {
        switch (level) {
            case 0 -> {
                return BANNED;
            }
            case 1 -> {
                return NORMAL;
            }
            case 2 -> {
                return ADMINISTRATOR;
            }
            case 3 -> {
                return OWNER;
            }
            default -> {
                return null;
            }
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case OWNER -> {
                return "机器人主人";
            }
            case BANNED -> {
                return "已封禁用户";
            }
            case NORMAL -> {
                return "普通用户";
            }
            case ADMINISTRATOR -> {
                return "机器人管理员";
            }
        }
        return "其它用户";
    }
}