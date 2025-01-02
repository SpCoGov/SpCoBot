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

import top.spco.core.Wrapper;

import java.util.Collection;

/**
 * 用户的所有好友分组
 *
 * @author SpCo
 * @version 2.0.0
 * @since 0.1.0
 */
public abstract class FriendGroups<T> extends Wrapper<T> {
    protected FriendGroups(T object) {
        super(object);
    }

    /**
     * 获取 {@link FriendGroup#getId() id} 为 {@code 0} 的默认分组 ("我的好友")
     */
    public abstract FriendGroup<?> getDefault();

    /**
     * 新建一个好友分组<p>
     * 允许名称重复, 当新建一个已存在名称的分组时, 服务器会返回一个拥有重复名字的新分组
     */
    public abstract FriendGroup<?> create(String name);

    /**
     * 获取指定 ID 的好友分组, 不存在时返回 {@code null}
     */
    public abstract FriendGroup<?> get(int id);

    public abstract Collection<FriendGroup<?>> asCollection();
}