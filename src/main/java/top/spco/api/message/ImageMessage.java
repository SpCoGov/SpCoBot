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
package top.spco.api.message;

import java.net.URL;

/**
 * 图片
 *
 * @author SpCo
 * @version 3.0.2
 * @since 1.3.0
 */
public class ImageMessage extends Message {
    private final URL url;

    public ImageMessage(URL url) {
        super();
        this.url = url;
    }

    public URL getUrl() {
        return url;
    }

    @Override
    public String toMessageContext() {
        return "[图片]";
    }

    /**
     * @deprecated {@code Image} 表示一张图片，不能在后添加消息。如需添加消息请使用 {@link Message}
     */
    @Override
    @Deprecated
    public MessageChain append(Message appendage) {
        return new MessageChain().append(appendage);
    }

    /**
     * @deprecated {@code Image} 表示一张图片，不能在后添加消息。如需添加消息请使用 {@link Message}
     */
    @Override
    @Deprecated
    public MessageChain append(String appendage) {
        return new MessageChain().append(appendage);
    }

    @Override
    public MessageChain toMessageChain() {
        return new MessageChain().append(this);
    }
}