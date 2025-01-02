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
package top.spco.api;

/**
 * 群成员的权限<p>
 * 可通过 {@link #compareTo} 判断是否有更高的权限
 *
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public enum MemberPermission implements Comparable<MemberPermission> {
    /**
     * 一般群成员
     */
    MEMBER, // ordinal = 0

    /**
     * 管理员
     */
    ADMINISTRATOR, // ordinal = 1

    /**
     * 群主
     */
    OWNER; // ordinal = 2

    /**
     * 权限等级. {@link #OWNER} 为 2, {@link #ADMINISTRATOR} 为 1, {@link #MEMBER} 为 0
     */
    public int getLevel() {
        return ordinal();
    }

    /**
     * 判断权限是否为群主
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
     * 判断权限是否为管理员或群主
     */
    public boolean isOperator() {
        return isAdministrator() || isOwner();
    }

    public static MemberPermission byLevel(int level) {
        switch (level) {
            case 0 -> {
                return MEMBER;
            }
            case 1 -> {
                return ADMINISTRATOR;
            }
            case 2 -> {
                return OWNER;
            }
            default -> {
                return null;
            }
        }
    }
}