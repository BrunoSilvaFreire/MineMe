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
import java.util.HashMap;
import java.util.concurrent.Callable;
import me.ddevil.core.thread.CustomThread;
import me.ddevil.mineme.MessageManager;
import me.ddevil.mineme.MineMe;
import static me.ddevil.mineme.MineMe.WEP;
import static me.ddevil.mineme.MineMe.forceDefaultHolograms;
import static me.ddevil.mineme.MineMe.messagesConfig;
import static me.ddevil.mineme.MineMe.minesFolder;
import static me.ddevil.mineme.MineMe.pluginConfig;
import static me.ddevil.mineme.MineMe.pluginFolder;
import static me.ddevil.mineme.MineMe.setForceHologramsUse;
import static me.ddevil.mineme.MineMe.setHologramsUsable;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.mines.MineManager;
import me.ddevil.mineme.mines.configs.MineConfig;
import me.ddevil.mineme.mines.impl.CuboidMine;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class PluginLoader extends CustomThread {

    private final MineMe mineMe = MineMe.getInstance();

    @Override
    public void doRun() {

        mineMe.debug("Loading config...");
        setupConfig();
        mineMe.debug("Preparing Message Manager...");
        MessageManager.setup();
        mineMe.debug("Preparing Plugin...");
        setupPlugin();
    }

    private void setupConfig() {
        pluginFolder = mineMe.getDataFolder();
        pluginConfig = mineMe.loadConfig();
        if (!pluginFolder.exists()) {
            mineMe.debug("Plugin folder not found! Making one...");
            pluginFolder.mkdir();
        }
        minesFolder = new File(pluginFolder.getPath(), "mines");
        if (!minesFolder.exists()) {
            mineMe.debug("Mines folder not found! Making one...");
            minesFolder.mkdir();
        }
        File messages = new File(mineMe.getDataFolder(), "messages.yml");
        if (!messages.exists()) {
            //Load from plugin
            mineMe.debug("Messages file not found! Making one...");
            mineMe.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messages);
    }

    public void setupDependencies() {
        //Try to get dependencies
        if (mineMe.getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
            WEP = (WorldEditPlugin) mineMe.getServer().getPluginManager().getPlugin("WorldEdit");
        }
        if (WEP == null) {
            mineMe.debug("World edit is need for this plugin to work! :(");
            mineMe.debug("Please download and install it for it to work!");
            Bukkit.getPluginManager().disablePlugin(mineMe);
            return;
        }

        //HolographicDisplays
        if (mineMe.getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
            mineMe.debug();
            mineMe.debug("Detected HolographicDisplays!");
            setHologramsUsable();
            MineMe.useHolograms = pluginConfig.getBoolean("global.useHolographicDisplays");
            forceDefaultHolograms = pluginConfig.getBoolean("global.forceDefaultHologramOnAllMines");
            if (MineMe.useHolograms) {
                mineMe.debug("Holograms enabled!");
                setForceHologramsUse(forceDefaultHolograms);
                if (forceDefaultHolograms) {
                    mineMe.debug("Forcing allHolograms to use default preset.");
                }
            } else {
                mineMe.debug("Holograms are usable, but not enabled.");
            }
            mineMe.debug();
        }
    }

    private void setupPlugin() {
        //Get mines folder
        minesFolder = new File(mineMe.getDataFolder(), "mines");
        if (!minesFolder.exists()) {
            mineMe.debug("Mines folder not found! Making one...");
            minesFolder.mkdir();
        }
        if (minesFolder.listFiles().length == 0) {
            mineMe.debug("Mines folder is empty! Adding examplemine.yml");
            mineMe.saveResource("examplemine.yml", false);
            File f = new File(mineMe.getDataFolder() + "/examplemine.yml");
            try {
                FileUtils.moveFileToDirectory(f, minesFolder, false);
                mineMe.debug("examplemine.yml added!");
            } catch (IOException ex) {
                f.delete();
                mineMe.debug("There was a problem trying to copy examplemine.yml to the mines folder. Skipping.");
            }
        }
        mineMe.debug();
        //load mines
        mineMe.debug("Loading mines");
        mineMe.debug();
        File[] mineFiles = minesFolder.listFiles();
        int i = 0;
        for (File file : mineFiles) {
            String filename = file.getName();
            mineMe.debug("Attempting to load " + filename + "...");

            String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
            if (!"yml".equals(extension)) {
                mineMe.debug(filename + " isn't a .yml file! Skipping.");
                continue;
            }
            FileConfiguration mine = YamlConfiguration.loadConfiguration(file);
            //Get name
            String name = mine.getString("name");
            if (!mine.getBoolean("enabled")) {
                mineMe.debug("Mine " + name + " is disabled, skipping.");
                continue;
            }
            //Load mine
            try {
                mineMe.debug("Loading...");
                MineConfig config = new MineConfig(mine);
                //Instanciate
                mineMe.debug("Instancializating mine " + name + " in world " + config.getWorld().getName());
                Mine m = new CuboidMine(config);
                Bukkit.getScheduler().callSyncMethod(mineMe, new Callable<Mine>() {

                    @Override
                    public Mine call() throws Exception {
                        m.reset();
                        return m;
                    }
                });
                MineManager.registerMine(m);
                mineMe.debug("Loaded mine " + m.getName() + ".");
                mineMe.debug();
                i++;
            } catch (Throwable t) {
                mineMe.debug("Something went wrong while loading " + file.getName() + " :(");
                mineMe.debug("--== Error ==--");
                t.printStackTrace();
                mineMe.debug("--== Error ==--");
                mineMe.debug();
            }
            mineMe.debug("Loaded  " + i + " mines :D");
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
