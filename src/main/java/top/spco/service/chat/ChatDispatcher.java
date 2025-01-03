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
package top.spco.service.chat;

import top.spco.api.Bot;
import top.spco.api.Identifiable;
import top.spco.api.Interactive;
import top.spco.api.message.Message;
import top.spco.core.Manager;
import top.spco.service.RegistrationException;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于管理 {@link Chat} 的单例类。
 *
 * @author SpCo
 * @version 4.0.0
 * @since 0.1.1
 */
public class ChatDispatcher extends Manager<Long, Chat> {
    private static ChatDispatcher instance;
    private static boolean registered = false;
    private final Map<Long, Chat> friendChats = new HashMap<>();
    private final Map<Long, Chat> groupChats = new HashMap<>();
    private final Map<Long, Chat> groupTempChats = new HashMap<>();

    private ChatDispatcher() {
        if (!registered) {
            registered = true;
        }
    }

    public synchronized static ChatDispatcher getInstance() {
        if (instance == null) {
            instance = new ChatDispatcher();
        }
        return instance;
    }

    public void onMessage(ChatType chatType, Bot<?> bot, Interactive<?> source, Interactive<?> sender, Message<?> message, int time) {
        Chat chat = getChat(source, chatType);
        if (chat == null) {
            return;
        }
        chat.handleMessage(bot, source, sender, message, time);
    }

    public boolean isInChat(Identifiable<?> where, ChatType chatType) {
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
     * @deprecated 使用此方法会始终返回 {@code null} ，请用 {@link #getChat(Identifiable, ChatType)} 替代。
     */
    @Deprecated
    @Override
    public Map<Long, Chat> getAllRegistered() {
        return null;
    }

    /**
     * @deprecated 请使用 {@link #getChat(Identifiable, ChatType)} 。
     */
    @Deprecated
    @Override
    public Chat get(Long id) {
        return groupChats.get(id);
    }

    /**
     * 获取对话对象。
     *
     * @param where    发生对话的场所
     * @param chatType 对话的类型
     * @return 对话对象，失败时返回null
     */
    public Chat getChat(Identifiable<?> where, ChatType chatType) {
        return switch (chatType) {
            case GROUP -> groupChats.get(where.getId());
            case FRIEND -> friendChats.get(where.getId());
            case GROUP_TEMP -> groupTempChats.get(where.getId());
        };
    }

    @Override
    public void register(Long value, Chat chat) throws RegistrationException {
        register(chat);
    }

    /**
     * 注册一个对话。
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
     * 结束对话。
     *
     * @param where    发生对话的场所
     * @param chatType 对话的类型
     */
    public void stopChat(Identifiable<?> where, ChatType chatType) {
        switch (chatType) {
            case GROUP -> this.groupChats.remove(where.getId());
            case FRIEND -> this.friendChats.remove(where.getId());
            case GROUP_TEMP -> this.groupTempChats.remove(where.getId());
        }
    }
}