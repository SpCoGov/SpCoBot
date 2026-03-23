package top.spco.telegram;

import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import top.spco.api.Channel;
import top.spco.api.message.MessageChain;

class TelegramChannel extends Channel {
    private final Chat chat;

    TelegramChannel(Chat channel) {
        this.chat = channel;
        if (!channel.isChannelChat()) {
            throw new IllegalArgumentException("Not a channel.");
        }
    }

    @Override
    public String getName() {
        return chat.getTitle();
    }

    @Override
    public void sendMessage(MessageChain message) {
        try {
            TelegramMessageSender.sendMessage(TelegramAdapter.getInstance().telegramClient, getId(), message.toMessageContext());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getId() {
        return chat.getId() + "";
    }
}
