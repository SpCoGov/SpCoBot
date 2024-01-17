/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.spco.core.event;

import top.spco.core.event.impl.base.event.EventFactoryImpl;
import top.spco.core.resource.ResourceIdentifier;

import java.util.function.Function;

/**
 * Helper for creating {@link Event} classes.
 *
 * @author Fabric
 * @version 0.1.0
 * @since 0.1.0
 */
public final class EventFactory {
    private EventFactory() {
    }

    /**
     * Create an "array-backed" Event instance.
     *
     * <p>If your factory simply delegates to the listeners without adding custom behavior,
     * consider using {@linkplain #createArrayBacked(Class, Object, Function) the other overload}
     * if performance of this event is critical.
     *
     * @param type           The listener class type.
     * @param invokerFactory The invoker factory, combining multiple listeners into one instance.
     * @param <T>            The listener type.
     * @return The Event instance.
     */
    public static <T> Event<T> createArrayBacked(Class<? super T> type, Function<T[], T> invokerFactory) {
        return EventFactoryImpl.createArrayBacked(type, invokerFactory);
    }

    public static void ensureEventThreadName() {
        Thread currentThread = Thread.currentThread();
        if (!"Event Thread".equals(currentThread.getName())) {
            currentThread.setName("Event Thread");
        }
    }

    /**
     * Create an "array-backed" Event instance with a custom empty invoker,
     * for an event whose {@code invokerFactory} only delegates to the listeners.
     * <ul>
     *   <li>If there is no listener, the custom empty invoker will be used.</li>
     *   <li><b>If there is only one listener, that one will be used as the invoker
     *   and the factory will not be called.</b></li>
     *   <li>Only when there are at least two listeners will the factory be used.</li>
     * </ul>
     *
     * <p>Having a custom empty invoker (of type (...) -&gt; {}) increases performance
     * relative to iterating over an empty array; however, it only really matters
     * if the event is executed thousands of times a second.
     *
     * @param type           The listener class type.
     * @param emptyInvoker   The custom empty invoker.
     * @param invokerFactory The invoker factory, combining multiple listeners into one instance.
     * @param <T>            The listener type.
     * @return The Event instance.
     */
    public static <T> Event<T> createArrayBacked(Class<T> type, T emptyInvoker, Function<T[], T> invokerFactory) {
        return createArrayBacked(type, listeners -> {
            switch (listeners.length) {
                case 0 -> {
                    return emptyInvoker;
                }
                case 1 -> {
                    return listeners[0];
                }
                default -> {
                    return invokerFactory.apply(listeners);
                }
            }
        });
    }

    /**
     * Create an array-backed event with a list of default phases that get invoked in order.
     * Exposing the identifiers of the default phases as {@code public static final} constants is encouraged.
     *
     * <p>An event phase is a named group of listeners, which may be ordered before or after other groups of listeners.
     * This allows some listeners to take priority over other listeners.
     * Adding separate events should be considered before making use of multiple event phases.
     *
     * <p>Phases may be freely added to events created with any of the factory functions,
     * however using this function is preferred for widely used event phases.
     * If more phases are necessary, discussion with the author of the Event is encouraged.
     *
     * <p>Refer to {@link Event#addPhaseOrdering} for an explanation of event phases.
     *
     * @param type           The listener class type.
     * @param invokerFactory The invoker factory, combining multiple listeners into one instance.
     * @param defaultPhases  The default phases of this event, in the correct order. Must contain {@link Event#DEFAULT_PHASE}.
     * @param <T>            The listener type.
     * @return The Event instance.
     */
    public static <T> Event<T> createWithPhases(Class<? super T> type, Function<T[], T> invokerFactory, ResourceIdentifier... defaultPhases) {
        EventFactoryImpl.ensureContainsDefault(defaultPhases);
        EventFactoryImpl.ensureNoDuplicates(defaultPhases);

        Event<T> event = createArrayBacked(type, invokerFactory);

        for (int i = 1; i < defaultPhases.length; ++i) {
            event.addPhaseOrdering(defaultPhases[i - 1], defaultPhases[i]);
        }

        return event;
    }
}