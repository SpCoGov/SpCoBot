package top.spco.qq.message;

import top.spco.api.message.Message;
import top.spco.api.message.MessageChain;

public class ReplyMessage extends Message {
    private final String replyId;
    private final String senderId;
    private final String fromId;

    public ReplyMessage(String replyId, String senderId, String fromId) {
        this.replyId = replyId;
        this.senderId = senderId;
        this.fromId = fromId;
    }

    public String getReplyId() {
        return replyId;
    }

    public String getFromId() {
        return fromId;
    }

    public String getSenderId() {
        return senderId;
    }

    @Override
    public String toMessageContext() {
        return "";
    }

    @Override
    public MessageChain append(Message appendage) {
        return null;
    }

    @Override
    public MessageChain append(String appendage) {
        return null;
    }

    @Override
    public MessageChain toMessageChain() {
        return null;
    }
}
