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
package top.spco.qq.payload;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import top.spco.SpCoBot;
import top.spco.qq.QQNTWebSocketClient;
import top.spco.qq.payload.handler.*;
import top.spco.qq.payload.handler.message.MessageEventHandler;
import top.spco.util.Ansi;

import java.net.http.WebSocket;
import java.util.HashMap;
import java.util.Map;

import static top.spco.qq.payload.handler.ErrorHandler.Handlers.*;

public class NapCatPayloadDispatcher {
    private static final Gson GSON = new Gson();
    private static NapCatPayloadDispatcher instance;

    private final Map<Integer, ErrorHandler> errorHandlers = new HashMap<>();
    private final Map<String, EventTypeHandler> postTypeHandlers = new HashMap<>();

    private NapCatPayloadDispatcher() {
        errorHandlers.put(1403, E_1403);

        postTypeHandlers.put("meta_event", new MetaEventHandler());
        postTypeHandlers.put("message", new MessageEventHandler());
    }

    public static NapCatPayloadDispatcher getInstance() {
        if (instance == null) {
            instance = new NapCatPayloadDispatcher();
        }
        return instance;
    }

    public void onPayloadReceived(QQNTWebSocketClient client, WebSocket webSocket, String raw) {
        JsonObject payload = GSON.fromJson(raw, JsonObject.class);
        boolean isResponse = payload.has("post_type");
        if (!isResponse) {
            String status = payload.get("status").getAsString();
            int retCode = payload.get("retcode").getAsInt();
            String echo = null;
            if (payload.has("echo")) {
                JsonElement element = payload.get("echo");
                if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                    echo = element.getAsString();
                }
            }
            if (retCode != 0) {
                String wording = payload.get("wording").getAsString();
                if (errorHandlers.containsKey(retCode)) {
                    errorHandlers.get(retCode).onError(client, webSocket, status, retCode, wording);
                    return;
                }
                SpCoBot.LOGGER.error("未处理的错误：{}({})，echo：{}，status：{}", wording, retCode, echo, status);
                return;
            }
            SpCoBot.LOGGER.info(Ansi.BRIGHT_PURPLE + "{}", raw);
        } else {
            String postType = payload.get("post_type").getAsString();
            if (postTypeHandlers.containsKey(postType)) {
                EventTypeHandler postTypeHandler = postTypeHandlers.get(postType);
                String subtype = payload.get(postTypeHandler.getSubtypeKeyName()).getAsString();
                if (postTypeHandler.hasHandler(subtype)) {
                    PostPayloadHandler handler = postTypeHandler.getPayloadHandler(subtype);
                    handler.onPayload(client, webSocket, payload);
                    return;
                } else {
                    SpCoBot.LOGGER.warn("未处理的推送事件子类型：" + "{}", subtype);
                }
            } else {
                SpCoBot.LOGGER.warn("未处理的推送事件类型：" + "{}", postType);
            }
            SpCoBot.LOGGER.info(Ansi.BRIGHT_BLUE + "{}", raw);
        }
    }
}