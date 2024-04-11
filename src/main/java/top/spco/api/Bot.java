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
 * 机器人
 *
 * @author SpCo
 * @version 2.0.0
 * @since 0.1.0
 */
public abstract class Bot<T> extends Identifiable<T> {
    protected Bot(T bot) {
        super(bot);
    }

    /**
     * 当 BotSettings 在线 (可正常收发消息) 时返回 {@code true}.
     */
    public abstract boolean isOnline();

    /**
     * 获取机器人的昵称
     *
     * @return 机器人的昵称
     */
    public abstract String getNick();

    /**
     * 获取机器人作为好友的实例
     *
     * @return 机器人作为好友的实例
     */
    public abstract Friend<?> asFriend();

    /**
     * 全部的好友分组
     */
    public abstract FriendGroups<?> getFriendGroups();

    /**
     * 好友列表
     */
    public abstract InteractiveList<Friend<?>> getFriends();

    /**
     * 群列表
     */
    public abstract InteractiveList<Group<?>> getGroups();

    /**
     * 获取一个好友对象, 在获取失败时返回 {@code null}
     *
     * @param id 对方 QQ 号码
     */
    public abstract Friend<?> getFriend(long id);

    /**
     * 当 {@link Bot} 拥有 {@link Friend#getId()} 为 id 的好友时返回 {@code true}
     *
     * @param id 好友的id
     */
    public abstract boolean hasFriend(long id);

    /**
     * 当 {@link Bot} 拥有 {@link Group#getId()} 为 id 的群组时返回 {@code true}
     *
     * @param id 群组的id
     */
    public abstract boolean hasGroup(long id);

    public abstract User<?> getUser(long id);

    public abstract Group<?> getGroup(long id);
}