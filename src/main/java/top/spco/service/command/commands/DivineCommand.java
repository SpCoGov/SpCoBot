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

import top.spco.api.Bot;
import top.spco.api.Interactive;
import top.spco.api.User;
import top.spco.api.message.Message;
import top.spco.service.command.BaseCommand;
import top.spco.user.BotUser;
import top.spco.util.DateUtils;
import top.spco.util.HashUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @author SpCo
 * @version 2.0
 * @since 1.0
 */
public class DivineCommand extends BaseCommand {
    @Override
    public String[] getLabels() {
        return new String[]{"divine"};
    }

    @Override
    public String getDescriptions() {
        return "占卜";
    }

    @Override
    public void onCommand(Bot bot, Interactive from, User sender, BotUser user, Message message, int time, String command, String label, String[] args) {
        LocalDate today = DateUtils.today();
        try {
            BigDecimal hundred = new BigDecimal("100.00");
            StringBuilder sb = new StringBuilder();
            sb.append("你好，").append(user.getId()).append("\n");
            if (args.length == 0) {
                BigDecimal probability = getProbability(user.getId() + "在" + today);
                String fortune = getFortune(probability.doubleValue());
                sb.append("汝的今日运势：").append(fortune).append("\n");
                if (fortune.equals("大凶") || fortune.equals("凶")) {
                    sb.append("汝今天倒大霉概率是 ").append(probability).append("%");
                } else {
                    sb.append("汝今天行大运概率是 ").append(hundred.subtract(probability)).append("%");
                }
            } else {
                String event = command.substring(8);
                sb.append("所求事项：").append(event).append("\n");
                if (isHentai(event)) {
                    if (randomBoolean(user.getId() + "在" + today + "做" + event)) {
                        sb.append("结果：").append("好变态哦!").append("\n");
                    } else {
                        sb.append("结果：").append("变态!").append("\n");
                    }
                    if (randomBoolean(user.getId() + "在" + today + "做2" + event)) {
                        sb.append("此事有 ").append("0.00").append("% 的概率不发生");
                    } else {
                        sb.append("此事有 ").append("100.00").append("% 的概率发生");
                    }
                } else {
                    BigDecimal probability = getProbability(user.getId() + "在" + today + "做" + event);
                    String fortune = getFortune(probability.doubleValue());
                    sb.append("结果：").append(fortune).append("\n");
                    if (fortune.equals("大凶") || fortune.equals("凶")) {
                        sb.append("此事有 ").append(probability).append("% 的概率不发生");
                    } else {
                        sb.append("此事有 ").append(hundred.subtract(probability)).append("% 的概率发生");
                    }
                }
            }
            from.quoteReply(message, sb.toString());
        } catch (NoSuchAlgorithmException e) {
            from.quoteReply(message, "[错误发生] 占卜失败，占卜师说：" + e.getMessage());
        }

    }

    /**
     * 获取倒大霉的概率
     */
    private BigDecimal getProbability(String seed) throws NoSuchAlgorithmException {
        // 将种子进行 SHA-256 加密
        MessageDigest sha256Digest = MessageDigest.getInstance("SHA-256");
        byte[] inputBytes = seed.getBytes();
        byte[] hashBytes = sha256Digest.digest(inputBytes);
        // 将加密结果作为种子
        Random random = new Random();
        random.setSeed(Arrays.hashCode(hashBytes));
        BigDecimal bigDecimal = BigDecimal.valueOf(random.nextDouble() * 100.00);
        return bigDecimal.setScale(2, RoundingMode.HALF_UP);
    }

    private boolean randomBoolean(String seed) throws NoSuchAlgorithmException {
        seed = HashUtils.sha256(seed);
        return new Random(seed.hashCode()).nextBoolean();
    }

    private String getFortune(double number) {
        if (number >= 0.00 && number <= 20.00) {
            return "大吉";
        } else if (number > 20.00 && number <= 40.00) {
            return "吉";
        } else if (number > 40.00 && number <= 60.00) {
            return "尚可";
        } else if (number > 60.00 && number <= 80.00) {
            return "凶";
        } else if (number > 80.00 && number <= 100.00) {
            return "大凶";
        } else {
            return "？？？";
        }
    }

    private static boolean isHentai(String event) {
        if (event.contains("手冲") || event.contains("帮我口")) {
            return true;
        }
        Set<String> hentaiEvents = new HashSet<>();
        hentaiEvents.add("说好变态哦");
        hentaiEvents.add("说变态");
        return hentaiEvents.contains(event);
    }
}