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
package top.spco.core.event.base.event;

import top.spco.core.resource.ResourceIdentifier;
import top.spco.core.event.base.toposort.SortableNode;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Data of an {@link ArrayBackedEvent} phase.
 *
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
class EventPhaseData<T> extends SortableNode<EventPhaseData<T>> {
    final ResourceIdentifier id;
    T[] listeners;

    @SuppressWarnings("unchecked")
    EventPhaseData(ResourceIdentifier id, Class<?> listenerClass) {
        this.id = id;
        this.listeners = (T[]) Array.newInstance(listenerClass, 0);
    }

    void addListener(T listener) {
        int oldLength = listeners.length;
        listeners = Arrays.copyOf(listeners, oldLength + 1);
        listeners[oldLength] = listener;
    }

    @Override
    protected String getDescription() {
        return id.toString();
    }
}