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
package top.spco.user;

/**
 * 表示操作用户时出现的异常情况。
 *
 * @author SpCo
 * @version 1.2.3
 * @since 1.2.3
 */
public class UserOperationException extends Exception {

    /**
     * 使用指定的错误消息创建一个 UserOperationException 实例。
     *
     * @param message 错误消息描述异常情况。
     */
    public UserOperationException(String message) {
        super(message);
    }

    /**
     * 使用指定的错误消息和原始异常创建一个 UserOperationException 实例。
     *
     * @param message 错误消息描述异常情况。
     * @param cause 原始异常（导致此异常的异常）。
     */
    public UserOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}