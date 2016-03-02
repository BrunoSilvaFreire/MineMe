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
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import java.io.File;
import java.io.IOException;
import java.util.List;
import me.ddevil.core.CustomPlugin;
import me.ddevil.core.thread.FinishListener;
import me.ddevil.mineme.commands.MineCommand;
import me.ddevil.mineme.holograms.HologramAdapter;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.mines.MineManager;
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
    //World Edit
    public static WorldEditPlugin WEP;
    //MineMe
    public static Integer resetId;
    public static boolean useHolograms = false;
    public static boolean forceDefaultBroadcastMessage = true;
    public static boolean forceDefaultHolograms = false;
    //Holograms
    public static HologramAdapter hologramAdapter;
    public static List<String> defaultHologramText;

    //HolographicDisplays
    public static boolean useHolographicDisplay;
    public static boolean holographicDisplaysUsable = false;

    public static MineMe getInstance() {
        return (MineMe) instance;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        PluginLoader pLoader = new PluginLoader();
        pLoader.start();
        pLoader.addListener(new FinishListener() {

            @Override
            public void onFinish() {
                //Register commands
                registerBaseCommands();

                debug("Plugin loaded!");
                debug("It's all right, it's all favorable :D");
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
