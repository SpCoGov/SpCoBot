package top.spco.qq.napcat;

import top.spco.SpCoBot;
import top.spco.api.Group;
import top.spco.api.MemberPermission;
import top.spco.api.PlatformPermission;
import top.spco.api.message.Member;
import top.spco.api.message.MessageChain;
import top.spco.qq.message.MessageSender;

import java.util.Set;

public class NapCatGroup extends Group {
    private final String id;
    private final String name;
    private final PlatformPermission botPermission;

    public NapCatGroup(String id, String name) {
        this(id, name, PlatformPermission.UNKNOWN);
    }

    public NapCatGroup(String id, String name, PlatformPermission botPermission) {
        this.id = id;
        this.name = name;
        this.botPermission = botPermission == null ? PlatformPermission.UNKNOWN : botPermission;
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
        return switch (botPermission) {
            case MEMBER -> MemberPermission.MEMBER;
            case ADMINISTRATOR -> MemberPermission.ADMINISTRATOR;
            case OWNER -> MemberPermission.OWNER;
            case UNKNOWN -> null;
        };
    }

    public PlatformPermission botPlatformPermission() {
        return botPermission;
    }

    @Override
    public Member botAsMember() {
        return new NapCatMember(SpCoBot.getInstance().botId, SpCoBot.getInstance().botId, this, botPermission);
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
