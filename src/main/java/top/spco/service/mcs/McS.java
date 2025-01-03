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

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import top.spco.SpCoBot;
import top.spco.api.Group;
import top.spco.api.message.Message;
import top.spco.util.ExceptionUtil;
import top.spco.util.TimeUtil;
import top.spco.util.tuple.MutablePair;
import top.spco.util.tuple.Pair;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * 表示一个通过 MSSBB(<b>M</b>inecraft<b>S</b>ever<b>S</b>pCo<b>B</b>ot<b>B</b>ridge Minecraft服务器-SpCoBot桥接器) 连接的Minecraft服务器
 *
 * @author SpCo
 * @version 3.2.3
 * @since 2.0.3
 */
public class McS {
    private final Socket socket;
    private final PrintWriter out;
    private ScheduledExecutorService heartbeat;
    private int syn;
    private int heartbeatInterval;
    private String name = "undefined";
    private final Group<?> group;
    private final Map<Integer, Message<?>> commandCaller = new ConcurrentHashMap<>();
    private final Set<Integer> heartbeats = new HashSet<>();
    private final Set<Integer> timeoutHeartbeats = new HashSet<>();
    private int timeoutCount;
    private final Map<Integer, String> commandResults = new ConcurrentHashMap<>();
    /*
     * Pair L : 第一条消息返回的时间
     * Pair R : 最后一条消息返回的时间
     */
    private final Map<Integer, Pair<Long, Long>> commandReceivingTime = new HashMap<>();
    private final AtomicBoolean silence = new AtomicBoolean(false);
    private final boolean hasCaller;
    private boolean debug = false;
    private boolean connected = false;
    private final Message<?> callerMessage;

    public McS(String host, int port, Group<?> group, @Nullable Message<?> callerMessage, boolean afterHeartbeatTimeout) throws IOException {
        this.group = group;
        hasCaller = callerMessage != null;
        this.callerMessage = callerMessage;
        if (hasCaller && !afterHeartbeatTimeout) {
            group.sendMessage(SpCoBot.getInstance().getMessageService().asMessage("开始尝试连接到Minecraft服务器：" + host + ":" + port).quoteReply(callerMessage));
        }
        socket = new Socket(host, port);
        this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

        new Thread(() -> {
            try {
                while (true) {
                    byte[] buffer = new byte[4096];
                    int bytesRead = this.socket.getInputStream().read(buffer);
                    if (bytesRead == -1) {
                        continue;
                    }
                    String message = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
                    if (debug) {
                        group.sendMessage("收到来自McS的消息：" + message);
                    }
                    Iterable<Payload> pls = Payload.fromJson(message);
                    for (Payload pl : pls) {
                        switch (pl.getOperationCode()) {
                            case 0 -> {
                                JsonObject data = (JsonObject) pl.getData();
                                send(new Payload(3, new JsonPrimitive(SpCoBot.getInstance().botId + "-" + host + ":" + port), "IDENTIFY"));
                                heartbeatInterval = data.get("heartbeat_interval").getAsInt();
                                name = data.get("name").getAsString();
                                startHeartbeat();
                                Thread.currentThread().setName("MsC-" + name);
                                connected = true;
                                McSManager.getInstance().getAllRegistered().put(group.getId(), this);
                                if (hasCaller) {
                                    group.sendMessage(SpCoBot.getInstance().getMessageService().asMessage("连接成功").quoteReply(callerMessage));
                                }
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
                                                SpCoBot.LOGGER.info("玩家{}发送了一条消息：{}", playerName, chatMessage);
                                            }
                                            case "PLAYER_LOGGED_IN" -> {
                                                String playerName = data.get("player_name").getAsString();
                                                this.group.sendMessage(playerName + "加入了服务器");
                                                SpCoBot.LOGGER.info("玩家{}加入了服务器", playerName);
                                            }
                                            case "PLAYER_LOGGED_OUT" -> {
                                                String playerName = data.get("player_name").getAsString();
                                                this.group.sendMessage(playerName + "退出了服务器");
                                                SpCoBot.LOGGER.info("玩家{}退出了服务器", playerName);
                                            }
                                        }
                                    }
                                    case "REPLY" -> {
                                        JsonObject data = (JsonObject) pl.getData();
                                        int ack = data.get("ack").getAsInt();
                                        // 判断是否为命令的第一条返回值
                                        boolean firstResult = commandResults.get(ack) == null;
                                        // 如果是第一条消息，则设置为""，否则从缓存中获取
                                        String results = firstResult ? "" : (commandResults.get(ack) + "\n");
                                        // 添加当前消息
                                        results += data.get("result").getAsString();
                                        // 更新缓存
                                        commandResults.put(ack, results);
                                        // 设置定时器用于清理消息
                                        if (firstResult) {
                                            // 如果是第一条消息
                                            commandReceivingTime.put(ack, new MutablePair<>(System.nanoTime(), System.nanoTime()));
                                            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                                            Message<?> caller = commandCaller.get(ack);
                                            // 指定毫秒后发送命令返回值
                                            int delay = 100;
                                            scheduler.schedule(() -> {
                                                if (debug) {
                                                    group.sendMessage(ack + "的销毁任务开始");
                                                }
                                                // 获取50ms后的时间信息
                                                var time = commandReceivingTime.get(ack);
                                                long nanos = time.getValue() - time.getKey();
                                                TimeUnit unit = TimeUtil.chooseUnit(nanos);
                                                double value = (double) nanos / NANOSECONDS.convert(1, unit);
                                                commandReceivingTime.remove(ack);
                                                String finalMessage = commandResults.get(ack);
                                                if (nanos != 300) {
                                                    finalMessage = finalMessage + "\n\n" + "命令耗时：" + String.format(Locale.ROOT, "%.4g", value) + " " + TimeUtil.abbreviate(unit);
                                                }
                                                if (caller == null) {
                                                    this.group.sendMessage(finalMessage + "\n\n命令超时响应，原消息信息已被清理");
                                                } else {
                                                    this.group.quoteReply(caller, finalMessage);
                                                }
                                                commandResults.remove(ack);
                                            }, delay, TimeUnit.MILLISECONDS);
                                            scheduler.shutdown();
                                        } else {
                                            // 如果不是第一条消息，更新收到消息的最后时间
                                            if (commandReceivingTime.get(ack) != null) {
                                                commandReceivingTime.get(ack).setValue(System.nanoTime());
                                            }
                                        }
                                    }
                                }
                            }
                            case 6 -> {
                                JsonObject data = (JsonObject) pl.getData();
                                int ack = data.get("ack").getAsInt();
                                heartbeats.remove(ack);
                                if (timeoutHeartbeats.contains(ack)) {
                                    timeoutHeartbeats.remove(ack);
                                    timeoutCount--;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                SpCoBot.LOGGER.error(ExceptionUtil.getStackTraceAsString(e));
                stopHeartBeat();
                close(false, "错误发生：" + e.getMessage());
            }

        }).start();
    }

    public int executeCommand(String command, Message<?> callerMessage) {
        int payloadSyn = syn++;
        JsonObject data = new JsonObject();
        data.addProperty("type", "CALL_COMMAND");
        data.addProperty("command", command);
        data.addProperty("syn", payloadSyn);
        send(new Payload(5, data, "REQUEST"));
        commandCaller.put(payloadSyn, callerMessage);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            this.commandCaller.remove(payloadSyn);
        }, 1, TimeUnit.MINUTES);
        scheduler.shutdown();
        return payloadSyn;
    }

    public void close(boolean silence, String message, boolean toSilence) {
        connected = false;
        stopHeartBeat();
        McSManager.getInstance().getAllRegistered().remove(this.group.getId());
        try {
            this.socket.close();
            this.out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!silence && !this.silence.get()) {
            setSilence(toSilence);
            this.group.sendMessage("绑定的Minecraft服务器已离线 (" + message + ")");

        }
    }

    public void close(boolean silence, String message) {
        connected = false;
        stopHeartBeat();
        McSManager.getInstance().getAllRegistered().remove(this.group.getId());
        try {
            this.socket.close();
            this.out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!silence && !this.silence.get()) {
            this.group.sendMessage("绑定的Minecraft服务器已离线 (" + message + ")");
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
        int heartbeatSyn = syn++;
        this.send(Payload.heartbeat(heartbeatSyn));
        heartbeats.add(heartbeatSyn);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            if (heartbeats.contains(heartbeatSyn)) {
                timeoutHeartbeats.add(heartbeatSyn);
                if (++timeoutCount >5) {
                    this.close(true, "心跳超时", true);
                    try {
                        McSManager.getInstance().connect(group, null, true);
                    } catch (IOException e) {
                        if (hasCaller) {
                            group.quoteReply(callerMessage, "绑定的Minecraft服务器已离线 (心跳超时)，且重连失败。");
                        } else {
                            group.sendMessage("绑定的Minecraft服务器已离线 (心跳超时)，且重连失败。");
                        }
                    }
                }
            }
        }, 3, TimeUnit.SECONDS);
        scheduler.shutdown();
    }

    public void setSilence(boolean silence) {
        this.silence.set(silence);
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean toggleDebug() {
        debug = !debug;
        return debug;
    }

    public boolean isConnected() {
        return connected;
    }

    public void send(Payload pl) {
        out.println(pl.toString());
        out.flush();
        if (debug) {
            group.sendMessage("向McS发送消息：" + pl);
        }
    }
}