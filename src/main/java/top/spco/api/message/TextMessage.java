package top.spco.api.message;

public class TextMessage extends Message {
    private final String context;
    public TextMessage(String content) {
        this.context = content;
    }
    @Override
    public String toMessageContext() {
        return context;
    }

    @Override
    public MessageChain append(Message appendage) {
        return toMessageChain().append(appendage);
    }

    @Override
    public MessageChain append(String appendage) {
        return toMessageChain().append(appendage);
    }

    @Override
    public MessageChain toMessageChain() {
        return new MessageChain().append(this);
    }

    @Override
    public String serialize() {
        return context;
    }

    @Override
    public String toString() {
        return context;
    }
}
