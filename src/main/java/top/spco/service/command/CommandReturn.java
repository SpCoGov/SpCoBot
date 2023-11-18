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
 * 命令的返回值
 *
 * @author SpCo
 * @version 3.0
 * @since 3.0
 */
public class CommandReturn {
    public static final String UNKNOWN_COMMAND = "未知命令";
    public static final String UNKNOWN_ARGUMENT = "命令参数不正确";
    public static final String FAILED = "尝试执行该命令时发生意外错误";
    public static final String EXPECTED_SEPARATOR = "预期以空格结束一个参数，但发现了尾随数据";
}