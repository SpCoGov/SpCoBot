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
import com.google.gson.JsonObject;
import top.spco.SpCoBot;
import top.spco.api.message.Message;
import top.spco.api.message.MessageChain;
import top.spco.api.message.MessageSource;
import top.spco.events.MessageEvents;
import top.spco.qq.NapCatWebSocketClient;
import top.spco.qq.QQBot;
import top.spco.qq.message.MessageParser;
import top.spco.qq.message.ReplyMessage;
import top.spco.qq.napcat.NapCatMessageSource;
import top.spco.qq.napcat.NapCatUser;
import top.spco.qq.payload.handler.PostPayloadHandler;

import java.net.http.WebSocket;
import java.util.ArrayList;

import static top.spco.util.JsonUtil.getAsLong;
import static top.spco.util.JsonUtil.getAsString;

public class PrivateMessagePayloadHandler implements PostPayloadHandler {
    @Override
    public void onPayload(NapCatWebSocketClient client, WebSocket webSocket, JsonObject payload) {
        String botId = getAsString(payload, "self_id");
        QQBot bot = new QQBot("SpCoBot", botId);
        JsonObject senderJsonObject = payload.get("sender").getAsJsonObject();
        int time = getAsLong(payload, "time").intValue();
        String senderId = getAsString(payload, "user_id");
        String senderNickName = getAsString(senderJsonObject, "nickname");
        String messageId = getAsString(payload, "message_id");
        JsonArray elements = payload.get("message").getAsJsonArray();

        NapCatUser sender = new NapCatUser(senderId, senderNickName);

        JsonArray elementsRaw = payload.get("raw").getAsJsonObject().get("elements").getAsJsonArray();
        ArrayList<Message> messageComponents = new ArrayList<>();
        MessageSource replySource = null;
        for (int i = 0; i < elements.size(); i++) {
            JsonObject element = elements.get(i).getAsJsonObject();
            JsonObject elementRaw = elementsRaw.get(i).getAsJsonObject();
            Message component = MessageParser.getInstance().parse(element, elementRaw, senderId);

            if (component instanceof ReplyMessage replyMessage) {
                replySource = new NapCatMessageSource(replyMessage.getSenderId(), replyMessage.getFromId(), replyMessage.getReplyId());
            } else {
                messageComponents.add(component);
            }
        }
        MessageChain messageChain = new MessageChain(messageComponents);
        if (replySource != null) {
            messageChain.setReplySource(replySource);
        }
        MessageSource source = new NapCatMessageSource(senderId, senderId, messageId);
        messageChain.setSource(source);

        MessageEvents.PRIVATE_MESSAGE.invoker().onPrivateMessage(bot, sender, messageChain, time);
    }
}
