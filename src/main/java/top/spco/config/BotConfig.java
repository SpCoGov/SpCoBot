package top.spco.config;

import top.spco.core.config.Config;
import top.spco.core.config.ConfigSpecHelper;
import top.spco.user.BotUser;
import top.spco.user.UserPermission;

/**
 * 本配置包含了机器人的基本设置，包含以下配置项：
 * <table border="1">
 *   <tr>
 *     <th>配置项名</th>
 *     <th>描述</th>
 *   </tr>
 *   <tr>
 *     <td>bot_id</td>
 *     <td>机器人所使用的账号ID。<p>
 *         当机器人创建以此配置项的值为ID的 {@link BotUser} 时，会自动将该用户的权限设置为 {@link UserPermission#OWNER 机器人主人}。</td>
 *   </tr>
 *   <tr>
 *     <td>owner_id</td>
 *     <td>机器人主人所使用的账号ID。<p>
 *         当机器人创建以此配置项的值为ID的 {@link BotUser} 时，会自动将该用户的权限设置为 {@link UserPermission#OWNER 机器人主人}。<p>
 *         和其他仅拥有 {@link UserPermission#OWNER 机器人主人} 权限的用户不同的是，一些通知信息或报错信息只会发给以此处设置的账号。</td>
 *   </tr>
 *   <tr>
 *     <td>test_group</td>
 *     <td>用于机器人功能的群ID。<p>
 *         一些通知信息或报错信息会发送至此处设置的群。</td>
 *   </tr>
 * </table>
 *
 * @author SpCo
 * @version 4.1.0
 * @since 4.1.0
 */
public class BotConfig extends Config {
    BotConfig() {
        super("bot");
        init();
    }

    private final ConfigSpecHelper.ConfigValue<Long> BOT_ID = builder
            .comment("The Id used by the bot.")
            .define("bot_id", 0L, o -> o instanceof Number);
    private final ConfigSpecHelper.ConfigValue<Long> OWNER_ID = builder
            .comment("The Id used by the bot owner.")
            .define("owner_id", 0L, o -> o instanceof Number);
    private final ConfigSpecHelper.ConfigValue<Long> TEST_GROUP = builder
            .comment("Group Id for bot functionality.")
            .define("test_group", 0L,o -> o instanceof Number);

    private final ConfigSpecHelper.ConfigValue<Boolean> ENABLE_RECHARGE_SYSTEM = builder
            .define("enable_recharge_system", true);

    private final ConfigSpecHelper.ConfigValue<String> TELEGRAM_BOT_TOKEN = builder
            .define("telegram_bot_token", "");
    private final ConfigSpecHelper.ConfigValue<String> QQ_BOT_TOKEN = builder
            .define("qq_bot_token", "");

    public Long getBotId() {
        return BOT_ID.get();
    }

    public Long getOwnerId() {
        return OWNER_ID.get();
    }

    public Long getTestGroup() {
        return TEST_GROUP.get();
    }

    public boolean isEnableRechargeSystem() {
        return ENABLE_RECHARGE_SYSTEM.get();
    }

    public String getTelegramBotToken() {
        return TELEGRAM_BOT_TOKEN.get();
    }
}
