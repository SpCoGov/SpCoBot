package top.spco.api;

public enum PlatformPermission {
    UNKNOWN(-1),
    MEMBER(0),
    ADMINISTRATOR(1),
    OWNER(2);

    private final int level;

    PlatformPermission(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public boolean isOperator() {
        return this == ADMINISTRATOR || this == OWNER;
    }

    public boolean canOperate(PlatformPermission target) {
        return isKnown() && target != null && target.isKnown() && isOperator() && level > target.level;
    }

    public boolean isKnown() {
        return this != UNKNOWN;
    }

    public static PlatformPermission fromRole(String role) {
        if (role == null) {
            return UNKNOWN;
        }
        return switch (role.toLowerCase()) {
            case "owner" -> OWNER;
            case "admin", "administrator" -> ADMINISTRATOR;
            case "member" -> MEMBER;
            default -> UNKNOWN;
        };
    }

    public static PlatformPermission fromMemberPermission(MemberPermission permission) {
        if (permission == null) {
            return UNKNOWN;
        }
        return switch (permission) {
            case MEMBER -> MEMBER;
            case ADMINISTRATOR -> ADMINISTRATOR;
            case OWNER -> OWNER;
        };
    }
}
