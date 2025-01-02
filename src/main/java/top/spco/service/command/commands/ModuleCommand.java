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
package top.spco.service.command.commands;

import top.spco.SpCoBot;
import top.spco.api.Bot;
import top.spco.api.Interactive;
import top.spco.api.User;
import top.spco.api.message.Message;
import top.spco.core.module.AbstractModule;
import top.spco.service.command.AbstractCommand;
import top.spco.service.command.CommandMarker;
import top.spco.service.command.CommandMeta;
import top.spco.service.command.usage.Usage;
import top.spco.service.command.usage.UsageBuilder;
import top.spco.service.command.usage.parameters.ModuleParameter;
import top.spco.service.command.usage.parameters.SpecifiedParameter;
import top.spco.user.BotUser;
import top.spco.user.UserPermission;

import java.util.List;

/**
 * 模块管理命令
 *
 * @author SpCo
 * @version 4.0.0
 * @since 2.0.0
 */
@CommandMarker
public final class ModuleCommand extends AbstractCommand {
    @Override
    public String[] getLabels() {
        return new String[]{"module"};
    }

    @Override
    public String getDescriptions() {
        return "机器人的模块管理";
    }

    @Override
    public List<Usage> getUsages() {
        return List.of(
                new UsageBuilder(getLabels()[0], "获取所有已加载的模块").build(),
                new UsageBuilder(getLabels()[0], "切换模块状态")
                        .add(new SpecifiedParameter("操作类型", false, "toggle", "toggle"))
                        .add(new ModuleParameter("模块", false, null)).build());
    }

    @Override
    public UserPermission needPermission() {
        return UserPermission.ADMINISTRATOR;
    }

    @Override
    public void onCommand(Bot<?> bot, Interactive<?> from, User<?> sender, BotUser user, Message<?> message, int time, CommandMeta meta, String usageName) {
        switch (usageName) {
            case "获取所有已加载的模块" -> {
                var modules = SpCoBot.getInstance().moduleManager.getAll();
                StringBuilder sb = new StringBuilder("所有已加载的模块：\n");
                for (var module : modules) {
                    sb.append(module.getName()).append(" --> ").append(module.isActive() ? "已开启" : "已关闭").append("\n");
                }
                from.quoteReply(message, sb.toString());
            }
            case "切换模块状态" -> {
                AbstractModule module = (AbstractModule) meta.getParams().get("模块");
                try {
                    module.toggle();
                    from.quoteReply(message, module.getName() + (module.isActive() ? "已开启" : "已关闭"));
                } catch (Exception e) {
                    from.handleException(message, "无法保存模块状态", e);
                }
            }
        }
    }
}