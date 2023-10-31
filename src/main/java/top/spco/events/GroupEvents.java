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

import top.spco.base.api.Behavior;
import top.spco.base.api.Friend;
import top.spco.base.api.Group;
import top.spco.base.event.Event;
import top.spco.base.event.EventFactory;

/**
 * <p>
 * Created on 2023/10/26 0026 19:56
 * <p>
 *
 * @author SpCo
 * @version 1.0
 * @since 1.0
 */
public class GroupEvents {
    private GroupEvents() {
    }

    /**
     * Called when the robot is invited to join the group.
     */
    public static final Event<InvitedJoinGroup> INVITED_JOIN_GROUP = EventFactory.createArrayBacked(InvitedJoinGroup.class, callbacks -> (eventId, invitorId, groupId, invitor, behavior) -> {
        for (InvitedJoinGroup event : callbacks) {
            event.invitedJoinGroup(eventId, invitorId, groupId, invitor, behavior);
        }
    });

    @FunctionalInterface
    public interface InvitedJoinGroup {
        void invitedJoinGroup(long eventId, long invitorId, long groupId, Friend invitor, Behavior behavior);
    }

    /**
     * Called when an account requests to join the group.
     */
    public static final Event<RequestJoinGroup> REQUEST_JOIN_GROUP = EventFactory.createArrayBacked(RequestJoinGroup.class, callbacks -> (eventId, fromId, group, behavior) -> {
        for (RequestJoinGroup event : callbacks) {
            event.requestJoinGroup(eventId, fromId, group, behavior);
        }
    });

    @FunctionalInterface
    public interface RequestJoinGroup {
        void requestJoinGroup(long eventId, long fromId, Group group, Behavior behavior);
    }
}