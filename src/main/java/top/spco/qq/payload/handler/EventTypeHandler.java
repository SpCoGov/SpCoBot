/*
 * Copyright 2026 SpCo
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
package top.spco.qq.payload.handler;

import java.util.HashMap;

public abstract class EventTypeHandler {
    private final HashMap<String, PostPayloadHandler> subtypes = new HashMap<>();

    public abstract String getSubtypeKeyName();

    public void register(String subtype, PostPayloadHandler handler) {
        subtypes.put(subtype, handler);
    }

    public PostPayloadHandler getPayloadHandler(String subtype) {
        return subtypes.get(subtype);
    }

    public boolean hasHandler(String subtype) {
        return subtypes.containsKey(subtype);
    }
}