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
package top.spco.statistics;

import java.util.LinkedHashMap;
import java.util.NoSuchElementException;

/**
 * 机器人统计
 *
 * @author SpCo
 * @version 3.2.1
 * @since 3.2.1
 */
public class Statistic {
    private final LinkedHashMap<String, StatisticsItem> nameItemMap = new LinkedHashMap<>();

    public void add(StatisticsItem item) {
        if (nameItemMap.containsKey(item.getName())) {
            throw new IllegalArgumentException("统计项目的名称重复");
        }
        nameItemMap.put(item.getName(), item);
    }

    /**
     * 获取指定名称的统计组
     *
     * @param name 名称
     * @return 返回对应的统计组，不存在返回 {@code null}
     * @throws IllegalArgumentException 对应的不是统计组
     */
    public GroupStatistics group(String name) {
        if (nameItemMap.containsKey(name)) {
            if (nameItemMap.get(name) instanceof GroupStatistics group) {
                return group;
            } else {
                throw new IllegalArgumentException("该名称的统计项目不是一个统计组");
            }
        }
        return null;
    }

    /**
     * 获取指定名称的统计项目
     *
     * @param name 名称
     * @return 返回对应的统计项目，不存在返回 {@code null}
     * @throws IllegalArgumentException 对应的不是统计项目
     */
    public ItemStatistics item(String name) {
        if (nameItemMap.containsKey(name)) {
            if (nameItemMap.get(name) instanceof ItemStatistics item) {
                return item;
            } else {
                throw new IllegalArgumentException("该名称对应的项目不是一条统计项目");
            }
        }
        return null;
    }

    /**
     * 获取指定名称的统计项目
     *
     * @param name 名称
     * @return 返回对应的统计项目
     * @throws NoSuchElementException   对应的统计项目不存在
     * @throws IllegalArgumentException 对应的不是统计项目
     */
    public ItemStatistics itemOrThrow(String name) {
        ItemStatistics item = item(name);
        if (item == null) {
            throw new NoSuchElementException("对应的统计项目不存在");
        }
        return item;
    }

    /**
     * 获取指定名称的统计组
     *
     * @param name 名称
     * @return 返回对应的统计组
     * @throws IllegalArgumentException 对应的不是统计组
     * @throws NoSuchElementException   对应的统计组不存在
     */
    public GroupStatistics groupOrThrow(String name) {
        GroupStatistics group = group(name);
        if (group == null) {
            throw new NoSuchElementException("对应的统计组不存在");
        }
        return group;
    }

    public String genReport() {
        StringBuilder sb = new StringBuilder();
        for (var item : nameItemMap.entrySet()) {
            sb.append(item.getValue().toString());
            sb.append("\n");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }
}