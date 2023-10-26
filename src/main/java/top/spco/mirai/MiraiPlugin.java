package top.spco.mirai;

import net.mamoe.mirai.console.extension.PluginComponentStorage;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.contact.AnonymousMember;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.Event;
import net.mamoe.mirai.event.EventChannel;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.*;
import net.mamoe.mirai.message.data.MessageChain;
import org.jetbrains.annotations.NotNull;
import top.spco.SpCoBot;
import top.spco.events.BotEvents;
import top.spco.events.MessageEvents;
import top.spco.events.PluginEvents;
import top.spco.mirai.message.MiraiMessage;

public final class MiraiPlugin extends JavaPlugin {
    public static SpCoBot BOT = SpCoBot.getInstance();
    public static final MiraiPlugin INSTANCE = new MiraiPlugin();


    private MiraiPlugin() {
        super(new JvmPluginDescriptionBuilder("top.spco.spcobot", "0.1.0").name("SpCoBot").author("SpCo").build());
        SpCoBot.logger = new MiraiLogger(getLogger());
    }


    @Override
    public void onEnable() {
        PluginEvents.ENABLE_PLUGIN_TICK.invoker().onEnableTick();
        EventChannel<Event> e = GlobalEventChannel.INSTANCE.parentScope(this);
        e.subscribeAlways(GroupMessageEvent.class, g -> {
            Group group = g.getGroup();
            Member sender = g.getSender();
            MessageChain messages = g.getMessage();
            if (sender instanceof NormalMember normalMember) {
                MessageEvents.GROUP_MESSAGE.invoker().onGroupMessage(new MiraiGroup(group), new MiraiNormalMember(normalMember), new MiraiMessage(messages), g.getTime());
            } else if (sender instanceof AnonymousMember anonymousMember) {
                MessageEvents.GROUP_MESSAGE.invoker().onGroupMessage(new MiraiGroup(group), new MiraiAnonymousMember(anonymousMember), new MiraiMessage(messages), g.getTime());
            }
        });
        e.subscribeAlways(GroupTempMessageEvent.class, gt -> {
            Group group = gt.getGroup();
            NormalMember member = gt.getSender();
            MessageChain messages = gt.getMessage();
            MessageEvents.GROUP_TEMP_MESSAGE.invoker().onGroupTempMessage(new MiraiGroup(group), new MiraiNormalMember(member), new MiraiMessage(messages), gt.getTime());
        });
        e.subscribeAlways(NudgeEvent.class, n -> BotEvents.NUDGED_TICK.invoker().onNudgedTick(new MiraiIdentifiable(n.getFrom()), new MiraiIdentifiable(n.getTarget()), new MiraiInteractive(n.getSubject()), n.getAction(), n.getSuffix()));
        e.subscribeAlways(BotOnlineEvent.class, bo -> BotEvents.ONLINE_TICK.invoker().onOnlineTick(new MiraiBot(bo.getBot())));
        e.subscribeAlways(BotOfflineEvent.Active.class, bofa -> BotEvents.ACTIVE_OFFLINE_TICK.invoker().onActiveOfflineTick(new MiraiBot(bofa.getBot())));
        e.subscribeAlways(BotOfflineEvent.Force.class, boff -> BotEvents.FORCE_OFFLINE_TICK.invoker().onForceOfflineTick(new MiraiBot(boff.getBot())));
        e.subscribeAlways(BotOfflineEvent.Dropped.class, bofd -> BotEvents.DROPPED_OFFLINE_TICK.invoker().onDroppedOfflineTick(new MiraiBot(bofd.getBot())));
        e.subscribeAlways(BotOfflineEvent.RequireReconnect.class, bofr -> BotEvents.REQUIRE_RECONNECT_OFFLINE_TICK.invoker().onRequireReconnectOfflineTick(new MiraiBot(bofr.getBot())));
    }

    @Override
    public void onDisable() {
        PluginEvents.DISABLE_PLUGIN_TICK.invoker().onDisableTick();
    }

    @Override
    public void onLoad(@NotNull PluginComponentStorage $this$onLoad) {
        PluginEvents.LOAD_PLUGIN_TICK.invoker().onLoadTick();
    }
}