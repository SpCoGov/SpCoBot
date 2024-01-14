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

import top.spco.api.*;
import top.spco.util.InteractiveList;

/**
 * @author SpCo
 * @version 2.0.0
 * @since 0.1.0
 */
class MiraiBot extends Bot<net.mamoe.mirai.Bot> {
    protected MiraiBot(net.mamoe.mirai.Bot bot) {
        super(bot);
    }

    @Override
    public boolean isOnline() {
        return this.wrapped().isOnline();
    }

    @Override
    public FriendGroups<net.mamoe.mirai.contact.friendgroup.FriendGroups> getFriendGroups() {
        return new MiraiFriendGroups(this.wrapped().getFriendGroups());
    }

    @Override
    public InteractiveList<Friend<?>> getFriends() {
        InteractiveList<Friend<?>> n = new InteractiveList<>();
        for (var friend : this.wrapped().getFriends().delegate) {
            n.add(new MiraiFriend(friend));
        }
        return n;
    }

    @Override
    public InteractiveList<Group<?>> getGroups() {
        InteractiveList<Group<?>> n = new InteractiveList<>();
        for (var group : this.wrapped().getGroups().delegate) {
            n.add(new MiraiGroup(group));
        }
        return n;
    }

    @Override
    public Friend<net.mamoe.mirai.contact.Friend> getFriend(long id) {
        return new MiraiFriend(this.wrapped().getFriend(id));
    }

    @Override
    public boolean hasFriend(long id) {
        return this.wrapped().getFriends().contains(id);
    }

    @Override
    public boolean hasGroup(long id) {
        return this.wrapped().getGroups().contains(id);
    }

    @Override
    public User<net.mamoe.mirai.contact.User> getUser(long id) {
        return new MiraiUser(this.wrapped().getStranger(id));
    }

    @Override
    public Group<net.mamoe.mirai.contact.Group> getGroup(long id) {
        return new MiraiGroup(this.wrapped().getGroup(id));
    }

    @Override
    public long getId() {
        return this.wrapped().getId();
    }
}