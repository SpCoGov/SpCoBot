package top.spco.qq.payload;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;
import top.spco.SpCoBot;
import top.spco.qq.NapCatWebSocketClient;
import top.spco.util.NamedThreadFactory;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * NapCat 发包管理器。
 *
 * <p>该类负责统一管理 QQ/NapCat 的主动发包流程，包括：</p>
 * <ul>
 *     <li>同步发包并等待回包</li>
 *     <li>异步发包并在回包后触发回调</li>
 *     <li>通过 echo 关联请求与响应</li>
 *     <li>处理超时、发送失败、连接关闭等异常情况</li>
 * </ul>
 *
 * <p>如果调用方没有手动传入 echo，本类会自动生成一个，确保每个待响应请求都能被正确追踪。</p>
 */
public class NapCatPacketManager {
    /**
     * 默认发包超时时间。
     */
    public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    private final NapCatWebSocketClient client;
    private final ConcurrentMap<String, PendingRequest> pendingRequests = new ConcurrentHashMap<>();
    private final ScheduledExecutorService timeoutScheduler = Executors.newSingleThreadScheduledExecutor(
            new NamedThreadFactory("NapCat-Packet-Timeout")
    );

    public NapCatPacketManager(NapCatWebSocketClient client) {
        this.client = client;
    }

    /**
     * 同步发送一个 payload，并使用默认超时时间等待回包。
     *
     * @param payload 要发送的请求 payload
     * @return NapCat 返回的完整回包 payload
     * @throws InterruptedException 当前线程在等待期间被中断
     * @throws ExecutionException   发送或处理过程中发生异常
     * @throws TimeoutException     在超时时间内未收到对应回包
     */
    public JsonObject sendSync(JsonObject payload) throws InterruptedException, ExecutionException, TimeoutException {
        return sendSync(payload, DEFAULT_TIMEOUT);
    }

    /**
     * 同步发送一个 payload，并在指定超时时间内等待回包。
     *
     * @param payload 要发送的请求 payload
     * @param timeout 超时时间，必须大于 0；为 null 时使用默认值
     * @return NapCat 返回的完整回包 payload
     * @throws InterruptedException 当前线程在等待期间被中断
     * @throws ExecutionException   发送或处理过程中发生异常
     * @throws TimeoutException     在超时时间内未收到对应回包
     */
    public JsonObject sendSync(JsonObject payload, Duration timeout) throws InterruptedException, ExecutionException, TimeoutException {
        CompletableFuture<JsonObject> future = sendAsyncInternal(payload, timeout, null);
        try {
            return future.get();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof TimeoutException timeoutException) {
                throw timeoutException;
            }
            throw e;
        }
    }

    /**
     * 异步发送一个 payload，并使用默认超时时间等待回包。
     *
     * @param payload  要发送的请求 payload
     * @param callback 回包或失败时调用的回调
     * @return 可用于链式处理的 Future，结果为完整回包 payload
     */
    public CompletableFuture<JsonObject> sendAsync(JsonObject payload, NapCatPacketCallback callback) {
        return sendAsync(payload, DEFAULT_TIMEOUT, callback);
    }

    /**
     * 异步发送一个 payload，并在指定超时时间内等待回包。
     *
     * @param payload  要发送的请求 payload
     * @param timeout  超时时间，必须大于 0；为 null 时使用默认值
     * @param callback 回包或失败时调用的回调
     * @return 可用于链式处理的 Future，结果为完整回包 payload
     */
    public CompletableFuture<JsonObject> sendAsync(JsonObject payload, Duration timeout, NapCatPacketCallback callback) {
        return sendAsyncInternal(payload, timeout, callback);
    }

    /**
     * 在收到 NapCat 回包时，根据 echo 匹配并唤醒对应请求。
     *
     * @param payload 收到的回包 payload
     * @return 是否成功匹配到了一个待处理请求
     */
    public boolean onResponseReceived(JsonObject payload) {
        String echo = normalizeEcho(payload.get("echo"));
        if (echo == null) {
            return false;
        }
        PendingRequest pendingRequest = pendingRequests.remove(echo);
        if (pendingRequest == null) {
            return false;
        }
        pendingRequest.cancelTimeout();
        pendingRequest.future.complete(payload);
        if (pendingRequest.callback != null) {
            try {
                pendingRequest.callback.onResponse(payload);
            } catch (Exception e) {
                SpCoBot.LOGGER.error("处理 NapCat 异步回包回调时发生异常，echo={}", echo, e);
            }
        }
        return true;
    }

    /**
     * 将当前所有等待中的请求统一标记为失败。
     *
     * <p>通常在连接断开、鉴权失败或客户端关闭时调用，避免同步/异步请求无限等待。</p>
     *
     * @param throwable 失败原因
     */
    public void failAllPending(Throwable throwable) {
        pendingRequests.forEach((echo, pendingRequest) -> {
            if (pendingRequests.remove(echo, pendingRequest)) {
                pendingRequest.cancelTimeout();
                pendingRequest.future.completeExceptionally(throwable);
                if (pendingRequest.callback != null) {
                    try {
                        pendingRequest.callback.onFailure(throwable);
                    } catch (Exception e) {
                        SpCoBot.LOGGER.error("处理 NapCat 异步失败回调时发生异常，echo={}", echo, e);
                    }
                }
            }
        });
    }

    /**
     * 关闭发包管理器并取消所有等待中的请求。
     */
    public void shutdown() {
        failAllPending(new CancellationException("NapCat 发包管理器已关闭"));
        timeoutScheduler.shutdownNow();
    }

    private CompletableFuture<JsonObject> sendAsyncInternal(JsonObject payload, Duration timeout, @Nullable NapCatPacketCallback callback) {
        Objects.requireNonNull(payload, "payload");
        Duration effectiveTimeout = validateTimeout(timeout);
        JsonObject requestPayload = payload.deepCopy();
        // 统一使用 echo 跟踪请求。如果调用方未传入，则自动补一个。
        String echo = ensureEcho(requestPayload);
        CompletableFuture<JsonObject> future = new CompletableFuture<>();
        PendingRequest pendingRequest = new PendingRequest(future, callback);

        // 先注册超时任务，避免发出后因为超时无人清理而造成挂起。
        ScheduledFuture<?> timeoutFuture = timeoutScheduler.schedule(() -> {
            if (pendingRequests.remove(echo, pendingRequest)) {
                TimeoutException timeoutException = new TimeoutException("NapCat 发包超时，echo=" + echo + ", timeout=" + effectiveTimeout);
                pendingRequest.future.completeExceptionally(timeoutException);
                if (pendingRequest.callback != null) {
                    try {
                        pendingRequest.callback.onFailure(timeoutException);
                    } catch (Exception e) {
                        SpCoBot.LOGGER.error("处理 NapCat 超时回调时发生异常，echo={}", echo, e);
                    }
                }
            }
        }, effectiveTimeout.toMillis(), TimeUnit.MILLISECONDS);
        pendingRequest.setTimeoutFuture(timeoutFuture);

        PendingRequest previous = pendingRequests.putIfAbsent(echo, pendingRequest);
        if (previous != null) {
            timeoutFuture.cancel(false);
            throw new IllegalStateException("重复的 NapCat echo，当前存在未完成请求: " + echo);
        }

        // 发送失败时要立刻把挂起请求移除，并通知同步/异步调用方。
        client.sendPacket(requestPayload.toString()).whenComplete((socket, throwable) -> {
            if (throwable == null) {
                return;
            }
            if (pendingRequests.remove(echo, pendingRequest)) {
                pendingRequest.cancelTimeout();
                pendingRequest.future.completeExceptionally(throwable);
                if (pendingRequest.callback != null) {
                    try {
                        pendingRequest.callback.onFailure(throwable);
                    } catch (Exception e) {
                        SpCoBot.LOGGER.error("处理 NapCat 发送失败回调时发生异常，echo={}", echo, e);
                    }
                }
            }
        });
        return future;
    }

    private Duration validateTimeout(@Nullable Duration timeout) {
        if (timeout == null) {
            return DEFAULT_TIMEOUT;
        }
        if (timeout.isZero() || timeout.isNegative()) {
            throw new IllegalArgumentException("timeout 必须大于 0");
        }
        return timeout;
    }

    private String ensureEcho(JsonObject payload) {
        String echo = normalizeEcho(payload.get("echo"));
        if (echo != null) {
            return echo;
        }
        // NapCat 回包会原样带回 echo，这里自动补齐后即可用于请求-响应配对。
        echo = UUID.randomUUID().toString();
        payload.addProperty("echo", echo);
        return echo;
    }

    @Nullable
    private String normalizeEcho(@Nullable JsonElement echoElement) {
        if (echoElement == null || echoElement.isJsonNull()) {
            return null;
        }
        // 字符串类型必须取实际值，不能使用 toString()，否则会把双引号也带上，导致 echo 无法匹配。
        if (echoElement.isJsonPrimitive() && echoElement.getAsJsonPrimitive().isString()) {
            return echoElement.getAsString();
        }
        return echoElement.toString();
    }

    /**
     * 单个待完成请求的上下文。
     *
     * <p>内部同时保存 Future、异步回调和超时任务句柄，便于在成功、失败、超时三种情况下统一清理。</p>
     */
    private static final class PendingRequest {
        private final CompletableFuture<JsonObject> future;
        private final NapCatPacketCallback callback;
        private volatile ScheduledFuture<?> timeoutFuture;

        private PendingRequest(CompletableFuture<JsonObject> future, @Nullable NapCatPacketCallback callback) {
            this.future = future;
            this.callback = callback;
        }

        private void setTimeoutFuture(ScheduledFuture<?> timeoutFuture) {
            this.timeoutFuture = timeoutFuture;
        }

        private void cancelTimeout() {
            ScheduledFuture<?> future = timeoutFuture;
            if (future != null) {
                future.cancel(false);
            }
        }
    }
}
