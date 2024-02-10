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
package top.spco.core;

import top.spco.SpCoBot;
import top.spco.events.CAATPEvents;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * CAATP
 *
 * @author SpCo
 * @version 2.0.1
 * @since 0.1.0
 */
public class CAATP {
    private static final int OPERATION_INTERVAL = 5;
    private static CAATP instance;
    private int reconnectionAttempts = 0;
    private ScheduledExecutorService heartbeat;
    private Socket socket;
    private PrintWriter out;
    private volatile boolean isConnected = false;
    private static boolean registered = false;

    private CAATP() {
        if (registered) {
            return;
        }
        registered = true;
        CAATPEvents.RECEIVE.register(message -> {
            if (message.equals("hello")) {
                this.sendMessage("register qqspcobot");
                startHeartbeat();
                return;
            }
            SpCoBot.LOGGER.info("收到CAATP发送的消息: {}", message);
            if (message.equals("connected")) {
                this.isConnected = true;
            }
        });
        new Thread(() -> {
            while (true) {
                try {
                    socket = new Socket("192.168.50.2", 8900);
                    isConnected = true;
                    reconnectionAttempts = 0;
                    CAATPEvents.CONNECT.invoker().onConnect();
                    SpCoBot.LOGGER.info("已连接到CAATP。");
                    this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
                    while (true) {
                        byte[] buffer = new byte[1024];
                        int bytesRead = getInstance().socket.getInputStream().read(buffer);
                        if (bytesRead == -1) {
                            continue;
                        }
                        String message = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
                        CAATPEvents.RECEIVE.invoker().onReceive(message);
                    }
                } catch (IOException e) {
                    CAATPEvents.DISCONNECT.invoker().onDisconnect();
                    stopHeartBeat();
                    SpCoBot.LOGGER.info("无法连接至CAATP: {}, 将在{}秒后重连。", e.getMessage(), nextOperationInterval());
                    isConnected = false;
                    reconnectionAttempts++;
                    try {
                        TimeUnit.SECONDS.sleep(nextOperationInterval()); // 等待一段时间后重试连接
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }, "CAATP").start();
    }

    public boolean isConnected() {
        return isConnected;
    }

    private int nextOperationInterval() {
        if (reconnectionAttempts >= 5) {
            return OPERATION_INTERVAL << 5;
        }
        return OPERATION_INTERVAL << reconnectionAttempts;
    }

    /**
     * 向服务端发送消息。
     *
     * @param message 要发送的消息字符串。
     */
    public void sendMessage(String message) {
        try {
            out.println(message);
            // 刷新输出流，确保消息被发送
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 开始心跳线程
     */
    private void startHeartbeat() {
        heartbeat = Executors.newScheduledThreadPool(1);
        heartbeat.scheduleAtFixedRate(this::sendHeartbeat, 0, 5000L, TimeUnit.MICROSECONDS);
    }

    /**
     * 停止心跳线程
     */
    private void stopHeartBeat() {
        if (heartbeat != null && !heartbeat.isShutdown()) {
            heartbeat.shutdown();
        }
    }

    private void sendHeartbeat() {
        if (isConnected) {
            this.sendMessage("heart");
        }
    }

    public synchronized static CAATP getInstance() {
        if (instance == null) {
            instance = new CAATP();
        }
        return instance;
    }
}