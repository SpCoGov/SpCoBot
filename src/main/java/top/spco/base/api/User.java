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
package top.spco.base.api;

/**
 * 代表一位用户<p>
 *
 * @author SpCo
 * @version 1.0
 * @since 1.0
 */
public interface User extends Interactive {
    /**
     * 备注信息<p>
     * 仅与 {@link User} 存在好友关系的时候才可能存在备注<p>
     * 与 {@link User} 没有好友关系时永远为空{@link String 字符串} ("")
     */
    String getRemark();

    void nudge();

    String getNick();
}