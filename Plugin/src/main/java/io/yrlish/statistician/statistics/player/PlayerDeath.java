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
import org.spongepowered.api.entity.living.Hostile;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.scheduler.Scheduler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class PlayerDeath {
    private Queue<QueueItem> queue = new LinkedBlockingQueue<>();

    public PlayerDeath() {
        Scheduler scheduler = Sponge.getScheduler();
        scheduler.createTaskBuilder()
                .async()
                .interval(5, TimeUnit.SECONDS)
                .execute(new Task())
                .submit(Statistician.getInstance());
    }

    @Listener
    public void onEntityLivingDeath(DestructEntityEvent.Death event) {
        Living living = event.getTargetEntity();

        if (living instanceof Player) { // check if death occurred on a player
            Optional<DamageSource> cause = event.getCause().first(DamageSource.class);
            Player player = (Player) living;

            if (cause.isPresent()) { // check if the cause of the death is present
                DamageSource damageSource = cause.get();

                if (damageSource instanceof EntityDamageSource) { // check if the damage source came from an entity
                    EntityDamageSource source = (EntityDamageSource) damageSource;

                    if (source.getSource() instanceof Player) { // check if that entity is a player
                        QueueItem queueItem = new QueueItem(player, damageSource.getType(), (Player) source.getSource());
                        queue.add(queueItem);
                    } else if (source.getSource() instanceof Hostile) { // check if that entity is hostile
                        QueueItem queueItem = new QueueItem(player, damageSource.getType(), (Hostile) source.getSource());
                        queue.add(queueItem);
                    } else { // damage source not from player or hostile entity
                        QueueItem queueItem = new QueueItem(player, damageSource.getType());
                        queue.add(queueItem);
                    }
                } else { // damage source is not from an entity
                    QueueItem queueItem = new QueueItem(player, damageSource.getType());
                    queue.add(queueItem);
                }
            } else { // if there was no cause, default to VOID damage type
                QueueItem queueItem = new QueueItem(player, DamageTypes.VOID);
                queue.add(queueItem);
            }
        }
    }

    private class Task implements Runnable {

        @Override
        public void run() {
            DatabaseManager databaseManager = new DatabaseManager();
            try (Connection con = databaseManager.getConnection()) {
                String sql = "INSERT INTO player_deaths (uuid, type, player, hostile, amount) " +
                        "VALUES (?, ?, ?, ?, 1) ON DUPLICATE KEY UPDATE amount=amount + VALUES(amount);";

                try (PreparedStatement preparedStatement = con.prepareStatement(sql)) {
                    QueueItem item;
                    while ((item = queue.poll()) != null) {
                        preparedStatement.setString(1, item.getPlayer().getUniqueId().toString());
                        preparedStatement.setString(2, item.getDamageType().getId());
                        preparedStatement.setString(3, (item.getByPlayer() != null) ? item.getByPlayer().getUniqueId().toString() : null);
                        preparedStatement.setString(4, (item.getByHostile() != null) ? item.getByHostile().getType().getName() : null);
                        preparedStatement.addBatch();
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
        private final DamageType damageType;
        private final Player byPlayer;
        private final Hostile byHostile;

        public QueueItem(Player player, DamageType damageType) {
            this.player = player;
            this.damageType = damageType;
            this.byPlayer = null;
            this.byHostile = null;
        }
        public QueueItem(Player player, DamageType damageType, Player byPlayer) {
            this.player = player;
            this.damageType = damageType;
            this.byPlayer = byPlayer;
            this.byHostile = null;
        }

        public QueueItem(Player player, DamageType damageType, Hostile byHostile) {
            this.player = player;
            this.damageType = damageType;
            this.byPlayer = null;
            this.byHostile = byHostile;
        }

        public Player getPlayer() {
            return player;
        }

        public DamageType getDamageType() {
            return damageType;
        }

        public Player getByPlayer() {
            return byPlayer;
        }

        public Hostile getByHostile() {
            return byHostile;
        }
    }
}
