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
package top.spco.service.command;

import top.spco.service.command.exceptions.CommandSyntaxException;
import top.spco.service.command.usage.Usage;

import java.util.*;

/**
 * 用于收集并给出命令检测阶段最合理的报错
 *
 * @author SpCo
 * @version 3.0.0
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
        Comparator<Map.Entry<Usage, Integer>> comparator = Map.Entry.comparingByValue();
        List<Map.Entry<Usage, Integer>> entryList = new ArrayList<>(detectionDepth.entrySet());
        entryList.sort(comparator.reversed());
        return entryList.isEmpty() ? lastException : potentials.get(entryList.get(0).getKey());
    }
}