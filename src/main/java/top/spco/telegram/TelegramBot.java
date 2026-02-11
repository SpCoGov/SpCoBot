package top.spco.telegram;

import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import top.spco.api.*;

class TelegramBot extends Bot<org.telegram.telegrambots.meta.api.objects.User> {
    protected TelegramBot(org.telegram.telegrambots.meta.api.objects.User bot) {
        super(bot);
    }

    @Override
    public boolean isOnline() {
        return true;
    }

    /**
     * 获取机器人的昵称
     *
     * @return 机器人的昵称
     */
    @Override
    public String getNick() {
        return wrapped().getFirstName();
    }

    /**
     * 获取机器人作为好友的实例
     *
     * @return 机器人作为好友的实例
     */
    @Override
    public Friend<?> asFriend() {
        return new TelegramFriend(wrapped());
    }

    @Override
    @Deprecated
    public FriendGroups<?> getFriendGroups() {
        return null;
    }

    @Override
    @Deprecated
    public InteractiveList<Friend<?>> getFriends() {
        return null;
    }

    @Override
    @Deprecated
    public InteractiveList<Group<?>> getGroups() {
        return null;
    }

    @Override
    @Deprecated
    public Friend<?> getFriend(long id) {
        return null;
    }

    @Deprecated
    @Override
    public boolean hasFriend(long id) {
        return false;
    }

    @Deprecated
    @Override
    public boolean hasGroup(long id) {
        return false;
    }

    @Deprecated
    @Override
    public User<?> getUser(long id) {
        try {
            GetChat getChat = GetChat.builder()
                    .chatId(id)
                    .build();
            Chat chat = TelegramAdapter.getInstance().telegramClient.execute(getChat);
            return new TelegramUser(chat);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Group<?> getGroup(long id) {
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
    public long getId() {
        return wrapped().getId();
    }
}
