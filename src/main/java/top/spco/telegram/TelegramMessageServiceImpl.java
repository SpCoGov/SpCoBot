package top.spco.telegram;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.ChatFullInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import top.spco.SpCoBot;
import top.spco.api.message.ImageMessage;
import top.spco.api.Identifiable;
import top.spco.api.Interactive;
import top.spco.api.exception.PlatformMismatchException;
import top.spco.api.message.Message;
import top.spco.api.message.MessageSource;
import top.spco.api.message.service.MessageService;
import top.spco.core.Platform;
import top.spco.util.tuple.ImmutablePair;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

class TelegramMessageServiceImpl implements MessageService {
    private void requireTelegram(Object target, String argumentName) {
        if (!(target instanceof Identifiable identifiable)) {
            return;
        }
        Platform platform = identifiable.getPlatform();
        if (platform != null && platform != Platform.TELEGRAM) {
            throw new PlatformMismatchException(argumentName, Platform.TELEGRAM, platform);
        }
    }

    @Override
    public Message at(long id) {
        return at(id, null);
    }

    @Override
    public Message at(long id, String message) {
        GetChat getChat = GetChat.builder().chatId(id).build();
        String firstName = "";
        try {
            ChatFullInfo chat = TelegramAdapter.getInstance().telegramClient.execute(getChat);
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
                .type("text_mention")
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
    public Message atAll() {
        return null;
    }

    @Override
    public String getFirstMentioned(Message message, String phrase) {
        requireTelegram(message, "message");
        List<MessageEntity> entities = ((TelegramMessage) message).getMessage().getEntities();
        if (entities != null && !entities.isEmpty()) {
            Optional<Long> mentionedId = entities.stream()
                    .filter(entity -> "mention".equals(entity.getType()) || "text_mention".equals(entity.getType())) // 过滤 mention 和 text_mention 类型
                    .min(Comparator.comparingInt(MessageEntity::getOffset)) // 获取第一个提及的实体
                    .map(entity -> {
                        if ("mention".equals(entity.getType())) {
                            throw new RuntimeException("无法通过@id来选中用户");
                        } else if ("text_mention".equals(entity.getType())) {
                            // 如果是 "text_mention"，直接使用 User 的 ID
                            return entity.getUser().getId();
                        }
                        return null;
                    });

            if (mentionedId.isPresent()) {
                return mentionedId.get() + "";
            }
        }
        try {
            return Long.parseLong(phrase)+ "";
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 获取消息所引用的消息
     *
     * @param message 源消息
     * @return 如果有引用时返回被引用的消息，如果没有时返回null
     */
    @Override
    public @Nullable ImmutablePair<@NotNull MessageSource, @NotNull Message> getQuote(Message message) {
        requireTelegram(message, "message");
        org.telegram.telegrambots.meta.api.objects.message.Message message1 = ((TelegramMessage) message).getMessage();
        if (!message1.isReply()) {
            return null;
        }
        return new ImmutablePair<>(new TelegramMessageSource(message1.getReplyToMessage()), new TelegramMessage(message1.getReplyToMessage()));
    }

    @Override
    public void recall(MessageSource original) {
        requireTelegram(original, "original");
        DeleteMessage deleteMessage = DeleteMessage.builder()
                .chatId(original.getFromId())
                .messageId(Integer.parseInt(original.getMessageId()))
                .build();
        try {
            TelegramAdapter.getInstance().telegramClient.execute(deleteMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将字符串转换为 {@code Message} 对象
     *
     * @param content 需要转换的内容
     */
    @Override
    public Message asMessage(String content) {
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
    public ImageMessage toImage(File image, Interactive interactive) {
        requireTelegram(interactive, "interactive");
        return new TelegramImageMessage(image);
    }

    /**
     * 将输入流转换为 {@code Image} 对象
     *
     * @param image       需要转换的图片
     * @param interactive 发送的对象
     */
    @Override
    public ImageMessage toImage(InputStream image, Interactive interactive) {
        requireTelegram(interactive, "interactive");
        return new TelegramImageMessage(new InputFile(image, "image"));
    }
}
