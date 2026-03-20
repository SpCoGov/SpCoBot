package top.spco.telegram;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import top.spco.api.User;
import top.spco.api.message.Message;

import java.io.File;

class TelegramUser extends User<org.telegram.telegrambots.meta.api.objects.User> {
    public TelegramUser(org.telegram.telegrambots.meta.api.objects.User user) {
        super(user);
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
    public void sendMessage(Message message) {
        TelegramMessageSender.sendMessage(TelegramAdapter.getInstance().telegramClient, String.valueOf(getId()), ((TelegramMessage) message).getMessage());
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
