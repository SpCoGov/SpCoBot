package top.spco.telegram;

import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.groupadministration.LeaveChat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import top.spco.api.Group;
import top.spco.api.MemberPermission;
import top.spco.api.message.Message;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

class TelegramGroup extends Group<Chat> {
    protected TelegramGroup(Chat group) {
        super(group);
    }

    /**
     * 获取该群群名称
     *
     * @return 群名称
     */
    @Override
    public String getName() {
        return wrapped().getTitle();
    }

    /**
     * 获取该群群主
     *
     * @return 群主对象
     */
    @Override
    public TelegramUser getOwner() {
        GetChatAdministrators getChatAdministrators = GetChatAdministrators.builder()
                .chatId(getId())
                .build();
        try {
            ArrayList<ChatMember> chatMembers = TelegramAdapter.getInstance().telegramClient.execute(getChatAdministrators);
            for (ChatMember chatMember : chatMembers) {
                if (chatMember.getStatus().equals("creator")) {
                    return new TelegramUser(chatMember.getUser());
                }
            }
            return null;
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 让机器人退出这个群
     *
     * @return 退出成功时返回 {@code true}; 已经退出时返回 {@code false}
     */
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
        // TODO: 修复这个
        return MemberPermission.OWNER;
    }

    /**
     * 获取机器人在群中的成员对象
     *
     * @return 成员对象
     */
    @Override
    public top.spco.api.User<?> botAsMember() {
        GetMe getMe = GetMe.builder()
                .build();
        try {
            User user = TelegramAdapter.getInstance().telegramClient.execute(getMe);
            return getMember(user.getId() + "");
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 查询群成员对象
     *
     * @param id 成员Id
     * @return 查询结果. 不存在时返回 {@code null}
     */
    @Override
    public top.spco.api.User<?> getMember(String id) {
        GetChatMember getChatMember = GetChatMember.builder()
                .chatId(getId())
                .userId(Long.parseLong(id))
                .build();
        try {
            ChatMember chatMember = TelegramAdapter.getInstance().telegramClient.execute(getChatMember);
            return new TelegramUser(chatMember.getUser());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    @Override
    public Set<top.spco.api.User<?>> getMembers() {
        Set<top.spco.api.User<?>> administrators = new HashSet<>();
        GetChatAdministrators getChatAdministrators = GetChatAdministrators.builder()
                .chatId(getId())
                .build();
        try {
            ArrayList<ChatMember> chatMembers = TelegramAdapter.getInstance().telegramClient.execute(getChatAdministrators);
            for (ChatMember chatMember : chatMembers) {
                administrators.add(new TelegramUser(chatMember.getUser()));
            }
            return administrators;
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
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
    public String getId() {
        return wrapped().getId() + "";
    }
}
