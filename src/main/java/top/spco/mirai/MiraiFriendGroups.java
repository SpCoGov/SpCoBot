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

import top.spco.api.FriendGroup;
import top.spco.api.FriendGroups;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author SpCo
 * @version 1.0
 * @since 1.0
 */
record MiraiFriendGroups(net.mamoe.mirai.contact.friendgroup.FriendGroups friendGroups) implements FriendGroups {
    @Override
    public FriendGroup getDefault() {
        return new MiraiFriendGroup(this.friendGroups.getDefault());
    }

    @Override
    public FriendGroup create(String name) {
        return new MiraiFriendGroup(this.friendGroups.create(name));
    }

    @Override
    public FriendGroup get(int id) {
        return new MiraiFriendGroup(this.friendGroups.get(id));
    }

    @Override
    public Collection<FriendGroup> asCollection() {
        var friendGroups = this.friendGroups.asCollection();
        Collection<FriendGroup> c = new ArrayList<>();
        for (var friendGroup : friendGroups) {
            c.add(new MiraiFriendGroup(friendGroup));
        }
        return c;
    }
}