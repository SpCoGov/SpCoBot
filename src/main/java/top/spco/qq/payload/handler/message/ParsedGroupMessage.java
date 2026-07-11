package top.spco.qq.payload.handler.message;

import top.spco.api.message.MessageChain;
import top.spco.qq.QQBot;
import top.spco.qq.napcat.NapCatGroup;
import top.spco.qq.napcat.NapCatMember;

public class ParsedGroupMessage {
    private final QQBot bot;
    private final NapCatGroup group;
    private final NapCatMember sender;
    private final MessageChain message;
    private final long time;

    public ParsedGroupMessage(QQBot bot, NapCatGroup group, NapCatMember sender, MessageChain message, long time) {
        this.bot = bot;
        this.group = group;
        this.sender = sender;
        this.message = message;
        this.time = time;
    }

    public QQBot getBot() {
        return bot;
    }

    public NapCatGroup getGroup() {
        return group;
    }

    public NapCatMember getSender() {
        return sender;
    }

    public MessageChain getMessage() {
        return message;
    }

    public long getTime() {
        return time;
    }
}
