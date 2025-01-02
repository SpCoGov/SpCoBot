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

import top.spco.core.Builder;

/**
 * @author SpCo
 * @version 4.0.0
 * @since 4.0.0
 */
public class ColumnBuilder implements Builder<String> {
    private final String name;
    private final FieldType type;
    private boolean isPrimaryKey = false;
    private boolean isNotNull = false;
    private boolean isUnique = false;
    private String defaultValue = null;
    private String checkConstraint = null;
    private String collate = null;
    private boolean autoIncrement = false;

    public ColumnBuilder(String name, FieldType type) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Column name cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Column type cannot be null");
        }
        this.name = name;
        this.type = type;

    }

    public String getName() {
        return name;
    }

    public ColumnBuilder primaryKey() {
        this.isPrimaryKey = true;
        return this;
    }

    public ColumnBuilder notNull() {
        this.isNotNull = true;
        return this;
    }

    public ColumnBuilder unique() {
        this.isUnique = true;
        return this;
    }

    public ColumnBuilder defaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public ColumnBuilder check(String checkConstraint) {
        this.checkConstraint = checkConstraint;
        return this;
    }

    public ColumnBuilder collate(String collate) {
        this.collate = collate;
        return this;
    }

    public ColumnBuilder autoIncrement() {
        if (!isPrimaryKey) {
            throw new IllegalStateException("Auto increment can only be applied to primary key fields.");
        }
        this.autoIncrement = true;
        return this;
    }

    @Override
    public String build() {
        if (type == null) {
            throw new IllegalStateException("Column type must be specified.");
        }

        StringBuilder columnDef = new StringBuilder(name);

        columnDef.append(" ").append(type);

        if (isPrimaryKey) {
            columnDef.append(" PRIMARY KEY");
            if (autoIncrement) {
                columnDef.append(" AUTOINCREMENT");
            }
        }
        if (isNotNull) {
            columnDef.append(" NOT NULL");
        }
        if (isUnique) {
            columnDef.append(" UNIQUE");
        }
        if (defaultValue != null) {
            if (type == FieldType.TEXT) {
                columnDef.append(" DEFAULT '").append(defaultValue).append("'");
            } else {
                if (type == FieldType.INTEGER) {
                    try {
                        Integer.parseInt(defaultValue);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid default value for INTEGER type.");
                    }
                }
                columnDef.append(" DEFAULT ").append(defaultValue);
            }
        }
        if (checkConstraint != null) {
            columnDef.append(" CHECK (").append(checkConstraint).append(")");
        }
        if (collate != null) {
            columnDef.append(" COLLATE ").append(collate);
        }

        return columnDef.toString();
    }
}