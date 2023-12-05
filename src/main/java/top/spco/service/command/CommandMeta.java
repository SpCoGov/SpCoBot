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
    private final String command;
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
    public CommandMeta(String context) {
        this.command = context;
        if (context.startsWith(CommandSystem.COMMAND_START_SYMBOL_STRING)) {
            // 将用户的输入以 空格 为分隔符分割
            String[] parts = context.split(CommandSystem.ARGUMENT_SEPARATOR_STRING);
            // 检查parts数组是否为空
            if (parts.length > 0) {
                // 创建一个长度为parts数组的长度减一的数组, 用于存储命令的参数
                String[] args = new String[parts.length - 1];
                // 将parts数组中从第二个元素开始的所有元素复制到新的数组中
                System.arraycopy(parts, 1, args, 0, parts.length - 1);
                // 标签转换为小写
                this.label = parts[0].toLowerCase(Locale.ENGLISH).substring(1);
//                // 删除args中每个元素的空格
//                for (int i = 0; i < args.length; i++) {
//                    args[i] = args[i].replaceAll(" ", "");
//                }
                this.args = args;
            }
        }
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
            throw CommandSyntaxException.DISPATCHER_UNKNOWN_ARGUMENT;
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
            throw CommandSyntaxException.error("Expected integer", label, args, index);
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
            throw CommandSyntaxException.error("Expected Long", label, args, index);
        }
    }

    /**
     * 检测提交的参数是否过多
     *
     * @throws CommandSyntaxException 提交的参数过多
     */
    public void max(int amount) throws CommandSyntaxException {
        if (args.length > amount) {
            throw CommandSyntaxException.DISPATCHER_EXPECTED_SEPARATOR;
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
                throw CommandSyntaxException.error("Expected UserId or at a user", label, args, index);
            }
        } else {
            try {
                return longArgument(index);
            } catch (CommandSyntaxException e) {
                throw CommandSyntaxException.error("Expected UserId or at a user", label, args, index);
            }
        }
    }
}