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

import com.flowpowered.math.vector.Vector3d;
import io.yrlish.statistician.Statistician;
import io.yrlish.statistician.database.DatabaseManager;
import io.yrlish.statistician.utilities.EntityHelper;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.scheduler.Scheduler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class PlayerTravelDistance {
    Queue<QueueItem> queue = new LinkedBlockingQueue<>();

    public PlayerTravelDistance() {
        Scheduler scheduler = Sponge.getScheduler();
        scheduler.createTaskBuilder()
                .async()
                .interval(5, TimeUnit.SECONDS)
                .execute(new Task())
                .submit(Statistician.getInstance());
    }

    @Listener
    public void onEntityMove(MoveEntityEvent event) {
        // If the move event is in the same world
        if (event.getFromTransform().getExtent().getUniqueId()
                .equals(event.getToTransform().getExtent().getUniqueId())) {

            // Retrieve the entire entity stack
            // For every player entity, register movement
            Set<Entity> passengerStack = EntityHelper.getPassengerStack(event.getTargetEntity());

            for (Entity entity : passengerStack) {
                if (entity instanceof Player) {
                    QueueItem item = new QueueItem((Player) entity,
                            event.getFromTransform().getPosition(),
                            event.getToTransform().getPosition());
                    queue.add(item);
                }
            }
        }
    }

    private class Task implements Runnable {
        @Override
        public void run() {
            DatabaseManager databaseManager = new DatabaseManager();
            try (Connection con = databaseManager.getConnection()) {
                String sql = "INSERT INTO player_travel_distance (uuid, distance) " +
                        "VALUES (?, ?) ON DUPLICATE KEY UPDATE distance=distance + VALUES(distance);";

                try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
                    QueueItem item;
                    while ((item = queue.poll()) != null) {
                        double distance = item.getFrom().distance(item.getTo());

                        if (distance > 0 && distance < 10) {
                            preparedStatement.setString(1, item.getPlayer().getUniqueId().toString());
                            preparedStatement.setDouble(2, distance);
                            preparedStatement.addBatch();
                        }
                    }

                    preparedStatement.executeBatch();
                }
            } catch (SQLException e) {
                Statistician.getLogger().error("Could not query database", e);
            }
        }
    }

    private class QueueItem {
        private final Player player;
        private final Vector3d from;
        private final Vector3d to;

        public QueueItem(Player player, Vector3d from, Vector3d to) {
            this.player = player;
            this.from = from;
            this.to = to;
        }

        public Player getPlayer() {
            return player;
        }

        public Vector3d getFrom() {
            return from;
        }

        public Vector3d getTo() {
            return to;
        }
    }
}
