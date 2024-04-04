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

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.PermissionDeniedException;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;
import net.mamoe.mirai.utils.ExternalResource;
import org.checkerframework.checker.units.qual.C;
import top.spco.SpCoBot;
import top.spco.api.Image;
import top.spco.api.Interactive;
import top.spco.api.message.Message;
import top.spco.api.message.MessageSource;
import top.spco.api.message.service.MessageService;
import top.spco.util.tuple.ImmutablePair;

import java.io.File;

/**
 * @author SpCo
 * @version 2.0.0
 * @since 0.1.0
 */
class MiraiMessageServiceImpl implements MessageService {
    @Override
    public Message<?> at(long id) {
        return new MiraiMessage(new MessageChainBuilder().append(new At(id)).build());
    }

    @Override
    public Message<?> atAll() {
        return new MiraiMessage(new MessageChainBuilder().append(net.mamoe.mirai.message.data.AtAll.INSTANCE).build());
    }

    /**
     * @deprecated 请使用 {@link Message#append(String)}
     */
    @Override
    @Deprecated
    public Message<?> append(Message<?> original, Message<?> other) {
        return original.append(other);
    }

    /**
     * @deprecated 请使用 {@link Message#append(String)}
     */
    @Override
    @Deprecated
    public Message<?> append(Message<?> original, String other) {
        return original.append(other);
    }

    @Override
    public String getAtRegex() {
        return "\\[mirai:at:\\d+\\]";
    }

    @Override
    public ImmutablePair<MessageSource<?>, Message<?>> getQuote(Message<?> message) {
        try {
            MiraiMessage miraiMessage = ((MiraiMessage) message);
            for (var singleMessage : miraiMessage.wrapped()) {
                if (singleMessage instanceof QuoteReply quoteReply) {
                    return new ImmutablePair<>(new MiraiMessageSource(quoteReply.getSource()), new MiraiMessage(quoteReply.getSource().getOriginalMessage()));
                }
            }
            return null;
        } catch (Exception e) {
            SpCoBot.LOGGER.error(e);
        }
        return null;
    }

    @Override
    public void recall(Message<?> original) {
        try {
            net.mamoe.mirai.message.data.MessageSource.recall((MessageChain) original.wrapped());
        } catch (PermissionDeniedException e) {
            throw new top.spco.api.exception.PermissionDeniedException("权限不足");
        }

    }

    @Override
    public Message<?> asMessage(String content) {
        return new MiraiMessageChainBuilder().append(content).build();
    }

    @Override
    public Image<?> toImage(File image, Interactive<?> interactive) {
        return new MiraiImage(ExternalResource.uploadAsImage(image, (Contact) interactive.wrapped()));
    }
}