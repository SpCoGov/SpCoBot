/*
 * Copyright 2026 SpCo
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
package top.spco.qq.message;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import top.spco.SpCoBot;
import top.spco.api.message.*;
import top.spco.qq.message.parsers.AtMessageParser;
import top.spco.qq.message.parsers.ReplyMessageParser;
import top.spco.qq.message.parsers.TextMessageParser;
import top.spco.util.JsonUtil;

import java.util.HashMap;

public class MessageParser {
    private static MessageParser instance;

    public static MessageParser getInstance() {
        if (instance == null) {
            instance = new MessageParser();
        }
        return instance;
    }

    private final HashMap<String, MessageComponentParser> componentsParsers = new HashMap<>();

    private MessageParser() {
        register(new TextMessageParser());
        register(new AtMessageParser());
        register(new ReplyMessageParser());
    }

    public void register(MessageComponentParser parser) {
        componentsParsers.put(parser.componentName(), parser);
    }

    public Message parse(JsonObject element, JsonObject raw, String fromId) {
        String type = JsonUtil.getAsString(element, "type");
        if (componentsParsers.containsKey(type)) {
            JsonObject data = element.get("data").getAsJsonObject();
            return componentsParsers.get(type).parse(data, raw, fromId);
        }
        SpCoBot.LOGGER.warn("消息解析器发现不支持的消息类型：{}", type);
        return UnsupportedMessage.INSTANCE;
    }

    /**
     * 将单个消息对象序列化为 QQ/NapCat 消息段。
     *
     * @param message 待序列化的消息对象
     * @return 消息段 JsonObject
     */
    public JsonObject serialize(Message message) {
        for (MessageComponentParser parser : componentsParsers.values()) {
            if (parser.supports(message)) {
                return parser.serialize(message);
            }
        }
        SpCoBot.LOGGER.warn("消息解析器发现暂不支持序列化的消息类型：{}，将退化为文本消息", message.getClass().getName());
        return componentsParsers.get("text").serialize(new TextMessage(message.toMessageContext()));
    }

    /**
     * 将一条消息链序列化为 QQ/NapCat 消息段数组。
     *
     * @param messageChain 待序列化消息链
     * @return 消息段数组
     */
    public JsonArray serialize(MessageChain messageChain) {
        JsonArray result = new JsonArray();
        if (messageChain.getReplySource() != null) {
            MessageSource replySource = messageChain.getReplySource();
            result.add(serialize(new ReplyMessage(replySource.getMessageId(), replySource.getSenderId(), replySource.getFromId())));
        }
        for (Message component : messageChain.getComponents()) {
            result.add(serialize(component));
        }
        return result;
    }
}
