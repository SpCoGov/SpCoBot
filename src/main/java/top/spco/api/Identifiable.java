/*
 * Copyright 2023 SpCo
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

/**
 * 表示具有标识号的对象的接口
 *
 * @author SpCo
 * @version 2.0.0
 * @since 0.1.0
 */
public abstract class Identifiable<T> extends Wrapper<T> {
    protected Identifiable(T object) {
        super(object);
    }

    public abstract long getId();
}