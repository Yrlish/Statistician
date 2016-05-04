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

package io.yrlish.statistician;

import com.google.inject.Inject;
import io.yrlish.statistician.statistics.server.Uptime;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

@Plugin(id = PluginInfo.ID, name = PluginInfo.NAME, description = PluginInfo.DESCRIPTION, version = PluginInfo.VERSION, url = PluginInfo.URL)
public class Statistician {
    private static Logger logger;
    private static Statistician instance;
    private static Game game;

    @Inject
    public Statistician(Logger logger, Game game) {
        Statistician.instance = this;
        Statistician.logger = logger;
        Statistician.game = game;
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent preInitEvent) {
        // TODO: Load configuration


        // TODO: Connect database


        // TODO: Register listeners
        Sponge.getEventManager().registerListeners(this, new Uptime());
    }

    @Listener
    public void onServerInit(GameInitializationEvent event) {
        // TODO: Init command(s)

    }

    public static Logger getLogger() {
        return logger;
    }

    public static Statistician getInstance() {
        return instance;
    }

    public static Game getGame() {
        return game;
    }
}
