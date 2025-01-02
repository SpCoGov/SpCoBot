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

public final class DummyFeatureManager extends SimpleFeatureManager<DummyFeature> {
    public static final DummyFeatureManager INSTANCE = new DummyFeatureManager();

    private DummyFeatureManager() {
    }

    @Override
    public boolean isFeatureAvailable(Interactive<?> where, DummyFeature key, DummyFeature feature) {
        return false;
    }

    @Override
    public String getFeatureType() {
        return "dummy";
    }
}
