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

import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;
import top.spco.api.message.Message;

/**
 * @author SpCo
 * @version 2.0.0
 * @since 0.1.0
 */
class MiraiMessage extends Message<net.mamoe.mirai.message.data.MessageChain> {
    protected MiraiMessage(MessageChain message) {
        super(message);
    }

    @Override
    public String toMessageContext() {
        return wrapped().contentToString();
    }

    @Override
    public Message<MessageChain> quoteReply(Message<?> toQuote) {
        wrap(new MessageChainBuilder().append(new QuoteReply((MessageChain)toQuote.wrapped())).append(wrapped()).build());
        return this;
    }

    @Override
    public Message<net.mamoe.mirai.message.data.MessageChain> append(Message<?> message) {
        wrap(new MessageChainBuilder().append(wrapped()).append((net.mamoe.mirai.message.data.Message) message.wrapped()).build());
        return this;
    }

    @Override
    public Message<net.mamoe.mirai.message.data.MessageChain> append(String message) {
        wrap(new MessageChainBuilder().append(wrapped()).append(message).build());
        return this;
    }

    @Override
    public Message<?> toMessage() {
        return this;
    }

    @Override
    public String serialize() {
        return this.wrapped().serializeToMiraiCode();
    }
}