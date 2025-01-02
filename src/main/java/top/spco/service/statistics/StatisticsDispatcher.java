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
package top.spco.service.statistics;

import top.spco.api.Group;
import top.spco.api.NormalMember;
import top.spco.core.Manager;
import top.spco.events.MessageEvents;
import top.spco.service.RegistrationException;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于管理 {@link Statistics} 的单例类。
 *
 * @author SpCo
 * @version 4.0.0
 * @since 0.1.1
 */
public class StatisticsDispatcher extends Manager<Long, Statistics> {
    private static StatisticsDispatcher instance;
    private static boolean registered = false;
    private final Map<Long, Statistics> statistics = new HashMap<>();

    private StatisticsDispatcher() {
        if (registered) {
            return;
        }
        registered = true;
        MessageEvents.GROUP_MESSAGE.register((bot, source, sender, message, time) -> {
            Statistics s = getInstance().get(source.getId());
            if (s != null) {
                s.receive(source, (NormalMember<?>) sender, message);
            }
        });
    }

    @Override
    public Statistics get(Long groupId) {
        if (statistics.containsKey(groupId)) {
            if (statistics.get(groupId) != null) {
                return statistics.get(groupId);
            }
        }
        return null;
    }

    public synchronized static StatisticsDispatcher getInstance() {
        if (instance == null) {
            instance = new StatisticsDispatcher();
        }
        return instance;
    }

    @Override
    public void register(Long groupId, Statistics statistics) throws RegistrationException {
        if (this.statistics.containsKey(groupId)) {
            if (this.statistics.get(groupId) == null) {
                throw new RegistrationException("Group " + groupId + " already has a Statistics instance");
            }
        }
        this.statistics.put(groupId, statistics);
    }

    public void remove(Group<?> group) {
        this.statistics.remove(group.getId());
    }
}