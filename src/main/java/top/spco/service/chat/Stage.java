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
import top.spco.api.Interactive;
import top.spco.api.message.Message;

import java.util.function.Supplier;

/**
 * {@link Chat 对话}中的一个阶段
 *
 * @see Chat
 * @author SpCo
 * @version 2.0.0
 * @since 0.1.1
 */
public class Stage {
    public final Supplier<String> startMessage;
    public final StageExecuter stageExecuter;

    public Stage(Supplier<String> startMessage, StageExecuter executer) {
        this.startMessage = startMessage;
        this.stageExecuter = executer;
    }

    public interface StageExecuter {
        void onMessage(Chat chat, Bot<?> bot, Interactive<?> source, Interactive<?> sender, Message<?> message, int time);
    }
}