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
 * Represents a supplier of {@code boolean}-valued results.  This is the
 * {@code boolean}-producing primitive specialization of {@link Supplier}.
 *
 * <p>There is no requirement that a new or distinct result be returned each
 * time the supplier is invoked.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #getAsBoolean()}.
 *
 * @see Supplier
 * @since 3.1
 */
@FunctionalInterface
public interface BooleanSupplier {
    /**
     * Gets a result.
     *
     * @return a result
     */
    boolean getAsBoolean();
}