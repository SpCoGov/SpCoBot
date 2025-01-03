/*
 * Copyright 2025 SpCo
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
package top.spco.service.command;

import top.spco.service.command.exceptions.CommandSyntaxException;
import top.spco.service.command.usage.Usage;

import java.util.*;

/**
 * 用于收集并给出命令检测阶段最合理的报错
 *
 * @author SpCo
 * @version 3.2.2
 * @since 3.0.0
 */
public class Potential {
    private final HashMap<Usage, Integer> detectionDepth = new HashMap<>();
    private final HashMap<Usage, CommandSyntaxException> potentials = new HashMap<>();
    private CommandSyntaxException lastException;

    public void setLast(CommandSyntaxException e) {
        lastException = e;
    }

    public void add(Usage usage, CommandSyntaxException e) {
        potentials.put(usage, e);
        lastException = e;
    }

    public void remove(Usage usage) {
        if (detectionDepth.containsKey(usage)) {
            detectionDepth.put(usage, Integer.MIN_VALUE);
        }
    }

    public void further(Usage usage) {
        detectionDepth.put(usage, detectionDepth.getOrDefault(usage, 0) + 1);
    }

    public CommandSyntaxException get() {
        // 定义一个比较器，用于根据用法的深度进行排序
        Comparator<Map.Entry<Usage, Integer>> comparator = Map.Entry.comparingByValue();
        // 创建一个包含 detectionDepth 中所有条目的列表
        List<Map.Entry<Usage, Integer>> entryList = new ArrayList<>(detectionDepth.entrySet());
        // 使用比较器对列表进行排序，并倒序排列（从大到小）
        entryList.sort(comparator.reversed());
        // 如果 entryList 为空，返回 lastException；否则，返回与最大值对应的用法关联的 CommandSyntaxException
        CommandSyntaxException exception = entryList.isEmpty() ? lastException : potentials.get(entryList.get(0).getKey());
        // 如果关联的对象也为空，则返回 lastException
        return exception == null ? lastException : exception;
    }
}