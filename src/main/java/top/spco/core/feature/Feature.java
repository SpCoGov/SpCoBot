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
package top.spco.core.feature;

import top.spco.SpCoBot;
import top.spco.api.Interactive;
import top.spco.util.SerializationUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * 该类表示机器人的一个功能。
 *
 * @author SpCo
 * @version 4.0.0
 * @since 4.0.0
 */
public abstract class Feature {
    private static final HashMap<String,Feature> availableIds = new HashMap<>();

    /**
     * 查询该功能在某个可交互的对象中是否可用。
     *
     * @param where 要查询的对象
     */
    public abstract boolean isAvailable(Interactive<?> where) throws Exception;

    /**
     * 功能的初始化。会在其注册完毕后执行。
     */
    public void init() {
    }

    public abstract Supplier<FeatureManager<?, ? extends Feature>> manager();

    public abstract String getFeatureName();

    public String getFeatureId() {
        return manager().get().getFeatureType() + "." + getFeatureName();
    }

    public static boolean isAvailable(Feature feature, Interactive<?> where) throws SQLException {
        String featureId = feature.getFeatureId();
        String sql = "SELECT disable, unavailable FROM feature WHERE id = ?";
        try (PreparedStatement stmt = SpCoBot.getInstance().getDataBase().getConn().prepareStatement(sql)) {
            stmt.setString(1, featureId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    boolean disable = rs.getInt("disable") == 1;
                    if (disable) {
                        return false;
                    }
                    String unavailable = rs.getString("unavailable");
                    Set<Long> unavailableIds = SerializationUtil.deserializeLongSet(unavailable);
                    return !unavailableIds.contains(where.getId());
                }
                return true;
            }
        }
    }

    public static boolean isDisabled(Feature feature) throws SQLException {
        String featureId = feature.getFeatureId();
        String sql = "SELECT disable FROM feature WHERE id = ?";
        try (PreparedStatement stmt = SpCoBot.getInstance().getDataBase().getConn().prepareStatement(sql)) {
            stmt.setString(1, featureId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("disable") == 1;
                }
                return false;
            }
        }
    }

    public static Set<Long> getUnavailableIds(Feature feature) throws SQLException {
        String featureId = feature.getFeatureId();
        String sql = "SELECT unavailable FROM feature WHERE id = ?";
        try (PreparedStatement stmt = SpCoBot.getInstance().getDataBase().getConn().prepareStatement(sql)) {
            stmt.setString(1, featureId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return SerializationUtil.deserializeLongSet(rs.getString("unavailable"));
                }
                return new HashSet<>();
            }
        }
    }

    public static void addUnavailable(Feature feature, Interactive<?> where) throws SQLException {
        Set<Long> unavailableIds = getUnavailableIds(feature);
        unavailableIds.add(where.getId());
        setUnavailable(feature, unavailableIds);
    }

    public static void removeUnavailable(Feature feature, Interactive<?> where) throws SQLException {
        Set<Long> unavailableIds = getUnavailableIds(feature);
        unavailableIds.remove(where.getId());
        setUnavailable(feature, unavailableIds);
    }

    public static void setDisabled(Feature feature, boolean disable) throws SQLException {
        checkAvailableIdOrThrow(feature);
        String featureId = feature.getFeatureId();
        if (isFeatureExistsInDatabase(feature)) {
            // 更新记录
            String updateQuery = "UPDATE feature SET disable = ? WHERE id = ?";
            try (PreparedStatement updateStmt = SpCoBot.getInstance().getDataBase().getConn().prepareStatement(updateQuery)) {
                updateStmt.setInt(1, disable ? 1 : 0);
                updateStmt.setString(2, featureId);
                updateStmt.executeUpdate();
            }
        } else {
            insertFeature(feature, disable, null);
        }
    }

    public static void setUnavailable(Feature feature, Set<Long> unavailable) throws SQLException {
        checkAvailableIdOrThrow(feature);
        String featureId = feature.getFeatureId();
        if (isFeatureExistsInDatabase(feature)) {
            // 更新记录
            String updateQuery = "UPDATE feature SET unavailable = ? WHERE id = ?";
            try (PreparedStatement updateStmt = SpCoBot.getInstance().getDataBase().getConn().prepareStatement(updateQuery)) {
                updateStmt.setString(1, SerializationUtil.serializeLongSet(unavailable));
                updateStmt.setString(2, featureId);
                updateStmt.executeUpdate();
            }
        } else {
            insertFeature(feature, false, unavailable);
        }
    }

    public static void insertFeature(Feature feature, boolean disable, Set<Long> unavailable) throws SQLException {
        if (!isFeatureExistsInDatabase(feature)) {
            checkAvailableIdOrThrow(feature);
            String insertQuery = "INSERT INTO feature (id, disable, unavailable) VALUES (?, ?, ?)";
            try (PreparedStatement insertStmt = SpCoBot.getInstance().getDataBase().getConn().prepareStatement(insertQuery)) {
                insertStmt.setString(1, feature.getFeatureId());
                insertStmt.setInt(2, disable ? 1 : 0);
                insertStmt.setString(3, SerializationUtil.serializeLongSet(unavailable));
                insertStmt.executeUpdate();
            }
        }
    }

    public static boolean isFeatureExistsInDatabase(Feature feature) throws SQLException {
        String checkQuery = "SELECT COUNT(*) FROM feature WHERE id = ?";
        try (PreparedStatement checkStmt = SpCoBot.getInstance().getDataBase().getConn().prepareStatement(checkQuery)) {
            checkStmt.setString(1, feature.getFeatureId());
            try (ResultSet rs = checkStmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public static void checkAvailableIdOrThrow(Feature feature) {
        if (!isFeatureIdAvailable(feature.getFeatureId())) {
            throw new IllegalArgumentException("Unregistered Feature: " + feature.getFeatureId());
        }
    }

    public static void addAvailableFeatureId(Feature feature) {
        if (feature == DummyFeature.INSTANCE) {
            return;
        }
        availableIds.put(feature.getFeatureId(),feature);
    }

    public static boolean isFeatureIdAvailable(String id) {
        return availableIds.containsKey(id);
    }

    public static Feature getFeatureById(String featureId) {
        return availableIds.get(featureId);
    }

    public static Set<String> getAllFeatureIds() {
        return availableIds.keySet();
    }
}
