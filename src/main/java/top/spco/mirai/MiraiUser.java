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
import top.spco.api.User;
import top.spco.api.message.Message;

import java.io.File;

/**
 * @author SpCo
 * @version 1.3.0
 * @since 0.1.0
 */
record MiraiUser(net.mamoe.mirai.contact.User user) implements User {
    @Override
    public long getId() {
        return this.user.getId();
    }

    @Override
    public String getRemark() {
        return this.user.getRemark();
    }

    @Override
    public void sendMessage(String message) {
        user.sendMessage(message);
    }

    @Override
    public void sendMessage(Message message) {
        this.user.sendMessage(((MiraiMessage) message).message());
    }

    @Override
    public void nudge() {
        this.user.nudge();
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
    public void quoteReply(Message sourceMessage, Message message) {
        this.sendMessage(new MiraiMessageChainBuilder(sourceMessage).append(message).build());
    }

    @Override
    public void quoteReply(Message sourceMessage, String message) {
        this.sendMessage(new MiraiMessageChainBuilder(sourceMessage).append(message).build());
    }

    @Override
    public void sendImage(File image) {
        Contact.uploadImage(this.user, image);
    }

    @Override
    public String getNick() {
        return this.user.getNick();
    }
}