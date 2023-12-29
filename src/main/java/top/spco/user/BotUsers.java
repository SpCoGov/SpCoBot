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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 该类提供与机器人用户相关的功能，包括检索或创建 BotUser 对象以及检查用户是否存在的方法。
 *
 * <p>该类中的方法操作 {@link BotUser} 类，并在机器人的上下文中处理与用户相关的操作。
 *
 * @author SpCo
 * @version 1.2.3
 * @see BotUser
 * @since 1.2.3
 */
public class BotUsers {
    /**
     * 获取或创建指定用户的 {@link BotUser} 对象。
     *
     * @param id 用户的Id
     * @return 用户对象
     * @throws UserFetchException 获取用户信息失败时抛出的异常
     */
    public static BotUser getOrCreate(long id) throws UserFetchException {
        BotUser botUser = get(id);
        if (botUser == null) {
            UserPermission defaultPermission = UserPermission.NORMAL;
            if (id == SpCoBot.getInstance().botId || id == SpCoBot.getInstance().botOwnerId) {
                defaultPermission = UserPermission.OWNER;
            }
            try {
                SpCoBot.getInstance().getDataBase().insertData("insert into user(id,permission) values (?,?)", id, defaultPermission.getLevel());
                return get(id);
            } catch (SQLException e) {
                throw new UserFetchException("Exception occurred while creating user: " + e.getMessage(), e);
            }
        }
        return botUser;
    }

    /**
     * 获取或创建指定用户的 {@link BotUser} 对象。
     *
     * @param id 用户的Id
     * @return 用户对象
     * @throws UserFetchException 获取用户信息失败时抛出的异常
     */
    public static BotUser get(long id) throws UserFetchException {
        String sql = "SELECT * FROM user WHERE id = ?";
        try (PreparedStatement preparedStatement = SpCoBot.getInstance().getDataBase().getConn().prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    return null;
                }
                int smfCoin = rs.getInt(2);
                UserPermission permission = UserPermission.byLevel(rs.getInt(3));
                String signDate = rs.getString(4);
                int premium = rs.getInt(5);
                return new BotUser(id, permission, smfCoin, signDate, premium);
            }
        } catch (SQLException e) {
            throw new UserFetchException("An exception occurred while reading data from the database: " + e.getMessage(), e);
        }
    }

    /**
     * 检查用户是否已注册
     *
     * @param id 要检查的QQ号。
     * @return 如果记录存在，则返回 {@code true} ；否则返回 {@code false}。
     * @throws SQLException 查询数据库发生异常时抛出
     */
    public static boolean isUserExists(long id) throws SQLException {
        String query = "SELECT COUNT(*) FROM user WHERE id = ?";
        try (PreparedStatement pstmt = SpCoBot.getInstance().getDataBase().getConn().prepareStatement(query)) {
            pstmt.setLong(1, id); // 设置查询参数 ID
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        }
        return false;
    }
}