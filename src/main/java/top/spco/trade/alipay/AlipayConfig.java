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

import top.spco.SpCoBot;
import top.spco.core.config.PayApiSettings;

public class AlipayConfig {
    // 支付宝openapi域名
    private static String openApiDomain = "https://openapi.alipay.com/gateway.do";
    // 商户应用id
    private static String appid = SpCoBot.getInstance().getSettings().getStringProperty(PayApiSettings.ALIPAY_APP_ID);
    // RSA私钥，用于对商户请求报文加签
    private static String privateKey = SpCoBot.getInstance().getSettings().getStringProperty(PayApiSettings.ALIPAY_PRIVATE_KEY);
    // 支付宝RSA公钥，用于验签支付宝应答
    private static String alipayPublicKey = SpCoBot.getInstance().getSettings().getStringProperty(PayApiSettings.ALIPAY_ALIPAY_PUBLIC_KEY);
    // 签名类型
    private static String signType = "RSA2";
    // 最大查询次数
    private static int maxQueryRetry = 5;
    // 查询间隔（毫秒）
    private static long queryDuration = 5000;
    // 最大撤销次数
    private static int maxCancelRetry = 3;
    // 撤销间隔（毫秒）
    private static long cancelDuration = 2000;

    public static int getMaxQueryRetry() {
        return maxQueryRetry;
    }

    public static long getQueryDuration() {
        return queryDuration;
    }

    public static int getMaxCancelRetry() {
        return maxCancelRetry;
    }

    public static long getCancelDuration() {
        return cancelDuration;
    }

    public static String getAlipayPublicKey() {
        return alipayPublicKey;
    }

    public static String getAppid() {
        return appid;
    }

    public static String getOpenApiDomain() {
        return openApiDomain;
    }

    public static String getSignType() {
        return signType;
    }

    public static String getPrivateKey() {
        return privateKey;
    }
}