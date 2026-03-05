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
package top.spco.api;

/**
 * 代表一位用户
 *
 * @author SpCo
 * @version 4.1.0
 * @since 0.1.0
 */
public abstract class User<T> extends Interactive<T> {
    public User(T user) {
        super(user);
    }

    /**
     * 获取该用户的昵称。
     *
     * @return 昵称
     */
    public abstract String getNick();

    /**
     * 该用户是否是机器人。
     *
     * @return 当该用户是机器人时返回 {@code true}
     */
    public abstract boolean isBot();
}