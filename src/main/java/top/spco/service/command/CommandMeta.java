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

import top.spco.SpCoBot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 命令的数据
 *
 * @author SpCo
 * @version 1.0.0
 * @since 0.1.1
 */
public class CommandMeta {
    private static final char SYNTAX_ESCAPE = '\\';
    private static final char SYNTAX_DOUBLE_QUOTE = '"';
    private final String command;
    private int cursor;
    private String label = null;
    private String[] args = null;

    /**
     * 获取命令的原始文本
     */
    public String getCommand() {
        return command;
    }

    /**
     * 获取命令标签
     */
    public String getLabel() {
        return label;
    }

    /**
     * 获取命令参数
     */
    public String[] getArgs() {
        return args;
    }

    /**
     * 创建命令元数据
     *
     * @param context 命令的原始文本
     */
    public CommandMeta(String context) throws CommandSyntaxException {
        this.command = context;
        if (context.startsWith(CommandSystem.COMMAND_START_SYMBOL)) {
            List<String> partList = new ArrayList<>();
            StringBuilder currentArgument = new StringBuilder();
            boolean inQuote = false;
            boolean inEscape = false;
            boolean afterQuoteClose = false;
            while (canRead()) {
                boolean skip = false;
                final char next = peek();
                if (inEscape) {
                    currentArgument.append(next);
                    inEscape = false;
                } else {
                    if (next == SYNTAX_DOUBLE_QUOTE) {
                        if (inQuote) {
                            skip = true;
                        }
                        inQuote = !inQuote;
                    } else if (next == SYNTAX_ESCAPE) {
                        inEscape = true;
                    } else if (next == CommandSystem.ARGUMENT_SEPARATOR_CHAR) {
                        if (inQuote) {
                            currentArgument.append(next);
                        } else {
                            partList.add(currentArgument.toString());
                            currentArgument = new StringBuilder();
                        }
                    } else {
                        currentArgument.append(next);
                    }
                }
                if (afterQuoteClose && next != CommandSystem.ARGUMENT_SEPARATOR_CHAR) {
                    currentArgument.append(next);
                    partList.add(currentArgument.toString());
                    String[] parts = partList.toArray(new String[0]);
                    // 标签转换为小写
                    this.label = parts[0].toLowerCase(Locale.ENGLISH).substring(1);
                    // 创建一个长度为parts数组的长度减一的数组, 用于存储命令的参数
                    String[] args = new String[parts.length - 1];
                    // 将parts数组中从第二个元素开始的所有元素复制到新的数组中
                    System.arraycopy(parts, 1, args, 0, parts.length - 1);
                    throw CommandSyntaxException.expectedSeparator(this.label, args, args.length - 1);
                }
                afterQuoteClose = skip;
                skip();
            }
            partList.add(currentArgument.toString());
            String[] parts = partList.toArray(new String[0]);
            // 标签转换为小写
            this.label = parts[0].toLowerCase(Locale.ENGLISH).substring(1);
            // 创建一个长度为parts数组的长度减一的数组, 用于存储命令的参数
            String[] args = new String[parts.length - 1];
            // 将parts数组中从第二个元素开始的所有元素复制到新的数组中
            System.arraycopy(parts, 1, args, 0, parts.length - 1);
            if (inQuote) {
                throw CommandSyntaxException.error("需要\"", this.label, args, args.length - 1);
            }
            this.args = removeEmptyStrings(args);
        }
    }

    public boolean canRead(final int length) {
        return cursor + length <= command.length();
    }

    public boolean canRead() {
        return canRead(1);
    }

    public char peek() {
        return command.charAt(cursor);
    }

    public char read() {
        return command.charAt(cursor++);
    }

    public void skip() {
        cursor++;
    }

    @Override
    public String toString() {
        return "CommandMeta{" +
                "command='" + command + '\'' +
                ", label='" + label + '\'' +
                ", args=" + Arrays.toString(args) +
                '}';
    }

    /**
     * 获取命令的字符串类型参数
     *
     * @param index 参数的索引
     * @return 对应参数的值
     * @throws CommandSyntaxException 索引超出了参数数组的范围
     */
    public String argument(int index) throws CommandSyntaxException {
        if (args.length < index + 1) {
            throw CommandSyntaxException.expectedSeparator(label, args, args.length - 1);
        }
        return args[index];
    }

    /**
     * 获取命令的整数类型参数
     *
     * @param index 参数的索引
     * @return 对应参数的值
     * @throws CommandSyntaxException 索引超出了参数数组的范围或该参数不是预期类型
     */
    public int integerArgument(int index) throws CommandSyntaxException {
        String arg = argument(index);
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            throw CommandSyntaxException.error("需要整型", label, args, index);
        }
    }

    /**
     * 获取命令的长整数类型参数
     *
     * @param index 参数的索引
     * @return 对应参数的值
     * @throws CommandSyntaxException 索引超出了参数数组的范围或该参数不是预期类型
     */
    public long longArgument(int index) throws CommandSyntaxException {
        String arg = argument(index);
        try {
            return Long.parseLong(arg);
        } catch (NumberFormatException e) {
            throw CommandSyntaxException.error("需要长整型", label, args, index);
        }
    }

    /**
     * 检测提交的参数是否过多
     *
     * @throws CommandSyntaxException 提交的参数过多
     */
    public void max(int amount) throws CommandSyntaxException {
        if (args.length > amount) {
            throw CommandSyntaxException.expectedSeparator(label, args, args.length - 1);
        }
    }

    /**
     * 获取命令的用户id类型参数
     *
     * @param index 参数的索引
     * @return 对应参数的值
     * @throws CommandSyntaxException 索引超出了参数数组的范围或该参数不是预期类型
     */
    public long userIdArgument(int index) throws CommandSyntaxException {
        String arg = argument(index);
        Matcher atMatcher = Pattern.compile("@(\\w+)").matcher(arg);
        if (SpCoBot.getInstance().getMessageService().isAtFormat(arg)) {
            Pattern pattern = Pattern.compile(SpCoBot.getInstance().getMessageService().getAtRegex());
            Matcher matcher = pattern.matcher(arg);
            return Long.parseLong(matcher.group(1));
        } else if (atMatcher.find()) {
            try {
                String id = atMatcher.group(1);
                return Long.parseLong(id);
            } catch (NumberFormatException e) {
                throw CommandSyntaxException.error("需要用户ID或@一位用户", label, args, index);
            }
        } else {
            try {
                return longArgument(index);
            } catch (CommandSyntaxException e) {
                throw CommandSyntaxException.error("需要用户ID或@一位用户", label, args, index);
            }
        }
    }

    public static String[] removeEmptyStrings(String[] array) {
        List<String> resultList = new ArrayList<>();

        for (String str : array) {
            if (str != null && !str.trim().isEmpty()) {
                // 如果字符串不为空且不只包含空格，则将其添加到结果列表中
                resultList.add(str);
            }
        }

        // 将结果列表转换为字符串数组
        return resultList.toArray(new String[0]);
    }
}