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

import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;

import top.spco.api.message.Message;
import top.spco.api.message.MessageSource;
import top.spco.api.message.service.MessageService;
import top.spco.util.tuple.ImmutablePair;

/**
 * @author SpCo
 * @version 0.3.0
 * @since 0.1.0
 */
class MiraiMessageServiceImpl implements MessageService {
    @Override
    public Message at(long id) {
        return new MiraiMessage(new MessageChainBuilder().append(new At(id)).build());
    }

    @Override
    public Message atAll() {
        return new MiraiMessage(new MessageChainBuilder().append(net.mamoe.mirai.message.data.AtAll.INSTANCE).build());
    }

    @Override
    public Message append(Message original, Message other) {
        return new MiraiMessageChainBuilder().append(original).append(other).build();
    }

    @Override
    public Message append(Message original, String other) {
        return new MiraiMessageChainBuilder().append(original).append(other).build();
    }

    @Override
    public String getAtRegex() {
        return "\\[mirai:at:\\d+\\]";
    }

    @Override
    public ImmutablePair<MessageSource, Message> getQuote(Message message) {
        try {
            MiraiMessage miraiMessage = ((MiraiMessage) message);
            for (var singleMessage : miraiMessage.message()) {
                if (singleMessage instanceof QuoteReply quoteReply) {
                    return new ImmutablePair<>(new MiraiMessageSource(quoteReply.getSource()), new MiraiMessage(quoteReply.getSource().getOriginalMessage()));
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}