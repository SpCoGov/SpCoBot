package top.spco.api.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageChain extends Message {
    private boolean isCommandMessage = false;
    private final ArrayList<Message> messageComponents = new ArrayList<>();
    private MessageSource source;
    private MessageSource replySource;

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

    /**
     * 获取消息链中的所有消息组件。
     *
     * <p>返回值为只读视图，调用方可以高效遍历，但不能直接修改消息链内部结构。</p>
     *
     * @return 不可修改的消息组件列表
     */
    public List<Message> getComponents() {
        return Collections.unmodifiableList(messageComponents);
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
     * @param toReply 需要引用的消息
     */
    public MessageChain quoteReply(MessageChain toReply) {
        if (toReply.source == null) {
            throw new NullPointerException("the reply message had no source");
        }
        this.replySource = toReply.source;
        return this;
    }

    public void setReplySource(MessageSource source) {
        this.replySource = source;
    }

    public void setSource(MessageSource source) {
        this.source = source;
    }

    public MessageSource getReplySource() {
        return replySource;
    }
}
