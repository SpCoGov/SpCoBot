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

import lombok.SneakyThrows;
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
public class BotUser {
    private final long id;
    private UserPermission permission;
    private int smfCoin;

    public BotUser(long id, UserPermission permission, int smfCoin) throws UserFetchException {
        this.id = id;
        this.permission = permission;
        this.smfCoin = smfCoin;
    }

    public UserPermission getPermission() throws SQLException {
        this.permission = UserPermission.byLevel(SpCoBot.getInstance().getDataBase().selectInt("user", "permission", "id", this.id));
        return this.permission;
    }

    public long getId() {
        return id;
    }

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
        setSmfCoin(getSmfCoin() + randomNumber);
        return randomNumber;
    }


    public void setSmfCoin(int smfCoin) throws SQLException {
        this.smfCoin = smfCoin;
        SpCoBot.getInstance().getDataBase().update("update user set smf_coin=? where id=?", this.smfCoin, this.id);
    }

    public int getSmfCoin() throws SQLException {
        this.smfCoin = SpCoBot.getInstance().getDataBase().selectInt("user", "smf_coin", "id", this.id);
        return this.smfCoin;
    }

    public static BotUser getOrCreate(long id) throws UserFetchException, SQLException {
        BotUser botUser;
        if (isUserExists(id)) {
            int permission = SpCoBot.getInstance().getDataBase().selectInt("user", "permission", "id", id);
            int smfCoin = SpCoBot.getInstance().getDataBase().selectInt("user", "smf_coin", "id", id);
            botUser = new BotUser(id, UserPermission.byLevel(permission), smfCoin);
            return botUser;
        }
        UserPermission userPermission = UserPermission.NORMAL;
        if (id == SpCoBot.getInstance().BOT_ID || id == SpCoBot.getInstance().BOT_OWNER_ID) {
            userPermission = UserPermission.OWNER;
        }
        SpCoBot.getInstance().getDataBase().insertData("insert into user(id,permission) values (?,?)", id, userPermission.getLevel());
        int smfCoin = SpCoBot.getInstance().getDataBase().selectInt("user", "smf_coin", "id", id);
        botUser = new BotUser(id, userPermission, smfCoin);
        return botUser;
    }

    /**
     * 检查用户是否已注册
     *
     * @param id 要检查的QQ号。
     * @return 如果记录存在，则返回 true；否则返回 false。
     */
    @SneakyThrows
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