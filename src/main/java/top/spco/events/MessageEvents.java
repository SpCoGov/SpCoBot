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
package top.spco.events;

import top.spco.SpCoBot;
import top.spco.api.*;
import top.spco.api.message.Message;
import top.spco.api.message.MessageSource;
import top.spco.core.event.Event;
import top.spco.core.event.EventFactory;

/**
 * 消息相关事件
 *
 * @author SpCo
 * @version 4.1.0
 * @since 0.1.0
 */
public class MessageEvents {
    private MessageEvents() {
    }

    /**
     * Called when a group message is received.
     */
    public static final Event<GroupMessage> GROUP_MESSAGE = EventFactory.createArrayBacked(GroupMessage.class, callbacks -> (bot, source, sender, message, time) -> {
        for (GroupMessage event : callbacks) {
            SpCoBot.getInstance().getRuntimeStatistic().group("收到消息").add("群消息");
            event.onGroupMessage(bot, source, sender, message, time);
        }
    });

    @FunctionalInterface
    public interface GroupMessage {
        void onGroupMessage(Bot<?> bot, Group<?> source, User<?> sender, Message<?> message, int time);
    }

    /**
     * Called when a channel message is received.
     */
    public static final Event<ChannelMessage> CHANNEL_MESSAGE = EventFactory.createArrayBacked(ChannelMessage.class, callbacks -> (bot, source, sender, message, time) -> {
        for (ChannelMessage event : callbacks) {
            SpCoBot.getInstance().getRuntimeStatistic().group("收到消息").add("频道消息");
            event.onChannelMessage(bot, source, sender, message, time);
        }
    });

    @FunctionalInterface
    public interface ChannelMessage {
        void onChannelMessage(Bot<?> bot, Channel<?> source, User<?> sender, Message<?> message, int time);
    }

    /**
     * Called when a private message is received.
     */
    public static final Event<UserMessage> PRIVATE_MESSAGE = EventFactory.createArrayBacked(UserMessage.class, callbacks -> (bot, sender, message, time) -> {
        for (UserMessage event : callbacks) {
            SpCoBot.getInstance().getRuntimeStatistic().group("收到消息").add("群私聊消息");
            event.onPrivateMessage(bot, sender, message, time);
        }
    });

    @FunctionalInterface
    public interface UserMessage {
        void onPrivateMessage(Bot<?> bot, User<?> sender, Message<?> message, int time);
    }

    /**
     * Called after actively sending a private message.
     */
    public static final Event<PrivateMessagePostSend> PRIVATE_MESSAGE_POST_SEND = EventFactory.createArrayBacked(PrivateMessagePostSend.class, callbacks -> (bot, friend, message) -> {
        for (PrivateMessagePostSend event : callbacks) {
            SpCoBot.getInstance().getRuntimeStatistic().group("发出消息").add("私聊消息");
            event.onPrivateMessagePostSend(bot, friend, message);
        }
    });


    @FunctionalInterface
    public interface PrivateMessagePostSend {
        void onPrivateMessagePostSend(Bot<?> bot, User<?> friend, Message<?> message);
    }

    /**
     * Called when a group message is recalled.
     */
    public static final Event<GroupMessageRecall> GROUP_MESSAGE_RECALL = EventFactory.createArrayBacked(GroupMessageRecall.class, callbacks -> (bot, source, sender, operator, message) -> {
        for (GroupMessageRecall event : callbacks) {
            event.onGroupMessageRecall(bot, source, sender, operator, message);
        }
    });

    @FunctionalInterface
    public interface GroupMessageRecall {
        void onGroupMessageRecall(Bot<?> bot, Group<?> source, User<?> sender, User<?> operator, MessageSource<?> recalledMessage);
    }

    /**
     * Called when a private message is recalled.
     */
    public static final Event<PrivateMessageRecall> PRIVATE_MESSAGE_RECALL = EventFactory.createArrayBacked(PrivateMessageRecall.class, callbacks -> (bot, sender, operator, message) -> {
        for (PrivateMessageRecall event : callbacks) {
            event.onPrivateMessageRecall(bot, sender, operator, message);
        }
    });

    @FunctionalInterface
    public interface PrivateMessageRecall {
        void onPrivateMessageRecall(Bot<?> bot, User<?> sender, User<?> operator, MessageSource<?> recalledMessage);
    }
}