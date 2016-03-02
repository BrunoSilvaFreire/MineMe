/* 
 * Copyright (C) 2016 Selma
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.ddevil.mineme.thread;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import java.io.File;
import java.io.IOException;
import me.ddevil.core.thread.CustomThread;
import me.ddevil.mineme.messages.MineMeMessageManager;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.events.MineLoadEvent;
import me.ddevil.mineme.holograms.impl.HolographicDisplaysAdapter;
import me.ddevil.mineme.mines.HologramCompatible;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.mines.MineManager;
import me.ddevil.mineme.mines.configs.MineConfig;
import me.ddevil.mineme.mines.impl.CuboidMine;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class PluginLoader extends CustomThread {

    private final MineMe plugin = MineMe.getInstance();

    @Override
    public void doRun() {
        plugin.debug("Loading config...");
        setupConfig();
        plugin.debug("Config loaded!");
        plugin.debug("Loading Message Manager...");
        MineMe.messageManager = new MineMeMessageManager();
        MineMe.messageManager.setup();
        plugin.debug("Message Manager loaded!");
        plugin.debug("Looking for extra dependencies...");
        setupDependencies();
        plugin.debug("Loading Plugin...");
        setupPlugin();
        plugin.debug("Plugin loaded!");
    }

    private void setupConfig() {
        MineMe.pluginFolder = plugin.getDataFolder();
        if (!MineMe.pluginFolder.exists()) {
            plugin.debug("Plugin folder not found! Making one...", 3);
            MineMe.pluginFolder.mkdir();
        }

        File config = new File(plugin.getDataFolder(), "config.yml");
        if (!config.exists()) {
            //Load from plugin
            plugin.debug("Config file not found! Making one...", 3);
            plugin.saveResource("config.yml", false);
        }
        MineMe.pluginConfig = YamlConfiguration.loadConfiguration(config);
        plugin.minimumDebugPriotity = MineMe.pluginConfig.getInt("settings.minimumDebugLevel");

        MineMe.minesFolder = new File(MineMe.pluginFolder.getPath(), "mines");
        if (!MineMe.minesFolder.exists()) {
            plugin.debug("Mines folder not found! Making one...", 3);
            MineMe.minesFolder.mkdir();
        }
        File messages = new File(plugin.getDataFolder(), "messages.yml");
        if (!messages.exists()) {
            //Load from plugin
            plugin.debug("Messages file not found! Making one...", 3);
            plugin.saveResource("messages.yml", false);
        }
        MineMe.defaultHologramText = MineMe.pluginConfig.getStringList("settings.holograms.defaultHologramText");
        MineMe.messagesConfig = YamlConfiguration.loadConfiguration(messages);
        MineMe.forceDefaultBroadcastMessage = MineMe.messagesConfig.getBoolean("global.forceDefaultBroadcastMessage");
    }

    public void setupDependencies() {
        //Try to get dependencies
        if (Bukkit.getPluginManager().isPluginEnabled("WorldEdit")) {
            MineMe.WEP = (WorldEditPlugin) plugin.getServer().getPluginManager().getPlugin("WorldEdit");
        }
        if (MineMe.WEP == null) {
            plugin.debug("WorldEdit is need for this plugin to work! :(", true);
            plugin.debug("Please download and install it!", true);
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }
        //Holograms
        MineMe.useHolograms = MineMe.pluginConfig.getBoolean("settings.holograms.enableHolograms");
        //Holographic Displays

        MineMe.forceDefaultHolograms = MineMe.pluginConfig.getBoolean("global.forceDefaultHologramOnAllMines");
        MineMe.useHolographicDisplay = MineMe.pluginConfig.getBoolean("settings.holograms.useHolographicDisplaysAPI");
        if (MineMe.useHolograms) {
            //Check if HD is available
            MineMe.holographicDisplaysUsable = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");
            if (MineMe.useHolographicDisplay) {
                if (MineMe.holographicDisplaysUsable) {
                    plugin.debug();
                    plugin.debug("Detected HolographicDisplays!", 3);

                    plugin.debug("Using Holographic Displays as the hologram adapter...", 3);
                    MineMe.setForceHologramsUse(MineMe.forceDefaultHolograms);
                    MineMe.hologramAdapter = new HolographicDisplaysAdapter();
                    if (MineMe.forceDefaultHolograms) {
                        plugin.debug("Forcing all holograms to use default preset.", 3);
                    }
                    plugin.debug();
                } else {
                    //Enabled in config, .jar is not in plugins
                    plugin.debug("HolographicDisplays was enabled in the config, but is not installed in the server! Fixing...", true);
                    try {
                        MineMe.pluginConfig.set("settings.holograms.useHolographicDisplaysAPI", false);
                        MineMe.pluginConfig.save(new File(plugin.getDataFolder(), "config.yml"));
                        MineMe.useHolographicDisplay = false;
                    } catch (IOException ex) {
                        plugin.debug("There was a error fixing HolographicDisplays in the config!", true);
                        plugin.printException(ex);
                    }
                }
            }
        }

    }

    private void setupPlugin() {
        //Get mines folder
        MineMe.minesFolder = new File(plugin.getDataFolder(), "mines");
        if (!MineMe.minesFolder.exists()) {
            plugin.debug("Mines folder not found! Making one...", 3);
            MineMe.minesFolder.mkdir();
        }
        if (MineMe.minesFolder.listFiles().length == 0) {
            plugin.debug("Mines folder is empty! Adding examplemine.yml", 3);
            plugin.saveResource("examplemine.yml", false);
            File f = new File(plugin.getDataFolder() + "/examplemine.yml");
            try {
                FileUtils.moveFileToDirectory(f, MineMe.minesFolder, false);
                plugin.debug("examplemine.yml added!", 3);
            } catch (IOException ex) {
                f.delete();
                plugin.debug("There was a problem trying to copy examplemine.yml to the mines folder. Skipping.", true);
            }
        }
        if (MineMe.useHolograms && MineMe.hologramAdapter == null) {
            plugin.debug("Holograms were enabled in the config, but we didn't find an hologram adapter! Fixing...", true);
            plugin.setInConfig(MineMe.pluginConfig, "settings.holograms.enableHolograms", false);
            MineMe.useHolograms = false;
        }
        plugin.debug();
        //load mines
        plugin.debug("Loading mines", true);
        plugin.debug();
        File[] mineFiles = MineMe.minesFolder.listFiles();
        int i = 0;
        for (File file : mineFiles) {
            String filename = file.getName();
            plugin.debug("Attempting to load " + filename + "...", 3);

            String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
            if (!"yml".equals(extension)) {
                plugin.debug(filename + " isn't a .yml file! This shouldn't be here! Skipping.", true);
                continue;
            }
            FileConfiguration mine = YamlConfiguration.loadConfiguration(file);
            //Get name
            String name = mine.getString("name");
            if (!mine.getBoolean("enabled")) {
                plugin.debug("Mine " + name + " is disabled, skipping.", 3);
                continue;
            }
            //Load mine
            try {
                plugin.debug("Loading...");
                MineConfig config = new MineConfig(mine);
                //Instanciate
                plugin.debug("Instancializating mine " + name + " in world " + config.getWorld().getName(), 3);
                Mine m = new CuboidMine(config);
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

                    @Override
                    public void run() {
                        try {
                            if (MineMe.useHolograms) {
                                if (m instanceof HologramCompatible) {
                                    plugin.debug("Mine " + m.getName() + " is Holograms compatible! Creating holograms...", 3);
                                    HologramCompatible h = (HologramCompatible) m;
                                    h.setupHolograms();
                                }
                            }
                        } finally {
                            m.reset();
                        }
                    }
                }, 0l);
                MineManager.registerMine(m);
                new MineLoadEvent(m).call();
                plugin.debug("Loaded mine " + m.getName() + ".", true);
                plugin.debug();
                i++;
            } catch (Throwable t) {
                plugin.debug("Something went wrong while loading " + file.getName() + " :(", true);
                plugin.printException(t);
            }
            plugin.debug("Loaded  " + i + " mines :D", true);
        }
        long minute = 60 * 20L;
        //Check if timer is running
        if (MineMe.resetId != null) {
            Bukkit.getScheduler().cancelTask(MineMe.resetId);
        }
        //Start timer
        MineMe.resetId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                for (Mine mine : MineManager.getMines()) {
                    mine.minuteCountdown();
                }
            }
        }, minute, minute);
    }

}
