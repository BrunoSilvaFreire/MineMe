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
import java.util.HashMap;
import java.util.List;
import me.ddevil.core.utils.items.ItemUtils;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.messages.MineMeMessageManager;
import me.ddevil.mineme.mines.Mine;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Selma
 */
public class GUIResourcesUtils {

    static final HashMap<String, ItemStack> customItems = new HashMap();
    public static String clickToRemove;
    //Global items
    public static ItemStack splitter;
    public static ItemStack empty;
    public static ItemStack removeButton;
    public static ItemStack backButton;
    public static ItemStack teleporter;
    public static ItemStack resetButton;
    public static ItemStack deleteMineButton;
    public static ItemStack information;
    public static ItemStack clearMaterials;
    public static List<String> infomationLore;

    //Mine utils
    public static String mineItemNameFormat;
    public static String clickToEdit;
    public static String clickToSee;

    public static ItemStack generateCompositionItemStack(Mine m, ItemStack i) {
        ItemStack is = new ItemStack(i);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(MineMeMessageManager.getInstance().translateAll("$1" + is.getType() + "$3:$2" + i.getData().getData() + "$3-$1" + m.getComposition().get(i) + "%"));
        List<String> lore = ItemUtils.getLore(i);
        lore.add(GUIResourcesUtils.clickToEdit);
        im.setLore(lore);
        is.setItemMeta(im);
        return is;
    }

    public static ItemStack generateCompositionChangeItemStack(double change) {
        boolean add = change > 0;
        String prefix = add ? "§a+" : "§c";
        Material m = add ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK;
        ItemStack is = new ItemStack(m);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(MineMeMessageManager.getInstance().translateAll(prefix + change + "%"));
        is.setItemMeta(im);
        return is;
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
        ItemStack i = new ItemStack(information);

        i = ItemUtils.addToLore(ItemUtils.clearLore(i),
                MineMeMessageManager.getInstance().translateAll(
                        infomationLore,
                        mine));
        return i;
    }
}
