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

package io.yrlish.statistician.database.queue;

import io.yrlish.statistician.database.DatabaseManager;
import io.yrlish.statistician.statistics.Statistic;
import io.yrlish.statistician.statistics.StatisticLoader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static io.yrlish.statistician.Statistician.getLogger;

class QueueConsumer implements Runnable {
    @Override
    public void run() {
        DatabaseManager databaseManager = new DatabaseManager();
        try (Connection con = databaseManager.getConnection()) {
            for (Statistic statistic : StatisticLoader.getStatistics()) {
                getLogger().debug("Ticking {} once", statistic.getClass().getSimpleName());
                statistic.tick();

                getLogger().debug("Starting sending {} statistic to the database", statistic.getClass().getSimpleName());
                PreparedStatement[] ps = new PreparedStatement[0];
                try {
                    ps = statistic.getPreparedStatements(con);

                    statistic.fillPreparedStatements(ps);

                    for (PreparedStatement p : ps) {
                        p.executeBatch();
                    }
                } finally {
                    for (PreparedStatement p : ps) {
                        p.close();
                    }
                }
                getLogger().debug("Done processing {}", statistic.getClass().getSimpleName());
            }
        } catch (SQLException e) {
            getLogger().error("Error while trying to query the database", e);
        } catch (NullPointerException e) {
            getLogger().error("A NullPointerException appeared somewhere", e);
        }
    }
}
