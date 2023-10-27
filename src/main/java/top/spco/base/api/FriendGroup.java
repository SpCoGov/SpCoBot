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
package top.spco.base.api;

import java.util.Collection;
import java.util.Collections;

/**
 * <p>
 * Created on 2023/10/27 0027 1:10
 * <p>
 *
 * @author SpCo
 * @version 1.0
 * @since 1.0
 */
public interface FriendGroup {
    /**
     * 好友分组 ID
     */
    int getId();

    /**
     * 好友分组名
     */
    String getName();

    /**
     * 好友分组内好友数量
     */
    int getCount();

    /**
     * 属于本分组的好友集合
     */
    Collection<Friend> getFriends();

    /**
     * 更改好友分组名称<p>
     * 允许存在同名分组<p>
     * 当操作成时返回 {@code true}; 当分组不存在时返回 {@code false}
     */
    boolean renameTo(String newName);

    /**
     * 把一名好友移动至本分组内<p>
     * 当远程分组不存在时会自动移动该好友到 ID 为 0 的默认好友分组<p>
     * 当操作成功时返回 {@code true}; 当分组不存在 (如已经在远程被删除) 时返回 {@code false}
     */
    boolean moveIn(Friend friend);

    /**
     * 删除本分组<p>
     * 删除后组内全部好友移动至 ID 为 0 的默认好友分组, 本分组的好友列表会被清空<p>
     * 当操作成功时返回 {@code true}; 当分组不存在或试图删除 ID 为 0 的默认好友分组时返回 {@code false}
     */
    boolean delete();
}