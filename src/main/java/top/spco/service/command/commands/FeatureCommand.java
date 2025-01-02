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

import top.spco.api.Bot;
import top.spco.api.Interactive;
import top.spco.api.User;
import top.spco.api.message.Message;
import top.spco.core.feature.Feature;
import top.spco.service.command.AbstractCommand;
import top.spco.service.command.CommandMarker;
import top.spco.service.command.CommandMeta;
import top.spco.service.command.exceptions.CommandSyntaxException;
import top.spco.service.command.usage.Usage;
import top.spco.service.command.usage.UsageBuilder;
import top.spco.service.command.usage.parameters.BooleanParameter;
import top.spco.service.command.usage.parameters.FeatureParameter;
import top.spco.service.command.util.SpecifiedParameterHelper;
import top.spco.service.command.util.SpecifiedParameterSet;
import top.spco.user.BotUser;
import top.spco.user.UserPermission;

import java.sql.SQLException;
import java.util.List;

/**
 * @author SpCo
 * @version 4.0.0
 * @since 4.0.0
 */
@CommandMarker
public class FeatureCommand extends AbstractCommand {
    @Override
    public String[] getLabels() {
        return new String[]{"feature"};
    }

    @Override
    public String getDescriptions() {
        return "管理机器人的功能";
    }

    @Override
    public UserPermission needPermission() {
        return UserPermission.ADMINISTRATOR;
    }

    @Override
    public List<Usage> getUsages() {
        SpecifiedParameterSet set = new SpecifiedParameterHelper("操作类型", false).add("disable", "enable").build();
        return List.of(
                new UsageBuilder(getLabels()[0], "列出所有功能")
                        .build(),
                new UsageBuilder(getLabels()[0], "关闭指定功能")
                        .add(set.get("disable"))
                        .add(new FeatureParameter("功能", false, null))
                        .add(new BooleanParameter("仅当前场景", true, false))
                        .build(),
                new UsageBuilder(getLabels()[0], "开启指定功能")
                        .add(set.get("enable"))
                        .add(new FeatureParameter("功能", false, null))
                        .add(new BooleanParameter("仅当前场景", true, false))
                        .build()
        );
    }

    @Override
    public void onCommand(Bot<?> bot, Interactive<?> from, User<?> sender, BotUser user, Message<?> message, int time, CommandMeta meta, String usageName) throws CommandSyntaxException {
        switch (usageName) {
            case "列出所有功能" -> {
                StringBuilder sb = new StringBuilder("当前有以下功能：\n");
                for (String id : FeatureCommand.getAllFeatureIds()) {
                    sb.append(id).append("\n");
                }
                sb.setLength(sb.length() - 1);
                from.quoteReply(message, sb.toString());
            }
            case "关闭指定功能" -> {
                Feature feature = (Feature) meta.getParams().get("功能");
                boolean currentOnly = (Boolean) meta.getParams().get("仅当前场景");
                try {
                    if (!currentOnly) {
                        Feature.setDisabled(feature, true);
                    } else {
                        Feature.addUnavailable(feature, from);
                    }
                } catch (SQLException e) {
                    from.handleException(message, "关闭失败", e);
                }
            }
            case "开启指定功能" -> {
                Feature feature = (Feature) meta.getParams().get("功能");
                boolean currentOnly = (Boolean) meta.getParams().get("仅当前场景");
                try {
                    if (!currentOnly) {
                        Feature.setDisabled(feature, false);
                    } else {
                        Feature.removeUnavailable(feature, from);
                    }
                } catch (SQLException e) {
                    from.handleException(message, "开启失败", e);
                }
            }
        }
    }
}
