package top.spco.telegram;

import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import top.spco.api.User;
import top.spco.api.message.Message;

import java.io.File;

class TelegramUser extends User<Chat> {
    public TelegramUser(Chat user) {
        super(user);
    }

    @Deprecated
    @Override
    public String getRemark() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
    public void nudge() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getNick() {
        return TelegramAdapter.getUserNick(wrapped());
    }

    @Override
    public boolean isBot() {
        return false;
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
    public long getId() {
        return wrapped().getId();
    }
}
