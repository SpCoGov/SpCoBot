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
package top.spco;

import top.spco.events.CAATPEvents;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Created on 2023/10/29 0029 20:35
 * <p>
 *
 * @author SpCo
 * @version 1.0
 * @since 1.0
 */
public class CAATP {
    private static CAATP instance;
    private final String caatpAddr = "192.168.50.2";
    private final int caatpPort = 8900;
    // 重连间隔, 单位为毫秒
    private final int operationInterval = 5;
    private Socket socket;
    private PrintWriter out;
    private boolean isConnected = false;
    private volatile boolean stopRequested = false;
    private final Thread receiveThread = new Thread(() -> {
        while (!stopRequested) {
            try {
                while (true) {
                    byte[] buffer = new byte[1024];
                    int bytesRead = socket.getInputStream().read(buffer);
                    if (bytesRead == -1) {
                        continue;
                    }
                    String message = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
                    CAATPEvents.RECEIVE.invoker().onReceive(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    });

    private CAATP() {
        CAATPEvents.RECEIVE.register(message -> {
            SpCoBot.logger.info("收到CAATP发送的消息: " + message);
            if (message.equals("hello")) {
                this.sendMessage("register qqspcobot");
            }
            if (message.equals("connected")) {
                this.isConnected = true;
            }
        });
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
        executor.execute(this::autoReconnect);
        executor.scheduleAtFixedRate(this::sendHeartbeat, 0, operationInterval, TimeUnit.SECONDS);
    }

    public boolean isConnected() {
        return isConnected;
    }

    private synchronized void autoReconnect() {

        isConnected = false;
        while (true)
            try {
                SpCoBot.logger.info("开始尝试连接CAATP.");
                this.socket = new Socket(this.caatpAddr, this.caatpPort);
                this.out = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream(), StandardCharsets.UTF_8), true);
                isConnected = true;
                CAATPEvents.CONNECT.invoker().onConnect();

                // 停止 receiveThread（如果它正在运行）
                if (receiveThread.isAlive()) {
                    stopRequested = true; // 设置停止标志
                    receiveThread.join(); // 等待 receiveThread 完全停止
                }

                receiveThread.start();

                break;
            } catch (IOException e) {
                stopRequested = true;
                isConnected = false;
                SpCoBot.logger.info("无法连接至CAATP: " + e.getMessage() + ", 将在5秒后重连.");
                try {
                    TimeUnit.SECONDS.sleep(operationInterval); // 等待一段时间后重试连接
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

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

    private void sendHeartbeat() {
        if (isConnected) {
            this.sendMessage("heart");
        }
    }

    public static CAATP getInstance() {
        if (instance == null) {
            instance = new CAATP();
        }
        return instance;
    }
}