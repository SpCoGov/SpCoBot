/*
 * Copyright 2024 SpCo
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
import top.spco.core.event.Event;
import top.spco.core.event.EventFactory;

/**
 * 机器人相关事件
 *
 * @author SpCo
 * @version 2.0.0
 * @since 0.1.0
 */
public class BotEvents {
    private BotEvents() {
    }

    /**
     * Called at the bot online tick.
     */
    public static final Event<OnlineTick> ONLINE_TICK = EventFactory.createArrayBacked(OnlineTick.class, callbacks -> (bot) -> {
        for (OnlineTick event : callbacks) {
            event.onOnlineTick(bot);
        }
    });

    @FunctionalInterface
    public interface OnlineTick {
        void onOnlineTick(Bot<?> bot);
    }

    /**
     * Called at the bot offline tick.
     */
    public static final Event<OfflineTick> OFFLINE_TICK = EventFactory.createArrayBacked(OfflineTick.class, callbacks -> (bot) -> {
        for (OfflineTick event : callbacks) {
            event.onOfflineTick(bot);
        }
    });

    @FunctionalInterface
    public interface OfflineTick {
        void onOfflineTick(Bot<?> bot);
    }

    /**
     * Called at the bot active offline tick.
     */
    public static final Event<ActiveOfflineTick> ACTIVE_OFFLINE_TICK = EventFactory.createArrayBacked(ActiveOfflineTick.class, callbacks -> (bot) -> {
        for (ActiveOfflineTick event : callbacks) {
            event.onActiveOfflineTick(bot);
        }
    });

    @FunctionalInterface
    public interface ActiveOfflineTick {
        void onActiveOfflineTick(Bot<?> bot);
    }

    /**
     * Called at the bot force offline tick.
     */
    public static final Event<ForceOfflineTick> FORCE_OFFLINE_TICK = EventFactory.createArrayBacked(ForceOfflineTick.class, callbacks -> (bot) -> {
        for (ForceOfflineTick event : callbacks) {
            event.onForceOfflineTick(bot);
        }
    });

    @FunctionalInterface
    public interface ForceOfflineTick {
        void onForceOfflineTick(Bot<?> bot);
    }

    /**
     * Called at the bot dropped offline tick.
     */
    public static final Event<DroppedOfflineTick> DROPPED_OFFLINE_TICK = EventFactory.createArrayBacked(DroppedOfflineTick.class, callbacks -> (bot) -> {
        for (DroppedOfflineTick event : callbacks) {
            event.onDroppedOfflineTick(bot);
        }
    });

    @FunctionalInterface
    public interface DroppedOfflineTick {
        void onDroppedOfflineTick(Bot<?> bot);
    }

    /**
     * Called at the bot require-reconnect offline tick.
     */
    public static final Event<RequireReconnectOfflineTick> REQUIRE_RECONNECT_OFFLINE_TICK = EventFactory.createArrayBacked(RequireReconnectOfflineTick.class, callbacks -> (bot) -> {
        for (RequireReconnectOfflineTick event : callbacks) {
            event.onRequireReconnectOfflineTick(bot);
        }
    });

    @FunctionalInterface
    public interface RequireReconnectOfflineTick {
        void onRequireReconnectOfflineTick(Bot<?> bot);
    }
}