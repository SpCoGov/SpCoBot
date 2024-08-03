/*
 * Copyright 2024 SpCo
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

import com.google.gson.JsonObject;
import top.spco.SpCoBot;
import top.spco.api.Friend;
import top.spco.core.database.DataBase;
import top.spco.util.TimeUtils;

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
 * 用户可以检索和修改其属性，执行签到等操作，检查Premium会员资格，并将权限级别转换为 {@link UserPermission} 。
 *
 * <p>需要创建或获取用户，可以通过 {@link BotUsers} 类中的方法 {@link BotUsers#get(long)} 或 {@link BotUsers#getOrCreate(long)}
 *
 * @author SpCo
 * @version 3.0.4
 * @see BotUsers
 * @since 0.1.0
 */
public class BotUser {
    private final long id;
    private UserPermission permission;
    private int smfCoin;
    private int starCoin;
    private String sign;
    private int premium;

    BotUser(long id, UserPermission permission, int smfCoin, int starCoin, String sign, int premium) {
        this.id = id;
        this.permission = permission;
        this.smfCoin = smfCoin;
        this.starCoin = starCoin;
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
     * 设置用户权限。
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
     * 签到。
     *
     * @return 成功时返回签到获得的海绵山币数量, 已签到返回 {@code -1}
     * @throws UserOperationException 签到失败时抛出此异常
     */
    public int sign() throws UserOperationException {
        try {
            LocalDate today = TimeUtils.today();
            String signDate = SpCoBot.getInstance().getDataBase().selectString("user", "sign", "id", id);
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

    public void recharge(String tradeNo, int amount) throws UserOperationException {
        DataBase db = SpCoBot.getInstance().getDataBase();
        try {
            int baseAmount = db.selectInt("user", "star_coin", "id", id);
            this.starCoin = baseAmount + amount;
            db.getConn().setAutoCommit(false);
            // 更新用户StarCoin数量
            db.update("update user set star_coin=? where id=?", starCoin, id);
            // 为用户添加StarCoin变动记录
            db.insertData("insert into expenses(user,date,time,amount,balance,desc) values (?,?,?,?,?,?)",
                    id, TimeUtils.today(), System.currentTimeMillis(), amount, this.starCoin, tradeNo + " Recharge " + amount + " StarCoin");
            // 更新充值交易状态
            SpCoBot.getInstance().getDataBase().update("update trade set state=? where id=?", "paid", tradeNo);
            db.getConn().commit();
            Friend<?> friend = SpCoBot.getInstance().getBot().getFriend(getId());
            if (friend != null) {
                friend.sendMessage("订单" + tradeNo + "支付成功，已到账" + amount + "星币，账户余额: " + this.starCoin);
            }
        } catch (SQLException e) {
            try {
                db.getConn().rollback();
            } catch (SQLException rollbackException) {
                SpCoBot.LOGGER.error("Rollback failed.", rollbackException);
            }
            throw new UserOperationException("An error occurred while reading or saving data.", e);
        } finally {
            try {
                db.getConn().setAutoCommit(true);
            } catch (SQLException e) {
                SpCoBot.LOGGER.error(e);
            }
        }
    }

    public void recharge(JsonObject decrypted) throws UserOperationException {
        String tradeNo = decrypted.get("out_trade_no").getAsString();
        int totalAmount = (decrypted.get("amount").getAsJsonObject().get("total").getAsInt()) / 100;
        recharge(tradeNo, totalAmount);
    }

    /**
     * 解封此用户。
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
     * 封禁此用户。
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
     * 将此用户设置为管理员。
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
     * 将管理员用户设置为普通用户。
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
     * 检查用户是否被封禁。
     *
     * @return 已被封禁时返回 {@code true}
     */
    public boolean isBanned() {
        return permission.isBanned();
    }

    /**
     * 判断用户是否为Premium会员。
     *
     * @return 如果用户是Premium会员，返回 {@code true} ；否则返回 {@code false} 。
     */
    public boolean isPremium() {
        return premium == 1;
    }

    /**
     * 将用户权限转换为 {@link UserPermission} 。
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
        return "QQ: " + this.id + "\n海绵山币: " + smfCoin + "\n星币: " + starCoin + "\n会员信息: " + (isPremium() ? "Premium会员" : "普通会员") + "\n权限信息: " + permission;
    }
}