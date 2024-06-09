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
package top.spco.trade.alipay;

public enum TradeStatus {
    SUCCESS  // 业务交易明确成功，比如支付成功、退货成功

    ,FAILED  // 业务交易明确失败，比如支付明确失败、退货明确失败

    ,UNKNOWN // 业务交易状态未知，此时不清楚该业务是否成功或者失败，需要商户自行确认
}