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

import top.spco.core.event.Event;
import top.spco.core.event.EventFactory;

/**
 * 插件事件
 *
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public class PluginEvents {
    private PluginEvents() {
    }

    /**
     * Called at enable of the plugin tick.
     */
    public static final Event<EnableTick> ENABLE_PLUGIN_TICK = EventFactory.createArrayBacked(EnableTick.class, callbacks -> () -> {
        for (EnableTick event : callbacks) {
            event.onEnableTick();
        }
    });

    @FunctionalInterface
    public interface EnableTick {
        void onEnableTick();
    }

    /**
     * Called at disable of the plugin tick.
     */
    public static final Event<DisableTick> DISABLE_PLUGIN_TICK = EventFactory.createArrayBacked(DisableTick.class, callbacks -> () -> {
        for (DisableTick event : callbacks) {
            event.onDisableTick();
        }
    });

    @FunctionalInterface
    public interface DisableTick {
        void onDisableTick();
    }

    /**
     * Called at the load of the plugin tick.
     */
    public static final Event<LoadTick> LOAD_PLUGIN_TICK = EventFactory.createArrayBacked(LoadTick.class, callbacks -> () -> {
        for (LoadTick event : callbacks) {
            event.onLoadTick();
        }
    });

    @FunctionalInterface
    public interface LoadTick {
        void onLoadTick();
    }
}