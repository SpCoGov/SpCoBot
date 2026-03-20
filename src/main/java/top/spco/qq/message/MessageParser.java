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

import com.google.gson.JsonObject;
import top.spco.SpCoBot;
import top.spco.api.message.Message;
import top.spco.api.message.UnsupportedMessage;
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
    }

    public void register(MessageComponentParser parser) {
        componentsParsers.put(parser.componentName(), parser);
    }

    public Message parse(JsonObject object) {
        String type = JsonUtil.getAsString(object, "type");
        if (componentsParsers.containsKey(type)) {
            JsonObject data = object.get("data").getAsJsonObject();
            return componentsParsers.get(type).parse(data);
        }
        SpCoBot.LOGGER.warn("消息解析器发现不支持的消息类型：{}", type);
        return UnsupportedMessage.INSTANCE;
    }

}