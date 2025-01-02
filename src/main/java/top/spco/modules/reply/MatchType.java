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

/**
 * 自定义回复的匹配类型
 *
 * @author SpCo
 * @version 3.2.0
 * @since 3.2.0
 */
public enum MatchType {
    /**
     * 前缀匹配
     */
    PREFIX,
    /**
     * 后缀匹配
     */
    SUFFIX,
    /**
     * 关键词匹配
     */
    KEYWORD,
    /**
     * 正则表达式匹配
     */
    REGEX,
    /**
     * 完全匹配
     */
    EXACT
}