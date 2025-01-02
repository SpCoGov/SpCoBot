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

import java.util.Random;
import java.util.Set;

/**
 * 自定义回复
 *
 * @author SpCo
 * @version 3.2.0
 * @since 3.2.0
 */
public interface CustomReply {
    MatchType getMatchType();

    boolean isMatch(String text);

    String reply(String text);

    static Set<String> checkRules(final String... rules) throws IllegalArgumentException {
        if (rules.length == 0) {
            throw new IllegalArgumentException();
        }
        for (String rule : rules) {
            if (rule.isEmpty()) {
                throw new IllegalArgumentException();
            }
        }
        return Set.of(rules);
    }

    static String randomString(String[] strings) {
        if (strings == null || strings.length == 0) {
            return null;
        }

        Random random = new Random();
        int randomIndex = random.nextInt(strings.length);
        return strings[randomIndex];
    }
}