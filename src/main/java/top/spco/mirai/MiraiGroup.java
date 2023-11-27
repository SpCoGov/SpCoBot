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

import top.spco.api.Group;
import top.spco.api.MemberPermission;
import top.spco.api.NormalMember;
import top.spco.api.message.Message;

/**
 * @author SpCo
 * @version 0.3.0
 * @since 0.1.0
 */
record MiraiGroup(net.mamoe.mirai.contact.Group group) implements Group {
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
    public void handleException(Message sourceMessage, String message, Throwable throwable) {
        this.sendMessage(new MiraiMessageChainBuilder(sourceMessage).append("[错误发生] " + message + ": " + throwable.getMessage()).build());
    }

    @Override
    public void handleException(Message sourceMessage, Throwable throwable) {
        this.sendMessage(new MiraiMessageChainBuilder(sourceMessage).append("[错误发生] SpCoBot运行时抛出了意料之外的异常: " + throwable.getMessage()).build());
    }

    @Override
    public void handleException(Message sourceMessage, String message) {
        this.sendMessage(new MiraiMessageChainBuilder(sourceMessage).append("[错误发生] " + message).build());
    }

    @Override
    public void handleException(String message, Throwable throwable) {
        this.handleException("[错误发生] " + message + ": " + throwable.getMessage());
    }

    @Override
    public void handleException(Throwable throwable) {
        this.handleException("[错误发生] SpCoBot运行时抛出了意料之外的异常: " + throwable.getMessage());
    }

    @Override
    public void handleException(String message) {
        this.sendMessage(message);
    }

    @Override
    public NormalMember botAsMember() {
        return new MiraiNormalMember(this.group.getBotAsMember());
    }

    @Override
    public NormalMember getMember(long id) {
        return new MiraiNormalMember(group.get(id));
    }

    @Override
    public void quoteReply(Message sourceMessage, Message message) {
        this.sendMessage(new MiraiMessageChainBuilder(sourceMessage).append(message).build());
    }

    @Override
    public void quoteReply(Message sourceMessage, String message) {
        this.sendMessage(new MiraiMessageChainBuilder(sourceMessage).append(message).build());
    }
}