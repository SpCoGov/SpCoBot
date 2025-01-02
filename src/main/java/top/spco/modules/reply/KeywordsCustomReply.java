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

/**
 * 匹配关键词的自定义回复
 *
 * @author SpCo
 * @version 3.2.0
 * @since 3.2.0
 */
public abstract class KeywordsCustomReply implements CustomReply {
    private final Set<String> keywords;

    public KeywordsCustomReply(final String... keywords) {
        try {
            this.keywords = CustomReply.checkRules(keywords);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("关键词不能为空");
        }
    }

    @Override
    public MatchType getMatchType() {
        return MatchType.KEYWORD;
    }

    @Override
    public boolean isMatch(String text) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}