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

import net.mamoe.mirai.message.data.QuoteReply;
import top.spco.api.message.Message;
import top.spco.api.message.MessageChainBuilder;

/**
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
class MiraiMessageChainBuilder implements MessageChainBuilder {
    public final net.mamoe.mirai.message.data.MessageChainBuilder builder;

    public MiraiMessageChainBuilder(Message toQuote) {
        this.builder = new net.mamoe.mirai.message.data.MessageChainBuilder();
        this.builder.append(new QuoteReply(((MiraiMessage) toQuote).message()));
    }

    public MiraiMessageChainBuilder() {
        this.builder = new net.mamoe.mirai.message.data.MessageChainBuilder();
    }

    @Override
    public MessageChainBuilder append(Message message) {
        this.builder.append(((MiraiMessage) message).message());
        return this;
    }

    @Override
    public MessageChainBuilder append(String message) {
        this.builder.append(message);
        return this;
    }

    @Override
    public Message build() {
        return new MiraiMessage(this.builder.build());
    }
}