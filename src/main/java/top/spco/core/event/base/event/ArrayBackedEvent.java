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
import top.spco.core.event.Event;
import top.spco.core.event.base.toposort.NodeSorting;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;

/**
 * 用于管理事件及其阶段的类，可以注册和处理事件监听器
 *
 * @author SpCo
 * @version 1.0
 * @since 1.0
 */
public class ArrayBackedEvent<T> extends Event<T> {
    private final Function<T[], T> invokerFactory;
    private final Object lock = new Object();
    private T[] handlers;
    /**
     * Registered event phases.
     */
    private final Map<ResourceIdentifier, EventPhaseData<T>> phases = new LinkedHashMap<>();
    /**
     * Phases sorted in the correct dependency order.
     */
    private final List<EventPhaseData<T>> sortedPhases = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public ArrayBackedEvent(Class<? super T> type, Function<T[], T> invokerFactory) {
        this.invokerFactory = invokerFactory;
        this.handlers = (T[]) Array.newInstance(type, 0);
        update();
    }

    public void update() {
        this.invoker = invokerFactory.apply(handlers);
    }

    @Override
    public void register(T listener) {
        register(DEFAULT_PHASE, listener);
    }

    @Override
    public void register(ResourceIdentifier phaseIdentifier, T listener) {
        Objects.requireNonNull(phaseIdentifier, "Tried to register a listener for a null phase!");
        Objects.requireNonNull(listener, "Tried to register a null listener!");

        synchronized (lock) {
            getOrCreatePhase(phaseIdentifier, true).addListener(listener);
            rebuildInvoker(handlers.length + 1);
        }
    }

    private EventPhaseData<T> getOrCreatePhase(ResourceIdentifier id, boolean sortIfCreate) {
        EventPhaseData<T> phase = phases.get(id);

        if (phase == null) {
            phase = new EventPhaseData<>(id, handlers.getClass().getComponentType());
            phases.put(id, phase);
            sortedPhases.add(phase);

            if (sortIfCreate) {
                NodeSorting.sort(sortedPhases, "event phases", Comparator.comparing(data -> data.id));
            }
        }

        return phase;
    }

    private void rebuildInvoker(int newLength) {
        // Rebuild handlers.
        if (sortedPhases.size() == 1) {
            // Special case with a single phase: use the array of the phase directly.
            handlers = sortedPhases.get(0).listeners;
        } else {
            @SuppressWarnings("unchecked")
            T[] newHandlers = (T[]) Array.newInstance(handlers.getClass().getComponentType(), newLength);
            int newHandlersIndex = 0;

            for (EventPhaseData<T> existingPhase : sortedPhases) {
                int length = existingPhase.listeners.length;
                System.arraycopy(existingPhase.listeners, 0, newHandlers, newHandlersIndex, length);
                newHandlersIndex += length;
            }

            handlers = newHandlers;
        }

        // Rebuild invoker.
        update();
    }

    @Override
    public void addPhaseOrdering(ResourceIdentifier firstPhase, ResourceIdentifier secondPhase) {
        Objects.requireNonNull(firstPhase, "Tried to add an ordering for a null phase.");
        Objects.requireNonNull(secondPhase, "Tried to add an ordering for a null phase.");
        if (firstPhase.equals(secondPhase)) throw new IllegalArgumentException("Tried to add a phase that depends on itself.");

        synchronized (lock) {
            EventPhaseData<T> first = getOrCreatePhase(firstPhase, false);
            EventPhaseData<T> second = getOrCreatePhase(secondPhase, false);
            EventPhaseData.link(first, second);
            NodeSorting.sort(this.sortedPhases, "event phases", Comparator.comparing(data -> data.id));
            rebuildInvoker(handlers.length);
        }
    }
}