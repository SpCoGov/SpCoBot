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
package top.spco.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

/**
 * Operations on {@link Object}.
 *
 * <p>This class tries to handle {@code null} input gracefully.
 * An exception will generally not be thrown for a {@code null} input.
 * Each method documents its behavior in more detail.</p>
 *
 * <p>#ThreadSafe#</p>
 *
 * @since 0.1.0
 */
public class ObjectUtils {

    /**
     * Class used as a null placeholder where {@code null}
     * has another meaning.
     *
     * <p>For example, in a {@link HashMap} the
     * {@link java.util.HashMap#get(Object)} method returns
     * {@code null} if the {@link Map} contains {@code null} or if there is
     * no matching key. The {@code null} placeholder can be used to distinguish
     * between these two cases.</p>
     *
     * <p>Another example is {@link Hashtable}, where {@code null}
     * cannot be stored.</p>
     */
    public static class Null implements Serializable {
        /**
         * Required for serialization support. Declare serialization compatibility with Commons Lang 1.0
         *
         * @see java.io.Serializable
         */
        private static final long serialVersionUID = 7092611880189329093L;

        /**
         * Restricted constructor - singleton.
         */
        Null() {
        }

        /**
         * Ensure Singleton after serialization.
         *
         * @return the singleton value
         */
        private Object readResolve() {
            return NULL;
        }
    }

    private static final char AT_SIGN = '@';

    /**
     * Singleton used as a {@code null} placeholder where
     * {@code null} has another meaning.
     *
     * <p>For example, in a {@link HashMap} the
     * {@link java.util.HashMap#get(Object)} method returns
     * {@code null} if the {@link Map} contains {@code null} or if there
     * is no matching key. The {@code null} placeholder can be used to
     * distinguish between these two cases.</p>
     *
     * <p>Another example is {@link Hashtable}, where {@code null}
     * cannot be stored.</p>
     *
     * <p>This instance is Serializable.</p>
     */
    public static final ObjectUtils.Null NULL = new ObjectUtils.Null();

    /**
     * Null safe comparison of Comparables.
     * <p>TODO Move to ComparableUtils.</p>
     *
     * @param <T>         type of the values processed by this method
     * @param c1          the first comparable, may be null
     * @param c2          the second comparable, may be null
     * @param nullGreater if true {@code null} is considered greater
     *                    than a non-{@code null} value or if false {@code null} is
     *                    considered less than a Non-{@code null} value
     * @return a negative value if c1 &lt; c2, zero if c1 = c2
     * and a positive value if c1 &gt; c2
     * @see java.util.Comparator#compare(Object, Object)
     */
    public static <T extends Comparable<? super T>> int compare(final T c1, final T c2, final boolean nullGreater) {
        if (c1 == c2) {
            return 0;
        }
        if (c1 == null) {
            return nullGreater ? 1 : -1;
        }
        if (c2 == null) {
            return nullGreater ? -1 : 1;
        }
        return c1.compareTo(c2);
    }

    /**
     * Delegates to {@link Object#getClass()} using generics.
     *
     * @param <T>    The argument type or null.
     * @param object The argument.
     * @return The argument's Class or null.
     * @since 0.3.1
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getClass(final T object) {
        return object == null ? null : (Class<T>) object.getClass();
    }

    /**
     * Returns the hex hash code for the given object per {@link System#identityHashCode(Object)}.
     * <p>
     * Short hand for {@code Integer.toHexString(System.identityHashCode(object))}.
     * </p>
     *
     * @param object object for which the hashCode is to be calculated
     * @return Hash code in hexadecimal format.
     * @since 0.3.1
     */
    public static String identityHashCodeHex(final Object object) {
        return Integer.toHexString(System.identityHashCode(object));
    }

    /**
     * Appends the toString that would be produced by {@link Object}
     * if a class did not override toString itself. {@code null}
     * will throw a NullPointerException for either of the two parameters.
     *
     * <pre>
     * ObjectUtils.identityToString(buf, "")            = buf.append("java.lang.String@1e23")
     * ObjectUtils.identityToString(buf, Boolean.TRUE)  = buf.append("java.lang.Boolean@7fa")
     * ObjectUtils.identityToString(buf, Boolean.TRUE)  = buf.append("java.lang.Boolean@7fa")
     * </pre>
     *
     * @param buffer the buffer to append to
     * @param object the object to create a toString for
     * @since 0.3.1
     */
    public static void identityToString(final StringBuffer buffer, final Object object) {
        Objects.requireNonNull(object, "object");
        final String name = object.getClass().getName();
        final String hexString = identityHashCodeHex(object);
        buffer.ensureCapacity(buffer.length() + name.length() + 1 + hexString.length());
        buffer.append(name)
                .append(AT_SIGN)
                .append(hexString);
    }

    /**
     * Tests whether the given object is an Object array or a primitive array in a null-safe manner.
     *
     * <p>
     * A {@code null} {@code object} Object will return {@code false}.
     * </p>
     *
     * <pre>
     * ObjectUtils.isArray(null)             = false
     * ObjectUtils.isArray("")               = false
     * ObjectUtils.isArray("ab")             = false
     * ObjectUtils.isArray(new int[]{})      = true
     * ObjectUtils.isArray(new int[]{1,2,3}) = true
     * ObjectUtils.isArray(1234)             = false
     * </pre>
     *
     * @param object the object to check, may be {@code null}
     * @return {@code true} if the object is an {@code array}, {@code false} otherwise
     * @since 0.3.1
     */
    public static boolean isArray(final Object object) {
        return object != null && object.getClass().isArray();
    }

    /**
     * Null safe comparison of Comparables.
     * <p>TODO Move to ComparableUtils.</p>
     *
     * @param <T>    type of the values processed by this method
     * @param values the set of comparable values, may be null
     * @return <ul>
     * <li>If any objects are non-null and unequal, the greater object.
     * <li>If all objects are non-null and equal, the first.
     * <li>If any of the comparables are null, the greater of the non-null objects.
     * <li>If all the comparables are null, null is returned.
     * </ul>
     */
    @SafeVarargs
    public static <T extends Comparable<? super T>> T max(final T... values) {
        T result = null;
        if (values != null) {
            for (final T value : values) {
                if (compare(value, result, false) > 0) {
                    result = value;
                }
            }
        }
        return result;
    }

    /**
     * {@link ObjectUtils} instances should NOT be constructed in
     * standard programming. Instead, the static methods on the class should
     * be used, such as {@code ObjectUtils.defaultIfNull("a","b");}.
     *
     * <p>This constructor is public to permit tools that require a JavaBean
     * instance to operate.</p>
     */
    public ObjectUtils() {
    }

}