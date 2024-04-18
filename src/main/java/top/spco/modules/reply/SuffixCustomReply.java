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
package top.spco.modules.reply;

import java.util.Set;

/**
 * 匹配后缀的自定义回复
 *
 * @author SpCo
 * @version 3.2.0
 * @since 3.2.0
 */
public abstract class SuffixCustomReply implements CustomReply {
    private final Set<String> suffixes;

    public SuffixCustomReply(final String... suffixes) {
        try {
            this.suffixes = CustomReply.checkRules(suffixes);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("后缀不能为空");
        }

    }

    @Override
    public MatchType getMatchType() {
        return MatchType.SUFFIX;
    }

    @Override
    public boolean isMatch(String text) {
        for (String suffix : suffixes) {
            if (text.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }
}