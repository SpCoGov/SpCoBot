package top.spco.telegram;

import top.spco.api.message.Message;
import top.spco.api.message.MessageSource;

public class TelegramMessageSource extends MessageSource<org.telegram.telegrambots.meta.api.objects.message.Message> {
    protected TelegramMessageSource(org.telegram.telegrambots.meta.api.objects.message.Message message) {
        super(message);
    }

    /**
     * 发送人用户 ID
     */
    @Override
    public long getSenderId() {
        return wrapped().getForwardFrom().getId();
    }

    /**
     * 消息发送目标用户或群号码
     */
    @Override
    public long getFromId() {
        return wrapped().getChatId();
    }

    @Override
    public Message<?> getOriginalMessage() {
        return new TelegramMessage(wrapped().getReplyToMessage());
    }
}
