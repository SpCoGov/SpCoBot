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
package top.spco.core.feature;


import top.spco.api.Interactive;
import top.spco.core.Manager;

/**
 * 表示某个{@link Feature 功能}的管理器。
 *
 * @param <F> 功能的类型
 * @author SpCo
 * @version 4.0.0
 * @since 4.0.0
 */
public abstract class FeatureManager<K, F extends Feature> extends Manager<K, F> {
    public abstract boolean isFeatureAvailable(Interactive<?> where, K key, F feature) throws Exception;

    public abstract String getFeatureType();
}
