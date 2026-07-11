package top.spco.qq.payload.handler.message;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import top.spco.api.PlatformPermission;
import top.spco.api.message.Message;
import top.spco.api.message.MessageChain;
import top.spco.api.message.MessageSource;
import top.spco.qq.QQBot;
import top.spco.qq.message.MessageParser;
import top.spco.qq.message.ReplyMessage;
import top.spco.qq.napcat.NapCatGroup;
import top.spco.qq.napcat.NapCatMember;
import top.spco.qq.napcat.NapCatMessageSource;
import top.spco.qq.napcat.NapCatUser;

import java.util.ArrayList;

import static top.spco.util.JsonUtil.getAsLong;
import static top.spco.util.JsonUtil.getAsString;

public final class NapCatMessagePayloadParser {
    private NapCatMessagePayloadParser() {
    }

    public static ParsedGroupMessage parseGroupMessage(JsonObject payload) {
        String botId = getAsString(payload, "self_id");
        QQBot bot = new QQBot("SpCoBot", botId);
        JsonObject senderJsonObject = payload.get("sender").getAsJsonObject();
        long time = getAsLong(senderJsonObject, "time");
        String senderId = getAsString(senderJsonObject, "user_id");
        String senderNickName = getAsString(senderJsonObject, "nickname");
        String role = getAsString(senderJsonObject, "role");
        String messageId = getAsString(payload, "message_id");
        String groupId = getAsString(payload, "group_id");
        String groupName = getAsString(payload, "group_name");

        NapCatGroup group = new NapCatGroup(groupId, groupName);
        NapCatMember member = new NapCatMember(senderId, senderNickName, group, PlatformPermission.fromRole(role));
        MessageChain message = parseMessageChain(payload, groupId, senderId, groupId, messageId);
        return new ParsedGroupMessage(bot, group, member, message, time);
    }

    public static ParsedPrivateMessage parsePrivateMessage(JsonObject payload) {
        String botId = getAsString(payload, "self_id");
        QQBot bot = new QQBot("SpCoBot", botId);
        JsonObject senderJsonObject = payload.get("sender").getAsJsonObject();
        int time = getAsLong(payload, "time").intValue();
        String senderId = getAsString(payload, "user_id");
        String senderNickName = getAsString(senderJsonObject, "nickname");
        String messageId = getAsString(payload, "message_id");

        NapCatUser sender = new NapCatUser(senderId, senderNickName);
        MessageChain message = parseMessageChain(payload, senderId, senderId, senderId, messageId);
        return new ParsedPrivateMessage(bot, sender, message, time);
    }

    public static MessageChain parseMessageChain(JsonObject payload, String parserFromId, String sourceSenderId, String sourceFromId, String messageId) {
        JsonArray elements = payload.get("message").getAsJsonArray();
        JsonArray elementsRaw = null;
        if (payload.has("raw")) {
            elementsRaw = payload.get("raw").getAsJsonObject().get("elements").getAsJsonArray();
        }
        ArrayList<Message> messageComponents = new ArrayList<>();
        MessageSource replySource = null;
        for (int i = 0; i < elements.size(); i++) {
            JsonObject element = elements.get(i).getAsJsonObject();
            JsonObject elementRaw;
            if (elementsRaw != null) {
                elementRaw = elementsRaw.get(i).getAsJsonObject();
            } else {
                elementRaw = null;
            }
            Message component = MessageParser.getInstance().parse(element, elementRaw, parserFromId);

            if (component instanceof ReplyMessage replyMessage) {
                replySource = new NapCatMessageSource(replyMessage.getSenderId(), replyMessage.getFromId(), replyMessage.getReplyId(), null);
            } else {
                messageComponents.add(component);
            }
        }
        MessageChain messageChain = new MessageChain(messageComponents);
        if (replySource != null) {
            messageChain.setReplySource(replySource);
        }
        MessageSource source = new NapCatMessageSource(sourceSenderId, sourceFromId, messageId, messageChain);
        messageChain.setSource(source);
        return messageChain;
    }
}
