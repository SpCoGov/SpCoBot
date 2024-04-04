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

import top.spco.api.Bot;
import top.spco.api.Interactive;
import top.spco.api.NormalMember;
import top.spco.api.User;
import top.spco.api.message.Message;
import top.spco.service.command.CommandMarker;
import top.spco.service.command.CommandMeta;
import top.spco.service.command.GroupAbstractCommand;
import top.spco.service.command.usage.Usage;
import top.spco.service.command.usage.UsageBuilder;
import top.spco.service.command.usage.parameters.TargetUserIdParameter;
import top.spco.service.command.util.PermissionsValidator;
import top.spco.user.BotUser;

import java.util.List;

/**
 * @author SpCo
 * @version 3.0.0
 * @since 0.3.0
 */
@CommandMarker
public class UnmuteCommand extends GroupAbstractCommand {
    @Override
    public String[] getLabels() {
        return new String[]{"unmute"};
    }

    @Override
    public String getDescriptions() {
        return "解除禁言一位群员";
    }

    @Override
    public List<Usage> getUsages() {
        return List.of(new UsageBuilder("unmute", "解除禁言一位群员").add(new TargetUserIdParameter("目标用户", false, null)).build());
    }

    @Override
    public void onCommand(Bot<?> bot, Interactive<?> from, User<?> sender, BotUser user, Message<?> message, int time, CommandMeta meta, String usageName) {
        long id = (Long) meta.getParams().get("目标用户");
        NormalMember<?> target = PermissionsValidator.verifyMemberPermissions(from, user, message, id);
        if (target != null) {
            target.unmute();
            from.quoteReply(message, "已将 " + target.getNameCard() + "(" + target.getId() + ")" + " 解除禁言");
        }
    }
}