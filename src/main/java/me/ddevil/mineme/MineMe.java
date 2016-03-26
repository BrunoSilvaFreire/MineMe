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

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import java.io.File;
import java.io.IOException;
import java.util.List;
import me.ddevil.core.CustomPlugin;
import me.ddevil.core.thread.FinishListener;
import me.ddevil.mineme.commands.MineCommand;
import me.ddevil.mineme.conversion.MRLConverter;
import me.ddevil.mineme.gui.GUIManager;
import me.ddevil.mineme.holograms.HologramAdapter;
import me.ddevil.mineme.mines.HologramCompatible;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.mines.MineManager;
import me.ddevil.mineme.thread.PluginLoader;
import me.ddevil.mineme.utils.MVdWPlaceholderManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class MineMe extends CustomPlugin {

    //Configs
    public static FileConfiguration pluginConfig;
    public static FileConfiguration guiConfig;
    public static FileConfiguration messagesConfig;
    public static File pluginFolder;
    public static File minesFolder;
    //Storage
    public static File storageFolder;
    //World Edit
    public static WorldEditPlugin WEP;
    //Scheduler ids
    public static Integer timerID;
    public static Integer hologramUpdaterID;
    //MineMe
    public static boolean useHolograms = false;
    public static boolean forceDefaultBroadcastMessage = true;
    public static boolean forceDefaultHolograms = false;
    public static boolean useHologramMineSync = false;
    public static long hologramRefreshRate;

    public static MineMe getInstance() {
        return (MineMe) instance;
    }
    //Holograms
    public static HologramAdapter hologramAdapter;
    public static List<String> defaultHologramText;

    //HolographicDisplays
    public static boolean useHolographicDisplay;
    public static boolean holographicDisplaysUsable = false;
    //MVdW
    public static boolean useMVdWPlaceholderAPI = false;
    //MineResetLite
    public static boolean convertMineResetLite;

    @Override
    public void onEnable() {
        super.onEnable();
        PluginLoader pLoader = new PluginLoader();
        pLoader.start();
        pLoader.addListener(new FinishListener() {

            @Override
            public void onFinish() {
                //Register commands and World Edit
                WorldEdit.getInstance().getEventBus().register(new me.ddevil.mineme.mines.MineManager.WorldEditManager());
                registerBaseCommands();
                if (useMVdWPlaceholderAPI) {
                    try {
                        MVdWPlaceholderManager.setupPlaceholders();
                    } catch (Exception ex) {
                        printException("There was an error creting MVdWPlaceholder! Skipping", ex);
                    }
                }
                if (convertMineResetLite) {
                    MineMe.instance.debug("Converting MineResetLite...", true);
                    MRLConverter.convert();
                    MineMe.instance.debug("Converted MineResetLite!", true);
                }
                debug("Starting metrics...", 3);
                try {
                    Metrics metrics = new Metrics(MineMe.this);
                    metrics.start();
                    debug("Metrics started!", 3);
                } catch (IOException ex) {
                    printException("There was an error start metrics! Skipping", ex);
                }
                Bukkit.getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {

                    @Override
                    public void run() {
                        debug("Starting MineEditorGUI...", 3);
                        GUIManager.setup();
                        debug("MineEditorGUI started!", 3);
                    }
                }, 2l);
            }
        });
    }

    @Override
    public void onDisable() {
        unloadEverything();
        MineManager.saveAll();
    }

    public static FileConfiguration getYAMLMineFile(Mine m) {
        return YamlConfiguration.loadConfiguration(getMineFile(m));
    }

    public static File getMineFile(Mine m) {
        return new File(minesFolder.getPath(), m.getName() + ".yml");
    }

    public static void setHologramsUsable(boolean hologramsUsable) {
        MineMe.holographicDisplaysUsable = hologramsUsable;
    }

    public static void setForceHologramsUse(boolean forceHologramsUse) {
        MineMe.forceDefaultHolograms = forceHologramsUse;
    }

    public static boolean isForceHologramsUse() {
        return forceDefaultHolograms;
    }

    private void registerBaseCommands() {
        Bukkit.getScheduler().runTask(this, new Runnable() {

            @Override
            public void run() {
                registerCommand(new MineCommand());
            }
        });
    }

    public void reload(Player p) {
        messageManager.sendMessage(p, "Reloading config...");
        debug("Stopping reseter task...");
        Bukkit.getScheduler().cancelTask(timerID);
        debug("Unloading...");
        unloadEverything();
        PluginLoader l = new PluginLoader();
        l.start();
        l.addListener(new FinishListener() {

            @Override
            public void onFinish() {
                debug("Reload complete!");
                messageManager.sendMessage(p, "Reloaded! :D");
            }
        });
    }

    private void unloadEverything() {
        if (useHolograms) {
            for (Hologram h : HologramsAPI.getHolograms(this)) {
                h.delete();
            }
        }
        MineManager.unregisterMines();
        pluginConfig = null;
        pluginFolder = null;
        messagesConfig = null;
        timerID = null;
    }

    public static void startTimers() {
        if (MineMe.timerID != null) {
            Bukkit.getScheduler().cancelTask(MineMe.timerID);
            timerID = null;
        }
        if (MineMe.hologramUpdaterID != null) {
            Bukkit.getScheduler().cancelTask(MineMe.hologramUpdaterID);
            hologramUpdaterID = null;
        }
        MineMe.hologramRefreshRate = ((Integer) MineMe.pluginConfig.getInt("settings.holograms.customHologramRefreshRate")).longValue();
        MineMe.useHologramMineSync = MineMe.pluginConfig.getBoolean("settings.holograms.useHologramResetSync");

        if (MineMe.useHolograms) {
            //Start timer
            if (useHologramMineSync) {
                instance.debug("Setting up hologram updater syncronized with mine reseter.", 3);
                MineMe.timerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
                    @Override
                    public void run() {
                        for (Mine mine : MineManager.getMines()) {
                            mine.secondCountdown();
                            if (mine instanceof HologramCompatible) {
                                HologramCompatible hc = (HologramCompatible) mine;
                                hc.updateHolograms();
                            }
                        }
                    }
                }, 20l, 20l);
            } else {
                MineMe.timerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
                    @Override
                    public void run() {
                        for (Mine mine : MineManager.getMines()) {
                            mine.secondCountdown();
                        }
                    }
                }, 20l, 20l);
                instance.debug("Setting up hologram updater @ " + hologramRefreshRate + " refresh speed.", 3);
                MineMe.hologramUpdaterID = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
                    long last = System.currentTimeMillis();

                    @Override
                    public void run() {
                        long now = System.currentTimeMillis();
                        for (Mine mine : MineManager.getMines()) {
                            if (mine instanceof HologramCompatible) {
                                HologramCompatible compatible = (HologramCompatible) mine;
                                compatible.updateHolograms();
                            }
                        }
                        last = now;
                    }
                }, MineMe.hologramRefreshRate, MineMe.hologramRefreshRate);
            }
        } else {
            MineMe.timerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
                @Override
                public void run() {
                    for (Mine mine : MineManager.getMines()) {
                        mine.secondCountdown();
                    }
                }
            }, 20l, 20l);
        }
    }
}
