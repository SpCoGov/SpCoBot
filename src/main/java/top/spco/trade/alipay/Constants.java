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
package top.spco.trade.alipay;

public class Constants {

    private Constants() {
        // No Constructor.
    }

    public static final String SUCCESS = "10000"; // 成功
    public static final String PAYING  = "10003"; // 用户支付中
    public static final String FAILED  = "40004"; // 失败
    public static final String ERROR   = "20000"; // 系统异常
}