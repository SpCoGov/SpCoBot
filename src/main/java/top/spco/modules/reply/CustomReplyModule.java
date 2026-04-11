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
package top.spco.modules.reply;

import top.spco.SpCoBot;
import top.spco.core.module.AbstractModule;
import top.spco.events.MessageEvents;
import top.spco.statistics.ItemStatistics;

import java.sql.SQLException;

/**
 * 三种不同场合的自定义回复
 *
 * @author SpCo
 * @version 4.1.0
 * @since 3.0.0
 */
public class CustomReplyModule extends AbstractModule {
    private final Replier replier = new Replier();

    public CustomReplyModule() {
        super("CustomReply");
    }

    @Override
    public void init() {
        SpCoBot.getInstance().getRuntimeStatistic().add(new ItemStatistics("触发自定义回复", "次"));
        MessageEvents.PRIVATE_MESSAGE.register((bot, sender, message, time) -> {
            if (!isActive()) {
                return;
            }
            String result = replier.reply(message.toMessageContext());
            if (result != null) {
                SpCoBot.getInstance().getRuntimeStatistic().item("触发自定义回复").add();
                sender.quoteReply(message, result);
            }
        });
        MessageEvents.GROUP_MESSAGE.register((bot, source, sender, message, time) -> {
            if (!isActive()) {
                return;
            }
            try {
                if (!isAvailable(source)) {
                    return;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            String result = replier.reply(message.toMessageContext());
            if (result != null) {
                SpCoBot.getInstance().getRuntimeStatistic().item("触发自定义回复").add();
                source.quoteReply(message, result);
            }
        });
    }

}