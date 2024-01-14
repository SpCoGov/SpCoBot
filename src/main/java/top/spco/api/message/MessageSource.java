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
package top.spco.api.message;

import top.spco.api.Wrapper;

/**
 * 消息的来源信息
 *
 * @author SpCo
 * @version 2.0.0
 * @since 0.3.0
 */
public abstract class MessageSource<T> extends Wrapper<T> {
protected MessageSource(T object) {
        super(object);
    }

    /**
     * 发送人用户 ID
     */
    public abstract long getFromId();

    /**
     * 消息发送目标用户或群号码
     */
    public abstract long getTargetId();
}