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
package top.spco.user;

import top.spco.SpCoBot;
import top.spco.util.DateUtils;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 表示具有特定属性和功能的机器人用户。该类提供了管理机器人系统中用户属性和操作的方法。
 *
 * <p>该类的属性包括：
 * <ul>
 * <li>ID：用户的唯一标识符。
 * <li>Permission：用户的权限级别。
 * <li>SMFCoin：用户拥有的海绵山币。
 * <li>Sign：用户最后一次签到的日期。
 * <li>Premium：Premium会员状态。
 * </ul>
 *
 * <p>可以创建具有指定属性或未指定属性的此类实例。
 * 用户可以检索和修改其属性，执行签到等操作，检查Premium会员资格，并将权限级别转换为{@link UserPermission}。
 *
 * @author SpCo
 * @version 1.2.3
 * @since 0.1.0
 */
public class BotUser {
    private final long id;
    private UserPermission permission;
    private int smfCoin;
    private String sign;
    private int premium;

    public BotUser(long id, UserPermission permission, int smfCoin, String sign, int premium) {
        this.id = id;
        this.permission = permission;
        this.smfCoin = smfCoin;
        this.sign = sign;
        this.premium = premium;
    }

    public long getId() {
        return id;
    }

    public UserPermission getPermission() {
        return permission;
    }

    /**
     * 设置用户权限
     *
     * @throws UserOperationException 解封用户失败时抛出此异常
     */
    public void setPermission(UserPermission permission) throws UserOperationException {
        this.permission = permission;
        try {
            SpCoBot.getInstance().getDataBase().update("update user set permission=? where id=?", permission.getLevel(), id);
        } catch (SQLException e) {
            throw new UserOperationException("An error occurred while saving data.", e);
        }
    }

    public int getSMFCoin() {
        return smfCoin;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSign() {
        return sign;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BotUser user = (BotUser) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * 签到
     *
     * @return 成功时返回签到获得的海绵山币数量, 已签到返回-1
     * @throws UserOperationException 签到失败时抛出此异常
     */
    public int sign() throws UserOperationException {
        try {
            LocalDate today = DateUtils.today();
            String signDate = SpCoBot.getInstance().getDataBase().select("user", "sign", "id", id);
            if (signDate.equals(today.toString())) {
                return -1;
            }
            SpCoBot.getInstance().getDataBase().update("update user set sign=? where id=?", today, id);
            int randomNumber = ThreadLocalRandom.current().nextInt(10, 101);
            this.smfCoin += randomNumber;
            SpCoBot.getInstance().getDataBase().update("update user set smf_coin=? where id=?", this.smfCoin, this.id);
            return randomNumber;
        } catch (SQLException e) {
            throw new UserOperationException("An error occurred while reading or saving data.", e);
        }
    }

    /**
     * 解封此用户
     *
     * @throws UserOperationException 解封用户失败时抛出此异常
     */
    public void pardon() throws UserOperationException {
        if (!isBanned()) {
            throw new UserOperationException("User(" + id + ") is not banned.");
        } else {
            setPermission(UserPermission.NORMAL);
        }
    }

    /**
     * 封禁此用户
     *
     * @throws UserOperationException 封禁用户失败时抛出此异常
     */
    public void ban() throws UserOperationException {
        if (isBanned()) {
            throw new UserOperationException("User(" + id + ") has been banned.");
        } else {
            setPermission(UserPermission.BANNED);
        }
    }

    /**
     * 将此用户设置为管理员
     *
     * @throws UserOperationException 提权失败时抛出此异常
     */
    public void promote() throws UserOperationException {
        if (isBanned()) {
            throw new UserOperationException("User(" + id + ") has been banned. Please lift the ban before proceeding with any operations.");
        } else if (permission.isOperator()) {
            throw new UserOperationException("User(" + id + ") already has operator privileges.");
        } else {
            setPermission(UserPermission.ADMINISTRATOR);
        }
    }

    /**
     * 将管理员用户设置为普通用户
     *
     * @throws UserOperationException 限权失败时抛出此异常
     */
    public void demote() throws UserOperationException {
        if (isBanned()) {
            throw new UserOperationException("User(" + id + ") has been banned. Please lift the ban before proceeding with any operations.");
        } else if (!permission.isOperator()) {
            throw new UserOperationException("User(" + id + ") does not have operator privileges.");
        } else {
            setPermission(UserPermission.NORMAL);
        }
    }

    /**
     * 检查用户是否被封禁
     *
     * @return 已被封禁时返回 {@code true}
     */
    public boolean isBanned() {
        return permission.isBanned();
    }

    /**
     * 判断用户是否为Premium会员。
     *
     * @return 如果用户是Premium会员，返回 true；否则返回 false。
     */
    public boolean isPremium() {
        return premium == 1;
    }

    /**
     * 将用户权限转换为 {@link UserPermission}。
     *
     * @return 对应的用户权限
     * @deprecated 请使用 {@link #getPermission()} 替代
     */
    @Deprecated
    public UserPermission toUserPermission() {
        return getPermission();
    }

    @Override
    public String toString() {
        return "QQ: " + this.id + "\n海绵山币: " + smfCoin + "\n会员信息: " + (isPremium() ? "Premium会员" : "普通会员") + "\n权限信息: " + permission;
    }
}