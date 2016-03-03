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

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import be.maximvdw.placeholderapi.internal.MVdWPlaceholderReplacer;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import java.io.File;
import java.util.List;
import me.ddevil.core.CustomPlugin;
import me.ddevil.core.thread.FinishListener;
import me.ddevil.mineme.commands.MineCommand;
import me.ddevil.mineme.conversion.MRLConverter;
import me.ddevil.mineme.holograms.HologramAdapter;
import me.ddevil.mineme.messages.MineMeMessageManager;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.mines.MineManager;
import me.ddevil.mineme.storage.StorageManager;
import me.ddevil.mineme.thread.PluginLoader;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class MineMe extends CustomPlugin {

    //Configs
    public static FileConfiguration pluginConfig;
    public static FileConfiguration messagesConfig;
    public static File pluginFolder;
    public static File minesFolder;
    //Storage
    public static File storageFolder;
    //World Edit
    public static WorldEditPlugin WEP;
    //MineMe
    public static Integer resetId;
    public static boolean useHolograms = false;
    public static boolean forceDefaultBroadcastMessage = true;
    public static boolean forceDefaultHolograms = false;

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
                debug("It's all right, it's all favorable :D");
                if (useMVdWPlaceholderAPI) {
                    debug("Registering MVdW placeholders...", true);
                    //Players placeholders
                    PlaceholderAPI.registerPlaceholder(MineMe.this, "minememine", new PlaceholderReplacer() {
                        @Override
                        public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                            Player p = e.getPlayer();
                            return !MineManager.isPlayerInAMine(p) ? "" : MineManager.getMineWith(p).getAlias();
                        }
                    });
                    PlaceholderAPI.registerPlaceholder(MineMe.this, "minememineremaining", new PlaceholderReplacer() {
                        @Override
                        public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                            Player p = e.getPlayer();
                            return !MineManager.isPlayerInAMine(p) ? "" : String.valueOf(MineManager.getMineWith(p).getRemainingBlocks());
                        }
                    });
                    PlaceholderAPI.registerPlaceholder(MineMe.this, "minememineresettime", new PlaceholderReplacer() {
                        @Override
                        public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                            Player p = e.getPlayer();
                            return !MineManager.isPlayerInAMine(p) ? "" : String.valueOf(MineMeMessageManager.secondsToString(MineManager.getMineWith(p).getTimeToNextReset()));
                        }
                    });
                    PlaceholderAPI.registerPlaceholder(MineMe.this, "minememinemined", new PlaceholderReplacer() {
                        @Override
                        public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                            Player p = e.getPlayer();
                            return !MineManager.isPlayerInAMine(p) ? "" : String.valueOf(MineManager.getMineWith(p).getMinedBlocks());
                        }
                    });
                    PlaceholderAPI.registerPlaceholder(MineMe.this, "minememinetotalminedblocks", new PlaceholderReplacer() {
                        @Override
                        public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                            Player p = e.getPlayer();
                            return !MineManager.isPlayerInAMine(p) ? "" : String.valueOf(StorageManager.getTotalBrokenBlocks(MineManager.getMineWith(p)));
                        }
                    });
                    PlaceholderAPI.registerPlaceholder(MineMe.this, "minememinetotalresets", new PlaceholderReplacer() {
                        @Override
                        public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                            Player p = e.getPlayer();
                            return !MineManager.isPlayerInAMine(p) ? "" : String.valueOf(StorageManager.getTotalResets(MineManager.getMineWith(p)));
                        }
                    });
                    PlaceholderAPI.registerPlaceholder(MineMe.this, "minememineremainingpercent", new PlaceholderReplacer() {
                        @Override
                        public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                            Player p = e.getPlayer();
                            return !MineManager.isPlayerInAMine(p) ? "" : String.valueOf(MineManager.getMineWith(p).getPercentageRemaining());
                        }
                    });
                    PlaceholderAPI.registerPlaceholder(MineMe.this, "minememineminedpercent", new PlaceholderReplacer() {
                        @Override
                        public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                            Player p = e.getPlayer();
                            return !MineManager.isPlayerInAMine(p) ? "" : String.valueOf(MineManager.getMineWith(p).getPercentageMined());
                        }
                    });
                    for (Mine m : MineManager.getMines()) {
                        //Register mines placeholders
                        PlaceholderAPI.registerPlaceholder(MineMe.this, "mineme:" + m.getName() + ":remaining", new PlaceholderReplacer() {
                            @Override
                            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                                return String.valueOf(m.getRemainingBlocks());
                            }
                        });
                        PlaceholderAPI.registerPlaceholder(MineMe.this, "mineme:" + m.getName() + ":mined", new PlaceholderReplacer() {
                            @Override
                            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                                return String.valueOf(m.getMinedBlocks());
                            }
                        });
                        PlaceholderAPI.registerPlaceholder(MineMe.this, "mineme:" + m.getName() + ":totalminedblocks", new PlaceholderReplacer() {
                            @Override
                            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                                return String.valueOf(StorageManager.getTotalBrokenBlocks(m));
                            }
                        });
                        PlaceholderAPI.registerPlaceholder(MineMe.this, "mineme:" + m.getName() + ":totalresets", new PlaceholderReplacer() {
                            @Override
                            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                                return String.valueOf(StorageManager.getTotalResets(m));
                            }
                        });
                        PlaceholderAPI.registerPlaceholder(MineMe.this, "mineme:" + m.getName() + ":remainingpercent", new PlaceholderReplacer() {
                            @Override
                            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                                return String.valueOf(m.getPercentageRemaining());
                            }
                        });
                        PlaceholderAPI.registerPlaceholder(MineMe.this, "mineme:" + m.getName() + ":resettime", new PlaceholderReplacer() {
                            @Override
                            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                                return MineMeMessageManager.secondsToString(m.getTimeToNextReset());
                            }
                        });
                        PlaceholderAPI.registerPlaceholder(MineMe.this, "mineme:" + m.getName() + ":minedpercent", new PlaceholderReplacer() {
                            @Override
                            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                                return String.valueOf(m.getPercentageMined());
                            }
                        });
                    }
                    debug("Placeholders registered!", true);
                }
                if (convertMineResetLite) {
                    MineMe.instance.debug("Converting MineResetLite...", true);
                    MRLConverter.convert();
                    MineMe.instance.debug("Converted MineResetLite!", true);

                }
            }
        });

    }

    @Override
    public void onDisable() {
        unloadEverything();
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
        registerCommand(new MineCommand());
    }

    public void reload(Player p) {
        messageManager.sendMessage(p, "Reloading config...");
        debug("Stopping reseter task...");
        Bukkit.getScheduler().cancelTask(resetId);
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
        resetId = null;
    }

}
