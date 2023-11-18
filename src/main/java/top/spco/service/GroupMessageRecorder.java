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
package top.spco.service;

import top.spco.events.MessageEvents;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 如果用户在某个群发送的消息与机器人在此群发送的最后一条消息匹配，禁言他
 *
 * @author SpCo
 * @version 3.2
 * @since 1.0
 */
public class GroupMessageRecorder {
    private static boolean registered = false;
    /**
     * 记录机器人在每个群发送的最后一条消息
     */
    private static final Map<Long, String> lastMessage = new HashMap<>();

    public GroupMessageRecorder() {
        if (registered) {
            return;
        }
        registered = true;
        MessageEvents.GROUP_MESSAGE.register((bot, source, sender, message, time) -> {
            if (source.botPermission().isOperator() && !sender.getPermission().isOperator()) {
                if (isRepeating(source.getId(), message.toMessageContext())) {
                    int i = new Random().nextInt(60, 100);
                    sender.mute(i);
                    source.quoteReply(message, String.format("学我说话很好玩\uD83D\uDC34? 劳资反手就是禁言 %d 秒.", i));
                }
            }
        });
        MessageEvents.GROUP_MESSAGE_POST_SEND.register((bot, group, message) -> record(group.getId(), message.toMessageContext()));
    }

    public void record(long id, String content) {
        lastMessage.put(id, content);
    }

    public boolean isRepeating(long id, String content) {
        if (lastMessage.containsKey(id)) {
            return lastMessage.get(id).equals(content);
        }
        return false;
    }
}