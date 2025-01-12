package top.spco.core.config;

import top.spco.service.command.commands.DashScopeCommand;

/**
 * 本配置包含了 {@link DashScopeCommand} 的相关配置，包含以下配置项：
 * <table border="1">
 *   <tr>
 *     <th>配置项名</th>
 *     <th>描述</th>
 *   </tr>
 *   <tr>
 *     <td>api_key</td>
 *     <td>调用DashScope模型所需的API KEY。</td>
 *   </tr>
 * </table>
 *
 * @author SpCo
 * @version 4.1.0
 * @see DashScopeCommand
 * @since 4.1.0
 */
public class DashScopeConfig extends Config {
    public DashScopeConfig() {
        super("dashscope");
        init();
    }

    private final ConfigSpecHelper.ConfigValue<String> API_KEY = builder
            .comment("API KEY required to call DashScope model.")
            .define("api_key", "");

    public String getApiKey() {
        return API_KEY.get();
    }
}
