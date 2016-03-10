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
package me.ddevil.mineme.gui.menus;

import me.ddevil.core.utils.InventoryUtils;
import me.ddevil.core.utils.ItemUtils;
import me.ddevil.mineme.gui.GUIResourcesUtils;
import me.ddevil.mineme.messages.MineMeMessageManager;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.mines.MineUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Selma
 */
public class CompositionEditorMenu {

    private final Mine owner;
    private final ItemStack item;
    private final Inventory main;

    public CompositionEditorMenu(Mine owner, ItemStack item, int inventorySize) {
        this.owner = owner;
        this.item = MineUtils.getItemStackInComposition(owner, item);
        String title = MineMeMessageManager.translateTagsAndColor("$2" + item.getType() + "$3:$1" + item.getData().getData());
        this.main = InventoryUtils.createInventory(title, inventorySize / 9);
    }

    //Composition editors.
    public void update() {
        ItemStack invIcon = ItemUtils.createItem(
                //Reference item stack
                item,
                //Item name
                main.getTitle(),
                //Lore
                MineMeMessageManager.translateTagsAndColors(
                        new String[]{
                            "$3Current: $1" + owner.getPercentage(item)
                        }));
        double currentAddPercentage = 50;
        //Top left 4 add buttons
        for (int i : InventoryUtils.getPartialLane(main, 0, 0, 3)) {
            main.setItem(i, GUIResourcesUtils.generateCompositionChangeItemStack(currentAddPercentage));
            currentAddPercentage /= 2;
        }
        //Bottom left 4 remove buttons
        double currentRemovePercentage = -50;
        for (int i : InventoryUtils.getPartialLane(main, InventoryUtils.getTotalLanes(main) - 1, 0, 3)) {
            main.setItem(i, GUIResourcesUtils.generateCompositionChangeItemStack(currentRemovePercentage));
            currentRemovePercentage /= 2;
        }
        //Top right 4 add buttons
        int[] customValues = {1, 5, 10, 20};
        int currentCustomUpid = 0;
        for (int i : InventoryUtils.getPartialLane(main, 0, 5, 8)) {
            main.setItem(i, GUIResourcesUtils.generateCompositionChangeItemStack(customValues[currentCustomUpid]));
            currentCustomUpid++;
        }
        //Bottom right 4 add buttons
        int currentCustomBottomid = 0;
        for (int i : InventoryUtils.getPartialLane(main, InventoryUtils.getTotalLanes(main) - 1, 5, 8)) {
            main.setItem(i, GUIResourcesUtils.generateCompositionChangeItemStack(customValues[currentCustomBottomid] * -1));
            currentCustomBottomid++;
        }
        //Containers
        for (int i : InventoryUtils.getLane(main, InventoryUtils.getTotalLanes(main) - 2)) {
            main.setItem(i, GUIResourcesUtils.splitter);
        }
        for (int i : InventoryUtils.getLane(main, 1)) {
            main.setItem(i, GUIResourcesUtils.splitter);
        }
        //Extra items
        main.setItem(InventoryUtils.getTopMiddlePoint(main), GUIResourcesUtils.splitter);
        main.setItem(InventoryUtils.getBottomMiddlePoint(main), GUIResourcesUtils.splitter);
        int middle = InventoryUtils.getMiddlePoint(main);
        main.setItem(middle, owner.getIcon());
        main.setItem(middle - 9, GUIResourcesUtils.backButton);
        main.setItem(middle + 9, GUIResourcesUtils.removeButton);
        for (int i = 18; i < 36; i++) {
            if (main.getItem(i) == null) {
                main.setItem(i, invIcon);
            }
        }
    }

    public boolean isThis(Inventory i) {
        return i.equals(main);
    }

    public Inventory getMainInventory() {
        return main;
    }

    public void open(Player p) {
        update();
        p.openInventory(main);
    }

}
