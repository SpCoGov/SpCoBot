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
package top.spco.api;

import top.spco.core.Platform;

/**
 * 表示具有标识号的对象
 *
 * @author SpCo
 * @version 2.0.0
 * @since 0.1.0
 */
public abstract class Identifiable {

    protected Identifiable() {
    }

    public abstract String getId();

    public abstract String getName();

    /**
     * 返回当前对象所属的平台。
     * <p>
     * 该方法用于运行时的平台一致性校验；无法识别的平台返回 {@code null}。
     *
     * @return 当前对象所属平台，无法识别时返回 {@code null}
     */
    public Platform getPlatform() {
        String className = getClass().getName();
        if (className.startsWith("top.spco.telegram.")) {
            return Platform.TELEGRAM;
        }
        if (className.startsWith("top.spco.mirai.") || className.startsWith("top.spco.qq.")) {
            return Platform.QQ;
        }
        return null;
    }
}
