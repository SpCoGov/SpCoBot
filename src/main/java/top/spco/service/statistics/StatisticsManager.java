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

import top.spco.api.Group;
import top.spco.api.NormalMember;
import top.spco.events.MessageEvents;
import top.spco.service.RegistrationException;
import top.spco.service.dashscope.DashScope;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于管理{@link Statistics}的单例类
 *
 * @author SpCo
 * @version 1.1
 * @since 1.1
 */
public class StatisticsManager {
    private static StatisticsManager instance;
    private static boolean registered = false;
    private final Map<Long, Statistics> statistics = new HashMap<>();

    private StatisticsManager() {
        if (registered) {
            return;
        }
        registered = true;
        MessageEvents.GROUP_MESSAGE.register((bot, source, sender, message, time) -> {
            Statistics s = getInstance().getStatistics(source);
            if (s != null) {
                s.receive(source, (NormalMember) sender, message);
            }
        });
    }

    public Statistics getStatistics(Group group) {
        if (statistics.containsKey(group.getId())) {
            if (statistics.get(group.getId()) != null) {
                return statistics.get(group.getId());
            }
        }
        return null;
    }

    public static StatisticsManager getInstance() {
        if (instance == null) {
            instance = new StatisticsManager();
        }
        return instance;
    }

    public void register(Group group, Statistics statistics) throws RegistrationException {
        if (this.statistics.containsKey(group.getId())) {
            if (this.statistics.get(group.getId()) == null) {
                throw new RegistrationException("Group " + group.getId() + " already has a Statistics instance");
            }
        }
        this.statistics.put(group.getId(), statistics);
    }

    public void remove(Group group) {
        this.statistics.remove(group.getId());
    }
}