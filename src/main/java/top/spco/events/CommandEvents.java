/*
 * Copyright 2024 SpCo
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
package top.spco.events;

import top.spco.api.*;
import top.spco.api.message.Message;
import top.spco.core.event.Event;
import top.spco.core.event.EventFactory;

/**
 * 命令相关事件
 *
 * @author SpCo
 * @version 3.0.0
 * @since 0.3.0
 */
public class CommandEvents {
    /**
     * Called when a command is received.
     */
    public static final Event<Command> COMMAND = EventFactory.createArrayBacked(Command.class, callbacks -> (bot, from, sender, message, time) -> {
        for (Command event : callbacks) {
            event.onCommand(bot, from, sender, message, time);
        }
    });

    @FunctionalInterface
    public interface Command {
        /**
         * 假如用户发送了命令 "/command a b c"
         *
         * @param bot     收到命令的机器人
         * @param from    收到命令的来源
         * @param sender  命令的发送者
         * @param message 原始消息
         * @param time    命令发送的时间
         */
        void onCommand(Bot<?> bot, Interactive<?> from, User<?> sender, Message<?> message, int time);
    }

    /**
     * Called when a friend command is received.
     */
    public static final Event<FriendCommand> FRIEND_COMMAND = EventFactory.createArrayBacked(FriendCommand.class, callbacks -> (bot, interactor, message, time) -> {
        for (FriendCommand event : callbacks) {
            event.onFriendCommand(bot, interactor, message, time);
        }
    });

    @FunctionalInterface
    public interface FriendCommand {
        /**
         * 假如好友发送了命令 "/command a b c"
         *
         * @param bot        收到命令的机器人
         * @param interactor 命令的发送者
         * @param message    原始消息
         * @param time       命令发送的时间
         */
        void onFriendCommand(Bot<?> bot, Friend<?> interactor, Message<?> message, int time);
    }

    /**
     * Called when a group command is received.
     */
    public static final Event<GroupCommand> GROUP_COMMAND = EventFactory.createArrayBacked(GroupCommand.class, callbacks -> (bot, from, sender, message, time) -> {
        for (GroupCommand event : callbacks) {
            event.onGroupCommand(bot, from, sender, message, time);
        }
    });

    @FunctionalInterface
    public interface GroupCommand {
        /**
         * 假如好友发送了命令 "/command a b c"
         *
         * @param bot     收到命令的机器人
         * @param from    收到命令的来源
         * @param sender  命令的发送者
         * @param time    命令发送的时间
         * @param message 原始消息
         */
        void onGroupCommand(Bot<?> bot, Group<?> from, Member<?> sender, Message<?> message, int time);
    }

    /**
     * Called when a group-temp command is received.
     */
    public static final Event<GroupTempCommand> GROUP_TEMP_COMMAND = EventFactory.createArrayBacked(GroupTempCommand.class, callbacks -> (bot, interactor, message, time) -> {
        for (GroupTempCommand event : callbacks) {
            event.onGroupTempCommand(bot, interactor, message, time);
        }
    });

    @FunctionalInterface
    public interface GroupTempCommand {
        /**
         * 假如好友发送了命令 "/command a b c"
         *
         * @param bot        收到命令的机器人
         * @param interactor 命令的发送者
         * @param time       命令发送的时间
         * @param message    原始消息
         */
        void onGroupTempCommand(Bot<?> bot, Member<?> interactor, Message<?> message, int time);
    }
}