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
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.gui.GUIManager;
import me.ddevil.mineme.gui.GUIResourcesUtils;
import me.ddevil.mineme.messages.MineMeMessageManager;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.mines.MineUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Selma
 */
public class CompositionEditorMenu implements Listener {

    private final Mine owner;
    private final ItemStack item;
    private final Inventory main;

    public CompositionEditorMenu(Mine owner, ItemStack item, int inventorySize) {
        this.owner = owner;
        this.item = MineUtils.getItemStackInComposition(owner, item);
        String title = MineMeMessageManager.getInstance().translateTagsAndColor("$2" + item.getType() + "$3:$1" + item.getData().getData());
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
                MineMeMessageManager.getInstance().translateAll(
                        new String[]{
                            "$3Current: $1" + owner.getPercentage(item) + "%"
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
        InventoryUtils.drawSquare(main, 18, 35, invIcon);
        main.setItem(middle, owner.getIcon());
        main.setItem(middle - 18, GUIResourcesUtils.generateInformationItem(owner));

        main.setItem(middle - 9, GUIResourcesUtils.backButton);
        main.setItem(middle + 9, GUIResourcesUtils.removeButton);
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

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Inventory inventory = e.getInventory();
        Player p = (Player) e.getWhoClicked();
        //Composition editors
        if (inventory != null) {
            if (isThis(inventory)) {
                e.setCancelled(true);
                ItemStack i = e.getCurrentItem();
                ItemMeta itemMeta = i.getItemMeta();
                String itemName = itemMeta.getDisplayName();
                //Check go back
                if (itemName.equalsIgnoreCase(GUIResourcesUtils.backButton.getItemMeta().getDisplayName())) {
                    GUIManager.mineEditorGUI.openMineMenu(owner, p);
                }
                //Check remove
                if (itemName.equalsIgnoreCase(GUIResourcesUtils.removeButton.getItemMeta().getDisplayName())) {
                    owner.removeMaterial(item);
                    GUIManager.mineEditorGUI.openMineMenu(owner, p);
                }
                //Check change percentage
                if (InventoryUtils.wasClickedInLane(inventory, e.getRawSlot(), 0)
                        || InventoryUtils.wasClickedInLane(inventory, e.getRawSlot(), InventoryUtils.getTotalLanes(inventory) - 1)) {
                    double finalValue = GUIResourcesUtils.getCompositionChangeValue(i) + owner.getPercentage(item);
                    owner.setMaterialPercentage(item, finalValue);
                    open(p);
                }
            }
        }
    }

    public Mine getOwner() {
        return owner;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setup() {
        MineMe.registerListener(this);
    }

    public void end() {
        MineMe.unregisterListener(this);
    }
}
