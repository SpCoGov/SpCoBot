/*
 * Copyright 2023 SpCo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.spco.service.command.util;

import top.spco.api.Group;
import top.spco.api.Interactive;
import top.spco.api.NormalMember;
import top.spco.api.message.Message;
import top.spco.user.BotUser;
import top.spco.user.UserPermission;

/**
 * 群组成员命令验证器。<p>
 * 这个类提供了一系列静态方法，用于验证群组内用户和机器人的权限状态。<p>
 * 它主要用于确保执行特定群组命令时，用户和机器人具有适当的权限。<p>
 * 方法包括：
 * <table border="1">
 *   <tr>
 *     <th>方法名称</th>
 *     <th>描述</th>
 *   </tr>
 *   <tr>
 *     <td>{@link #verifyMemberPermissions}</td>
 *     <td>验证发起命令的用户是否具有管理员权限，并检查机器人是否有权操作目标用户。</td>
 *   </tr>
 *   <tr>
 *     <td>{@link #verifyBotPermissions(Interactive, Message, long)} 和 {@link #verifyBotPermissions(Interactive, Message, NormalMember)}</td>
 *     <td>只验证机器人是否有权限操作指定的群成员。</td>
 *   </tr>
 * </table>
 *
 *
 * <p>
 * 这些方法有助于在执行群管理命令之前确保权限和安全性。
 *
 * @author SpCo
 * @version 2.0.0
 * @since 1.2.0
 */
public class PermissionsValidator {
    /**
     * 验证发起命令的用户是否具有管理员权限，并检查机器人是否有权操作目标用户。<p>
     * 此方法首先检查发起命令的用户在群组中是否具有管理员权限。
     * 接着，它会验证机器人是否有足够的权限操作目标用户。
     * 如果任一条件不满足，方法将发送相应的告知消息并返回 {@code null}。
     *
     * @param from     命令来源
     * @param user     命令发送者
     * @param message  命令源消息
     * @param targetId 命令操作的目标Id
     * @return 可操作返回被操作的目标对象，不可操作返回 {@code null}
     */
    public static NormalMember<?> verifyMemberPermissions(Interactive<?> from, BotUser user, Message<?> message, long targetId) {
        try {
            if (from instanceof Group<?> group) {
                if (!group.getMember(user.getId()).getPermission().isOperator()) {
                    if (user.getPermission().getLevel() < UserPermission.ADMINISTRATOR.getLevel()) {
                        from.quoteReply(message, "[告知] 您无权使用此命令.");
                        return null;
                    }
                }
                NormalMember<?> target = group.getMember(targetId);
                if (verifyBotPermissions(from, message, target)) {
                    return target;
                }
                return null;
            }
        } catch (NullPointerException e) {
            from.quoteReply(message, "[告知] 该用户不存在");
            return null;
        }
        return null;
    }

    /**
     * 只验证机器人是否有权限操作指定的群成员。
     *
     * @param from     命令来源
     * @param message  命令源消息
     * @param targetId 命令操作的目标Id
     * @return 可操作返回 {@code true}，不可操作返回 {@code false}
     * @see #verifyBotPermissions(Interactive, Message, NormalMember)
     */
    public static boolean verifyBotPermissions(Interactive<?> from, Message<?> message, long targetId) {
        if (from instanceof Group<?> group) {
            return verifyBotPermissions(from, message, group.getMember(targetId));
        }
        return false;
    }

    /**
     * 只验证机器人是否有权限操作指定的群成员。<p>
     * 此方法检查机器人在群组中是否具有操作指定成员的权限。
     * 它首先验证机器人的权限是否足够高，然后比较目标成员的权限等级。
     * 如果机器人权限不足或目标成员的权限等级较高，方法会发送告知消息并返回 false。
     *
     * @param from    命令来源
     * @param message 命令源消息
     * @param target  命令操作的目标对象
     * @return 可操作返回 {@code true}，不可操作返回 {@code false}
     */
    public static boolean verifyBotPermissions(Interactive<?> from, Message<?> message, NormalMember<?> target) {
        return verifyBotPermissions(from, message, target, true);
    }

    public static boolean verifyBotPermissions(Interactive<?> from, Message<?> message, NormalMember<?> target, boolean prompt) {
        if (from instanceof Group<?> group) {
            if (!group.botPermission().isOperator()) {
                if (prompt) from.quoteReply(message, "[告知] 机器人权限不足");
                return false;
            }
            if (target.getPermission().getLevel() >= group.botPermission().getLevel()) {
                if (prompt) from.quoteReply(message, "[告知] 大佬，惹不起");
                return false;
            }
            return true;
        }
        return false;
    }
}