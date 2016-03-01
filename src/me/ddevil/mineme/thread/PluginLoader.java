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
import java.util.concurrent.Callable;
import me.ddevil.core.thread.CustomThread;
import me.ddevil.mineme.MineMeMessageManager;
import me.ddevil.mineme.MineMe;
import static me.ddevil.mineme.MineMe.WEP;
import static me.ddevil.mineme.MineMe.forceDefaultHolograms;
import static me.ddevil.mineme.MineMe.messagesConfig;
import static me.ddevil.mineme.MineMe.minesFolder;
import static me.ddevil.mineme.MineMe.pluginConfig;
import static me.ddevil.mineme.MineMe.pluginFolder;
import static me.ddevil.mineme.MineMe.setForceHologramsUse;
import me.ddevil.mineme.events.MineLoadEvent;
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

    private final MineMe mineMe = MineMe.getInstance();

    @Override
    public void doRun() {
        mineMe.debug("Loading config...");
        setupConfig();
        mineMe.debug("Config loaded!");
        mineMe.debug("Loading Message Manager...");
        MineMe.messageManager = new MineMeMessageManager();
        MineMe.messageManager.setup();
        mineMe.debug("Message Manager loaded!");
        mineMe.debug("Looking for extra dependencies...");
        setupDependencies();
        mineMe.debug("Loading Plugin...");
        setupPlugin();
        mineMe.debug("Plugin loaded!");
    }

    private void setupConfig() {
        pluginFolder = mineMe.getDataFolder();
        if (!pluginFolder.exists()) {
            mineMe.debug("Plugin folder not found! Making one...", 3);
            pluginFolder.mkdir();
        }

        File config = new File(mineMe.getDataFolder(), "config.yml");
        if (!config.exists()) {
            //Load from plugin
            mineMe.debug("Config file not found! Making one...", 3);
            mineMe.saveResource("config.yml", false);
        }
        pluginConfig = YamlConfiguration.loadConfiguration(config);
        mineMe.minimumDebugPriotity = pluginConfig.getInt("settings.minimumDebugLevel");

        minesFolder = new File(pluginFolder.getPath(), "mines");
        if (!minesFolder.exists()) {
            mineMe.debug("Mines folder not found! Making one...", 3);
            minesFolder.mkdir();
        }
        File messages = new File(mineMe.getDataFolder(), "messages.yml");
        if (!messages.exists()) {
            //Load from plugin
            mineMe.debug("Messages file not found! Making one...", 3);
            mineMe.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messages);
        MineMe.forceDefaultBroadcastMessage = messagesConfig.getBoolean("global.forceDefaultBroadcastMessage");
    }

    public void setupDependencies() {
        //Try to get dependencies
        if (Bukkit.getPluginManager().isPluginEnabled("WorldEdit")) {
            WEP = (WorldEditPlugin) mineMe.getServer().getPluginManager().getPlugin("WorldEdit");
        }
        if (WEP == null) {
            mineMe.debug("WorldEdit is need for this plugin to work! :(", true);
            mineMe.debug("Please download and install it!", true);
            Bukkit.getPluginManager().disablePlugin(mineMe);
            return;
        }
        MineMe.hologramsUsable = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");
        if (MineMe.hologramsUsable) {
            mineMe.debug();
            mineMe.debug("Detected HolographicDisplays!", 3);
            MineMe.useHolograms = pluginConfig.getBoolean("settings.useHolographicDisplays");
            MineMe.forceDefaultHolograms = pluginConfig.getBoolean("global.forceDefaultHologramOnAllMines");
            if (MineMe.useHolograms) {
                mineMe.debug("Holograms enabled!", 3);
                setForceHologramsUse(forceDefaultHolograms);
                if (forceDefaultHolograms) {
                    mineMe.debug("Forcing allHolograms to use default preset.", 3);
                }
            } else {
                mineMe.debug("Holograms are usable, but not enabled.", true);
                mineMe.debug("If you wish to use them, enable useHolographicDisplays in config.yml", true);
            }
            mineMe.debug();
        }
    }

    private void setupPlugin() {
        //Get mines folder
        minesFolder = new File(mineMe.getDataFolder(), "mines");
        if (!minesFolder.exists()) {
            mineMe.debug("Mines folder not found! Making one...", 3);
            minesFolder.mkdir();
        }
        if (minesFolder.listFiles().length == 0) {
            mineMe.debug("Mines folder is empty! Adding examplemine.yml", 3);
            mineMe.saveResource("examplemine.yml", false);
            File f = new File(mineMe.getDataFolder() + "/examplemine.yml");
            try {
                FileUtils.moveFileToDirectory(f, minesFolder, false);
                mineMe.debug("examplemine.yml added!", 3);
            } catch (IOException ex) {
                f.delete();
                mineMe.debug("There was a problem trying to copy examplemine.yml to the mines folder. Skipping.", true);
            }
        }
        mineMe.debug();
        //load mines
        mineMe.debug("Loading mines", true);
        mineMe.debug();
        File[] mineFiles = minesFolder.listFiles();
        int i = 0;
        for (File file : mineFiles) {
            String filename = file.getName();
            mineMe.debug("Attempting to load " + filename + "...", 3);

            String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
            if (!"yml".equals(extension)) {
                mineMe.debug(filename + " isn't a .yml file! This shouldn't be here! Skipping.", true);
                continue;
            }
            FileConfiguration mine = YamlConfiguration.loadConfiguration(file);
            //Get name
            String name = mine.getString("name");
            if (!mine.getBoolean("enabled")) {
                mineMe.debug("Mine " + name + " is disabled, skipping.", 3);
                continue;
            }
            //Load mine
            try {
                mineMe.debug("Loading...");
                MineConfig config = new MineConfig(mine);
                //Instanciate
                mineMe.debug("Instancializating mine " + name + " in world " + config.getWorld().getName(), 3);
                Mine m = new CuboidMine(config);
                Bukkit.getScheduler().scheduleSyncDelayedTask(mineMe, new Runnable() {

                    @Override
                    public void run() {
                        if (MineMe.useHolograms) {
                            if (m instanceof HologramCompatible) {
                                mineMe.debug("Mine " + m.getName() + " is Holograms compatible! Creating holograms...", 3);
                                HologramCompatible h = (HologramCompatible) m;
                                h.setupHolograms();
                            }
                        }
                        m.reset();
                    }
                }, 0l);
                MineManager.registerMine(m);
                new MineLoadEvent(m).call();
                mineMe.debug("Loaded mine " + m.getName() + ".", true);
                mineMe.debug();
                i++;
            } catch (Throwable t) {
                mineMe.debug("Something went wrong while loading " + file.getName() + " :(", true);
                mineMe.debug("--== Error ==--", true);
                t.printStackTrace();
                mineMe.debug("--== Error ==--", true);
                mineMe.debug();
            }
            mineMe.debug("Loaded  " + i + " mines :D", true);
        }
        long minute = 60 * 20L;
        //Check if timer is running
        if (MineMe.resetId != null) {
            Bukkit.getScheduler().cancelTask(MineMe.resetId);
        }
        //Start timer
        MineMe.resetId = Bukkit.getScheduler().scheduleSyncRepeatingTask(mineMe, new Runnable() {
            @Override
            public void run() {
                for (Mine mine : MineManager.getMines()) {
                    mine.tictoc();
                }
            }
        }, minute, minute);
    }

}
