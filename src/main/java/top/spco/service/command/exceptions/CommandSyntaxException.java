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

import org.jetbrains.annotations.NotNull;

/**
 * 用于处理用户使用命令时的语法错误
 *
 * @author SpCo
 * @version 3.0.0
 * @since 0.3.0
 */
public class CommandSyntaxException extends Exception {
    public static final int CONTEXT_AMOUNT = 10;
    public static boolean ENABLE_COMMAND_STACK_TRACES = true;
    private final @NotNull String message;
    private final String input;
    private final int cursor;

    public CommandSyntaxException(final @NotNull String message, final String input, final int cursor) {
        super(message, null, ENABLE_COMMAND_STACK_TRACES, ENABLE_COMMAND_STACK_TRACES);
        this.message = message;
        this.input = input;
        this.cursor = cursor;
    }

    @Override
    public String getMessage() {
        final String context = getContext();
        if (context != null) {
            return "在第" + cursor + "个字符处发现" + message + ": " + context;
        } else {
            return message;
        }
    }

    public String getRawMessage() {
        return message;
    }

    public String getContext() {
        if (input == null || cursor < 0) {
            return null;
        }
        final StringBuilder builder = new StringBuilder();
        final int cursor = Math.min(input.length(), this.cursor);

        if (cursor > CONTEXT_AMOUNT) {
            builder.append("...");
        }

        builder.append(input, Math.max(0, cursor - CONTEXT_AMOUNT), cursor);
        builder.append("<--[此处]");

        return builder.toString();
    }
}