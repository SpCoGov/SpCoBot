/*
 * Copyright 2025 SpCo
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

import top.spco.SpCoBot;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库
 *
 * @author SpCo
 * @version 4.0.0
 * @since 0.1.0
 */
public class DataBase {
    private Connection conn;

    public DataBase() {
        try {
            String dbFilePath = SpCoBot.dataFolder.getAbsolutePath() + File.separator + "spcobot.db";
            // 检查数据库文件是否存在
            if (!checkFileExists(dbFilePath)) {
                SpCoBot.LOGGER.info("正在初始化数据库。");
                try {
                    // 尝试创建数据库文件
                    File file = new File(dbFilePath);
                    if (!file.createNewFile()) {
                        throw new IOException("无法创建数据库文件");
                    }
                    SpCoBot.LOGGER.info("已成功创建数据库文件。");
                } catch (IOException e) {
                    throw new RuntimeException("试图于 " + dbFilePath + " 创建数据库文件时发生错误: " + e.getMessage());
                }
            }

            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);

            checkTables();
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

    private void checkTables() throws SQLException {
        new TableChecker(this, "user")
                .addColumn(new ColumnBuilder("id", FieldType.INTEGER).notNull().primaryKey())
                .addColumn(new ColumnBuilder("smf_coin", FieldType.INTEGER).defaultValue("0"))
                .addColumn(new ColumnBuilder("permission", FieldType.INTEGER).defaultValue("1"))
                .addColumn(new ColumnBuilder("sign", FieldType.TEXT).defaultValue("从未签到过"))
                .addColumn(new ColumnBuilder("premium", FieldType.INTEGER).defaultValue("0"))
                .addColumn(new ColumnBuilder("star_coin", FieldType.INTEGER).defaultValue("0"))
                .check();
        new TableChecker(this, "valorant_user")
                .addColumn(new ColumnBuilder("id", FieldType.INTEGER).notNull().primaryKey())
                .addColumn(new ColumnBuilder("username", FieldType.TEXT).defaultValue("null"))
                .addColumn(new ColumnBuilder("password", FieldType.TEXT).defaultValue("null"))
                .addColumn(new ColumnBuilder("access_token", FieldType.TEXT).defaultValue("null"))
                .addColumn(new ColumnBuilder("entitlements", FieldType.TEXT).defaultValue("null"))
                .addColumn(new ColumnBuilder("uuid", FieldType.TEXT).defaultValue("null"))
                .addColumn(new ColumnBuilder("name", FieldType.TEXT).defaultValue("null"))
                .addColumn(new ColumnBuilder("tag", FieldType.TEXT).defaultValue("null"))
                .addColumn(new ColumnBuilder("create_data", FieldType.TEXT).defaultValue("null"))
                .addColumn(new ColumnBuilder("ban_type", FieldType.TEXT).defaultValue("null"))
                .addColumn(new ColumnBuilder("region", FieldType.TEXT).defaultValue("null"))
                .check();
        new TableChecker(this, "mcs")
                .addColumn(new ColumnBuilder("group_id", FieldType.INTEGER).notNull().primaryKey())
                .addColumn(new ColumnBuilder("host", FieldType.TEXT).defaultValue("null"))
                .addColumn(new ColumnBuilder("port", FieldType.INTEGER).defaultValue("58964"))
                .check();
        new TableChecker(this, "trade")
                .addColumn(new ColumnBuilder("id", FieldType.INTEGER).notNull().primaryKey())
                .addColumn(new ColumnBuilder("user", FieldType.INTEGER).notNull())
                .addColumn(new ColumnBuilder("date", FieldType.TEXT).notNull())
                .addColumn(new ColumnBuilder("time", FieldType.TEXT).notNull())
                .addColumn(new ColumnBuilder("amount", FieldType.INTEGER).notNull().defaultValue("0"))
                .addColumn(new ColumnBuilder("state", FieldType.TEXT).defaultValue("unpaid"))
                .check();
        new TableChecker(this, "expenses")
                .addColumn(new ColumnBuilder("user", FieldType.INTEGER).notNull())
                .addColumn(new ColumnBuilder("date", FieldType.TEXT).notNull())
                .addColumn(new ColumnBuilder("time", FieldType.TEXT).notNull())
                .addColumn(new ColumnBuilder("amount", FieldType.INTEGER).notNull())
                .addColumn(new ColumnBuilder("balance", FieldType.INTEGER).notNull())
                .addColumn(new ColumnBuilder("desc", FieldType.TEXT).defaultValue("null"))
                .check();
        new TableChecker(this, "feature")
                .addColumn(new ColumnBuilder("id", FieldType.TEXT).notNull().unique())
                .addColumn(new ColumnBuilder("disable", FieldType.INTEGER).defaultValue("0"))
                .addColumn(new ColumnBuilder("unavailable", FieldType.TEXT))
                .check();
    }

    /**
     * 从数据库表中查询符合指定条件的数据，并返回结果中的某个字段的值。
     *
     * @param tableName            表名
     * @param columnName           要获取值的字段名
     * @param primaryKeyColumnName 作为查询条件的主键字段名，字段必须为 {@code PRIMARY KEY}
     * @param primaryKeyValue      主键字段的值
     * @return 符合条件的记录中指定字段的值，如果未找到记录则返回 null
     * @throws SQLException 如果执行 SQL 查询时发生错误
     */
    public String selectString(String tableName, String columnName, String primaryKeyColumnName, Object primaryKeyValue) throws SQLException {
        String sql = "SELECT " + columnName + " FROM " + tableName + " WHERE " + primaryKeyColumnName + " = ?";
        try (PreparedStatement pstmt = getConn().prepareStatement(sql)) {
            pstmt.setObject(1, primaryKeyValue);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString(columnName);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new SQLException("Error executing SQL query: " + e.getMessage());
        }
    }

    /**
     * 从数据库表中查询符合指定条件的数据，并返回结果中的某个字段的值。
     *
     * @param tableName            表名
     * @param columnName           要获取值的字段名
     * @param primaryKeyColumnName 作为查询条件的主键字段名，字段必须为 {@code PRIMARY KEY}
     * @param primaryKeyValue      主键字段的值
     * @return 符合条件的记录中指定字段的值，如果未找到记录则返回 null
     * @throws SQLException 如果执行 SQL 查询时发生错误
     */
    public Integer selectInt(String tableName, String columnName, String primaryKeyColumnName, Object primaryKeyValue) throws SQLException {
        String sql = "SELECT " + columnName + " FROM " + tableName + " WHERE " + primaryKeyColumnName + " = ?";
        try (PreparedStatement pstmt = getConn().prepareStatement(sql)) {
            pstmt.setObject(1, primaryKeyValue);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(columnName);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new SQLException("Error executing SQL query: " + e.getMessage());
        }
    }

    /**
     * 从数据库表中查询符合指定条件的数据，并返回结果中的某个字段的值。
     *
     * @param tableName            表名
     * @param columnName           要获取值的字段名
     * @param primaryKeyColumnName 作为查询条件的主键字段名，字段必须为 {@code PRIMARY KEY}
     * @param primaryKeyValue      主键字段的值
     * @return 符合条件的记录中指定字段的值，如果未找到记录则返回 null
     * @throws SQLException 如果执行 SQL 查询时发生错误
     */
    public Long selectLong(String tableName, String columnName, String primaryKeyColumnName, Object primaryKeyValue) throws SQLException {
        String sql = "SELECT " + columnName + " FROM " + tableName + " WHERE " + primaryKeyColumnName + " = ?";
        try (PreparedStatement pstmt = getConn().prepareStatement(sql)) {
            pstmt.setObject(1, primaryKeyValue);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong(columnName);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new SQLException("Error executing SQL query: " + e.getMessage());
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
     * @deprecated 请用 {@link #selectString(String, String, String, Object)} 替代
     */
    @Deprecated
    public String select(String table, String columns, String whereClause, Object whereValues) throws SQLException {
        try (ResultSet rs = select(table, new String[]{columns}, whereClause + " = ?", new Object[]{whereValues})) {
            if (rs.next()) {
                return rs.getString(columns);
            } else {
                return null;
            }
        }
    }

    @Deprecated
    public ResultSet select(String table, String[] columns, String whereClause, Object[] whereValues) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT ");
        for (int i = 0; i < columns.length; i++) {
            sql.append(columns[i]);
            if (i != columns.length - 1) {
                sql.append(",");
            }
        }
        sql.append(" FROM ").append(table).append(" WHERE ").append(whereClause);
        try (PreparedStatement pstmt = getConn().prepareStatement(sql.toString())) {
            for (int i = 0; i < whereValues.length; i++) {
                pstmt.setObject(i + 1, whereValues[i]);
            }
            return pstmt.executeQuery();
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
        try (PreparedStatement pstmt = getConn().prepareStatement(sql)) {
            setParameters(pstmt, params);
            return pstmt.executeUpdate();
        }
    }

    /**
     * 设置PreparedStatement的参数
     *
     * @param pstmt  PreparedStatement
     * @param params 参数数组
     */
    public void setParameters(PreparedStatement pstmt, Object... params) throws SQLException {
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
        }
    }

    /**
     * 该方法用于检查指定的列是否在指定的 SQLite 表中存在。
     *
     * @param tableName  需要检查的表名
     * @param columnName 需要检查的列名
     * @return 如果列存在则返回 true，否则返回 false
     */
    public boolean columnExistsInTable(String tableName, String columnName) throws SQLException {
        try (Statement stmt = getConn().createStatement();
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
     * @return 包含字段值的列表
     */
    public List<Long> getLongFieldValues(String fieldName, String tableName) throws SQLException {
        if (conn == null || conn.isClosed()) {
            openConn();
        }
        List<Long> fieldValues = new ArrayList<>();
        // 创建查询语句
        String sql = "SELECT " + fieldName + " FROM " + tableName;
        // 执行查询
        Statement statement = this.conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        // 处理结果
        while (resultSet.next()) {
            Long fieldValue = resultSet.getLong(fieldName);
            fieldValues.add(fieldValue);
        }
        resultSet.close();
        statement.close();

        return fieldValues;
    }

    public void insertData(String sql, Object... params) throws SQLException {
        try (PreparedStatement pstmt = getConn().prepareStatement(sql)) {
            // 设置参数
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            // 执行插入操作
            pstmt.executeUpdate();
        } catch (Exception e) {
            SpCoBot.LOGGER.error(e);
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

    public boolean isTableExists(String tableName) throws SQLException {
        DatabaseMetaData metaData = getConn().getMetaData();
        try (ResultSet rs = metaData.getTables(null, null, tableName, null)) {
            return rs.next();
        }
    }

    public boolean isColumnExists(String tableName, String columnName) throws SQLException {
        DatabaseMetaData metaData = getConn().getMetaData();
        try (ResultSet rs = metaData.getColumns(null, null, tableName, columnName)) {
            return rs.next();
        }
    }
}