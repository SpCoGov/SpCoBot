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

import org.jetbrains.annotations.ApiStatus;

/**
 * Base class for Fabric's event implementations.
 *
 * @param <T> The listener type.
 * @author Fabric
 * @version 2.0.0
 * @see EventFactory
 * @since 0.1.0
 */
@ApiStatus.NonExtendable
public abstract class Event<T> {
    /**
     * The invoker field. This should be updated by the implementation to
     * always refer to an instance containing all code that should be
     * executed upon event emission.
     */
    protected volatile T invoker;

    /**
     * Returns the invoker instance.
     *
     * <p>An "invoker" is an object which hides multiple registered
     * listeners of type T under one instance of type T, executing
     * them and leaving early as necessary.
     *
     * @return The invoker instance.
     */
    public final T invoker() {
        EventFactory.ensureEventThreadName();
        return invoker;
    }

    /**
     * Register a listener to the event, in the default phase.
     *
     * @param listener The desired listener.
     */
    public abstract void register(T listener);

    /**
     * The identifier of the default phase.
     */
    public static final String DEFAULT_PHASE = "default";

    /**
     * Register a listener to the event for the specified phase.
     *
     * @param phase    Identifier of the phase this listener should be registered for. It will be created if it didn't exist yet.
     * @param listener The desired listener.
     */
    public void register(String phase, T listener) {
        // This is done to keep compatibility with existing Event subclasses, but they should really not be subclassing Event.
        register(listener);
    }
}