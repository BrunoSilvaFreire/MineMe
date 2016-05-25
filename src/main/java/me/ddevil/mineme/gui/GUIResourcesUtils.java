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

import java.util.HashMap;
import me.ddevil.core.utils.items.ItemUtils;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.MineMeConfiguration;
import me.ddevil.mineme.messages.MineMeMessageManager;
import me.ddevil.mineme.mines.Mine;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Selma
 */
public class GUIResourcesUtils {

    static final HashMap<String, ItemStack> customItems = new HashMap();
    //Global items
    private static final ConfigurationSection ITEMS_SECTION = MineMeConfiguration.guiConfig.getConfigurationSection("globalItems");
    private static final ConfigurationSection STRINGS_SECTION = MineMeConfiguration.guiConfig.getConfigurationSection("config");
    //Placeholders
    public static final ItemStack SPLITTER = loadFromConfig("splitter");
    public static final ItemStack EMPTY_MATERIAL = loadFromConfig("emptyMaterial");
    public static final ItemStack EMPTY_EFFECT = loadFromConfig("emptyEffect");
    public static final ItemStack EMPTY_NEUTRAL = loadFromConfig("empty");
    //Iteraction
    public static final ItemStack BACK_BUTTON = loadFromConfig("back");
    public static final ItemStack TELEPORT_TO_MINE_BUTTON = loadFromConfig("teleporter");
    public static final ItemStack TOOGLE_MATERIALS_EFFECTS_SELECTION = loadFromConfig("toogleMaterialEditor");
    public static final ItemStack FILL_MINE = loadFromConfig("fillMine");
    public static final ItemStack BALANCE_MATERIALS = loadFromConfig("balanceMaterials");
    public static final ItemStack RESET_MATERIALS = loadFromConfig("resetMaterials");
    //Removers
    public static final ItemStack REMOVE_MATERIAL_BUTTON = loadFromConfig("removeMaterial");
    public static final ItemStack RESET_MINE_BUTTON = loadFromConfig("resetNow");
    public static final ItemStack CLEAR_MINE = loadFromConfig("clearMine");
    public static final ItemStack DELETE_MINE_BUTTON = loadFromConfig("deleteMine");
    public static final ItemStack MINE_CLEAR_MATERIALS = loadFromConfig("clearMaterials");
    public static final ItemStack DISABLE_MINE_BUTTON = loadFromConfig("disableMine");
    //Loading
    public static final ItemStack NOT_LOADED_MINES = loadFromConfig("unloadedMines");
    public static final ItemStack NO_MINE_TO_DISPLAY = loadFromConfig("noMine");
    public static final ItemStack NOT_LOADED_ICON = loadFromConfig("iconNotLoaded");
    //Stats
    public static final ItemStack MINE_COMPOSITION_INFORMATION = loadFromConfig("info");
    public static final ItemStack REFRESH_MENU = loadFromConfig("refresh");
    public static final ItemStack FILES_SEARCH_RESULT = loadFromConfig("couldNotLoadFiles");
    //Mine utils
    public static final String FOUND_MINE_FILES = loadStringFromConfig("foundFiles");
    public static final String NO_MISFORMATTED_FILES = loadStringFromConfig("noMisformattedFiles");
    public static final String CLICK_TO_EDIT = loadStringFromConfig("clickToEdit");
    public static final String CLICK_TO_SEE = loadStringFromConfig("clickToSeeMine");
    public static final String CLICK_TO_LOAD = loadStringFromConfig("clickToLoad");
    public static final String CLICK_TO_REMOVE = loadStringFromConfig("clickToRemove");

    public static final int INVENTORY_SIZE = 6;

    public static ItemStack generateNotYetLoadedIcon(Mine m) {
        ItemStack itemStack = new ItemStack(NOT_LOADED_ICON);
        return MineMeMessageManager.getInstance().translateItemStack(itemStack, m);
    }

    public static double getCompositionChangeValue(ItemStack i) {
        if (ItemUtils.checkDisplayName(i)) {
            String intString = i.getItemMeta().
                    getDisplayName().
                    replace("§a+", "")
                    .replace("§c", "").replace("%", "");
            try {
                return Double.valueOf(intString);
            } catch (NumberFormatException e) {
                MineMe.getInstance().printException("There was an error getting the change value of " + intString, e);
                return 0;
            }
        } else {
            MineMe.getInstance().debug("Tried to get the change value of item, but display name is null.", true);
            return 0;
        }
    }

    public static ItemStack generateInformationItem(Mine mine) {
        return MineMeMessageManager.getInstance().translateItemStack(new ItemStack(MINE_COMPOSITION_INFORMATION),
                mine);
    }

    private static String loadStringFromConfig(String configSection) {
        return MineMeMessageManager.getInstance().translateAll(STRINGS_SECTION.getString(configSection));
    }

    private static ItemStack loadFromConfig(String configSection) {
        return ItemUtils.createItem(ITEMS_SECTION.getConfigurationSection(configSection));
    }
}
