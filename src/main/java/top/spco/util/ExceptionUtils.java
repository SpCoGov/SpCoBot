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
package top.spco.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常工具类。
 *
 * @author SpCo
 * @version 0.1.2
 * @since 0.1.2
 */
public class ExceptionUtils {
    /**
     * 将异常的堆栈追踪转换为字符串。
     *
     * @param e 异常对象
     * @return 包含异常堆栈追踪的字符串
     */
    public static String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}