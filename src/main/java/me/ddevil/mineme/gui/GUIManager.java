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

import me.ddevil.core.utils.items.ItemUtils;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.MineMeConfiguration;
import me.ddevil.mineme.gui.menus.MainMenu;
import me.ddevil.mineme.messages.MineMeMessageManager;
import me.ddevil.mineme.mines.Mine;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Selma
 */
public class GUIManager {

    public static boolean ready = false;
    public static MainMenu mainMenu;

    public static void setup() {
        ConfigurationSection mmConfig = MineMeConfiguration.guiConfig.getConfigurationSection("mainMenu");
        String mainMenuName = MineMeMessageManager.getInstance().translateAll(mmConfig.getString("name"));
        MineMe.instance.debug();
        MineMe.instance.debug("Loading MEGUI's mainMenu title " + mainMenuName, 3);
        MineMe.instance.debug();
        if (GUIManager.mainMenu != null) {
            GUIManager.mainMenu.disable();
        }
        GUIManager.mainMenu = new MainMenu(mainMenuName);
        GUIManager.mainMenu.initialSetup();
        ready = true;
    }

    private static ItemStack loadFromConfig(ConfigurationSection configSection) {
        return ItemUtils.createItem(configSection);
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
