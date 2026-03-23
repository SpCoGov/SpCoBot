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

import net.mamoe.mirai.contact.PermissionDeniedException;
import org.jetbrains.annotations.NotNull;
import top.spco.api.Identifiable;
import top.spco.api.Interactive;
import top.spco.api.exception.PlatformMismatchException;
import top.spco.api.message.Image;
import top.spco.api.message.Message;
import top.spco.api.message.MessageSource;
import top.spco.api.message.service.MessageService;
import top.spco.core.Platform;
import top.spco.util.tuple.ImmutablePair;

import java.io.File;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author SpCo
 * @version 4.1.0
 * @since 0.1.0
 */
class MiraiMessageServiceImpl implements MessageService {
    private void requireQQ(Object target, String argumentName) {
        if (!(target instanceof Identifiable identifiable)) {
            return;
        }
        Platform platform = identifiable.getPlatform();
        if (platform != null && platform != Platform.QQ) {
            throw new PlatformMismatchException(argumentName, Platform.QQ, platform);
        }
    }

    @Override
    public Message at(long id) {
//        return new MiraiMessage(new MessageChainBuilder().append(new At(id)).build());
        return null;
    }

    @Deprecated
    @Override
    public Message at(long id, String message) {
//        return new MiraiMessage(new MessageChainBuilder().append(new At(id)).build());
        return null;
    }

    @Override
    public Message atAll() {
        //return new MiraiMessage(new MessageChainBuilder().append(net.mamoe.mirai.message.data.AtAll.INSTANCE).build());
        return null;
    }

    @Override
    public String getFirstMentioned(Message message, String phrase) {
        requireQQ(message, "message");
        Pattern pattern = Pattern.compile("\\[mirai:at:\\d+]");
        Matcher matcher = pattern.matcher(message.toMessageContext());
        Matcher atMatcher = Pattern.compile("^@(\\d+)$").matcher(phrase);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1)) + "";
        } else if (atMatcher.find()) {
            try {
                String id = atMatcher.group(1);
                return Long.parseLong(id) + "";
            } catch (NumberFormatException e) {
                return null;
            }
        } else {
            try {
                return Long.parseLong(phrase) + "";
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    @Override
    public ImmutablePair<@NotNull MessageSource, @NotNull Message> getQuote(Message message) {
//        requireQQ(message, "message");
//        try {
//            MiraiMessage miraiMessage = ((MiraiMessage) message);
//            for (var singleMessage : miraiMessage.wrapped()) {
//                if (singleMessage instanceof QuoteReply quoteReply) {
//                    return new ImmutablePair<>(new MiraiMessageSource(quoteReply.getSource()), new MiraiMessage(quoteReply.getSource().getOriginalMessage()));
//                }
//            }
//            return null;
//        } catch (Exception e) {
//            SpCoBot.LOGGER.error(e);
//        }
        return null;
    }

    @Override
    public void recall(MessageSource original) {

    }

    @Override
    public Message asMessage(String content) {
        //return new MiraiMessage(new net.mamoe.mirai.message.data.MessageChainBuilder().append(content).build());
        return null;
    }

    @Override
    public Image toImage(File image, Interactive interactive) {
        requireQQ(interactive, "interactive");
//        return new MiraiImage(ExternalResource.uploadAsImage(image, (Contact) interactive.wrapped()));
        return null;
    }

    @Override
    public Image toImage(InputStream image, Interactive interactive) {
        requireQQ(interactive, "interactive");
//        return new MiraiImage(ExternalResource.uploadAsImage(image, (Contact) interactive.wrapped()));
        return null;
    }
}