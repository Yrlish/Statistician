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
import io.yrlish.statistician.database.DatabaseManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.scheduler.Scheduler;

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
import java.util.concurrent.TimeUnit;

public class PlayerOnlineTime {
    Queue<QueueItem> queue = new LinkedBlockingQueue<>();
    Map<UUID, ZonedDateTime> lastJoinTime = new HashMap<>();

    public PlayerOnlineTime() {
        Scheduler scheduler = Sponge.getScheduler();
        scheduler.createTaskBuilder()
                .async()
                .interval(5, TimeUnit.SECONDS)
                .execute(new Task())
                .submit(Statistician.getInstance());
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
            QueueItem queueItem = new QueueItem(user.getUniqueId(), user.getName(), lastJoinTime.get(user.getUniqueId()), now, event.getConnection().getAddress());
            queue.add(queueItem);

            lastJoinTime.remove(user.getUniqueId());
        }
    }

    private class Task implements Runnable {
        @Override
        public void run() {
            ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);

            for (Player player : Statistician.getGame().getServer().getOnlinePlayers()) {
                if (lastJoinTime.containsKey(player.getUniqueId())) {
                    QueueItem queueItem = new QueueItem(player.getUniqueId(), player.getName(), lastJoinTime.get(player.getUniqueId()), now, player.getConnection().getAddress());
                    queue.add(queueItem);
                }
            }

            DatabaseManager databaseManager = new DatabaseManager();
            try (Connection con = databaseManager.getConnection()) {
                String sql = "INSERT INTO player_list (uuid, display_name) " +
                        "VALUES (?, ?) ON DUPLICATE KEY UPDATE display_name=VALUES(display_name);";

                String sql2 = "INSERT INTO player_login_history (uuid, login, lastActive, ip) " +
                        "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE login=VALUES(login), lastActive=VALUES(lastActive);";

                try (PreparedStatement preparedStatement = con.prepareStatement(sql);
                     PreparedStatement preparedStatement2 = con.prepareStatement(sql2)) {

                    QueueItem item;
                    while ((item = queue.poll()) != null) {
                        preparedStatement.setString(1, item.getUuid().toString());
                        preparedStatement.setString(2, item.getUsername());
                        preparedStatement.addBatch();

                        preparedStatement2.setString(1, item.getUuid().toString());
                        preparedStatement2.setTimestamp(2, Timestamp.from(item.getJoined().toInstant()));
                        preparedStatement2.setTimestamp(3, Timestamp.from(item.getLastActive().toInstant()));
                        preparedStatement2.setString(4, item.getAddress().toString());
                        preparedStatement2.addBatch();
                    }

                    preparedStatement.executeBatch();
                    preparedStatement2.executeBatch();
                }
            } catch (SQLException e) {
                Statistician.getLogger().error("Could not query database", e);
            } catch (NullPointerException e) {
                Statistician.getLogger().error("NullPointerException", e);
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
