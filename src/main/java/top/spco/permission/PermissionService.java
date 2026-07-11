package top.spco.permission;

import top.spco.SpCoBot;
import top.spco.core.database.DataBase;
import top.spco.user.BotUser;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public final class PermissionService {
    private static final PermissionService INSTANCE = new PermissionService();

    private PermissionService() {
    }

    public static PermissionService getInstance() {
        return INSTANCE;
    }

    public void initialize() throws SQLException {
        createBuiltinRoles();
        createBuiltinRoleNodes();
        migrateLegacyUsers();
    }

    public boolean has(BotUser user, String node) throws SQLException {
        if (user == null || node == null || node.isBlank()) {
            return false;
        }
        String userId = user.getId();
        if (isRuntimeOwner(userId)) {
            return true;
        }
        ensureLegacyRole(user);
        String normalizedNode = normalize(node);
        if (hasUserEffect(userId, normalizedNode, PermissionEffect.DENY)
                || hasUserEffect(userId, BotPermissionNodes.WILDCARD, PermissionEffect.DENY)) {
            return false;
        }
        if (hasRole(userId, BotRole.BANNED.id())) {
            return false;
        }
        return hasUserEffect(userId, normalizedNode, PermissionEffect.ALLOW)
                || hasUserEffect(userId, BotPermissionNodes.WILDCARD, PermissionEffect.ALLOW)
                || hasRoleNode(userId, normalizedNode)
                || hasRoleNode(userId, BotPermissionNodes.WILDCARD);
    }

    public boolean hasAny(BotUser user, String... nodes) throws SQLException {
        for (String node : nodes) {
            if (has(user, node)) {
                return true;
            }
        }
        return false;
    }

    public void grantRole(String userId, String roleId) throws SQLException {
        roleId = normalize(roleId);
        if (!hasRole(userId, roleId)) {
            execute("insert into permission_user_role(user_id, role_id) values (?,?)", userId, roleId);
        }
    }

    public void revokeRole(String userId, String roleId) throws SQLException {
        db().update("delete from permission_user_role where user_id=? and role_id=?", userId, normalize(roleId));
    }

    public void grantPermission(String userId, String node) throws SQLException {
        setUserPermission(userId, node, PermissionEffect.ALLOW);
    }

    public void denyPermission(String userId, String node) throws SQLException {
        setUserPermission(userId, node, PermissionEffect.DENY);
    }

    public void revokePermission(String userId, String node) throws SQLException {
        db().update("delete from permission_user_node where user_id=? and node=?", userId, normalize(node));
    }

    public String commandNode(String label) {
        return BotPermissionNodes.command(label);
    }

    private void createBuiltinRoles() throws SQLException {
        for (BotRole role : BotRole.values()) {
            execute("insert or ignore into permission_role(id,name,builtin) values (?,?,1)", role.id(), role.id());
        }
    }

    private void createBuiltinRoleNodes() throws SQLException {
        addRoleNodes(BotRole.NORMAL, commonCommandNodes());
        addRoleNodes(BotRole.ADMIN, concat(commonCommandNodes(), adminCommandNodes()));
        addRoleNodes(BotRole.OWNER, BotPermissionNodes.WILDCARD);
    }

    private String[] commonCommandNodes() {
        return new String[]{
                BotPermissionNodes.command("about"),
                BotPermissionNodes.command("admin"),
                BotPermissionNodes.command("balancetop"),
                BotPermissionNodes.command("banme"),
                BotPermissionNodes.command("divine"),
                BotPermissionNodes.command("getme"),
                BotPermissionNodes.command("help"),
                BotPermissionNodes.command("info"),
                BotPermissionNodes.command("quote"),
                BotPermissionNodes.command("sign"),
                BotPermissionNodes.command("usage"),
                BotPermissionNodes.command("valorant")
        };
    }

    private String[] adminCommandNodes() {
        return new String[]{
                BotPermissionNodes.command("feature"),
                BotPermissionNodes.command("gc"),
                BotPermissionNodes.command("getother"),
                BotPermissionNodes.command("mcs"),
                BotPermissionNodes.command("memory"),
                BotPermissionNodes.command("module"),
                BotPermissionNodes.command("mute"),
                BotPermissionNodes.command("kick"),
                BotPermissionNodes.command("recall"),
                BotPermissionNodes.command("report"),
                BotPermissionNodes.command("statistics"),
                BotPermissionNodes.GROUP_MUTE,
                BotPermissionNodes.GROUP_KICK,
                BotPermissionNodes.GROUP_RECALL,
                BotPermissionNodes.MCS_MANAGE
        };
    }

    private String[] concat(String[] left, String[] right) {
        String[] result = Arrays.copyOf(left, left.length + right.length);
        System.arraycopy(right, 0, result, left.length, right.length);
        return result;
    }

    private void addRoleNodes(BotRole role, String... nodes) throws SQLException {
        addRoleNodes(role, Arrays.asList(nodes));
    }

    private void addRoleNodes(BotRole role, Collection<String> nodes) throws SQLException {
        for (String node : nodes) {
            node = normalize(node);
            if (!hasBuiltinRoleNode(role.id(), node)) {
                execute("insert into permission_role_node(role_id,node) values (?,?)", role.id(), node);
            }
        }
    }

    private void migrateLegacyUsers() throws SQLException {
        String sql = "select id, permission from user";
        try (PreparedStatement stmt = db().getConn().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int level = rs.getInt("permission");
                grantRole(rs.getString("id"), BotRole.fromLegacy(top.spco.user.UserPermission.byLevel(level)).id());
            }
        }
    }

    private void ensureLegacyRole(BotUser user) throws SQLException {
        grantRole(user.getId(), BotRole.fromLegacy(user.getPermission()).id());
    }

    private void setUserPermission(String userId, String node, PermissionEffect effect) throws SQLException {
        node = normalize(node);
        if (!hasUserNode(userId, node)) {
            execute("insert into permission_user_node(user_id,node,effect) values (?,?,?)",
                    userId, node, effect.name());
        }
        db().update("update permission_user_node set effect=? where user_id=? and node=?",
                effect.name(), userId, node);
    }

    private boolean hasUserEffect(String userId, String node, PermissionEffect effect) throws SQLException {
        String sql = "select 1 from permission_user_node where user_id=? and node=? and effect=? limit 1";
        try (PreparedStatement stmt = db().getConn().prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, normalize(node));
            stmt.setString(3, effect.name());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean hasRoleNode(String userId, String node) throws SQLException {
        String sql = """
                select 1 from permission_user_role ur
                join permission_role_node rn on rn.role_id = ur.role_id
                where ur.user_id=? and rn.node=? limit 1
                """;
        try (PreparedStatement stmt = db().getConn().prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, normalize(node));
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean hasBuiltinRoleNode(String roleId, String node) throws SQLException {
        String sql = "select 1 from permission_role_node where role_id=? and node=? limit 1";
        try (PreparedStatement stmt = db().getConn().prepareStatement(sql)) {
            stmt.setString(1, roleId);
            stmt.setString(2, normalize(node));
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean hasUserNode(String userId, String node) throws SQLException {
        String sql = "select 1 from permission_user_node where user_id=? and node=? limit 1";
        try (PreparedStatement stmt = db().getConn().prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, normalize(node));
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean hasRole(String userId, String roleId) throws SQLException {
        String sql = "select 1 from permission_user_role where user_id=? and role_id=? limit 1";
        try (PreparedStatement stmt = db().getConn().prepareStatement(sql)) {
            stmt.setString(1, userId);
            stmt.setString(2, normalize(roleId));
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean isRuntimeOwner(String userId) {
        SpCoBot bot = SpCoBot.getInstance();
        return Objects.equals(userId, bot.botId) || Objects.equals(userId, bot.botOwnerId);
    }

    private void execute(String sql, Object... params) throws SQLException {
        try (PreparedStatement stmt = db().getConn().prepareStatement(sql)) {
            db().setParameters(stmt, params);
            stmt.executeUpdate();
        }
    }

    private String normalize(String value) {
        return value.toLowerCase();
    }

    private DataBase db() {
        return SpCoBot.getInstance().getDataBase();
    }
}
