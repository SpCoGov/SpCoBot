/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.spco.util.tuple;

import java.io.Serial;
import java.util.Map;

/**
 * An immutable pair consisting of two {@link Object} elements.
 *
 * <p>Although the implementation is immutable, there is no restriction on the objects
 * that may be stored. If mutable objects are stored in the pair, then the pair
 * itself effectively becomes mutable. The class is also {@code final}, so a subclass
 * can not add undesirable behavior.</p>
 *
 * <p>#ThreadSafe# if both paired objects are thread-safe</p>
 *
 * @param <L> the left element type
 * @param <R> the right element type
 * @since 0.3.1
 */
public class ImmutablePair<L, R> extends Pair<L, R> {
    /**
     * An immutable pair of nulls.
     */
    // This is not defined with generics to avoid warnings in call sites.
    @SuppressWarnings("rawtypes")
    private static final ImmutablePair NULL = new ImmutablePair<>(null, null);

    /**
     * Serialization version
     */
    @Serial
    private static final long serialVersionUID = 4954918890077093841L;

    /**
     * Creates an immutable pair of two objects inferring the generic types.
     *
     * <p>This factory allows the pair to be created using inference to
     * obtain the generic types.</p>
     *
     * @param <L>  the left element type
     * @param <R>  the right element type
     * @param left the left element, may be null
     * @return a pair formed from the two parameters, not null
     * @since 0.3.1
     */
    public static <L, R> Pair<L, R> left(final L left) {
        return ImmutablePair.of(left, null);
    }

    /**
     * Returns an immutable pair of nulls.
     *
     * @param <L> the left element of this pair. Value is {@code null}.
     * @param <R> the right element of this pair. Value is {@code null}.
     * @return an immutable pair of nulls.
     * @since 0.3.1
     */
    @SuppressWarnings("unchecked")
    public static <L, R> ImmutablePair<L, R> nullPair() {
        return NULL;
    }

    /**
     * Creates an immutable pair of two objects inferring the generic types.
     *
     * <p>This factory allows the pair to be created using inference to
     * obtain the generic types.</p>
     *
     * @param <L>   the left element type
     * @param <R>   the right element type
     * @param left  the left element, may be null
     * @param right the right element, may be null
     * @return a pair formed from the two parameters, not null
     */
    public static <L, R> ImmutablePair<L, R> of(final L left, final R right) {
        return left != null || right != null ? new ImmutablePair<>(left, right) : nullPair();
    }

    /**
     * Creates an immutable pair from a map entry.
     *
     * <p>This factory allows the pair to be created using inference to
     * obtain the generic types.</p>
     *
     * @param <L>  the left element type
     * @param <R>  the right element type
     * @param pair the existing map entry.
     * @return a pair formed from the map entry
     * @since 0.3.1
     */
    public static <L, R> ImmutablePair<L, R> of(final Map.Entry<L, R> pair) {
        return pair != null ? new ImmutablePair<>(pair.getKey(), pair.getValue()) : nullPair();
    }

    /**
     * Creates an immutable pair of two objects inferring the generic types.
     *
     * <p>This factory allows the pair to be created using inference to
     * obtain the generic types.</p>
     *
     * @param <L>   the left element type
     * @param <R>   the right element type
     * @param right the right element, may be null
     * @return a pair formed from the two parameters, not null
     * @since 0.3.1
     */
    public static <L, R> Pair<L, R> right(final R right) {
        return ImmutablePair.of(null, right);
    }

    /**
     * Left object
     */
    public final L left;

    /**
     * Right object
     */
    public final R right;

    /**
     * Create a new pair instance.
     *
     * @param left  the left value, may be null
     * @param right the right value, may be null
     */
    public ImmutablePair(final L left, final R right) {
        this.left = left;
        this.right = right;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public L getLeft() {
        return left;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R getRight() {
        return right;
    }

    /**
     * Throws {@link UnsupportedOperationException}.
     *
     * <p>This pair is immutable, so this operation is not supported.</p>
     *
     * @param value the value to set
     * @return never
     * @throws UnsupportedOperationException as this operation is not supported
     */
    @Override
    public R setValue(final R value) {
        throw new UnsupportedOperationException();
    }
}