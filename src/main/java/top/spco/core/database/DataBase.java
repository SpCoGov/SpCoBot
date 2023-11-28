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
package top.spco.core.database;

import cn.hutool.core.util.ObjectUtil;
import lombok.SneakyThrows;
import top.spco.SpCoBot;
import top.spco.api.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库
 *
 * @author SpCo
 * @version 0.1.2
 * @since 0.1.0
 */
public class DataBase {
    private static final Logger LOGGER = SpCoBot.logger;
    private Connection conn;

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
                    throw new RuntimeException("创建数据库文件 (试图于" + dbFilePath + " ) 时发生错误: " + e.getMessage());
                }
            }

            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);

            createTables();
            if (!columnExistsInTable("user", "premium")) {
                addColumn("user", "premium", "INTEGER DEFAULT 0");
            }
        } catch (Exception e) {
            throw new RuntimeException("无法连接至数据库或数据库初始化失败: " + e.getMessage());
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

    public Connection openConn() throws SQLException {
        String dbFilePath = SpCoBot.dataFolder.getAbsolutePath() + File.separator + "spcobot.db";
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);
        return conn;
    }

    private void createTables() throws SQLException {
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
     * @throws SQLException 如果在执行数据库查询时发生错误
     */
    public String select(String table, String columns, String whereClause, Object whereValues) throws SQLException {
        ResultSet rs = select(table, new String[]{columns}, whereClause + " = ?", new Object[]{whereValues});
        if (rs.next()) {
            String s = rs.getString(columns);
            rs.close();
            return s;
        } else {
            rs.close();
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
    public Integer selectInt(String table, String columns, String whereClause, Object whereValues) throws SQLException {
        ResultSet rs = select(table, new String[]{columns}, whereClause + " = ?", new Object[]{whereValues});
        if (rs.next()) {
            int i = rs.getInt(columns);
            rs.close();
            return i;
        } else {
            rs.close();
            return null;
        }
    }

    public ResultSet select(String table, String[] columns, String whereClause, Object[] whereValues) throws SQLException {
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
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            for (int i = 0; i < whereValues.length; i++) {
                pstmt.setObject(i + 1, whereValues[i]);
            }
            return pstmt.executeQuery();
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
    public int update(String sql, Object... params) throws SQLException {
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
     * @param sql    SQL语句 如："select * from product where shop_id=? and data=? and price=?"
     * @param clazz  实体类
     * @param params 参数数组
     * @param <T>    实体类类型
     * @return 实体类对象
     */
    public <T> T queryForObject(String sql, Class<T> clazz, Object... params) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (conn == null || conn.isClosed()) {
            openConn();
        }

        // 初始化结果对象
        T result = null;

        PreparedStatement pstmt = conn.prepareStatement(sql);
        setParameters(pstmt, params);

        // 执行查询，获取结果集
        ResultSet rs = pstmt.executeQuery();

        // 如果结果集中有数据，进行处理
        if (rs.next()) {
            // 使用反射创建实体类对象
            result = clazz.getDeclaredConstructor().newInstance();

            // 获取结果集的元数据
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // 遍历结果集的每一列
            for (int i = 1; i <= columnCount; i++) {
                // 将数据库列名转换为驼峰命名规则
                String columnName = underlineToCamel(metaData.getColumnName(i));
                // 获取数据库列的值
                Object columnValue = rs.getObject(metaData.getColumnName(i));
                // 使用反射设置实体类的属性值
                setProperty(result, columnName, columnValue);
            }
        }
        closeResultSet(rs);
        closeStatement(pstmt);
        return result;
    }

    /**
     * 查询多个对象
     *
     * @param sql    SQL语句 如："select * from product where shop_id=? and data=?"
     * @param clazz  实体类
     * @param params 参数数组
     * @param <T>    实体类类型
     * @return 实体类对象列表
     */
    public <T> List<T> queryForList(String sql, Class<T> clazz, Object... params) throws SQLException, InstantiationException, IllegalAccessException {
        if (conn == null || conn.isClosed()) {
            openConn();
        }
        List<T> resultList = new ArrayList<>();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        setParameters(pstmt, params);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            T result = clazz.newInstance();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = underlineToCamel(metaData.getColumnName(i));
                Object columnValue = rs.getObject(metaData.getColumnName(i));
                setProperty(result, columnName, columnValue);
            }
            resultList.add(result);
        }
        closeResultSet(rs);
        closeStatement(pstmt);
        return resultList;
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
    private void closeResultSet(ResultSet rs) throws SQLException {
        if (rs != null) {
            rs.close();
        }
    }

    /**
     * 关闭PreparedStatement
     *
     * @param pstmt PreparedStatement
     */
    private void closeStatement(PreparedStatement pstmt) throws SQLException {
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
    private void setProperty(Object obj, String propertyName, Object value) throws IllegalAccessException {
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
    public boolean columnExistsInTable(String tableName, String columnName) throws SQLException {
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

    /**
     * 获取指定字段的所有记录。
     *
     * @param fieldName 要获取的字段名称
     * @param tableName 数据表名称
     * @param condition 查询条件，可以为 SQL WHERE 子句的一部分
     * @return 包含字段值的列表
     */
    public List<String> getFieldValues(String fieldName, String tableName, String condition) throws SQLException {
        if (conn == null || conn.isClosed()) {
            openConn();
        }
        List<String> fieldValues = new ArrayList<>();
        // 创建查询语句
        String sql = "SELECT " + fieldValues + " FROM " + tableName + " WHERE " + condition;
        // 执行查询
        Statement statement = this.conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        // 处理结果
        while (resultSet.next()) {
            String fieldValue = resultSet.getString(fieldName);
            fieldValues.add(fieldValue);
        }
        resultSet.close();
        statement.close();

        return fieldValues;
    }

    public void insertData(String sql, Object... params) throws SQLException {
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

    /**
     * 为数据库添加一个新的字段
     *
     * @param tableName  要添加的表名
     * @param columnName 要添加的字段名
     * @param dataType   要添加的字段的数据类型
     */
    public void addColumn(String tableName, String columnName, String dataType) throws SQLException {
        Statement statement = getConn().createStatement();

        String sql = "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + dataType;

        statement.executeUpdate(sql);
    }
}