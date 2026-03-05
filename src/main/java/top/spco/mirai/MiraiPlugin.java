package top.spco.mirai;

import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import top.spco.SpCoBot;
import top.spco.util.LoggedTimer;

import java.io.File;

@SuppressWarnings("ClassInitializerMayBeStatic")
public final class MiraiPlugin extends JavaPlugin {
    static final SpCoBot bot = SpCoBot.getInstance();
    @Deprecated
    public static final MiraiPlugin INSTANCE = new MiraiPlugin();
    private static LoggedTimer totalTime;

    {
        totalTime = new LoggedTimer();
        totalTime.start("初始化SpCoBot");
    }

    private MiraiPlugin() {
        super(new JvmPluginDescriptionBuilder("top.spco.spcobot", SpCoBot.MAIN_VERSION).name("SpCoBot").author("SpCo").build());
        SpCoBot.dataFolder = getDataFolder();
        SpCoBot.configFolder = getConfigFolder();
        SpCoBot.cacheFolder = new File(SpCoBot.dataFolder, "cache");
        SpCoBot.jarFile = getJvmPluginClasspath().getPluginFile();
        bot.initOthers();
        bot.setMessageService(new MiraiMessageServiceImpl());
        totalTime.stop();
    }

    @Override
    public void onEnable() {
    }
}