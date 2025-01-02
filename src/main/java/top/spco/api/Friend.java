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

/**
 * 代表一位好友
 *
 * @author SpCo
 * @version 2.0.0
 * @since 0.1.0
 */
public abstract class Friend<T> extends User<T> {
    public Friend(T objcet) {
        super(objcet);
    }

    /**
     * 该好友所在的好友分组
     */
    public abstract FriendGroup<?> getFriendGroup();

    /**
     * 删除并屏蔽该好友, 屏蔽后对方将无法发送临时会话消息
     */
    public abstract void delete();
}