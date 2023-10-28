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
package top.spco.events;

import top.spco.base.api.Bot;
import top.spco.base.api.Friend;
import top.spco.base.api.Interactive;
import top.spco.base.api.User;
import top.spco.base.event.Event;
import top.spco.base.event.EventFactory;

/**
 * <p>
 * Created on 2023/10/27 0027 17:57
 * <p>
 *
 * @author SpCo
 * @version 1.0
 * @since 1.0
 */
public class CommandEvents {
    /**
     * Called when a command is received.
     */
    public static final Event<Command> COMMAND = EventFactory.createArrayBacked(Command.class, callbacks -> (bot, from, sender, command, label, args) -> {
        for (Command event : callbacks) {
            event.onCommand(bot, from, sender, command, label, args);
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
         * @param command 命令的原始文本 (如 {@code "/command a b c"} )
         * @param label   命令的类型 (如 {@code "command"} )
         * @param args    命令的参数 (如 {@code ["a", "b", "c"]) }
         */
        void onCommand(Bot bot, Interactive from, User sender, String command, String label, String[] args);
    }

    /**
     * Called when a friend command is received.
     */
    public static final Event<FriendCommand> FRIEND_COMMAND = EventFactory.createArrayBacked(FriendCommand.class, callbacks -> (bot, interactor, command, label, args) -> {
        for (FriendCommand event : callbacks) {
            event.onFriendCommand(bot, interactor, command, label, args);
        }
    });

    @FunctionalInterface
    public interface FriendCommand {
        /**
         * 假如好友发送了命令 "/command a b c"
         *
         * @param bot        收到命令的机器人
         * @param interactor 命令的发送者
         * @param command    命令的原始文本 (如 {@code "/command a b c"} )
         * @param label      命令的类型 (如 {@code "command"} )
         * @param args       命令的参数 (如 {@code ["a", "b", "c"]) }
         */
        void onFriendCommand(Bot bot, Friend interactor, String command, String label, String[] args);
    }
}