package top.spco.telegram;

import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import top.spco.api.*;

class TelegramBot extends Bot {
    private final org.telegram.telegrambots.meta.api.objects.User user;

    protected TelegramBot(org.telegram.telegrambots.meta.api.objects.User bot) {
        this.user = bot;
    }
    /**
     * 获取机器人的昵称
     *
     * @return 机器人的昵称
     */
    @Override
    public String getNick() {
        return user.getFirstName();
    }


    @Override
    public Group getGroup(String id) {
        try {
            GetChat getChat = GetChat.builder()
                    .chatId(id)
                    .build();
            Chat chat = TelegramAdapter.getInstance().telegramClient.execute(getChat);
            return new TelegramGroup(chat);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getId() {
        return user.getId() + "";
    }
}
