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
package top.spco;

import cn.hutool.core.util.ObjectUtil;
import lombok.SneakyThrows;
import top.spco.base.api.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.*;

/**
 * <p>
 * Created on 2023/10/28 0028 19:32
 * <p>
 *
 * @author SpCo
 * @version 1.0
 * @since 1.0
 */
public class DataBase {
    private static final Logger LOGGER = SpCoBot.logger;
    private Connection conn;
    private PreparedStatement pstmt;
    private ResultSet rs;

    public DataBase() {
        try {
            String dbFilePath = SpCoBot.dataFolder.getAbsolutePath() + File.separator + "spcobot.db";
            // 检查数据库文件是否存在
            if (!checkFileExists(dbFilePath)) {
                LOGGER.info("正在初始化数据库");
                try {
                    // 尝试创建数据库文件
                    File file = new File(dbFilePath);
                    if (!file.createNewFile()) {
                        throw new IOException("无法创建数据库文件");
                    }
                    LOGGER.info("已成功创建数据库文件");
                } catch (IOException e) {
                    throw new RuntimeException("创建数据库文件 (试图于位置" + dbFilePath + " ) 时发生错误: " + e.getMessage());
                }
            }

            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);

            createTables();
        } catch (Exception e) {
            throw new RuntimeException("数据库连接或表创建失败: " + e.getMessage());
        }
    }

    private boolean checkFileExists(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    public Connection getConn() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = openConn();
        }
        return conn;
    }

    @SneakyThrows
    public Connection openConn() {
        String dbFilePath = SpCoBot.dataFolder.getAbsolutePath() + File.separator + "spcobot.db";
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);
        return conn;
    }

    @SneakyThrows
    private void createTables() {
        if (conn == null || conn.isClosed()) {
            openConn();
        }
        String createUserTableSql =
                "CREATE TABLE IF NOT EXISTS user " +
                        "(id INTEGER NOT NULL PRIMARY KEY, " +
                        "smf_coin INTEGER DEFAULT 0, " +
                        "permission INTEGER DEFAULT 1, " +
                        "sign TEXT DEFAULT '从未签到过')";

        try (PreparedStatement stmt = conn.prepareStatement(createUserTableSql)) {
            stmt.execute();
        }
    }

    /**
     * 从数据库表中查询符合指定条件的数据，并返回结果中的某个字段的值。只有一个占位符！！！
     *
     * @param table       查询的数据表名称
     * @param columns     要查询的字段名称
     * @param whereClause 查询条件语句，占位符用"?"
     * @param whereValues 查询条件中占位符的值
     * @return 指定字段的值. 如果结果集中没有数据, 则返回null
     */
    @SneakyThrows
    public String select(String table, String columns, String whereClause, Object whereValues) {
        ResultSet rs = select(table, new String[]{columns}, whereClause + " = ?", new Object[]{whereValues});
        if (rs.next()) {
            return rs.getString(columns);
        } else {
            return null;
        }
    }

    /**
     * 从数据库表中查询符合指定条件的整数数据，并返回结果中的某个字段的值。只有一个占位符！！！
     *
     * @param table       查询的数据表名称
     * @param columns     要查询的字段名称
     * @param whereClause 查询条件语句，占位符用"?"
     * @param whereValues 查询条件中占位符的值
     * @return 指定字段的值. 如果结果集中没有数据, 则返回null
     */
    @SneakyThrows
    public Integer selectInt(String table, String columns, String whereClause, Object whereValues) {
        ResultSet rs = select(table, new String[]{columns}, whereClause + " = ?", new Object[]{whereValues});
        if (rs.next()) {
            return rs.getInt(columns);
        } else {
            return null;
        }
    }

    @SneakyThrows
    public ResultSet select(String table, String[] columns, String whereClause, Object[] whereValues) {
        if (conn == null || conn.isClosed()) {
            openConn();
        }
        StringBuilder sql = new StringBuilder("SELECT ");
        for (int i = 0; i < columns.length; i++) {
            sql.append(columns[i]);
            if (i != columns.length - 1) {
                sql.append(",");
            }
        }
        sql.append(" FROM ").append(table).append(" WHERE ").append(whereClause);
        try {
            pstmt = conn.prepareStatement(sql.toString());
            for (int i = 0; i < whereValues.length; i++) {
                pstmt.setObject(i + 1, whereValues[i]);
            }
            rs = pstmt.executeQuery();
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 更新数据
     *
     * @param sql    SQL语句
     * @param params 参数数组
     * @return 影响行数
     */
    @SneakyThrows
    public int update(String sql, Object... params) {
        if (conn == null || conn.isClosed()) {
            openConn();
        }
        PreparedStatement pstmt = conn.prepareStatement(sql);
        setParameters(pstmt, params);
        int result = pstmt.executeUpdate();
        closeStatement(pstmt);
        return result;
    }

    /**
     * 查询单个对象
     *
     * @param sql    SQL语句
     * @param clazz  实体类
     * @param params 参数数组
     * @param <T>    实体类类型
     * @return 实体类对象
     */
    @SneakyThrows
    public <T> T queryForObject(String sql, Class<T> clazz, Object... params) {
        if (conn == null || conn.isClosed()) {
            openConn();
        }
        T result = null;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        setParameters(pstmt, params);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            result = clazz.getDeclaredConstructor().newInstance();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = underlineToCamel(metaData.getColumnName(i));
                Object columnValue = rs.getObject(metaData.getColumnName(i));
                setProperty(result, columnName, columnValue);
            }
        }
        closeResultSet(rs);
        closeStatement(pstmt);
        return result;
    }

    /**
     * 设置PreparedStatement的参数
     *
     * @param pstmt  PreparedStatement
     * @param params 参数数组
     */
    @SneakyThrows
    private void setParameters(PreparedStatement pstmt, Object... params) {
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
        }
    }

    /**
     * 关闭结果集
     *
     * @param rs 结果集
     */
    @SneakyThrows
    private void closeResultSet(ResultSet rs) {
        if (rs != null) {
            rs.close();
        }
    }

    /**
     * 关闭PreparedStatement
     *
     * @param pstmt PreparedStatement
     */
    @SneakyThrows
    private void closeStatement(PreparedStatement pstmt) {
        if (pstmt != null) {
            pstmt.close();
        }
    }

    /**
     * 为实体类设置属性值
     *
     * @param obj          实体类对象
     * @param propertyName 属性名
     * @param value        属性值
     */
    @SneakyThrows
    private void setProperty(Object obj, String propertyName, Object value) {
        Field field = getFieldByName(obj.getClass(), propertyName);
        if (ObjectUtil.isNotEmpty(field)) {
            field.setAccessible(true);
            field.set(obj, value);
        }
    }

    /**
     * 获取实体类所有属性进行过滤
     *
     * @param clazz     clazz
     * @param fieldName propertyName
     */
    public Field getFieldByName(Class<?> clazz, String fieldName) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        return null;
    }

    /**
     * 将下划线命名的字符串转换为驼峰式命名的字符串
     *
     * @param str 下划线命名的字符串
     * @return 驼峰式命名的字符串
     */
    private String underlineToCamel(String str) {
        StringBuilder sb = new StringBuilder();
        boolean upperCase = false;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == '_') {
                upperCase = true;
            } else {
                if (upperCase) {
                    sb.append(Character.toUpperCase(ch));
                    upperCase = false;
                } else {
                    sb.append(ch);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 该方法用于检查指定的列是否在指定的 SQLite 表中存在。
     *
     * @param tableName  需要检查的表名
     * @param columnName 需要检查的列名
     * @return 如果列存在则返回 true，否则返回 false
     */
    @SneakyThrows
    public boolean columnExistsInTable(String tableName, String columnName) {
        if (conn == null || conn.isClosed()) {
            openConn();
        }
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(" + tableName + ");")) {

            while (rs.next()) {
                if (columnName.equals(rs.getString("name"))) {
                    return true;
                }
            }
            return false;
        }
    }

    @SneakyThrows
    public void insertData(String sql, Object... params) {
        if (conn == null || conn.isClosed()) {
            openConn();
        }

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // 设置参数
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            // 执行插入操作
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}