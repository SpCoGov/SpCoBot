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
package top.spco.service.dashscope;

import top.spco.api.Interactive;
import top.spco.api.message.Message;
import top.spco.service.RegistrationException;
import top.spco.user.BotUser;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于管理{@link DashScope}的单例类
 *
 * @author SpCo
 * @version 2.1
 * @since 2.1
 */
public class DashScopeManager {
    private static DashScopeManager instance;
    private static boolean registered = false;
    private final Map<Long, DashScope> dashScopes = new HashMap<>();

    private DashScopeManager() {
        if (registered) {
            return;
        }
        registered = true;
    }

    public static DashScopeManager getInstance() {
        if (instance == null) {
            instance = new DashScopeManager();
        }
        return instance;
    }

    public void register(BotUser user, DashScope dashScope) throws RegistrationException {
        if (this.dashScopes.containsKey(user.getId())) {
            if (this.dashScopes.get(user.getId()) == null) {
                throw new RegistrationException("Bot User " + user.getId() + " already has a DashScope instance");
            }
        }
        this.dashScopes.put(user.getId(), dashScope);
    }

    public void remove(long userId) {
        this.dashScopes.remove(userId);
    }

    public DashScope getDashScope(long userId) {
        if (dashScopes.containsKey(userId)) {
            if (dashScopes.get(userId) != null) {
                return dashScopes.get(userId);
            }
        }
        return null;
    }

    public DashScope getDashScopeOrCreate(BotUser user, Interactive from, Message message) throws RegistrationException {
        var d = getDashScope(user.getId());
        if (d == null) {
            d = new DashScope(user, from, message);
            register(user, d);
        }
        return d;
    }
}