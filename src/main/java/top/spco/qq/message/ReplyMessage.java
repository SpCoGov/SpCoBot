package top.spco.qq.message;

import top.spco.api.message.Message;
import top.spco.api.message.MessageChain;

public class ReplyMessage extends Message {
    private final String replyId;

    public ReplyMessage(String replyId) {
        this.replyId = replyId;
    }

    public String getReplyId() {
        return replyId;
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
