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
package top.spco.modules.reply;

import top.spco.core.module.AbstractModule;
import top.spco.events.MessageEvents;
import top.spco.modules.reply.rules.BarkRule;
import top.spco.modules.reply.rules.BotRule;
import top.spco.modules.reply.rules.CallFatherRule;
import top.spco.modules.reply.rules.ScoldedRule;

/**
 * 三种不同场合的自定义回复
 *
 * @author SpCo
 * @version 3.2.0
 * @since 3.0.0
 */
public class CustomReplyModule extends AbstractModule {
    private final Replier replier = new Replier();

    /**
     * 构造一个新的模块。
     */
    public CustomReplyModule() {
        super("CustomReply");
    }

    @Override
    public void onActivate() {

    }

    @Override
    public void onDeactivate() {

    }

    @Override
    public void init() {
        replier.add(new ScoldedRule());
        replier.add(new BotRule());
        replier.add(new CallFatherRule());
        replier.add(new BarkRule());
        MessageEvents.FRIEND_MESSAGE.register((bot, sender, message, time) -> {
            if (isActive()) {
                String result = replier.reply(message.toMessageContext());
                if (result != null) {
                    sender.quoteReply(message, result);
                }
            }
        });
        MessageEvents.GROUP_MESSAGE.register((bot, source, sender, message, time) -> {
            if (isActive()) {
                String result = replier.reply(message.toMessageContext());
                if (result != null) {
                    source.quoteReply(message, result);
                }
            }
        });
        MessageEvents.GROUP_TEMP_MESSAGE.register((bot, source, sender, message, time) -> {
            if (isActive()) {
                String result = replier.reply(message.toMessageContext());
                if (result != null) {
                    sender.quoteReply(message, result);
                }
            }
        });
    }

}