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
import top.spco.base.api.Bot;
import top.spco.base.api.Interactive;
import top.spco.base.api.message.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Created on 2023/11/5 0005 22:03
 * <p>
 *
 * @author SpCo
 * @version 1.1
 * @since 1.1
 */
public class Chat {
    private final ChatType type;
    private volatile boolean frozen = false;
    private List<Stage> stages = new ArrayList<>();
    private int currentStageIndex = 0;
    private final Interactive target;
    private boolean stopped;

    public ChatType getType() {
        return type;
    }

    public Interactive getTarget() {
        return target;
    }

    public Chat(ChatType type, Interactive target) {
        this.type = type;
        this.target = target;
    }

    public void addStage(Stage stage) {
        if (frozen) {
            throw new IllegalStateException("Cannot add stages after the pre-initialization phase!");
        }
        stages.add(stage);
    }

    public void freeze() {
        frozen = true;
    }

    public void runStage() {
        if (stopped) {
            return;
        }
        target.sendMessage(getCurrentStage().startMessage.get());
    }

    public void handleMessage(Bot bot, Interactive source, Interactive sender, Message message, int time) {
        if (stopped) {
            return;
        }
        getCurrentStage().stageExecuter.onMessage(this, bot, source, sender, message, time);
    }

    public void start() {
        runStage();
    }

    public void stop() {
        this.stages = null;
        this.stopped = true;
        SpCoBot.getInstance().chatManager.stopChat(target, type);
    }

    private Stage getCurrentStage() {
        return this.stages.get(this.currentStageIndex);
    }

    public void replay() {
        runStage();
    }

    public void toStage(int index) {
        this.currentStageIndex = index;
        runStage();
    }

    public void next() {
        this.currentStageIndex += 1;
        if (this.currentStageIndex == this.stages.size()) {
            this.stop();
        }
        runStage();
    }
}