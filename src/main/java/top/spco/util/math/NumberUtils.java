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
package top.spco.util.math;

/**
 * Provides extra functionality for Java Number classes.
 *
 * @since 0.3.1
 */
public class NumberUtils {
    /**
     * Reusable Integer constant for zero.
     */
    public static final Integer INTEGER_ZERO = Integer.valueOf(0);
    /**
     * Reusable Integer constant for one.
     */
    public static final Integer INTEGER_ONE = Integer.valueOf(1);

    /**
     * {@link Integer#MAX_VALUE} as a {@link Long}.
     *
     * @since 0.3.1
     */
    public static final Long LONG_INT_MAX_VALUE = Long.valueOf(Integer.MAX_VALUE);

    /**
     * {@link Integer#MIN_VALUE} as a {@link Long}.
     *
     * @since 0.3.1
     */
    public static final Long LONG_INT_MIN_VALUE = Long.valueOf(Integer.MIN_VALUE);


    /**
     * Compares two {@code int} values numerically. This is the same functionality as provided in Java 7.
     *
     * @param x the first {@code int} to compare
     * @param y the second {@code int} to compare
     * @return the value {@code 0} if {@code x == y};
     * a value less than {@code 0} if {@code x < y}; and
     * a value greater than {@code 0} if {@code x > y}
     * @since 0.3.1
     */
    public static int compare(final int x, final int y) {
        if (x == y) {
            return 0;
        }
        return x < y ? -1 : 1;
    }

    /**
     * Compares to {@code long} values numerically. This is the same functionality as provided in Java 7.
     *
     * @param x the first {@code long} to compare
     * @param y the second {@code long} to compare
     * @return the value {@code 0} if {@code x == y};
     * a value less than {@code 0} if {@code x < y}; and
     * a value greater than {@code 0} if {@code x > y}
     * @since 0.3.1
     */
    public static int compare(final long x, final long y) {
        if (x == y) {
            return 0;
        }
        return x < y ? -1 : 1;
    }

    /**
     * Compares to {@code short} values numerically. This is the same functionality as provided in Java 7.
     *
     * @param x the first {@code short} to compare
     * @param y the second {@code short} to compare
     * @return the value {@code 0} if {@code x == y};
     * a value less than {@code 0} if {@code x < y}; and
     * a value greater than {@code 0} if {@code x > y}
     * @since 0.3.1
     */
    public static int compare(final short x, final short y) {
        if (x == y) {
            return 0;
        }
        return x < y ? -1 : 1;
    }

    /**
     * Compares two {@code byte} values numerically. This is the same functionality as provided in Java 7.
     *
     * @param x the first {@code byte} to compare
     * @param y the second {@code byte} to compare
     * @return the value {@code 0} if {@code x == y};
     * a value less than {@code 0} if {@code x < y}; and
     * a value greater than {@code 0} if {@code x > y}
     * @since 0.3.1
     */
    public static int compare(final byte x, final byte y) {
        return x - y;
    }
}