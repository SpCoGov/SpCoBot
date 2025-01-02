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

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@code NamedThreadFactory} 类提供了一种具有自定义名称的线程工厂实现。
 * 这个类允许用户为创建的线程指定一个基础名称，新创建的线程将会附加一个递增的数字作为其名称的一部分。
 * 这样可以更容易地识别和调试多线程环境中的特定线程。
 *
 * <p>例如，如果基础名称被指定为 "Worker"，那么生成的线程名称将会是 "Worker-1"、"Worker-2" 等。</p>
 *
 * <p>这个类实现了 {@link java.util.concurrent.ThreadFactory} 接口，允许它与各种并发工具一起使用，比如 {@link java.util.concurrent.ExecutorService}。</p>
 *
 * <h2>使用教程</h2>
 * <p>以下是如何使用 {@code NamedThreadFactory} 的一个基本示例：</p>
 * <pre>
 * // 创建 NamedThreadFactory 实例，指定基础名称
 * NamedThreadFactory factory = new NamedThreadFactory("WorkerThread");
 *
 * // 创建一个固定大小的线程池，并使用 NamedThreadFactory
 * ExecutorService executor = Executors.newFixedThreadPool(5, factory);
 *
 * // 提交任务到线程池
 * executor.submit(() -&gt; {
 *     System.out.println("任务在 " + Thread.currentThread().getName() + " 上执行");
 * });
 *
 * // 完成后关闭线程池
 * executor.shutdown();
 * </pre>
 *
 * <p>使用 {@code NamedThreadFactory} 可以帮助您在多线程应用程序中更容易地追踪和调试线程。</p>
 *
 * @author SpCo
 * @version 2.0.0
 * @since 2.0.0
 */
public class NamedThreadFactory implements ThreadFactory {
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String baseThreadName;

    /**
     * 使用指定的基础名称构造一个新的 {@code NamedThreadFactory} 实例。
     *
     * @param name 用于生成线程名称的基础名称。
     */
    public NamedThreadFactory(String name) {
        this.baseThreadName = name;
    }

    /**
     * 创建一个新的线程，该线程将使用 {@code NamedThreadFactory} 指定的基础名称和递增的数字。
     *
     * @param r 将在新线程中执行的 {@code Runnable} 任务。
     * @return 创建的新线程，已经被赋予了一个基于指定基础名称和递增数字的名称。
     */
    @Override
    public Thread newThread(@NotNull Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(baseThreadName + "-" + threadNumber.getAndIncrement());
        return thread;
    }
}