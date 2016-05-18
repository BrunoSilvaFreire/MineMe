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
import java.io.FileWriter;
import java.io.IOException;
import me.ddevil.core.chat.ColorDesign;
import me.ddevil.core.chat.PluginChatManager;
import me.ddevil.core.utils.FileUtils;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.MineMeConfiguration;
import me.ddevil.mineme.holograms.impl.HolographicDisplaysAdapter;
import me.ddevil.mineme.messages.MineMeMessageManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

public class PluginLoader {

    private static final MineMe plugin = MineMe.getInstance();

    public static void load() {
        long startms = System.currentTimeMillis();
        plugin.debug("Loading config...");
        setupConfig();
        plugin.debug("Looking for extra dependencies...");
        setupDependencies();
        plugin.debug("Loading Plugin...");
        setupPlugin();
        long endms = System.currentTimeMillis();
        long startupTime = endms - startms;
        plugin.debug("Plugin loaded after " + (startupTime / 1000) + " seconds(" + startupTime + "ms)!", true);
    }

    private static void setupConfig() {
        MineMe.pluginFolder = plugin.getDataFolder();
        if (!MineMe.pluginFolder.exists()) {
            plugin.debug("Plugin folder not found! Making one...", 3);
            MineMe.pluginFolder.mkdir();
        }
        File pluginconfig = new File(plugin.getDataFolder(), "config.yml");
        if (!pluginconfig.exists()) {
            //Load from plugin
            plugin.debug("Config file not found! Making one...", 3);
            MineMe.instance.loadResource(pluginconfig, "config.yml");
        }
        MineMe.pluginConfig = YamlConfiguration.loadConfiguration(pluginconfig);
        File guiconfig = new File(plugin.getDataFolder(), "guiconfig.yml");
        if (!guiconfig.exists()) {
            //Load from plugin
            plugin.debug("GUI config file not found! Making one...", 3);
            MineMe.instance.loadResource(guiconfig, "guiconfig.yml");
        }
        MineMeConfiguration.guiConfig = YamlConfiguration.loadConfiguration(guiconfig);
        plugin.minimumDebugPriotity = MineMe.pluginConfig.getInt("settings.minimumDebugLevel");
        MineMe.storageFolder = new File(MineMe.pluginFolder.getPath(), "storage");
        if (!MineMe.storageFolder.exists()) {
            plugin.debug("Storage folder not found! Making one...", 3);
            MineMe.storageFolder.mkdir();
            //Make readme
            File readme = new File(MineMe.storageFolder, "README.txt");
            try (FileWriter fileWriter = new FileWriter(readme)) {
                fileWriter.write(
                        "Whatever you do DON'T EDIT ANY FILE IN HERE!" + System.getProperty("line.separator")
                        + "These are encrypted files, and if you change anything here the plugin won't be able to load the info it needs!" + System.getProperty("line.separator")
                        + "k thx lov u bai <3");
                fileWriter.close();
            } catch (Exception e) {
                plugin.printException("There was an error creating the README file!", e);
            }
        }
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
        MineMeConfiguration.messagesConfig = YamlConfiguration.loadConfiguration(messages);
        MineMeConfiguration.forceDefaultBroadcastMessage = MineMeConfiguration.messagesConfig.getBoolean("global.forceDefaultBroadcastMessage");
        MineMe.convertMineResetLite = MineMe.pluginConfig.getBoolean("global.convertFromMineResetLite");
    }

    public static void setupDependencies() {
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
        MineMe.useMVdWPlaceholderAPI = Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI");
        //Holograms
        MineMeConfiguration.useHolograms = MineMe.pluginConfig.getBoolean("settings.holograms.enableHolograms");
        //Holographic Displays
        MineMeConfiguration.forceDefaultHolograms = MineMe.pluginConfig.getBoolean("global.forceDefaultHologramOnAllMines");
        MineMe.useHolographicDisplay = MineMe.pluginConfig.getBoolean("settings.holograms.useHolographicDisplaysAPI");
        if (MineMeConfiguration.useHolograms) {
            //Check if HD is available
            MineMe.holographicDisplaysUsable = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");
            //useHolographicDisplaysAPI is true in config
            if (MineMe.useHolographicDisplay) {
                if (MineMe.holographicDisplaysUsable) {
                    plugin.debug();
                    plugin.debug("Detected HolographicDisplays!", 3);
                    plugin.debug("Using Holographic Displays as the hologram adapter...", 3);
                    //Set the hologram adapter
                    MineMe.hologramAdapter = new HolographicDisplaysAdapter();
                    if (MineMeConfiguration.forceDefaultHolograms) {
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
                        MineMe.instance.printException("There was a error fixing HolographicDisplays in the config!", ex);
                    }
                }
            }
            //Check hologram adapter
            if (MineMe.hologramAdapter == null) {
                MineMeConfiguration.useHolograms = false;
                plugin.debug("Holograms were set enabled, but not HologramAdapter was setup. Are you sure you enabled an API in the config? Disabling holograms...", 3);
                try {
                    MineMe.pluginConfig.set("settings.holograms.enableHolograms", false);
                    MineMe.pluginConfig.save(new File(plugin.getDataFolder(), "config.yml"));
                } catch (IOException ex) {
                    MineMe.instance.printException("There was an error correcting enableHolograms in the config!", ex);
                }

            }
        }
    }

    private static void setupPlugin() {
        //Get mines folder
        MineMe.minesFolder = new File(plugin.getDataFolder(), "mines");
        if (!MineMe.minesFolder.exists()) {
            plugin.debug("Mines folder not found! Making one...", 3);
            MineMe.minesFolder.mkdir();
        }
        //Check if mines folder is empty
        if (MineMe.minesFolder.listFiles().length == 0) {
            plugin.debug("Mines folder is empty! Adding examplemines...", 3);
            plugin.saveResource("examplemine.yml", false);
            plugin.saveResource("examplecircularmine.yml", false);
            plugin.saveResource("examplepoligonalmine.yml", false);
            File examplecircularmine = new File(plugin.getDataFolder() + "/examplecircularmine.yml");
            File examplecuboidmine = new File(plugin.getDataFolder() + "/examplemine.yml");
            File examplepoligonalmine = new File(plugin.getDataFolder() + "/examplepoligonalmine.yml");
            try {
                FileUtils.moveFileToDirectory(examplecuboidmine, MineMe.minesFolder);
                plugin.debug("examplemine.yml added!", 3);
                FileUtils.moveFileToDirectory(examplecircularmine, MineMe.minesFolder);
                plugin.debug("examplecircularmine.yml added!", 3);
                FileUtils.moveFileToDirectory(examplepoligonalmine, MineMe.minesFolder);
                plugin.debug("examplepoligonalmine.yml added!", 3);
            } catch (SecurityException | IOException ex) {
                plugin.printException("There was a problem trying to copy the example mines to the mines folder. Skipping.", ex);
            }
        }
        if (MineMeConfiguration.useHolograms && MineMe.hologramAdapter == null) {
            plugin.debug("Holograms were enabled in the config, but we didn't find an hologram adapter! Fixing...", true);
            MineMe.pluginConfig.set("settings.holograms.enableHolograms", false);
            MineMeConfiguration.useHolograms = false;
        }
        MineMe.chatManager = (PluginChatManager) PluginChatManager.getInstance(plugin).setup();
        MineMe.messageManager = (MineMeMessageManager) MineMeMessageManager.getInstance().setup();
        MineMe.defaultColorDesign = new ColorDesign('a', 'e', '7', 'c');
        plugin.debug();
        plugin.debug("Starting timer...", 2);
        MineMe.startTimers();
        plugin.debug("Timer started!", 2);

    }

}
