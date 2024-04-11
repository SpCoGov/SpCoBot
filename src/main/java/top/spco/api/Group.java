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
 * 代表一个群
 *
 * @author SpCo
 * @version 3.1.0
 * @since 0.1.0
 */
public abstract class Group<T> extends Interactive<T> {
    protected Group(T objcet) {
        super(objcet);
    }

    /**
     * 获取该群群名称
     *
     * @return 群名称
     */
    public abstract String getName();

    /**
     * 获取该群群主
     *
     * @return 群主对象
     */
    public abstract NormalMember<?> getOwner();

    /**
     * 让机器人退出这个群
     *
     * @return 退出成功时返回 {@code true}; 已经退出时返回 {@code false}
     */
    public abstract boolean quit();

    public abstract MemberPermission botPermission();

    /**
     * 获取机器人在群中的成员对象
     *
     * @return 成员对象
     */
    public abstract NormalMember<?> botAsMember();

    /**
     * 查询群成员对象
     *
     * @param id 成员Id
     * @return 查询结果. 不存在时返回 {@code null}
     */
    public abstract NormalMember<?> getMember(long id);

    /**
     * 获取该群的所有群成员
     *
     * @return 查询结果
     */
    public abstract InteractiveList<NormalMember<?>> getMembers();
}