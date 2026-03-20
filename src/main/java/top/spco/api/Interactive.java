/*
 * Copyright 2025 SpCo
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
package top.spco.api;

import top.spco.api.message.Message;
import top.spco.api.message.MessageChain;
import top.spco.api.message.TextMessage;

import java.io.File;

/**
 * 表示具有消息发送和异常处理功能的对象
 *
 * @author SpCo
 * @version 3.2.2
 * @since 0.1.0
 */
public abstract class Interactive<T> extends Identifiable<T> {
    protected Interactive(T interactive) {
        super(interactive);
    }

    public abstract void sendMessage(String message);

    public abstract void sendMessage(Message message);

    public void handleException(MessageChain sourceMessage, String message) {
        this.sendMessage(new TextMessage("[错误发生] " + message).toMessageChain().quoteReply(sourceMessage));
    }

    public void handleException(MessageChain sourceMessage, String message, Throwable throwable) {
        this.sendMessage(new TextMessage("[错误发生] " + message + ": " + throwable.getMessage()).toMessageChain().quoteReply(sourceMessage));
    }

    public void handleException(MessageChain sourceMessage, Throwable throwable) {
        this.sendMessage(new TextMessage("[错误发生] SpCoBot运行时抛出了意料之外的异常: " + throwable.getMessage()).toMessageChain().quoteReply(sourceMessage));
    }

    public void handleException(String message, Throwable throwable) {
        this.handleException("[错误发生] " + message + ": " + throwable.getMessage());
    }

    public void handleException(Throwable throwable) {
        this.handleException("[错误发生] SpCoBot运行时抛出了意料之外的异常: " + throwable.getMessage());
    }

    public void handleException(String message) {
        this.sendMessage(message);
    }

    /**
     * 回复并引用源消息。
     *
     * @param sourceMessage 源消息，用于引用
     * @param message       要发送的回复消息
     */
    public void quoteReply(MessageChain sourceMessage, MessageChain message) {
        // TODO: 需要修改引用逻辑
        //this.sendMessage(message.quoteReply(sourceMessage));
        this.sendMessage(sourceMessage.quoteReply(message));
    }

    /**
     * 回复并引用源消息。
     *
     * @param sourceMessage 源消息，用于引用
     * @param message       要发送的回复消息
     */
    public void quoteReply(MessageChain sourceMessage, String message) {
        this.sendMessage(new TextMessage(message).toMessageChain().quoteReply(sourceMessage));
    }

    public abstract void sendImage(File image);
}