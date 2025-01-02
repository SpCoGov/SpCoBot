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

/**
 * @author SpCo
 * @version 4.0.0
 * @since 4.0.0
 */
public enum FieldType {
    NULL("NULL"),
    INTEGER("INTEGER"),
    REAL("REAL"),
    TEXT("TEXT"),
    BLOB("BLOB"),
    NUMERIC("NUMERIC"),
    BOOLEAN("BOOLEAN"),
    DATE("DATE"),
    DATETIME("DATETIME");
    private final String type;

    FieldType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return type;
    }

    /**
     * 判断是否为常用的数值类型。
     */
    public static boolean isNumericType(FieldType fieldType) {
        return fieldType == INTEGER || fieldType == REAL || fieldType == NUMERIC;
    }

    /**
     * 判断是否为文本类型。
     */
    public static boolean isTextType(FieldType fieldType) {
        return fieldType == TEXT || fieldType == DATE || fieldType == DATETIME;
    }

    /**
     * 判断是否为二进制类型。
     */
    public static boolean isBlobType(FieldType fieldType) {
        return fieldType == BLOB;
    }
}