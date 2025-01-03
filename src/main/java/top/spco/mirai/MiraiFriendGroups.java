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
package top.spco.mirai;

import top.spco.api.FriendGroup;
import top.spco.api.FriendGroups;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author SpCo
 * @version 2.0.0
 * @since 0.1.0
 */
class MiraiFriendGroups extends FriendGroups<net.mamoe.mirai.contact.friendgroup.FriendGroups> {
    protected MiraiFriendGroups(net.mamoe.mirai.contact.friendgroup.FriendGroups groups) {
        super(groups);
    }

    @Override
    public FriendGroup<net.mamoe.mirai.contact.friendgroup.FriendGroup> getDefault() {
        return new MiraiFriendGroup(this.wrapped().getDefault());
    }

    @Override
    public FriendGroup<net.mamoe.mirai.contact.friendgroup.FriendGroup> create(String name) {
        return new MiraiFriendGroup(this.wrapped().create(name));
    }

    @Override
    public FriendGroup<net.mamoe.mirai.contact.friendgroup.FriendGroup> get(int id) {
        return new MiraiFriendGroup(this.wrapped().get(id));
    }

    @Override
    public Collection<FriendGroup<?>> asCollection() {
        var friendGroups = this.wrapped().asCollection();
        Collection<FriendGroup<?>> c = new ArrayList<>();
        for (var friendGroup : friendGroups) {
            c.add(new MiraiFriendGroup(friendGroup));
        }
        return c;
    }
}