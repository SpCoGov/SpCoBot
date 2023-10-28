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

import top.spco.base.api.Group;
import top.spco.base.api.MemberPermission;
import top.spco.base.api.NormalMember;
import top.spco.base.api.message.Message;
import top.spco.mirai.message.MiraiMessage;

/**
 * <p>
 * Created on 2023/10/26 0026 15:35
 * <p>
 *
 * @author SpCo
 * @version 1.0
 * @since 1.0
 */
public record MiraiGroup(net.mamoe.mirai.contact.Group group) implements Group {

    @Override
    public String getName() {
        return this.group.getName();
    }

    @Override
    public long getId() {
        return this.group.getId();
    }

    @Override
    public NormalMember getOwner() {
        return new MiraiNormalMember(this.group.getOwner());
    }

    @Override
    public void sendMessage(Message message) {
        this.group.sendMessage(((MiraiMessage) message).message());
    }

    @Override
    public void sendMessage(String message) {
        this.group.sendMessage(message);
    }

    @Override
    public boolean quit() {
        return this.group.quit();
    }

    @Override
    public MemberPermission botPermission() {
        return MemberPermission.byLevel(this.group.getBotPermission().getLevel());
    }

    @Override
    public NormalMember botAsMember() {
        return new MiraiNormalMember(this.group.getBotAsMember());
    }
}