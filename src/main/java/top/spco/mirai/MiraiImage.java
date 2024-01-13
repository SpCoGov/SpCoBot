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
package top.spco.mirai;

import top.spco.api.Image;

/**
 * @author SpCo
 * @version 1.3.0
 * @since 1.3.0
 */
record MiraiImage(net.mamoe.mirai.message.data.Image image) implements Image {
    @Override
    public String getImageId() {
        return image().getImageId();
    }

    @Override
    public int getWidth() {
        return image().getWidth();
    }

    @Override
    public int getHeight() {
        return image().getHeight();
    }

    @Override
    public String serialize() {
        return image().serializeToMiraiCode();
    }

    @Override
    public String toMessageContext() {
        return image().contentToString();
    }
}