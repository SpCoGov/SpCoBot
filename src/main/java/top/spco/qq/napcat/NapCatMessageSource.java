/*
 * Copyright 2026 SpCo
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
package top.spco.qq.napcat;

import top.spco.api.message.MessageSource;

public class NapCatMessageSource extends MessageSource {
    public final String senderId;
    public final String fromId;
    public final String messageId;

    public NapCatMessageSource(String senderId, String fromId, String messageId) {
        this.senderId = senderId;
        this.fromId = fromId;
        this.messageId = messageId;
    }

    @Override
    public String getSenderId() {
        return senderId;
    }

    @Override
    public String getFromId() {
        return fromId;
    }

    @Override
    public String getMessageId() {
        return messageId;
    }
}