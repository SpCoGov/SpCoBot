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

/**
 * Operations on char primitives and Character objects.
 *
 * <p>This class tries to handle {@code null} input gracefully.
 * An exception will not be thrown for a {@code null} input.
 * Each method documents its behavior in more detail.</p>
 *
 * <p>#ThreadSafe#</p>
 *
 * @since 0.3.1
 */
public class CharUtils {
    private static final String[] CHAR_STRING_ARRAY = new String[128];

    /**
     * Linefeed character LF ({@code '\n'}, Unicode 000a).
     *
     * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-3.10.6">JLF: Escape Sequences
     * for Character and String Literals</a>
     * @since 0.3.1
     */
    public static final char LF = '\n';

    /**
     * Carriage return character CR ('\r', Unicode 000d).
     *
     * @see <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-3.10.6">JLF: Escape Sequences
     * for Character and String Literals</a>
     * @since 0.3.1
     */
    public static final char CR = '\r';

    static {
        ArrayUtils.setAll(CHAR_STRING_ARRAY, i -> String.valueOf((char) i));
    }
}