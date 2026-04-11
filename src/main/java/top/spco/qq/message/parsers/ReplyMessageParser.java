package top.spco.qq.message.parsers;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.Logger;
import top.spco.SpCoBot;
import top.spco.api.message.Message;
import top.spco.qq.message.MessageComponentParser;
import top.spco.qq.message.ReplyMessage;
import top.spco.util.JsonUtil;

public class ReplyMessageParser extends MessageComponentParser {
    @Override
    public String componentName() {
        return "reply";
    }

    @Override
    public Message parse(JsonObject data, JsonObject raw, String fromId) {
        JsonObject replyElement = raw.get("replyElement").getAsJsonObject();
        String senderId = replyElement.get("senderUid").getAsString();
        return new ReplyMessage(JsonUtil.getAsString(data, "id"), senderId, fromId);
    }

    @Override
    public boolean supports(Message message) {
        return message instanceof ReplyMessage;
    }

    @Override
    public JsonObject serialize(Message message) {
        ReplyMessage replyMessage = (ReplyMessage) message;
        JsonObject segment = new JsonObject();
        segment.addProperty("type", componentName());

        JsonObject data = new JsonObject();
        data.addProperty("id", replyMessage.getReplyId());
        segment.add("data", data);
        return segment;
    }
}
