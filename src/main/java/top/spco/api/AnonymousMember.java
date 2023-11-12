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

/**
 * 代表匿名群成员<p>
 *
 * @author SpCo
 * @version 1.0
 * @see Member
 * @see NormalMember
 * @since 1.0
 */
public interface AnonymousMember extends Member {
    /**
     * @deprecated 无法发送信息至 AnonymousMember
     */
    @Override
    @Deprecated
    default void sendMessage(String message) {
    }

    /**
     * @deprecated 无法发送信息至 AnonymousMember
     */
    @Override
    @Deprecated
    default void sendMessage(Message message) {
    }

    /**
     * @deprecated 无法拍一拍 AnonymousMember
     */
    @Override
    @Deprecated
    default void nudge() {
    }

    @Deprecated
    @Override
    default void handleException(Message sourceMessage, String message, Throwable throwable) {
    }

    @Deprecated
    @Override
    default void handleException(Message sourceMessage, Throwable throwable) {
    }

    @Deprecated
    @Override
    default void handleException(String message, Throwable throwable) {
    }

    @Deprecated
    @Override
    default void handleException(Throwable throwable) {
    }

    @Deprecated
    @Override
    default void handleException(String message) {
    }

    @Deprecated
    @Override
    default void quoteReply(Message sourceMessage, Message message) {
    }

    @Deprecated
    @Override
    default void quoteReply(Message sourceMessage, String message) {
    }
}