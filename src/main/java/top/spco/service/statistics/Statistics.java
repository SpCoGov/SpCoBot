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
package top.spco.service.statistics;

import top.spco.SpCoBot;
import top.spco.api.Group;
import top.spco.api.NormalMember;
import top.spco.api.message.Message;
import top.spco.util.function.PentaConsumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计
 *
 * @author SpCo
 * @version 1.1.0
 * @since 0.1.1
 */
public class Statistics {
    private Map<Integer, String> mapping = new HashMap<>();
    private Map<Integer, Map<Long, Message>> statistics = new HashMap<>();
    /**
     * 已报名的用户
     */
    private List<Long> users = new ArrayList<>();
    private Group group;
    private PentaConsumer<Boolean, NormalMember, Message, Integer, Group> received;

    public Statistics(Group group, PentaConsumer<Boolean, NormalMember, Message, Integer, Group> received) {
        this.group = group;
        this.received = received;
    }

    /**
     * 添加一项需要统计的事项
     *
     * @param item 事项内容
     * @return 事项的ID
     */
    public int addItem(String item) {
        int i = mapping.size() + 1;
        mapping.put(i, item);
        return i;
    }

    public void receive(Group source, NormalMember sender, Message message) {
        if (group.getId() != source.getId()) {
            return;
        }
        try {
            int itemId = Integer.parseInt(message.toMessageContext());
            if (!mapping.containsKey(itemId)) {
                return;
            }
            received.accept(record(sender, itemId, message), sender, message, itemId, source);
        } catch (Exception ignored) {
        }
    }

    private boolean record(NormalMember sender, int itemId, Message message) {
        Map<Long, Message> map;
        if (users.contains(sender.getId())) {
            return false;
        }
        if (statistics.containsKey(itemId)) {
            map = statistics.get(itemId);
            for (var record : map.entrySet()) {
                if (record.getKey() == sender.getId()) {
                    return false;
                }
            }
        } else {
            map = new HashMap<>();
        }
        map.put(sender.getId(), message);
        users.add(sender.getId());
        statistics.put(itemId, map);
        return true;
    }

    public Integer getIndex(String item) {
        for (var itemMap : mapping.entrySet()) {
            if (itemMap.getValue().equals(item)) {
                return itemMap.getKey();
            }
        }
        return null;
    }

    public Map<Long, Message> getRecords(int itemId) {
        if (statistics.containsKey(itemId)) {
            return statistics.get(itemId);
        }
        return null;
    }

    public void stop() {
        SpCoBot.getInstance().statisticsDispatcher.remove(group);
        this.mapping = null;
        this.statistics = null;
        this.group = null;
        this.received = null;
        this.users = null;
    }
}