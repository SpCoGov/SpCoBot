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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * {@code LoggedTimer} 类是一个用于记录特定事件执行时间的实用工具。
 * 它结合了 {@code Stopwatch} 的时间测量功能和日志记录，以便在事件开始和结束时提供实时反馈。
 * <p>
 * 使用方法:
 * <pre>
 * {@code
 * LoggedTimer timer = new LoggedTimer();
 * timer.start("事件名称");
 * // 执行一些代码
 * timer.stop();
 * }</pre>
 * 在开始和停止计时时，会记录日志消息，指示事件的开始和持续时间。
 *
 * @author SpCo
 * @version 2.0.0
 * @since 2.0.0
 */
public class LoggedTimer {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Stopwatch stopWatch = Stopwatch.createUnstarted();
    private String event;

    /**
     * 启动计时器并记录特定事件的开始。
     * 这个方法将重置并启动内部的 {@code Stopwatch} 实例，并记录一个包含事件名称的日志。
     *
     * @param event 要记录的事件的名称。
     */
    public void start(String event) {
        this.event = event;
        LOGGER.info("{}...", event);
        stopWatch.reset();
        stopWatch.start();
    }

    /**
     * 停止计时器并记录特定事件的持续时间。
     * 这个方法停止内部的 {@code Stopwatch} 实例，并记录一个包含事件名称和持续时间的日志。
     */
    public void stop() {
        stopWatch.stop();
        LOGGER.info("{} 完成, 耗时 {}。", this.event, stopWatch);
    }
}