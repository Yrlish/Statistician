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

package io.yrlish.statistician.statistics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface Statistic {

    /**
     * Lets the Statistic "tick" one time, does internal calculations and checks that needs to be run periodically.
     */
    void tick();

    /**
     * This method should return a prepared statement for the specific Statistic.
     *
     * @param connection A database connection
     * @return Returns a prepared statement
     * @throws SQLException Throws an SQL Exception
     */
    PreparedStatement[] getPreparedStatements(Connection connection) throws SQLException;

    /**
     * Fills the Prepared Statement with items the should be sent to the database, makes it execute ready.
     *
     * @param preparedStatements The prepared statement
     * @throws SQLException Throws an SQL Exception
     */
    void fillPreparedStatements(PreparedStatement[] preparedStatements) throws SQLException;

}
