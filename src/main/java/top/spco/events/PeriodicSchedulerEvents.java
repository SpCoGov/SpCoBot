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
 * 定时事件
 *
 * @author SpCo
 * @version 0.1.2
 * @since 0.1.2
 */
public class PeriodicSchedulerEvents {
    private PeriodicSchedulerEvents() {
    }

    /**
     * Called every second.
     */
    public static final Event<SecondTick> SECOND_TICK = EventFactory.createArrayBacked(SecondTick.class, callbacks -> () -> {
        for (SecondTick event : callbacks) {
            event.onSecondTick();
        }
    });

    @FunctionalInterface
    public interface SecondTick {
        void onSecondTick();
    }

    /**
     * Called every minute.
     */
    public static final Event<MinuteTick> MINUTE_TICK = EventFactory.createArrayBacked(MinuteTick.class, callbacks -> () -> {
        for (MinuteTick event : callbacks) {
            event.onMinuteTick();
        }
    });

    @FunctionalInterface
    public interface MinuteTick {
        void onMinuteTick();
    }
}