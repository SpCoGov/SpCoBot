package top.spco.qq.payload.handler.message;

import top.spco.api.message.MessageChain;
import top.spco.qq.QQBot;
import top.spco.qq.napcat.NapCatUser;

public class ParsedPrivateMessage {
    private final QQBot bot;
    private final NapCatUser sender;
    private final MessageChain message;
    private final int time;

    public ParsedPrivateMessage(QQBot bot, NapCatUser sender, MessageChain message, int time) {
        this.bot = bot;
        this.sender = sender;
        this.message = message;
        this.time = time;
    }

    public QQBot getBot() {
        return bot;
    }

    public NapCatUser getSender() {
        return sender;
    }

    public MessageChain getMessage() {
        return message;
    }

    public int getTime() {
        return time;
    }
}
