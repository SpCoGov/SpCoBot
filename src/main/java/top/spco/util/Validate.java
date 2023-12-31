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

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * This class assists in validating arguments. The validation methods are
 * based along the following principles:
 * <ul>
 *   <li>An invalid {@code null} argument causes a {@link NullPointerException}.</li>
 *   <li>A non-{@code null} argument causes an {@link IllegalArgumentException}.</li>
 *   <li>An invalid index into an array/collection/map/string causes an {@link IndexOutOfBoundsException}.</li>
 * </ul>
 *
 * <p>All exceptions messages are
 * <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html#syntax">format strings</a>
 * as defined by the Java platform. For example:
 *
 * <pre>
 * Validate.isTrue(i &gt; 0, "The value must be greater than zero: %d", i);
 * Validate.notNull(surname, "The surname must not be %s", null);
 * </pre>
 *
 * <p>#ThreadSafe#</p>
 *
 * @see String#format(String, Object...)
 * @since 0.3.1
 */
public class Validate {
    public static void isTrue(final boolean expression, final String message, final Object... values) {
        if (!expression) {
            throw new IllegalArgumentException(getMessage(message, values));
        }
    }

    /**
     * Gets the message using {@link String#format(String, Object...) String.format(message, values)}
     * if the values are not empty, otherwise return the message unformatted.
     * This method exists to allow validation methods declaring a String message and varargs parameters
     * to be used without any message parameters when the message contains special characters,
     * e.g. {@code Validate.isTrue(false, "%Failed%")}.
     *
     * @param message the {@link String#format(String, Object...)} exception message if invalid, not null
     * @param values  the optional values for the formatted message
     * @return formatted message using {@link String#format(String, Object...) String.format(message, values)}
     * if the values are not empty, otherwise return the unformatted message.
     */
    private static String getMessage(final String message, final Object... values) {
        return ArrayUtils.isEmpty(values) ? message : String.format(message, values);
    }
}