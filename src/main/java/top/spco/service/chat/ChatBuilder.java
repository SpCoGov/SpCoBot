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
import top.spco.base.api.Friend;
import top.spco.base.api.Group;
import top.spco.base.api.Interactive;
import top.spco.base.api.NormalMember;

/**
 * <p>
 * Created on 2023/11/6 0006 10:38
 * <p>
 *
 * @author SpCo
 * @version 1.1
 * @since 1.1
 */
public class ChatBuilder {
    private final Chat chat;

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

    public ChatBuilder addStage(Stage stage) {
        chat.addStage(stage);
        return this;
    }

    public Chat build() {
        chat.freeze();
        SpCoBot.getInstance().chatManager.register(chat);
        return chat;
    }
}