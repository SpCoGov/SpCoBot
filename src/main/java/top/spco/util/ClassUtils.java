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

import top.spco.util.mutable.MutableObject;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
    /**
     * Inclusivity literals for {@link #hierarchy(Class, ClassUtils.Interfaces)}.
     *
     * @since 0.3.1
     */
    public enum Interfaces {

        /**
         * Includes interfaces.
         */
        INCLUDE,

        /**
         * Excludes interfaces.
         */
        EXCLUDE
    }

    private static final Comparator<Class<?>> COMPARATOR = (o1, o2) -> Objects.compare(getName(o1), getName(o2), String::compareTo);

    /**
     * The package separator character: {@code '&#x2e;' == {@value}}.
     */
    public static final char PACKAGE_SEPARATOR_CHAR = '.';

    /**
     * The package separator String: {@code "&#x2e;"}.
     */
    public static final String PACKAGE_SEPARATOR = String.valueOf(PACKAGE_SEPARATOR_CHAR);

    /**
     * The inner class separator character: {@code '$' == {@value}}.
     */
    public static final char INNER_CLASS_SEPARATOR_CHAR = '$';

    /**
     * The inner class separator String: {@code "$"}.
     */
    public static final String INNER_CLASS_SEPARATOR = String.valueOf(INNER_CLASS_SEPARATOR_CHAR);

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
     * Given a {@link List} of {@link Class} objects, this method converts them into class names.
     *
     * <p>
     * A new {@link List} is returned. {@code null} objects will be copied into the returned list as {@code null}.
     * </p>
     *
     * @param classes the classes to change
     * @return a {@link List} of class names corresponding to the Class objects, {@code null} if null input
     * @throws ClassCastException if {@code classes} contains a non-{@link Class} entry
     */
    public static List<String> convertClassesToClassNames(final List<Class<?>> classes) {
        return classes == null ? null : classes.stream().map(e -> getName(e, null)).collect(Collectors.toList());
    }

    /**
     * Given a {@link List} of class names, this method converts them into classes.
     *
     * <p>
     * A new {@link List} is returned. If the class name cannot be found, {@code null} is stored in the {@link List}. If the
     * class name in the {@link List} is {@code null}, {@code null} is stored in the output {@link List}.
     * </p>
     *
     * @param classNames the classNames to change
     * @return a {@link List} of Class objects corresponding to the class names, {@code null} if null input
     * @throws ClassCastException if classNames contains a non String entry
     */
    public static List<Class<?>> convertClassNamesToClasses(final List<String> classNames) {
        if (classNames == null) {
            return null;
        }
        final List<Class<?>> classes = new ArrayList<>(classNames.size());
        classNames.forEach(className -> {
            try {
                classes.add(Class.forName(className));
            } catch (final Exception ex) {
                classes.add(null);
            }
        });
        return classes;
    }

    /**
     * Gets the abbreviated name of a {@link Class}.
     *
     * @param cls        the class to get the abbreviated name for, may be {@code null}
     * @param lengthHint the desired length of the abbreviated name
     * @return the abbreviated name or an empty string
     * @throws IllegalArgumentException if len &lt;= 0
     * @see #getAbbreviatedName(String, int)
     * @since 0.3.1
     */
    public static String getAbbreviatedName(final Class<?> cls, final int lengthHint) {
        if (cls == null) {
            return StringUtils.EMPTY;
        }
        return getAbbreviatedName(cls.getName(), lengthHint);
    }

    /**
     * Gets the abbreviated class name from a {@link String}.
     *
     * <p>
     * The string passed in is assumed to be a class name - it is not checked.
     * </p>
     *
     * <p>
     * The abbreviation algorithm will shorten the class name, usually without significant loss of meaning.
     * </p>
     *
     * <p>
     * The abbreviated class name will always include the complete package hierarchy. If enough space is available,
     * rightmost sub-packages will be displayed in full length. The abbreviated package names will be shortened to a single
     * character.
     * </p>
     * <p>
     * Only package names are shortened, the class simple name remains untouched. (See examples.)
     * </p>
     * <p>
     * The result will be longer than the desired length only if all the package names shortened to a single character plus
     * the class simple name with the separating dots together are longer than the desired length. In other words, when the
     * class name cannot be shortened to the desired length.
     * </p>
     * <p>
     * If the class name can be shortened then the final length will be at most {@code lengthHint} characters.
     * </p>
     * <p>
     * If the {@code lengthHint} is zero or negative then the method throws exception. If you want to achieve the shortest
     * possible version then use {@code 1} as a {@code lengthHint}.
     * </p>
     *
     * <table>
     * <caption>Examples</caption>
     * <tr>
     * <td>className</td>
     * <td>len</td>
     * <td>return</td>
     * </tr>
     * <tr>
     * <td>null</td>
     * <td>1</td>
     * <td>""</td>
     * </tr>
     * <tr>
     * <td>"java.lang.String"</td>
     * <td>5</td>
     * <td>"j.l.String"</td>
     * </tr>
     * <tr>
     * <td>"java.lang.String"</td>
     * <td>15</td>
     * <td>"j.lang.String"</td>
     * </tr>
     * <tr>
     * <td>"java.lang.String"</td>
     * <td>30</td>
     * <td>"java.lang.String"</td>
     * </tr>
     * <tr>
     * <td>"ClassUtils"</td>
     * <td>18</td>
     * <td>"o.a.c.l.ClassUtils"</td>
     * </tr>
     * </table>
     *
     * @param className  the className to get the abbreviated name for, may be {@code null}
     * @param lengthHint the desired length of the abbreviated name
     * @return the abbreviated name or an empty string if the specified class name is {@code null} or empty string. The
     * abbreviated name may be longer than the desired length if it cannot be abbreviated to the desired length.
     * @throws IllegalArgumentException if {@code len <= 0}
     * @since 0.3.1
     */
    public static String getAbbreviatedName(final String className, final int lengthHint) {
        if (lengthHint <= 0) {
            throw new IllegalArgumentException("len must be > 0");
        }
        if (className == null) {
            return StringUtils.EMPTY;
        }
        if (className.length() <= lengthHint) {
            return className;
        }
        final char[] abbreviated = className.toCharArray();
        int target = 0;
        int source = 0;
        while (source < abbreviated.length) {
            // copy the next part
            int runAheadTarget = target;
            while (source < abbreviated.length && abbreviated[source] != '.') {
                abbreviated[runAheadTarget++] = abbreviated[source++];
            }

            ++target;
            if (useFull(runAheadTarget, source, abbreviated.length, lengthHint) || target > runAheadTarget) {
                target = runAheadTarget;
            }

            // copy the '.' unless it was the last part
            if (source < abbreviated.length) {
                abbreviated[target++] = abbreviated[source++];
            }
        }
        return new String(abbreviated, 0, target);
    }

    /**
     * Gets a {@link List} of all interfaces implemented by the given class and its superclasses.
     *
     * <p>
     * The order is determined by looking through each interface in turn as declared in the source file and following its
     * hierarchy up. Then each superclass is considered in the same way. Later duplicates are ignored, so the order is
     * maintained.
     * </p>
     *
     * @param cls the class to look up, may be {@code null}
     * @return the {@link List} of interfaces in order, {@code null} if null input
     */
    public static List<Class<?>> getAllInterfaces(final Class<?> cls) {
        if (cls == null) {
            return null;
        }

        final LinkedHashSet<Class<?>> interfacesFound = new LinkedHashSet<>();
        getAllInterfaces(cls, interfacesFound);

        return new ArrayList<>(interfacesFound);
    }

    /**
     * Gets the interfaces for the specified class.
     *
     * @param cls             the class to look up, may be {@code null}
     * @param interfacesFound the {@link Set} of interfaces for the class
     */
    private static void getAllInterfaces(Class<?> cls, final HashSet<Class<?>> interfacesFound) {
        while (cls != null) {
            final Class<?>[] interfaces = cls.getInterfaces();

            for (final Class<?> i : interfaces) {
                if (interfacesFound.add(i)) {
                    getAllInterfaces(i, interfacesFound);
                }
            }

            cls = cls.getSuperclass();
        }
    }

    /**
     * Gets a {@link List} of superclasses for the given class.
     *
     * @param cls the class to look up, may be {@code null}
     * @return the {@link List} of superclasses in order going up from this one {@code null} if null input
     */
    public static List<Class<?>> getAllSuperclasses(final Class<?> cls) {
        if (cls == null) {
            return null;
        }
        final List<Class<?>> classes = new ArrayList<>();
        Class<?> superclass = cls.getSuperclass();
        while (superclass != null) {
            classes.add(superclass);
            superclass = superclass.getSuperclass();
        }
        return classes;
    }

    /**
     * Gets the canonical class name for a {@link Class}.
     *
     * @param cls the class for which to get the canonical class name; may be null
     * @return the canonical name of the class, or the empty String
     * @see Class#getCanonicalName()
     * @since 0.3.1
     */
    public static String getCanonicalName(final Class<?> cls) {
        return getCanonicalName(cls, StringUtils.EMPTY);
    }

    /**
     * Gets the canonical name for a {@link Class}.
     *
     * @param cls         the class for which to get the canonical class name; may be null
     * @param valueIfNull the return value if null
     * @return the canonical name of the class, or {@code valueIfNull}
     * @see Class#getCanonicalName()
     * @since 0.3.1
     */
    public static String getCanonicalName(final Class<?> cls, final String valueIfNull) {
        if (cls == null) {
            return valueIfNull;
        }
        final String canonicalName = cls.getCanonicalName();
        return canonicalName == null ? valueIfNull : canonicalName;
    }

    /**
     * Gets the canonical name for an {@link Object}.
     *
     * @param object the object for which to get the canonical class name; may be null
     * @return the canonical name of the object, or the empty String
     * @see Class#getCanonicalName()
     * @since 0.3.1
     */
    public static String getCanonicalName(final Object object) {
        return getCanonicalName(object, StringUtils.EMPTY);
    }

    /**
     * Gets the canonical name for an {@link Object}.
     *
     * @param object      the object for which to get the canonical class name; may be null
     * @param valueIfNull the return value if null
     * @return the canonical name of the object or {@code valueIfNull}
     * @see Class#getCanonicalName()
     * @since 0.3.1
     */
    public static String getCanonicalName(final Object object, final String valueIfNull) {
        if (object == null) {
            return valueIfNull;
        }
        final String canonicalName = object.getClass().getCanonicalName();
        return canonicalName == null ? valueIfNull : canonicalName;
    }

    /**
     * Converts a given name of class into canonical format. If name of class is not a name of array class it returns
     * unchanged name.
     *
     * <p>
     * The method does not change the {@code $} separators in case the class is inner class.
     * </p>
     *
     * <p>
     * Example:
     * <ul>
     * <li>{@code getCanonicalName("[I") = "int[]"}</li>
     * <li>{@code getCanonicalName("[Ljava.lang.String;") = "java.lang.String[]"}</li>
     * <li>{@code getCanonicalName("java.lang.String") = "java.lang.String"}</li>
     * </ul>
     * </p>
     *
     * @param className the name of class
     * @return canonical form of class name
     * @since 0.3.1
     */
    private static String getCanonicalName(String className) {
        className = StringUtils.deleteWhitespace(className);
        if (className == null) {
            return null;
        }
        int dim = 0;
        while (className.startsWith("[")) {
            dim++;
            className = className.substring(1);
        }
        if (dim < 1) {
            return className;
        }
        if (className.startsWith("L")) {
            className = className.substring(1, className.endsWith(";") ? className.length() - 1 : className.length());
        } else if (!className.isEmpty()) {
            className = reverseAbbreviationMap.get(className.substring(0, 1));
        }
        final StringBuilder canonicalClassNameBuffer = new StringBuilder(className);
        for (int i = 0; i < dim; i++) {
            canonicalClassNameBuffer.append("[]");
        }
        return canonicalClassNameBuffer.toString();
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
     * Gets an {@link Iterable} that can iterate over a class hierarchy in ascending (subclass to superclass) order.
     *
     * @param type               the type to get the class hierarchy from
     * @param interfacesBehavior switch indicating whether to include or exclude interfaces
     * @return Iterable an Iterable over the class hierarchy of the given class
     * @since 0.3.1
     */
    public static Iterable<Class<?>> hierarchy(final Class<?> type, final ClassUtils.Interfaces interfacesBehavior) {
        final Iterable<Class<?>> classes = () -> {
            final MutableObject<Class<?>> next = new MutableObject<>(type);
            return new Iterator<Class<?>>() {

                @Override
                public boolean hasNext() {
                    return next.getValue() != null;
                }

                @Override
                public Class<?> next() {
                    final Class<?> result = next.getValue();
                    next.setValue(result.getSuperclass());
                    return result;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }

            };
        };
        if (interfacesBehavior != ClassUtils.Interfaces.INCLUDE) {
            return classes;
        }
        return () -> {
            final Set<Class<?>> seenInterfaces = new HashSet<>();
            final Iterator<Class<?>> wrapped = classes.iterator();

            return new Iterator<>() {
                Iterator<Class<?>> interfaces = Collections.emptyIterator();

                @Override
                public boolean hasNext() {
                    return interfaces.hasNext() || wrapped.hasNext();
                }

                @Override
                public Class<?> next() {
                    if (interfaces.hasNext()) {
                        final Class<?> nextInterface = interfaces.next();
                        seenInterfaces.add(nextInterface);
                        return nextInterface;
                    }
                    final Class<?> nextSuperclass = wrapped.next();
                    final Set<Class<?>> currentInterfaces = new LinkedHashSet<>();
                    walkInterfaces(currentInterfaces, nextSuperclass);
                    interfaces = currentInterfaces.iterator();
                    return nextSuperclass;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }

                private void walkInterfaces(final Set<Class<?>> addTo, final Class<?> c) {
                    for (final Class<?> iface : c.getInterfaces()) {
                        if (!seenInterfaces.contains(iface)) {
                            addTo.add(iface);
                        }
                        walkInterfaces(addTo, iface);
                    }
                }

            };
        };
    }

    /**
     * Tests whether a {@link Class} is public.
     *
     * @param cls Class to test.
     * @return {@code true} if {@code cls} is public.
     * @since 0.3.1
     */
    public static boolean isPublic(final Class<?> cls) {
        return Modifier.isPublic(cls.getModifiers());
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
     * Converts the specified array of primitive Class objects to an array of its corresponding wrapper Class objects.
     *
     * @param classes the class array to convert, may be null or empty
     * @return an array which contains for each given class, the wrapper class or the original class if class is not a
     * primitive. {@code null} if null input. Empty array if an empty array passed in.
     * @since 0.3.1
     */
    public static Class<?>[] primitivesToWrappers(final Class<?>... classes) {
        if (classes == null) {
            return null;
        }

        if (classes.length == 0) {
            return classes;
        }

        final Class<?>[] convertedClasses = new Class[classes.length];
        Arrays.setAll(convertedClasses, i -> primitiveToWrapper(classes[i]));
        return convertedClasses;
    }

    /**
     * Converts the specified primitive Class object to its corresponding wrapper Class object.
     *
     * <p>
     * NOTE: From v2.2, this method handles {@code Void.TYPE}, returning {@code Void.TYPE}.
     * </p>
     *
     * @param cls the class to convert, may be null
     * @return the wrapper class for {@code cls} or {@code cls} if {@code cls} is not a primitive. {@code null} if null
     * input.
     * @since 0.3.1
     */
    public static Class<?> primitiveToWrapper(final Class<?> cls) {
        Class<?> convertedClass = cls;
        if (cls != null && cls.isPrimitive()) {
            convertedClass = primitiveWrapperMap.get(cls);
        }
        return convertedClass;
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
     * Converts an array of {@link Object} in to an array of {@link Class} objects. If any of these objects is null, a null
     * element will be inserted into the array.
     *
     * <p>
     * This method returns {@code null} for a {@code null} input array.
     * </p>
     *
     * @param array an {@link Object} array
     * @return a {@link Class} array, {@code null} if null array input
     * @since 0.3.1
     */
    public static Class<?>[] toClass(final Object... array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        final Class<?>[] classes = new Class[array.length];
        Arrays.setAll(classes, i -> array[i] == null ? null : array[i].getClass());
        return classes;
    }

    /**
     * Decides if the part that was just copied to its destination location in the work array can be kept as it was copied
     * or must be abbreviated. It must be kept when the part is the last one, which is the simple name of the class. In this
     * case the {@code source} index, from where the characters are copied points one position after the last character,
     * a.k.a. {@code source ==
     * originalLength}
     *
     * <p>
     * If the part is not the last one then it can be kept unabridged if the number of the characters copied so far plus the
     * character that are to be copied is less than or equal to the desired length.
     * </p>
     *
     * @param runAheadTarget the target index (where the characters were copied to) pointing after the last character copied
     *                       when the current part was copied
     * @param source         the source index (where the characters were copied from) pointing after the last character copied when
     *                       the current part was copied
     * @param originalLength the original length of the class full name, which is abbreviated
     * @param desiredLength  the desired length of the abbreviated class name
     * @return {@code true} if it can be kept in its original length {@code false} if the current part has to be abbreviated
     * and
     */
    private static boolean useFull(final int runAheadTarget, final int source, final int originalLength, final int desiredLength) {
        return source >= originalLength || runAheadTarget + originalLength - source <= desiredLength;
    }

    /**
     * Converts the specified array of wrapper Class objects to an array of its corresponding primitive Class objects.
     *
     * <p>
     * This method invokes {@code wrapperToPrimitive()} for each element of the passed in array.
     * </p>
     *
     * @param classes the class array to convert, may be null or empty
     * @return an array which contains for each given class, the primitive class or <b>null</b> if the original class is not
     * a wrapper class. {@code null} if null input. Empty array if an empty array passed in.
     * @see #wrapperToPrimitive(Class)
     * @since 0.3.1
     */
    public static Class<?>[] wrappersToPrimitives(final Class<?>... classes) {
        if (classes == null) {
            return null;
        }

        if (classes.length == 0) {
            return classes;
        }

        final Class<?>[] convertedClasses = new Class[classes.length];
        Arrays.setAll(convertedClasses, i -> wrapperToPrimitive(classes[i]));
        return convertedClasses;
    }

    /**
     * Converts the specified wrapper class to its corresponding primitive class.
     *
     * <p>
     * This method is the counter part of {@code primitiveToWrapper()}. If the passed in class is a wrapper class for a
     * primitive type, this primitive type will be returned (e.g. {@code Integer.TYPE} for {@code Integer.class}). For other
     * classes, or if the parameter is <b>null</b>, the return value is <b>null</b>.
     * </p>
     *
     * @param cls the class to convert, may be <b>null</b>
     * @return the corresponding primitive type if {@code cls} is a wrapper class, <b>null</b> otherwise
     * @see #primitiveToWrapper(Class)
     * @since 0.3.1
     */
    public static Class<?> wrapperToPrimitive(final Class<?> cls) {
        return wrapperPrimitiveMap.get(cls);
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