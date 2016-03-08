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
import java.util.List;
import me.ddevil.core.utils.ItemUtils;
import me.ddevil.mineme.messages.MineMeMessageManager;
import me.ddevil.mineme.mines.Mine;
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
    public static ItemStack backButton;
    //Mine utils
    public static String mineItemNameFormat;
    public static String clickToEdit;
    //Global strings
    public static String clickToSee;

    public static ItemStack generateCompositionItemStack(Mine m, ItemStack i) {
        ItemStack is = new ItemStack(i);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(MineMeMessageManager.translateTagsAndColor("$1" + is.getType() + "$3-$2" + m.getComposition().get(i) + "%"));
        List<String> lore = ItemUtils.getLore(i);
        lore.add(GUIResourcesUtils.clickToEdit);
        im.setLore(lore);
        is.setItemMeta(im);
        return is;
    }

}