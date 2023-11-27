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
 * 命令的作用域
 *
 * @author SpCo
 * @version 0.3.3
 * @since 0.1.0
 */
public enum CommandScope {
    /**
     * 仅可以在好友聊天中发送
     */
    ONLY_FRIEND,
    /**
     * 仅可以在私聊(如好友聊天, 群临时消息)中发送
     */
    ONLY_PRIVATE,
    /**
     * 仅可以在群中发送
     */
    ONLY_GROUP,
    /**
     * 可以在任何来源中发送
     */
    ALL
}