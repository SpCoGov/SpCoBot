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

import top.spco.api.Friend;
import top.spco.api.FriendGroup;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
record MiraiFriendGroup(net.mamoe.mirai.contact.friendgroup.FriendGroup friendGroup) implements FriendGroup {
    @Override
    public int getId() {
        return this.friendGroup.getId();
    }

    @Override
    public String getName() {
        return this.friendGroup.getName();
    }

    @Override
    public int getCount() {
        return this.friendGroup.getCount();
    }

    @Override
    public Collection<Friend> getFriends() {
        Collection<Friend> friends = new ArrayList<>();
        for (var friend : this.friendGroup.getFriends()) {
            MiraiFriend miraiFriend = new MiraiFriend(friend);
            friends.add(miraiFriend);
        }
        return friends;
    }

    @Override
    public boolean renameTo(String newName) {
        return this.friendGroup.renameTo(newName);
    }

    @Override
    public boolean moveIn(Friend friend) {
        return this.friendGroup.moveIn(((MiraiFriend) friend).friend());
    }

    @Override
    public boolean delete() {
        return this.friendGroup.delete();
    }
}