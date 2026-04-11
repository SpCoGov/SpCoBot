package top.spco.qq.napcat;

import top.spco.SpCoBot;
import top.spco.api.Group;
import top.spco.api.MemberPermission;
import top.spco.api.message.Member;
import top.spco.api.message.MessageChain;
import top.spco.qq.message.MessageSender;

import java.util.Set;

public class NapCatGroup extends Group {
    private final String id;
    private final String name;

    public NapCatGroup(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public Member getOwner() {
        return null;
    }

    @Override
    public boolean quit() {
        return false;
    }

    @Override
    public MemberPermission botPermission() {
        return null;
    }

    @Override
    public Member botAsMember() {
        return null;
    }

    @Override
    public Member getMember(String id) {
        return null;
    }

    @Override
    public Set<Member> getMembers() {
        return Set.of();
    }

    @Override
    public void sendMessage(MessageChain message) {
        try {
            SpCoBot.LOGGER.info(MessageSender.sendSyncGroupMessage(id, message));
        } catch (Exception e) {
            SpCoBot.LOGGER.error(e);
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }
}
