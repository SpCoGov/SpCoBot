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

import top.spco.api.*;
import top.spco.api.message.Message;
import top.spco.service.command.*;
import top.spco.service.command.util.PermissionsValidator;
import top.spco.user.BotUser;

import java.security.SecureRandom;

/**
 * @author SpCo
 * @version 2.0.0
 * @since 0.1.0
 */
public class BanmeCommand extends GroupAbstractCommand {
    @Override
    public String[] getLabels() {
        return new String[]{"banme"};
    }

    @Override
    public String getDescriptions() {
        return "禁言我";
    }

    @Override
    public void onCommand(Bot<?> bot, Interactive<?> from, User<?> sender, BotUser user, Message<?> message, int time, CommandMeta meta, String usageName) {
        if (sender instanceof NormalMember<?> member) {
            if (PermissionsValidator.verifyBotPermissions(from, message, member)) {
                int d = new SecureRandom().nextInt(1, 61);
                member.mute(d);
                from.quoteReply(message, "恭喜，您已被禁言" + d + "秒");
            }
        }
    }
}