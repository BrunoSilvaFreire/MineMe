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
import me.ddevil.core.utils.InventoryUtils;
import me.ddevil.mineme.mines.Mine;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Selma
 */
public class MineMenu {

    private final Mine owner;
    private final Inventory mainInventory;
    private final HashMap<ItemStack, Inventory> compositionEditInventories = new HashMap();
    private final int globalInventorySize;

    public MineMenu(int size, String name, Mine owner) {
        mainInventory = Bukkit.createInventory(null, size, name);
        this.owner = owner;
        this.globalInventorySize = size;
    }

    public Inventory getMainInventory() {
        return mainInventory;
    }

    public void updateCompositionEditingInventory(ItemStack is) {
        Inventory inv = getCompositionEditInventory(is);
        for (int i : InventoryUtils.getLane(inv, 0)) {
            inv.setItem(i, GUIResourcesUtils.splitter);
        }
    }

    public Inventory getCompositionEditInventory(ItemStack i) {
        if (compositionEditInventories.containsKey(i)) {
            return compositionEditInventories.get(i);
        } else {
            String title = "$2" + i.getType() + "$3:$1" + i.getData().getData();
            Inventory inv = Bukkit.createInventory(null, globalInventorySize, title);
            compositionEditInventories.put(i, inv);
            return inv;
        }
    }
}
