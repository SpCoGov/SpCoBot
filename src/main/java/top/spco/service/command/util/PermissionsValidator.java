package top.spco.service.command.util;

import top.spco.api.Group;
import top.spco.api.Interactive;
import top.spco.api.MemberPermission;
import top.spco.api.PlatformPermission;
import top.spco.api.User;
import top.spco.api.message.Member;
import top.spco.api.message.MessageChain;
import top.spco.permission.BotPermissionNodes;
import top.spco.permission.PermissionService;
import top.spco.user.BotUser;

import java.sql.SQLException;

public class PermissionsValidator {
    public static boolean verifyBotUserPermission(Interactive from, BotUser user, MessageChain message, String node) {
        try {
            if (PermissionService.getInstance().has(user, node)) {
                return true;
            }
        } catch (SQLException e) {
            from.handleException(message, "获取用户权限失败", e);
            return false;
        }
        from.quoteReply(message, "您无权使用此命令。");
        return false;
    }

    public static boolean verifyBotPlatformPermission(Interactive from, MessageChain message) {
        PlatformPermission botPermission = getBotPlatformPermission(from);
        if (!botPermission.isKnown()) {
            from.quoteReply(message, "无法确认机器人平台权限。");
            return false;
        }
        if (!botPermission.isOperator()) {
            from.quoteReply(message, "机器人平台权限不足。");
            return false;
        }
        return true;
    }

    public static boolean verifyTargetOperable(Interactive from, MessageChain message, User target) {
        PlatformPermission botPermission = getBotPlatformPermission(from);
        PlatformPermission targetPermission = target.getPlatformPermission();
        if (!targetPermission.isKnown()) {
            from.quoteReply(message, "无法确认目标平台权限。");
            return false;
        }
        if (!botPermission.canOperate(targetPermission)) {
            from.quoteReply(message, "目标平台权限过高，机器人无法操作。");
            return false;
        }
        return true;
    }

    public static boolean isMemberAdmin(Interactive from, BotUser user, MessageChain message) {
        if (!(from instanceof Group)) {
            from.quoteReply(message, "该命令只能在群聊中使用。");
            return false;
        }
        return verifyBotUserPermission(from, user, message, BotPermissionNodes.GROUP_RECALL);
    }

    public static User verifyMemberPermissions(Interactive from, BotUser user, MessageChain message, long targetId) {
        return verifyMemberPermissions(from, user, message, targetId, BotPermissionNodes.GROUP_MUTE);
    }

    public static User verifyMemberPermissions(Interactive from, BotUser user, MessageChain message, long targetId, String node) {
        if (!(from instanceof Group group)) {
            from.quoteReply(message, "该命令只能在群聊中使用。");
            return null;
        }
        if (!verifyBotUserPermission(from, user, message, node)) {
            return null;
        }
        if (!verifyBotPlatformPermission(from, message)) {
            return null;
        }
        Member target = group.getMember(String.valueOf(targetId));
        if (target == null) {
            from.quoteReply(message, "该用户不存在，或平台适配器无法获取目标用户信息。");
            return null;
        }
        if (!verifyTargetOperable(from, message, target)) {
            return null;
        }
        return target;
    }

    public static boolean verifyBotPermissions(Interactive from, MessageChain message, long targetId) {
        if (!(from instanceof Group group)) {
            return false;
        }
        User target = group.getMember(String.valueOf(targetId));
        return target != null && verifyBotPermissions(from, message, target);
    }

    public static boolean verifyBotPermissions(Interactive from, MessageChain message, User target) {
        return verifyBotPermissions(from, message, target, true);
    }

    public static boolean verifyBotPermissions(Interactive from, MessageChain message, User target, boolean prompt) {
        PlatformPermission botPermission = getBotPlatformPermission(from);
        PlatformPermission targetPermission = target.getPlatformPermission();
        if (!botPermission.isKnown()) {
            if (prompt) {
                from.quoteReply(message, "无法确认机器人平台权限。");
            }
            return false;
        }
        if (!targetPermission.isKnown()) {
            if (prompt) {
                from.quoteReply(message, "无法确认目标平台权限。");
            }
            return false;
        }
        if (!botPermission.canOperate(targetPermission)) {
            if (prompt) {
                from.quoteReply(message, "目标平台权限过高，机器人无法操作。");
            }
            return false;
        }
        return true;
    }

    private static PlatformPermission getBotPlatformPermission(Interactive from) {
        if (!(from instanceof Group group)) {
            return PlatformPermission.UNKNOWN;
        }
        Member botMember = group.botAsMember();
        if (botMember != null) {
            return botMember.getPlatformPermission();
        }
        MemberPermission permission = group.botPermission();
        return PlatformPermission.fromMemberPermission(permission);
    }
}
