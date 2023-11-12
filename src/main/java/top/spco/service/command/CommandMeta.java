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

import java.util.Locale;

/**
 * 命令的数据
 *
 * @author SpCo
 * @version 1.1
 * @since 1.1
 */
public class CommandMeta {
    private String command = null;
    private String label = null;
    private String[] args = null;

    /**
     * 获取命令的原始文本
     */
    public String getCommand() {
        return command;
    }

    /**
     * 获取命令标签
     */
    public String getLabel() {
        return label;
    }

    /**
     * 获取命令参数
     */
    public String[] getArgs() {
        return args;
    }

    /**
     * 创建命令元数据
     *
     * @param context 命令的原始文本
     */
    public CommandMeta(String context) {
        if (context.startsWith("/")) {
            // 将用户的输入以 空格 为分隔符分割
            String[] parts = context.split(" ");
            // 检查parts数组是否为空
            if (parts.length > 0) {
                // 创建一个长度为parts数组的长度减一的数组, 用于存储命令的参数
                String[] args = new String[parts.length - 1];
                // 将parts数组中从第二个元素开始的所有元素复制到新的数组中
                System.arraycopy(parts, 1, args, 0, parts.length - 1);
                // 标签转换为小写
                String label = parts[0].toLowerCase(Locale.ENGLISH).substring(1);
                this.command = context;
                this.label = label;
                // 删除args中每个元素的空格
                for (int i = 0; i < args.length; i++) {
                    args[i] = args[i].replaceAll(" ", "");
                }
                this.args = args;
            }
        }
    }
}