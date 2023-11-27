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

import java.util.function.Supplier;

/**
 * Helps use {@link Supplier}.
 *
 * @since 0.3.1
 */
public class Suppliers {
    /**
     * Null-safe call to {@link Supplier#get()}.
     *
     * @param <T>      the type of results supplied by this supplier.
     * @param supplier the supplier or null.
     * @return Result of {@link Supplier#get()} or null.
     */
    public static <T> T get(final Supplier<T> supplier) {
        return supplier == null ? null : supplier.get();
    }
}