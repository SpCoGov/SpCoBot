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
import top.spco.api.Behavior;
import top.spco.api.Bot;
import top.spco.events.*;

public final class MiraiPlugin extends JavaPlugin {
    public static SpCoBot BOT = SpCoBot.getInstance();
    @Deprecated
    public static final MiraiPlugin INSTANCE = new MiraiPlugin();

    private MiraiPlugin() {
        super(new JvmPluginDescriptionBuilder("top.spco.spcobot", SpCoBot.MAIN_VERSION).name("SpCoBot").author("SpCo").build());
        SpCoBot.dataFolder = getDataFolder();
        SpCoBot.configFolder = getConfigFolder();
        SpCoBot.logger = new MiraiLogger(getLogger());
        BOT.initOthers();
        BOT.setMessageService(new MiraiMessageServiceImpl());
    }

    @Override
    public void onEnable() {
        PluginEvents.ENABLE_PLUGIN_TICK.invoker().onEnableTick();
        EventChannel<Event> e = GlobalEventChannel.INSTANCE.parentScope(this);
        e.subscribeAlways(GroupMessageEvent.class, g -> {
            MiraiBot bot = new MiraiBot(g.getBot());
            Group group = g.getGroup();
            Member sender = g.getSender();
            MessageChain messages = g.getMessage();
            if (sender instanceof NormalMember normalMember) {
                MessageEvents.GROUP_MESSAGE.invoker().onGroupMessage(bot, new MiraiGroup(group), new MiraiNormalMember(normalMember), new MiraiMessage(messages), g.getTime());
            } else if (sender instanceof AnonymousMember anonymousMember) {
                MessageEvents.GROUP_MESSAGE.invoker().onGroupMessage(bot, new MiraiGroup(group), new MiraiAnonymousMember(anonymousMember), new MiraiMessage(messages), g.getTime());
            }
        });
        e.subscribeAlways(GroupTempMessageEvent.class, gt -> {
            NormalMember member = gt.getSender();
            MessageChain messages = gt.getMessage();
            MessageEvents.GROUP_TEMP_MESSAGE.invoker().onGroupTempMessage(new MiraiBot(gt.getBot()), new MiraiNormalMember(member), new MiraiNormalMember(member), new MiraiMessage(messages), gt.getTime());
        });
        e.subscribeAlways(NudgeEvent.class, n -> BotEvents.NUDGED_TICK.invoker().onNudgedTick(new MiraiIdentifiable(n.getFrom()), new MiraiIdentifiable(n.getTarget()), new MiraiInteractive(n.getSubject()), n.getAction(), n.getSuffix()));
        e.subscribeAlways(BotOnlineEvent.class, bo -> {
            Bot bot = new MiraiBot(bo.getBot());
            SpCoBot.getInstance().setBot(bot);
            BotEvents.ONLINE_TICK.invoker().onOnlineTick(bot);
        });
        e.subscribeAlways(BotOfflineEvent.Active.class, bofa -> {
            SpCoBot.getInstance().setBot(null);
            BotEvents.ACTIVE_OFFLINE_TICK.invoker().onActiveOfflineTick(new MiraiBot(bofa.getBot()));
        });
        e.subscribeAlways(BotOfflineEvent.Force.class, boff -> {
            SpCoBot.getInstance().setBot(null);
            BotEvents.FORCE_OFFLINE_TICK.invoker().onForceOfflineTick(new MiraiBot(boff.getBot()));
        });
        e.subscribeAlways(BotOfflineEvent.Dropped.class, bofd -> {
            SpCoBot.getInstance().setBot(null);
            BotEvents.DROPPED_OFFLINE_TICK.invoker().onDroppedOfflineTick(new MiraiBot(bofd.getBot()));
        });
        e.subscribeAlways(BotOfflineEvent.RequireReconnect.class, bofr -> {
            SpCoBot.getInstance().setBot(null);
            BotEvents.REQUIRE_RECONNECT_OFFLINE_TICK.invoker().onRequireReconnectOfflineTick(new MiraiBot(bofr.getBot()));
        });

        e.subscribeAlways(BotInvitedJoinGroupRequestEvent.class, bi -> GroupEvents.INVITED_JOIN_GROUP.invoker().invitedJoinGroup(bi.getEventId(), bi.getInvitorId(), bi.getGroupId(), new MiraiFriend(bi.getInvitor()), new Behavior() {
            @Override
            public void accept() {
                bi.accept();
            }

            @Override
            public void ignore() {
                bi.ignore();
            }

            /**
             * @see #ignore()
             * @deprecated 无法拒绝入群请求
             */
            @Deprecated
            @Override
            public void reject(boolean block, String message) {

            }
        }));
        e.subscribeAlways(NewFriendRequestEvent.class, nf -> FriendEvents.REQUESTED_AS_FRIEND.invoker().requestedAsFriend(nf.getEventId(), nf.getMessage(), nf.getFromId(), nf.getFromGroupId(), new MiraiGroup(nf.getFromGroup()), new Behavior() {
            @Override
            public void accept() {
                nf.accept();
            }

            /**
             * @see #reject(boolean, String)
             * @deprecated 无法忽略好友请求
             */
            @Deprecated
            @Override
            public void ignore() {

            }

            @Override
            public void reject(boolean block, String message) {
                nf.reject(block);
            }
        }));
        e.subscribeAlways(MemberJoinRequestEvent.class, mj -> GroupEvents.REQUEST_JOIN_GROUP.invoker().requestJoinGroup(mj.getEventId(), mj.getFromId(), new MiraiGroup(mj.getGroup()), new Behavior() {
            @Override
            public void accept() {
                mj.accept();
            }

            @Override
            public void ignore() {

            }

            @Override
            public void reject(boolean block, String message) {
                mj.reject(block, message);
            }
        }));
        e.subscribeAlways(FriendMessageEvent.class, fm -> MessageEvents.FRIEND_MESSAGE.invoker().onFriendMessage(new MiraiBot(fm.getBot()), new MiraiFriend(fm.getSender()), new MiraiMessage(fm.getMessage()), fm.getTime()));
        e.subscribeAlways(GroupMessagePostSendEvent.class, gmp -> MessageEvents.GROUP_MESSAGE_POST_SEND.invoker().onGroupMessagePostSend(new MiraiBot(gmp.getBot()), new MiraiGroup(gmp.getTarget()), new MiraiMessage(gmp.getMessage())));
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