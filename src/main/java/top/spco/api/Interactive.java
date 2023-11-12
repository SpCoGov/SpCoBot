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

import top.spco.api.message.Message;

/**
 * <p>
 * Created on 2023/10/26 0026 17:24
 * <p>
 *
 * @author SpCo
 * @version 1.0
 * @since 1.0
 */
public interface Interactive extends Identifiable {
    void sendMessage(String message);

    void sendMessage(Message message);

    void handleException(Message sourceMessage, String message, Throwable throwable);

    void handleException(Message sourceMessage, Throwable throwable);

    void handleException(String message, Throwable throwable);

    void handleException(Throwable throwable);

    void handleException(String message);

    void quoteReply(Message sourceMessage, Message message);

    void quoteReply(Message sourceMessage, String message);
}