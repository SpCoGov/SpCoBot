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

import java.util.List;

/**
 * @author SpCo
 * @version 2.0.0
 * @since 1.2.2
 */
@CommandMarker
public final class UsageCommand extends AbstractCommand {

    @Override
    public String[] getLabels() {
        return new String[]{"usage"};
    }

    @Override
    public String getDescriptions() {
        return "获取命令在当前作用域下的所有用法";
    }

    @Override
    public List<CommandUsage> getUsages() {
        return List.of(new CommandUsage("usage", "获取命令在当前作用域下的所有用法", new CommandParam("命令名", CommandParam.ParamType.REQUIRED, CommandParam.ParamContent.TEXT)));
    }

    @Override
    public void onCommand(Bot<?> bot, Interactive<?> from, User<?> sender, BotUser user, Message<?> message, int time, CommandMeta meta, String usageName) {
        var usages = SpCoBot.getInstance().getCommandDispatcher().getUsages(meta.argument(0), from);
        if (usages == null) {
            from.quoteReply(message, "命令不存在或定义域错误");
            return;
        }
        var ite = usages.iterator();
        StringBuilder sb = new StringBuilder();
        while (ite.hasNext()) {
            sb.append(ite.next()).append("\n");
        }
        from.quoteReply(message,  sb.toString());
    }
}