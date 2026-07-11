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
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class DataBase {
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");
    private Connection conn;

    public DataBase() {
        try {
            String dbFilePath = dbFilePath();
            if (!checkFileExists(dbFilePath)) {
                SpCoBot.LOGGER.info("Initializing database.");
                try {
                    File file = new File(dbFilePath);
                    if (!file.createNewFile()) {
                        throw new IOException("Failed to create database file.");
                    }
                    SpCoBot.LOGGER.info("Database file created.");
                } catch (IOException e) {
                    throw new RuntimeException("Failed to create database file at " + dbFilePath + ": " + e.getMessage(), e);
                }
            }

            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);
            configureConnection(conn);
            checkTables();
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to or initialize database.", e);
        }
    }

    private String dbFilePath() {
        return SpCoBot.dataFolder.getAbsolutePath() + File.separator + "spcobot.db";
    }

    private boolean checkFileExists(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    public synchronized Connection getConn() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = openConn();
        }
        return conn;
    }

    public synchronized Connection openConn() throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath());
        configureConnection(conn);
        return conn;
    }

    private void configureConnection(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
            stmt.execute("PRAGMA busy_timeout = 5000");
        }
    }

    private void checkTables() throws SQLException {
        new TableChecker(this, "user")
                .addColumn(new ColumnBuilder("id", FieldType.TEXT).notNull().primaryKey())
                .addColumn(new ColumnBuilder("smf_coin", FieldType.INTEGER).defaultValue("0"))
                .addColumn(new ColumnBuilder("permission", FieldType.INTEGER).defaultValue("1"))
                .addColumn(new ColumnBuilder("sign", FieldType.TEXT).defaultValue("从未签到过"))
                .addColumn(new ColumnBuilder("premium", FieldType.INTEGER).defaultValue("0"))
                .addColumn(new ColumnBuilder("star_coin", FieldType.INTEGER).defaultValue("0"))
                .check();
        new TableChecker(this, "valorant_user")
                .addColumn(new ColumnBuilder("id", FieldType.TEXT).notNull().primaryKey())
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
                .addColumn(new ColumnBuilder("group_id", FieldType.TEXT).notNull().primaryKey())
                .addColumn(new ColumnBuilder("host", FieldType.TEXT).defaultValue("null"))
                .addColumn(new ColumnBuilder("port", FieldType.INTEGER).defaultValue("58964"))
                .check();
        new TableChecker(this, "expenses")
                .addColumn(new ColumnBuilder("user", FieldType.TEXT).notNull())
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
                .addColumn(new ColumnBuilder("available", FieldType.TEXT))
                .check();
        new TableChecker(this, "permission_role")
                .addColumn(new ColumnBuilder("id", FieldType.TEXT).notNull().primaryKey())
                .addColumn(new ColumnBuilder("name", FieldType.TEXT).notNull())
                .addColumn(new ColumnBuilder("builtin", FieldType.INTEGER).defaultValue("0"))
                .check();
        new TableChecker(this, "permission_role_node")
                .addColumn(new ColumnBuilder("role_id", FieldType.TEXT).notNull())
                .addColumn(new ColumnBuilder("node", FieldType.TEXT).notNull())
                .check();
        new TableChecker(this, "permission_user_role")
                .addColumn(new ColumnBuilder("user_id", FieldType.TEXT).notNull())
                .addColumn(new ColumnBuilder("role_id", FieldType.TEXT).notNull())
                .check();
        new TableChecker(this, "permission_user_node")
                .addColumn(new ColumnBuilder("user_id", FieldType.TEXT).notNull())
                .addColumn(new ColumnBuilder("node", FieldType.TEXT).notNull())
                .addColumn(new ColumnBuilder("effect", FieldType.TEXT).notNull().defaultValue("ALLOW"))
                .check();
    }

    public String selectString(String tableName, String columnName, String primaryKeyColumnName, Object primaryKeyValue) throws SQLException {
        String sql = "SELECT " + quoteIdentifier(columnName) + " FROM " + quoteIdentifier(tableName)
                + " WHERE " + quoteIdentifier(primaryKeyColumnName) + " = ?";
        try (PreparedStatement pstmt = getConn().prepareStatement(sql)) {
            pstmt.setObject(1, primaryKeyValue);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getString(columnName) : null;
            }
        } catch (SQLException e) {
            throw new SQLException("Error executing SQL query.", e);
        }
    }

    public Integer selectInt(String tableName, String columnName, String primaryKeyColumnName, Object primaryKeyValue) throws SQLException {
        String sql = "SELECT " + quoteIdentifier(columnName) + " FROM " + quoteIdentifier(tableName)
                + " WHERE " + quoteIdentifier(primaryKeyColumnName) + " = ?";
        try (PreparedStatement pstmt = getConn().prepareStatement(sql)) {
            pstmt.setObject(1, primaryKeyValue);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt(columnName) : null;
            }
        } catch (SQLException e) {
            throw new SQLException("Error executing SQL query.", e);
        }
    }

    public Long selectLong(String tableName, String columnName, String primaryKeyColumnName, Object primaryKeyValue) throws SQLException {
        String sql = "SELECT " + quoteIdentifier(columnName) + " FROM " + quoteIdentifier(tableName)
                + " WHERE " + quoteIdentifier(primaryKeyColumnName) + " = ?";
        try (PreparedStatement pstmt = getConn().prepareStatement(sql)) {
            pstmt.setObject(1, primaryKeyValue);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getLong(columnName) : null;
            }
        } catch (SQLException e) {
            throw new SQLException("Error executing SQL query.", e);
        }
    }

    public synchronized int update(String sql, Object... params) throws SQLException {
        try (PreparedStatement pstmt = getConn().prepareStatement(sql)) {
            setParameters(pstmt, params);
            return pstmt.executeUpdate();
        }
    }

    public void setParameters(PreparedStatement pstmt, Object... params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
        }
    }

    public boolean columnExistsInTable(String tableName, String columnName) throws SQLException {
        validateIdentifier(tableName);
        validateIdentifier(columnName);
        try (Statement stmt = getConn().createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(" + quoteIdentifier(tableName) + ");")) {
            while (rs.next()) {
                if (columnName.equals(rs.getString("name"))) {
                    return true;
                }
            }
            return false;
        }
    }

    public List<Long> getLongFieldValues(String fieldName, String tableName) throws SQLException {
        List<Long> fieldValues = new ArrayList<>();
        String sql = "SELECT " + quoteIdentifier(fieldName) + " FROM " + quoteIdentifier(tableName);
        try (Statement statement = getConn().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                fieldValues.add(resultSet.getLong(fieldName));
            }
        }
        return fieldValues;
    }

    public List<String> getStringFieldValues(String fieldName, String tableName) throws SQLException {
        List<String> fieldValues = new ArrayList<>();
        String sql = "SELECT " + quoteIdentifier(fieldName) + " FROM " + quoteIdentifier(tableName);
        try (Statement statement = getConn().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                fieldValues.add(resultSet.getString(fieldName));
            }
        }
        return fieldValues;
    }

    public synchronized void insertData(String sql, Object... params) throws SQLException {
        try (PreparedStatement pstmt = getConn().prepareStatement(sql)) {
            setParameters(pstmt, params);
            pstmt.executeUpdate();
        }
    }

    public void addColumn(String tableName, String columnName, String dataType) throws SQLException {
        validateIdentifier(tableName);
        validateIdentifier(columnName);
        FieldType fieldType;
        try {
            fieldType = FieldType.valueOf(dataType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new SQLException("Unsupported field type: " + dataType, e);
        }
        String sql = "ALTER TABLE " + quoteIdentifier(tableName) + " ADD COLUMN " + quoteIdentifier(columnName) + " " + fieldType;
        try (Statement statement = getConn().createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    public boolean isTableExists(String tableName) throws SQLException {
        validateIdentifier(tableName);
        DatabaseMetaData metaData = getConn().getMetaData();
        try (ResultSet rs = metaData.getTables(null, null, tableName, null)) {
            return rs.next();
        }
    }

    public boolean isColumnExists(String tableName, String columnName) throws SQLException {
        validateIdentifier(tableName);
        validateIdentifier(columnName);
        DatabaseMetaData metaData = getConn().getMetaData();
        try (ResultSet rs = metaData.getColumns(null, null, tableName, columnName)) {
            return rs.next();
        }
    }

    public void assertTableAndColumnsExist(String tableName, String... columnNames) throws SQLException {
        if (!isTableExists(tableName)) {
            throw new SQLException("Unknown table: " + tableName);
        }
        for (String columnName : columnNames) {
            if (!isColumnExists(tableName, columnName)) {
                throw new SQLException("Unknown column: " + tableName + "." + columnName);
            }
        }
    }

    public synchronized void runInTransaction(SqlRunnable runnable) throws SQLException {
        Connection connection = getConn();
        boolean previousAutoCommit = connection.getAutoCommit();
        try {
            connection.setAutoCommit(false);
            runnable.run();
            connection.commit();
        } catch (SQLException | RuntimeException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(previousAutoCommit);
        }
    }

    public static void validateIdentifier(String identifier) throws SQLException {
        if (identifier == null || !IDENTIFIER_PATTERN.matcher(identifier).matches()) {
            throw new SQLException("Invalid SQL identifier: " + identifier);
        }
    }

    public static String quoteIdentifier(String identifier) throws SQLException {
        validateIdentifier(identifier);
        return "\"" + identifier + "\"";
    }

    @FunctionalInterface
    public interface SqlRunnable {
        void run() throws SQLException;
    }
}
