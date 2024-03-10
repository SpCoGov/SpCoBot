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
package top.spco.service.mcs;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.spco.SpCoBot;
import top.spco.api.Group;
import top.spco.api.message.Message;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 表示一个通过 MSSBB(<b>M</b>inecraft<b>S</b>ever<b>S</b>pCo<b>B</b>ot<b>B</b>ridge Minecraft服务器-SpCoBot桥接器) 连接的Minecraft服务器
 *
 * @author SpCo
 * @version 2.0.3
 * @since 2.0.3
 */
public class McS {
    private String host;
    private int port;
    private Socket socket;
    private PrintWriter out;
    private ScheduledExecutorService heartbeat;
    private int syn;
    private int heartbeatInterval;
    private String name;
    private final Group<?> group;
    private Logger LOGGER;
    private final Map<Integer, Message<?>> commandCaller = new HashMap<>();

    public McS(String host, int port, Group<?> group) throws IOException {
        this.host = host;
        this.port = port;
        this.group = group;
        socket = new Socket(host, port);
        this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        new Thread(() -> {
            while (true) {
                try {
                    byte[] buffer = new byte[1024];
                    int bytesRead = this.socket.getInputStream().read(buffer);
                    if (bytesRead == -1) {
                        continue;
                    }
                    String message = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
                    Payload pl = Payload.fromJson(message);
                    switch (pl.getOperationCode()) {
                        case 0 -> {
                            JsonObject data = (JsonObject) pl.getData();
                            send(new Payload(3, new JsonPrimitive(SpCoBot.getInstance().botId + "-" + host + ":" + port), "IDENTIFY"));
                            heartbeatInterval = data.get("heartbeat_interval").getAsInt();
                            name = data.get("name").getAsString();
                            startHeartbeat();
                            Thread.currentThread().setName(name);
                            LOGGER = LogManager.getLogger("MsC-" + name);
                        }
                        case 2 -> {
                            switch (pl.getType()) {
                                case "DISPATCH" -> {
                                    JsonObject data = (JsonObject) pl.getData();
                                    String eventType = data.get("type").getAsString();
                                    switch (eventType) {
                                        case "CHAT" -> {
                                            String playerName = data.get("sender_name").getAsString();
                                            String chatMessage = data.get("message").getAsString();
                                            this.group.sendMessage(playerName + ": " + chatMessage);
                                            LOGGER.info("玩家{}发送了一条消息：{}。", playerName, chatMessage);
                                        }
                                        case "PLAYER_LOGGED_IN" -> {
                                            String playerName = data.get("player_name").getAsString();
                                            this.group.sendMessage(playerName + "加入了服务器。");
                                            LOGGER.info("玩家{}加入了服务器。", playerName);
                                        }
                                        case "PLAYER_LOGGED_OUT" -> {
                                            String playerName = data.get("player_name").getAsString();
                                            this.group.sendMessage(playerName + "退出了服务器。");
                                            LOGGER.info("玩家{}退出了服务器。", playerName);
                                        }
                                    }
                                }
                                case "REPLY" -> {
                                    JsonObject data = (JsonObject) pl.getData();
                                    int ack = data.get("ack").getAsInt();
                                    this.group.quoteReply(commandCaller.get(ack), data.get("result").getAsString());
                                    commandCaller.remove(ack);
                                }
                            }

                        }
                    }
                } catch (IOException e) {
                    stopHeartBeat();
                    close();
                    this.group.sendMessage("绑定的Minecraft服务器已离线");
                }
            }
        }).start();
    }

    public int executeCommand(String command, Message<?> callerMessage) {
        JsonObject data = new JsonObject();
        data.addProperty("type", "CALL_COMMAND");
        data.addProperty("command", command);
        data.addProperty("syn", syn++);
        send(new Payload(5, data, "REQUEST"));
        commandCaller.put(syn - 1, callerMessage);
        return syn - 1;
    }

    public void close() {
        stopHeartBeat();
        try {
            this.socket.close();
            this.out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void stopHeartBeat() {
        if (heartbeat != null && !heartbeat.isShutdown()) {
            heartbeat.shutdown();
        }
    }

    private void startHeartbeat() {
        if (heartbeatInterval != 0) {
            heartbeat = Executors.newScheduledThreadPool(1);
            heartbeat.scheduleAtFixedRate(this::sendHeartbeat, 0, heartbeatInterval, TimeUnit.MILLISECONDS);
        }
    }

    private void sendHeartbeat() {
        this.send(Payload.heartbeat());
    }

    public void send(Payload pl) {
        out.println(pl.toString());
        out.flush();
    }
}