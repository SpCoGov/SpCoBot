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

import top.spco.api.Group;
import top.spco.api.InteractiveList;
import top.spco.api.MemberPermission;
import top.spco.api.NormalMember;
import top.spco.api.message.Message;

import java.io.File;

/**
 * @author SpCo
 * @version 3.1.0
 * @since 0.1.0
 */
class MiraiGroup extends Group<net.mamoe.mirai.contact.Group> {
    protected MiraiGroup(net.mamoe.mirai.contact.Group group) {
        super(group);
    }

    @Override
    public String getName() {
        return this.wrapped().getName();
    }

    @Override
    public long getId() {
        return this.wrapped().getId();
    }

    @Override
    public NormalMember<net.mamoe.mirai.contact.NormalMember> getOwner() {
        return new MiraiNormalMember(this.wrapped().getOwner());
    }

    @Override
    public void sendMessage(Message<?> message) {
        this.wrapped().sendMessage(((MiraiMessage) message).wrapped());
    }

    @Override
    public void sendMessage(String message) {
        this.wrapped().sendMessage(message);
    }

    @Override
    public boolean quit() {
        return this.wrapped().quit();
    }

    @Override
    public MemberPermission botPermission() {
        return MemberPermission.byLevel(this.wrapped().getBotPermission().getLevel());
    }

    @Override
    public NormalMember<net.mamoe.mirai.contact.NormalMember> botAsMember() {
        return new MiraiNormalMember(this.wrapped().getBotAsMember());
    }

    @Override
    public NormalMember<net.mamoe.mirai.contact.NormalMember> getMember(long id) {
        return new MiraiNormalMember(wrapped().get(id));
    }

    @Override
    public InteractiveList<NormalMember<?>> getMembers() {
        InteractiveList<NormalMember<?>> n = new InteractiveList<>();
        for (var group : this.wrapped().getMembers().delegate) {
            n.add(new MiraiNormalMember(group));
        }
        // mirai返回的群成员列表不包含机器人自己
        // 再添加机器人自己
        n.add(botAsMember());
        return n;
    }

    @Override
    public void sendImage(File image) {
        net.mamoe.mirai.contact.Contact.uploadImage(this.wrapped(), image);
    }
}