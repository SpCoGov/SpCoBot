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
package top.spco.service;

import top.spco.SpCoBot;
import top.spco.api.Friend;
import top.spco.user.BotUsers;
import top.spco.util.DateUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 自动签到
 *
 * @author SpCo
 * @version 1.3.0
 * @since 0.1.2
 */
public class AutoSign {
    private static boolean registered = false;

    @SuppressWarnings("all")
    public AutoSign() {
        if (registered) {
            return;
        }
        registered = true;
        Timer autoSign = new Timer("AutoSign", true);
        autoSign.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    // 创建查询语句
                    String sql = "SELECT id FROM user WHERE sign != ? AND premium = 1";
                    try (PreparedStatement pstmt = SpCoBot.getInstance().getDataBase().getConn().prepareStatement(sql)) {
                        pstmt.setString(1, DateUtils.today().toString());
                        SpCoBot.getInstance().getDataBase().setParameters(pstmt);
                        try (ResultSet rs = pstmt.executeQuery()) {
                            while (rs.next()) {
                                long id = rs.getLong("id");
                                BotUsers.get(id).sign();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Friend friend = SpCoBot.getInstance().getBot().getFriend(SpCoBot.getInstance().botOwnerId);
                    friend.handleException("自动签到时抛出了意料之外的异常", e);
                }
            }
        }, DateUtils.getTodayStart(), 864000000L);
    }
}