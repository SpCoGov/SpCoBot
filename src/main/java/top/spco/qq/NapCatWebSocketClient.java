package top.spco.qq;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import top.spco.SpCoBot;
import top.spco.config.Configs;
import top.spco.qq.payload.NapCatPacketManager;
import top.spco.qq.payload.NapCatPayloadDispatcher;
import top.spco.util.Ansi;
import top.spco.util.JsonUtil;
import top.spco.util.NamedThreadFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class NapCatWebSocketClient implements WebSocket.Listener {
    private static final Gson GSON = new Gson();
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_LOCAL = "\u001B[33m";
    private static final String ANSI_RECV = "\u001B[36m";

    public static NapCatWebSocketClient runNapCatWebSocketClient() {
        NapCatWebSocketClient client = new NapCatWebSocketClient(
                Configs.BOT.getQQServerIp(), Configs.BOT.getQQServerPort(), Configs.BOT.getQQServerHeartbeatInterval()
        );
        client.start();
        return client;
    }

    private static final long INITIAL_BACKOFF_MS = 1000L;
    private static final long MAX_BACKOFF_MS = 120000L;
    private static final String AUTHORIZATION_HEADER = "Bearer " + Configs.BOT.getQQBotToken();

    private final URI uri;
    private final long heartbeatIntervalMs;
    private final HttpClient httpClient;
    private final ScheduledExecutorService scheduler;
    private final ExecutorService eventExecutor;

    private final NapCatPayloadDispatcher dispatcher = NapCatPayloadDispatcher.getInstance();
    private final NapCatPacketManager packetManager = new NapCatPacketManager(this);

    private final AtomicBoolean started = new AtomicBoolean(false);
    private final AtomicBoolean reconnectScheduled = new AtomicBoolean(false);
    private final AtomicBoolean authFailed = new AtomicBoolean(false);
    private final AtomicInteger reconnectAttempts = new AtomicInteger(0);

    private volatile WebSocket webSocket;
    private volatile ScheduledFuture<?> heartbeatTask;
    private final StringBuilder textPacketBuffer = new StringBuilder();

    NapCatWebSocketClient(String host, int port, long heartbeatIntervalMs) {
        this.uri = URI.create("ws://" + host + ":" + port);
        this.heartbeatIntervalMs = heartbeatIntervalMs;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "QQ-WebSocket-Client");
            t.setDaemon(true);
            return t;
        });
        this.eventExecutor = Executors.newCachedThreadPool(new NamedThreadFactory("QQ-Event-Dispatcher"));
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
        packetManager.shutdown();
        WebSocket ws = webSocket;
        webSocket = null;
        if (ws != null && !ws.isOutputClosed()) {
            ws.sendClose(WebSocket.NORMAL_CLOSURE, "manual stop");
        }
        scheduler.shutdownNow();
        eventExecutor.shutdownNow();
    }

    private void connect() {
        if (authFailed.get()) {
            logLocal("鉴权失败状态下不再尝试重连");
            return;
        }
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
                    if (authFailed.get()) {
                        if (!ws.isOutputClosed()) {
                            ws.sendClose(WebSocket.NORMAL_CLOSURE, "auth failed");
                        }
                        return;
                    }
                    this.webSocket = ws;
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
            connectedSocket.sendPing(ByteBuffer.wrap(buildHeartbeatPayload().getBytes(StandardCharsets.UTF_8)))
                    .exceptionally(throwable -> {
                        logLocal("心跳发送失败: {}", throwable.getMessage());
                        scheduleReconnect();
                        return null;
                    });
        }, heartbeatIntervalMs, heartbeatIntervalMs, TimeUnit.MILLISECONDS);
    }

    private String buildHeartbeatPayload() {
        return "{\"action\":\"get_status\",\"echo\":\"" + System.currentTimeMillis() + "\"}";
    }

    private void cancelHeartbeat() {
        ScheduledFuture<?> task = heartbeatTask;
        if (task != null && !task.isCancelled()) {
            task.cancel(false);
        }
    }

    private void scheduleReconnect() {
        if (!started.get() || authFailed.get() || !reconnectScheduled.compareAndSet(false, true)) {
            return;
        }
        cancelHeartbeat();
        int attempt = reconnectAttempts.incrementAndGet();
        long delay = Math.min(MAX_BACKOFF_MS, INITIAL_BACKOFF_MS * (1L << Math.min(attempt - 1, 16)));
        logLocal("{}ms 后进行第 {} 次重连", delay, attempt);
        scheduler.schedule(this::connect, delay, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        this.webSocket = webSocket;
        reconnectAttempts.set(0);
        reconnectScheduled.set(false);
        SpCoBot.LOGGER.info(Ansi.BRIGHT_GREEN + Ansi.BOLD + "连接成功: {}", uri);
        webSocket.request(1);
        startHeartbeat(webSocket);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence payload, boolean last) {
        String packet;
        synchronized (textPacketBuffer) {
            textPacketBuffer.append(payload);
            if (!last) {
                webSocket.request(1);
                return CompletableFuture.completedFuture(null);
            }
            packet = textPacketBuffer.toString();
            textPacketBuffer.setLength(0);
        }
        try {
            JsonObject json = GSON.fromJson(packet, JsonObject.class);
            if (json.has("status")) {
                // 主动请求的回包必须立即在收包线程内处理，这样才能及时唤醒正在同步等待结果的业务线程。
                dispatcher.onPayloadReceived(this, webSocket, packet);
            } else {
                // 推送事件不要在 WebSocket 收包回调线程里直接执行业务逻辑，
                // 否则业务里如果再次同步发包，会把收包线程卡住，导致回包只能等当前回调结束后才能处理。
                eventExecutor.execute(() -> {
                    try {
                        dispatcher.onPayloadReceived(this, webSocket, packet);
                        handleEventPacket(json);
                    } catch (Exception e) {
                        SpCoBot.LOGGER.error("处理推送事件时发生错误", e);
                        SpCoBot.LOGGER.error("收到的完整文本封包（长度={}）: {}", packet.length(), packet);
                    }
                });
            }
            webSocket.request(1);
        } catch (Exception e) {
            SpCoBot.LOGGER.error("错误发生", e);
            SpCoBot.LOGGER.error("收到的完整文本封包（长度={}）: {}", packet.length(), packet);
            webSocket.request(1);
        }
        return CompletableFuture.completedFuture(null);
    }

    private void handleEventPacket(JsonObject json) {
        if (!json.has("post_type")) {
            return;
        }
        String postType = getAsString(json, "post_type");
        if (!"message".equals(postType)) {
            return;
        }
        handleMessageEvent(json);
    }

    private void handleMessageEvent(JsonObject event) {
        String messageType = getAsString(event, "message_type");
        if ("group".equals(messageType)) {
            return;
        }
        if ("private".equals(messageType)) {
            handlePrivateMessageEvent(event);
        }
    }

    private void handlePrivateMessageEvent(JsonObject event) {
        if (!hasRequiredPrivateMessageFields(event)) {
            SpCoBot.LOGGER.warn("[QQNT] 私聊消息事件缺少必须字段: {}", event);
            return;
        }
        String userId = getAsString(event, "user_id");
        String subType = getAsString(event, "sub_type");
        String text = getAsString(event, "raw_message");
        int time = getAsInt(event, "time", 0);

        JsonObject sender = event.has("sender") && event.get("sender").isJsonObject() ? event.getAsJsonObject("sender") : null;
        String nick = sender == null ? "" : getAsString(sender, "nickname");

        if ("friend".equals(subType)) {
            SpCoBot.LOGGER.info("[QQNT] 好友私聊 sender={}({}) time={} text={}", nick, userId, time, text);
            return;
        }
        if ("group".equals(subType)) {
            String groupId = getAsString(event, "group_id");
            String tempSource = getAsString(event, "temp_source");
            SpCoBot.LOGGER.info("[QQNT] 群临时会话 sender={}({}) group={} temp_source={} time={} text={}",
                    nick, userId, groupId, tempSource, time, text);
            return;
        }
        SpCoBot.LOGGER.info("[QQNT] 私聊消息 sender={}({}) sub_type={} time={} text={}", nick, userId, subType, time, text);
    }

    private boolean hasRequiredPrivateMessageFields(JsonObject event) {
        return event.has("self_id")
                && event.has("time")
                && event.has("message_id")
                && event.has("message_type")
                && event.has("user_id")
                && event.has("raw_message")
                && event.has("message")
                && event.has("sender")
                && event.has("post_type");
    }

    private String getAsString(JsonObject json, String key) {
        return JsonUtil.getAsString(json, key);
    }

    private int getAsInt(JsonObject json, String key, int fallback) {
        if (json == null || !json.has(key)) {
            return fallback;
        }
        JsonElement element = json.get(key);
        if (element == null || element.isJsonNull()) {
            return fallback;
        }
        try {
            return element.getAsInt();
        } catch (Exception e) {
            return fallback;
        }
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
        packetManager.failAllPending(new IllegalStateException("NapCat WebSocket 已关闭: " + statusCode + " / " + reason));
        synchronized (textPacketBuffer) {
            textPacketBuffer.setLength(0);
        }
        if (authFailed.get()) {
            // 鉴权失败后收到关闭事件，跳过重连
            return CompletableFuture.completedFuture(null);
        }
        scheduleReconnect();
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        logLocal("ERROR {}", error.getMessage());
        this.webSocket = null;
        packetManager.failAllPending(error);
        synchronized (textPacketBuffer) {
            textPacketBuffer.setLength(0);
        }
        if (authFailed.get()) {
            // 鉴权失败后收到异常事件，跳过重连
            return;
        }
        scheduleReconnect();
    }

    private static void logLocal(String message, Object... args) {
        SpCoBot.LOGGER.info(ANSI_LOCAL + message + ANSI_RESET, args);
    }

    private static void logRecv(String message, Object... args) {
        SpCoBot.LOGGER.info(ANSI_RECV + message + ANSI_RESET, args);
    }

    public void setAuthFailed() {
        authFailed.set(true);
        started.set(false);
        cancelHeartbeat();
        // 鉴权失败后，所有正在等待回包的请求都不可能再成功返回。
        packetManager.failAllPending(new SecurityException("NapCat 鉴权失败"));
        WebSocket ws = this.webSocket;
        if (ws != null && !ws.isOutputClosed()) {
            ws.sendClose(WebSocket.NORMAL_CLOSURE, "auth failed");
        }
        this.webSocket = null;
    }

    /**
     * 直接向当前 NapCat WebSocket 发送文本包。
     *
     * <p>该方法只负责发送，不负责 echo 关联、超时和回包等待；这些由 {@link NapCatPacketManager} 统一管理。</p>
     *
     * @param packet 已序列化好的文本 payload
     * @return 发送完成后的 Future；如果当前未连接则直接返回失败 Future
     */
    public CompletableFuture<WebSocket> sendPacket(String packet) {
        WebSocket ws = this.webSocket;
        if (ws == null || ws.isOutputClosed()) {
            return CompletableFuture.failedFuture(new IllegalStateException("NapCat WebSocket 当前未连接"));
        }
        return ws.sendText(packet, true).toCompletableFuture().thenApply(ignored -> ws);
    }

    /**
     * 获取当前 WebSocket 客户端对应的发包管理器。
     *
     * @return NapCat 发包管理器
     */
    public NapCatPacketManager getPacketManager() {
        return packetManager;
    }
}
