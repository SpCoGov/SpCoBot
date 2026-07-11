package top.spco.qq.napcat;

import top.spco.api.Group;
import top.spco.api.PlatformPermission;
import top.spco.api.message.Member;
import top.spco.api.message.MessageChain;

public class NapCatMember extends Member {
    private final String id;
    private final String name;
    private final NapCatGroup group;
    private final PlatformPermission platformPermission;

    public NapCatMember(String id, String name, NapCatGroup group) {
        this(id, name, group, PlatformPermission.UNKNOWN);
    }

    public NapCatMember(String id, String name, NapCatGroup group, PlatformPermission platformPermission) {
        this.id = id;
        this.name = name;
        this.group = group;
        this.platformPermission = platformPermission == null ? PlatformPermission.UNKNOWN : platformPermission;
    }

    @Override
    public Group getGroup() {
        return group;
    }

    @Override
    public boolean isBot() {
        return false;
    }

    @Override
    public PlatformPermission getPlatformPermission() {
        return platformPermission;
    }

    @Override
    public void sendMessage(MessageChain message) {

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
