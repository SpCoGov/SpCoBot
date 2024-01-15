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

import net.mamoe.mirai.contact.Contact;
import top.spco.api.Group;
import top.spco.api.MemberPermission;
import top.spco.api.NormalMember;
import top.spco.api.message.Message;

import java.io.File;

/**
 * @author SpCo
 * @version 2.0.0
 * @since 0.1.0
 */
class MiraiNormalMember extends NormalMember<net.mamoe.mirai.contact.NormalMember> {
    public MiraiNormalMember(net.mamoe.mirai.contact.NormalMember member) {
        super(member);
    }

    @Override
    public Group<net.mamoe.mirai.contact.Group> getGroup() {
        return new MiraiGroup(this.wrapped().getGroup());
    }

    @Override
    public MemberPermission getPermission() {
        return MemberPermission.byLevel(this.wrapped().getPermission().getLevel());
    }

    @Override
    public void mute(int time) {
        this.wrapped().mute(time);
    }

    @Override
    public String getNameCard() {
        return this.wrapped().getNameCard();
    }

    @Override
    public String getSpecialTitle() {
        return this.wrapped().getSpecialTitle();
    }

    @Override
    public void modifyPermission(boolean operation) {
        this.wrapped().modifyAdmin(operation);
    }

    @Override
    public int muteTimeRemaining() {
        return this.wrapped().getMuteTimeRemaining();
    }

    @Override
    public boolean isMuted() {
        return this.wrapped().isMuted();
    }

    @Override
    public void unmute() {
        this.wrapped().unmute();
    }

    @Override
    public void kick(String message, boolean block) {
        this.wrapped().kick(message, block);
    }

    @Override
    public boolean isFriend() {
        return wrapped().getBot().getFriends().contains(getId());
    }

    public void kick(String message) {
        this.wrapped().kick(message);
    }

    @Override
    public long getId() {
        return this.wrapped().getId();
    }

    @Override
    public String getRemark() {
        return this.wrapped().getRemark();
    }

    @Override
    public void sendMessage(String message) {
        this.wrapped().sendMessage(message);
    }

    @Override
    public void sendMessage(Message<?> message) {
        this.wrapped().sendMessage(((MiraiMessage) message).wrapped());
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
    public void sendImage(File image) {
        Contact.uploadImage(this.wrapped(), image);
    }
}