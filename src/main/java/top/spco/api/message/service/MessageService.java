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
package top.spco.api.message.service;

import org.jetbrains.annotations.Nullable;
import top.spco.api.Image;
import top.spco.api.Interactive;
import top.spco.api.message.Message;
import top.spco.api.message.MessageSource;
import top.spco.util.tuple.ImmutablePair;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 消息服务
 *
 * @author SpCo
 * @version 1.3.0
 * @since 0.1.1
 */
public interface MessageService {
    /**
     * At一位群成员
     *
     * @param id 群成员的Id
     * @return 包含At的Message对象
     */
    Message at(long id);

    /**
     * At全体成员
     *
     * @return 包含At全体成员的Message对象
     */
    Message atAll();

    /**
     * 往消息后添加一条消息
     *
     * @param original 原始消息
     * @param other    待添加的消息
     * @return 操作后的Message对象
     */
    Message append(Message original, Message other);

    /**
     * 往消息后添加一则文本
     *
     * @param original 原始消息
     * @param other    待添加的文本
     * @return 操作后的Message对象
     */
    Message append(Message original, String other);

    String getAtRegex();

    default boolean isAtFormat(String input) {
        String regex = getAtRegex();
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);
        // 创建匹配器
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }

    /**
     * 获取消息所引用的消息
     *
     * @param message 源消息
     * @return 如果有引用时返回被引用的消息，如果没有时返回null
     */
    @Nullable
    ImmutablePair<MessageSource, Message> getQuote(Message message);

    /**
     * 撤回一条消息<p>
     * 当机器人撤回自己的消息时，不需要权限。
     *
     * @param original 需要撤回的消息
     */
    void recall(Message original);

    /**
     * 将字符串转换为 {@code Message} 对象
     *
     * @param content 需要转换的内容
     */
    Message toMessage(String content);

    /**
     * 将文件转换为 {@code Image} 对象
     *
     * @param image       需要转换的图片
     * @param interactive 发送的对象
     */
    Image toImage(File image, Interactive interactive);
}