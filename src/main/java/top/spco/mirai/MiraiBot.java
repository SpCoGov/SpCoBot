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
package top.spco.mirai;

import net.mamoe.mirai.contact.Stranger;
import top.spco.base.api.*;
import top.spco.base.InteractiveList;
import top.spco.user.UserFetchException;

/**
 * <p>
 * Created on 2023/10/26 0026 18:00
 * <p>
 *
 * @author SpCo
 * @version 1.0
 * @since 1.0
 */
record MiraiBot(net.mamoe.mirai.Bot bot) implements Bot {

    @Override
    public boolean isOnline() {
        return this.bot.isOnline();
    }

    @Override
    public FriendGroups getFriendGroups() {
        return new MiraiFriendGroups(this.bot.getFriendGroups());
    }

    @Override
    public InteractiveList<Friend> getFriends() {
        InteractiveList<Friend> n = new InteractiveList<>();
        for (var friend : this.bot.getFriends().delegate) {
            n.add(new MiraiFriend(friend));
        }
        return n;
    }

    @Override
    public InteractiveList<Group> getGroups() {
        InteractiveList<Group> n = new InteractiveList<>();
        for (var group : this.bot.getGroups().delegate) {
            n.add(new MiraiGroup(group));
        }
        return n;
    }

    @Override
    public Friend getFriend(long id) {
        return new MiraiFriend(this.bot.getFriend(id));
    }

    @Override
    public boolean hasFriend(long id) {
        return this.bot.getFriends().contains(id);
    }

    @Override
    public boolean hasGroup(long id) {
        return this.bot.getGroups().contains(id);
    }

    @Override
    public User getUser(long id) {
        return new MiraiUser(this.bot.getStranger(id));
    }

    @Override
    public long getId() {
        return this.bot.getId();
    }
}