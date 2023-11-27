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
 * 收到某些请求后的行为。如请求加群、请求添加好友等。
 *
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public interface Behavior {
    /**
     * 同意
     */
    void accept();

    /**
     * 忽略
     */
    void ignore();

    /**
     * 拒绝
     */
    void reject(boolean block, String message);
}