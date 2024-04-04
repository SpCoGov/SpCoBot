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

import top.spco.api.message.Message;
import top.spco.service.command.exceptions.CommandSyntaxException;
import top.spco.service.command.usage.Usage;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 命令的元数据。包含调用命令的消息，命令的原始文本，负责解析该命令的解析器，命令参数等
 *
 * @author SpCo
 * @version 3.0.0
 * @since 0.1.1
 */
public class CommandMeta {
    private final String command;
    private final Message<?> sourceMessage;
    private final Parser parser;
    private Usage usage;
    private String label;
    private final LinkedHashMap<String, Object> params = new LinkedHashMap<>();

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
    public LinkedHashMap<String, Object> getParams() {
        return params;
    }

    /**
     * 创建命令元数据
     *
     * @param context 命令的原始文本
     */
    public CommandMeta(String context, Message<?> sourceMessage, Parser parser) throws CommandSyntaxException {
        this.sourceMessage = sourceMessage;
        this.command = context;
        this.parser = parser;
    }

    public Message<?> getSourceMessage() {
        return sourceMessage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    public Usage getUsage() {
        return usage;
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

    @Override
    public String toString() {
        return "CommandMeta{" +
                "command='" + command + '\'' +
                ", label='" + label + '\'' +
                ", args=" + params.toString() +
                '}';
    }

//    /**
//     * 获取命令的字符串类型参数
//     *
//     * @param index 参数的索引
//     * @return 对应参数的值
//     * @throws CommandSyntaxException 索引超出了参数数组的范围
//     */
//    public String argument(int index) throws CommandSyntaxException {
//        if (params.length < index + 1) {
//            throw CommandSyntaxException.error("需要文本型", label, params, index);
//        }
//        return params[index];
//    }
//
//    /**
//     * 获取命令的整数类型参数
//     *
//     * @param index 参数的索引
//     * @return 对应参数的值
//     * @throws CommandSyntaxException 索引超出了参数数组的范围或该参数不是预期类型
//     */
//    public int integerArgument(int index) throws CommandSyntaxException {
//        String arg = argument(index);
//        try {
//            return Integer.parseInt(arg);
//        } catch (NumberFormatException e) {
//            throw CommandSyntaxException.error("需要整型", label, params, index);
//        }
//    }
//
//    /**
//     * 获取命令的长整数类型参数
//     *
//     * @param index 参数的索引
//     * @return 对应参数的值
//     * @throws CommandSyntaxException 索引超出了参数数组的范围或该参数不是预期类型
//     */
//    public long longArgument(int index) throws CommandSyntaxException {
//        String arg = argument(index);
//        try {
//            return Long.parseLong(arg);
//        } catch (NumberFormatException e) {
//            throw CommandSyntaxException.error("需要长整型", label, params, index);
//        }
//    }
//
//    /**
//     * 获取命令的用户id类型参数
//     *
//     * @param index 参数的索引
//     * @return 对应参数的值
//     * @throws CommandSyntaxException 索引超出了参数数组的范围或该参数不是预期类型
//     */
//    public long userIdArgument(int index) throws CommandSyntaxException {
//        String arg = argument(index);
//        Matcher atMatcher = Pattern.compile("@(\\w+)").matcher(arg);
//        if (SpCoBot.getInstance().getMessageService().isAtFormat(arg)) {
//            Pattern pattern = Pattern.compile(SpCoBot.getInstance().getMessageService().getAtRegex());
//            Matcher matcher = pattern.matcher(arg);
//            return Long.parseLong(matcher.group(1));
//        } else if (atMatcher.find()) {
//            try {
//                String id = atMatcher.group(1);
//                return Long.parseLong(id);
//            } catch (NumberFormatException e) {
//                throw CommandSyntaxException.error("需要用户ID或@一位用户", label, params, index);
//            }
//        } else {
//            try {
//                return longArgument(index);
//            } catch (CommandSyntaxException e) {
//                throw CommandSyntaxException.error("需要用户ID或@一位用户", label, params, index);
//            }
//        }
//    }
//
//    /**
//     * 获取命令的目标用户id类型参数
//     *
//     * @param index 参数的索引
//     * @return 对应参数的值
//     * @throws CommandSyntaxException 索引超出了参数数组的范围或该参数不是预期类型
//     */
//    public long targetUserIdArgument(int index) throws CommandSyntaxException {
//        try {
//            return userIdArgument(index);
//        } catch (Exception e) {
//            var quote = SpCoBot.getInstance().getMessageService().getQuote(this.sourceMessage);
//            if (quote == null) {
//                throw CommandSyntaxException.error("需要用户ID或@一位用户或在回复时发送这条命令", this.label, this.params, index);
//            }
//            return quote.getLeft().getFromId();
//        }
//    }

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