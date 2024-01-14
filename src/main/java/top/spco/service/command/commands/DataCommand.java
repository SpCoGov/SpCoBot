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
import top.spco.service.command.AbstractCommand;
import top.spco.service.command.CommandMeta;
import top.spco.service.command.CommandParam;
import top.spco.service.command.CommandUsage;
import top.spco.user.BotUser;
import top.spco.user.UserPermission;

import java.sql.SQLException;
import java.util.List;

/**
 * @author SpCo
 * @version 2.0.0
 * @since 0.1.0
 */
public final class DataCommand extends AbstractCommand {
    @Override
    public String[] getLabels() {
        return new String[]{"data"};
    }

    @Override
    public String getDescriptions() {
        return "操作数据";
    }

    @Override
    public List<CommandUsage> getUsages() {
        return List.of(
                new CommandUsage("data", "查询记录",
                        new CommandParam("操作类型", CommandParam.ParamType.REQUIRED, CommandParam.ParamContent.SELECTION, "get"),
                        new CommandParam("表名", CommandParam.ParamType.REQUIRED, CommandParam.ParamContent.TEXT),
                        new CommandParam("字段名", CommandParam.ParamType.REQUIRED, CommandParam.ParamContent.TEXT),
                        new CommandParam("记录值", CommandParam.ParamType.REQUIRED, CommandParam.ParamContent.TEXT),
                        new CommandParam("待查询的字段名", CommandParam.ParamType.REQUIRED, CommandParam.ParamContent.TEXT)),
                new CommandUsage("data", "编辑记录",
                        new CommandParam("操作类型", CommandParam.ParamType.REQUIRED, CommandParam.ParamContent.SELECTION, "set"),
                        new CommandParam("表名", CommandParam.ParamType.REQUIRED, CommandParam.ParamContent.TEXT),
                        new CommandParam("字段名", CommandParam.ParamType.REQUIRED, CommandParam.ParamContent.TEXT),
                        new CommandParam("记录值", CommandParam.ParamType.REQUIRED, CommandParam.ParamContent.TEXT),
                        new CommandParam("待修改的字段名", CommandParam.ParamType.REQUIRED, CommandParam.ParamContent.TEXT),
                        new CommandParam("新值", CommandParam.ParamType.REQUIRED, CommandParam.ParamContent.TEXT)));
    }

    @Override
    public UserPermission needPermission() {
        return UserPermission.OWNER;
    }

    /**
     * <pre>
     *        0    1       2      3           4        5
     * /data set <表名> <字段名> <记录值> <待修改的字段名> <新值>
     * /data get <表名> <字段名> <记录值> <待查询的字段名>
     * </pre>
     */
    @Override
    public void onCommand(Bot<?> bot, Interactive<?> from, User<?> sender, BotUser user, Message<?> message, int time, String command, String label, String[] args, CommandMeta meta, String usageName) {
        switch (usageName) {
            case "查询记录" -> {
                String table = args[1];
                String columns = args[4];
                String whereClause = args[2];
                String whereValues = args[3];
                try {
                    String value = SpCoBot.getInstance().getDataBase().selectString(table, columns, whereClause, whereValues);
                    from.quoteReply(message, "[告知] 您查询的数据为: " + value);
                } catch (SQLException e) {
                    from.handleException(message, "数据查询失败", e);
                }
            }
            case "编辑记录" -> {
                String table = args[1];
                String columns = args[4];
                String whereClause = args[2];
                String whereValues = args[3];
                String toChange = args[5];
                try {
                    String value = SpCoBot.getInstance().getDataBase().selectString(table, columns, whereClause, whereValues);
                    SpCoBot.getInstance().getDataBase().update("update " + table + " set " + columns + "=? where " + whereClause + "=?", toChange, whereValues);
                    from.quoteReply(message, "[告知] 已将数据从 " + value + " 修改为 " + toChange);
                } catch (SQLException e) {
                    from.handleException(message, "数据更新失败", e);
                }
            }
        }
    }
}