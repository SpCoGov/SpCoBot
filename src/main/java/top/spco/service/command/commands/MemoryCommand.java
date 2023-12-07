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
import top.spco.service.command.AbstractCommand;
import top.spco.service.command.CommandMeta;
import top.spco.user.BotUser;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * @author SpCo
 * @version 1.0.0
 * @since 0.3.4
 */
public class MemoryCommand extends AbstractCommand {
    @Override
    public String[] getLabels() {
        return new String[]{"memory"};
    }

    @Override
    public String getDescriptions() {
        return "查看机器人的内存使用信息";
    }

    @Override
    public void onCommand(Bot bot, Interactive from, User sender, BotUser user, Message message, int time, String command, String label, String[] args, CommandMeta meta, String usageName) {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = memoryBean.getNonHeapMemoryUsage();

        from.quoteReply(message, "堆内存的使用情况\n" + memoryUsage(heapMemoryUsage));
        from.quoteReply(message, "非堆内存的使用情况\n" + memoryUsage(nonHeapMemoryUsage));
        from.quoteReply(message, "机器人总占用内存：" + (heapMemoryUsage.getUsed() / (1024 * 1024)) + (nonHeapMemoryUsage.getUsed() / (1024 * 1024)) + "MB");
    }

    private String memoryUsage(MemoryUsage usage) {
        long initMB = usage.getInit() / (1024 * 1024);
        long usedMB = usage.getUsed() / (1024 * 1024);
        long committedMB = usage.getCommitted() / (1024 * 1024);
        long maxMB = usage.getMax() == Long.MAX_VALUE ? -1 : usage.getMax() / (1024 * 1024);
        return "初始化时的内存大小：" + initMB + "MB\n" +
                "已使用的内存大小：" + usedMB + "MB\n" +
                "已提交（分配）的内存大小：" + committedMB + "MB\n" +
                "可以使用的最大内存大小：" + maxMB + "MB\n";
    }
}