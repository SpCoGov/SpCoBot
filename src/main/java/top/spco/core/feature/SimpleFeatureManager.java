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

import top.spco.service.RegistrationException;

import java.util.Map;

/**
 * {@code SimpleFeatureManager} 和 {@link FeatureManager} 一样，是一种功能管理器，用于管理特定类型的功能。
 * 它使用 {@link DummyFeature} 作为占位符，以标识已注册的功能。
 *
 * @param <F> 功能的类型
 * @author SpCo
 * @version 4.0.0
 * @see FeatureManager
 * @see DummyFeature
 * @since 4.0.0
 */
public abstract class SimpleFeatureManager<F extends Feature> extends FeatureManager<F, DummyFeature> {
    protected static final DummyFeature PRESENT = DummyFeature.INSTANCE;

    @Deprecated
    @Override
    public void register(F feature, DummyFeature object) throws RegistrationException {
        register(feature);
    }

    public void register(F feature) throws RegistrationException {
        if (getAllRegistered().containsKey(feature)) {
            throw new RegistrationException(feature + " is registered in the " + this.getClass().getName() + ".");
        }
        getAllRegistered().put(feature, PRESENT);
    }

    @Deprecated
    @Override
    public DummyFeature get(F key) {
        return PRESENT;
    }

    @Override
    public Map<F, DummyFeature> getAllRegistered() {
        return super.getAllRegistered();
    }
}
