package top.spco.telegram;

import org.telegram.telegrambots.meta.api.methods.groupadministration.BanChatMember;
import org.telegram.telegrambots.meta.api.methods.groupadministration.BanChatSenderChat;
import org.telegram.telegrambots.meta.api.methods.groupadministration.UnbanChatSenderChat;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberAdministrator;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberOwner;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import top.spco.api.Group;
import top.spco.api.MemberPermission;
import top.spco.api.NormalMember;
import top.spco.api.message.Message;
import top.spco.util.tuple.ImmutablePair;
import top.spco.util.tuple.Pair;

import java.io.File;

class TelegramMember extends NormalMember<Pair<ChatMember, Chat>> {
    public TelegramMember(ChatMember member, Chat chat) {
        super(new ImmutablePair<>(member, chat));
    }

    @Override
    @Deprecated
    public Group<?> getGroup() {
        return null;
    }

    @Deprecated
    @Override
    public String getNameCard() {
        return "";
    }

    @Override
    public String getSpecialTitle() {
        if (wrapped().getLeft() instanceof ChatMemberAdministrator admin) {
            return admin.getCustomTitle();
        } else if (wrapped().getLeft() instanceof ChatMemberOwner owner) {
            return owner.getCustomTitle();
        }
        return "";
    }

    /**
     * 被禁言剩余时长. 单位为秒
     *
     * @deprecated Telegram无此功能
     */
    @Deprecated
    @Override
    public int muteTimeRemaining() {
        return 0;
    }

    /**
     * 当该群员处于禁言状态时返回 {@code true}.
     *
     * @deprecated Telegram无此功能
     */
    @Override
    @Deprecated
    public boolean isMuted() {
        return false;
    }

    @Override
    public void unmute() {
        UnbanChatSenderChat unmute = UnbanChatSenderChat.builder()
                .chatId(wrapped().getRight().getId())
                .senderChatId(wrapped().getLeft().getUser().getId())
                .build();
        try {
            TelegramAdapter.getInstance().telegramClient.execute(unmute);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void kick(String message, boolean block) {
        BanChatMember banChatMember = BanChatMember.builder()
                .chatId(wrapped().getRight().getId())
                .userId(wrapped().getLeft().getUser().getId())
                .build();
        try {
            TelegramAdapter.getInstance().telegramClient.execute(banChatMember);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    @Override
    public boolean isFriend() {
        return false;
    }

    @Override
    public MemberPermission getPermission() {
        if (wrapped().getLeft() instanceof ChatMemberAdministrator) {
            return MemberPermission.ADMINISTRATOR;
        } else if (wrapped().getLeft() instanceof ChatMemberOwner) {
            return MemberPermission.OWNER;
        }
        return MemberPermission.OWNER;
    }

    @Override
    public void mute(int time) {
        BanChatSenderChat mute = BanChatSenderChat.builder()
                .chatId(wrapped().getRight().getId())
                .senderChatId(wrapped().getLeft().getUser().getId())
                .untilDate(time)
                .build();
        try {
            TelegramAdapter.getInstance().telegramClient.execute(mute);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    @Override
    public String getRemark() {
        return "";
    }

    @Deprecated
    @Override
    public void nudge() {

    }

    @Override
    public String getNick() {
        return TelegramAdapter.getUserNick(wrapped().getLeft().getUser());
    }

    @Override
    public boolean isBot() {
        return wrapped().getLeft().getUser().getIsBot();
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
        TelegramMessageSender.sendMessage(TelegramAdapter.getInstance().telegramClient, String.valueOf(getId()), ((TelegramMessage) message).wrapped());
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
        return wrapped().getLeft().getUser().getId();
    }
}
