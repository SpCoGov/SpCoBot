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

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import top.spco.SpCoBot;
import top.spco.api.Bot;
import top.spco.api.Interactive;
import top.spco.api.User;
import top.spco.api.message.Message;
import top.spco.events.CommandEvents;
import top.spco.service.chat.ChatType;
import top.spco.service.command.commands.HelpCommand;
import top.spco.service.command.exceptions.BuiltInExceptions;
import top.spco.service.command.exceptions.CommandRegistrationException;
import top.spco.service.command.exceptions.CommandSyntaxException;
import top.spco.service.command.usage.Usage;
import top.spco.service.command.usage.parameters.Parameter;
import top.spco.service.command.usage.parameters.SpecifiedParameter;
import top.spco.service.command.usage.parameters.TargetUserIdParameter;
import top.spco.user.BotUser;
import top.spco.user.BotUsers;
import top.spco.user.UserFetchException;
import top.spco.util.LoggedTimer;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

/**
 * 一个用于处理机器人应用中命令的系统<p>
 * 它负责注册、执行和管理各种命令的权限
 *
 * @author SpCo
 * @version 3.0.0
 * @since 0.1.0
 */
public class CommandDispatcher {
    public static final char PARAMETER_SEPARATOR_CHAR = ' ';
    public static final String COMMAND_START_SYMBOL = "/";
    public static final char USAGE_OPTIONAL_OPEN = '[';
    public static final char USAGE_OPTIONAL_CLOSE = ']';
    public static final char USAGE_REQUIRED_OPEN = '<';
    public static final char USAGE_REQUIRED_CLOSE = '>';
    public static final char USAGE_TARGET_USER_ID_OPEN = '{';
    public static final char USAGE_TARGET_USER_ID_CLOSE = '}';
    public static final char USAGE_OR = '|';
    private static CommandDispatcher instance;
    private static boolean registered = false;
    private boolean frozen = false;
    private final Set<Command> allCommands = new HashSet<>();
    private final Map<String, Command> friendCommands = new HashMap<>();
    private final Map<String, Command> groupTempCommands = new HashMap<>();
    private final Map<String, Command> groupCommands = new HashMap<>();
    private int commandCount;

    private CommandDispatcher() {
        if (registered) {
            return;
        }
        registered = true;
        LoggedTimer time = new LoggedTimer();
        time.start("初始化命令系统");
        init();
        registerCommands();
        time.stop();
    }

    private void registerCommands() {
        Set<Command> toBeRegistered = new HashSet<>();

        try {
            URL url = SpCoBot.pluginFile.toURI().toURL();
            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .setScanners(new SubTypesScanner(false), new TypeAnnotationsScanner())
                    .setUrls(url));
            Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(CommandMarker.class);
            for (Class<?> cls : annotatedClasses) {
                Command command = (Command) cls.getDeclaredConstructor().newInstance();
                toBeRegistered.add(command);
            }

        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException | MalformedURLException e) {
            throw new RuntimeException(e);
        }

        for (var command : toBeRegistered) {
            try {
                registerCommand(command);
            } catch (Exception e) {
                SpCoBot.LOGGER.error(e);
            }
        }
        freeze();
        SpCoBot.LOGGER.info("已注册{}个命令。", toBeRegistered.size());
    }

    /**
     * 获取所有已注册的群组命令
     */
    public Command getGroupCommand(String label) {
        return groupCommands.get(label);
    }

    private void init() {
        CommandEvents.FRIEND_COMMAND.register((bot, interactor, message, time) -> {
            if (SpCoBot.getInstance().chatDispatcher.isInChat(interactor, ChatType.FRIEND)) {
                return;
            }
            callCommand(friendCommands, interactor, interactor, message, bot, time);
        });
        CommandEvents.GROUP_COMMAND.register((bot, from, sender, message, time) -> {
            if (SpCoBot.getInstance().chatDispatcher.isInChat(from, ChatType.GROUP)) {
                return;
            }
            callCommand(groupCommands, from, sender, message, bot, time);
        });
        CommandEvents.GROUP_TEMP_COMMAND.register((bot, interactor, message, time) -> {
            if (SpCoBot.getInstance().chatDispatcher.isInChat(interactor, ChatType.GROUP_TEMP)) {
                return;
            }
            callCommand(groupTempCommands, interactor, interactor, message, bot, time);
        });
    }

    private void callCommand(Map<String, Command> targetCommands, Interactive<?> from, User<?> sender, Message<?> message, Bot<?> bot, int time) {
        Parser input = new Parser(message, message.toMessageContext().substring(1));
        while (input.canRead() && input.peek() != ' ') {
            input.skip();
        }
        String label = input.getString().substring(0, input.getCursor());
        if (!targetCommands.containsKey(label)) {
            return;
        }
        // 读完标签后光标位于标签后，光标之后还有一格空格，所以需要后移一位到达参数
        if (input.canRead()) {
            input.skip();
        }
        message.setCommandMessage();
        callCommand(targetCommands, from, sender, message, bot, time, label, input);
    }

    private void callCommand(Map<String, Command> targetCommands, Interactive<?> from, User<?> sender, Message<?> message, Bot<?> bot, int time, String label, Parser parser) {
        try {
            // 获取命令实例和发送者和发送者的用户实例
            Command object = targetCommands.get(label);
            BotUser user = BotUsers.getOrCreate(sender.getId());
            // 先检测发送者是否有权限
            try {
                if (!object.hasPermission(user)) {
                    from.quoteReply(message, "您无权使用此命令。");
                    return;
                }
            } catch (SQLException e) {
                from.handleException(message, "获取用户权限失败", e);
            }
            Potential potential = new Potential();
            potential.setLast(BuiltInExceptions.dispatcherUnknownCommand(parser));
            CommandMeta meta = new CommandMeta(parser.getMessage().toMessageContext(), parser.getMessage(), parser);
            // 判断用户提交的参数是否符合命令的用法
            final int start = parser.getCursor();
            for (Usage usage : object.getUsages()) {
                meta.setUsage(usage);
                meta.getParams().clear();
                parser.setCursor(start);
                // 判断用法的每个参数是否与用户提交的匹配
                try {
                    for (Parameter<?> param : usage.getParams()) {
                        if (!param.isOptional()) {
                            meta.getParams().put(param.getName(), param.parse(parser));
                            potential.further(usage);
                        } else {
                            if (parser.canRead()) {
                                meta.getParams().put(param.getName(), param.parse(parser));
                                potential.further(usage);
                            } else {
                                meta.getParams().put(param.getName(), param.getDefaultValue());
                                potential.further(usage);
                            }
                        }
                        if (parser.canRead()) {
                            if (parser.peek() != PARAMETER_SEPARATOR_CHAR) {
                                // 如果下一个字符不是参数分隔符
                                throw BuiltInExceptions.dispatcherExpectedArgumentSeparator(parser);
                            }
                            // 跳过参数分隔符
                            parser.skip();
                        }
                    }
                } catch (CommandSyntaxException e) {
                    potential.add(usage, e);
                    continue;
                }
                if (parser.canRead()) {
                    potential.setLast(BuiltInExceptions.dispatcherExpectedArgumentSeparator(parser));
                    potential.remove(usage);
                } else {
                    object.onCommand(bot, from, sender, user, message, time, meta, usage.name);
                    return;
                }
            }
            // 用户提交的参数不符合命令的任何用法
            from.handleException(message, potential.get().getMessage());
        } catch (UserFetchException e) {
            from.handleException(message, "SpCoBot获取用户时失败", e);
        } catch (Exception e) {
            SpCoBot.LOGGER.error(e);
            from.handleException(message, e);
        }
    }

    public static boolean contains(final Object[] array, final Object objectToFind) {
        if (array == null) {
            return false;
        }
        if (objectToFind == null) {
            for (Object o : array) {
                if (o == null) {
                    return true;
                }
            }
        } else {
            for (Object o : array) {
                if (objectToFind.equals(o)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取{@link CommandDispatcher}单例
     */
    public synchronized static CommandDispatcher getInstance() {
        if (instance == null) {
            instance = new CommandDispatcher();
        }
        return instance;
    }

    /**
     * 注册一个命令
     *
     * @param command 待注册的命令
     */
    public void registerCommand(Command command) throws CommandRegistrationException {
        if (frozen) {
            throw new IllegalStateException("Cannot register command after the pre-initialization phase!");
        }
        for (String label : command.getLabels()) {
            label = label.toLowerCase(Locale.ENGLISH);
            if (label.isEmpty() || label == null) {
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
        commandCount++;
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
         *
         *      比如有两个命令用法
         *          A：/command <A>
         *          B：/command <A>
         *      A、B这两个用法的参数直接重复。
         *
         *      比如有两个命令用法
         *          A：/command {A} <B>
         *          B：/command <A>
         *      A、B这两个用法的参数间接重复。
         */

        // 用于储存命令的所有用法的用法名，便于判断是否重复
        List<String> usageNames = new ArrayList<>();
        // 用于储存命令的所有用法，便于判断是否重复
        Map<String, String> usages = new HashMap<>();
        // 遍历一个命令的所有用法

        for (Usage usage : command.getUsages()) {
            // 检测命令用法的用法名是否重复
            if (usageNames.contains(usage.name)) {
                throw new CommandRegistrationException("Duplicate command usage: " + usage);
            }
            // 存储处理过的参数， 用于创建处理过的命令用法
            List<Parameter<?>> processedParams = new ArrayList<>();
            // 遍历当前用法的所有参数
            for (var param : usage.getParams()) {
                // 判断该参数是否为可省略的（可选参数、目标用户ID参数）参数
                if (param.isOptional() || param instanceof TargetUserIdParameter) {
                    continue;
                }
                // 如果不是，则将其记录到“处理过的参数”里
                // 为了消除参数名的影响，将所有参数的参数名转换为“参数”
                if (param instanceof SpecifiedParameter) {
                    processedParams.add(new SpecifiedParameter("Param", param.isOptional(), ((SpecifiedParameter) param).getDefaultValue(), ((SpecifiedParameter) param).getDefaultValue()));
                } else {
                    processedParams.add(new Parameter<Object>("Param", param.isOptional(), "参数") {
                        @Override
                        public Object parse(Parser parser) {
                            return null;
                        }
                    });
                }
            }
            // 用处理过的参数创建一个命令用法
            Usage processedUsage = new Usage("Usage", "Usage", processedParams, null);
            // 检测命令用法的用法是否重复
            if (usages.containsKey(processedUsage.toString())) {
                throw new CommandRegistrationException("Duplicate command usage: " + usage + " and " + usages.get(processedUsage.toString()));
            }
            // 添加用法名
            usageNames.add(usage.name);
            // 添加用法
            usages.put(processedUsage.toString(), usage.toString());
        }
        return true;
    }

    /**
     * 获取命令帮助列表
     *
     * @see HelpCommand
     */
    public List<String> getHelpList() {
        List<String> helpList = new ArrayList<>(commandCount);
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

    public List<String> getUsages(String label, Interactive<?> from) {
        CommandScope scope = CommandScope.getCommandScope(from);
        if (scope == null) {
            return null;
        }
        Map<String, Command> commands;
        switch (scope) {
            case ONLY_GROUP -> commands = groupCommands;
            case ONLY_FRIEND -> commands = friendCommands;
            case ONLY_PRIVATE -> commands = groupTempCommands;
            default -> {
                return null;
            }
        }
        if (!commands.containsKey(label)) {
            return null;
        }
        var ite = commands.get(label).getUsages().iterator();
        List<String> usages = new ArrayList<>();
        while (ite.hasNext()) {
            usages.add(ite.next().toString());
        }
        return usages;
    }

    private void freeze() {
        this.frozen = true;
    }
}