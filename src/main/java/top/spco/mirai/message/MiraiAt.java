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
package top.spco.mirai.message;

import top.spco.base.api.message.At;

/**
 * <p>
 * Created on 2023/10/26 0026 15:15
 * <p>
 *
 * @author SpCo
 * @version 1.0
 * @since 1.0
 */
public class MiraiAt implements At {
    public final net.mamoe.mirai.message.data.At at;

    public MiraiAt(long target) {
        this.at = new net.mamoe.mirai.message.data.At(target);
    }

    public MiraiAt(net.mamoe.mirai.message.data.At target) {
        this.at = target;
    }

    @Override
    public String toMessageContext() {
        return this.at.contentToString();
    }

    @Override
    public String serialize() {
        return this.at.serializeToMiraiCode();
    }

    @Override
    public long getTarget() {
        return this.at.getTarget();
    }
}