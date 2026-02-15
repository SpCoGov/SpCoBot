package top.spco.test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * WebSocket手动联调测试：
 * 1. 连接到 ws://127.0.0.1:3001
 * 2. 每30000ms发送一次心跳Ping
 * 3. 连接失败或断开后自动重连（指数退避，最大30s）
 * 4. 打印所有接收到的封包（TEXT/BINARY/PING/PONG/CLOSE）
 */
public class WebSocketClientTest {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_LOCAL = "\u001B[33m";
    private static final String ANSI_RECV = "\u001B[36m";
    @Test
    public void runWebSocketClientTest() throws InterruptedException {
        ReconnectableWebSocketClient client = new ReconnectableWebSocketClient("127.0.0.1", 3001, 30000L);
        client.start();
        Thread.sleep(TimeUnit.MINUTES.toMillis(5));
        client.stop();
    }

    static final class ReconnectableWebSocketClient implements WebSocket.Listener {
        private static final long INITIAL_BACKOFF_MS = 1000L;
        private static final long MAX_BACKOFF_MS = 30000L;
        private static final String AUTHORIZATION_HEADER = "Bearer 4nKq~U-F3bFlzs8~";
        private static final String HEARTBEAT_PAYLOAD = "heartbeat";

        private final URI uri;
        private final long heartbeatIntervalMs;
        private final HttpClient httpClient;
        private final ScheduledExecutorService scheduler;

        private final AtomicBoolean started = new AtomicBoolean(false);
        private final AtomicBoolean reconnectScheduled = new AtomicBoolean(false);
        private final AtomicInteger reconnectAttempts = new AtomicInteger(0);

        private volatile WebSocket webSocket;
        private volatile ScheduledFuture<?> heartbeatTask;

        ReconnectableWebSocketClient(String host, int port, long heartbeatIntervalMs) {
            this.uri = URI.create("ws://" + host + ":" + port);
            this.heartbeatIntervalMs = heartbeatIntervalMs;
            this.httpClient = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();
            this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "WebSocket-Test-Client");
                t.setDaemon(true);
                return t;
            });
        }

        void start() {
            if (!started.compareAndSet(false, true)) {
                return;
            }
            connect();
        }

        void stop() {
            started.set(false);
            cancelHeartbeat();
            WebSocket ws = webSocket;
            webSocket = null;
            if (ws != null && !ws.isOutputClosed()) {
                ws.sendClose(WebSocket.NORMAL_CLOSURE, "manual stop");
            }
            scheduler.shutdownNow();
        }

        private void connect() {
            reconnectScheduled.set(false);
            httpClient.newWebSocketBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .header("Authorization", AUTHORIZATION_HEADER)
                    .buildAsync(uri, this)
                    .whenComplete((ws, throwable) -> {
                        if (throwable != null) {
                            logLocal("连接失败: {}", throwable.getMessage());
                            scheduleReconnect();
                            return;
                        }
                        this.webSocket = ws;
                        reconnectAttempts.set(0);
                        logLocal("连接成功: {}", uri);
                        startHeartbeat(ws);
                        ws.request(1);
                    });
        }

        private void startHeartbeat(WebSocket connectedSocket) {
            cancelHeartbeat();
            heartbeatTask = scheduler.scheduleAtFixedRate(() -> {
                if (!started.get()) {
                    return;
                }
                if (webSocket != connectedSocket || connectedSocket.isOutputClosed()) {
                    return;
                }
                connectedSocket.sendPing(ByteBuffer.wrap(HEARTBEAT_PAYLOAD.getBytes(StandardCharsets.UTF_8)))
                        .exceptionally(throwable -> {
                            logLocal("心跳发送失败: {}", throwable.getMessage());
                            scheduleReconnect();
                            return null;
                        });
            }, heartbeatIntervalMs, heartbeatIntervalMs, TimeUnit.MILLISECONDS);
        }

        private void cancelHeartbeat() {
            ScheduledFuture<?> task = heartbeatTask;
            if (task != null && !task.isCancelled()) {
                task.cancel(false);
            }
        }

        private void scheduleReconnect() {
            if (!started.get() || !reconnectScheduled.compareAndSet(false, true)) {
                return;
            }
            cancelHeartbeat();
            int attempt = reconnectAttempts.incrementAndGet();
            long delay = Math.min(MAX_BACKOFF_MS, INITIAL_BACKOFF_MS << Math.min(attempt - 1, 5));
            logLocal("{}ms 后进行第 {} 次重连", delay, attempt);
            scheduler.schedule(this::connect, delay, TimeUnit.MILLISECONDS);
        }

        @Override
        public void onOpen(WebSocket webSocket) {
            this.webSocket = webSocket;
            reconnectAttempts.set(0);
            reconnectScheduled.set(false);
            logLocal("连接已打开(onOpen)");
            String initPayload = sendGroupMessage("testMessage", 460056296);
            webSocket.sendText(initPayload, true)
                    .thenRun(() -> logLocal("已发送初始化payload: {}", initPayload))
                    .exceptionally(throwable -> {
                        logLocal("发送初始化payload失败: {}", throwable.getMessage());
                        return null;
                    });
            webSocket.request(1);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            logRecv("TEXT last={}, payload={}", last, data);
            webSocket.request(1);
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
            data.position(data.limit());
            webSocket.request(1);
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message) {
            message.position(message.limit());
            webSocket.request(1);
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public CompletionStage<?> onPong(WebSocket webSocket, ByteBuffer message) {
            message.position(message.limit());
            webSocket.request(1);
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            logRecv("CLOSE code={}, reason={}", statusCode, reason);
            this.webSocket = null;
            scheduleReconnect();
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            logLocal("ERROR {}", error.getMessage());
            this.webSocket = null;
            scheduleReconnect();
        }

        private static void logLocal(String message, Object... args) {
            LOGGER.info(ANSI_LOCAL + "[LOCAL] " + message + ANSI_RESET, args);
        }

        private static void logRecv(String message, Object... args) {
            LOGGER.info(ANSI_RECV + "[RECV ] " + message + ANSI_RESET, args);
        }
    }

    private static String sendGroupMessage(String message, long group) {
        JsonObject payload = new JsonObject();
        payload.addProperty("action", "send_group_msg");
        JsonObject params = new JsonObject();
        params.addProperty("group_id", group);
        JsonArray messageList = new JsonArray();
        JsonObject messageObject = new JsonObject();
        messageObject.addProperty("type", "text");
        JsonObject messageData = new JsonObject();
        messageData.addProperty("text", message);
        messageObject.add("data", messageData);
        messageList.add(messageObject);
        params.add("message", messageList);
        payload.add("params", params);
        return payload.toString();
    }

    @Test
    public void buildSendMessageTest() {
        LOGGER.info(sendGroupMessage("testMessage", 460056296L));
    }
}
