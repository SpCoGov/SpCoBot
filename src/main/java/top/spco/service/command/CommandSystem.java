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
package top.spco.service.command;

import lombok.SneakyThrows;
import top.spco.SpCoBot;
import top.spco.events.CommandEvents;
import top.spco.service.chat.ChatType;
import top.spco.service.command.commands.*;
import top.spco.user.BotUser;
import top.spco.user.UserFetchException;

import java.sql.SQLException;
import java.util.*;

/**
 * Created on 2023/10/28 0028 18:11
 *
 * @author SpCo
 * @version 2.1
 * @since 1.0
 */
public class CommandSystem {
    private static CommandSystem instance;
    private static boolean registered = false;
    private static final List<Command> allCommands = new ArrayList<>();
    private static final Map<String, Command> friendCommands = new HashMap<>();
    private static final Map<String, Command> groupTempCommands = new HashMap<>();
    private static final Map<String, Command> groupCommands = new HashMap<>();

    private CommandSystem() {
        if (registered) {
            return;
        }
        registered = true;
        init();
        registerCommands();
    }

    @SneakyThrows
    private void registerCommands() {
        List<Command> toBeRegistered = new ArrayList<>();
        toBeRegistered.add(new InfoCommand());
        toBeRegistered.add(new SignCommand());
        toBeRegistered.add(new GetmeCommand());
        toBeRegistered.add(new DataCommand());
        toBeRegistered.add(new AboutCommand());
        toBeRegistered.add(new DivineCommand());
        toBeRegistered.add(new HelpCommand());
        toBeRegistered.add(new BalancetopCommand());
        toBeRegistered.add(new StatisticsCommand());
        toBeRegistered.add(new BanmeCommand());
        toBeRegistered.add(new DashscopeCommand());


        toBeRegistered.add(new TestCommand());

        for (var command : toBeRegistered) {
            registerCommand(command);
        }
    }

    /**
     * 获取所有已注册的群组命令
     */
    public Command getGroupCommand(String label) {
        if (groupCommands.containsKey(label)) {
            return groupCommands.get(label);
        }
        return null;
    }

    private void init() {
        CommandEvents.FRIEND_COMMAND.register((bot, interactor, message, time, command, label, args) -> {
            if (SpCoBot.getInstance().chatManager.isInChat(interactor, ChatType.FRIEND)) {
                return;
            }
            if (friendCommands.containsKey(label)) {
                try {
                    Command object = friendCommands.get(label);
                    BotUser user = BotUser.getOrCreate(interactor.getId());
                    try {
                        if (!object.hasPermission(user)) {
                            interactor.quoteReply(message, "[告知] 您无权使用此命令.");
                            return;
                        }
                    } catch (SQLException e) {
                        interactor.handleException(message, "获取用户权限失败", e);
                    }
                    object.onCommand(bot, interactor, interactor, user, message, time, command, label, args);
                } catch (UserFetchException e) {
                    interactor.handleException(message, "SpCoBot获取用户时失败", e);
                } catch (Exception e) {
                    interactor.handleException(message, e);
                }
            }
        });
        CommandEvents.GROUP_COMMAND.register((bot, from, sender, message, time, command, label, args) -> {
            if (SpCoBot.getInstance().chatManager.isInChat(from, ChatType.GROUP)) {
                return;
            }
            if (groupCommands.containsKey(label)) {
                try {
                    Command object = groupCommands.get(label);
                    BotUser user = BotUser.getOrCreate(sender.getId());
                    try {
                        if (!object.hasPermission(user)) {
                            from.quoteReply(message, "[告知] 您无权使用此命令.");
                            return;
                        }
                    } catch (SQLException e) {
                        from.handleException(message, "获取用户权限失败", e);
                    }
                    object.onCommand(bot, from, sender, user, message, time, command, label, args);
                } catch (UserFetchException e) {
                    from.handleException(message, "SpCoBot获取用户时失败", e);
                } catch (Exception e) {
                    from.handleException(message, e);
                }
            }
        });
        CommandEvents.GROUP_TEMP_COMMAND.register((bot, interactor, message, time, command, label, args) -> {
            if (SpCoBot.getInstance().chatManager.isInChat(interactor, ChatType.GROUP_TEMP)) {
                return;
            }
            if (groupTempCommands.containsKey(label)) {
                try {
                    Command object = groupTempCommands.get(label);
                    BotUser user = BotUser.getOrCreate(interactor.getId());
                    try {
                        if (!object.hasPermission(user)) {
                            interactor.quoteReply(message, "[告知] 您无权使用此命令.");
                            return;
                        }
                    } catch (SQLException e) {
                        interactor.handleException(message, "获取用户权限失败", e);
                    }
                    object.onCommand(bot, interactor, interactor, user, message, time, command, label, args);
                } catch (UserFetchException e) {
                    interactor.handleException(message, "SpCoBot获取用户时失败", e);
                } catch (Exception e) {
                    interactor.handleException(message, e);
                }
            }
        });
    }

    /**
     * 获取{@link CommandSystem}单例
     */
    public static CommandSystem getInstance() {
        if (instance == null) {
            instance = new CommandSystem();
        }
        return instance;
    }

    /**
     * 注册一个命令
     *
     * @param command 待注册的命令
     */
    public static void registerCommand(Command command) throws CommandRegistrationException {
        String[] labels = command.getLabels();
        for (String label : labels) {
            label = label.toLowerCase(Locale.ENGLISH);
            if (label.equals("") || label == null) {
                throw new CommandRegistrationException("The command label is not valid.");
            }
            switch (command.getType()) {
                case ALL -> {
                    if (groupCommands.containsKey(label)) {
                        throw new CommandRegistrationException("The command: " + label + " is registered in the group command.");
                    } else if (groupTempCommands.containsKey(label)) {
                        throw new CommandRegistrationException("The command: " + label + " is registered in the private command.");
                    } else if (friendCommands.containsKey(label)) {
                        throw new CommandRegistrationException("The command: " + label + " is registered in the friend command.");
                    } else {
                        groupCommands.put(label, command);
                        friendCommands.put(label, command);
                        groupTempCommands.put(label, command);
                    }
                }
                case ONLY_GROUP -> {
                    if (groupCommands.containsKey(label)) {
                        throw new CommandRegistrationException("The command: " + label + " is registered in the group command.");
                    } else {
                        groupCommands.put(label, command);
                    }
                }
                case ONLY_FRIEND -> {
                    if (groupTempCommands.containsKey(label)) {
                        throw new CommandRegistrationException("The command: " + label + " is registered in the private command.");
                    } else if (friendCommands.containsKey(label)) {
                        throw new CommandRegistrationException("The command: " + label + " is registered in the friend command.");
                    } else {
                        friendCommands.put(label, command);
                    }
                }
                case ONLY_PRIVATE -> {
                    if (groupTempCommands.containsKey(label)) {
                        throw new CommandRegistrationException("The command: " + label + " is registered in the private command.");
                    } else if (friendCommands.containsKey(label)) {
                        throw new CommandRegistrationException("The command: " + label + " is registered in the friend command.");
                    } else {
                        friendCommands.put(label, command);
                        groupTempCommands.put(label, command);
                    }
                }
                default ->
                        throw new CommandRegistrationException("The command: " + command.getLabels()[0] + " registration failed.");
            }
        }
        allCommands.add(command);
        command.init();
    }

    /**
     * 获取命令帮助列表
     * @see HelpCommand
     */
    public static List<String> getHelpList() {
        List<String> helpList = new ArrayList<>();
        for (Command command : allCommands) {
            if (command.isVisible()) {
                int index = 0;
                String mainLabel = "MainLabel";
                for (String label : command.getLabels()) {
                    if (index == 0) {
                        mainLabel = label;
                        helpList.add(mainLabel + " - " + command.getDescriptions());
                    } else {
                        helpList.add(label + " -> " + mainLabel);
                    }
                    index += 1;
                }
            }
        }
        helpList.sort(String::compareToIgnoreCase);
        return helpList;
    }
}