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

/**
 * 一条统计项目
 *
 * @author SpCo
 * @version 3.2.1
 * @since 3.2.1
 */
public class ItemStatistics implements StatisticsItem {
    private final String name;
    private final String unit;
    private int value;

    public ItemStatistics(String name, String unit) {
        this.name = name;
        this.unit = unit;
        this.value = 0;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * 统计一次
     */
    public void add() {
        value++;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name + ": " + value + unit;
    }
}