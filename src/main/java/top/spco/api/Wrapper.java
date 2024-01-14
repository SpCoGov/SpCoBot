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
package top.spco.api;

/**
 * 这个抽象类提供了一个泛型封装器，用于封装任意类型的对象。它不仅允许在构造时封装一个对象，
 * 还允许在对象的生命周期中更改被封装的对象。这种灵活性使得 Wrapper 类可以在各种场景下使用，
 * 如在不同上下文中重用对象，或在运行时更改对象的行为。
 *
 * <p>使用示例：</p>
 * <pre>
 * {@code
 *  // 创建一个包含字符串的Wrapper对象
 *  Wrapper<String> stringWrapper = new Wrapper<>("Hello");
 *  // 获取并打印封装的字符串
 *  System.out.println(stringWrapper.wrapped());
 *
 *  // 更改封装的对象
 *  stringWrapper.warp("World");
 *  // 再次获取并打印封装的字符串
 *  System.out.println(stringWrapper.wrapped());
 * }
 * </pre>
 *
 * @param <T> 该封装器类封装的对象的类型
 * @author SpCo
 * @version 2.0.0
 * @since 2.0.0
 */
public abstract class Wrapper<T> {
    /**
     * 被封装的泛型对象。
     */
    protected T object;

    /**
     * 构造函数，用于创建一个包含指定对象的封装器。
     * 这个构造方法允许将任意类型的对象封装到 Wrapper 类的实例中。
     *
     * @param object 需要被封装的对象。传递给该构造器的对象将会被存储在内部属性中，
     *               并且可以通过 {@link #wrapped()} 方法获取。
     */
    protected Wrapper(T object) {
        this.object = object;
    }

    /**
     * 用于更改当前被封装的对象。
     * 此方法提供了一种更改封装器内部对象的机制，而不需要创建新的封装器实例。
     *
     * @param object 新的被封装对象。此对象将替换当前存储的对象。
     */
    public void wrap(T object) {
        this.object = object;
    }

    /**
     * 返回被封装的对象。
     * 该方法提供了一个安全的方式来获取内部封装的对象，而不暴露对象的具体实现细节或允许对其进行更改。
     *
     * @return 封装的泛型对象。返回的是对内部封装对象的引用，而非其副本。
     */
    public T wrapped() {
        return object;
    }
}