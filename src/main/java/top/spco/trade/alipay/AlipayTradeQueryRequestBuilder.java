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

import com.google.gson.annotations.SerializedName;

public class AlipayTradeQueryRequestBuilder extends RequestBuilder {

    private BizContent bizContent = new BizContent();

    @Override
    public BizContent getBizContent() {
        return bizContent;
    }

    @Override
    public boolean validate() {
        if (isEmpty(bizContent.tradeNo) &&
                isEmpty(bizContent.outTradeNo)) {
            throw new IllegalStateException("tradeNo and outTradeNo can not both be NULL!");
        }
        return true;
    }

    private static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AlipayTradeQueryRequestBuilder{");
        sb.append("bizContent=").append(bizContent);
        sb.append(", super=").append(super.toString());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public AlipayTradeQueryRequestBuilder setAppAuthToken(String appAuthToken) {
        return (AlipayTradeQueryRequestBuilder) super.setAppAuthToken(appAuthToken);
    }

    @Override
    public AlipayTradeQueryRequestBuilder setNotifyUrl(String notifyUrl) {
        return (AlipayTradeQueryRequestBuilder) super.setNotifyUrl(notifyUrl);
    }

    public String getTradeNo() {
        return bizContent.tradeNo;
    }

    public AlipayTradeQueryRequestBuilder setTradeNo(String tradeNo) {
        bizContent.tradeNo = tradeNo;
        return this;
    }

    public String getOutTradeNo() {
        return bizContent.outTradeNo;
    }

    public AlipayTradeQueryRequestBuilder setOutTradeNo(String outTradeNo) {
        bizContent.outTradeNo = outTradeNo;
        return this;
    }

    public static class BizContent {
        // 支付宝交易号,和商户订单号不能同时为空, 如果同时存在则通过tradeNo查询支付宝交易
        @SerializedName("trade_no")
        private String tradeNo;

        // 商户订单号，通过此商户订单号查询当面付的交易状态
        @SerializedName("out_trade_no")
        private String outTradeNo;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("BizContent{");
            sb.append("tradeNo='").append(tradeNo).append('\'');
            sb.append(", outTradeNo='").append(outTradeNo).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}