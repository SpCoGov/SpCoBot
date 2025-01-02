/*
 * Copyright 2025 SpCo
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
package top.spco.modules.reply;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 匹配正则表达式的自定义回复
 *
 * @author SpCo
 * @version 3.2.0
 * @since 3.2.0
 */
public abstract class RegexCustomReply implements CustomReply {
    private final Set<String> regexes;

    public RegexCustomReply(final String... regexes) {
        if (regexes.length == 0) {
            throw new IllegalArgumentException("正则表达式不能为空");
        }
        for (String regex : regexes) {
            if (regex.isEmpty()) {
                throw new IllegalArgumentException("正则表达式不能为空");
            }
            try {
                Pattern.compile(regex);
            } catch (PatternSyntaxException e) {
                throw new IllegalArgumentException("正则表达式‘" + regex + "’不合法");
            }
        }
        this.regexes = Set.of(regexes);
    }

    @Override
    public MatchType getMatchType() {
        return MatchType.REGEX;
    }

    @Override
    public boolean isMatch(String text) {
        for (String regex : regexes) {
            if (Pattern.matches(regex, text)) {
                return true;
            }
        }
        return false;
    }
}