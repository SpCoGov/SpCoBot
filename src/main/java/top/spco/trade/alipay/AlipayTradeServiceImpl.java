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

import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;

public class AlipayTradeServiceImpl extends AbsAlipayTradeService {

    public static class ClientBuilder {
        private String gatewayUrl;
        private String appid;
        private String privateKey;
        private String format;
        private String charset;
        private String alipayPublicKey;
        private String signType;

        public AlipayTradeServiceImpl build() {
            if (isEmpty(gatewayUrl)) {
                gatewayUrl = AlipayConfig.getOpenApiDomain(); // 与mcloudmonitor网关地址不同
            }
            if (isEmpty(appid)) {
                appid = AlipayConfig.getAppid();
            }
            if (isEmpty(privateKey)) {
                privateKey = AlipayConfig.getPrivateKey();
            }
            if (isEmpty(format)) {
                format = "json";
            }
            if (isEmpty(charset)) {
                charset = "utf-8";
            }
            if (isEmpty(alipayPublicKey)) {
                alipayPublicKey = AlipayConfig.getAlipayPublicKey();
            }
            if (isEmpty(signType)) {
                signType = AlipayConfig.getSignType();
            }

            return new AlipayTradeServiceImpl(this);
        }

        private static boolean isEmpty(String str) {
            return str == null || str.isEmpty();
        }

        public ClientBuilder setAlipayPublicKey(String alipayPublicKey) {
            this.alipayPublicKey = alipayPublicKey;
            return this;
        }

        public ClientBuilder setAppid(String appid) {
            this.appid = appid;
            return this;
        }

        public ClientBuilder setCharset(String charset) {
            this.charset = charset;
            return this;
        }

        public ClientBuilder setFormat(String format) {
            this.format = format;
            return this;
        }

        public ClientBuilder setGatewayUrl(String gatewayUrl) {
            this.gatewayUrl = gatewayUrl;
            return this;
        }

        public ClientBuilder setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
            return this;
        }

        public ClientBuilder setSignType(String signType) {
            this.signType = signType;
            return this;
        }

        public String getAlipayPublicKey() {
            return alipayPublicKey;
        }

        public String getSignType() {
            return signType;
        }

        public String getAppid() {
            return appid;
        }

        public String getCharset() {
            return charset;
        }

        public String getFormat() {
            return format;
        }

        public String getGatewayUrl() {
            return gatewayUrl;
        }

        public String getPrivateKey() {
            return privateKey;
        }
    }

    public AlipayTradeServiceImpl(ClientBuilder builder) {
        if (isEmpty(builder.getGatewayUrl())) {
            throw new NullPointerException("gatewayUrl should not be NULL!");
        }
        if (isEmpty(builder.getAppid())) {
            throw new NullPointerException("appid should not be NULL!");
        }
        if (isEmpty(builder.getPrivateKey())) {
            throw new NullPointerException("privateKey should not be NULL!");
        }
        if (isEmpty(builder.getFormat())) {
            throw new NullPointerException("format should not be NULL!");
        }
        if (isEmpty(builder.getCharset())) {
            throw new NullPointerException("charset should not be NULL!");
        }
        if (isEmpty(builder.getAlipayPublicKey())) {
            throw new NullPointerException("alipayPublicKey should not be NULL!");
        }
        if (isEmpty(builder.getSignType())) {
            throw new NullPointerException("signType should not be NULL!");
        }

        client = new DefaultAlipayClient(builder.getGatewayUrl(), builder.getAppid(), builder.getPrivateKey(),
                builder.getFormat(), builder.getCharset(), builder.getAlipayPublicKey(), builder.getSignType());
    }

    private static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    // 商户可以直接使用的pay方法
    @Override
    public AlipayF2FPayResult tradePay(AlipayTradePayRequestBuilder builder) {
        validateBuilder(builder);

        final String outTradeNo = builder.getOutTradeNo();

        AlipayTradePayRequest request = new AlipayTradePayRequest();
        // 设置平台参数
        request.setNotifyUrl(builder.getNotifyUrl());
        String appAuthToken = builder.getAppAuthToken();
        // todo 等支付宝sdk升级公共参数后使用如下方法
        // request.setAppAuthToken(appAuthToken);
        request.putOtherTextParam("app_auth_token", builder.getAppAuthToken());

        // 设置业务参数
        request.setBizContent(builder.toJsonString());

        // 首先调用支付api
        AlipayTradePayResponse response = (AlipayTradePayResponse) getResponse(client, request);

        AlipayF2FPayResult result = new AlipayF2FPayResult(response);
        if (response != null && Constants.SUCCESS.equals(response.getCode())) {
            // 支付交易明确成功
            result.setTradeStatus(TradeStatus.SUCCESS);

        } else if (response != null && Constants.PAYING.equals(response.getCode())) {
            // 返回用户处理中，则轮询查询交易是否成功，如果查询超时，则调用撤销
            AlipayTradeQueryRequestBuilder queryBuiler = new AlipayTradeQueryRequestBuilder()
                    .setAppAuthToken(appAuthToken)
                    .setOutTradeNo(outTradeNo);
            AlipayTradeQueryResponse loopQueryResponse = loopQueryResult(queryBuiler);
            return checkQueryAndCancel(outTradeNo, appAuthToken, result, loopQueryResponse);

        } else if (tradeError(response)) {
            // 系统错误，则查询一次交易，如果交易没有支付成功，则调用撤销
            AlipayTradeQueryRequestBuilder queryBuiler = new AlipayTradeQueryRequestBuilder()
                    .setAppAuthToken(appAuthToken)
                    .setOutTradeNo(outTradeNo);
            AlipayTradeQueryResponse queryResponse = tradeQuery(queryBuiler);
            return checkQueryAndCancel(outTradeNo, appAuthToken, result, queryResponse);

        } else {
            // 其他情况表明该订单支付明确失败
            result.setTradeStatus(TradeStatus.FAILED);
        }

        return result;
    }
}