<?php
/**
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

class StatisticianDatabase
{
    var $con;

    public function __construct()
    {
        $this->con = mysqli_connect(DB_HOST, DB_USER, DB_PASS, DB_NAME, DB_PORT);
    }

    function __destruct()
    {
        mysqli_close($this->con);
    }

    public function getTotalOnlinePlayers()
    {
        $sql = "SELECT count(*) AS total_online_players FROM player_login_history WHERE lastActive > now() - 30";

        $result = mysqli_query($this->con, $sql);
        if (!$result) {
            return intval(0);
        }

        $row = $result->fetch_array();
        return intval($row['total_online_players']);
    }

    public function getTotalUniquePlayers()
    {
        $sql = "SELECT count(*) AS total_unique_players FROM player_list";

        $result = mysqli_query($this->con, $sql);
        if (!$result) {
            return intval(0);
        }

        $row = $result->fetch_array();
        return intval($row['total_unique_players']);
    }

    public function getServerUptime()
    {
        $sql = "SELECT * FROM server_uptime";

        $result = mysqli_query($this->con, $sql);
        if (!$result) {
            return intval(0);
        }

        $firstStart = null;
        $downtime = 0;
        $lastStop = 0;

        while ($row = $result->fetch_array()) {
            if ($firstStart === null) {
                $firstStart = strtotime($row['start']);
            }

            if ($lastStop !== 0) {
                $downtime += strtotime($row['start']) - $lastStop;
            }

            $lastStop = strtotime($row['stop']);
        }

        $diffSinceFirstStart = time() - $firstStart;
        $uptime = $diffSinceFirstStart - $downtime;

        return round($uptime / $downtime, 2);
    }
}