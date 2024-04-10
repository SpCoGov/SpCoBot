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
import top.spco.service.command.CommandMarker;
import top.spco.service.command.CommandMeta;
import top.spco.service.command.usage.Usage;
import top.spco.service.command.usage.UsageBuilder;
import top.spco.service.command.util.SpecifiedParameterHelper;
import top.spco.service.command.util.SpecifiedParameterSet;
import top.spco.service.command.usage.parameters.StringParameter;
import top.spco.user.BotUser;
import top.spco.user.UserPermission;

import java.sql.SQLException;
import java.util.List;

/**
 * @author SpCo
 * @version 3.0.3
 * @since 0.1.0
 */
@CommandMarker
public class DataCommand extends AbstractCommand {
    @Override
    public String[] getLabels() {
        return new String[]{"data"};
    }

    @Override
    public String getDescriptions() {
        return "操作数据";
    }

    @Override
    public List<Usage> getUsages() {
        SpecifiedParameterSet set = new SpecifiedParameterHelper("操作类型", false).add("set", "get").build();
        return List.of(
                new UsageBuilder("data", "查询记录")
                        .add(set.get("get"))
                        .add(new StringParameter("表名", false, null, StringParameter.StringType.SINGLE_WORD))
                        .add(new StringParameter("字段名", false, null, StringParameter.StringType.SINGLE_WORD))
                        .add(new StringParameter("记录值", false, null, StringParameter.StringType.QUOTABLE_PHRASE))
                        .add(new StringParameter("待查询的字段名", false, null, StringParameter.StringType.SINGLE_WORD)).build()
                ,
                new UsageBuilder("data", "编辑记录")
                        .add(set.get("set"))
                        .add(new StringParameter("表名", false, null, StringParameter.StringType.SINGLE_WORD))
                        .add(new StringParameter("字段名", false, null, StringParameter.StringType.SINGLE_WORD))
                        .add(new StringParameter("记录值", false, null, StringParameter.StringType.QUOTABLE_PHRASE))
                        .add(new StringParameter("待修改的字段名", false, null, StringParameter.StringType.SINGLE_WORD))
                        .add(new StringParameter("新值", false, null, StringParameter.StringType.QUOTABLE_PHRASE)).build());
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
    public void onCommand(Bot<?> bot, Interactive<?> from, User<?> sender, BotUser user, Message<?> message, int time, CommandMeta meta, String usageName) {
        switch (usageName) {
            case "查询记录" -> {
                String table = (String) meta.getParams().get("表名");
                String columns = (String) meta.getParams().get("待查询的字段名");
                String whereClause = (String) meta.getParams().get("字段名");
                String whereValues = (String) meta.getParams().get("记录值");
                try {
                    String value = SpCoBot.getInstance().getDataBase().selectString(table, columns, whereClause, whereValues);
                    from.quoteReply(message, "您查询的数据为: " + value);
                } catch (SQLException e) {
                    from.handleException(message, "数据查询失败", e);
                }
            }
            case "编辑记录" -> {
                String table = (String) meta.getParams().get("表名");
                String columns = (String) meta.getParams().get("待查询的字段名");
                String whereClause = (String) meta.getParams().get("字段名");
                String whereValues = (String) meta.getParams().get("记录值");
                String toChange = (String) meta.getParams().get("新值");
                try {
                    String value = SpCoBot.getInstance().getDataBase().selectString(table, columns, whereClause, whereValues);
                    SpCoBot.getInstance().getDataBase().update("update " + table + " set " + columns + "=? where " + whereClause + "=?", toChange, whereValues);
                    from.quoteReply(message, "已将数据从 " + value + " 修改为 " + toChange);
                } catch (SQLException e) {
                    from.handleException(message, "数据更新失败", e);
                }
            }
        }
    }
}