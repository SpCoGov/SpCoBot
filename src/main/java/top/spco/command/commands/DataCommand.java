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
package top.spco.command.commands;

import top.spco.SpCoBot;
import top.spco.base.api.Bot;
import top.spco.base.api.Interactive;
import top.spco.base.api.message.Message;
import top.spco.command.BaseCommand;
import top.spco.user.BotUser;
import top.spco.user.UserPermission;

import java.sql.SQLException;

/**
 * <p>
 * Created on 2023/10/29 0029 14:41
 * <p>
 *
 * @author SpCo
 * @version 1.0
 * @since 1.0
 */
public final class DataCommand extends BaseCommand {
    @Override
    public String[] getLabels() {
        return new String[]{"data"};
    }

    @Override
    public String getDescriptions() {
        return "操作数据";
    }

    @Override
    public UserPermission needPermission() {
        return UserPermission.OWNER;
    }

    /**
     * <pre>
     *        0    1       2      3           4        5
     * /data set [表名] [字段名] [记录值] [待修改的字段名] [新值]
     * /data get [表名] [字段名] [记录值] [待查询的字段名]
     * </pre>
     */
    @Override
    public void onCommand(Bot bot, Interactive from, BotUser sender, Message message, int time, String command, String label, String[] args) {
        if (args.length < 5) {
            from.quoteReply(message, "[告知] 语法错误");
            return;
        }
        String subcommand = args[0];
        switch (subcommand) {
            case "get" -> {
                if (args.length != 5) {
                    from.quoteReply(message, "[告知] 语法错误");
                    return;
                }
                String table = args[1];
                String columns = args[4];
                String whereClause = args[2];
                String whereValues = args[3];
                try {
                    String value = SpCoBot.getInstance().getDataBase().select(table, columns, whereClause, whereValues);
                    from.quoteReply(message, "[告知] 您查询的数据为: " + value);
                } catch (SQLException e) {
                    from.handleException(message, "数据查询失败", e);
                }
            }
            case "set" -> {
                if (args.length != 6) {
                    from.quoteReply(message, "[告知] 语法错误");
                    return;
                }
                String table = args[1];
                String columns = args[4];
                String whereClause = args[2];
                String whereValues = args[3];
                String toChange = args[5];
                try {
                    String value = SpCoBot.getInstance().getDataBase().select(table, columns, whereClause, whereValues);
                    SpCoBot.getInstance().getDataBase().update("update " + table + " set " + columns + "=? where " + whereClause + "=?", toChange, whereValues);
                    from.quoteReply(message, "[告知] 已将数据从 " + value + " 修改为 " + toChange);
                } catch (SQLException e) {
                    from.handleException(message, "数据更新失败", e);
                }
            }
        }
    }
}