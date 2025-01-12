package top.spco.core;

import top.spco.core.main.Main;

/**
 * 平台适配器。
 *
 * @author SpCo
 * @version 4.1.0
 * @since 4.1.0
 */
public abstract class PlatformAdapter {
    protected final Platform platform;

    protected PlatformAdapter(Platform platform) {
        if (!Main.getPlatforms().contains(platform)) {
            throw new IllegalArgumentException("Can not create adapter for not enabled platform " + platform.getName());
        }
        this.platform = platform;
    }
}
