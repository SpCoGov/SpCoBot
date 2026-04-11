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
package top.spco.qq.message.parsers;

import com.google.gson.JsonObject;
import top.spco.api.message.AtAllMessage;
import top.spco.api.message.AtMessage;
import top.spco.api.message.Message;
import top.spco.qq.message.MessageComponentParser;

public class AtMessageParser extends MessageComponentParser {
    @Override
    public String componentName() {
        return "at";
    }

    @Override
    public Message parse(JsonObject data, JsonObject raw) {
        String at = data.get("qq").getAsString();
        if ("all".equals(at) || "qq".equals(at)) {
            return AtAllMessage.INSTANCE;
        }
        JsonObject textElement = raw.get("textElement").getAsJsonObject();
        String content = textElement.get("content").getAsString();
        return new AtMessage(at, content.substring(1));
    }

    @Override
    public boolean supports(Message message) {
        return message instanceof AtMessage || message instanceof AtAllMessage;
    }

    @Override
    public JsonObject serialize(Message message) {
        JsonObject segment = new JsonObject();
        segment.addProperty("type", componentName());

        JsonObject data = new JsonObject();
        if (message instanceof AtAllMessage) {
            data.addProperty("qq", "all");
        } else {
            AtMessage atMessage = (AtMessage) message;
            data.addProperty("qq", atMessage.getTarget());
        }
        segment.add("data", data);
        return segment;
    }
}
