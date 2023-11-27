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
package top.spco.service.chat;

import top.spco.api.Bot;
import top.spco.api.Identifiable;
import top.spco.api.Interactive;
import top.spco.api.message.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于管理{@link Chat}的单例类
 *
 * @author SpCo
 * @version 0.1.1
 * @since 0.1.1
 */
public class ChatManager {
    private static ChatManager instance;
    private static boolean registered = false;
    private final Map<Long, Chat> friendChats = new HashMap<>();
    private final Map<Long, Chat> groupChats = new HashMap<>();
    private final Map<Long, Chat> groupTempChats = new HashMap<>();

    private ChatManager() {
        if (!registered) {
            registered = true;
        }
    }

    public static ChatManager getInstance() {
        if (instance == null) {

            instance = new ChatManager();
        }
        return instance;
    }

    public void onMessage(ChatType chatType, Bot bot, Interactive source, Interactive sender, Message message, int time) {
        Chat chat = getChat(source, chatType);
        if (chat == null) {
            return;
        }
        chat.handleMessage(bot, source, sender, message, time);
    }

    public boolean isInChat(Identifiable where, ChatType chatType) {
        switch (chatType) {
            case GROUP -> {
                if (this.groupChats.containsKey(where.getId()) && this.groupChats.get(where.getId()) != null) {
                    return true;
                }
            }
            case FRIEND -> {
                if (this.friendChats.containsKey(where.getId()) && this.friendChats.get(where.getId()) != null) {
                    return true;
                }
            }
            case GROUP_TEMP -> {
                if (this.groupTempChats.containsKey(where.getId()) && this.groupTempChats.get(where.getId()) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取对话对象
     *
     * @param where    发生对话的场所
     * @param chatType 对话的类型
     * @return 对话对象，失败时返回null
     */
    public Chat getChat(Identifiable where, ChatType chatType) {
        switch (chatType) {
            case GROUP -> {
                if (this.groupChats.containsKey(where.getId()) && this.groupChats.get(where.getId()) != null) {
                    return this.groupChats.get(where.getId());
                }
            }
            case FRIEND -> {
                if (this.friendChats.containsKey(where.getId()) && this.friendChats.get(where.getId()) != null) {
                    return this.friendChats.get(where.getId());
                }
            }
            case GROUP_TEMP -> {
                if (this.groupTempChats.containsKey(where.getId()) && this.groupTempChats.get(where.getId()) != null) {
                    return this.groupTempChats.get(where.getId());
                }
            }
        }
        return null;
    }

    /**
     * 注册一个对话
     *
     * @param chat 对话
     */
    public void register(Chat chat) {
        ChatType chatType = chat.getType();
        long id = chat.getTarget().getId();
        switch (chatType) {
            case GROUP -> this.groupChats.put(id, chat);
            case FRIEND -> this.friendChats.put(id, chat);
            case GROUP_TEMP -> this.groupTempChats.put(id, chat);
        }
    }

    /**
     * 结束对话
     *
     * @param where    发生对话的场所
     * @param chatType 对话的类型
     */
    public void stopChat(Identifiable where, ChatType chatType) {
        switch (chatType) {
            case GROUP -> {
                if (this.groupChats.containsKey(where.getId()) && this.groupChats.get(where.getId()) != null) {
                    this.groupChats.put(where.getId(), null);
                }
            }
            case FRIEND -> {
                if (this.friendChats.containsKey(where.getId()) && this.friendChats.get(where.getId()) != null) {
                    this.friendChats.put(where.getId(), null);
                }
            }
            case GROUP_TEMP -> {
                if (this.groupTempChats.containsKey(where.getId()) && this.groupTempChats.get(where.getId()) != null) {
                    this.groupTempChats.put(where.getId(), null);
                }
            }
        }
    }
}