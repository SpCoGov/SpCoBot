package top.spco.qq.napcat;

import top.spco.SpCoBot;
import top.spco.api.PlatformPermission;
import top.spco.api.User;
import top.spco.api.message.MessageChain;
import top.spco.qq.message.MessageSender;

public class NapCatUser extends User {
    private final String id;
    private final String name;

    public NapCatUser(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean isBot() {
        return false;
    }

    @Override
    public PlatformPermission getPlatformPermission() {
        return PlatformPermission.UNKNOWN;
    }

    @Override
    public void sendMessage(MessageChain message) {
        try {
            SpCoBot.LOGGER.info(MessageSender.sendSyncPrivateMessage(id, message));
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
