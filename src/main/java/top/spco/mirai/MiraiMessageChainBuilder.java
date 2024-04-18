/*
 * Copyright 2024 SpCo
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

import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.QuoteReply;
import top.spco.api.message.Message;
import top.spco.api.message.MessageChainBuilder;

/**
 * @author SpCo
 * @version 2.0.0
 * @since 0.1.0
 */
class MiraiMessageChainBuilder extends MessageChainBuilder<net.mamoe.mirai.message.data.MessageChainBuilder> {
    /**
     * @deprecated 需要回复一条消息请使用 {@link Message#quoteReply(Message)}*/
    @Deprecated
    public MiraiMessageChainBuilder(Message<?> toQuote) {
        super(new net.mamoe.mirai.message.data.MessageChainBuilder());
        this.wrapped().append(new QuoteReply((MessageChain) toQuote.wrapped()));
    }

    public MiraiMessageChainBuilder() {
        super(new net.mamoe.mirai.message.data.MessageChainBuilder());
    }

    @Override
    public MessageChainBuilder<net.mamoe.mirai.message.data.MessageChainBuilder> append(Message<?> message) {
        this.wrapped().append((net.mamoe.mirai.message.data.Message) message.wrapped());
        return this;
    }

    @Override
    public MessageChainBuilder<net.mamoe.mirai.message.data.MessageChainBuilder> append(String message) {
        this.wrapped().append(message);
        return this;
    }

    @Override
    public Message<?> build() {
        return new MiraiMessage(this.wrapped().build());
    }
}