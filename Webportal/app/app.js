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

$(function () {
    if (window.location.hash == "") {
        window.location = "#/";
    }

    function loadMaterializeJS() {
        $('.materialboxed').materialbox();
        $('.collapsible').collapsible({
            accordion: false // A setting that changes the collapsible behavior to expandable instead of the default accordion style
        });
    }

    $(window).on('hashchange', function () {
        $("html, body").scrollTop(0);

        loadMaterializeJS();
    });

    setTimeout(loadMaterializeJS, 200);
});

var statisticianApp = angular.module('statisticianApp', [
    'ui.router',
    'ui.materialize'
]);

statisticianApp.config(function ($stateProvider, $urlRouterProvider) {
    $urlRouterProvider
        .otherwise("/");

    $stateProvider
        .state('home', {
            url: '/',
            templateUrl: 'app/home/home.html'
        })
});