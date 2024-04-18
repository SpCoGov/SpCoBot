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
import top.spco.api.Identifiable;
import top.spco.api.Interactive;
import top.spco.core.event.Event;
import top.spco.core.event.EventFactory;

/**
 * 用户相关事件
 *
 * @author SpCo
 * @version 2.0.0
 * @since 2.0.0
 */
public class UserEvents {
    private UserEvents() {

    }

    /**
     * Called at the nudged tick.
     */
    public static final Event<NudgedTick> NUDGED_TICK = EventFactory.createArrayBacked(NudgedTick.class, callbacks -> (bot, from, target, interactive, action, suffix) -> {
        for (NudgedTick event : callbacks) {
            event.onNudgedTick(bot, from, target, interactive, action, suffix);
        }
    });

    @FunctionalInterface
    public interface NudgedTick {
        void onNudgedTick(Bot<?> bot, Identifiable<?> from, Identifiable<?> target, Interactive<?> subject, String action, String suffix);
    }
}