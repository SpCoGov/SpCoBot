package top.spco.telegram;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.games.Animation;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;
import org.telegram.telegrambots.meta.api.objects.polls.PollOption;
import org.telegram.telegrambots.meta.api.objects.polls.input.InputPollOption;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

class TelegramMessageSender {
    private static Message sendMessage(TelegramClient telegramClient, int replyToMessageId, boolean disableNotification, String chatId, Message message) throws TelegramApiException {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .replyToMessageId(replyToMessageId)
                .disableNotification(disableNotification)

                .text(message.getText())
                .entities(nullToEmpty(message.getEntities()))
                .build();
        return telegramClient.execute(sendMessage);
    }

    private static Message sendAudio(TelegramClient telegramClient, int replyToMessageId, boolean disableNotification, String chatId, Message message) throws TelegramApiException {
        SendAudio sendAudio = SendAudio.builder()
                .chatId(chatId)
                .replyToMessageId(replyToMessageId)
                .disableNotification(disableNotification)

                .audio(new InputFile(message.getAudio().getFileId()))
                .caption(message.getCaption())
                .captionEntities(nullToEmpty(message.getCaptionEntities()))
                .thumbnail(thumbnailOrNull(message.getAudio().getThumbnail()))
                .build();
        return telegramClient.execute(sendAudio);
    }

    private static Message sendDocument(TelegramClient telegramClient, int replyToMessageId, boolean disableNotification, String chatId, Message message) throws TelegramApiException {
        SendDocument sendDocument = SendDocument.builder()
                .chatId(chatId)
                .replyToMessageId(replyToMessageId)
                .disableNotification(disableNotification)

                .document(new InputFile(message.getDocument().getFileId()))
                .caption(message.getCaption())
                .captionEntities((message.getCaptionEntities()))
                .thumbnail(thumbnailOrNull(message.getDocument().getThumbnail()))
                .build();
        return telegramClient.execute(sendDocument);
    }

    private static Message sendPhoto(TelegramClient telegramClient, int replyToMessageId, boolean disableNotification, String chatId, Message message) throws TelegramApiException {
        String fileId = message.getPhoto().get(0).getFileId();
        SendPhoto sendPhoto;
        if (fileId.startsWith("filepath://")) {
            File file = new File(fileId.substring("filepath://".length()));
            sendPhoto = SendPhoto.builder()
                    .chatId(chatId)
                    .replyToMessageId(replyToMessageId)
                    .disableNotification(disableNotification)

                    .photo(new InputFile(file))
                    .caption(message.getCaption())
                    .captionEntities(nullToEmpty(message.getCaptionEntities()))
                    .build();
        } else {
            sendPhoto = SendPhoto.builder()
                    .chatId(chatId)
                    .replyToMessageId(replyToMessageId)
                    .disableNotification(disableNotification)

                    .photo(new InputFile(fileId))
                    .caption(message.getCaption())
                    .captionEntities(nullToEmpty(message.getCaptionEntities()))
                    .build();
        }
        return telegramClient.execute(sendPhoto);
    }

    private static Message sendSticker(TelegramClient telegramClient, int replyToMessageId, boolean disableNotification, String chatId, Message message) throws TelegramApiException {
        SendSticker sendSticker = SendSticker.builder()
                .chatId(chatId)
                .replyToMessageId(replyToMessageId)
                .disableNotification(disableNotification)

                .sticker(new InputFile(message.getSticker().getFileId()))
                .emoji(message.getSticker().getEmoji())
                .build();
        return telegramClient.execute(sendSticker);
    }

    private static Message sendVideo(TelegramClient telegramClient, int replyToMessageId, boolean disableNotification, String chatId, Message message) throws TelegramApiException {
        SendVideo sendVideo = SendVideo.builder()
                .chatId(chatId)
                .replyToMessageId(replyToMessageId)
                .disableNotification(disableNotification)

                .video(new InputFile(message.getVideo().getFileId()))
                .thumbnail(thumbnailOrNull(message.getVideo().getThumbnail()))
                .caption(message.getCaption())
                .captionEntities(nullToEmpty(message.getCaptionEntities()))
                .build();
        return telegramClient.execute(sendVideo);
    }

    private static Message sendVideoNote(TelegramClient telegramClient, int replyToMessageId, boolean disableNotification, String chatId, Message message) throws TelegramApiException {
        SendVideoNote sendVideoNote = SendVideoNote.builder()
                .chatId(chatId)
                .replyToMessageId(replyToMessageId)
                .disableNotification(disableNotification)
                .videoNote(new InputFile(message.getVideoNote().getFileId()))
                .duration(message.getVideoNote().getDuration())
                .length(message.getVideoNote().getLength())
                .thumbnail(thumbnailOrNull(message.getVideoNote().getThumbnail()))
                .build();
        return telegramClient.execute(sendVideoNote);
    }

    private static Message sendVoice(TelegramClient telegramClient, int replyToMessageId, boolean disableNotification, String chatId, Message message) throws TelegramApiException {
        SendVoice sendVideo = SendVoice.builder()
                .chatId(chatId)
                .replyToMessageId(replyToMessageId)
                .disableNotification(disableNotification)

                .voice(new InputFile(message.getVoice().getFileId()))
                .duration(message.getVoice().getDuration())
                .caption(message.getCaption())
                .captionEntities(nullToEmpty(message.getCaptionEntities()))
                .build();
        return telegramClient.execute(sendVideo);
    }


    private static Message sendPoll(TelegramClient telegramClient, int replyToMessageId, boolean disableNotification, String chatId, Message message) throws TelegramApiException {
        Poll poll = message.getPoll();
        List<InputPollOption> inputPollOptions = new ArrayList<>();
        for (PollOption pollOption : poll.getOptions()) {
            InputPollOption inputPollOption = new InputPollOption(pollOption.getText());
            inputPollOption.setTextEntities(nullToEmpty(pollOption.getTextEntities()));
            inputPollOptions.add(inputPollOption);
        }
        SendPoll sendPoll = SendPoll.builder()
                .chatId(chatId)
                .replyToMessageId(replyToMessageId)
                .disableNotification(disableNotification)

                .question(poll.getQuestion())
                .openPeriod(poll.getOpenPeriod())
                .closeDate(poll.getCloseDate())
                .explanation(poll.getExplanation())
                .explanationEntities(nullToEmpty(poll.getExplanationEntities()))
                .options(inputPollOptions)
                .build();
        return telegramClient.execute(sendPoll);
    }

    private static Message sendDice(TelegramClient telegramClient, int replyToMessageId, boolean disableNotification, String chatId, Message message) throws TelegramApiException {
        SendDice sendDice = SendDice.builder()
                .chatId(chatId)
                .replyToMessageId(replyToMessageId)
                .disableNotification(disableNotification)

                .emoji(message.getDice().getEmoji())
                .build();
        return telegramClient.execute(sendDice);
    }

    private static Message sendContact(TelegramClient telegramClient, int replyToMessageId, boolean disableNotification, String chatId, Message message) throws TelegramApiException {
        Contact contact = message.getContact();
        SendContact sendContact = SendContact.builder()
                .chatId(chatId)
                .replyToMessageId(replyToMessageId)
                .disableNotification(disableNotification)

                .phoneNumber(contact.getPhoneNumber())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .vCard(contact.getVCard())
                .build();
        return telegramClient.execute(sendContact);
    }

    private static Message sendLocation(TelegramClient telegramClient, int replyToMessageId, boolean disableNotification, String chatId, Message message) throws TelegramApiException {
        Location location = message.getLocation();
        SendLocation sendLocation = SendLocation.builder()
                .chatId(chatId)
                .replyToMessageId(replyToMessageId)
                .disableNotification(disableNotification)

                .horizontalAccuracy(location.getHorizontalAccuracy())
                .heading(location.getHeading())
                .proximityAlertRadius(location.getProximityAlertRadius())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .livePeriod(location.getLivePeriod())
                .build();
        return telegramClient.execute(sendLocation);
    }

    private static Message sendVenue(TelegramClient telegramClient, int replyToMessageId, boolean disableNotification, String chatId, Message message) throws TelegramApiException {
        Venue venue = message.getVenue();
        Location location = venue.getLocation();
        SendVenue sendVenue = SendVenue.builder()
                .chatId(chatId)
                .replyToMessageId(replyToMessageId)
                .disableNotification(disableNotification)

                .title(venue.getTitle())
                .address(venue.getAddress())
                .foursquareId(venue.getFoursquareId())
                .foursquareType(venue.getFoursquareType())
                .googlePlaceId(venue.getGooglePlaceId())
                .googlePlaceType(venue.getGooglePlaceType())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build();
        return telegramClient.execute(sendVenue);
    }

    private static Message sendAnimation(TelegramClient telegramClient, int replyToMessageId, boolean disableNotification, String chatId, Message message) throws TelegramApiException {
        Animation animation = message.getAnimation();
        SendAnimation sendAnimation = SendAnimation.builder()
                .chatId(chatId)
                .replyToMessageId(replyToMessageId)
                .disableNotification(disableNotification)

                .animation(new InputFile(message.getAnimation().getFileId()))
                .width(animation.getWidth())
                .height(animation.getHeight())
                .duration(animation.getDuration())
                .thumbnail(thumbnailOrNull(animation.getThumbnail()))
                .caption(message.getCaption())
                .captionEntities(nullToEmpty(message.getCaptionEntities()))
                .build();
        return telegramClient.execute(sendAnimation);
    }

    private static Message sendGame(TelegramClient telegramClient, int replyToMessageId, boolean disableNotification, String chatId, Message message) throws TelegramApiException {
        SendGame sendGame = SendGame.builder()
                .chatId(chatId)
                .replyToMessageId(replyToMessageId)
                .disableNotification(disableNotification)

                .gameShortName(message.getGame().getTitle())
                .build();
        return telegramClient.execute(sendGame);
    }

    static Message sendImage(TelegramClient telegramClient, String chatId, File image) throws TelegramApiException {
        SendPhoto sendPhoto = SendPhoto.builder()
                .chatId(chatId)
                .photo(new InputFile(image))
                .build();
        return telegramClient.execute(sendPhoto);
    }

    static Message sendMessage(TelegramClient telegramClient, String chatId, String message) throws TelegramApiException {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .parseMode(ParseMode.MARKDOWN)
                .text(message)
                .build();
        return telegramClient.execute(sendMessage);
    }

    static List<Message> sendMessage(TelegramClient telegramClient, String chatId, Message message) {
        boolean disableNotification = false;
        List<Message> sentMessages = new ArrayList<>();
        final int replyToMessageId;
        if (message.isReply()) {
            replyToMessageId = message.getReplyToMessage().getMessageId();
        } else {
            replyToMessageId = 0;
        }
        forAllMessageComponent(message,
                () -> {
                    try {
                        sentMessages.add(sendMessage(telegramClient, replyToMessageId, disableNotification, chatId, message));
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                },
                () -> {
                    try {
                        sentMessages.add(sendAudio(telegramClient, replyToMessageId, disableNotification, chatId, message));
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                },
                () -> {
                    try {
                        sentMessages.add(sendDocument(telegramClient, replyToMessageId, disableNotification, chatId, message));
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                },
                () -> {
                    try {
                        sentMessages.add(sendPhoto(telegramClient, replyToMessageId, disableNotification, chatId, message));
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                },
                () -> {
                    try {
                        sentMessages.add(sendSticker(telegramClient, replyToMessageId, disableNotification, chatId, message));
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                },
                () -> {
                    try {
                        sentMessages.add(sendVideo(telegramClient, replyToMessageId, disableNotification, chatId, message));
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                },
                () -> {
                    try {
                        sentMessages.add(sendVideoNote(telegramClient, replyToMessageId, disableNotification, chatId, message));
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                },
                () -> {
                    try {
                        sentMessages.add(sendVoice(telegramClient, replyToMessageId, disableNotification, chatId, message));
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                },
                () -> {
                    try {
                        sentMessages.add(sendPoll(telegramClient, replyToMessageId, disableNotification, chatId, message));
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                },
                () -> {
                    try {
                        sentMessages.add(sendDice(telegramClient, replyToMessageId, disableNotification, chatId, message));
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                },
                () -> {
                    try {
                        sentMessages.add(sendContact(telegramClient, replyToMessageId, disableNotification, chatId, message));
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                },
                () -> {
                    try {
                        sentMessages.add(sendLocation(telegramClient, replyToMessageId, disableNotification, chatId, message));
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                },
                () -> {
                    try {
                        sentMessages.add(sendVenue(telegramClient, replyToMessageId, disableNotification, chatId, message));
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                },
                () -> {
                    try {
                        sentMessages.add(sendAnimation(telegramClient, replyToMessageId, disableNotification, chatId, message));
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                },
                () -> {
                    try {
                        sentMessages.add(sendGame(telegramClient, replyToMessageId, disableNotification, chatId, message));
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        return sentMessages;
    }

    static void appendText(Message appended, String appendage) {
        if (appendage == null || appendage.isEmpty()) {
            return;
        }
        if (appended.hasText()) {
            appended.setText(appended.getText() + appendage);
        } else {
            appended.setText(appendage);
        }
    }

    private static void appendText(Message appended, Message appendage) {
        if (appendage == null || !appendage.hasText()) {
            return;
        }
        List<MessageEntity> clonedEntities = cloneEntities(appendage.getEntities());
        if (appended.hasText()) {
            int originalTextLength = appended.getText().length();
            appended.setText(appended.getText() + appendage.getText());
            if (!clonedEntities.isEmpty()) {
                for (MessageEntity entity : clonedEntities) {
                    entity.setOffset(entity.getOffset() + originalTextLength);
                }
                if (appended.getEntities() == null) {
                    appended.setEntities(new ArrayList<>());
                }
                appended.getEntities().addAll(clonedEntities);
            }
        } else {
            appended.setText(appendage.getText());
            appended.setEntities(clonedEntities);
        }
    }

    private static void appendCaption(Message appended, Message appendage) {
        if (appendage == null || !appendage.hasCaption()) {
            return;
        }
        List<MessageEntity> clonedEntities = cloneEntities(appendage.getCaptionEntities());
        if (appended.hasCaption()) {
            appended.setCaption(appended.getCaption() + appendage);
            for (MessageEntity entity : clonedEntities) {
                entity.setOffset(entity.getOffset() + appended.getCaption().length());
            }
            if (appended.getCaptionEntities() == null) {
                appended.setCaptionEntities(new ArrayList<>());
            }
            appended.getCaptionEntities().addAll(clonedEntities);
        } else {
            appended.setCaption(appendage.getCaption());
            appended.setCaptionEntities(clonedEntities);
        }
    }

    private static List<MessageEntity> cloneEntities(List<MessageEntity> entities) {
        List<MessageEntity> clonedEntities = new ArrayList<>();
        if (entities == null || entities.isEmpty()) {
            return clonedEntities;
        }
        for (MessageEntity entity : entities) {
            MessageEntity cloned = new MessageEntity(entity.getType(), entity.getOffset(), entity.getLength(), entity.getUrl(), entity.getUser(), entity.getLanguage(), entity.getCustomEmojiId(), entity.getText());
            clonedEntities.add(cloned);
        }
        return clonedEntities;
    }

    static void appendMessage(Message appended, Message appendage) {
        AtomicBoolean captionAppended = new AtomicBoolean(false);
        forAllMessageComponent(appendage,
                () -> appendText(appended, appendage),
                () -> {
                    if (!appended.hasAudio()) {
                        appended.setAudio(appendage.getAudio());
                        if (!captionAppended.get()) {
                            appendCaption(appended, appendage);
                            captionAppended.set(true);
                        }
                    }
                },
                () -> {
                    if (!appended.hasDocument() && !appended.hasAnimation()) {
                        appended.setDocument(appendage.getDocument());
                        if (!captionAppended.get()) {
                            appendCaption(appended, appendage);
                            captionAppended.set(true);
                        }
                    }
                },
                () -> {
                    if (!appended.hasPhoto()) {
                        appended.setPhoto(appendage.getPhoto());
                    } else {
                        appended.getPhoto().addAll(appendage.getPhoto());
                    }
                    if (!captionAppended.get()) {
                        appendCaption(appended, appendage);
                        captionAppended.set(true);
                    }
                },
                () -> {
                    if (!appended.hasSticker()) {
                        appended.setSticker(appendage.getSticker());
                    }
                },
                () -> {
                    if (!appended.hasVideo()) {
                        appended.setVideo(appendage.getVideo());
                        if (!captionAppended.get()) {
                            appendCaption(appended, appendage);
                            captionAppended.set(true);
                        }
                    }
                },
                () -> {
                    if (!appended.hasVideoNote()) {
                        appended.setVideoNote(appendage.getVideoNote());
                    }
                },
                () -> {
                    if (!appended.hasVoice()) {
                        appended.setVoice(appendage.getVoice());
                        if (!captionAppended.get()) {
                            appendCaption(appended, appendage);
                            captionAppended.set(true);
                        }
                    }
                },
                () -> {
                    if (!appended.hasPoll()) {
                        appended.setPoll(appendage.getPoll());
                    }
                },
                () -> {
                    if (!appended.hasDice()) {
                        appended.setDice(appendage.getDice());
                    }
                },
                () -> {
                    if (!appended.hasContact()) {
                        appended.setContact(appendage.getContact());
                    }
                },
                () -> {
                    if (!appended.hasLocation()) {
                        appended.setLocation(appendage.getLocation());
                    }
                },
                () -> {
                    if (!appended.hasLocation() && appended.getVenue() != null) {
                        appended.setLocation(appendage.getLocation());
                        appended.setVenue(appendage.getVenue());
                    }
                },
                () -> {
                    if (!appended.hasAnimation() && !appended.hasDocument()) {
                        appended.setAnimation(appendage.getAnimation());
                        appended.setDocument(appendage.getDocument());
                        if (!captionAppended.get()) {
                            appendCaption(appended, appendage);
                            captionAppended.set(true);
                        }
                    }
                },
                () -> {
                    if (!appended.hasGame()) {
                        appended.setGame(appendage.getGame());
                    }
                }
        );
    }

    private static void forAllMessageComponent(Message message, Runnable text, Runnable audio, Runnable document, Runnable photo,
                                               Runnable sticker, Runnable video, Runnable videoNote, Runnable voice, Runnable poll, Runnable dice,
                                               Runnable contract, Runnable location, Runnable venue, Runnable animation, Runnable game) {
        if (message.hasText()) {
            text.run();
        }
        if (message.hasAudio()) {
            audio.run();
        }
        if (message.hasDocument() && !message.hasAnimation()) {
            document.run();
        }
        if (message.hasPhoto()) {
            photo.run();
        }
        if (message.hasSticker()) {
            sticker.run();
        }
        if (message.hasVideo()) {
            video.run();
        }
        if (message.hasVideoNote()) {
            videoNote.run();
        }
        if (message.hasVoice()) {
            voice.run();
        }
        if (message.hasPoll()) {
            poll.run();
        }
        if (message.hasDice()) {
            dice.run();
        }
        if (message.hasContact()) {
            contract.run();
        }
        if (message.hasLocation() && message.getVenue() == null) {
            location.run();
        }
        if (message.hasLocation() && message.getVenue() != null) {
            venue.run();
        }
        if (message.hasAnimation()) {
            animation.run();
        }
        if (message.hasGame()) {
            game.run();
        }
    }

    private static <T> List<T> nullToEmpty(List<T> list) {
        return list == null ? new ArrayList<>() : list;
    }

    private static InputFile thumbnailOrNull(PhotoSize photoSize) {
        return photoSize == null ? null : new InputFile(photoSize.getFileId());
    }
}