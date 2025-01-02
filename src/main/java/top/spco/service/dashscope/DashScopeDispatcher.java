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
package top.spco.service.dashscope;

import top.spco.api.Interactive;
import top.spco.api.message.Message;
import top.spco.core.Manager;
import top.spco.service.RegistrationException;
import top.spco.user.BotUser;

/**
 * 用于管理 {@link DashScope} 的单例类。
 *
 * @author SpCo
 * @version 4.0.0
 * @since 0.2.1
 */
public class DashScopeDispatcher extends Manager<Long, DashScope> {
    private static DashScopeDispatcher instance;
    private static boolean registered = false;

    private DashScopeDispatcher() {
        if (registered) {
            return;
        }
        registered = true;
    }

    public synchronized static DashScopeDispatcher getInstance() {
        if (instance == null) {
            instance = new DashScopeDispatcher();
        }
        return instance;
    }

    @Override
    public void register(Long userId, DashScope dashScope) throws RegistrationException {
        if (getAllRegistered().containsKey(userId)) {
            if (getAllRegistered().get(userId) == null) {
                throw new RegistrationException("Bot User " + userId + " already has a DashScope instance");
            }
        }
        getAllRegistered().put(userId, dashScope);
    }

    public void remove(long userId) {
        this.getAllRegistered().remove(userId);
    }

    @Override
    public DashScope get(Long userId) {
        if (getAllRegistered().containsKey(userId)) {
            if (getAllRegistered().get(userId) != null) {
                return getAllRegistered().get(userId);
            }
        }
        return null;
    }

    public DashScope getDashScopeOrCreate(BotUser user, Interactive<?> from, Message<?> message) throws RegistrationException {
        var d = get(user.getId());
        if (d == null) {
            d = new DashScope(user, from, message);
            register(user.getId(), d);
        }
        return d;
    }
}