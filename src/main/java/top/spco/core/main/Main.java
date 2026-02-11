package top.spco.core.main;

import top.spco.SpCoBot;
import top.spco.core.Platform;
import top.spco.core.PlatformAdapter;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

/**
 * @author SpCo
 * @version 5.0.0
 * @since 4.1.0
 */
public class Main {
    private static final HashMap<Platform, PlatformAdapter> platforms = new HashMap<>();
    static final SpCoBot bot = SpCoBot.getInstance();

    public static void main(String[] args) {
        File directory = new File(System.getProperty("user.dir"));
        SpCoBot.dataFolder = new File(directory, "data");
        SpCoBot.configFolder = new File(directory, "config");
        SpCoBot.cacheFolder = new File(directory, "cache");
        SpCoBot.jarFile = directory;
        bot.initOthers();
        for (String arg : args) {
            if (arg.startsWith("+")) {
                Platform platform = Platform.from(arg.substring(1)).orElseThrow(
                        () -> new IllegalArgumentException("Unknown platform: " + arg.substring(1))
                );
                platforms.put(platform, null);
                PlatformAdapter adapter = platform.getAdapterSupplier().get();
                platforms.put(platform, adapter);
            }
        }
        if (platforms.isEmpty()) {
            throw new IllegalArgumentException("No platforms found.");
        }
    }

    public static Set<Platform> getPlatforms() {
        return platforms.keySet();
    }
}
