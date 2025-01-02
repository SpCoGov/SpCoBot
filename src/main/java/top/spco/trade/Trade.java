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
package top.spco.trade;


public abstract class Trade {
    private final String tradeNo;
    private final long callerUser;
    private final String qrCode;
    private final int amount;
    private final PaymentMethod paymentMethod;

    public Trade(String id, long callerUser, String qrCode, int amount, PaymentMethod paymentMethod) {
        this.tradeNo = id;
        this.callerUser = callerUser;
        this.qrCode = qrCode;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public long getCallerUser() {
        return callerUser;
    }

    public String getQrCode() {
        return qrCode;
    }

    public int getAmount() {
        return amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
}