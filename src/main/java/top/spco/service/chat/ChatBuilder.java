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
import top.spco.api.*;
import top.spco.core.Builder;

/**
 * {@code ChatBuilder} 类是用于构建 {@link Chat} 对象的构建器。
 * 它提供了一个流畅的接口来设置对话的类型、目标对象以及添加多个交互阶段（{@link Stage}）。
 * 使用此类可以轻松构建和配置一个{@link Chat}对象，以管理用户与机器人之间的交互。
 *
 * <p>基本用法：</p>
 * <ol>
 *     <li>首先，使用{@code ChatBuilder}构造函数，指定聊天类型和目标交互对象。</li>
 *     <li>然后，通过调用{@code addStage(Stage stage)}方法添加一个或多个交互阶段。</li>
 *     <li>最后，调用{@code build()}方法来构建并冻结{@link Chat}对象，防止更多阶段的添加。</li>
 * </ol>
 *
 * <p>示例：</p>
 * <pre>{@code
 * Chat chat = new ChatBuilder(ChatType.FRIEND, targetInteractive)
 *     .addStage(new Stage(...)) // 添加第一个交互阶段
 *     .addStage(new Stage(...)) // 添加更多交互阶段
 *     .build(); // 构建并获取Chat对象
 * }</pre>
 *
 * <h1>注意事项</h1>
 * 如果要为 {@code NormalMember} 创建对话，创建前请使用 {@link NormalMember#isFriend()} 来确认群成员是否为机器人的好友。
 * 如果该群成员是机器人的好友且创建的对话类型为 {@code GROUP_TEMP} 的话，对话对象无法获取该用户发来的消息。
 *
 * @author SpCo
 * @version 2.0.0
 * @since 0.3.1
 */
public class ChatBuilder implements Builder<Chat> {
    private final Chat chat;

    /**
     * 创建一个{@code ChatBuilder}实例，根据给定的{@link ChatType}和目标{@link Interactive}对象。
     *
     * @param chatType {@link ChatType 聊天类型}，可以是{@link ChatType#GROUP}、{@link ChatType#FRIEND}或{@link ChatType#GROUP_TEMP}之一
     * @param target   目标{@link Interactive}对象，根据{@link ChatType 聊天类型}的不同可以是{@link Group}、{@link Friend}或{@link NormalMember}
     * @throws ChatTypeMismatchException 如果{@link ChatType 聊天类型}与目标{@link Interactive}对象不匹配时抛出异常
     */
    public ChatBuilder(ChatType chatType, Interactive<?> target) throws ChatTypeMismatchException {
        switch (chatType) {
            case GROUP -> {
                if (!(target instanceof Group)) {
                    throw new ChatTypeMismatchException(chatType, target);
                }
            }
            case FRIEND -> {
                boolean isFriend = false;
                if (target instanceof NormalMember<?> member) {
                    isFriend = member.isFriend();
                }
                if (!isFriend && !(target instanceof Friend)) {
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
     * 构建{@link Chat}对象，冻结它以防止添加更多{@link Stage 阶段}，并将其注册到{@link ChatDispatcher}中。
     *
     * @return 创建的Chat对象
     */
    @Override
    public Chat build() {
        chat.freeze();
        SpCoBot.getInstance().chatDispatcher.register(chat);
        return chat;
    }
}