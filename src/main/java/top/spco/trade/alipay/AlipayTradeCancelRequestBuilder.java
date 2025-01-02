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

import com.google.gson.annotations.SerializedName;

public class AlipayTradeCancelRequestBuilder extends RequestBuilder {

    private BizContent bizContent = new BizContent();

    @Override
    public BizContent getBizContent() {
        return bizContent;
    }

    @Override
    public boolean validate() {
        if (isEmpty(bizContent.outTradeNo)) {
            throw new NullPointerException("out_trade_no should not be NULL!");
        }
        return true;
    }

    private static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AlipayTradeCancelRequestBuilder{");
        sb.append("bizContent=").append(bizContent);
        sb.append(", super=").append(super.toString());
        sb.append('}');
        return sb.toString();
    }

    @Override
    public AlipayTradeCancelRequestBuilder setAppAuthToken(String appAuthToken) {
        return (AlipayTradeCancelRequestBuilder) super.setAppAuthToken(appAuthToken);
    }

    @Override
    public AlipayTradeCancelRequestBuilder setNotifyUrl(String notifyUrl) {
        return (AlipayTradeCancelRequestBuilder) super.setNotifyUrl(notifyUrl);
    }

    public String getOutTradeNo() {
        return bizContent.outTradeNo;
    }

    public AlipayTradeCancelRequestBuilder setOutTradeNo(String outTradeNo) {
        bizContent.outTradeNo = outTradeNo;
        return this;
    }

    public static class BizContent {
        // 商户订单号，通过此商户订单号撤销当面付的交易状态
        @SerializedName("out_trade_no")
        private String outTradeNo;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("BizContent{");
            sb.append("outTradeNo='").append(outTradeNo).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}