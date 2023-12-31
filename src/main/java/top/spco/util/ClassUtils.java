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

import java.util.*;
import java.util.stream.Collectors;

/**
 * Operates on classes without using reflection.
 *
 * <p>
 * This class handles invalid {@code null} inputs as best it can. Each method documents its behavior in more detail.
 * </p>
 *
 * <p>
 * The notion of a {@code canonical name} includes the human readable name for the type, for example {@code int[]}. The
 * non-canonical method variants work with the JVM names, such as {@code [I}.
 * </p>
 *
 * @since 0.3.1
 */
public class ClassUtils {

    private static final Comparator<Class<?>> COMPARATOR = (o1, o2) -> Objects.compare(getName(o1), getName(o2), String::compareTo);

    /**
     * The package separator character: {@code '&#x2e;' == {@value}}.
     */
    public static final char PACKAGE_SEPARATOR_CHAR = '.';

    /**
     * The inner class separator character: {@code '$' == {@value}}.
     */
    public static final char INNER_CLASS_SEPARATOR_CHAR = '$';

    /**
     * Maps names of primitives to their corresponding primitive {@link Class}es.
     */
    private static final Map<String, Class<?>> namePrimitiveMap = new HashMap<>();

    static {
        namePrimitiveMap.put("boolean", Boolean.TYPE);
        namePrimitiveMap.put("byte", Byte.TYPE);
        namePrimitiveMap.put("char", Character.TYPE);
        namePrimitiveMap.put("short", Short.TYPE);
        namePrimitiveMap.put("int", Integer.TYPE);
        namePrimitiveMap.put("long", Long.TYPE);
        namePrimitiveMap.put("double", Double.TYPE);
        namePrimitiveMap.put("float", Float.TYPE);
        namePrimitiveMap.put("void", Void.TYPE);
    }

    /**
     * Maps primitive {@link Class}es to their corresponding wrapper {@link Class}.
     */
    private static final Map<Class<?>, Class<?>> primitiveWrapperMap = new HashMap<>();

    static {
        primitiveWrapperMap.put(Boolean.TYPE, Boolean.class);
        primitiveWrapperMap.put(Byte.TYPE, Byte.class);
        primitiveWrapperMap.put(Character.TYPE, Character.class);
        primitiveWrapperMap.put(Short.TYPE, Short.class);
        primitiveWrapperMap.put(Integer.TYPE, Integer.class);
        primitiveWrapperMap.put(Long.TYPE, Long.class);
        primitiveWrapperMap.put(Double.TYPE, Double.class);
        primitiveWrapperMap.put(Float.TYPE, Float.class);
        primitiveWrapperMap.put(Void.TYPE, Void.TYPE);
    }

    /**
     * Maps wrapper {@link Class}es to their corresponding primitive types.
     */
    private static final Map<Class<?>, Class<?>> wrapperPrimitiveMap = new HashMap<>();

    static {
        primitiveWrapperMap.forEach((primitiveClass, wrapperClass) -> {
            if (!primitiveClass.equals(wrapperClass)) {
                wrapperPrimitiveMap.put(wrapperClass, primitiveClass);
            }
        });
    }

    /**
     * Maps a primitive class name to its corresponding abbreviation used in array class names.
     */
    private static final Map<String, String> abbreviationMap;

    /**
     * Maps an abbreviation used in array class names to corresponding primitive class name.
     */
    private static final Map<String, String> reverseAbbreviationMap;

    /* Feed abbreviation maps. */
    static {
        final Map<String, String> map = new HashMap<>();
        map.put("int", "I");
        map.put("boolean", "Z");
        map.put("float", "F");
        map.put("long", "J");
        map.put("short", "S");
        map.put("byte", "B");
        map.put("double", "D");
        map.put("char", "C");
        abbreviationMap = Collections.unmodifiableMap(map);
        reverseAbbreviationMap = Collections.unmodifiableMap(map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey)));
    }

    /**
     * Gets the class comparator, comparing by class name.
     *
     * @return the class comparator.
     * @since 0.3.1
     */
    public static Comparator<Class<?>> comparator() {
        return COMPARATOR;
    }

    /**
     * Returns the (initialized) class represented by {@code className} using the {@code classLoader}. This implementation
     * supports the syntaxes "{@code java.util.Map.Entry[]}", "{@code java.util.Map$Entry[]}",
     * "{@code [Ljava.util.Map.Entry;}", and "{@code [Ljava.util.Map$Entry;}".
     *
     * @param classLoader the class loader to use to load the class
     * @param className   the class name
     * @return the class represented by {@code className} using the {@code classLoader}
     * @throws NullPointerException   if the className is null
     * @throws ClassNotFoundException if the class is not found
     */
    public static Class<?> getClass(final ClassLoader classLoader, final String className) throws ClassNotFoundException {
        return getClass(classLoader, className, true);
    }

    /**
     * Returns the class represented by {@code className} using the {@code classLoader}. This implementation supports the
     * syntaxes "{@code java.util.Map.Entry[]}", "{@code java.util.Map$Entry[]}", "{@code [Ljava.util.Map.Entry;}", and
     * "{@code [Ljava.util.Map$Entry;}".
     *
     * @param classLoader the class loader to use to load the class
     * @param className   the class name
     * @param initialize  whether the class must be initialized
     * @return the class represented by {@code className} using the {@code classLoader}
     * @throws NullPointerException   if the className is null
     * @throws ClassNotFoundException if the class is not found
     */
    public static Class<?> getClass(final ClassLoader classLoader, final String className, final boolean initialize) throws ClassNotFoundException {
        try {
            Class<?> clazz = namePrimitiveMap.get(className);
            return clazz != null ? clazz : Class.forName(toCanonicalName(className), initialize, classLoader);
        } catch (final ClassNotFoundException ex) {
            // allow path separators (.) as inner class name separators
            final int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR_CHAR);

            if (lastDotIndex != -1) {
                try {
                    return getClass(classLoader, className.substring(0, lastDotIndex) + INNER_CLASS_SEPARATOR_CHAR + className.substring(lastDotIndex + 1),
                            initialize);
                } catch (final ClassNotFoundException ignored) {
                    // ignore exception
                }
            }

            throw ex;
        }
    }

    /**
     * Returns the (initialized) class represented by {@code className} using the current thread's context class loader.
     * This implementation supports the syntaxes "{@code java.util.Map.Entry[]}", "{@code java.util.Map$Entry[]}",
     * "{@code [Ljava.util.Map.Entry;}", and "{@code [Ljava.util.Map$Entry;}".
     *
     * @param className the class name
     * @return the class represented by {@code className} using the current thread's context class loader
     * @throws NullPointerException   if the className is null
     * @throws ClassNotFoundException if the class is not found
     */
    public static Class<?> getClass(final String className) throws ClassNotFoundException {
        return getClass(className, true);
    }

    /**
     * Returns the class represented by {@code className} using the current thread's context class loader. This
     * implementation supports the syntaxes "{@code java.util.Map.Entry[]}", "{@code java.util.Map$Entry[]}",
     * "{@code [Ljava.util.Map.Entry;}", and "{@code [Ljava.util.Map$Entry;}".
     *
     * @param className  the class name
     * @param initialize whether the class must be initialized
     * @return the class represented by {@code className} using the current thread's context class loader
     * @throws NullPointerException   if the className is null
     * @throws ClassNotFoundException if the class is not found
     */
    public static Class<?> getClass(final String className, final boolean initialize) throws ClassNotFoundException {
        final ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
        final ClassLoader loader = contextCL == null ? ClassUtils.class.getClassLoader() : contextCL;
        return getClass(loader, className, initialize);
    }

    /**
     * Delegates to {@link Class#getComponentType()} using generics.
     *
     * @param <T> The array class type.
     * @param cls A class or null.
     * @return The array component type or null.
     * @see Class#getComponentType()
     * @since 0.3.1
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getComponentType(final Class<T[]> cls) {
        return cls == null ? null : (Class<T>) cls.getComponentType();
    }

    /**
     * Null-safe version of {@code cls.getName()}
     *
     * @param cls the class for which to get the class name; may be null
     * @return the class name or the empty string in case the argument is {@code null}
     * @see Class#getSimpleName()
     * @since 0.3.1
     */
    public static String getName(final Class<?> cls) {
        return getName(cls, StringUtils.EMPTY);
    }

    /**
     * Null-safe version of {@code cls.getName()}
     *
     * @param cls         the class for which to get the class name; may be null
     * @param valueIfNull the return value if the argument {@code cls} is {@code null}
     * @return the class name or {@code valueIfNull}
     * @see Class#getName()
     * @since 0.3.1
     */
    public static String getName(final Class<?> cls, final String valueIfNull) {
        return cls == null ? valueIfNull : cls.getName();
    }

    /**
     * Null-safe version of {@code object.getClass().getName()}
     *
     * @param object the object for which to get the class name; may be null
     * @return the class name or the empty String
     * @see Class#getSimpleName()
     * @since 0.3.1
     */
    public static String getName(final Object object) {
        return getName(object, StringUtils.EMPTY);
    }

    /**
     * Null-safe version of {@code object.getClass().getSimpleName()}
     *
     * @param object      the object for which to get the class name; may be null
     * @param valueIfNull the value to return if {@code object} is {@code null}
     * @return the class name or {@code valueIfNull}
     * @see Class#getName()
     * @since 0.3.1
     */
    public static String getName(final Object object, final String valueIfNull) {
        return object == null ? valueIfNull : object.getClass().getName();
    }

    /**
     * Gets the class name minus the package name from a {@link Class}.
     *
     * <p>
     * This method simply gets the name using {@code Class.getName()} and then calls {@link #getShortClassName(Class)}. See
     * relevant notes there.
     * </p>
     *
     * @param cls the class to get the short name for.
     * @return the class name without the package name or an empty string. If the class is an inner class then the returned
     * value will contain the outer class or classes separated with {@code .} (dot) character.
     */
    public static String getShortClassName(final Class<?> cls) {
        if (cls == null) {
            return StringUtils.EMPTY;
        }
        return getShortClassName(cls.getName());
    }

    public static String getShortClassName(String className) {
        if (StringUtils.isEmpty(className)) {
            return StringUtils.EMPTY;
        }

        final StringBuilder arrayPrefix = new StringBuilder();

        // Handle array encoding
        if (className.startsWith("[")) {
            while (className.charAt(0) == '[') {
                className = className.substring(1);
                arrayPrefix.append("[]");
            }
            // Strip Object type encoding
            if (className.charAt(0) == 'L' && className.charAt(className.length() - 1) == ';') {
                className = className.substring(1, className.length() - 1);
            }

            if (reverseAbbreviationMap.containsKey(className)) {
                className = reverseAbbreviationMap.get(className);
            }
        }

        final int lastDotIdx = className.lastIndexOf(PACKAGE_SEPARATOR_CHAR);
        final int innerIdx = className.indexOf(INNER_CLASS_SEPARATOR_CHAR, lastDotIdx == -1 ? 0 : lastDotIdx + 1);
        String out = className.substring(lastDotIdx + 1);
        if (innerIdx != -1) {
            out = out.replace(INNER_CLASS_SEPARATOR_CHAR, PACKAGE_SEPARATOR_CHAR);
        }
        return out + arrayPrefix;
    }

    /**
     * Returns whether the given {@code type} is a primitive or primitive wrapper ({@link Boolean}, {@link Byte},
     * {@link Character}, {@link Short}, {@link Integer}, {@link Long}, {@link Double}, {@link Float}).
     *
     * @param type The class to query or null.
     * @return true if the given {@code type} is a primitive or primitive wrapper ({@link Boolean}, {@link Byte},
     * {@link Character}, {@link Short}, {@link Integer}, {@link Long}, {@link Double}, {@link Float}).
     * @since 0.3.1
     */
    public static boolean isPrimitiveOrWrapper(final Class<?> type) {
        if (type == null) {
            return false;
        }
        return type.isPrimitive() || isPrimitiveWrapper(type);
    }

    /**
     * Returns whether the given {@code type} is a primitive wrapper ({@link Boolean}, {@link Byte}, {@link Character},
     * {@link Short}, {@link Integer}, {@link Long}, {@link Double}, {@link Float}).
     *
     * @param type The class to query or null.
     * @return true if the given {@code type} is a primitive wrapper ({@link Boolean}, {@link Byte}, {@link Character},
     * {@link Short}, {@link Integer}, {@link Long}, {@link Double}, {@link Float}).
     * @since 0.3.1
     */
    public static boolean isPrimitiveWrapper(final Class<?> type) {
        return wrapperPrimitiveMap.containsKey(type);
    }


    /**
     * Converts a class name to a JLS style class name.
     *
     * @param className the class name
     * @return the converted name
     * @throws NullPointerException if the className is null
     */
    private static String toCanonicalName(final String className) {
        String canonicalName = StringUtils.deleteWhitespace(className);
        Objects.requireNonNull(canonicalName, "className");
        if (canonicalName.endsWith("[]")) {
            final StringBuilder classNameBuffer = new StringBuilder();
            while (canonicalName.endsWith("[]")) {
                canonicalName = canonicalName.substring(0, canonicalName.length() - 2);
                classNameBuffer.append("[");
            }
            final String abbreviation = abbreviationMap.get(canonicalName);
            if (abbreviation != null) {
                classNameBuffer.append(abbreviation);
            } else {
                classNameBuffer.append("L").append(canonicalName).append(";");
            }
            canonicalName = classNameBuffer.toString();
        }
        return canonicalName;
    }


    /**
     * ClassUtils instances should NOT be constructed in standard programming. Instead, the class should be used as
     * {@code ClassUtils.getShortClassName(cls)}.
     *
     * <p>
     * This constructor is public to permit tools that require a JavaBean instance to operate.
     * </p>
     */
    public ClassUtils() {
    }

}