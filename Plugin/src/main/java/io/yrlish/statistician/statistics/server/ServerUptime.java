/*
 * MIT License
 *
 * Copyright (c) 2016 Dennis Alexandersson
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

package io.yrlish.statistician.statistics.server;

import io.yrlish.statistician.Statistician;
import io.yrlish.statistician.database.DatabaseManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.scheduler.Scheduler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

public class ServerUptime {
    private ZonedDateTime serverStarted;
    private ZonedDateTime lastUptimeCheck;

    @Listener
    public void onServerStarted(GameStartedServerEvent event) {
        serverStarted = ZonedDateTime.now(ZoneOffset.UTC);

        Scheduler scheduler = Sponge.getScheduler();
        scheduler.createTaskBuilder()
                .async()
                .interval(30, TimeUnit.SECONDS)
                .execute(new UptimeTask())
                .submit(Statistician.getInstance());
    }

    private class UptimeTask implements Runnable {
        @Override
        public void run() {
            lastUptimeCheck = ZonedDateTime.now(ZoneOffset.UTC);

            updateRow();
        }

        private void updateRow() {
            DatabaseManager databaseManager = new DatabaseManager();

            try (Connection conn = databaseManager.getConnection()) {
                String sql = "INSERT INTO statistician.server_uptime (start, stop) " +
                        "VALUES (?, ?) ON DUPLICATE KEY UPDATE stop=VALUES(stop);";
                try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
                    preparedStatement.setLong(1, serverStarted.toEpochSecond());
                    preparedStatement.setLong(2, lastUptimeCheck.toEpochSecond());

                    preparedStatement.execute();
                }
            } catch (SQLException e) {
                Statistician.getLogger().error("Could not query database", e);
            }
        }
    }
}
