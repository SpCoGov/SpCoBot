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
package top.spco.service.mcs;

import top.spco.SpCoBot;
import top.spco.api.Group;
import top.spco.api.message.Message;
import top.spco.core.Manager;
import top.spco.service.RegistrationException;
import top.spco.util.tuple.ImmutablePair;
import top.spco.util.tuple.Pair;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 用于管理 {@link McS} 的单例类。
 *
 * @author SpCo
 * @version 4.0.0
 * @since 2.0.3
 */
public class McSManager extends Manager<Long, McS> {
    private static McSManager instance;

    private McSManager() {

    }

    @Deprecated
    @Override
    public void register(Long value, McS object) throws RegistrationException {

    }

    public McS connect(Group<?> group, Message<?> caller) throws IOException {
        return connect(group, caller, false);
    }

    public McS connect(Group<?> group, Message<?> caller, boolean afterHeartbeatTimeout) throws IOException {
        if (!isBound(group.getId())) {
            throw new IllegalStateException("This group is not bound to the Minecraft server");
        }
        Pair<String, Integer> hp = getServer(group.getId());
        McS mcS = new McS(hp.getKey(), hp.getValue(), group, caller, afterHeartbeatTimeout);
        McS old = getAllRegistered().get(group.getId());
        if (old != null) {
            old.setSilence(true);
            old.close(true, null);
        }
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            if (caller != null) {
                if (!mcS.isConnected()) {
                    group.quoteReply(caller, "连接超时");
                }
            }
        }, 30, TimeUnit.SECONDS);
        scheduler.shutdown();
        return mcS;
    }

    public boolean isConnected(long groupId) {
        return getAllRegistered().containsKey(groupId);
    }

    public McS getMcS(long groupId) {
        return getAllRegistered().get(groupId);
    }

    public int unbind(long groupId) throws SQLException {
        String sql = "DELETE FROM mcs WHERE group_id = ?";

        try (PreparedStatement preparedStatement = SpCoBot.getInstance().getDataBase().getConn().prepareStatement(sql)) {
            preparedStatement.setLong(1, groupId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                var s = getAllRegistered().get(groupId);
                if (s != null) {
                    s.close(true, null);
                    getAllRegistered().remove(groupId);
                }
            }
            return rowsAffected;
        }
    }

    public void bind(long groupId, String host, int port) throws SQLException {
        SpCoBot.getInstance().getDataBase().insertData("insert into mcs(group_id,host,port) values (?,?,?)", groupId, host, port);
    }

    public Pair<String, Integer> getServer(long groupId) {
        String sql = "SELECT host, port FROM mcs WHERE group_id = ?";

        try (PreparedStatement preparedStatement = SpCoBot.getInstance().getDataBase().getConn().prepareStatement(sql)) {
            preparedStatement.setLong(1, groupId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String host = resultSet.getString("host");
                    int port = resultSet.getInt("port");
                    return new ImmutablePair<>(host, port);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public boolean isBound(long groupId) {
        String sql = "SELECT COUNT(*) FROM mcs WHERE group_id = ?";
        try (PreparedStatement preparedStatement = SpCoBot.getInstance().getDataBase().getConn().prepareStatement(sql)) {
            preparedStatement.setLong(1, groupId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public static McSManager getInstance() {
        if (instance == null) {
            instance = new McSManager();
        }
        return instance;
    }
}