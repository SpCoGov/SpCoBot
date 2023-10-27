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
package top.spco;

import top.spco.base.Logger;
import top.spco.events.*;

/**
 * <p>
 * Created on 2023/10/25 0025 18:07
 * <p>
 *
 * @author SpCo
 * @version 1.0
 * @since 1.0
 */
public class SpCoBot {
    private static SpCoBot instance;
    public static Logger logger;
    public final long BOT_ID = 2758532041L;
    public final long BOT_OWNER_ID = 2247381667L;

    private SpCoBot() {
        PluginEvents.ENABLE_PLUGIN_TICK.register(this::onEnable);
        PluginEvents.DISABLE_PLUGIN_TICK.register(this::onDisable);
        MessageEvents.GROUP_MESSAGE.register((source, sender, message, time) -> {
        });
        BotEvents.NUDGED_TICK.register((from, target, subject, action, suffix) -> {
            if (target.getId() == this.BOT_ID) {
                subject.sendMessage("告知: 机器人正常运行中");
            }
        });
        FriendEvents.REQUESTED_AS_FRIEND.register((eventId, message, fromId, fromGroupId, fromGroup, behavior) -> behavior.accept());
        GroupEvents.INVITED_JOIN_GROUP.register((eventId, invitorId, groupId, invitor, behavior) -> behavior.accept());
    }

    private void onEnable() {
        logger.info("SpCoBot已上线");
    }

    private void onDisable() {
        logger.info("SpCoBot已下线");
    }

    public static SpCoBot getInstance() {
        if (instance == null) {
            instance = new SpCoBot();
        }
        return instance;
    }
}