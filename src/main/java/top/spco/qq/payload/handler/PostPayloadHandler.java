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
package top.spco.qq.payload.handler;

import com.google.gson.JsonObject;
import top.spco.SpCoBot;
import top.spco.qq.NapCatWebSocketClient;

import java.net.http.WebSocket;

public interface PostPayloadHandler {
    PostPayloadHandler EMPTY = (client, webSocket, payload) -> {
    };

    PostPayloadHandler PRINT = (client, webSocket, payload) -> {
        SpCoBot.LOGGER.info(payload.toString());
    };

    void onPayload(NapCatWebSocketClient client, WebSocket webSocket, JsonObject payload);
}