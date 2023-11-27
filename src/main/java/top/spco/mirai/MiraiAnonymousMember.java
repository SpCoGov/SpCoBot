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

import top.spco.api.AnonymousMember;
import top.spco.api.Group;
import top.spco.api.MemberPermission;

/**
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
record MiraiAnonymousMember(net.mamoe.mirai.contact.AnonymousMember member) implements AnonymousMember {
    @Override
    public Group getGroup() {
        return new MiraiGroup(this.member.getGroup());
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
    public MemberPermission getPermission() {
        return MemberPermission.byLevel(this.member.getPermission().getLevel());
    }

    @Override
    public void mute(int time) {
        this.member.mute(time);
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
    public String getNick() {
        return this.member.getNick();
    }
}