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
import top.spco.base.api.Friend;
import top.spco.events.PeriodicSchedulerEvents;
import top.spco.user.BotUser;
import top.spco.util.DateUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 自动签到
 *
 * @author SpCo
 * @version 1.2
 * @since 1.2
 */
public class AutoSign {
    private static boolean registered = false;

    public AutoSign() {
        if (registered) {
            return;
        }
        registered = true;
        PeriodicSchedulerEvents.MINUTE_TICK.register(() -> {
            try {
                if (DateUtils.now().format(DateTimeFormatter.ofPattern("HH:mm")).equals("00:00")) {
                    try {
                        List<BotUser> premiumUsers = SpCoBot.getInstance().getDataBase().queryForList("select * from user where sign!=? and premium=?", BotUser.class, DateUtils.today(), 1);
                        for (BotUser botUser : premiumUsers) {
                            botUser.sign();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Friend friend = SpCoBot.getInstance().getBot().getFriend(SpCoBot.getInstance().BOT_OWNER_ID);
                        friend.handleException("自动签到时抛出了意料之外的异常", e);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}