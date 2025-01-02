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
package top.spco.mirai;

import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;
import top.spco.api.message.Message;
import top.spco.api.message.MessageSource;

import java.util.NoSuchElementException;

/**
 * @author SpCo
 * @version 3.0.2
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
    public Message<net.mamoe.mirai.message.data.MessageChain> append(Message<?> another) {
        wrap(wrapped().plus((net.mamoe.mirai.message.data.Message) another.wrapped()));
        return this;
    }

    @Override
    public Message<net.mamoe.mirai.message.data.MessageChain> append(String another) {
        wrap(wrapped().plus(another));
        return this;
    }

    @Override
    public Message<?> toMessage() {
        return this;
    }

    @Override
    public MessageSource<?> getSource() {
        try {
            return new MiraiMessageSource(wrapped().get(net.mamoe.mirai.message.data.MessageSource.Key));
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @Override
    public String serialize() {
        return this.wrapped().serializeToMiraiCode();
    }
}