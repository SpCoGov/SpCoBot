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

import java.util.List;

/**
 * {@link AbstractCommand} 是所有命令的抽象基类，实现了{@link Command} 接口。
 * 提供了基本的命令信息和权限控制的默认实现。
 *
 * @author SpCo
 * @version 1.0.0
 * @see Command
 * @since 0.1.0
 */
public abstract class AbstractCommand implements Command {
    @Override
    public List<CommandUsage> getUsages() {
        return List.of(new CommandUsage(getLabels()[0], getDescriptions()));
    }

    @Override
    public CommandScope getScope() {
        return CommandScope.ALL;
    }

    @Override
    public UserPermission needPermission() {
        return UserPermission.NORMAL;
    }

    @Override
    public boolean hasPermission(BotUser user) {
        return user.toUserPermission().getLevel() >= needPermission().getLevel();
    }

    @Override
    public void init() {

    }

    @Override
    public boolean isVisible() {
        return true;
    }
}