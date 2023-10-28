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

import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.MessageSource;
import net.mamoe.mirai.message.data.MessageSourceBuilder;
import net.mamoe.mirai.message.data.QuoteReply;
import top.spco.base.api.message.Message;
import top.spco.base.api.message.MessageChainBuilder;

/**
 * <p>
 * Created on 2023/10/27 0027 19:31
 * <p>
 *
 * @author SpCo
 * @version 1.0
 * @since 1.0
 */
public class MiraiMessageChainBuilder implements MessageChainBuilder {
    public final net.mamoe.mirai.message.data.MessageChainBuilder builder;

    public MiraiMessageChainBuilder(Message toQuote) {
        this.builder = new net.mamoe.mirai.message.data.MessageChainBuilder();
        this.builder.append(new QuoteReply(((MiraiMessage) toQuote).message()));
    }

    @Override
    public MessageChainBuilder append(Message message) {
        this.builder.append(((MiraiMessage) message).message());
        return this;
    }

    @Override
    public MessageChainBuilder append(String message) {
        this.builder.append(message);
        return this;
    }

    @Override
    public Message build() {
        return new MiraiMessage(this.builder.build());
    }
}