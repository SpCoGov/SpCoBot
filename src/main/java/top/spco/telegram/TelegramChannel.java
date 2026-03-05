package top.spco.telegram;

import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import top.spco.api.Channel;
import top.spco.api.message.Message;

import java.io.File;

class TelegramChannel extends Channel<Chat> {
    protected TelegramChannel(Chat channel) {
        super(channel);
        if (!channel.isChannelChat()) {
            throw new IllegalArgumentException("Not a channel.");
        }
    }

    @Override
    public String getName() {
        return wrapped().getTitle();
    }

    @Override
    public void sendMessage(String message) {
        try {
            TelegramMessageSender.sendMessage(TelegramAdapter.getInstance().telegramClient, String.valueOf(getId()), message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMessage(Message<?> message) {
        TelegramMessageSender.sendMessage(TelegramAdapter.getInstance().telegramClient, String.valueOf(getId()), (org.telegram.telegrambots.meta.api.objects.message.Message) message.wrapped());
    }

    @Override
    public void sendImage(File image) {
        try {
            TelegramMessageSender.sendImage(TelegramAdapter.getInstance().telegramClient, String.valueOf(getId()), image);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getId() {
        return wrapped().getId() + "";
    }
}
