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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

/**
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public class TableChecker {
    private final DataBase database;
    private final String tableName;
    private final HashMap<String, ColumnBuilder> columns = new HashMap<>();

    public TableChecker(DataBase database, String tableName) {
        this.database = database;
        this.tableName = tableName;
    }

    public TableChecker addColumn(ColumnBuilder column) {
        columns.put(column.getName(), column);
        return this;
    }

    public void check() throws SQLException {
        if (columns.isEmpty()) {
            throw new IllegalStateException("Table cannot be without columns.");
        }
        try {
            if (!database.isTableExists(tableName)) {
                StringBuilder sqlBuilder = new StringBuilder();
                sqlBuilder.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");
                for (ColumnBuilder column : columns.values()) {
                    sqlBuilder.append(column.build()).append(", ");
                }
                sqlBuilder.setLength(sqlBuilder.length() - 2);
                sqlBuilder.append(")");

                try (PreparedStatement stmt = database.getConn().prepareStatement(sqlBuilder.toString())) {
                    stmt.execute();
                }
                SpCoBot.LOGGER.info("{}表不存在，已成功创建。", tableName);
                return;
            }
            for (ColumnBuilder column : columns.values()) {
                if (database.isColumnExists(tableName, column.getName())) {
                    continue;
                }
                String alterSql = "ALTER TABLE " + tableName + " ADD COLUMN " + column.build();
                try (Statement stmt = database.getConn().createStatement()) {
                    stmt.execute(alterSql);
                    SpCoBot.LOGGER.info("{}列不存在，已成功创建。", column.getName());
                }
            }
        } catch (SQLException e) {
            if (!database.getConn().getAutoCommit()) {
                database.getConn().rollback();
            }
            throw e;
        }
    }
}