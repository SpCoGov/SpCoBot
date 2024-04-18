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

import top.spco.api.AnonymousMember;
import top.spco.api.Group;
import top.spco.api.MemberPermission;

/**
 * @author SpCo
 * @version 2.0.0
 * @since 0.1.0
 */
class MiraiAnonymousMember extends AnonymousMember<net.mamoe.mirai.contact.AnonymousMember> {
    public MiraiAnonymousMember(net.mamoe.mirai.contact.AnonymousMember member) {
        super(member);
    }

    @Override
    public Group<net.mamoe.mirai.contact.Group> getGroup() {
        return new MiraiGroup(this.wrapped().getGroup());
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
    public MemberPermission getPermission() {
        return MemberPermission.byLevel(this.wrapped().getPermission().getLevel());
    }

    @Override
    public void mute(int time) {
        this.wrapped().mute(time);
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
    public String getNick() {
        return this.wrapped().getNick();
    }
}