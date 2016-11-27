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

package io.yrlish.statistician.statistics.server;

import io.yrlish.statistician.statistics.Statistic;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class ServerUptime implements Statistic {
    private ZonedDateTime serverStarted;
    private ZonedDateTime lastUptimeCheck;

    @Listener
    public void onServerStarted(GameStartedServerEvent event) {
        serverStarted = ZonedDateTime.now(ZoneOffset.UTC);
    }

    @Override
    public PreparedStatement[] getPreparedStatements(Connection connection) throws SQLException {
        return new PreparedStatement[]{
                connection.prepareStatement("INSERT INTO statistician.server_uptime (start, stop) " +
                        "VALUES (?, ?) ON DUPLICATE KEY UPDATE start=VALUES(start), stop=VALUES(stop);")
        };
    }

    @Override
    public void fillPreparedStatements(PreparedStatement[] preparedStatements) throws SQLException {
        preparedStatements[0].setTimestamp(1, Timestamp.from(serverStarted.toInstant()));
        preparedStatements[0].setTimestamp(2, Timestamp.from(lastUptimeCheck.toInstant()));
        preparedStatements[0].addBatch();
    }

    @Override
    public void tick() {
        lastUptimeCheck = ZonedDateTime.now(ZoneOffset.UTC);
    }
}
