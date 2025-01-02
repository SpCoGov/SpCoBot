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
package top.spco.mirai;

import net.mamoe.mirai.message.data.MessageChainBuilder;
import top.spco.api.Image;
import top.spco.api.message.Message;

/**
 * @author SpCo
 * @version 2.0.0
 * @since 1.3.0
 */
class MiraiImage extends Image<net.mamoe.mirai.message.data.Image> {
    protected MiraiImage(net.mamoe.mirai.message.data.Image image) {
        super(image);
    }

    @Override
    public String getImageId() {
        return wrapped().getImageId();
    }

    @Override
    public int getWidth() {
        return wrapped().getWidth();
    }

    @Override
    public int getHeight() {
        return wrapped().getHeight();
    }

    @Override
    public String serialize() {
        return wrapped().serializeToMiraiCode();
    }

    @Override
    public String toMessageContext() {
        return wrapped().contentToString();
    }

    @Override
    public Message<?> toMessage() {
        return new MiraiMessage(new MessageChainBuilder().append(wrapped()).build());
    }
}