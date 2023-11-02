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
package top.spco.command;

import top.spco.base.api.Bot;
import top.spco.base.api.Interactive;
import top.spco.base.api.message.Message;
import top.spco.user.BotUser;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * <p>
 * Created on 2023/11/2 0002 18:17
 * <p>
 *
 * @author SpCo
 * @version 1.0
 * @since 1.0
 */
public class DivineCommand extends BaseCommand {
    @Override
    public String[] getLabels() {
        return new String[]{"divine"};
    }

    @Override
    public void onCommand(Bot bot, Interactive from, BotUser sender, Message message, int time, String command, String label, String[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append("你好，").append(sender.getId()).append("\n");
        if (args.length == 0) {
            double probability = getProbability(sender.getId() + "在" + LocalDate.now(ZoneId.of("Asia/Shanghai")));
            String fortune = getFortune(probability);
            sb.append("汝的今日运势：").append(fortune).append("\n");
            if (fortune.equals("大凶") || fortune.equals("凶")) {
                sb.append("汝今天倒大霉概率是 ").append(probability).append("%");
            } else {
                sb.append("汝今天行大运概率是 ").append(100 - probability).append("%");
            }
        } else {
            String event = command.substring(8);
            sb.append("所求事项：").append(event).append("\n");
            if (isHentai(event)) {
                if (randomBoolean(sender.getId() + "在" + LocalDate.now(ZoneId.of("Asia/Shanghai")) + "做" + event)) {
                    sb.append("结果：").append("好变态哦!").append("\n");
                } else {
                    sb.append("结果：").append("变态!").append("\n");
                }
                if (randomBoolean(sender.getId() + "在" + LocalDate.now(ZoneId.of("Asia/Shanghai")) + "做2" + event)) {
                    sb.append("此事有 ").append("0.00").append("% 的概率不发生");
                } else {
                    sb.append("此事有 ").append("100.00").append("% 的概率发生");
                }
            } else {
                double probability = getProbability(sender.getId() + "在" + LocalDate.now(ZoneId.of("Asia/Shanghai")) + "做" + event);
                String fortune = getFortune(probability);
                sb.append("结果：").append(fortune).append("\n");
                if (fortune.equals("大凶") || fortune.equals("凶")) {
                    sb.append("此事有 ").append(probability).append("% 的概率不发生");
                } else {
                    sb.append("此事有 ").append(100 - probability).append("% 的概率发生");
                }
            }
        }
        from.quoteReply(message, sb.toString());
    }

    /**
     * 获取倒大霉的概率
     */
    private double getProbability(String seed) {
        double probability = new Random(seed.hashCode()).nextInt(101);
        probability += ((double) new Random(seed.hashCode()).nextInt(101) / 100);
        return probability;
    }

    private boolean randomBoolean(String seed) {
        return new Random(seed.hashCode()).nextBoolean();
    }

    private String getFortune(double number) {
        if (number >= 0 && number <= 20) {
            return "大吉";
        } else if (number >= 21 && number <= 40) {
            return "吉";
        } else if (number >= 41 && number <= 60) {
            return "尚可";
        } else if (number >= 61 && number <= 80) {
            return "凶";
        } else if (number >= 81 && number <= 100) {
            return "大凶";
        } else {
            return "？？？";
        }
    }

    private static boolean isHentai(String event) {
        if (event.contains("手冲")) {
            return true;
        }
        Set<String> hentaiEvents = new HashSet<>();
        hentaiEvents.add("说好变态哦");
        hentaiEvents.add("说变态");
        return hentaiEvents.contains(event);
    }
}