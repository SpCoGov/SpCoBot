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
 * 代表一位普通的群成员
 *
 * @author SpCo
 * @version 1.0
 * @see Member
 * @see AnonymousMember
 * @since 1.0
 */
public interface NormalMember extends Member {
    /**
     * 群名片. 可能为空
     */
    String getNameCard();

    /**
     * 群特殊头衔.
     */
    String getSpecialTitle();

    /**
     * 给予或移除群成员的管理员权限<p>
     * 此操作需要 BotSettings 为{@link MemberPermission#OWNER 群主}
     *
     * @param operation true 为给予
     */
    void modifyPermission(boolean operation);

    /**
     * 被禁言剩余时长. 单位为秒
     */
    int muteTimeRemaining();

    /**
     * 当该群员处于禁言状态时返回 {@code true}.
     */
    boolean isMuted();

    /**
     * 解除禁言<p>
     * 管理员可解除成员的禁言, 群主可解除管理员和群员的禁言
     */
    void unmute();

    /**
     * 踢出该成员<p>
     * 管理员可踢出成员, 群主可踢出管理员和群员
     *
     * @param block 为 {@code true} 时拉黑成员
     */
    void kick(String message, boolean block);


}