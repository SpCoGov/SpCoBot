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
import top.spco.user.BotUser;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Created on 2023/11/5 0005 12:25
 * <p>
 *
 * @author SpCo
 * @version 2.0
 * @since 1.0
 */
public class BalancetopCommand extends BaseCommand {
    @Override
    public String[] getLabels() {
        return new String[]{"balancetop"};
    }

    @Override
    public String getDescriptions() {
        return "查看海绵山币排行榜";
    }

    @Override
    public void onCommand(Bot bot, Interactive from, User sender, BotUser user, Message message, int time, String command, String label, String[] args) {
        try {
            List<Map.Entry<Long, Integer>> topRecords = new ArrayList<>(getRecords().entrySet());
            // 使用比较器进行值的降序排序
            topRecords.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
            // 取前10个记录
            if (topRecords.size() > 10) {
                topRecords = topRecords.subList(0, 10);
            }
            // 处理前十个最大值的记录
            StringBuilder sb = new StringBuilder("海绵山富豪榜\n");
            int no = 1;
            for (Map.Entry<Long, Integer> entry : topRecords) {
                long id = entry.getKey();
                int coin = entry.getValue();
                sb.append(no).append(". ").append(id).append(" - ").append(coin).append("\n");
                no += 1;
            }
            from.quoteReply(message, sb.toString());
        } catch (SQLException e) {
            from.handleException("查询记录失败", e);
        }
    }

    private static Map<Long, Integer> getRecords() throws SQLException {
        Map<Long, Integer> recordsMap = new HashMap<>();
        // 创建查询语句
        String sql = "SELECT id, smf_coin FROM user";
        Connection conn = SpCoBot.getInstance().getDataBase().getConn();
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            long id = resultSet.getLong("id");
            int coin = resultSet.getInt("smf_coin");
            recordsMap.put(id, coin);
        }
        statement.close();
        resultSet.close();
        return recordsMap;
    }
}