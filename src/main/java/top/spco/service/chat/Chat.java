/*
 * Copyright 2024 SpCo
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
import top.spco.api.Bot;
import top.spco.api.Interactive;
import top.spco.api.message.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code Chat} 类定义了用户与机器人之间的对话交互。它允许创建多个交互阶段，每个阶段可以定义特定的交互逻辑。
 * 该类支持动态添加阶段、冻结对话以防止更改、处理消息、以及控制对话流程。
 * <p>使用方法：</p>
 * <ol>
 *     <li>使用{@link ChatBuilder}创建一个新的{@code Chat}对象，指定对话的类型和交互目标。</li>
 *     <li>使用{@link #addStage(Stage)}方法添加一个或多个交互阶段。</li>
 *     <li>每个阶段定义一个开始消息和一个基于用户回复的处理逻辑。</li>
 *     <li>通过调用{@link ChatBuilder#build()}方法完成构建过程。</li>
 *     <li>调用{@link #start()}方法来启动对话。</li>
 * </ol>
 *
 * <p>示例代码：</p>
 * <pre>{@code
 * StringBuilder sb = new StringBuilder();
 * Chat chat = new ChatBuilder(ChatType.FRIEND, from)
 *     .addStage(new Stage(() -> "请输入你好", (chat1, bot, source, sender, message, time) -> {
 *         if (message.toMessageContext().equals("你好")) {
 *             sb.append("你好，").append(sender.getId());
 *             chat1.next();
 *         } else {
 *             chat1.replay();
 *         }
 *     }))
 *     .addStage(new Stage(() -> "请随意输入文本", (chat1, bot, source, sender, message, time) -> {
 *         sb.append("\n").append(message.toMessageContext());
 *         chat1.next();
 *     }))
 *     .addStage(new Stage(() -> "最终文本为\n" + sb + "\n输入确定即可发送，输入取消退出", (chat1, bot, source, sender, message, time) -> {
 *         switch (message.toMessageContext()) {
 *             case "取消" -> chat1.stop();
 *             case "确定" -> {
 *                 source.sendMessage(sb.toString());
 *                 chat1.next();
 *             }
 *             default -> chat1.replay();
 *         }
 *     }))
 *     .build();
 * chat.start();
 * }</pre>
 *
 * @author SpCo
 * @version 2.0.0
 * @since 0.1.1
 * @see Stage
 * @see ChatBuilder
 */
public class Chat {
    private final ChatType type;
    private volatile boolean frozen = false;
    private List<Stage> stages = new ArrayList<>();
    private int currentStageIndex = 0;
    private final Interactive<?> target;
    private boolean stopped;

    /**
     * 获取对话的类型。
     *
     * @return 对话的类型
     */
    public ChatType getType() {
        return type;
    }

    /**
     * 获取对话的目标交互对象。
     *
     * @return 目标交互对象
     */
    public Interactive<?> getTarget() {
        return target;
    }

    /**
     * 构造一个新的对话实例。
     *
     * @param type   对话的类型
     * @param target 对话的目标交互对象
     */
    Chat(ChatType type, Interactive<?> target) {
        this.type = type;
        this.target = target;
    }

    /**
     * 向对话中添加一个交互阶段。
     *
     * @param stage 要添加的交互阶段
     * @throws IllegalStateException 如果对话已被冻结，无法添加阶段
     */
    public void addStage(Stage stage) {
        if (frozen) {
            throw new IllegalStateException("Cannot add stages after the pre-initialization phase!");
        }
        stages.add(stage);
    }

    /**
     * 冻结对话，防止添加更多阶段。
     */
    public void freeze() {
        frozen = true;
    }

    /**
     * 运行当前阶段的交互。
     */
    public void runStage() {
        if (stopped) {
            return;
        }
        target.sendMessage(getCurrentStage().startMessage.get());
    }

    /**
     * 处理收到的消息。
     *
     * @param bot     机器人对象
     * @param source  消息的源交互对象
     * @param sender  消息的发送者交互对象
     * @param message 收到的消息
     * @param time    时间戳
     */
    public void handleMessage(Bot<?> bot, Interactive<?> source, Interactive<?> sender, Message<?> message, int time) {
        if (stopped) {
            return;
        }
        getCurrentStage().stageExecuter.onMessage(this, bot, source, sender, message, time);
    }

    /**
     * 启动对话，运行第一个交互阶段。
     */
    public void start() {
        toStage(0);
    }

    /**
     * 停止对话，释放资源。
     */
    public void stop() {
        this.stages = null;
        this.stopped = true;
        SpCoBot.getInstance().chatDispatcher.stopChat(target, type);
    }

    private Stage getCurrentStage() {
        return this.stages.get(this.currentStageIndex);
    }

    /**
     * 重新运行当前阶段的交互。
     */
    public void replay() {
        runStage();
    }

    /**
     * 跳转到指定索引的交互阶段。
     *
     * @param index 要跳转到的阶段索引
     */
    public void toStage(int index) {
        this.currentStageIndex = index;
        runStage();
    }

    /**
     * 进入下一个交互阶段，如果已经是最后一个阶段，则停止对话。
     */
    public void next() {
        this.currentStageIndex += 1;
        if (this.currentStageIndex == this.stages.size()) {
            this.stop();
        }
        runStage();
    }
}