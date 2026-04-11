package top.spco.qq.payload.handler.message;

import top.spco.qq.payload.handler.EventTypeHandler;
import top.spco.qq.payload.handler.PostPayloadHandler;

public class MessageSentEventHandler extends EventTypeHandler {
    @Override
    public String getSubtypeKeyName() {
        return "message_sent_type";
    }

    public MessageSentEventHandler() {
        register("self", PostPayloadHandler.EMPTY);
    }
}
