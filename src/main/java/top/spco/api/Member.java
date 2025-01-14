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
 * 代表一名群成员<p>
 * 群成员分为{@link NormalMember 普通成员}和{@link AnonymousMember 匿名成员}
 *
 * @author SpCo
 * @version 2.0.0
 * @see NormalMember
 * @see AnonymousMember
 * @since 0.1.0
 */
public abstract class Member<T> extends User<T> {
    public Member(T object) {
        super(object);
    }

    /**
     * 所在的群
     *
     * @return 所在的群
     */
    public abstract Group<?> getGroup();

    /**
     * 群名片
     *
     * @return 群名片. 可能为空
     */
    public abstract String getNameCard();

    /**
     * 群特殊头衔<p>
     * 为 {@link AnonymousMember 匿名成员} 时一定是 {@code "匿名"}
     *
     * @return 群特殊头衔
     */
    public abstract String getSpecialTitle();

    /**
     * 成员的权限<p>
     * {@link Member} 可能是 {@link NormalMember 普通成员} 或 {@link AnonymousMember 匿名成员}。
     *
     * @return 成员的权限
     */
    public abstract MemberPermission getPermission();

    /**
     * 禁言这个群成员。
     *
     * @param time 持续时间. 精确到秒. 最短 0 秒, 最长 30 天
     */
    public abstract void mute(int time);
}