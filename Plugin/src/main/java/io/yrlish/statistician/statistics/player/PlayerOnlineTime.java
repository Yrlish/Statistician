/*
 * MIT License
 *
 * Copyright (c) 2016 Dennis Alexandersson (Yrlish)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.yrlish.statistician.statistics.player;

import io.yrlish.statistician.Statistician;
import io.yrlish.statistician.statistics.Statistic;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

public class PlayerOnlineTime implements Statistic {
    Queue<QueueItem> queue = new LinkedBlockingQueue<>();
    Map<UUID, ZonedDateTime> lastJoinTime = new HashMap<>();

    public PlayerOnlineTime() {
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Login event) {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        User user = event.getTargetUser();

        lastJoinTime.put(user.getUniqueId(), now);

        QueueItem queueItem = new QueueItem(user.getUniqueId(), user.getName(), now, now, event.getConnection().getAddress());
        queue.add(queueItem);
    }

    @Listener
    public void onPlayerLeave(ClientConnectionEvent.Login event) {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        User user = event.getTargetUser();

        if (lastJoinTime.containsKey(user.getUniqueId())) {
            QueueItem queueItem = new QueueItem(user.getUniqueId(), user.getName(),
                    lastJoinTime.get(user.getUniqueId()), now, event.getConnection().getAddress());
            queue.add(queueItem);

            lastJoinTime.remove(user.getUniqueId());
        }
    }

    @Override
    public PreparedStatement[] getPreparedStatements(Connection connection) throws SQLException {
        return new PreparedStatement[]{
                connection.prepareStatement("INSERT INTO player_list (uuid, display_name) " +
                        "VALUES (?, ?) ON DUPLICATE KEY UPDATE display_name=VALUES(display_name);"),
                connection.prepareStatement("INSERT INTO player_login_history (uuid, login, lastActive, ip) " +
                        "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE login=VALUES(login), lastActive=VALUES(lastActive);")
        };
    }

    @Override
    public void fillPreparedStatements(PreparedStatement[] preparedStatements) throws SQLException {
        QueueItem item;
        while ((item = queue.poll()) != null) {
            preparedStatements[0].setString(1, item.getUuid().toString());
            preparedStatements[0].setString(2, item.getUsername());
            preparedStatements[0].addBatch();

            preparedStatements[1].setString(1, item.getUuid().toString());
            preparedStatements[1].setTimestamp(2, Timestamp.from(item.getJoined().toInstant()));
            preparedStatements[1].setTimestamp(3, Timestamp.from(item.getLastActive().toInstant()));
            preparedStatements[1].setString(4, item.getAddress().toString());
            preparedStatements[1].addBatch();
        }
    }

    @Override
    public void tick() {
        ZonedDateTime now = ZonedDateTime.now();
        for (Player player : Statistician.getGame().getServer().getOnlinePlayers()) {
            if (lastJoinTime.containsKey(player.getUniqueId())) {
                QueueItem queueItem = new QueueItem(player.getUniqueId(), player.getName(),
                        lastJoinTime.get(player.getUniqueId()), now, player.getConnection().getAddress());
                queue.add(queueItem);
            }
        }
    }

    private class QueueItem {
        private UUID uuid;
        private String username;
        private ZonedDateTime joined;
        private ZonedDateTime lastActive;
        private InetSocketAddress address;

        public QueueItem(UUID uuid, String username, ZonedDateTime joined, ZonedDateTime lastActive, InetSocketAddress address) {
            this.uuid = uuid;
            this.username = username;
            this.joined = joined;
            this.lastActive = lastActive;
            this.address = address;
        }

        public UUID getUuid() {
            return uuid;
        }

        public String getUsername() {
            return username;
        }

        public ZonedDateTime getJoined() {
            return joined;
        }

        public ZonedDateTime getLastActive() {
            return lastActive;
        }

        public InetSocketAddress getAddress() {
            return address;
        }
    }
}
