package top.spco.mirai;

import top.spco.SpCoBot;
import top.spco.util.LoggedTimer;

@SuppressWarnings("ClassInitializerMayBeStatic")
public final class MiraiPlugin {
    static final SpCoBot bot = SpCoBot.getInstance();
    @Deprecated
    public static final MiraiPlugin INSTANCE = new MiraiPlugin();
    private static LoggedTimer totalTime;

    {
        totalTime = new LoggedTimer();
        totalTime.start("初始化SpCoBot");
    }

    private MiraiPlugin() {
        bot.initOthers();
        bot.setMessageService(new MiraiMessageServiceImpl());
        totalTime.stop();
    }

}