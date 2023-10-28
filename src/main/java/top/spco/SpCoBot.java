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

import top.spco.base.api.Logger;
import top.spco.events.*;
import top.spco.mirai.message.MiraiMessageChainBuilder;

import java.util.Locale;

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
        init();
    }

    private void init() {
        // 插件启用时
        PluginEvents.ENABLE_PLUGIN_TICK.register(this::onEnable);
        // 插件禁用时
        PluginEvents.DISABLE_PLUGIN_TICK.register(this::onDisable);
        // 机器人被拍一拍时的提示
        BotEvents.NUDGED_TICK.register((from, target, subject, action, suffix) -> {
            if (target.getId() == this.BOT_ID) {
                subject.sendMessage("告知: 机器人正常运行中");
            }
        });
        // 自动接受好友请求
        FriendEvents.REQUESTED_AS_FRIEND.register((eventId, message, fromId, fromGroupId, fromGroup, behavior) -> behavior.accept());
        // 自动接受群邀请
        GroupEvents.INVITED_JOIN_GROUP.register((eventId, invitorId, groupId, invitor, behavior) -> behavior.accept());
        MessageEvents.GROUP_MESSAGE.register((bot, source, sender, message, time) -> {
            if (message.toMessageContext().equals("reply")) {
                source.sendMessage(new MiraiMessageChainBuilder(message).append("回复测试").build());
            }
        });
        MessageEvents.FRIEND_MESSAGE.register((bot, sender, message, time) -> {
            String context = message.toMessageContext();
            if (context.startsWith("/")) {
                // 将用户的输入以 空格 为分隔符分割
                String[] parts = context.split(" ");
                // 检查parts数组是否为空
                if (parts.length > 0) {
                    // 创建一个长度为parts数组的长度减一的数组, 用于存储命令的参数
                    String[] args = new String[parts.length - 1];
                    // 将parts数组中从第二个元素开始的所有元素复制到新的数组中
                    System.arraycopy(parts, 1, args, 0, parts.length - 1);
                    String label = parts[0].toLowerCase(Locale.ENGLISH);
                    CommandEvents.COMMAND.invoker().onCommand(bot, sender, sender, context, label, args);
                    CommandEvents.FRIEND_COMMAND.invoker().onFriendCommand(bot, sender, context, label, args);
                }
            }


        });
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