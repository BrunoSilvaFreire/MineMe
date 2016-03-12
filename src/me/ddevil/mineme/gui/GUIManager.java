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

import java.util.List;
import me.ddevil.core.utils.ItemUtils;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.exception.ItemConversionException;
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
        GUIResourcesUtils.clickToSee = MineMeMessageManager.translateTagsAndColor(MineMe.guiConfig.getString("config.clickToSeeMine"));
        GUIResourcesUtils.clickToRemove = MineMeMessageManager.translateTagsAndColor(MineMe.guiConfig.getString("config.clickToRemove"));
        GUIResourcesUtils.clickToEdit = MineMeMessageManager.translateTagsAndColor(MineMe.guiConfig.getString("config.clickToEdit"));
        GUIResourcesUtils.dropAddMaterial = MineMeMessageManager.translateTagsAndColor(MineMe.guiConfig.getString("config.dropAddMaterial"));
        //Load items
        //Load splitter
        ConfigurationSection splitterConfig = MineMe.guiConfig.getConfigurationSection("globalItems.splitter");
        Material splitterMaterial = Material.valueOf(splitterConfig.getString("type"));
        String splitterName = MineMeMessageManager.translateTagsAndColor(splitterConfig.getString("name"));
        byte splitterData = ((Integer) splitterConfig.get("data")).byteValue();
        try {
            GUIResourcesUtils.splitter = ItemUtils.convertFromInput(splitterMaterial + ":" + splitterData, splitterName);
        } catch (ItemConversionException ex) {
            MineMe.instance.printException("There was a problem loading GUIItem splitter, is it configured correctly?", ex);
        }
        //Load backitem
        ConfigurationSection backButtonConfig = MineMe.guiConfig.getConfigurationSection("globalItems.back");
        Material backMaterial = Material.valueOf(backButtonConfig.getString("type"));
        String backName = MineMeMessageManager.translateTagsAndColor(backButtonConfig.getString("name"));
        byte backData = ((Integer) backButtonConfig.get("data")).byteValue();

        try {
            GUIResourcesUtils.backButton = ItemUtils.convertFromInput(backMaterial + ":" + backData, backName);
        } catch (ItemConversionException ex) {
            MineMe.instance.printException("There was a problem loading GUIItem backButton, is it configured correctly?", ex);
        }
        //Load removeMaterial
        ConfigurationSection removeConfig = MineMe.guiConfig.getConfigurationSection("globalItems.removeMaterial");
        Material removeMaterial = Material.valueOf(removeConfig.getString("type"));
        String removeName = MineMeMessageManager.translateTagsAndColor(removeConfig.getString("name"));
        byte removeData = ((Integer) removeConfig.get("data")).byteValue();
        try {
            GUIResourcesUtils.removeButton = ItemUtils.convertFromInput(removeMaterial + ":" + removeData, removeName);
        } catch (ItemConversionException ex) {
            MineMe.instance.printException("There was a problem loading GUIItem removeButton, is it configured correctly?", ex);
        }
        //Load emptyitem
        ConfigurationSection emptyItemConfig = MineMe.guiConfig.getConfigurationSection("globalItems.empty");
        Material emptyMaterial = Material.valueOf(emptyItemConfig.getString("type"));
        byte emptyData = ((Integer) emptyItemConfig.get("data")).byteValue();

        String emptyName = MineMeMessageManager.translateTagsAndColor(emptyItemConfig.getString("name"));
        try {
            GUIResourcesUtils.empty = ItemUtils.convertFromInput(emptyMaterial + ":" + emptyData, emptyName);
            ItemUtils.addToLore(GUIResourcesUtils.empty, GUIResourcesUtils.dropAddMaterial);
        } catch (ItemConversionException ex) {
            MineMe.instance.printException("There was a problem loading GUIItem empty, is it configured correctly?", ex);
        }
        //Load teleporter
        ConfigurationSection teleporterConfig = MineMe.guiConfig.getConfigurationSection("globalItems.teleporter");
        Material teleporterMaterial = Material.valueOf(teleporterConfig.getString("type"));
        byte teleporterData = ((Integer) teleporterConfig.get("data")).byteValue();

        String teleporterName = MineMeMessageManager.translateTagsAndColor(teleporterConfig.getString("name"));
        try {
            GUIResourcesUtils.teleporter = ItemUtils.convertFromInput(teleporterMaterial + ":" + teleporterData, teleporterName);
        } catch (ItemConversionException ex) {
            MineMe.instance.printException("There was a problem loading GUIItem teleporter, is it configured correctly?", ex);
        }
        //Load info item
        ConfigurationSection infoConfig = MineMe.guiConfig.getConfigurationSection("globalItems.info");
        Material infoMaterial = Material.valueOf(infoConfig.getString("type"));
        byte infoData = ((Integer) infoConfig.get("data")).byteValue();

        String infoName = MineMeMessageManager.translateTagsAndColor(infoConfig.getString("name"));
        try {
            GUIResourcesUtils.information = ItemUtils.convertFromInput(infoMaterial + ":" + infoData, infoName);
            List<String> ilore = infoConfig.getStringList("lore");
            GUIResourcesUtils.infomationLore = ilore.toArray(new String[ilore.size()]);
        } catch (ItemConversionException ex) {
            MineMe.instance.printException("There was a problem loading GUIItem info, is it configured correctly?", ex);
        }
        //Load mainMenu
        ConfigurationSection mainMenu = MineMe.guiConfig.getConfigurationSection("mainMenu");
        String mainMenuName = MineMeMessageManager.translateTagsAndColor(mainMenu.getString("name"));
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
