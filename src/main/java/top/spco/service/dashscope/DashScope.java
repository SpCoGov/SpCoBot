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
package top.spco.service.dashscope;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.common.MessageManager;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import org.jetbrains.annotations.NotNull;
import top.spco.SpCoBot;
import top.spco.api.Interactive;
import top.spco.api.message.Message;
import top.spco.user.BotUser;
import top.spco.util.tuple.MutablePair;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用户个人的DashScope
 *
 * @author SpCo
 * @version 2.0.0
 * @since 0.2.1
 */
public class DashScope {
    private final long userId;
    private int timer;
    private Generation generation;
    private MessageManager msgManager;
    private final MutablePair<Interactive<?>, Message<?>> lastMessage = new MutablePair<>();

    public DashScope(BotUser user, Interactive<?> from, Message<?> message) {
        this.userId = user.getId();
        setLastMessage(from, message);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, new ThreadFactory() {
            private final AtomicInteger threadNumber = new AtomicInteger(1);

            @Override
            public Thread newThread(@NotNull Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("DashScopeTimer-" + threadNumber.getAndIncrement());
                return thread;
            }
        });
        // 创建一个定时任务，每隔1秒执行一次
        scheduler.scheduleAtFixedRate(() -> {
            if (timer > 0) {
                timer--;
            } else {
                close();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void setLastMessage(Interactive<?> from, Message<?> lastMessage) {
        this.lastMessage.setLeft(from);
        this.lastMessage.setRight(lastMessage);
    }

    public void close() {
        if (generation != null || msgManager != null) {
            if (generation != null) {
                lastMessage.getLeft().quoteReply(lastMessage.getRight(), "Chat(" + generation.toString().replaceAll("com.alibaba.dashscope.aigc.generation.Generation@", "") + ") released!");
            }
            msgManager = null;
            generation = null;
            SpCoBot.getInstance().dashScopeDispatcher.remove(userId);
        }
    }

    public GenerationResult callWithMessage(String content, int maxMessages) throws NoApiKeyException, ApiException, InputRequiredException {
        // 如果三分钟秒内没有对话，则对话会被自动关闭
        timer = 180;
        // 检测是否有对话存在，没有则创建一个新对话
        if (generation == null || msgManager == null) {
            generation = new Generation();
            lastMessage.getLeft().quoteReply(lastMessage.getRight(), "Chat(" + generation.toString().replaceAll("com.alibaba.dashscope.aigc.generation.Generation@", "") + ") created!");
            msgManager = new MessageManager(maxMessages);
            com.alibaba.dashscope.common.Message systemMsg = com.alibaba.dashscope.common.Message.builder().role(Role.SYSTEM.getValue()).content("所有问题请用汉语回答。").build();
            msgManager.add(systemMsg);
        }
        com.alibaba.dashscope.common.Message userMsg = com.alibaba.dashscope.common.Message.builder().role(Role.USER.getValue()).content(content).build();
        msgManager.add(userMsg);
        QwenParam param = QwenParam.builder().model(Generation.Models.QWEN_PLUS).messages(msgManager.get()).resultFormat(QwenParam.ResultFormat.MESSAGE).topP(0.8).enableSearch(true).build();
        GenerationResult result = generation.call(param);
        msgManager.add(result);

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DashScope dashScope = (DashScope) o;
        return userId == dashScope.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}