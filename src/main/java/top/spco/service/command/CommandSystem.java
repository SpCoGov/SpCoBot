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
import top.spco.api.Bot;
import top.spco.api.Interactive;
import top.spco.api.User;
import top.spco.api.message.Message;
import top.spco.events.CommandEvents;
import top.spco.service.chat.ChatType;
import top.spco.service.command.commands.*;
import top.spco.user.BotUser;
import top.spco.user.UserFetchException;
import top.spco.util.ArrayUtils;

import java.sql.SQLException;
import java.util.*;

/**
 * 一个用于处理机器人应用中命令的系统<p>
 * 它负责注册、执行和管理各种命令的权限
 *
 * @author SpCo
 * @version 1.0.0
 * @since 0.1.0
 */
public class CommandSystem {
    public static final String COMMAND_START_SYMBOL = "/";
    public static final char ARGUMENT_SEPARATOR_CHAR = ' ';
    public static final char USAGE_OPTIONAL_OPEN = '[';
    public static final char USAGE_OPTIONAL_CLOSE = ']';
    public static final char USAGE_REQUIRED_OPEN = '<';
    public static final char USAGE_REQUIRED_CLOSE = '>';
    public static final char USAGE_TARGET_USER_ID_OPEN = '{';
    public static final char USAGE_TARGET_USER_ID_CLOSE = '}';
    public static final char USAGE_OR = '|';
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
        toBeRegistered.add(new MuteCommand());
        toBeRegistered.add(new UnmuteCommand());
        toBeRegistered.add(new GetotherCommand());
        toBeRegistered.add(new NoteCommand());
        toBeRegistered.add(new KickCommand());
        toBeRegistered.add(new MemoryCommand());

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
        CommandEvents.FRIEND_COMMAND.register((bot, interactor, message, time, command, label, args, meta) -> {
            if (SpCoBot.getInstance().chatManager.isInChat(interactor, ChatType.FRIEND)) {
                return;
            }
            callCommand(friendCommands, label, interactor, interactor, message, bot, time, command, args, meta);
        });
        CommandEvents.GROUP_COMMAND.register((bot, from, sender, message, time, command, label, args, meta) -> {
            if (SpCoBot.getInstance().chatManager.isInChat(from, ChatType.GROUP)) {
                return;
            }
            callCommand(groupCommands, label, from, sender, message, bot, time, command, args, meta);
        });
        CommandEvents.GROUP_TEMP_COMMAND.register((bot, interactor, message, time, command, label, args, meta) -> {
            if (SpCoBot.getInstance().chatManager.isInChat(interactor, ChatType.GROUP_TEMP)) {
                return;
            }
            callCommand(groupTempCommands, label, interactor, interactor, message, bot, time, command, args, meta);
        });
    }

    private void callCommand(Map<String, Command> targetCommands, String label, Interactive from, User sender, Message message, Bot bot, int time, String command, String[] args, CommandMeta meta) {
        // 先检测用户提交的命令是否被注册
        if (targetCommands.containsKey(label)) {
            try {
                // 获取命令实例和发送者和发送者的用户实例
                Command object = targetCommands.get(label);
                BotUser user = BotUser.getOrCreate(sender.getId());
                // 先检测发送者是否有权限
                try {
                    if (!object.hasPermission(user)) {
                        from.quoteReply(message, "[告知] 您无权使用此命令.");
                        return;
                    }
                } catch (SQLException e) {
                    from.handleException(message, "获取用户权限失败", e);
                }
                Exception lastException = CommandSyntaxException.DISPATCHER_UNKNOWN_COMMAND;
                // 判断用户提交的参数是否符合命令的用法
                for (CommandUsage usage : object.getUsages()) {
                    // 先判断用户提交的参数的数量是否符合此用法需提交的参数数量
                    // 在一些情况下 用户正确提交的参数会比需要提交的参数数量少
                    // 先确定这个方法所需的最少参数数量
                    int minParamSize = usage.params.size();
                    // 若命令用法的最后一个参数是可选的，则将最少参数数量减一（因为只有最后一位可以是可选的）
                    if (usage.params.size() != 0 && usage.params.get(usage.params.size() - 1).type == CommandParam.ParamType.OPTIONAL) {
                        minParamSize -= 1;
                    }
                    // 若命令用法有目标用户ID参数，则将最少参数数量减一
                    if (usage.hasTargetParam()) {
                        minParamSize -= 1;
                    }
                    // 若实际提交的参数数量小于最少参数数量或大于用法的要求参数数量，跳过这个参数
                    if (args.length < minParamSize) {
                        lastException = CommandSyntaxException.unknownArgument(usage.getLabel(), args, args.length - 1);
                        continue;
                    } else if (args.length > usage.params.size()) {
                        lastException = CommandSyntaxException.expectedSeparator(usage.getLabel(), args, args.length - 1);
                        continue;
                    }
                    // 判断用法的每个参数是否与用户提交的匹配
                    int index = 0;
                    try {
                        for (CommandParam param : usage.params) {
                            if (param.type != CommandParam.ParamType.OPTIONAL) {
                                switch (param.content) {
                                    case INTEGER -> meta.integerArgument(index);
                                    case LONG -> meta.longArgument(index);
                                    case USER_ID -> meta.userIdArgument(index);
                                    case TEXT -> meta.argument(index);
                                    case SELECTION -> {
                                        String userSend = meta.argument(index);
                                        if (!ArrayUtils.contains(param.options, userSend)) {
                                            throw CommandSyntaxException.error("未知的" + param.name, usage.getLabel(), args, index);
                                        }
                                    }
                                    case TARGET_USER_ID -> {
                                        try {
                                            meta.userIdArgument(index);
                                        } catch (Exception e) {
                                            if (SpCoBot.getInstance().getMessageService().getQuote(message) == null) {
                                                throw CommandSyntaxException.error("需要用户ID或@一位用户或在回复时发送这条命令", usage.getLabel(), args, index);
                                            }
                                        }
                                    }
                                }
                            }
                            index += 1;
                        }
                    } catch (Exception e) {
                        lastException = e;
                        continue;
                    }
                    // 如用户的提交参数符合用法需求，退出循环并交由命令对象处理
                    object.onCommand(bot, from, sender, user, message, time, command, label, args, meta, usage.name);
                    return;
                }
                // 用户提交的参数不符合命令的任何用法
                from.handleException(message, lastException.getMessage());
            } catch (UserFetchException e) {
                from.handleException(message, "SpCoBot获取用户时失败", e);
            } catch (Exception e) {
                e.printStackTrace();
                from.handleException(message, e);
            }
        }
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
    public void registerCommand(Command command) throws CommandRegistrationException {
        String[] labels = command.getLabels();
        for (String label : labels) {
            label = label.toLowerCase(Locale.ENGLISH);
            if (label.equals("") || label == null) {
                throw new CommandRegistrationException("The command label is not valid.");
            }
            switch (command.getScope()) {
                case ALL -> {
                    if (groupCommands.containsKey(label)) {
                        throw new CommandRegistrationException("The command: " + label + " is registered in the group command.");
                    } else if (groupTempCommands.containsKey(label)) {
                        throw new CommandRegistrationException("The command: " + label + " is registered in the private command.");
                    } else if (friendCommands.containsKey(label)) {
                        throw new CommandRegistrationException("The command: " + label + " is registered in the friend command.");
                    } else {
                        if (validateCommand(command)) {
                            groupCommands.put(label, command);
                            friendCommands.put(label, command);
                            groupTempCommands.put(label, command);
                        }
                    }
                }
                case ONLY_GROUP -> {
                    if (groupCommands.containsKey(label)) {
                        throw new CommandRegistrationException("The command: " + label + " is registered in the group command.");
                    } else {
                        if (validateCommand(command)) {
                            groupCommands.put(label, command);
                        }
                    }
                }
                case ONLY_FRIEND -> {
                    if (groupTempCommands.containsKey(label)) {
                        throw new CommandRegistrationException("The command: " + label + " is registered in the private command.");
                    } else if (friendCommands.containsKey(label)) {
                        throw new CommandRegistrationException("The command: " + label + " is registered in the friend command.");
                    } else {
                        if (validateCommand(command)) {
                            friendCommands.put(label, command);
                        }
                    }
                }
                case ONLY_PRIVATE -> {
                    if (groupTempCommands.containsKey(label)) {
                        throw new CommandRegistrationException("The command: " + label + " is registered in the private command.");
                    } else if (friendCommands.containsKey(label)) {
                        throw new CommandRegistrationException("The command: " + label + " is registered in the friend command.");
                    } else {
                        if (validateCommand(command)) {
                            friendCommands.put(label, command);
                            groupTempCommands.put(label, command);
                        }
                    }
                }
                default ->
                        throw new CommandRegistrationException("The command: " + command.getLabels()[0] + " registration failed.");
            }
        }
        allCommands.add(command);
        command.init();
    }

    public boolean validateCommand(Command command) throws CommandRegistrationException {
        /*
         * 要检测一个命令是否可注册：
         * 1. 命令用法名不重复
         * 2. 命令用法的参数不直接或间接重复
         *      比如有两个命令用法
         *          A：/command <A> [B]
         *          B：/command <A>
         *      A、B这两个用法的参数间接重复。
         *      比如有两个命令用法
         *          A：/command <A>
         *          B：/command <A>
         *      A、B这两个用法的参数直接重复。
         *      比如有两个命令用法
         *          A：/command {A} <B>
         *          B：/command <A>
         *      A、B这两个用法的参数间接重复。
         */

        // 用于储存命令的所有用法的用法名，便于判断是否重复
        List<String> usageNames = new ArrayList<>();
        // 用于储存命令的所有用法，便于判断是否重复
        List<String> usages = new ArrayList<>();
        // 遍历一个命令的所有用法
        for (CommandUsage usage : command.getUsages()) {
            // 检测命令用法的用法名是否重复
            if (usageNames.contains(usage.name)) {
                throw new CommandRegistrationException("Duplicate command parameter: " + usage);
            }
            // 存储处理过的参数， 用于创建处理过的命令用法
            List<CommandParam> processedParams = new ArrayList<>();
            // 遍历当前用法的所有参数
            for (var params : usage.params) {
                // 判断该参数是否为可省略的（可选参数、目标用户ID参数）参数
                if (params.type == CommandParam.ParamType.OPTIONAL || params.content == CommandParam.ParamContent.TARGET_USER_ID) {
                    continue;
                }
                // 如果不是，则将其记录到“处理过的参数”里
                // 为了消除参数名的影响，将所有参数的参数名转换为“参数”
                processedParams.add(new CommandParam("Param", params.type, params.content, "参数"));
            }
            // 用处理过的参数创建一个命令用法
            CommandUsage processedUsage = new CommandUsage("Usage", "Usage", processedParams);
            // 检测命令用法的用法是否重复
            if (usages.contains(processedUsage.toString())) {
                throw new CommandRegistrationException("Duplicate command usage: " + usage);
            }

            // 添加用法名
            usageNames.add(usage.name);
            // 添加用法
            usages.add(processedUsage.toString());
        }
        return true;
    }

    /**
     * 获取命令帮助列表
     *
     * @see HelpCommand
     */
    public static List<String> getHelpList() {
        List<String> helpList = new ArrayList<>();
        for (Command command : allCommands) {
            if (command.isVisible()) {
                int index = 0;
                String mainLabel = "";
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