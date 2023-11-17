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

/**
 * Created on 2023/11/15 0015 19:33
 *
 * @author SpCo
 * @version 3.0
 * @since 3.0
 */
public class CommandSyntaxException extends Exception {
    public static final int CONTEXT_AMOUNT = 10;

    private CommandSyntaxException(String message) {
        super(message, null, true, true);
    }

    public static final CommandSyntaxException DISPATCHER_UNKNOWN_COMMAND = new CommandSyntaxException(CommandReturn.UNKNOWN_COMMAND);
    public static final CommandSyntaxException DISPATCHER_UNKNOWN_ARGUMENT = new CommandSyntaxException(CommandReturn.UNKNOWN_ARGUMENT);
    public static final CommandSyntaxException DISPATCHER_EXPECTED_SEPARATOR = new CommandSyntaxException(CommandReturn.EXPECTED_SEPARATOR);

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
            message += " at position " + cursor + ": " + context;
            return new CommandSyntaxException(message);
        }
        throw new RuntimeException(CommandReturn.FAILED);
    }
}