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
package top.spco.service.command;

import top.spco.api.Interactive;
import top.spco.core.feature.Feature;
import top.spco.core.feature.FeatureManager;
import top.spco.service.command.usage.Usage;
import top.spco.service.command.usage.UsageBuilder;
import top.spco.user.BotUser;
import top.spco.user.UserPermission;

import java.sql.SQLException;
import java.util.List;
import java.util.function.Supplier;

/**
 * {@link AbstractCommand} 是所有命令的抽象基类，实现了{@link Command} 接口。
 * 提供了基本的命令信息和权限控制的默认实现。
 *
 * @author SpCo
 * @version 4.0.0
 * @see Command
 * @since 0.1.0
 */
public abstract class AbstractCommand extends Command {
    @Override
    public List<Usage> getUsages() {
        return List.of(new UsageBuilder(getLabels()[0], getDescriptions()).build());
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
        return user.getPermission().getLevel() >= needPermission().getLevel();
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public Supplier<FeatureManager<?, ? extends Feature>> manager() {
        return CommandDispatcher::getInstance;
    }

    @Override
    public String getFeatureName() {
        return getLabels()[0].toLowerCase();
    }

    @Override
    public boolean isAvailable(Interactive<?> where) throws SQLException {
        return ((CommandDispatcher) manager().get()).isFeatureAvailable(where, this, null);
    }
}