/*
 * Copyright 2025 SpCo
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
package top.spco.util;

import java.util.HashSet;
import java.util.Set;

/**
 * 用于序列化和反序列化的工具类。
 *
 * @author SpCo
 * @version 4.0.0
 * @since 4.0.0
 */
public class SerializationUtil {
    private static final String DEFAULT_DELIMITER = ",";

    /**
     * 序列化 {@code Set} 为字符串。
     *
     * @param set       要序列化的 Set
     * @param delimiter 分隔符
     * @param <T>       Set 元素的类型
     * @return 序列化后的字符串
     */
    public static <T> String serializeWithDelimiter(Set<T> set, String delimiter) {
        if (set == null || set.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (T item : set) {
            result.append(item).append(delimiter);
        }
        result.setLength(result.length() - delimiter.length());
        return result.toString();
    }

    /**
     * 反序列化字符串为 {@code Set}。
     *
     * @param serialized 序列化后的字符串
     * @param delimiter  分隔符
     * @param parser     元素解析器（如 {@code Integer::valueOf}）
     * @param <T>        Set 元素的类型
     * @return 反序列化后的 Set
     */
    public static <T> Set<T> deserializeWithDelimiter(String serialized, String delimiter, Parser<T> parser) {
        if (serialized == null || serialized.isEmpty()) {
            return new HashSet<>();
        }
        Set<T> result = new HashSet<>();
        String[] items = serialized.split(delimiter);
        for (String item : items) {
            result.add(parser.parse(item));
        }
        return result;
    }

    /**
     * 使用默认分隔符序列化 {@code Set<String>}。
     */
    public static String serialize(Set<String> set) {
        return serializeWithDelimiter(set, DEFAULT_DELIMITER);
    }

    /**
     * 使用默认分隔符反序列化为 {@code Set<String>}。
     */
    public static Set<String> deserialize(String serialized) {
        return deserializeWithDelimiter(serialized, DEFAULT_DELIMITER, s -> s);
    }

    /**
     * 序列化 {@code Set<Integer>}（默认分隔符）。
     */
    public static String serializeIntegerSet(Set<Integer> set) {
        return serializeWithDelimiter(set, DEFAULT_DELIMITER);
    }

    /**
     * 反序列化为 {@code Set<Integer>}（默认分隔符）。
     */
    public static Set<Integer> deserializeIntegerSet(String serialized) {
        return deserializeWithDelimiter(serialized, DEFAULT_DELIMITER, Integer::valueOf);
    }

    /**
     * 序列化 {@code Set<Long>}（默认分隔符）。
     */
    public static String serializeLongSet(Set<Long> set) {
        return serializeWithDelimiter(set, DEFAULT_DELIMITER);
    }

    /**
     * 反序列化为 {@code Set<Long>}（默认分隔符）。
     */
    public static Set<Long> deserializeLongSet(String serialized) {
        return deserializeWithDelimiter(serialized, DEFAULT_DELIMITER, Long::valueOf);
    }

    @FunctionalInterface
    public interface Parser<T> {
        T parse(String input);
    }
}
