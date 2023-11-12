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
package top.spco.core.event.base;

import com.google.common.collect.MapMaker;
import top.spco.core.event.Event;
import top.spco.core.event.base.event.ArrayBackedEvent;
import top.spco.core.resource.ResourceIdentifier;

import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

/**
 * Created on 2023/10/25 0025 16:46
 *
 * @author SpCo
 * @version 1.2
 * @since 1.0
 */
public final class EventFactoryImpl {
    private static final Set<ArrayBackedEvent<?>> ARRAY_BACKED_EVENTS
            = Collections.newSetFromMap(new MapMaker().weakKeys().makeMap());

    private EventFactoryImpl() { }

    public static void invalidate() {
        ARRAY_BACKED_EVENTS.forEach(ArrayBackedEvent::update);
    }

    public static <T> Event<T> createArrayBacked(Class<? super T> type, Function<T[], T> invokerFactory) {
        ArrayBackedEvent<T> event = new ArrayBackedEvent<>(type, invokerFactory);
        ARRAY_BACKED_EVENTS.add(event);
        return event;
    }

    public static void ensureContainsDefault(ResourceIdentifier[] defaultPhases) {
        for (ResourceIdentifier id : defaultPhases) {
            if (id.equals(Event.DEFAULT_PHASE)) {
                return;
            }
        }

        throw new IllegalArgumentException("The event phases must contain Event.DEFAULT_PHASE.");
    }

    public static void ensureNoDuplicates(ResourceIdentifier[] defaultPhases) {
        for (int i = 0; i < defaultPhases.length; ++i) {
            for (int j = i+1; j < defaultPhases.length; ++j) {
                if (defaultPhases[i].equals(defaultPhases[j])) {
                    throw new IllegalArgumentException("Duplicate event phase: " + defaultPhases[i]);
                }
            }
        }
    }
}