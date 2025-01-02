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

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;

/**
 * 管理所有自定义回复规则<p>
 * 匹配顺序：完全匹配 -> 前缀匹配 -> 后缀匹配 -> 关键词匹配 -> 正则表达式匹配
 *
 * @author SpCo
 * @version 3.2.0
 * @since 3.2.0
 */
public class Replier {
    private final LinkedHashSet<ExactCustomReply> exactRules = new LinkedHashSet<>();
    private final LinkedHashSet<PrefixCustomReply> prefixRules = new LinkedHashSet<>();
    private final LinkedHashSet<SuffixCustomReply> suffixRules = new LinkedHashSet<>();
    private final LinkedHashSet<KeywordsCustomReply> keywordRules = new LinkedHashSet<>();
    private final LinkedHashSet<RegexCustomReply> regexRules = new LinkedHashSet<>();

    /**
     * 添加一个自定义回复规则
     */
    public void add(CustomReply reply) {
        switch (reply.getMatchType()) {
            case EXACT -> exactRules.add((ExactCustomReply) reply);
            case REGEX -> regexRules.add((RegexCustomReply) reply);
            case PREFIX -> prefixRules.add((PrefixCustomReply) reply);
            case SUFFIX -> suffixRules.add((SuffixCustomReply) reply);
            case KEYWORD -> keywordRules.add((KeywordsCustomReply) reply);
        }
    }

    /**
     * 根据已添加的自定义回复规则获取参数文本的对应自定义回复
     *
     * @param string 要回复的文本
     * @return 要回复的文本，如果没有匹配的规则则返回 {@code null}
     */
    public String reply(String string) {
        String result;
        LinkedList<HashSet<?>> rules = new LinkedList<>();
        rules.add(exactRules);
        rules.add(prefixRules);
        rules.add(suffixRules);
        rules.add(keywordRules);
        rules.add(regexRules);
        for (HashSet<?> ruleSet : rules) {
            result = getReplyText(ruleSet, string);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private String getReplyText(HashSet<?> ruleSet, String string) {
        for (Object r : ruleSet) {
            CustomReply rule = (CustomReply) r;
            if (rule.isMatch(string)) {
                return rule.reply(string);
            }
        }
        return null;
    }
}