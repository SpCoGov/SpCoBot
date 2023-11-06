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

/**
 * 表示一个接受五个输入参数并不返回结果的操作。
 * 与大多数其他函数式接口不同，{@code PentaConsumer} 预期通过副作用执行操作。
 *
 * <p>这是一个<a href="package-summary.html">函数式接口</a>，其功能方法是 {@link #accept(Object, Object, Object, Object, Object)}。
 *
 * @param <T> 第一个输入参数的类型
 * @param <U> 第二个输入参数的类型
 * @param <V> 第三个输入参数的类型
 * @param <W> 第四个输入参数的类型
 * @param <X> 第五个输入参数的类型
 *
 * @author SpCo
 * @version 1.1
 * @since 1.1
 */
@FunctionalInterface
public interface PentaConsumer<T, U, V, W, X> {
    /**
     * 对给定的四个输入参数执行此操作。
     *
     * @param t 第一个输入参数
     * @param u 第二个输入参数
     * @param v 第三个输入参数
     * @param w 第四个输入参数
     * @param x 第五个输入参数
     */
    void accept(T t, U u, V v, W w, X x);
}