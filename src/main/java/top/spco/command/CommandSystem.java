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
package top.spco.command;

import lombok.SneakyThrows;
import top.spco.events.CommandEvents;
import top.spco.user.BotUser;
import top.spco.user.UserFetchException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * <p>
 * Created on 2023/10/28 0028 18:11
 * <p>
 *
 * @author SpCo
 * @version 1.0
 * @since 1.0
 */
public class CommandSystem {
    private static CommandSystem instance;
    private static final Map<String, Command> friendCommands = new HashMap<>();
    private static final Map<String, Command> groupTempCommands = new HashMap<>();
    private static final Map<String, Command> groupCommands = new HashMap<>();

    private CommandSystem() {
        init();
        registerCommands();
    }

    @SneakyThrows
    private void registerCommands() {
        registerCommand(new InfoCommand());
        registerCommand(new SignCommand());
        registerCommand(new GetmeCommand());
        registerCommand(new DataCommand());
        registerCommand(new AboutCommand());
        registerCommand(new DivineCommand());
    }

    public Command getGroupCommand(String label) {
        if (groupCommands.containsKey(label)) {
            return groupCommands.get(label);
        }
        return null;
    }

    private void init() {
        CommandEvents.FRIEND_COMMAND.register((bot, interactor, message, time, command, label, args) -> {
            if (friendCommands.containsKey(label)) {
                try {
                    Command object = friendCommands.get(label);
                    BotUser sender = BotUser.getOrCreate(bot, interactor.getId());
                    if (!object.hasPermission(sender)) {
                        interactor.quoteReply(message, "[告知] 您无权使用此命令.");
                        return;
                    }
                    object.onCommand(bot, interactor, sender, message, time, command, label, args);
                } catch (UserFetchException e) {
                    interactor.handleException(message, "SpCoBot获取用户时失败", e);
                } catch (Exception e) {
                    interactor.handleException(message, e);
                }
            }
        });
        CommandEvents.GROUP_COMMAND.register((bot, from, sender, message, time, command, label, args) -> {
            if (groupCommands.containsKey(label)) {
                try {
                    Command object = groupCommands.get(label);
                    BotUser sender2 = BotUser.getOrCreate(bot, sender.getId());
                    if (!object.hasPermission(sender2)) {
                        sender.quoteReply(message, "[告知] 您无权使用此命令.");
                        return;
                    }
                    object.onCommand(bot, from, sender2, message, time, command, label, args);
                } catch (UserFetchException e) {
                    from.handleException(message, "SpCoBot获取用户时失败", e);
                } catch (Exception e) {
                    from.handleException(message, e);
                }
            }
        });
        CommandEvents.GROUP_TEMP_COMMAND.register((bot, interactor, message, time, command, label, args) -> {
            if (groupTempCommands.containsKey(label)) {
                try {
                    Command object = groupTempCommands.get(label);
                    BotUser sender = BotUser.getOrCreate(bot, interactor.getId());
                    if (!object.hasPermission(sender)) {
                        interactor.quoteReply(message, "[告知] 您无权使用此命令.");
                        return;
                    }
                    object.onCommand(bot, interactor, sender, message, time, command, label, args);
                } catch (UserFetchException e) {
                    interactor.handleException(message, "SpCoBot获取用户时失败", e);
                } catch (Exception e) {
                    interactor.handleException(message, e);
                }
            }
        });
    }

    public static CommandSystem getInstance() {
        if (instance == null) {
            instance = new CommandSystem();
        }
        return instance;
    }

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
            }
        }
    }

}