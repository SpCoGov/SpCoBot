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
 * 定义了记录日志的方法，提供了不同级别的日志记录能力
 *
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public interface Logger {
    void debug(String message);

    void debug(Throwable e);

    void debug(String message, Throwable e);

    void info(String message);

    void info(Throwable e);

    void info(String message, Throwable e);

    void warn(String message);

    void warn(Throwable e);

    void warn(String message, Throwable e);

    void error(String message);

    void error(Throwable e);

    void error(String message, Throwable e);
}