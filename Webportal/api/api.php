<?php
/**
 * MIT License
 *
 * Copyright (c) 2016 Dennis Alexandersson (Yrlish)
 * Additionnal code by Félix Oberson (Cat121)
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

// Initialisation

define('STATS', true);
require 'config.php';
require 'StatisticianDatabase.php';
$db = new StatisticianDatabase();

// Required to easily display json response
// TODO : move it in a separated file
function print_json($arr){
    header('Content-Type: application/json');
    print_r(json_encode($arr, JSON_PRETTY_PRINT));
    die();
}

/** BASIC ROUTING
 * We cannot force people to redirect to index.php
 * For various reasons (proxy, newbie...)
 * So we check for called ednpoints and we display if needed
 * Every endpoint has the same structure
 */

if(!empty($_GET)){
    // test required file

    reset($_GET);
    $endpoint = key($_GET);
    $file = 'endpoints/'.$endpoint.'.php';

    if(file_exists($file)){
        include $file;
    }
    else{
        print_json('API 404 : endpoint not found');
    }
}
else{
    print_json('API 403 : Access forbidden');
}