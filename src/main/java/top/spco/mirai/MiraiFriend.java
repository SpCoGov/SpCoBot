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

import top.spco.api.Friend;
import top.spco.api.FriendGroup;
import top.spco.api.message.Message;

/**
 * @author SpCo
 * @version 0.3.0
 * @since 0.1.0
 */
record MiraiFriend(net.mamoe.mirai.contact.Friend friend) implements Friend {
    @Override
    public long getId() {
        return this.friend.getId();
    }

    @Override
    public void sendMessage(String message) {
        this.friend.sendMessage(message);
    }

    @Override
    public void sendMessage(Message message) {
        this.friend.sendMessage(((MiraiMessage) message).message());
    }

    @Override
    public void handleException(Message sourceMessage, String message) {
        this.sendMessage(new MiraiMessageChainBuilder(sourceMessage).append("[错误发生] " + message).build());
    }

    @Override
    public String getRemark() {
        return this.friend.getRemark();
    }

    @Override
    public void nudge() {
        this.friend.nudge();
    }

    @Override
    public String getNick() {
        return this.friend.getNick();
    }

    @Override
    public FriendGroup getFriendGroup() {
        return new MiraiFriendGroup(this.friend.getFriendGroup());
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
    public void delete() {
        this.friend.delete();
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