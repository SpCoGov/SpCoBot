package top.spco.telegram;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import top.spco.api.Group;
import top.spco.api.MemberPermission;
import top.spco.api.message.Member;
import top.spco.api.message.MessageChain;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;

class TelegramMember extends Member {
    private final Chat chat;
    private final ChatMember chatMember;

    TelegramMember(Chat chat, ChatMember chatMember) {
        this.chat = chat;
        this.chatMember = chatMember;
    }

    @Override
    public Group getGroup() {
        // TODO: 实现getGroup
        return null;
    }

    @Override
    public String getName() {
        return TelegramAdapter.getUserNick(chatMember.getUser());
    }

    @Override
    public boolean isBot() {
        return chatMember.getUser().getIsBot();
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
        return chatMember.getUser().getId() + "";
    }

    MemberPermission getPermission() {
        return TelegramGroup.toPermission(chatMember.getStatus());
    }
}
