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
 * 表示命令参数。
 *
 * @author SpCo
 * @version 1.0.0
 * @since 1.0.0
 */
public class CommandParam {
    public final String name;
    public final ParamType type;
    public final ParamContent content;
    public final String[] options;

    /**
     * @param name    命令参数的参数名
     * @param type    命令参数的类型
     * @param content 命令参数的内容
     * @param options 命令参数的选项（仅参数内容为{@link ParamContent#SELECTION 选择型}有效）
     */
    public CommandParam(String name, ParamType type, ParamContent content, String... options) throws IllegalArgumentException {
        if (content == ParamContent.SELECTION && options.length == 0) {
            throw new IllegalArgumentException("Options cannot be empty for content type SELECTION");
        }
        if (options.length != 0) {
            this.options = options;
        } else {
            this.options = new String[]{};
        }
        this.name = name;
        this.type = type;
        this.content = content;
    }

    @Override
    public String toString() {
        if (this.content == ParamContent.TARGET_USER_ID) {
            return CommandSystem.USAGE_TARGET_USER_ID_OPEN + this.name + CommandSystem.USAGE_TARGET_USER_ID_CLOSE;
        } else if (this.type == ParamType.REQUIRED) {
            if (this.content == ParamContent.SELECTION) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < this.options.length; i++) {
                    if (i > 0) {
                        sb.append(CommandSystem.USAGE_OR);
                    }
                    sb.append(this.options[i]);
                }
                if (this.options.length == 1) {
                    return sb.toString();
                }
                return CommandSystem.USAGE_REQUIRED_OPEN + sb.toString() + CommandSystem.USAGE_REQUIRED_CLOSE;
            }
            return CommandSystem.USAGE_REQUIRED_OPEN + this.name + CommandSystem.USAGE_REQUIRED_CLOSE;
        } else if (this.type == ParamType.OPTIONAL) {
            if (this.content == ParamContent.SELECTION) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < this.options.length; i++) {
                    if (i > 0) {
                        sb.append(CommandSystem.USAGE_OR);
                    }
                    sb.append(this.options[i]);
                }
                return CommandSystem.USAGE_OPTIONAL_OPEN + sb.toString() + CommandSystem.USAGE_OPTIONAL_CLOSE;
            }
            return CommandSystem.USAGE_OPTIONAL_OPEN + this.name + CommandSystem.USAGE_OPTIONAL_CLOSE;
        }
        return "";
    }

    /**
     * 命令参数的类型
     */
    public enum ParamType {
        /**
         * 表示一个参数是必填的
         */
        REQUIRED,
        /**
         * 表示一个参数是选填的
         */
        OPTIONAL
    }

    /**
     * 命令参数的内容
     */
    public enum ParamContent {
        /**
         * 表示一个参数应该提交整数类型
         */
        INTEGER,
        /**
         * 表示一个参数应该提交长整数类型
         */
        LONG,
        /**
         * 表示一个参数应该提交文本类型
         */
        TEXT,
        /**
         * 表示一个参数应该提交用户ID<p>
         * 可以直接提交该用户的ID、@该用户
         */
        USER_ID,
        /**
         * 表示一个参数应该提交用户ID<p>
         * 与{@link #USER_ID}不同的是，此内容类型支持隐式传递（如引用该用户发送的消息）
         */
        TARGET_USER_ID,
        /**
         * 表示一个参数提交的内容只能为所提供的内容中选择
         */
        SELECTION
    }
}