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

package io.yrlish.statistician.database;

import io.yrlish.statistician.Statistician;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {
    private static SqlService sqlService;
    private final String jdbcUrl;

    public DatabaseManager() {
        jdbcUrl = String.format("jdbc:mysql://%s:%s@%s:%s/%s",
                Statistician.getConfigManager().getConfiguration().getDatabaseUser(),
                Statistician.getConfigManager().getConfiguration().getDatabasePass(),
                Statistician.getConfigManager().getConfiguration().getDatabaseHost(),
                Statistician.getConfigManager().getConfiguration().getDatabasePort(),
                Statistician.getConfigManager().getConfiguration().getDatabaseName());
    }

    public Connection getConnection() throws SQLException {
        if (sqlService == null) {
            sqlService = Sponge.getServiceManager().provide(SqlService.class).get();
        }
        return sqlService.getDataSource(jdbcUrl).getConnection();
    }
}
