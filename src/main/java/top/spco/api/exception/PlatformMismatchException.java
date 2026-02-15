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
package top.spco.api.exception;

import top.spco.core.Platform;

/**
 * 调用消息相关能力时，提交了不属于当前平台的对象。
 *
 * @author SpCo
 * @version 5.0.0
 * @since 5.0.0
 */
public class PlatformMismatchException extends RuntimeException {
    public PlatformMismatchException(String argumentName, Platform expected, Platform actual) {
        super("参数 " + argumentName + " 平台不匹配：期望 " + expected.getName() + "，实际 " + actual.getName());
    }
}
