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
package top.spco.api.message.service;

import top.spco.api.message.Message;

/**
 * 消息服务
 *
 * @author SpCo
 * @version 1.1
 * @since 1.1
 */
public interface MessageService {
    /**
     * At一位群成员
     *
     * @param id 群成员的Id
     * @return 包含At的Message对象
     */
    Message at(long id);

    /**
     * At全体成员
     *
     * @return 包含At全体成员的Message对象
     */
    Message atAll();

    /**
     * 往消息后添加一条消息
     *
     * @param original 原始消息
     * @param other    待添加的消息
     * @return 操作后的Message对象
     */
    Message append(Message original, Message other);

    /**
     * 往消息后添加一则文本
     *
     * @param original 原始消息
     * @param other    待添加的文本
     * @return 操作后的Message对象
     */
    Message append(Message original, String other);
}