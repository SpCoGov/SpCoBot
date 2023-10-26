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

import top.spco.base.api.Group;
import top.spco.base.api.Member;
import top.spco.base.api.message.Message;
import top.spco.base.event.Event;
import top.spco.base.event.EventFactory;

/**
 * <p>
 * Created on 2023/10/26 0026 10:39
 * <p>
 *
 * @author SpCo
 * @version 1.0
 * @since 1.0
 */
public class MessageEvents {
    private MessageEvents() {
    }

    /**
     * Called when a group message is received.
     */
    public static final Event<GroupMessage> GROUP_MESSAGE = EventFactory.createArrayBacked(GroupMessage.class, callbacks -> (source, sender, message, time) -> {
        for (GroupMessage event : callbacks) {
            event.onGroupMessage(source, sender, message, time);
        }
    });

    @FunctionalInterface
    public interface GroupMessage {
        void onGroupMessage(Group source, Member sender, Message message, int time);
    }

    /**
     * Called when a group temp-message is received.
     */
    public static final Event<GroupTempMessage> GROUP_TEMP_MESSAGE = EventFactory.createArrayBacked(GroupTempMessage.class, callbacks -> (source, sender, message, time) -> {
        for (GroupTempMessage event : callbacks) {
            event.onGroupTempMessage(source, sender, message, time);
        }
    });

    @FunctionalInterface
    public interface GroupTempMessage {
        void onGroupTempMessage(Group source, Member sender, Message message, int time);
    }
}