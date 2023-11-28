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
package top.spco.util.function;

import java.util.function.IntSupplier;

/**
 * A functional interface like {@link IntSupplier} but for {@code short} that declares a {@link Throwable}.
 *
 * @param <E> The kind of thrown exception or error.
 * @since 0.3.1
 */
@FunctionalInterface
public interface FailableShortSupplier<E extends Throwable> {

    /**
     * Supplies an int.
     *
     * @return a result
     * @throws E if the supplier fails
     */
    short getAsShort() throws E;
}