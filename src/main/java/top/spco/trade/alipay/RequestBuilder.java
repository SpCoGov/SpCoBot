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

import com.google.gson.Gson;

/**
 * Created on 2024/5/31 下午3:19
 *
 * @author SpCo
 * @version 1.0
 * @since 1.0
 */
public abstract class RequestBuilder {
    private String appAuthToken;
    private String notifyUrl;

    public abstract boolean validate();

    public abstract Object getBizContent();

    public String toJsonString() {
        return new Gson().toJson(this.getBizContent());
    }

    @Override
    public String toString() {
        return "RequestBuilder{" + "appAuthToken='" + appAuthToken + '\'' +
                ", notifyUrl='" + notifyUrl + '\'' +
                '}';
    }

    public String getAppAuthToken() {
        return appAuthToken;
    }

    public RequestBuilder setAppAuthToken(String appAuthToken) {
        this.appAuthToken = appAuthToken;
        return this;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public RequestBuilder setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
        return this;
    }
}