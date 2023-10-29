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
package top.spco.command;

import top.spco.base.api.Bot;
import top.spco.base.api.Interactive;
import top.spco.base.api.message.Message;
import top.spco.user.BotUser;
import top.spco.user.UserPermission;

/**
 * <p>
 * Created on 2023/10/28 0028 18:27
 * <p>
 *
 * @author SpCo
 * @version 1.0
 * @since 1.0
 */
public interface Command {
    String[] getLabels();

    CommandType getType();

    UserPermission needPermission();

    boolean hasPermission(BotUser user);

    void onCommand(Bot bot, Interactive from, BotUser sender, Message message, int time, String command, String label, String[] args);
}