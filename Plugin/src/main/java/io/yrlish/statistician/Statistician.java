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

package io.yrlish.statistician;

import com.google.inject.Inject;
import io.yrlish.statistician.config.Config;
import io.yrlish.statistician.database.queue.QueueManager;
import io.yrlish.statistician.statistics.Statistic;
import io.yrlish.statistician.statistics.StatisticLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;

@Plugin(
        id = "io-yrlish-statistician",
        name = "Statistician",
        version = Statistician.VERSION,
        description = "The Statistician who keeps track of your server and its users.",
        url = "https://bitbucket.org/YrlishTeam/statistician",
        authors = {"Yrlish"})
public class Statistician {
    public static final String VERSION = "0.0.1";

    private static Logger logger;
    private static Statistician instance;
    private static Game game;
    private static Config configManager;

    private static boolean disabled = false;

    @Inject
    @ConfigDir(sharedRoot = false)
    private File configDir;

    @Inject
    public Statistician(Logger logger, Game game) {
        Statistician.instance = this;
        Statistician.logger = logger;
        Statistician.game = game;
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent preInitEvent) {
        // Load config
        getLogger().debug("Loading configuration...");
        Statistician.configManager = new Config(configDir);
        if (!configManager.load()) {
            getLogger().error("Failed loading configuration");
            disablePlugin();
            return;
        }
        getLogger().debug("Done");

        // Register listeners
        getLogger().debug("Loading statistics...");
        for (Statistic statistic : StatisticLoader.getStatistics()) {
            getLogger().debug("Loading statistic: {}", statistic.getClass().getSimpleName());
            Sponge.getEventManager().registerListeners(this, statistic);
        }
        getLogger().debug("Done");

        getLogger().debug("Starting the queue consumer...");
        QueueManager.start();
        getLogger().debug("Done");
    }

    private void disablePlugin() {
        getLogger().warn("Disabling plugin...");
        disabled = true;

        Sponge.getEventManager().unregisterPluginListeners(this);
        QueueManager.stop();

        getLogger().warn("Plugin disabled");
    }

    @Listener
    public void onServerInit(GameInitializationEvent event) {
        // TODO: Setup commands

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

    public static Config getConfigManager() {
        return configManager;
    }

    public static boolean isDisabled() {
        return disabled;
    }
}
