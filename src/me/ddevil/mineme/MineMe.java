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
package me.ddevil.mineme;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.ddevil.core.CustomPlugin;
import me.ddevil.mineme.commands.MineCommand;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.mines.MineManager;
import me.ddevil.mineme.mines.impl.CuboidMine;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class MineMe extends CustomPlugin {

    public static FileConfiguration pluginConfig;
    public static FileConfiguration messagesConfig;
    public static File pluginFolder;
    public static File minesFolder;
    public static WorldEditPlugin WEP;

    public static MineMe getInstance() {
        return (MineMe) instance;
    }

    public static void sendMessage(Player p, String string) {
        p.sendMessage(MessageManager.pluginPrefix + MessageManager.messageSeparator + string);
    }

    public static void sendMessage(Player p, String[] messages) {
        for (String usageMessage : messages) {
            sendMessage(p, usageMessage);
        }
    }
    private Integer resetId;
    private boolean useHolograms;

    @Override
    public void onEnable() {
        super.onEnable();
        setupConfig();
        MessageManager.setup();
        setupDependencies();
        setupPlugin();
        debug("Plugin loaded!");
        debug("It's all right, it's all favorable :D");
    }

    public void debug(String[] msg) {
        for (String m : msg) {
            debug(m);
        }
    }

    public void debug(String msg) {
        getLogger().info(msg);
    }

    public void debug() {
        getLogger().info("");
    }

    public static FileConfiguration getYAMLMineFile(Mine m) {
        return YamlConfiguration.loadConfiguration(getMineFile(m));
    }

    public static File getMineFile(Mine m) {
        return new File(pluginFolder.getPath() + "/" + m.getName() + ".yml");

    }

    private void setupConfig() {
        pluginFolder = getDataFolder();
        pluginConfig = loadConfig();
        if (!pluginFolder.exists()) {
            debug("Plugin folder not found! Making one...");
            pluginFolder.mkdir();
        }
        minesFolder = new File(pluginFolder.getPath(), "mines");
        if (!minesFolder.exists()) {
            debug("Mines folder not found! Making one...");
            minesFolder.mkdir();
        }
        File messages = new File(getDataFolder(), "messages.yml");
        if (!messages.exists()) {
            //Load from plugin
            debug("Messages file not found! Making one...");
            saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messages);
    }

    public static boolean hologramsUsable = false;
    public static boolean forceDefaultHolograms = false;

    public static void setHologramsUsable() {
        hologramsUsable = true;
    }

    public static void setHologramsUsable(boolean hologramsUsable) {
        MineMe.hologramsUsable = hologramsUsable;
    }

    public static void setForceHologramsUse(boolean forceHologramsUse) {
        MineMe.forceDefaultHolograms = forceHologramsUse;
    }

    public static boolean isForceHologramsUse() {
        return forceDefaultHolograms;
    }

    private void registerBaseCommands() {
        registerCommand(new MineCommand());
    }

    public void reload(Player p) {
        sendMessage(p, "Reloading config...");
        debug("Stopping reseter task...");
        Bukkit.getScheduler().cancelTask(resetId);
        debug("Unloading...");
        unloadEverything();
        debug("Reloading config...");
        setupConfig();
        debug("Preparing Message Manager...");
        MessageManager.setup();
        debug("Preparing Plugin...");
        setupPlugin();
        debug("Reload complete!");
        sendMessage(p, "Reloaded! :D");
    }

    private void unloadEverything() {
        File messages = new File(getDataFolder() + "/messages.yml");
        File config = new File(getDataFolder() + "/config.yml");
        try {
            pluginConfig.save(config);
            messagesConfig.save(messages);
        } catch (IOException ex) {
            Logger.getLogger(MineMe.class.getName()).log(Level.SEVERE, null, ex);
        }
        pluginConfig = null;
        pluginFolder = null;
        messagesConfig = null;
        resetId = null;
    }

    private void setupPlugin() {
        //Get mines folder
        minesFolder = new File(getDataFolder(), "mines");
        if (!minesFolder.exists()) {
            debug("Mines folder not found! Making one...");
            minesFolder.mkdir();
        }
        if (minesFolder.listFiles().length == 0) {
            saveResource("examplemine.yml", false);
            File f = new File(getDataFolder() + "/examplemine.yml");
            try {
                FileUtils.moveFileToDirectory(f, minesFolder, false);
            } catch (IOException ex) {
                f.delete();
                debug("There was a problem trying to copy examplemine.yml to the mines folder. Skipping.");
            }
        }
        //load mines
        debug("Loading mines");
        debug();
        File[] mineFiles = minesFolder.listFiles();
        int i = 0;
        for (File file : mineFiles) {
            String filename = file.getName();
            debug("Attempting to load " + filename + "...");

            String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
            if (!"yml".equals(extension)) {
                debug(filename + " isn't a .yml file! Skipping.");
                continue;
            }
            FileConfiguration mine = YamlConfiguration.loadConfiguration(file);
            //Get name
            String name = mine.getString("name");
            if (!mine.getBoolean("enabled")) {
                debug("Mine " + name + " is disabled, skipping.");
                continue;
            }
            //Load mine
            try {
                debug("Loading...");
                //Get world
                World w = Bukkit.getWorld(mine.getString("world"));

                //Get Composition
                HashMap<Material, Double> comp = new HashMap();
                for (String s : mine.getStringList("composition")) {
                    String[] split = s.split("=");
                    try {
                        comp.put(Material.valueOf(split[0]), Double.valueOf(split[1]));
                    } catch (NumberFormatException e) {
                        debug(split[1] + " in " + s + "isn't a number!");
                        debug("Skipping mine " + name);
                    }

                }

                //Instanciate
                debug("Instancializating mine " + name + " in world " + w.getName());
                Mine m = new CuboidMine(
                        name,
                        new Location(w,
                                mine.getDouble("X1"),
                                mine.getDouble("Y1"),
                                mine.getDouble("Z1")),
                        new Location(w,
                                mine.getDouble("X2"),
                                mine.getDouble("Y2"),
                                mine.getDouble("Z2")),
                        comp,
                        mine.getBoolean("broadcastOnReset")
                );
                m.reset();
                MineManager.registerMine(m);
                debug("Loaded mine " + m.getName() + ".");
                debug();
                i++;
            } catch (Throwable t) {
                debug("Something went wrong while loading " + file.getName() + " :(");
                debug("--== Error ==--");
                t.printStackTrace();
                debug("--== Error ==--");
                debug();
            }
            debug("Loaded  " + i + " mines :D");
        }
        long minute = 60 * 20L;
        //Register commands
        registerBaseCommands();
        //Start timer
        resetId = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for (Mine mine : MineManager.getMines()) {
                    mine.tictoc();
                }
            }
        }, minute, minute);

    }

    public void setupDependencies() {
        //Try to get dependencies
        if (getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
            WEP = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
        }
        if (WEP == null) {
            debug("World edit is need for this plugin to work! :(");
            debug("Please download and install it for it to work!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        //HolographicDisplays
        if (getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
            debug();
            debug("Detected HolographicDisplays!");
            setHologramsUsable();
            useHolograms = pluginConfig.getBoolean("global.useHolographicDisplays");
            forceDefaultHolograms = pluginConfig.getBoolean("global.forceDefaultHologramOnAllMines");
            if (useHolograms) {
                debug("Holograms enabled!");
                setForceHologramsUse(forceDefaultHolograms);
                if (forceDefaultHolograms) {
                    debug("Forcing allHolograms to use default preset.");
                }
            } else {
                debug("Holograms are usable, but not enabled.");
            }
            debug();
        }
    }
}
