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

import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.utils.Constants;
import top.spco.SpCoBot;
import top.spco.api.Bot;
import top.spco.api.Interactive;
import top.spco.api.User;
import top.spco.api.message.Message;
import top.spco.core.config.DashScopeSettings;
import top.spco.service.command.AbstractCommand;
import top.spco.service.command.CommandMeta;
import top.spco.service.command.CommandParam;
import top.spco.service.command.CommandUsage;
import top.spco.service.dashscope.DashScope;
import top.spco.user.BotUser;

import java.util.List;

/**
 * @author SpCo
 * @version 1.0.0
 * @since 0.2.1
 */
public class DashscopeCommand extends AbstractCommand {
    @Override
    public String[] getLabels() {
        return new String[]{"dashscope"};
    }

    @Override
    public String getDescriptions() {
        return "调用一次DashScope模型";
    }

    @Override
    public List<CommandUsage> getUsages() {
        return List.of(new CommandUsage(getLabels()[0], getDescriptions(), new CommandParam("内容", CommandParam.ParamType.REQUIRED, CommandParam.ParamContent.TEXT)));
    }

    @Override
    public void init() {
        Constants.apiKey = SpCoBot.getInstance().getSettings().getProperty(DashScopeSettings.API_KET).toString();
    }

    @Override
    public void onCommand(Bot bot, Interactive from, User sender, BotUser user, Message message, int time, String command, String label, String[] args, CommandMeta meta, String usageName) {
        if (usageName.equals(getDescriptions())) {
            if (!user.isPremium()) {
                from.quoteReply(message, "仅Premium会员可使用此命令。");
                return;
            }
            try {
                DashScope dashScope = SpCoBot.getInstance().dashScopeDispatcher.getDashScopeOrCreate(user, from, message);
                String request = args[0];
                String result = dashScope.callWithMessage(request, 10000).getOutput().getChoices().get(0).getMessage().getContent();
                dashScope.setLastMessage(from, message);
                from.quoteReply(message, result);
            } catch (ApiException e) {
                from.handleException(message, e.getStatus().getMessage(), e);
            } catch (Exception e) {
                from.handleException(message, "创建或获取DashScope实例时发生了意料之外的异常", e);
            }
        }
    }
}