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

import top.spco.api.Behavior;
import top.spco.api.Group;
import top.spco.core.event.Event;
import top.spco.core.event.EventFactory;

/**
 * Created on 2023/10/27 0027 1:02
 *
 * @author SpCo
 * @version 1.0
 * @since 1.0
 */
public class FriendEvents {
    private FriendEvents() {

    }

    /**
     * Called when an account requests to add a robot as a friend.
     */
    public static final Event<RequestedAsFriend> REQUESTED_AS_FRIEND = EventFactory.createArrayBacked(RequestedAsFriend.class, callbacks -> (eventId, message, fromId, fromGroupId, fromGroup, behavior) -> {
        for (RequestedAsFriend event : callbacks) {
            event.requestedAsFriend(eventId, message, fromId, fromGroupId, fromGroup, behavior);
        }
    });

    @FunctionalInterface
    public interface RequestedAsFriend {
        void requestedAsFriend(long eventId, String message, long fromId, long fromGroupId, Group fromGroup, Behavior behavior);
    }
}