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
import top.spco.service.command.BaseCommand;
import top.spco.service.command.CommandMeta;
import top.spco.service.command.CommandSyntaxException;
import top.spco.user.BotUser;
import top.spco.user.UserFetchException;
import top.spco.user.UserPermission;

/**
 * @author SpCo
 * @version 3.1
 * @since 3.0
 */
public class GetotherCommand extends BaseCommand {
    @Override
    public String[] getLabels() {
        return new String[]{"getother"};
    }

    @Override
    public String getDescriptions() {
        return "获取其他人的个人资料";
    }

    @Override
    public UserPermission needPermission() {
        return UserPermission.ADMINISTRATOR;
    }

    @Override
    public void onCommand(Bot bot, Interactive from, User sender, BotUser user1, Message message, int time, String command, String label, String[] args, CommandMeta meta) {
        try {
            meta.max(1);
            if (args.length == 0) {
                var quote = SpCoBot.getInstance().getMessageService().getQuote(message);
                BotUser user = BotUser.getOrCreate(quote.getLeft().getFromId());
                from.quoteReply(message, "QQ: " + user.getId() + "\n海绵山币: " + user.getSmfCoin() + "\n会员信息: " + (user.isPremium() ? "Premium会员" : "普通会员") + "\n权限信息: " + user.toUserPermission());
            } else {
                long id = meta.userIdArgument(0);
                BotUser user = BotUser.getOrCreate(id);
                from.quoteReply(message, "QQ: " + user.getId() + "\n海绵山币: " + user.getSmfCoin() + "\n会员信息: " + (user.isPremium() ? "Premium会员" : "普通会员") + "\n权限信息: " + user.toUserPermission());
            }
        } catch (CommandSyntaxException e) {
            from.handleException(message, e.getMessage());
        } catch (UserFetchException e) {
            from.handleException(message, "获取机器人用户时发生异常", e);
        }
    }
}