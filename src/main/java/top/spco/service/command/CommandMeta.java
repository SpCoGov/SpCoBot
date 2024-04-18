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
package top.spco.service.command;

import top.spco.api.message.Message;
import top.spco.service.command.exceptions.CommandSyntaxException;
import top.spco.service.command.usage.Usage;

import java.util.LinkedHashMap;

/**
 * 命令的元数据。包含调用命令的消息，命令的原始文本，负责解析该命令的解析器，命令参数等
 *
 * @author SpCo
 * @version 3.0.4
 * @since 0.1.1
 */
public class CommandMeta {
    private final String command;
    private final Message<?> sourceMessage;
    private final Parser parser;
    private Usage usage;
    private String label;
    private final LinkedHashMap<String, Object> params = new LinkedHashMap<>();

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
    public LinkedHashMap<String, Object> getParams() {
        return params;
    }

    /**
     * 创建命令元数据
     *
     * @param context 命令的原始文本
     */
    public CommandMeta(String context, Message<?> sourceMessage, Parser parser) throws CommandSyntaxException {
        this.sourceMessage = sourceMessage;
        this.command = context;
        this.parser = parser;
    }

    public Parser getParser() {
        return parser;
    }

    public Message<?> getSourceMessage() {
        return sourceMessage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    public Usage getUsage() {
        return usage;
    }

    @Override
    public String toString() {
        return "CommandMeta{" +
                "command='" + command + '\'' +
                ", label='" + label + '\'' +
                ", args=" + params +
                '}';
    }
}