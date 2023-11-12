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
package top.spco.service.chat;

import top.spco.api.Interactive;
import top.spco.service.RegistrationException;

/**
 * 当创建{@link Chat}对象时提交的聊天类型和聊天目标不匹配时会抛出此异常
 *
 * @author SpCo
 * @version 1.1
 * @since 1.1
 */
public class ChatTypeMismatchException extends RegistrationException {
    /**
     * 带有详细消息的构造函数。
     *
     * @param chatType 待创建的{@link ChatType 对话类型}
     * @param target   对话作用于的目标
     */
    public ChatTypeMismatchException(ChatType chatType, Interactive target) {
        super("Chat type mismatch: Cannot create Chat with " + chatType + " for target " + target);
    }
}