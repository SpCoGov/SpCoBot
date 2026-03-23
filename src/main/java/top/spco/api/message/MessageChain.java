package top.spco.api.message;

import java.util.ArrayList;

public class MessageChain extends Message {
    private boolean isCommandMessage = false;
    private final ArrayList<Message> messageComponents = new ArrayList<>();
    private MessageSource source;

    public MessageChain() {
        super();
    }

    public MessageChain(ArrayList<Message> messageComponents) {
        this.messageComponents.addAll(messageComponents);
    }

    public MessageChain(Message message) {
        super();
        messageComponents.add(message);
    }

    @Override
    public String toMessageContext() {
        StringBuilder sb = new StringBuilder();
        for (Message message : messageComponents) {
            sb.append(message.toMessageContext());
        }
        return sb.toString();
    }

    @Override
    public MessageChain append(Message appendage) {
        if (appendage instanceof UnsupportedMessage) {
            throw new UnsupportedOperationException("Unable to append unsupported messages to other messages");
        }
        messageComponents.add(appendage);
        return this;
    }

    @Override
    public MessageChain append(String appendage) {
        return append(new TextMessage(appendage));
    }

    @Override
    public MessageChain toMessageChain() {
        return this;
    }

    /**
     * 获得消息的 {@link MessageSource} （如果有）
     *
     * @return 转换的结果
     */
    public MessageSource getSource() {
        return source;
    }

    public boolean isCommandMessage() {
        return isCommandMessage;
    }

    public void setCommandMessage() {
        isCommandMessage = true;
    }

    /**
     * 引用一条消息
     *
     * @param toQuote 需要引用的消息
     */
    public MessageChain quoteReply(MessageChain toQuote) {
        return this;
    }

    public void setSource(MessageSource source) {
        this.source = source;
    }
}
