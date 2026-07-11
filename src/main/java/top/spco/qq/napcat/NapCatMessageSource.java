package top.spco.qq.napcat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import top.spco.SpCoBot;
import top.spco.api.message.MessageChain;
import top.spco.api.message.MessageSource;
import top.spco.qq.QQAdapter;
import top.spco.qq.payload.NapCatPacketBuilder;
import top.spco.qq.payload.handler.message.NapCatMessagePayloadParser;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class NapCatMessageSource extends MessageSource {
    private final String senderId;
    private final String fromId;
    private final String messageId;
    private MessageChain messageChain;

    public NapCatMessageSource(String senderId, String fromId, String messageId, MessageChain messageChain) {
        this.senderId = senderId;
        this.fromId = fromId;
        this.messageId = messageId;
        this.messageChain = messageChain;
    }

    @Override
    public String getSenderId() {
        return senderId;
    }

    @Override
    public String getFromId() {
        return fromId;
    }

    @Override
    public String getMessageId() {
        return messageId;
    }

    @Override
    public MessageChain getMessageChain() {
        if (messageChain == null) {

            try {
                JsonObject getMessage = new NapCatPacketBuilder("get_msg").param("message_id", messageId).build();
                JsonObject response = QQAdapter.getInstance().getClient().getPacketManager().sendSync(getMessage);

                JsonObject data = response.get("data").getAsJsonObject();
                boolean isPrivate = data.get("message_type").getAsString().equals("private");
                if (isPrivate) {
                    this.messageChain = NapCatMessagePayloadParser.parsePrivateMessage(data).getMessage();
                } else {
                    this.messageChain = NapCatMessagePayloadParser.parseGroupMessage(data).getMessage();
                }
            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        }
        return messageChain;
    }
}
