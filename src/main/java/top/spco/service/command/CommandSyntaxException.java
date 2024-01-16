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

import java.util.Arrays;

/**
 * 用于处理用户使用命令时的语法错误
 *
 * @author SpCo
 * @version 2.0.0
 * @since 0.3.0
 */
public class CommandSyntaxException extends RuntimeException {
    /**
     * 错误信息显示的上下文长度。默认 {@value}
     */
    public static final int CONTEXT_AMOUNT = 10;

    private CommandSyntaxException(String message) {
        super(message, null, true, true);
    }

    public static final CommandSyntaxException DISPATCHER_UNKNOWN_COMMAND = new CommandSyntaxException(CommandReturn.UNKNOWN_COMMAND);

    public static CommandSyntaxException unknownArgument(String label, String[] args, int unknownArgIndex) {
        return error(CommandReturn.UNKNOWN_ARGUMENT, label, args, unknownArgIndex);
    }

    public static CommandSyntaxException expectedSeparator(String label, String[] args, int unknownArgIndex) {
        return error(CommandReturn.EXPECTED_SEPARATOR, label, args, unknownArgIndex);
    }

    public static CommandSyntaxException error(String message, String label, String[] args, int unknownArgIndex) {
        if (args.length >= unknownArgIndex + 1) {
            int cursor = label.length();
            StringBuilder command = new StringBuilder(label);
            for (int i = 0; i <= unknownArgIndex; i++) {
                command.append(" ");
                command.append(args[i]);
                cursor += 1 + args[i].length();
            }
            cursor = Math.min(command.length(), cursor);
            String context = "";
            if (cursor > CONTEXT_AMOUNT) {
                context = "...";
            }
            context += command.substring(Math.max(0, cursor - CONTEXT_AMOUNT), cursor);
            context += "<--[HERE]";
            String errormessage = "在第" + cursor + "个字符处发现" + message + ": " + context;
            return new CommandSyntaxException(errormessage);
        } else {
            String[] emptyArray = new String[unknownArgIndex + 1];
            Arrays.fill(emptyArray, "");
            return error(message, label, emptyArray, unknownArgIndex);
        }
    }
}