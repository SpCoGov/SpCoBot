package top.spco.core;

import top.spco.qq.QQAdapter;
import top.spco.telegram.TelegramAdapter;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 机器人支持的平台。
 *
 * @author SpCo
 * @version 5.0.0
 * @since 4.1.0
 */
public enum Platform {
    TELEGRAM(TelegramAdapter::getInstance, "telegram", "tg"),
    QQ(QQAdapter::getInstance, "qq");
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

    public static Optional<Platform> from(String input) {
        return Arrays.stream(Platform.values())
                .filter(platform -> platform.getName().equalsIgnoreCase(input) ||
                        Arrays.stream(platform.getAbbreviations()).anyMatch(abbr -> abbr.equalsIgnoreCase(input)))
                .findFirst();
    }
}
