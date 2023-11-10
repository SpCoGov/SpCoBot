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
import top.spco.base.api.Bot;
import top.spco.base.api.User;
import top.spco.util.DateUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>
 * Created on 2023/10/28 0028 18:47
 * <p>
 *
 * @author SpCo
 * @version 1.2
 * @since 1.0
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

    public boolean isPremium() {
        return premium == 1;
    }

    public static BotUser getOrCreate(long id) throws UserFetchException {
        try {
            BotUser botUser;
            if (isUserExists(id)) {
                botUser = SpCoBot.getInstance().getDataBase().queryForObject("select * from user where id=?", BotUser.class, id);
                return botUser;
            }
            UserPermission userPermission = UserPermission.NORMAL;
            if (id == SpCoBot.getInstance().BOT_ID || id == SpCoBot.getInstance().BOT_OWNER_ID) {
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

    private static User getUser(Bot bot, long id) throws UserFetchException {
        if (bot == null) {
            throw new UserFetchException("Bot instance is null while trying to fetch user.");
        } else {
            User user = bot.getUser(id);
            if (user == null) {
                throw new UserFetchException("Failed to obtain the User object with id " + id + ".");
            } else {
                return user;
            }
        }
    }
}