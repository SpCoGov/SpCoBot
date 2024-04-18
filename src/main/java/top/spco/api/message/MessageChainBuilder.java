/*
 * Copyright 2024 SpCo
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

import top.spco.core.Wrapper;
import top.spco.core.Builder;

/**
 * @author SpCo
 * @version 2.0.0
 * @since 0.1.0
 */
public abstract class MessageChainBuilder<T> extends Wrapper<T> implements Builder<Message<?>> {
    protected MessageChainBuilder(T builder) {
        super(builder);
    }

    /**
     * @deprecated 请使用 {@link Message#append(Message)}
     */
    @Deprecated
    public abstract MessageChainBuilder<T> append(Message<?> message);

    /**
     * @deprecated 请使用 {@link Message#append(String)}
     */
    @Deprecated
    public abstract MessageChainBuilder<T> append(String message);
}