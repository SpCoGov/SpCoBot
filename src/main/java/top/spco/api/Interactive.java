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
package top.spco.api;

import top.spco.api.message.Message;

import java.io.File;

/**
 * 表示具有消息发送和异常处理功能
 *
 * @author SpCo
 * @version 2.0.0
 * @since 0.1.0
 */
public abstract class Interactive<T> extends Wrapper<T> implements Identifiable {
    protected Interactive(T interactive) {
        super(interactive);
    }

    public abstract void sendMessage(String message);

    public abstract void sendMessage(Message<?> message);

    public abstract void handleException(Message<?> sourceMessage, String message);

    public abstract void handleException(Message<?> sourceMessage, String message, Throwable throwable);

    public abstract void handleException(Message<?> sourceMessage, Throwable throwable);

    public abstract void handleException(String message, Throwable throwable);

    public abstract void handleException(Throwable throwable);

    public abstract void handleException(String message);

    /**
     * 回复并引用源消息。
     *
     * @param sourceMessage 源消息，用于引用
     * @param message       要发送的回复消息
     */
    public abstract void quoteReply(Message<?> sourceMessage, Message<?> message);

    /**
     * 回复并引用源消息。
     *
     * @param sourceMessage 源消息，用于引用
     * @param message       要发送的回复消息
     */
    public abstract void quoteReply(Message<?> sourceMessage, String message);

    public abstract void sendImage(File image);
}