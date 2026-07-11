package top.spco.permission;

import top.spco.user.UserPermission;

public enum BotRole {
    BANNED("banned"),
    NORMAL("normal"),
    ADMIN("admin"),
    OWNER("owner");

    private final String id;

    BotRole(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public static BotRole fromLegacy(UserPermission permission) {
        if (permission == null) {
            return NORMAL;
        }
        return switch (permission) {
            case BANNED -> BANNED;
            case NORMAL -> NORMAL;
            case ADMINISTRATOR -> ADMIN;
            case OWNER -> OWNER;
        };
    }
}
