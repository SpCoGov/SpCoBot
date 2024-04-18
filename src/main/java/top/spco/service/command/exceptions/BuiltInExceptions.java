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
package top.spco.service.command.exceptions;

import top.spco.service.command.Parser;

/**
 * 预设的命令语法错误
 *
 * @author SpCo
 * @version 3.1.1
 * @since 3.0.0
 */
public class BuiltInExceptions {
    public static CommandSyntaxException doubleTooLow(Parser parser, double found, double minimum) {
        return createWithContext("双精度浮点型数据不能小于" + minimum + "，但发现了" + found, parser);
    }

    public static CommandSyntaxException doubleTooHigh(Parser parser, double found, double maximum) {
        return createWithContext("双精度浮点型数据不能大于" + maximum + "，但发现了" + found, parser);
    }

    public static CommandSyntaxException floatTooLow(Parser parser, float found, float minimum) {
        return createWithContext("浮点型数据不能小于" + minimum + "，但发现了" + found, parser);
    }

    public static CommandSyntaxException floatTooHigh(Parser parser, float found, float maximum) {
        return createWithContext("浮点型数据不能大于" + maximum + "，但发现了" + found, parser);
    }

    public static CommandSyntaxException integerTooLow(Parser parser, int found, int minimum) {
        return createWithContext("整型数据不能小于" + minimum + "，但发现了" + found, parser);
    }

    public static CommandSyntaxException integerTooHigh(Parser parser, int found, int maximum) {
        return createWithContext("整型数据不能大于" + maximum + "，但发现了" + found, parser);
    }

    public static CommandSyntaxException longTooLow(Parser parser, long found, long minimum) {
        return createWithContext("长整型数据不能小于" + minimum + "，但发现了" + found, parser);
    }

    public static CommandSyntaxException longTooHigh(Parser parser, long found, long maximum) {
        return createWithContext("长整型数据不能大于" + maximum + "，但发现了" + found, parser);
    }

    public static CommandSyntaxException literalIncorrect(Parser parser, String excepted) {
        return createWithContext("需要字面量" + excepted, parser);
    }

    public static CommandSyntaxException parserExpectedStartOfQuote(Parser parser) {
        return createWithContext("字符串的开头需要双引号", parser);
    }

    public static CommandSyntaxException parserExpectedEndOfQuote(Parser parser) {
        return createWithContext("字符串的双引号不成对", parser);
    }

    public static CommandSyntaxException parserInvalidEscape(Parser parser, String found) {
        return createWithContext("双引号内的字符串包含无效的转义序列“\\" + found + "”", parser);
    }

    public static CommandSyntaxException parserInvalidBool(Parser parser, String found) {
        return createWithContext("无效的布尔型数据，需要“true”或“false”却出现了“" + found + "”", parser);
    }

    public static CommandSyntaxException parserInvalidInt(Parser parser, String found) {
        return createWithContext("无效的整型数据“" + found + "”", parser);
    }

    public static CommandSyntaxException parserExpectedInt(Parser parser) {
        return createWithContext("需要整型", parser);
    }

    public static CommandSyntaxException parserInvalidLong(Parser parser, String found) {
        return createWithContext("无效的长整型数据“" + found + "”", parser);
    }

    public static CommandSyntaxException parserExpectedLong(Parser parser) {
        return createWithContext("需要长整型", parser);
    }

    public static CommandSyntaxException parserInvalidDouble(Parser parser, String found) {
        return createWithContext("无效的双精度浮点型数据“" + found + "”", parser);
    }

    public static CommandSyntaxException parserExpectedDouble(Parser parser) {
        return createWithContext("需要双精度浮点型", parser);
    }

    public static CommandSyntaxException parserInvalidFloat(Parser parser, String found) {
        return createWithContext("无效的浮点型数据“" + found + "”", parser);
    }

    public static CommandSyntaxException parserExpectedFloat(Parser parser) {
        return createWithContext("需要浮点型", parser);
    }

    public static CommandSyntaxException parserExpectedBool(Parser parser) {
        return createWithContext("需要布尔型", parser);
    }

    public static CommandSyntaxException parserExpectedSymbol(Parser parser, String symbol) {
        return createWithContext("需要“" + symbol + "”", parser);
    }

    public static CommandSyntaxException parserExpectedString(Parser parser) {
        return createWithContext("需要字符串", parser);
    }

    public static CommandSyntaxException dispatcherUnknownCommand(Parser parser) {
        return createWithContext("未知的命令", parser);
    }

    public static CommandSyntaxException dispatcherUnknownArgument(Parser parser) {
        return createWithContext("错误的命令参数", parser);
    }

    public static CommandSyntaxException dispatcherExpectedArgumentSeparator(Parser parser) {
        return createWithContext("参数后应有空格分隔，但发现了紧邻的数据", parser);
    }

    public static CommandSyntaxException dispatcherParseException(Parser parser, String message) {
        return createWithContext("无法解析命令：" + message, parser);
    }

    public static CommandSyntaxException createWithContext(String context, Parser parser) {
        return new CommandSyntaxException(context, parser.getString(), parser.getCursor());
    }

}