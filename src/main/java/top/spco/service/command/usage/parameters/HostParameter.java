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
package top.spco.service.command.usage.parameters;

import top.spco.service.command.Parser;
import top.spco.service.command.exceptions.BuiltInExceptions;
import top.spco.service.command.exceptions.CommandSyntaxException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 是主机地址的命令参数<p>
 * 如 {@code 8.8.8.8} 或 {@code google.com}
 *
 * @author SpCo
 * @version 3.0.2
 * @since 3.0.2
 */
public class HostParameter extends Parameter<String> {
    public HostParameter(String name, boolean isOptional, String defaultValue) {
        super(name, isOptional, defaultValue);
    }

    @Override
    public String parse(Parser parser) throws CommandSyntaxException {
        final int start = parser.getCursor();
        String input = parser.readUnquotedString();
        String ipRegex = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";
        String domainRegex = "[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\\.?";

        Pattern ipPattern = Pattern.compile(ipRegex);
        Pattern domainPattern = Pattern.compile(domainRegex);

        Matcher ipMatcher = ipPattern.matcher(input);
        Matcher domainMatcher = domainPattern.matcher(input);

        if (ipMatcher.matches() || domainMatcher.matches()) {
            return input;
        } else {
            parser.setCursor(start);
            throw BuiltInExceptions.createWithContext("无效的主机地址“" + input + "”", parser);
        }
    }
}