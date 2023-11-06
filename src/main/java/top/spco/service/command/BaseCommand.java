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

import top.spco.user.BotUser;
import top.spco.user.UserPermission;

import java.sql.SQLException;

/**
 * Created on 2023/10/28 0028 18:53
 */
public abstract class BaseCommand implements Command {

    @Override
    public CommandType getType() {
        return CommandType.ALL;
    }

    @Override
    public UserPermission needPermission() {
        return UserPermission.NORMAL;
    }

    @Override
    public boolean hasPermission(BotUser user) throws SQLException {
        return user.getPermission().getLevel() >= needPermission().getLevel();
    }

    @Override
    public void init() {

    }

    @Override
    public boolean isVisible() {
        return true;
    }
}