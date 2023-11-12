/*
 * Copyright 2023 SpCo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.spco.service.command.commands;

import top.spco.SpCoBot;
import top.spco.api.*;
import top.spco.api.message.Message;
import top.spco.service.RegistrationException;
import top.spco.service.chat.*;
import top.spco.service.command.BaseCommand;
import top.spco.service.command.CommandType;
import top.spco.service.statistics.Statistics;
import top.spco.user.BotUser;
import top.spco.user.UserPermission;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Created on 2023/11/5 0005 21:11
 * <p>
 *
 * @author SpCo
 * @version 2.0
 * @since 1.1
 */
public class StatisticsCommand extends BaseCommand {
    private final StringBuilder sb = new StringBuilder();
    private final Map<String, Integer> ranks = new HashMap<>();
    private String rankName;
    private Long groupId;
    private boolean needAtAll;
    private Statistics statistics;
    private int duration;

    @Override
    public String[] getLabels() {
        return new String[]{"statistics"};
    }

    @Override
    public boolean hasPermission(BotUser user) {
        return user.toUserPermission() == UserPermission.OWNER || user.getId() == 916154484L;
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public String getDescriptions() {
        return "发起一项报名统计";
    }

    @Override
    public CommandType getType() {
        return CommandType.ONLY_FRIEND;
    }

    @Override
    public void onCommand(Bot bot1, Interactive from1, User sender1, BotUser user1, Message message1, int time1, String command, String label, String[] args) {
        // 在每次调用命令时，重置
        sb.setLength(0);
        ranks.clear();
        rankName = "";
        groupId = 0L;
        needAtAll = false;
        statistics = null;
        duration = 0;
        try {
            sb.append("新晴上镜赛准备开始！").append("\n");
            sb.append("游戏：");
            Chat chat1 = new ChatBuilder(ChatType.FRIEND, from1).
                    addStage(new Stage(() -> "对话过程中，您可以随时通过发送“退出”来结束对话\n请输入游戏名", (chat, bot, source, sender, message, time) -> {
                        if (message.toMessageContext().equals("退出")) {
                            chat.stop();
                            return;
                        }
                        sb.append(message.toMessageContext()).append("\n").append("\n");
                        chat.next();
                    })).
                    addStage(new Stage(() -> "请输入段位（您可以输入“结束”来停止记录段位需求）", (chat, bot, source, sender, message, time) -> {
                        if (message.toMessageContext().equals("退出")) {
                            chat.stop();
                            return;
                        }
                        if (message.toMessageContext().equals("结束")) {
                            if (ranks.size() == 0) {
                                source.quoteReply(message, "至少记录一个段位");
                                chat.replay();
                            } else {
                                chat.toStage(3);
                            }
                            return;
                        }
                        rankName = "";
                        if (ranks.containsKey(message.toMessageContext())) {
                            source.quoteReply(message, "该段位已被记录");
                            chat.replay();
                            return;
                        }
                        rankName = message.toMessageContext();
                        source.quoteReply(message, "已记录段位：" + rankName);
                        chat.next();
                    })).
                    addStage(new Stage(() -> "请输入该段位所需要的人数", (chat, bot, source, sender, message, time) -> {
                        if (message.toMessageContext().equals("退出")) {
                            chat.stop();
                            return;
                        }
                        try {
                            int i = Integer.parseInt(message.toMessageContext());
                            if (i <= 0) {
                                source.quoteReply(message, "需求人数不能小于等于0");
                                chat.replay();
                                return;
                            }
                            ranks.put(rankName, i);
                            source.quoteReply(message, "已记录段位需求：" + rankName + "段位需要" + i + "人");
                            chat.toStage(1);
                            return;
                        } catch (Exception e) {
                            source.quoteReply(message, "请输入有效的整数数字");
                        }
                        chat.replay();
                    })).
                    addStage(new Stage(() -> "请输入报名持续的时间（单位：分钟）", (chat, bot, source, sender, message, time) -> {
                        if (message.toMessageContext().equals("退出")) {
                            chat.stop();
                            return;
                        }
                        try {
                            int i = Integer.parseInt(message.toMessageContext());
                            if (i <= 0) {
                                source.quoteReply(message, "时间必须大于0");
                                chat.replay();
                                return;
                            }
                            duration = i;
                            chat.next();
                        } catch (Exception e) {
                            source.quoteReply(message, "请输入有效的数字");
                            chat.replay();
                        }
                    })).
                    addStage(new Stage(() -> "请选择报名统计要发送到的群聊", (chat, bot, source, sender, message, time) -> {
                        if (message.toMessageContext().equals("退出")) {
                            chat.stop();
                            return;
                        }
                        try {
                            List<Long> availableGroups = List.of(490656871L, 437756874L, 275922971L, 460056296L);
                            long id = Long.parseLong(message.toMessageContext());
                            if (!availableGroups.contains(id)) {
                                source.quoteReply(message, "输入的群聊不支持发送报名统计");
                                chat.replay();
                                return;
                            }
                            if (!bot.hasGroup(id)) {
                                source.quoteReply(message, "机器人不在该群聊中，请邀请机器人加入群后重新发送");
                                chat.replay();
                                return;
                            }
                            groupId = id;
                            chat.next();
                            return;
                        } catch (Exception e) {
                            source.quoteReply(message, "请输入有效的群号");
                        }
                        chat.replay();
                    })).
                    addStage(new Stage(() -> "这次报名统计需要@全体成员吗（回复需要/不需要）", (chat, bot, source, sender, message, time) -> {
                        if (message.toMessageContext().equals("退出")) {
                            chat.stop();
                            return;
                        }
                        if (message.toMessageContext().equals("需要")) {
                            needAtAll = true;
                        } else if (message.toMessageContext().equals("不需要")) {
                            needAtAll = false;
                        } else {
                            chat.replay();
                            return;
                        }
                        source.sendMessage("请坐和放宽，正在创建报名统计");
                        statistics = new Statistics(bot.getGroup(groupId), (aBoolean, normalMember, message2, integer, group) -> {
                            if (aBoolean) {
                                group.quoteReply(message2, "已记录您的报名");
                            }
                        });
                        sb.append("段位需求：").append("\n");
                        for (var rank : ranks.entrySet()) {
                            sb.append("报名段位 ").append(rank.getKey()).append("(").append("需要").append(rank.getValue()).append("人) 发送").append(statistics.addItem(rank.getKey())).append("\n");
                        }
                        sb.append("\n");
                        sb.append("报名时间持续 ").append(duration).append(" 分钟！").append("\n");
                        sb.append("于 ").append(duration).append(" 分钟后@报名成功的选手，请以上选手私聊新晴~").append("\n");
                        sb.append("请勿重复刷屏，我们只会记录一次");
                        chat.next();
                    })).
                    addStage(new Stage(() -> "以下为最终消息预览，请确认是否要发送至群" + groupId + "。（如需确认，请发送“确认”。否则发送“退出”以退出对话）\n\n" +
                            "=================================\n" + sb + "\n=================================\n" +
                            "\n\n" + "注：@全体成员会分条发送（如需要）\n是否需要@全体成员：" + needAtAll,
                            (chat, bot, source, sender, message, time) -> {
                                if (message.toMessageContext().equals("退出")) {
                                    chat.stop();
                                    return;
                                }
                                if (!message.toMessageContext().equals("确认")) {
                                    chat.replay();
                                    return;
                                }
                                try {
                                    chat.stop();
                                    Group group = bot.getGroup(groupId);
                                    if (group == null) {
                                        source.quoteReply(message, "发送失败，无法获取群号为" + groupId + "的群对象");
                                        return;
                                    }
                                    if (needAtAll) {
                                        group.sendMessage(SpCoBot.getInstance().getMessageService().atAll());
                                    }
                                    group.sendMessage(sb.toString());
                                    source.sendMessage("消息已发送至目标群");
                                    Friend friend = SpCoBot.getInstance().getBot().getFriend(SpCoBot.getInstance().BOT_OWNER_ID);
                                    friend.sendMessage("有用户在群" + groupId + "中发起了一场报名统计，如果需要重启机器人，请注意这场报名统计的结束情况。");
                                    SpCoBot.getInstance().statisticsManager.register(group, statistics);
                                    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
                                    Runnable delayedTask = () -> {
                                        try {
                                            StringBuilder sb2 = new StringBuilder("报名结束，以下为本次报名的统计信息：").append("\n");
                                            Map<Map.Entry<String, Integer>, Boolean> condition = new HashMap<>();
                                            for (var rank : ranks.entrySet()) {
                                                var records = statistics.getRecords(statistics.getIndex(rank.getKey()));
                                                int i = 0;
                                                if (records != null) {
                                                    i = records.size();
                                                }
                                                // 已报名人数
                                                boolean finished = i >= rank.getValue();
                                                sb2.append("分段 ").append(rank.getKey()).append(" 须报").append(rank.getValue()).append("人，已报").append(i).append("人").
                                                        append(" （").append(finished ? "已完成" : "未完成").append("）").append("\n");
                                                condition.put(rank, finished);
                                            }
                                            group.sendMessage(sb2.toString());
                                            source.sendMessage(sb2.toString());
                                            // 开始从所有已报名的人中随机选择
                                            for (var rank : condition.entrySet()) {
                                                int itemId = statistics.getIndex(rank.getKey().getKey());
                                                int need = rank.getKey().getValue();
                                                var records = statistics.getRecords(itemId);
                                                if (records == null) {
                                                    continue;
                                                }
                                                // 如果已完成报名需求（已报人数 >= 须报人数）
                                                if (rank.getValue()) {
                                                    records = randomSelectFromMap(records, need);
                                                }
                                                for (var record : records.entrySet()) {
                                                    group.quoteReply(record.getValue(), SpCoBot.getInstance().getMessageService().append(SpCoBot.getInstance().getMessageService().at(record.getKey()), " 恭喜你参与本次上镜赛，请在 5 分钟内联系新晴"));
                                                }
                                            }
                                            statistics.stop();
                                        } catch (Exception e) {
                                            group.handleException("处理报名结果时抛出了意料之外的异常", e);
                                        }
                                    };
                                    // 使用scheduler.schedule来执行延迟任务
                                    scheduler.schedule(delayedTask, duration, TimeUnit.MINUTES);
                                    scheduler.shutdown();
                                } catch (RegistrationException e) {
                                    source.handleException("注册报名统计时出现异常", e);
                                }
                            })).
                    build();
            chat1.start();
        } catch (ChatTypeMismatchException e) {
            from1.handleException("创建会话失败", e);
        }
    }

    /**
     * 从给定的 Map 中随机选择 x 个键值对。
     *
     * @param map 要选择键值对的 Map
     * @param x   要选择的键值对数量
     * @param <K> 键的类型
     * @param <V> 值的类型
     * @return 包含随机选择的键值对的列表
     */
    public static <K, V> Map<K, V> randomSelectFromMap(Map<K, V> map, int x) {
        List<Map.Entry<K, V>> entryList = new ArrayList<>(map.entrySet());
        Collections.shuffle(entryList);

        Map<K, V> result = new LinkedHashMap<>();
        for (int i = 0; i < Math.min(x, entryList.size()); i++) {
            Map.Entry<K, V> entry = entryList.get(i);
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}