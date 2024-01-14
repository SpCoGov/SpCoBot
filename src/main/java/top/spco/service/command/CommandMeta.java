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
import top.spco.api.message.Message;

import java.lang.reflect.Array;
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
 * @version 2.0.0
 * @since 0.1.1
 */
public class CommandMeta {
    private static final char SYNTAX_ESCAPE = '\\';
    private static final char SYNTAX_DOUBLE_QUOTE = '"';
    private final String command;
    private final Message<?> sourceMessage;
    private int cursor;
    private String label = null;
    private String[] args = null;
    private String[] originalArgs = null;

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
    public CommandMeta(String context, Message<?> sourceMessage) throws CommandSyntaxException {
        this.sourceMessage = sourceMessage;
        this.command = context;
        if (context.startsWith(CommandDispatcher.COMMAND_START_SYMBOL)) {
            List<String> partList = new ArrayList<>();
            StringBuilder currentArgument = new StringBuilder();
            // 标志：是否在引号内
            boolean inQuote = false;
            // 标志：是否处于转义状态
            boolean inEscape = false;
            // 标志：是否处于后引号的后一个字符
            boolean afterQuoteClose = false;
            int commandLength = command.length();
            // 循环迭代命令字符串中的每个字符
            while (cursor + 1 <= commandLength) {
                boolean isAfterQuoteClose = false;
                final char next = peek();
                // 如果处于转义状态，直接将字符添加到当前参数中
                if (inEscape) {
                    currentArgument.append(next);
                    // 退出转义状态
                    inEscape = false;
                } else {
                    // 如果下一个字符是双引号
                    if (next == SYNTAX_DOUBLE_QUOTE) {
                        // 如果在引号内，则说明将要退出引号状态。
                        // 这里的 isAfterQuoteClose 和 afterQuoteClose 用于检测后引号的后一个字符是否为参数分隔符
                        if (inQuote) {
                            isAfterQuoteClose = true;
                        }
                        // 切换引号状态
                        inQuote = !inQuote;
                        // 如果下一个字符是转义字符
                    } else if (next == SYNTAX_ESCAPE) {
                        // 进入转义状态
                        inEscape = true;
                        // 如果下一个字符是参数分隔符
                    } else if (next == CommandDispatcher.ARGUMENT_SEPARATOR_CHAR) {
                        // 如果在引号内
                        if (inQuote) {
                            // 将字符添加到当前参数中
                            currentArgument.append(next);
                        } else {
                            // 添加当前参数到参数列表中
                            partList.add(currentArgument.toString());
                            // 清空当前参数构建器
                            currentArgument.setLength(0);
                        }
                    } else {
                        // 将字符添加到当前参数中
                        currentArgument.append(next);
                    }
                }
                // 如果在引号关闭后且下一个字符不是参数分隔符
                if (afterQuoteClose && next != CommandDispatcher.ARGUMENT_SEPARATOR_CHAR) {
                    // 将字符添加到当前参数中
                    currentArgument.append(next);
                    // 添加当前参数到参数列表中
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
                afterQuoteClose = isAfterQuoteClose;
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
            // 如果还处于引号内
            if (inQuote) {
                throw CommandSyntaxException.error("需要后引号(\")", this.label, args, args.length - 1);
            }
            // 移除参数中的空字符串
            this.originalArgs = removeEmptyStrings(args);
            this.args = this.originalArgs;
        }
    }

    public void setUsage(final CommandUsage usage) {
        var args = usage.params;
        if (args.size() == this.args.length) {
            return;
        }
        int index = 0;
        for (var arg : args) {
            if (arg.content == CommandParam.ParamContent.TARGET_USER_ID) {
                var quote = SpCoBot.getInstance().getMessageService().getQuote(this.sourceMessage);
                if (quote != null) {
                    this.args = insertIntoArray(this.originalArgs, String.valueOf(quote.getLeft().getFromId()), index);
                }
            }
            index += 1;
        }
    }

    /**
     * 在数组中插入一个元素并返回新数组。
     *
     * @param originalArray   原始数组。
     * @param elementToInsert 要插入的元素。
     * @param insertIndex     插入的索引位置。
     * @return 包含了新元素的新数组。
     */
    public static <T> T[] insertIntoArray(T[] originalArray, T elementToInsert, int insertIndex) {
        List<T> list = new ArrayList<>(Arrays.asList(originalArray));
        list.add(insertIndex, elementToInsert);
        @SuppressWarnings("unchecked")
        T[] newArray = (T[]) Array.newInstance(originalArray.getClass().getComponentType(), list.size());
        return list.toArray(newArray);
    }

    /**
     * 读取光标位置的字符
     */
    public char peek() {
        return command.charAt(cursor);
    }

    public char read() {
        return command.charAt(cursor++);
    }

    /**
     * 将光标后移一位
     */
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
            throw CommandSyntaxException.error("需要文本型", label, args, index);
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

    /**
     * 获取命令的目标用户id类型参数
     *
     * @param index 参数的索引
     * @return 对应参数的值
     * @throws CommandSyntaxException 索引超出了参数数组的范围或该参数不是预期类型
     */
    public long targetUserIdArgument(int index) throws CommandSyntaxException {
        try {
            return userIdArgument(index);
        } catch (Exception e) {
            var quote = SpCoBot.getInstance().getMessageService().getQuote(this.sourceMessage);
            if (quote == null) {
                throw CommandSyntaxException.error("需要用户ID或@一位用户或在回复时发送这条命令", this.label, this.args, index);
            }
            return quote.getLeft().getFromId();
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