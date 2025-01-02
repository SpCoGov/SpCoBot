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

import java.util.function.Supplier;

/**
 * {@code DummyFeature} 是一个占位符。
 * 它的 {@code isAvailable} 方法总是返回 {@code false}，并且其 {@code manager} 方法返回 {@code null}。
 * 这个类是单例模式的实现，唯一实例通过 {@link #INSTANCE} 静态字段获取。
 *
 * @author SpCo
 * @version 4.0.0
 * @see SimpleFeatureManager
 * @since 4.0.0
 */
public final class DummyFeature extends Feature {
    public static final DummyFeature INSTANCE = new DummyFeature();

    private DummyFeature() {
    }

    @Override
    public boolean isAvailable(Interactive<?> where) {
        return false;
    }

    @Override
    public Supplier<FeatureManager<?, ? extends Feature>> manager() {
        return () -> DummyFeatureManager.INSTANCE;
    }

    @Override
    public String getFeatureName() {
        return "dummy";
    }
}
