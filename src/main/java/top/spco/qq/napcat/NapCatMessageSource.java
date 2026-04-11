package top.spco.qq.napcat;

import top.spco.api.message.MessageSource;

public class NapCatMessageSource extends MessageSource {
    private final String senderId;
    private final String fromId;
    private final String messageId;

    public NapCatMessageSource(String senderId, String fromId, String messageId) {
        this.senderId = senderId;
        this.fromId = fromId;
        this.messageId = messageId;
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
}
