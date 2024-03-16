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
package top.spco.modules;

import top.spco.SpCoBot;
import top.spco.api.Friend;
import top.spco.core.module.AbstractModule;
import top.spco.user.BotUsers;
import top.spco.util.DateUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 每日零点自动签到
 *
 * @author SpCo
 * @version 2.0.5
 * @since 2.0.0
 */
public class AutoSign extends AbstractModule {
    public AutoSign() {
        super("AutoSign");
    }

    @Override
    public void onActivate() {

    }

    @Override
    public void onDeactivate() {

    }

    @Override
    public void init() {
        Timer autoSign = new Timer("AutoSign");
        SpCoBot.LOGGER.debug("自动签到任务已创建！首次任务将在{}毫秒后执行，每次执行间隔：{}", DateUtils.calculateMillisecondToMidnight(), 86400000L);

        autoSign.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!isActive()) {
                    return;
                }
                try {
                    // 创建查询语句
                    String sql = "SELECT id FROM user WHERE sign != ? AND premium = 1";
                    try (PreparedStatement pstmt = SpCoBot.getInstance().getDataBase().getConn().prepareStatement(sql)) {
                        SpCoBot.LOGGER.info("现在开始自动签到");
                        pstmt.setString(1, DateUtils.today().toString());
                        SpCoBot.getInstance().getDataBase().setParameters(pstmt);
                        try (ResultSet rs = pstmt.executeQuery()) {
                            while (rs.next()) {
                                long id = rs.getLong("id");
                                BotUsers.get(id).sign();
                                SpCoBot.LOGGER.info("已为用户 {} 自动签到", id);
                            }
                        }
                    }
                } catch (Exception e) {
                    SpCoBot.LOGGER.error(e);
                    Friend<?> friend = SpCoBot.getInstance().getBot().getFriend(SpCoBot.getInstance().botOwnerId);
                    friend.handleException("自动签到时抛出了意料之外的异常", e);
                }
            }
        }, DateUtils.calculateMillisecondToMidnight(), 86400000L);
    }
}