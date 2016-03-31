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
package me.ddevil.mineme.gui;

import java.util.Arrays;
import java.util.List;
import me.ddevil.core.exceptions.ItemConversionException;
import me.ddevil.core.utils.items.ItemUtils;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.gui.impl.BasicMineEditorGUI;
import me.ddevil.mineme.messages.MineMeMessageManager;
import me.ddevil.mineme.mines.Mine;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Selma
 */
public class GUIManager {

    public static MineEditorGUI mineEditorGUI;
    public static boolean ready = false;

    public static void setup() {
        //Load Strings
        GUIResourcesUtils.clickToSee = MineMeMessageManager.getInstance().translateAll(MineMe.guiConfig.getString("config.clickToSeeMine"));
        GUIResourcesUtils.clickToRemove = MineMeMessageManager.getInstance().translateAll(MineMe.guiConfig.getString("config.clickToRemove"));
        GUIResourcesUtils.clickToEdit = MineMeMessageManager.getInstance().translateAll(MineMe.guiConfig.getString("config.clickToEdit"));
        //Load items
        //Load splitter
        GUIResourcesUtils.splitter = loadFromConfig(MineMe.guiConfig.getConfigurationSection("globalItems.splitter"));
        //Load backitem
        GUIResourcesUtils.backButton = loadFromConfig(MineMe.guiConfig.getConfigurationSection("globalItems.back"));
        //Load removeMaterial
        GUIResourcesUtils.removeButton = loadFromConfig(MineMe.guiConfig.getConfigurationSection("globalItems.removeMaterial"));
        //Load emptyitem
        GUIResourcesUtils.empty = loadFromConfig(MineMe.guiConfig.getConfigurationSection("globalItems.empty"));
        //Load teleporter
        GUIResourcesUtils.teleporter = loadFromConfig(MineMe.guiConfig.getConfigurationSection("globalItems.teleporter"));
        //Load info item
        ConfigurationSection infoConfig = MineMe.guiConfig.getConfigurationSection("globalItems.info");
        GUIResourcesUtils.information = loadFromConfig(infoConfig);
        GUIResourcesUtils.infomationLore = infoConfig.getStringList("lore");
        //Load reset item
        GUIResourcesUtils.resetButton = loadFromConfig(MineMe.guiConfig.getConfigurationSection("globalItems.resetNow"));
        //Load reset item
        GUIResourcesUtils.deleteMineButton = loadFromConfig(MineMe.guiConfig.getConfigurationSection("globalItems.deleteMine"));
        GUIResourcesUtils.clearMaterials = loadFromConfig(MineMe.guiConfig.getConfigurationSection("globalItems.clearMaterials"));
        //Load mainMenu
        ConfigurationSection mainMenu = MineMe.guiConfig.getConfigurationSection("mainMenu");
        String mainMenuName = MineMeMessageManager.getInstance().translateAll(mainMenu.getString("name"));
        int mainMenuSize = mainMenu.getInt("totalLanes") * 9;
        //Load mineMenu
        ConfigurationSection mineMenu = MineMe.guiConfig.getConfigurationSection("mineMenu");
        int mineMenuSize = mineMenu.getInt("totalLanes") * 9;
        MineMe.instance.debug();
        MineMe.instance.debug("Loading MEGUI's mainMenu with size " + mainMenuSize + " and title " + mainMenuName, 3);
        MineMe.instance.debug("Loading MEGUI's mineMenu with size " + mineMenuSize, 3);
        MineMe.instance.debug();
        if (mineEditorGUI != null) {
            mineEditorGUI.end();
        }
        mineEditorGUI = new BasicMineEditorGUI(mainMenuName, mainMenuSize, mineMenuSize);
        mineEditorGUI.setup();
        ready = true;
    }

    private static ItemStack loadFromConfig(ConfigurationSection configSection) {
        String configName = configSection.getName();
        boolean containsLore;
        ItemStack i;

        try {
            Material material = Material.valueOf(configSection.getString("type"));
            byte data = ((Integer) configSection.get("data")).byteValue();
            String name = MineMeMessageManager.getInstance().translateAll(configSection.getString("name"));
            containsLore = configSection.contains("lore");
            i = ItemUtils.convertFromInput(material + ":" + data, name);
        } catch (ItemConversionException ex) {
            MineMe.instance.printException("There was a problem loading GUIItem reset, is it configured correctly?", ex);
            return ItemUtils.createItem(Material.TNT, "§4Error", MineMeMessageManager.getInstance().translateAll(
                    Arrays.asList(new String[]{
                        "§4There was a problem loading GUIItem §1" + configName, "§4Is it configured correctly?"
                    }
                    ))
            );
        }
        if (containsLore) {
            i = ItemUtils.addToLore(i, configSection.getStringList("lore"));
        }
        return i;

    }

    public static void registerItem(String name, ItemStack i) {
        name = name.toUpperCase();
        GUIResourcesUtils.customItems.put(name, i);
    }

    public static ItemStack getItem(String name) {
        return GUIResourcesUtils.customItems.get(name);
    }

    public static ItemStack getItem(String name, Mine m) {
        return GUIResourcesUtils.customItems.get(name);
    }

    public static ItemStack getMineIcon(Mine m) {
        return null;
    }

}
