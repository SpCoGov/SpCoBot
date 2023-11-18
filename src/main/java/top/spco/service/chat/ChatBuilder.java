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

import top.spco.SpCoBot;
import top.spco.api.Friend;
import top.spco.api.Group;
import top.spco.api.Interactive;
import top.spco.api.NormalMember;
import top.spco.util.builder.Builder;

/**
 * {@link ChatBuilder}是一个用于构建{@link Chat}对象的构建器。<p>
 * 该类允许您创建{@link Chat}对象，管理用户与机器人之间的交互，并添加{@link Stage 交互阶段}。
 *
 * @author SpCo
 * @version 1.1
 * @since 3.1
 */
public class ChatBuilder implements Builder<Chat> {
    private final Chat chat;

    /**
     * 创建一个{@link ChatBuilder}实例，根据给定的{@link ChatType}和目标{@link Interactive}对象。
     *
     * @param chatType {@link ChatType 聊天类型}，可以是{@link ChatType#GROUP}、{@link ChatType#FRIEND}或{@link ChatType#GROUP_TEMP}之一
     * @param target   目标{@link Interactive}对象，根据{@link ChatType 聊天类型}的不同可以是{@link Group}、{@link Friend}或{@link NormalMember}
     * @throws ChatTypeMismatchException 如果{@link ChatType 聊天类型}与目标{@link Interactive}对象不匹配时抛出异常
     */
    public ChatBuilder(ChatType chatType, Interactive target) throws ChatTypeMismatchException {
        switch (chatType) {
            case GROUP -> {
                if (!(target instanceof Group)) {
                    throw new ChatTypeMismatchException(chatType, target);
                }
            }
            case FRIEND -> {
                if (!(target instanceof Friend)) {
                    throw new ChatTypeMismatchException(chatType, target);
                }
            }
            case GROUP_TEMP -> {
                if (!(target instanceof NormalMember)) {
                    throw new ChatTypeMismatchException(chatType, target);
                }
            }
        }
        this.chat = new Chat(chatType, target);
    }

    /**
     * 向{@link Chat}对象添加一个{@link Stage 交互阶段}。
     *
     * @param stage 要添加的{@link Stage 交互阶段}
     * @return 当前ChatBuilder实例，以支持方法链式调用
     */
    public ChatBuilder addStage(Stage stage) {
        chat.addStage(stage);
        return this;
    }

    /**
     * 构建{@link Chat}对象，冻结它以防止添加更多{@link Stage 阶段}，并将其注册到{@link ChatManager}中。
     *
     * @return 创建的Chat对象
     */
    public Chat build() {
        chat.freeze();
        SpCoBot.getInstance().chatManager.register(chat);
        return chat;
    }
}