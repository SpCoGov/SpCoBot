package top.spco.telegram;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.ChatFullInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import top.spco.SpCoBot;
import top.spco.api.Image;
import top.spco.api.Interactive;
import top.spco.api.message.Message;
import top.spco.api.message.MessageSource;
import top.spco.api.message.service.MessageService;
import top.spco.util.tuple.ImmutablePair;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TelegramMessageServiceImpl implements MessageService {
    @Override
    public Message<?> at(long id) {
        return at(id, null);
    }

    @Override
    public Message<?> at(long id, String message) {
        GetChat getChat = GetChat.builder().chatId(id).build();
        String firstName = "";
        try {
            ChatFullInfo chat = Telegram.getInstance().telegramClient.execute(getChat);
            if (chat.getFirstName() != null && !chat.getFirstName().isEmpty()) {
                firstName = chat.getFirstName();
            }
        } catch (TelegramApiException e) {
            SpCoBot.LOGGER.warn("获取用户信息失败", e);
        }
        if (message == null || message.isEmpty()) {
            if (!firstName.isEmpty()) {
                message = firstName;
            } else {
                message = "@" + id;
            }
        }
        org.telegram.telegrambots.meta.api.objects.message.Message atMessage = new org.telegram.telegrambots.meta.api.objects.message.Message();
        atMessage.setText(message);
        MessageEntity atMessageEntity = MessageEntity.builder()
                .type("mention")
                .user(new User(id, firstName, false))
                .offset(0)
                .length(message.length())
                .text(message)
                .build();
        List<MessageEntity> entities = new ArrayList<>();
        entities.add(atMessageEntity);
        atMessage.setEntities(entities);
        return new TelegramMessage(atMessage);
    }

    @Deprecated
    @Override
    public Message<?> atAll() {
        return null;
    }

    @Override
    public long getFirstMentioned(Message<?> message, String phrase) {
        List<MessageEntity> entities = ((TelegramMessage) message).wrapped().getEntities();
        if (entities != null && !entities.isEmpty()) {
            return entities.stream()
                    .min(Comparator.comparingInt(MessageEntity::getOffset))
                    .map(MessageEntity::getOffset)
                    .orElse(-1);
        }
        try {
            return Long.parseLong(phrase);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * 获取消息所引用的消息
     *
     * @param message 源消息
     * @return 如果有引用时返回被引用的消息，如果没有时返回null
     */
    @Override
    public @Nullable ImmutablePair<@NotNull MessageSource<?>, @NotNull Message<?>> getQuote(Message<?> message) {
        org.telegram.telegrambots.meta.api.objects.message.Message message1 = ((TelegramMessage) message).wrapped();
        return new ImmutablePair<>(new TelegramMessageSource(message1), new TelegramMessage(message1.getReplyToMessage()));
    }

    /**
     * 撤回一条消息<p>
     * 当机器人撤回自己的消息时，不需要权限。
     *
     * @param original 需要撤回的消息
     */
    @Override
    public void recall(MessageSource<?> original) {

    }

    /**
     * 将字符串转换为 {@code Message} 对象
     *
     * @param content 需要转换的内容
     */
    @Override
    public Message<?> asMessage(String content) {
        org.telegram.telegrambots.meta.api.objects.message.Message message = new org.telegram.telegrambots.meta.api.objects.message.Message();
        message.setText(content);
        return new TelegramMessage(message);
    }

    /**
     * 将文件转换为 {@code Image} 对象
     *
     * @param image       需要转换的图片
     * @param interactive 发送的对象
     */
    @Override
    public Image<?> toImage(File image, Interactive<?> interactive) {
        return new TelegramImageMessage(image);
    }

    /**
     * 将输入流转换为 {@code Image} 对象
     *
     * @param image       需要转换的图片
     * @param interactive 发送的对象
     */
    @Override
    public Image<?> toImage(InputStream image, Interactive<?> interactive) {
        return new TelegramImageMessage(new InputFile(image, "image"));
    }
}
