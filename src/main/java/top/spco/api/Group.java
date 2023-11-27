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
package top.spco.api;

/**
 * 代表一个群
 *
 * @author SpCo
 * @version 0.3.0
 * @since 0.1.0
 */
public interface Group extends Interactive {
    /**
     * 群名称
     */
    String getName();

    /**
     * 群主
     */
    NormalMember getOwner();

    /**
     * 让机器人退出这个群
     *
     * @return 退出成功时 {@code true}; 已经退出时 {@code false}
     */
    boolean quit();

    MemberPermission botPermission();

    NormalMember botAsMember();

    NormalMember getMember(long id);
}