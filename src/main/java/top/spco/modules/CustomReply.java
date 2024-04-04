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
package top.spco.modules;

import top.spco.core.module.AbstractModule;
import top.spco.events.MessageEvents;

/**
 * 三种不同场合的自定义回复
 *
 * @author SpCo
 * @version 3.0.0
 * @since 3.0.0
 */
public class CustomReply extends AbstractModule {
    /**
     * 构造一个新的模块。
     */
    public CustomReply() {
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
        MessageEvents.FRIEND_MESSAGE.register((bot, sender, message, time) -> {
            if (isActive()) {
                switch (message.toMessageContext()) {
                    case "叫爸爸" -> sender.quoteReply(message, "爸爸");
                    case "叫爷爷" -> sender.quoteReply(message, "爷爷");
                }
            }
        });
        MessageEvents.GROUP_MESSAGE.register((bot, source, sender, message, time) -> {
            if (isActive()) {
                switch (message.toMessageContext()) {
                    case "叫爸爸" -> source.quoteReply(message, "爸爸");
                    case "叫爷爷" -> source.quoteReply(message, "爷爷");
                }
            }
        });
        MessageEvents.GROUP_TEMP_MESSAGE.register((bot, source, sender, message, time) -> {
            if (isActive()) {
                switch (message.toMessageContext()) {
                    case "叫爸爸" -> sender.quoteReply(message, "爸爸");
                    case "叫爷爷" -> sender.quoteReply(message, "爷爷");
                }
            }
        });
    }
}