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
package top.spco.service.chat;

/**
 * 对话的类型
 *
 * @author SpCo
 * @version 0.1.1
 * @since 0.1.1
 */
public enum ChatType {
    /**
     * 对话在好友聊天中发生
     */
    FRIEND,
    /**
     * 对话在群组聊天中发生
     */
    GROUP,
    /**
     * 对话在群临时聊天中发生
     */
    GROUP_TEMP
}