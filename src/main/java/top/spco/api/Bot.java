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

import top.spco.util.InteractiveList;
import top.spco.user.UserFetchException;

/**
 * <p>
 * Created on 2023/10/26 0026 17:56
 * <p>
 *
 * @author SpCo
 * @version 1.0
 * @since 1.0
 */
public interface Bot extends Identifiable {
    /**
     * 当 BotSettings 在线 (可正常收发消息) 时返回 {@code true}.
     */
    boolean isOnline();

    /**
     * 全部的好友分组
     */
    FriendGroups getFriendGroups();

    /**
     * 好友列表
     */
    InteractiveList<Friend> getFriends();

    /**
     * 群列表
     */
    InteractiveList<Group> getGroups();

    /**
     * 获取一个好友对象, 在获取失败时返回 {@code null}
     *
     * @param id 对方 QQ 号码
     */
    Friend getFriend(long id);

    /**
     * 当 {@link Bot} 拥有 {@link Friend#getId()} 为 id 的好友时返回 {@code true}
     *
     * @param id 好友的id
     */
    boolean hasFriend(long id);

    /**
     * 当 {@link Bot} 拥有 {@link Group#getId()} 为 id 的群组时返回 {@code true}
     *
     * @param id 群组的id
     */
    boolean hasGroup(long id);

    User getUser(long id) throws UserFetchException;

    Group getGroup(long id);
}