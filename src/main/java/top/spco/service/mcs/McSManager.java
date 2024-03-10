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
package top.spco.service.mcs;

import top.spco.SpCoBot;
import top.spco.api.Group;
import top.spco.util.tuple.ImmutablePair;
import top.spco.util.tuple.Pair;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2024/03/09 22:59
 *
 * @author SpCo
 * @version 1.0
 * @since 1.0
 */
public class McSManager {
    private static McSManager instance;
    private final Map<Long, McS> mcSs = new HashMap<>();

    private McSManager() {

    }

    public McS connect(Group<?> group) throws IOException {
        if (!isBound(group)) {
            throw new IllegalStateException("This group is not bound to the Minecraft server");
        }
        Pair<String, Integer> hp = getServer(group);
        McS mcS = new McS(hp.getKey(), hp.getValue(), group);
        var old = mcSs.get(group.getId());
        if (old != null) {
            old.close();
        }
        mcSs.put(group.getId(), mcS);
        return mcS;
    }

    public McS getMcS(Group<?> group) {
        return mcSs.get(group.getId());
    }

    public int unbind(Group<?> group) throws SQLException {
        String sql = "DELETE FROM mcs WHERE group_id = ?";

        try (PreparedStatement preparedStatement = SpCoBot.getInstance().getDataBase().getConn().prepareStatement(sql)) {
            preparedStatement.setLong(1, group.getId());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                var s = mcSs.get(group.getId());
                if (s != null) {
                    s.close();
                    mcSs.remove(group.getId());
                }
            }
            return rowsAffected;
        }
    }

    public void bind(Group<?> group, String host, int port) throws SQLException {
        SpCoBot.getInstance().getDataBase().insertData("insert into mcs(group_id,host,port) values (?,?,?)", group.getId(), host, port);
    }

    public Pair<String, Integer> getServer(Group<?> group) {
        String sql = "SELECT host, port FROM mcs WHERE group_id = ?";

        try (PreparedStatement preparedStatement = SpCoBot.getInstance().getDataBase().getConn().prepareStatement(sql)) {
            preparedStatement.setLong(1, group.getId());

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

    public boolean isBound(Group<?> group) {
        String sql = "SELECT COUNT(*) FROM mcs WHERE group_id = ?";
        try (PreparedStatement preparedStatement = SpCoBot.getInstance().getDataBase().getConn().prepareStatement(sql)) {
            preparedStatement.setLong(1, group.getId());
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