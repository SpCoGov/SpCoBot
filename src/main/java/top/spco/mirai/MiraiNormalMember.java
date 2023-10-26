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

import net.mamoe.mirai.message.code.MiraiCode;
import top.spco.base.api.Group;
import top.spco.base.api.MemberPermission;
import top.spco.base.api.NormalMember;
import top.spco.base.api.message.Message;

/**
 * <p>
 * Created on 2023/10/26 0026 15:37
 * <p>
 *
 * @author SpCo
 * @version 1.0
 * @since 1.0
 */
public class MiraiNormalMember implements NormalMember {
    private final net.mamoe.mirai.contact.NormalMember member;

    public MiraiNormalMember(net.mamoe.mirai.contact.NormalMember member) {
        this.member = member;
    }

    @Override
    public Group getGroup() {
        return new MiraiGroup(this.member.getGroup());
    }

    @Override
    public MemberPermission getPermission() {
        return MemberPermission.byLevel(this.member.getPermission().getLevel());
    }

    @Override
    public void mute(int time) {
        this.member.mute(time);
    }

    @Override
    public String getNameCard() {
        return this.member.getNameCard();
    }

    @Override
    public String getSpecialTitle() {
        return this.member.getSpecialTitle();
    }

    @Override
    public void modifyPermission(boolean operation) {
        this.member.modifyAdmin(operation);
    }

    @Override
    public int muteTimeRemaining() {
        return this.member.getMuteTimeRemaining();
    }

    @Override
    public boolean isMuted() {
        return this.member.isMuted();
    }

    @Override
    public void unmute() {
        this.member.unmute();
    }

    @Override
    public void kick(String message, boolean block) {
        this.member.kick(message, block);
    }

    public void kick(String message) {
        this.member.kick(message);
    }

    @Override
    public long getId() {
        return this.member.getId();
    }

    @Override
    public String getRemark() {
        return this.member.getRemark();
    }

    @Override
    public void sendMessage(String message) {
        this.member.sendMessage(message);
    }

    @Override
    public void sendMessage(Message message) {
        this.member.sendMessage(MiraiCode.deserializeMiraiCode(message.serialize()));
    }

    @Override
    public void nudge() {
        this.member.nudge();
    }
}