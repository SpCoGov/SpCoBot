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
package top.spco.util;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.*;

/**
 * {@code Stopwatch} 类用于精确测量时间段，比如代码执行时间。
 * 这个类提供方法来启动、停止计时器，并获取经过的时间。
 * 使用 {@code System.nanoTime()} 来确保高精度。
 * <p>
 * 示例用法:
 * <pre>
 * {@code
 * Stopwatch stopwatch = Stopwatch.createUnstarted();
 * stopwatch.start();
 * // 执行一些代码
 * stopwatch.stop();
 * long timeElapsed = stopwatch.elapsedMillis();
 * System.out.println("耗时: " + stopwatch);
 * }</pre>
 *
 * @author SpCo
 * @version 2.0.7
 * @since 2.0.0
 */
public class Stopwatch {
    private long startTime;
    private long endTime;
    private boolean isRunning;

    /**
     * 构造函数私有化，以防止外部直接实例化。
     */
    private Stopwatch() {
        this.startTime = 0;
        this.endTime = 0;
        this.isRunning = false;
    }

    /**
     * 创建并返回一个新的 {@code Stopwatch} 实例，但该实例尚未启动。
     * 必须显式调用 {@link #start()} 方法来开始计时。
     *
     * @return 一个新的、未启动的 {@code Stopwatch} 实例。
     */
    public static Stopwatch createUnstarted() {
        return new Stopwatch();
    }

    /**
     * 启动计时器。
     *
     * @throws IllegalStateException 如果计时器已经在运行。
     */
    public void start() {
        if (isRunning) {
            throw new IllegalStateException("Stopwatch is already running");
        }
        isRunning = true;
        startTime = System.nanoTime();
    }

    /**
     * 停止计时器。
     *
     * @throws IllegalStateException 如果计时器未在运行。
     */
    public void stop() {
        if (!isRunning) {
            throw new IllegalStateException("Stopwatch is not running");
        }
        endTime = System.nanoTime();
        isRunning = false;
    }

    /**
     * 返回自计时器启动以来经过的时间（纳秒）。
     * 如果计时器正在运行，则返回当前时间与开始时间的差；
     * 如果计时器已停止，则返回结束时间与开始时间的差。
     *
     * @return 经过的时间，以纳秒为单位。
     */
    public long elapsedNanos() {
        if (isRunning) {
            return System.nanoTime() - startTime;
        } else {
            return endTime - startTime;
        }
    }

    /**
     * 重置计时器。停止计时器（如果它正在运行）并将所有时间数据重置。
     * 重置后的计时器可以通过调用 {@link #start()} 重新开始计时。
     */
    public void reset() {
        isRunning = false;
        startTime = 0;
        endTime = 0;
    }

    /**
     * 返回自计时器启动以来经过时间的字符串表示形式
     */
    @Override
    public String toString() {
        long nanos = elapsedNanos();

        TimeUnit unit = TimeUtils.chooseUnit(nanos);
        double value = (double) nanos / NANOSECONDS.convert(1, unit);

        return String.format(Locale.ROOT, "%.4g", value) + " " + TimeUtils.abbreviate(unit);
    }


}
