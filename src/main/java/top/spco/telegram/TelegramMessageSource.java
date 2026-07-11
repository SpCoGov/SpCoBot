package top.spco.telegram;

import top.spco.api.message.MessageChain;
import top.spco.api.message.MessageSource;

class TelegramMessageSource extends MessageSource {
    private final org.telegram.telegrambots.meta.api.objects.message.Message message;

    protected TelegramMessageSource(org.telegram.telegrambots.meta.api.objects.message.Message message) {
        super();
        this.message = message;
    }

    /**
     * 发送人用户 ID
     */
    @Override
    public String getSenderId() {
        return message.getFrom().getId() + "";
    }

    /**
     * 消息发送目标用户或群号码
     */
    @Override
    public String getFromId() {
        return message.getChatId() + "";
    }

    @Override
    public String getMessageId() {
        return message.getMessageId() + "";
    }

    @Override
    public MessageChain getMessageChain() {
        // TODO: 实现这个
        return null;
    }
}
