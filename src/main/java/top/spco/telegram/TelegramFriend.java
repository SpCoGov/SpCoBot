package top.spco.telegram;

import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import top.spco.api.Friend;
import top.spco.api.FriendGroup;
import top.spco.api.message.Message;

import java.io.File;

class TelegramFriend extends Friend<User> {
    public TelegramFriend(User friend) {
        super(friend);
    }

    /**
     * 该好友所在的好友分组
     *
     * @deprecated Telegram无此功能
     */
    @Override
    @Deprecated
    public FriendGroup<?> getFriendGroup() {
        return null;
    }

    /**
     * 删除并屏蔽该好友, 屏蔽后对方将无法发送临时会话消息
     *
     * @deprecated Telegram无此功能
     */
    @Override
    @Deprecated
    public void delete() {
    }

    /**
     * 备注信息<p>
     * 仅与 {@link User} 存在好友关系的时候才可能存在备注<p>
     * 与 {@link User} 没有好友关系时永远为空{@link String 字符串} ("")
     *
     * @return 备注信息
     */
    @Override
    public String getRemark() {
        return getNick();
    }

    /**
     * 戳一戳
     *
     * @deprecated Telegram无此功能
     */
    @Override
    @Deprecated
    public void nudge() {

    }

    /**
     * 昵称
     *
     * @return 昵称
     */
    @Override
    public String getNick() {
        return Telegram.getUserNick(wrapped());
    }

    @Override
    public boolean isBot() {
        return wrapped().getIsBot();
    }

    @Override
    public void sendMessage(String message) {
        try {
            TelegramMessageSender.sendMessage(Telegram.getInstance().telegramClient, String.valueOf(getId()), message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendMessage(Message<?> message) {
        TelegramMessageSender.sendMessage(Telegram.getInstance().telegramClient, String.valueOf(getId()), ((TelegramMessage) message).wrapped());
    }

    @Override
    public void sendImage(File image) {
        try {
            TelegramMessageSender.sendImage(Telegram.getInstance().telegramClient, String.valueOf(getId()), image);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getId() {
        return wrapped().getId();
    }
}
