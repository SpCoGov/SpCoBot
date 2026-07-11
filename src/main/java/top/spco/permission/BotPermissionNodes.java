package top.spco.permission;

public final class BotPermissionNodes {
    public static final String WILDCARD = "*";
    public static final String PERMISSION_MANAGE = "permission.manage";
    public static final String GROUP_MUTE = "group.mute";
    public static final String GROUP_KICK = "group.kick";
    public static final String GROUP_RECALL = "group.recall";
    public static final String MCS_MANAGE = "mcs.manage";

    private BotPermissionNodes() {
    }

    public static String command(String label) {
        return "command." + label.toLowerCase() + ".use";
    }
}
