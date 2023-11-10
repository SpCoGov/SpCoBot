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

import top.spco.base.api.Bot;
import top.spco.base.api.Interactive;
import top.spco.base.api.message.Message;
import top.spco.service.command.BaseCommand;
import top.spco.user.BotUser;
import top.spco.user.UserPermission;

import java.sql.SQLException;

/**
 * <p>
 * Created on 2023/10/29 0029 1:10
 * <p>
 *
 * @author SpCo
 * @version 1.2
 * @since 1.0
 */
public final class GetmeCommand extends BaseCommand {
    @Override
    public String[] getLabels() {
        return new String[]{"getme"};
    }

    @Override
    public String getDescriptions() {
        return "获取个人信息";
    }

    @Override
    public void onCommand(Bot bot, Interactive from, BotUser sender, Message message, int time, String command, String label, String[] args) {
        from.quoteReply(message, "QQ: " + sender.getId() + "\n海绵山币: " + sender.getSmfCoin() + "\n会员信息: " + (sender.isPremium() ? "Premium会员" : "普通会员") + "\n权限信息: " + sender.toUserPermission());
    }
}