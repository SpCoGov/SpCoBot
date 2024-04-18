/*
 * Copyright 2024 SpCo
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

import net.mamoe.mirai.contact.Contact;
import top.spco.api.Friend;
import top.spco.api.FriendGroup;
import top.spco.api.message.Message;

import java.io.File;

/**
 * @author SpCo
 * @version 2.0.0
 * @since 0.1.0
 */
class MiraiFriend extends Friend<net.mamoe.mirai.contact.Friend> {
    MiraiFriend(net.mamoe.mirai.contact.Friend friend) {
        super(friend);
    }

    @Override
    public long getId() {
        return this.wrapped().getId();
    }

    @Override
    public void sendMessage(String message) {
        this.wrapped().sendMessage(message);
    }

    @Override
    public void sendMessage(Message<?> message) {
        this.wrapped().sendMessage((net.mamoe.mirai.message.data.Message) message.wrapped());
    }

    @Override
    public String getRemark() {
        return this.wrapped().getRemark();
    }

    @Override
    public void nudge() {
        this.wrapped().nudge();
    }

    @Override
    public String getNick() {
        return this.wrapped().getNick();
    }

    @Override
    public FriendGroup<net.mamoe.mirai.contact.friendgroup.FriendGroup> getFriendGroup() {
        return new MiraiFriendGroup(this.wrapped().getFriendGroup());
    }

    @Override
    public void delete() {
        this.wrapped().delete();
    }

    @Override
    public void sendImage(File image) {
        Contact.uploadImage(this.wrapped(), image);
    }
}