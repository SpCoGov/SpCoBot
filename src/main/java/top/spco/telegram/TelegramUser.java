package top.spco.telegram;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import top.spco.api.PlatformPermission;
import top.spco.api.User;
import top.spco.api.message.MessageChain;

class TelegramUser extends User {
    private final org.telegram.telegrambots.meta.api.objects.User tgUser;

    TelegramUser(org.telegram.telegrambots.meta.api.objects.User user) {
        this.tgUser = user;
    }

    @Override
    public String getName() {
        return TelegramAdapter.getUserNick(tgUser);
    }

    @Override
    public boolean isBot() {
        return tgUser.getIsBot();
    }

    @Override
    public PlatformPermission getPlatformPermission() {
        return PlatformPermission.UNKNOWN;
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
        return tgUser.getId() + "";
    }
}
