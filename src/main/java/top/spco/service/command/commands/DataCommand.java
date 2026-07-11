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

import top.spco.SpCoBot;
import top.spco.api.Bot;
import top.spco.api.Interactive;
import top.spco.api.User;
import top.spco.api.message.MessageChain;
import top.spco.core.database.DataBase;
import top.spco.service.command.AbstractCommand;
import top.spco.service.command.CommandMarker;
import top.spco.service.command.CommandMeta;
import top.spco.service.command.usage.Usage;
import top.spco.service.command.usage.UsageBuilder;
import top.spco.service.command.usage.parameters.StringParameter;
import top.spco.service.command.util.SpecifiedParameterHelper;
import top.spco.service.command.util.SpecifiedParameterSet;
import top.spco.user.BotUser;
import top.spco.user.UserPermission;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@CommandMarker
public class DataCommand extends AbstractCommand {
    @Override
    public String[] getLabels() {
        return new String[]{"data"};
    }

    @Override
    public String getDescriptions() {
        return "Operate database records";
    }

    @Override
    public List<Usage> getUsages() {
        SpecifiedParameterSet set = new SpecifiedParameterHelper("operation", false).add("set", "get").build();
        return List.of(
                new UsageBuilder("data", "get record")
                        .add(set.get("get"))
                        .add(new StringParameter("table", false, null, StringParameter.StringType.SINGLE_WORD))
                        .add(new StringParameter("where_column", false, null, StringParameter.StringType.SINGLE_WORD))
                        .add(new StringParameter("where_value", false, null, StringParameter.StringType.QUOTABLE_PHRASE))
                        .add(new StringParameter("target_column", false, null, StringParameter.StringType.SINGLE_WORD))
                        .build(),
                new UsageBuilder("data", "set record")
                        .add(set.get("set"))
                        .add(new StringParameter("table", false, null, StringParameter.StringType.SINGLE_WORD))
                        .add(new StringParameter("where_column", false, null, StringParameter.StringType.SINGLE_WORD))
                        .add(new StringParameter("where_value", false, null, StringParameter.StringType.QUOTABLE_PHRASE))
                        .add(new StringParameter("target_column", false, null, StringParameter.StringType.SINGLE_WORD))
                        .add(new StringParameter("new_value", false, null, StringParameter.StringType.QUOTABLE_PHRASE))
                        .build());
    }

    @Override
    public UserPermission needPermission() {
        return UserPermission.OWNER;
    }

    @Override
    public void onCommand(Bot bot, Interactive from, User sender, BotUser user, MessageChain message, long time, CommandMeta meta, String usageName) {
        List<Object> params = new ArrayList<>(meta.getParams().values());
        String operation = (String) params.get(0);
        String table = (String) params.get(1);
        String whereColumn = (String) params.get(2);
        String whereValue = (String) params.get(3);
        String targetColumn = (String) params.get(4);
        DataBase dataBase = SpCoBot.getInstance().getDataBase();

        try {
            dataBase.assertTableAndColumnsExist(table, whereColumn, targetColumn);
            if ("get".equals(operation)) {
                String value = dataBase.selectString(table, targetColumn, whereColumn, whereValue);
                from.quoteReply(message, "Value: " + value);
                return;
            }

            String newValue = (String) params.get(5);
            String previousValue = dataBase.selectString(table, targetColumn, whereColumn, whereValue);
            dataBase.update("update " + DataBase.quoteIdentifier(table)
                    + " set " + DataBase.quoteIdentifier(targetColumn)
                    + "=? where " + DataBase.quoteIdentifier(whereColumn) + "=?", newValue, whereValue);
            from.quoteReply(message, "Updated value from " + previousValue + " to " + newValue);
        } catch (SQLException e) {
            from.handleException(message, "Database operation failed", e);
        }
    }
}
