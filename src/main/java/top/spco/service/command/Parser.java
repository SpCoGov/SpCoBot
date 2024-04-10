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
import top.spco.service.command.exceptions.BuiltInExceptions;
import top.spco.service.command.exceptions.CommandSyntaxException;

/**
 * 命令文本解析器。用于解析命令的标签和参数。
 *
 * @author SpCo
 * @version 3.0.4
 * @since 3.0.0
 */
public class Parser {
    private static final char SYNTAX_ESCAPE = '\\';
    private static final char SYNTAX_DOUBLE_QUOTE = '"';
    private static final char SYNTAX_SINGLE_QUOTE = '\'';
    private final Message<?> message;
    private final String string;
    private int cursor;

    public Parser(Message<?> message, final String string) {
        this.message = message;
        this.string = string;
    }

    public String getString() {
        return string;
    }

    public Message<?> getMessage() {
        return message;
    }

    public void setCursor(int cursor) {
        this.cursor = cursor;
    }

    public int getCursor() {
        return cursor;
    }

    public int getRemainingLength() {
        return string.length() - cursor;
    }

    public int getTotalLength() {
        return string.length();
    }

    public String getRead() {
        return string.substring(0, cursor);
    }

    public String getRemaining() throws CommandSyntaxException {
        if (!canRead()) {
            throw BuiltInExceptions.createWithContext("需要字符串", this);
        }
        return string.substring(cursor);
    }

    public boolean canRead(final int length) {
        return cursor + length <= string.length();
    }

    public boolean canRead() {
        return canRead(1);
    }

    public char peek() {
        return string.charAt(cursor);
    }

    public char peek(final int offset) {
        return string.charAt(cursor + offset);
    }

    public char read() {
        return string.charAt(cursor++);
    }

    public void skip() {
        cursor++;
    }

    public static boolean isAllowedNumber(final char c) {
        return c >= '0' && c <= '9' || c == '.' || c == '-';
    }

    public static boolean isQuotedStringStart(char c) {
        return c == SYNTAX_DOUBLE_QUOTE || c == SYNTAX_SINGLE_QUOTE;
    }

    public void skipWhitespace() {
        while (canRead() && Character.isWhitespace(peek())) {
            skip();
        }
    }

    public int readInt() throws CommandSyntaxException {
        final int start = cursor;
        while (canRead() && isAllowedNumber(peek())) {
            skip();
        }

        final String number = string.substring(start, cursor);
        if (number.isEmpty()) {
            throw BuiltInExceptions.readerExpectedInt(this);
        }
        try {
            return Integer.parseInt(number);
        } catch (final NumberFormatException ex) {
            cursor = start;
            throw BuiltInExceptions.readerInvalidInt(this, number);
        }
    }

    public long readLong() throws CommandSyntaxException {
        final int start = cursor;
        while (canRead() && isAllowedNumber(peek())) {
            skip();
        }

        final String number = string.substring(start, cursor);
        if (number.isEmpty()) {
            throw BuiltInExceptions.readerExpectedLong(this);
        }
        try {
            return Long.parseLong(number);
        } catch (final NumberFormatException ex) {
            cursor = start;
            throw BuiltInExceptions.readerInvalidLong(this, number);
        }
    }

    public double readDouble() throws CommandSyntaxException {
        final int start = cursor;
        while (canRead() && isAllowedNumber(peek())) {
            skip();
        }

        final String number = string.substring(start, cursor);
        if (number.isEmpty()) {
            throw BuiltInExceptions.readerExpectedDouble(this);
        }
        try {
            return Double.parseDouble(number);
        } catch (final NumberFormatException ex) {
            cursor = start;
            throw BuiltInExceptions.readerInvalidDouble(this, number);
        }
    }

    public float readFloat() throws CommandSyntaxException {
        final int start = cursor;
        while (canRead() && isAllowedNumber(peek())) {
            skip();
        }

        final String number = string.substring(start, cursor);
        if (number.isEmpty()) {
            throw BuiltInExceptions.readerExpectedFloat(this);
        }
        try {
            return Float.parseFloat(number);
        } catch (final NumberFormatException ex) {
            cursor = start;
            throw BuiltInExceptions.readerInvalidFloat(this, number);
        }
    }

    public static boolean isAllowedInUnquotedString(final char c) {
        return c >= '0' && c <= '9'
                || c >= 'A' && c <= 'Z'
                || c >= 'a' && c <= 'z'
                || c == '_' || c == '-'
                || c == '.' || c == '+';
    }

    public String readUnquotedString() throws CommandSyntaxException {
        final int start = cursor;
        if (!canRead()) {
            setCursor(start);
            throw BuiltInExceptions.createWithContext("需要字符串", this);
        }
        while (canRead() && isAllowedInUnquotedString(peek())) {
            skip();
        }
        return string.substring(start, cursor);
    }

    public String readQuotedString() throws CommandSyntaxException {
        if (!canRead()) {
            return "";
        }
        final int start = cursor;
        final char next = peek();
        if (!isQuotedStringStart(next)) {
            setCursor(start);
            throw BuiltInExceptions.readerExpectedStartOfQuote(this);
        }
        skip();
        return readStringUntil(next);
    }

    public String readStringUntil(char terminator) throws CommandSyntaxException {
        final StringBuilder result = new StringBuilder();
        boolean escaped = false;
        while (canRead()) {
            final char c = read();
            if (escaped) {
                if (c == terminator || c == SYNTAX_ESCAPE) {
                    result.append(c);
                    escaped = false;
                } else {
                    setCursor(getCursor() - 1);
                    throw BuiltInExceptions.readerInvalidEscape(this, String.valueOf(c));
                }
            } else if (c == SYNTAX_ESCAPE) {
                escaped = true;
            } else if (c == terminator) {
                return result.toString();
            } else {
                result.append(c);
            }
        }

        throw BuiltInExceptions.readerExpectedEndOfQuote(this);
    }

    public String readString() throws CommandSyntaxException {
        if (!canRead()) {
            throw BuiltInExceptions.createWithContext("需要字符串", this);
        }
        final char next = peek();
        if (isQuotedStringStart(next)) {
            skip();
            return readStringUntil(next);
        }
        return readUnquotedString();
    }

    public boolean readBoolean() throws CommandSyntaxException {
        final int start = cursor;
        final String value = readString();
        if (value.isEmpty()) {
            throw BuiltInExceptions.readerExpectedBool(this);
        }

        if (value.equals("true")) {
            return true;
        } else if (value.equals("false")) {
            return false;
        } else {
            cursor = start;
            throw BuiltInExceptions.readerInvalidBool(this, value);
        }
    }

    public void expect(final char c) throws CommandSyntaxException {
        if (!canRead() || peek() != c) {
            throw BuiltInExceptions.readerExpectedSymbol(this, String.valueOf(c));
        }
        skip();
    }

    public static boolean isSingleWord(String word) {
        for (char s : word.toCharArray()) {
            if (!isAllowedInUnquotedString(s)) {
                return false;
            }
        }
        return true;
    }
}