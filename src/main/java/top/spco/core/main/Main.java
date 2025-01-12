package top.spco.core.main;

import top.spco.core.Platform;
import top.spco.core.PlatformAdapter;

import java.util.HashMap;
import java.util.Set;

/**
 * @author SpCo
 * @version 4.1.0
 * @since 4.1.0
 */
public class Main {
    private static final HashMap<Platform, PlatformAdapter> platforms = new HashMap<>();

    public static void main(String[] args) {
        for (String arg : args) {
            if (arg.startsWith("+")) {
                Platform platform = Platform.from(arg.substring(1)).orElseThrow(
                        () -> new IllegalArgumentException("Unknown platform: " + arg.substring(1))
                );
                if (platform == Platform.QQ) {
                    throw new IllegalArgumentException("SpCoBot on QQ can only run as a plugin.");
                }
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
