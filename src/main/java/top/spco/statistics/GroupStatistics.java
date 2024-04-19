/*
 * Copyright 2024 SpCo
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

/**
 * 统计信息的组，可以包含多条信息
 *
 * @author SpCo
 * @version 3.2.1
 * @since 3.2.1
 */
public class GroupStatistics implements StatisticsItem {
    private final String name;
    private final LinkedHashMap<String, ItemStatistics> items = new LinkedHashMap<>();

    /**
     * 创建一个统计组
     *
     * @param name 统计组的名称
     */
    public GroupStatistics(String name) {
        this.name = name;
    }

    /**
     * 获取统计组的名称
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * 开始进行一项统计
     *
     * @param name 统计项目的名称
     */
    public void start(String name, String unit) {
        if (!items.containsKey(name)) {
            items.put(name, new ItemStatistics(name, unit));
        }
    }


    /**
     * 统计一次
     *
     * @param name 统计项目的名称
     * @throws IllegalArgumentException 该项目没有开始统计时抛出此异常
     */
    public void add(String name) {
        if (!items.containsKey(name)) {
            throw new IllegalArgumentException("没有开始统计‘" + name + "’");
        }
        items.get(name).add();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(":");
        for (var item : items.entrySet()) {
            sb.append("\n").append("\t").append(item.getValue().toString());
        }
        return sb.toString();
    }
}