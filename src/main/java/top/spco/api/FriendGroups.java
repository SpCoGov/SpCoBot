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

import java.util.Collection;

/**
 * <p>
 * Created on 2023/10/27 0027 15:37
 * <p>
 *
 * @author SpCo
 * @version 1.0
 * @since 1.0
 */
public interface FriendGroups {
    /**
     * 获取 {@link FriendGroup#getId() id} 为 {@code 0} 的默认分组 ("我的好友")
     */
    FriendGroup getDefault();

    /**
     * 新建一个好友分组<p>
     * 允许名称重复, 当新建一个已存在名称的分组时, 服务器会返回一个拥有重复名字的新分组
     */
    FriendGroup create(String name);

    /**
     * 获取指定 ID 的好友分组, 不存在时返回 {@code null}
     */
    FriendGroup get(int id);

    Collection<FriendGroup> asCollection();
}