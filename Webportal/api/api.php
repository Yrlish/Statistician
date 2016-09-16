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

require_once('config.php');
require_once('StatisticianDatabase.php');

$db = new StatisticianDatabase();

if (isset($_GET['getTotalOnlinePlayers'])) {
    print_json(array(
        "total_online_players" => $db->getTotalOnlinePlayers()
    ));
}


// If no GETs where found, print status 404
print_json(array(
    "status" => 404
));

function print_json($arr)
{
    header('Content-Type: application/json');

    if (!is_array($arr)) {
        print_json(array(
            "status" => 500
        ));
    }

    if (!isset($arr['status'])) {
        $arr['status'] = 200;
    }

    print_r(json_encode($arr, JSON_PRETTY_PRINT));

    die();
}