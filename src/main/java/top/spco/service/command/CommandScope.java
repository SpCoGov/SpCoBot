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
package top.spco.service.command;

import top.spco.api.*;

/**
 * 命令的作用域
 *
 * @author SpCo
 * @version 2.0.0
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
    ALL;

    /**
     * 根据传入的 {@link Interactive} 对象推测对应的命令作用域。
     *
     * @param interactive 要推测作用域的 {@link Interactive} 对象
     * @return 对应的指令作用域，或者 {@code null} 如果未能确定作用域
     */
    public static CommandScope getCommandScope(Interactive<?> interactive) {
        if (interactive instanceof Group) {
            return ONLY_GROUP;
        } else if (interactive instanceof Friend) {
            return ONLY_FRIEND;
        } else if (interactive instanceof NormalMember) {
            return ONLY_PRIVATE;
        }
        return null;
    }
}