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

import java.io.File;

/**
 * 代表匿名群成员
 *
 * @author SpCo
 * @version 2.0.0
 * @see Member
 * @see NormalMember
 * @since 0.1.0
 */
public abstract class AnonymousMember<T> extends Member<T> {
    public AnonymousMember(T objcet) {
        super(objcet);
    }

    /**
     * @deprecated 无法发送信息至 AnonymousMember
     */
    @Override
    @Deprecated
    public void sendMessage(String message) {
    }

    /**
     * @deprecated 无法发送信息至 AnonymousMember
     */
    @Override
    @Deprecated
    public void sendMessage(Message<?> message) {
    }

    /**
     * @deprecated 无法拍一拍 AnonymousMember
     */
    @Override
    @Deprecated
    public void nudge() {
    }

    @Deprecated
    @Override
    public void handleException(Message<?> sourceMessage, String message, Throwable throwable) {
    }

    @Deprecated
    @Override
    public void handleException(Message<?> sourceMessage, Throwable throwable) {
    }

    @Deprecated
    @Override
    public void handleException(String message, Throwable throwable) {
    }

    @Deprecated
    @Override
    public void handleException(Throwable throwable) {
    }

    @Deprecated
    @Override
    public void handleException(String message) {
    }

    @Deprecated
    @Override
    public void handleException(Message<?> sourceMessage, String message) {
    }

    @Deprecated
    @Override
    public void quoteReply(Message<?> sourceMessage, Message<?> message) {
    }

    @Deprecated
    @Override
    public void quoteReply(Message<?> sourceMessage, String message) {
    }

    @Deprecated
    @Override
    public void sendImage(File image) {
    }
}