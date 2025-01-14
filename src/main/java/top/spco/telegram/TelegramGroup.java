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
import top.spco.api.InteractiveList;
import top.spco.api.MemberPermission;
import top.spco.api.NormalMember;
import top.spco.api.message.Message;

import java.io.File;
import java.util.ArrayList;

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
    public NormalMember<?> getOwner() {
        GetChatAdministrators getChatAdministrators = GetChatAdministrators.builder()
                .chatId(getId())
                .build();
        try {
            ArrayList<ChatMember> chatMembers = Telegram.getInstance().telegramClient.execute(getChatAdministrators);
            for (ChatMember chatMember : chatMembers) {
                if (chatMember.getStatus().equals("creator")) {
                    return new TelegramMember(chatMember, wrapped());
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
            return Telegram.getInstance().telegramClient.execute(leaveChat);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MemberPermission botPermission() {
        return botAsMember().getPermission();
    }

    /**
     * 获取机器人在群中的成员对象
     *
     * @return 成员对象
     */
    @Override
    public NormalMember<?> botAsMember() {
        GetMe getMe = GetMe.builder()
                .build();
        try {
            User user = Telegram.getInstance().telegramClient.execute(getMe);
            return getMember(user.getId());
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
    public NormalMember<?> getMember(long id) {
        GetChatMember getChatMember = GetChatMember.builder()
                .chatId(getId())
                .userId(id)
                .build();
        try {
            ChatMember chatMember = Telegram.getInstance().telegramClient.execute(getChatMember);
            return new TelegramMember(chatMember, wrapped());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    @Override
    public InteractiveList<NormalMember<?>> getMembers() {
        InteractiveList<NormalMember<?>> administrators = new InteractiveList<>();
        GetChatAdministrators getChatAdministrators = GetChatAdministrators.builder()
                .chatId(getId())
                .build();
        try {
            ArrayList<ChatMember> chatMembers = Telegram.getInstance().telegramClient.execute(getChatAdministrators);
            for (ChatMember chatMember : chatMembers) {
                administrators.add(new TelegramMember(chatMember, wrapped()));
            }
            return administrators;
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
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
