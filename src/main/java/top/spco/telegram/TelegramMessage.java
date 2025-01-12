package top.spco.telegram;

import top.spco.api.message.Message;
import top.spco.api.message.MessageSource;

class TelegramMessage extends Message<org.telegram.telegrambots.meta.api.objects.message.Message> {
    protected TelegramMessage(org.telegram.telegrambots.meta.api.objects.message.Message message) {
        super(message);
    }

    /**
     * 转为接近官方格式的字符串, 即 "内容". 如 At(member) + "test" 将转为 "@QQ test"
     *
     * @return 转化后的文本
     */
    @Override
    public String toMessageContext() {
        return wrapped().getText() == null ? "" : wrapped().getText();
    }

    /**
     * 引用一条消息
     *
     * @param toQuote 需要引用的消息
     */
    @Override
    public Message<org.telegram.telegrambots.meta.api.objects.message.Message> quoteReply(Message<?> toQuote) {
        wrapped().setReplyToMessage(((TelegramMessage) toQuote).wrapped());
        return this;
    }

    /**
     * 在这条消息后添加 {@code Message} 对象
     *
     * @param appendage 需要添加的 {@code Message} 对象
     */
    @Override
    public Message<org.telegram.telegrambots.meta.api.objects.message.Message> append(Message<?> appendage) {
        TelegramMessageSender.appendMessage(wrapped(), (org.telegram.telegrambots.meta.api.objects.message.Message) appendage.wrapped());
        return this;
    }

    /**
     * 在这条消息后添加文本
     *
     * @param appendage 需要添加的文本
     */
    @Override
    public Message<org.telegram.telegrambots.meta.api.objects.message.Message> append(String appendage) {
        TelegramMessageSender.appendText(wrapped(), appendage);
        return this;
    }

    /**
     * 将一条消息转换成普通的消息
     *
     * @return 转换的结果
     */
    @Override
    public Message<?> toMessage() {
        return this;
    }

    /**
     * 获得消息的 {@link MessageSource} （如果有）
     *
     * @return 转换的结果
     */
    @Override
    public MessageSource<?> getSource() {
        return new TelegramMessageSource(wrapped());
    }

    /**
     * 序列化
     *
     * @return 序列化后的结果
     */
    @Override
    public String serialize() {
        return wrapped().toString();
    }
}
