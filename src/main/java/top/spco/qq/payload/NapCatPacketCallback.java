package top.spco.qq.payload;

import com.google.gson.JsonObject;

/**
 * NapCat 异步发包回调接口。
 *
 * <p>当请求成功收到回包时，将调用 {@link #onResponse(JsonObject)}；
 * 当发送失败、等待超时、连接断开等异常情况发生时，将调用 {@link #onFailure(Throwable)}。</p>
 */
@FunctionalInterface
public interface NapCatPacketCallback {
    /**
     * 在收到对应回包后触发。
     *
     * @param payload NapCat 返回的完整回包 payload
     */
    void onResponse(JsonObject payload);

    /**
     * 在异步请求失败时触发。
     *
     * <p>默认空实现，调用方只关心成功结果时可以不覆写。</p>
     *
     * @param throwable 失败原因，例如超时、连接关闭、发送异常等
     */
    default void onFailure(Throwable throwable) {
    }
}
