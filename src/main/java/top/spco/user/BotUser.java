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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.spco.SpCoBot;
import top.spco.api.Bot;
import top.spco.api.User;
import top.spco.util.DateUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 用户类，用于表示机器人用户。
 *
 * @author SpCo
 * @version 0.1.2
 * @since 0.1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BotUser {
    private long id;
    private int permission;
    private int smfCoin;
    private String sign;
    private int premium;

    /**
     * 签到
     *
     * @return 成功时返回签到获得的海绵山币数量, 已签到返回-1
     * @throws SQLException 数据库访问异常
     */
    public int sign() throws SQLException {
        LocalDate today = DateUtils.today();
        String signDate = SpCoBot.getInstance().getDataBase().select("user", "sign", "id", id);
        if (Objects.equals(signDate, today.toString())) {
            return -1;
        }
        SpCoBot.getInstance().getDataBase().update("update user set sign=? where id=?", today, id);
        int randomNumber = ThreadLocalRandom.current().nextInt(10, 101);
        this.smfCoin += randomNumber;
        SpCoBot.getInstance().getDataBase().update("update user set smf_coin=? where id=?", this.smfCoin, this.id);
        return randomNumber;
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
     */
    public UserPermission toUserPermission() {
        return UserPermission.byLevel(this.permission);
    }

    /**
     * 获取或创建指定用户的 {@link BotUser} 对象。
     *
     * @param id 用户的Id
     * @return 用户对象
     * @throws UserFetchException 获取用户信息失败时抛出的异常
     */
    public static BotUser getOrCreate(long id) throws UserFetchException {
        try {
            BotUser botUser;
            if (isUserExists(id)) {
                botUser = SpCoBot.getInstance().getDataBase().queryForObject("select * from user where id=?", BotUser.class, id);
                return botUser;
            }
            UserPermission userPermission = UserPermission.NORMAL;
            if (id == SpCoBot.getInstance().botId || id == SpCoBot.getInstance().botOwnerId) {
                userPermission = UserPermission.OWNER;
            }
            SpCoBot.getInstance().getDataBase().insertData("insert into user(id,permission) values (?,?)", id, userPermission.getLevel());
            botUser = SpCoBot.getInstance().getDataBase().queryForObject("select * from user where id=?", BotUser.class, id);
            return botUser;
        } catch (Exception e) {
            e.printStackTrace();
            throw new UserFetchException(e.getMessage(), e);
        }
    }

    /**
     * 检查用户是否已注册
     *
     * @param id 要检查的QQ号。
     * @return 如果记录存在，则返回 true；否则返回 false。
     */
    public static boolean isUserExists(long id) {
        String query = "SELECT COUNT(*) FROM user WHERE id = ?";
        try (PreparedStatement pstmt = SpCoBot.getInstance().getDataBase().getConn().prepareStatement(query)) {
            pstmt.setLong(1, id); // 设置查询参数 ID
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取或创建指定用户的 {@link User} 对象。
     *
     * @param bot 机器人实例
     * @param id  用户的Id
     * @return 用户对象
     * @throws UserFetchException 获取用户信息失败时抛出的异常
     * @deprecated 请使用 {@link #getOrCreate(long)} 方法替代
     */
    @Deprecated(since = "1.2", forRemoval = true)
    public static User getUser(Bot bot, long id) throws UserFetchException {
        if (bot == null) {
            throw new UserFetchException("BotSettings instance is null while trying to fetch user.");
        } else {
            User user = bot.getUser(id);
            if (user == null) {
                throw new UserFetchException("Failed to obtain the User object with id " + id + ".");
            } else {
                return user;
            }
        }
    }

    @Override
    public String toString() {
        return  "QQ: " + this.id + "\n海绵山币: " + smfCoin + "\n会员信息: " + (isPremium() ? "Premium会员" : "普通会员") + "\n权限信息: " + toUserPermission();
    }
}