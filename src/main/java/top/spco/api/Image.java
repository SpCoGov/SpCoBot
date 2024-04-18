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
package top.spco.api;

import top.spco.api.message.Message;
import top.spco.api.message.MessageSource;

/**
 * 图片
 *
 * @author SpCo
 * @version 3.0.2
 * @since 1.3.0
 */
public abstract class Image<T> extends Message<T> {
    protected Image(T image) {
        super(image);
    }

    public abstract String getImageId();

    public abstract int getWidth();

    public abstract int getHeight();

    /**
     * @deprecated {@code Image} 表示一张图片，不能在后添加消息。如需添加消息请使用 {@link Message}
     */
    @Override
    @Deprecated
    public Message<T> append(Message<?> message) {
        return this;
    }

    /**
     * @deprecated {@code Image} 表示一张图片，不能在后添加消息。如需添加消息请使用 {@link Message}
     */
    @Override
    @Deprecated
    public Message<T> append(String message) {
        return this;
    }

    /**
     * @deprecated {@code Image} 表示一张图片，如需引用它先将其转换为普通的 {@code Message}。
     */
    @Override
    @Deprecated
    public Message<T> quoteReply(Message<?> toQuote) {
        return this;
    }

    /**
     * @deprecated {@code Image} 表示一张图片，没有该属性。
     */
    @Override
    @Deprecated
    public MessageSource<?> getSource() {
        return null;
    }
}