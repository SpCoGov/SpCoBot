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
package top.spco.core.config;

public enum PayApiSettings implements SettingsGroup {
    WECHAT_PAY_MERCHANT_ID("wechat_pay_merchant_id", ""),
    WECHAT_PAY_PRIVATE_KEY_PATH("wechat_pay_private_key_path", ""),
    WECHAT_PAY_MERCHANT_SERIAL_NUMBER("wechat_pay_merchant_serial_number", ""),
    WECHAT_PAY_API_V3_KEY("wechat_pay_api_v3_key", ""),
    WECHAT_PAY_APP_ID("wechat_pay_app_id", ""),
    ALIPAY_APP_ID("alipay_app_id", ""),
    ALIPAY_PRIVATE_KEY("alipay_private_key", ""),
    ALIPAY_ALIPAY_PUBLIC_KEY("alipay_alipay_public_key", ""),
    NOTIFY_SERVER_PORT("notify_server_port", 6698),
    NOTIFY_URL("notify_url", "https://notify.url/");

    private final String key;
    private final Object defaultValue;

    PayApiSettings(String key, Object defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    @Override
    public String groupName() {
        return "PayApi";
    }

    @Override
    public Object defaultValue() {
        return defaultValue;
    }

    @Override
    public String toString() {
        return key;
    }
}