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

package io.yrlish.statistician.config;

import io.yrlish.statistician.Statistician;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.io.IOException;

public class Config {
    private ConfigurationLoader<CommentedConfigurationNode> configLoader;
    private CommentedConfigurationNode rootNode;
    private Configuration configuration;
    private File config;

    public Config(File configDir) {
        this.config = new File(configDir, "config.conf");
        configDir.mkdir();
    }

    public boolean load() {
        try {
            if (config.exists()) {
                configLoader = HoconConfigurationLoader.builder().setFile(config).build();
                rootNode = configLoader.load();
                updateConfig();
                actuallyLoad();

                return true;
            } else if (config.createNewFile()) {
                configLoader = HoconConfigurationLoader.builder().setFile(config).build();
                rootNode = configLoader.createEmptyNode();
                updateConfig();
                actuallyLoad();

                return true;
            } else {
                Statistician.getLogger().error("Could not find or create config file.");
                return false;
            }

        } catch (IOException e) {
            Statistician.getLogger().error("Could not read or write config file.", e);
        }
        return false;
    }

    public boolean save() {
        try {
            configLoader.save(rootNode);
            return true;
        } catch (IOException e) {
            Statistician.getLogger().error("Could not save config file", e);
        }
        return false;
    }

    private void updateConfig() {
        if (rootNode.getNode("Statistician", "ConfigVersion").isVirtual()) {
            rootNode.getNode("Statistician", "ConfigVersion")
                    .setComment("DON'T EDIT! It is used to keep track on when to update your config.")
                    .setValue(1);

            rootNode.getNode("Statistician", "Database").setComment("The database credentials");
            rootNode.getNode("Statistician", "Database", "Host").setValue("localhost");
            rootNode.getNode("Statistician", "Database", "Port").setValue(3306);
            rootNode.getNode("Statistician", "Database", "Database").setValue("statistician");
            rootNode.getNode("Statistician", "Database", "Username").setValue("statistician");
            rootNode.getNode("Statistician", "Database", "Password").setValue("ChangeMe!");
        }

        save();
    }

    private void actuallyLoad() {
        Configuration configuration = new Configuration();
        configuration.setConfigVersion(rootNode.getNode("Statistician", "ConfigVersion").getInt());

        configuration.setDatabaseHost(rootNode.getNode("Statistician", "Database", "Host").getString());
        configuration.setDatabasePort(rootNode.getNode("Statistician", "Database", "Port").getInt());
        configuration.setDatabaseName(rootNode.getNode("Statistician", "Database", "Database").getString());
        configuration.setDatabaseUser(rootNode.getNode("Statistician", "Database", "Username").getString());
        configuration.setDatabasePass(rootNode.getNode("Statistician", "Database", "Password").getString());
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
