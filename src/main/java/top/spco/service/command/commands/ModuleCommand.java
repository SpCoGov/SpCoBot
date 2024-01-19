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
package top.spco.service.command.commands;

import top.spco.SpCoBot;
import top.spco.api.Bot;
import top.spco.api.Interactive;
import top.spco.api.User;
import top.spco.api.message.Message;
import top.spco.service.command.*;
import top.spco.user.BotUser;
import top.spco.user.UserPermission;

import java.util.List;

/**
 * 模块管理命令
 *
 * @author SpCo
 * @version 2.0.0
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
    public List<CommandUsage> getUsages() {
        return List.of(
                new CommandUsage(getLabels()[0], "获取所有已加载的模块"),
                new CommandUsage(getLabels()[0], "切换模块状态",
                        new CommandParam("操作类型", CommandParam.ParamType.REQUIRED, CommandParam.ParamContent.SELECTION, "toggle"),
                        new CommandParam("模块名", CommandParam.ParamType.REQUIRED, CommandParam.ParamContent.TEXT)));
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
                    sb.append(module.name).append(" --> ").append(module.isActive() ? "已开启" : "已关闭").append("\n");
                }
                from.quoteReply(message, sb.toString());
            }
            case "切换模块状态" -> {
                String moduleName = meta.argument(1);
                var module = SpCoBot.getInstance().moduleManager.get(moduleName);
                if (module == null) {
                    from.quoteReply(message, moduleName +  "未被加载");
                    return;
                }
                module.toggle();
                from.quoteReply(message, moduleName + (module.isActive() ? "已开启" : "已关闭"));
            }
        }
    }
}