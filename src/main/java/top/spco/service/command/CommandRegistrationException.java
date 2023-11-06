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
package top.spco.service.command;

/**
 * <p>
 * 用于处理机器人注册命令时出现的问题
 * <p>
 *
 * @author SpCo
 * @version 1.0
 * @since 1.0
 */
public class CommandRegistrationException extends Exception {
    /**
     * 带有详细消息的构造函数。
     *
     * @param message 详细的异常信息。
     */
    public CommandRegistrationException(String message) {
        super(message);
    }

    /**
     * 带有详细消息和原因的构造函数。
     *
     * @param message 详细的异常信息。
     * @param cause   引发此异常的原因。
     */
    public CommandRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}