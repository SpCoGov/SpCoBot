/*
 * Copyright 2025 SpCo
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
package top.spco.service.mcs;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import top.spco.SpCoBot;

/**
 * {@code Payload} represents a message carrier
 * <p>The list of all opcodes is as follows:
 *
 * <table border="1">
 *     <tr>
 *         <th>Code</th>
 *         <th>Name</th>
 *         <th>ClientAction</th>
 *         <th>Description</th>
 *     </tr>
 *     <tr>
 *         <td>0</td>
 *         <td>Hello</td>
 *         <td>Receive</td>
 *         <td>When the client connects to the server, the first message sent by the server</td>
 *     </tr>
 *     <tr>
 *         <td>1</td>
 *         <td>Heartbeat</td>
 *         <td>Send</td>
 *         <td>Client sends heartbeat</td>
 *     </tr>
 *     <tr>
 *         <td>2</td>
 *         <td>Dispatch</td>
 *         <td>Receive</td>
 *         <td>Server-side message push</td>
 *     </tr>
 *     <tr>
 *         <td>3</td>
 *         <td>Identify</td>
 *         <td>Send</td>
 *         <td>Client sends authentication</td>
 *     </tr>
 *     <tr>
 *         <td>4</td>
 *         <td>Invalid Session</td>
 *         <td>Receive</td>
 *         <td>When identifying, if the parameters are wrong, the server will return this message</td>
 *     </tr>
 *     <tr>
 *         <td>5</td>
 *         <td>Request</td>
 *         <td>Send</td>
 *         <td>Client sends request</td>
 *     </tr>
 * <tr>
 *         <td>6</td>
 *         <td>Heartbeat ACK</td>
 *         <td>Receive</td>
 *         <td>When the heartbeat is sent successfully, the message will be received</td>
 *     </tr>
 * </table>
 *
 * @author SpCo
 * @version 2.0.7
 * @since 2.0.3
 */
public class Payload {
    private static final Gson GSON = new Gson();

    /**
     * 操作码，用于指示消息的操作类型，具体含义依赖于连接维护的 opcode 列表。
     */
    @SerializedName("op")
    private final int opCode;

    /**
     * 事件内容，根据不同的事件类型，事件内容的格式可能不同。
     */
    @SerializedName("d")
    private final JsonElement data;

    /**
     * 事件类型。
     */
    @SerializedName("t")
    private final String type;

    public Payload(int opCode, JsonElement data, String type) {
        this.opCode = opCode;
        this.data = data;
        this.type = type;
    }

    public int getOperationCode() {
        return opCode;
    }

    public String getType() {
        return type;
    }

    public JsonElement getData() {
        return data;
    }

    /**
     * 从 JSON 字符串创建 Payload 实例。
     *
     * @param jsonString 要解析的 JSON 字符串
     * @return 解析后的 Payload 实例
     */
    public static ImmutableList<Payload> fromJson(String jsonString) {
        try {
            StringBuilder jsonBuilder = new StringBuilder();
            int depth = 0;
            ImmutableList.Builder<Payload> builder = new ImmutableList.Builder<>();
            for (int i = 0; i < jsonString.length(); i++) {
                char c = jsonString.charAt(i);
                jsonBuilder.append(c);
                if (i == 0 || jsonString.charAt(i - 1) != '/') {
                    switch (c) {
                        case '{' -> depth++;
                        case '}' -> depth--;
                    }
                }

                if (depth == 0) {
                    String json = jsonBuilder.toString();
                    builder.add(GSON.fromJson(json, Payload.class));
                    jsonBuilder.setLength(0);
                }
            }
            return builder.build();
        } catch (Exception e) {
            SpCoBot.LOGGER.error("解析Payload失败，被解析的文本：{}", jsonString);
            throw e;
        }
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static Payload heartbeat(int syn) {
        JsonObject data = new JsonObject();
        data.addProperty("syn", syn);
        return new Payload(1,data, "HEARTBEAT");
    }
}