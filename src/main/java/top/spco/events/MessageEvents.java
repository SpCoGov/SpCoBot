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
package top.spco.events;

import top.spco.api.Bot;
import top.spco.api.Friend;
import top.spco.api.Group;
import top.spco.api.Member;
import top.spco.api.message.Message;
import top.spco.core.event.Event;
import top.spco.core.event.EventFactory;

/**
 * Created on 2023/10/26 0026 10:39
 *
 * @author SpCo
 * @version 1.1
 * @since 1.0
 */
public class MessageEvents {
    private MessageEvents() {
    }

    /**
     * Called when a group message is received.
     */
    public static final Event<GroupMessage> GROUP_MESSAGE = EventFactory.createArrayBacked(GroupMessage.class, callbacks -> (bot, source, sender, message, time) -> {
        for (GroupMessage event : callbacks) {
            event.onGroupMessage(bot, source, sender, message, time);
        }
    });

    @FunctionalInterface
    public interface GroupMessage {
        void onGroupMessage(Bot bot, Group source, Member sender, Message message, int time);
    }

    /**
     * Called when a group temp-message is received.
     */
    public static final Event<GroupTempMessage> GROUP_TEMP_MESSAGE = EventFactory.createArrayBacked(GroupTempMessage.class, callbacks -> (bot, source, sender, message, time) -> {
        for (GroupTempMessage event : callbacks) {
            event.onGroupTempMessage(bot, source, sender, message, time);
        }
    });

    @FunctionalInterface
    public interface GroupTempMessage {
        void onGroupTempMessage(Bot bot, Member source, Member sender, Message message, int time);
    }

    /**
     * Called when a friend message is received.
     */
    public static final Event<FriendMessage> FRIEND_MESSAGE = EventFactory.createArrayBacked(FriendMessage.class, callbacks -> (bot, sender, message, time) -> {
        for (FriendMessage event : callbacks) {
            event.onFriendMessage(bot, sender, message, time);
        }
    });

    @FunctionalInterface
    public interface FriendMessage {
        void onFriendMessage(Bot bot, Friend sender, Message message, int time);
    }

    /**
     * Called after actively sending a group message.
     */
    public static final Event<GroupMessagePostSend> GROUP_MESSAGE_POST_SEND = EventFactory.createArrayBacked(GroupMessagePostSend.class, callbacks -> (bot, group, message) -> {
        for (GroupMessagePostSend event : callbacks) {
            event.onGroupMessagePostSend(bot, group, message);
        }
    });

    @FunctionalInterface
    public interface GroupMessagePostSend {
        void onGroupMessagePostSend(Bot bot, Group group, Message message);
    }
}