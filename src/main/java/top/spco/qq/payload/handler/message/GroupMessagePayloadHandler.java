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
package top.spco.qq.payload.handler.message;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import top.spco.api.message.Message;
import top.spco.qq.QQNTWebSocketClient;
import top.spco.qq.message.MessageParser;
import top.spco.qq.payload.handler.PostPayloadHandler;

import java.net.http.WebSocket;
import java.util.ArrayList;

import static top.spco.util.JsonUtil.*;

public class GroupMessagePayloadHandler implements PostPayloadHandler {
    @Override
    public void onPayload(QQNTWebSocketClient client, WebSocket webSocket, JsonObject payload) {
        String botId = getAsString(payload, "self_id");
        JsonObject senderJsonObject = payload.get("sender").getAsJsonObject();
        String senderId = getAsString(senderJsonObject, "user_id");
        String senderNickName = getAsString(senderJsonObject, "nickname");
        String role = getAsString(senderJsonObject, "role");
        String messageId = getAsString(payload, "message_id");
        JsonArray messageComponentsArray = payload.get("message").getAsJsonArray();
        String groupId = getAsString(payload, "group_id");
        String groupName = getAsString(payload, "group_name");

        ArrayList<Message> messageComponents = new ArrayList<>();
        for (JsonElement element: messageComponentsArray) {
            JsonObject o = element.getAsJsonObject();
            messageComponents.add(MessageParser.getInstance().parse(o));
        }
    }
}