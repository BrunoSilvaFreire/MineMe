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
package me.ddevil.mineme.utils;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import java.util.ArrayList;
import me.ddevil.core.utils.StringUtils;
import me.ddevil.mineme.MineMe;
import static me.ddevil.mineme.MineMe.useMVdWPlaceholderAPI;
import me.ddevil.mineme.messages.MineMeMessageManager;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.mines.MineManager;
import me.ddevil.mineme.storage.StorageManager;
import org.bukkit.entity.Player;

/**
 *
 * @author Selma
 */
public class MVdWPlaceholderManager {

    public static void setupPlaceholders() throws Exception {
        if (useMVdWPlaceholderAPI) {
            MineMe.instance.debug("Registering MVdW placeholders...", true);
            //Players placeholders
            PlaceholderAPI.registerPlaceholder(MineMe.instance, "minememine", new PlaceholderReplacer() {
                @Override
                public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                    Player p = e.getPlayer();
                    return !MineManager.isPlayerInAMine(p) ? "" : MineManager.getMineWith(p).getAlias();
                }
            });
            PlaceholderAPI.registerPlaceholder(MineMe.instance, "minememineremaining", new PlaceholderReplacer() {
                @Override
                public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                    Player p = e.getPlayer();
                    return !MineManager.isPlayerInAMine(p) ? "" : String.valueOf(MineManager.getMineWith(p).getRemainingBlocks());
                }
            });
            PlaceholderAPI.registerPlaceholder(MineMe.instance, "minememineresettime", new PlaceholderReplacer() {
                @Override
                public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                    Player p = e.getPlayer();
                    return !MineManager.isPlayerInAMine(p) ? "" : String.valueOf(StringUtils.secondsToString(MineManager.getMineWith(p).getTimeToNextReset()));
                }
            });
            PlaceholderAPI.registerPlaceholder(MineMe.instance, "minememinemined", new PlaceholderReplacer() {
                @Override
                public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                    Player p = e.getPlayer();
                    return !MineManager.isPlayerInAMine(p) ? "" : String.valueOf(MineManager.getMineWith(p).getMinedBlocks());
                }
            });
            PlaceholderAPI.registerPlaceholder(MineMe.instance, "minememinetotalminedblocks", new PlaceholderReplacer() {
                @Override
                public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                    Player p = e.getPlayer();
                    return !MineManager.isPlayerInAMine(p) ? "" : String.valueOf(StorageManager.getTotalBrokenBlocks(MineManager.getMineWith(p)));
                }
            });
            PlaceholderAPI.registerPlaceholder(MineMe.instance, "minememinetotalresets", new PlaceholderReplacer() {
                @Override
                public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                    Player p = e.getPlayer();
                    return !MineManager.isPlayerInAMine(p) ? "" : String.valueOf(StorageManager.getTotalResets(MineManager.getMineWith(p)));
                }
            });
            PlaceholderAPI.registerPlaceholder(MineMe.instance, "minememineremainingpercent", new PlaceholderReplacer() {
                @Override
                public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                    Player p = e.getPlayer();
                    return !MineManager.isPlayerInAMine(p) ? "" : String.valueOf(MineManager.getMineWith(p).getPercentageRemaining());
                }
            });
            PlaceholderAPI.registerPlaceholder(MineMe.instance, "minememineminedpercent", new PlaceholderReplacer() {
                @Override
                public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                    Player p = e.getPlayer();
                    return !MineManager.isPlayerInAMine(p) ? "" : String.valueOf(MineManager.getMineWith(p).getPercentageMined());
                }
            });
            for (Mine m : MineManager.getMines()) {
                //Register mines placeholders
                registerMinePlaceholders(m);
            }
            MineMe.instance.debug("Placeholders registered!", true);
        }
    }

    public static boolean isPlaceholderRegistered(Mine m) {
        return registeredMines.contains(m);
    }
    private final static ArrayList<Mine> registeredMines = new ArrayList();

    public static void registerMinePlaceholders(final Mine m) {
        PlaceholderAPI.registerPlaceholder(MineMe.instance, "mineme:" + m.getName() + ":remaining", new PlaceholderReplacer() {
            @Override
            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                return String.valueOf(m.getRemainingBlocks());
            }
        });
        PlaceholderAPI.registerPlaceholder(MineMe.instance, "mineme:" + m.getName() + ":mined", new PlaceholderReplacer() {
            @Override
            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                return String.valueOf(m.getMinedBlocks());
            }
        });
        PlaceholderAPI.registerPlaceholder(MineMe.instance, "mineme:" + m.getName() + ":totalminedblocks", new PlaceholderReplacer() {
            @Override
            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                return String.valueOf(StorageManager.getTotalBrokenBlocks(m));
            }
        });
        PlaceholderAPI.registerPlaceholder(MineMe.instance, "mineme:" + m.getName() + ":totalresets", new PlaceholderReplacer() {
            @Override
            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                return String.valueOf(StorageManager.getTotalResets(m));
            }
        });
        PlaceholderAPI.registerPlaceholder(MineMe.instance, "mineme:" + m.getName() + ":remainingpercent", new PlaceholderReplacer() {
            @Override
            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                return String.valueOf(m.getPercentageRemaining());
            }
        });
        PlaceholderAPI.registerPlaceholder(MineMe.instance, "mineme:" + m.getName() + ":resettime", new PlaceholderReplacer() {
            @Override
            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                return StringUtils.secondsToString(m.getTimeToNextReset());
            }
        });
        PlaceholderAPI.registerPlaceholder(MineMe.instance, "mineme:" + m.getName() + ":minedpercent", new PlaceholderReplacer() {
            @Override
            public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                return String.valueOf(m.getPercentageMined());
            }
        });
        registeredMines.add(m);
    }

}
