package top.spco.core;

import top.spco.telegram.Telegram;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 机器人支持的平台。
 *
 * @author SpCo
 * @version 4.1.0
 * @since 4.1.0
 */
public enum Platform {
    TELEGRAM(Telegram::getInstance, "tg"),
    QQ(() -> null, "qq");
    private final String name;
    private final String[] abbreviations;
    private final Supplier<? extends PlatformAdapter> adapterSupplier;

    Platform(Supplier<? extends PlatformAdapter> adapterSupplier, String name, String... abbreviations) {
        this.adapterSupplier = adapterSupplier;
        this.name = name;
        this.abbreviations = abbreviations;
    }

    public String getName() {
        return name;
    }

    public String[] getAbbreviations() {
        return abbreviations;
    }

    public Supplier<? extends PlatformAdapter> getAdapterSupplier() {
        return adapterSupplier;
    }

    // 静态方法：通过名称或缩写获取对应的枚举对象
    public static Optional<Platform> from(String input) {
        return Arrays.stream(Platform.values())
                .filter(platform -> platform.name.equalsIgnoreCase(input) ||
                        Arrays.stream(platform.abbreviations).anyMatch(abbr -> abbr.equalsIgnoreCase(input)))
                .findFirst();
    }
}
