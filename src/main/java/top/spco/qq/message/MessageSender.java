package top.spco.qq.message;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import top.spco.api.message.MessageChain;
import top.spco.qq.QQAdapter;
import top.spco.qq.payload.NapCatPacketBuilder;
import top.spco.qq.payload.NapCatPacketCallback;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class MessageSender {
    /**
     * 同步发送群消息。
     *
     * <p>当前实现会先将 {@link MessageChain} 转换为纯文本内容，再以 NapCat 标准消息段格式发送。</p>
     *
     * @param groupId      群号
     * @param messageChain 要发送的消息链
     * @return NapCat 返回的完整回包
     */
    public static JsonObject sendSyncGroupMessage(String groupId, MessageChain messageChain)
            throws ExecutionException, InterruptedException, TimeoutException {
        return QQAdapter.getInstance().getClient().getPacketManager().sendSync(buildGroupMessagePayload(groupId, messageChain));
    }

    /**
     * 异步发送群消息。
     *
     * <p>该方法不会阻塞当前线程，适合在事件线程中发送消息，避免因为等待回包而卡住收包流程。</p>
     *
     * @param groupId      群号
     * @param messageChain 要发送的消息链
     * @param callback     回包或失败时触发的回调
     * @return 异步结果 Future
     */
    public static CompletableFuture<JsonObject> sendAsyncGroupMessage(String groupId, MessageChain messageChain, NapCatPacketCallback callback) {
        return QQAdapter.getInstance().getClient().getPacketManager().sendAsync(buildGroupMessagePayload(groupId, messageChain), callback);
    }

    private static JsonObject buildGroupMessagePayload(String groupId, MessageChain messageChain) {
        JsonArray messageSegments = MessageParser.getInstance().serialize(messageChain);
        return NapCatPacketBuilder.create("send_group_msg")
                .param("group_id", groupId)
                .param("message", messageSegments)
                .build();
    }
}
