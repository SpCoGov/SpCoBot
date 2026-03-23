package top.spco.telegram;

import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.groupadministration.LeaveChat;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import top.spco.api.Group;
import top.spco.api.MemberPermission;
import top.spco.api.message.Member;
import top.spco.api.message.MessageChain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

class TelegramGroup extends Group {
    private final Chat chat;

    TelegramGroup(Chat group) {
        this.chat = group;
    }

    @Override
    public String getName() {
        return chat.getTitle();
    }

    @Override
    public TelegramMember getOwner() {
        GetChatAdministrators getChatAdministrators = GetChatAdministrators.builder()
                .chatId(getId())
                .build();
        try {
            ArrayList<ChatMember> chatMembers = TelegramAdapter.getInstance().telegramClient.execute(getChatAdministrators);
            for (ChatMember chatMember : chatMembers) {
                if ("creator".equals(chatMember.getStatus())) {
                    return new TelegramMember(chat, chatMember);
                }
            }
            return null;
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean quit() {
        LeaveChat leaveChat = LeaveChat.builder()
                .chatId(getId())
                .build();
        try {
            return TelegramAdapter.getInstance().telegramClient.execute(leaveChat);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MemberPermission botPermission() {
        return toPermission(fetchChatMember(TelegramAdapter.getSelf().getId() + "").getStatus());
    }

    @Override
    public Member botAsMember() {
        return getMember(TelegramAdapter.getSelf().getId() + "");
    }

    @Override
    public Member getMember(String id) {
        return new TelegramMember(chat, fetchChatMember(id));
    }

    @Deprecated
    @Override
    public Set<Member> getMembers() {
        Set<Member> administrators = new HashSet<>();
        GetChatAdministrators getChatAdministrators = GetChatAdministrators.builder()
                .chatId(getId())
                .build();
        try {
            ArrayList<ChatMember> chatMembers = TelegramAdapter.getInstance().telegramClient.execute(getChatAdministrators);
            for (ChatMember chatMember : chatMembers) {
                administrators.add(new TelegramMember(chat, chatMember));
            }
            return administrators;
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
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

    private ChatMember fetchChatMember(String id) {
        GetChatMember getChatMember = GetChatMember.builder()
                .chatId(getId())
                .userId(Long.parseLong(id))
                .build();
        try {
            return TelegramAdapter.getInstance().telegramClient.execute(getChatMember);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    static MemberPermission toPermission(String status) {
        if ("creator".equals(status)) {
            return MemberPermission.OWNER;
        }
        if ("administrator".equals(status)) {
            return MemberPermission.ADMINISTRATOR;
        }
        return MemberPermission.MEMBER;
    }
}
