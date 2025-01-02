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

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayRequest;
import com.alipay.api.AlipayResponse;
import top.spco.SpCoBot;

abstract class AbsAlipayService {
    // 验证bizContent对象
    protected void validateBuilder(RequestBuilder builder) {
        if (builder == null) {
            throw new NullPointerException("builder should not be NULL!");
        }

        if (!builder.validate()) {
            throw new IllegalStateException("builder validate failed! " + builder);
        }
    }

    // 调用AlipayClient的execute方法，进行远程调用
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected AlipayResponse getResponse(AlipayClient client, AlipayRequest request) {
        try {
            return client.execute(request);

        } catch (AlipayApiException e) {
            SpCoBot.LOGGER.error(e);
            return null;
        }
    }
}