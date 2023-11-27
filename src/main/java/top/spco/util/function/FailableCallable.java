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

/**
 * A functional interface like {@link java.util.concurrent.Callable} that declares a {@link Throwable}.
 *
 * @param <R> Return type.
 * @param <E> The kind of thrown exception or error.
 * @since 0.3.1
 */
@FunctionalInterface
public interface FailableCallable<R, E extends Throwable> {
    /**
     * Calls the callable.
     *
     * @return The value returned from the callable
     * @throws E if the callable fails
     */
    R call() throws E;
}