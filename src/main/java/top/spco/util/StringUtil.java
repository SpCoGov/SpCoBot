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
package top.spco.util;

/**
 * 字符串工具类。
 *
 * @author SpCo
 * @version 4.0.0
 * @since 4.0.0
 */
public class StringUtil {
    /**
     * 将驼峰命名法的字符串转换为蛇形命名法（snake_case）。
     * <p>
     * 例如：
     * <ul>
     *     <li>输入："MyVariableName"，输出："my_variable_name"</li>
     * </ul>
     * </p>
     *
     * @param input 要转换的字符串，必须是驼峰命名法
     * @return 转换后的蛇形命名法字符串。如果输入为 {@code null} 或空字符串，则返回原字符串
     */
    public static String toSnakeCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder();
        char[] chars = input.toCharArray();

        for (char c : chars) {
            if (Character.isUpperCase(c)) {
                // 如果不是第一个字符，先添加下划线
                if (!result.isEmpty()) {
                    result.append('_');
                }
                // 转为小写
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }
}
