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


import top.spco.api.message.MessageSource;

/**
 * @author SpCo
 * @version 2.0.0
 * @since 0.3.0
 */
class MiraiMessageSource extends MessageSource<net.mamoe.mirai.message.data.MessageSource> {
    protected MiraiMessageSource(net.mamoe.mirai.message.data.MessageSource object) {
        super(object);
    }

    @Override
    public long getFromId() {
        return wrapped().getFromId();
    }

    @Override
    public long getTargetId() {
        return wrapped().getTargetId();
    }
}