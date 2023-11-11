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
package top.spco.base.api.message.service;

import top.spco.base.api.message.Message;

/**
 * <p>
 * Created on 2023/11/6 0006 22:24
 * <p>
 *
 * @author SpCo
 * @version 1.1
 * @since 1.1
 */
public interface MessageService {
    Message at(long id);

    Message atAll();

    Message append(Message original, Message other);

    Message append(Message original, String other);
}