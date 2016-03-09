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
import me.ddevil.core.utils.InventoryUtils;
import me.ddevil.core.utils.ItemUtils;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.messages.MineMeMessageManager;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.mines.MineUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Selma
 */
public class MineMenu implements Listener {

    private final Mine owner;
    private final Inventory mainInventory;
    private final HashMap<ItemStack, Inventory> compositionEditInventories = new HashMap();
    private final int globalInventorySize;

    public MineMenu(int size, String name, Mine owner) {
        mainInventory = InventoryUtils.createInventory(name, size / 9);
        this.owner = owner;
        this.globalInventorySize = size;
    }

    public void setup() {
        MineMe.registerListener(this);
    }

    public void end() {
        compositionEditInventories.clear();
        MineMe.unregisterListener(this);
    }

    public Mine getOwner() {
        return owner;
    }

    public int getGlobalInventorySize() {
        return globalInventorySize;
    }

    public Inventory getMainInventory() {
        return mainInventory;
    }

    public boolean contains(Inventory inv) {
        for (Inventory i : compositionEditInventories.values()) {
            if (i.equals(inv)) {
                return true;
            }
        }
        return mainInventory.equals(inv);
    }

    //Composition editors.
    public void updateCompositionEditingInventory(ItemStack is) {
        Inventory inv = getCompositionEditInventory(is);
        ItemStack invIcon = ItemUtils.createItem(is, inv.getTitle(), MineMeMessageManager.translateTagsAndColors(new String[]{"$3Current: $1" + owner.getPercentage(is)}));
        double currentAddPercentage = 50;
        //Top left 4 add buttons
        for (int i : InventoryUtils.getPartialLane(inv, 0, 0, 3)) {
            inv.setItem(i, GUIResourcesUtils.generateCompositionChangeItemStack(currentAddPercentage));
            currentAddPercentage /= 2;
        }
        //Bottom left 4 remove buttons
        double currentRemovePercentage = -50;
        for (int i : InventoryUtils.getPartialLane(inv, InventoryUtils.getTotalLanes(inv) - 1, 0, 3)) {
            inv.setItem(i, GUIResourcesUtils.generateCompositionChangeItemStack(currentRemovePercentage));
            currentRemovePercentage /= 2;
        }
        //Top right 4 add buttons
        int[] customValues = {1, 5, 10, 20};
        int currentCustomUpid = 0;
        for (int i : InventoryUtils.getPartialLane(inv, 0, 5, 8)) {
            inv.setItem(i, GUIResourcesUtils.generateCompositionChangeItemStack(customValues[currentCustomUpid]));
            currentCustomUpid++;
        }
        //Bottom right 4 add buttons
        int currentCustomBottomid = 0;
        for (int i : InventoryUtils.getPartialLane(inv, InventoryUtils.getTotalLanes(inv) - 1, 5, 8)) {
            inv.setItem(i, GUIResourcesUtils.generateCompositionChangeItemStack(customValues[currentCustomBottomid] * -1));
            currentCustomBottomid++;
        }
        //Containers
        for (int i : InventoryUtils.getLane(inv, InventoryUtils.getTotalLanes(inv) - 2)) {
            inv.setItem(i, GUIResourcesUtils.splitter);
        }
        for (int i : InventoryUtils.getLane(inv, 1)) {
            inv.setItem(i, GUIResourcesUtils.splitter);
        }
        //Extra items
        inv.setItem(InventoryUtils.getTopMiddlePoint(inv), GUIResourcesUtils.splitter);
        inv.setItem(InventoryUtils.getBottomMiddlePoint(inv), GUIResourcesUtils.splitter);
        int middle = InventoryUtils.getMiddlePoint(inv);
        inv.setItem(middle, owner.getIcon());
        inv.setItem(middle - 9, GUIResourcesUtils.backButton);
        inv.setItem(middle + 9, GUIResourcesUtils.removeButton);
        for (int i = 18; i < 36; i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, invIcon);
            }
        }
    }

    public Inventory getCompositionEditInventory(ItemStack i) {
        if (compositionEditInventories.containsKey(i)) {
            return compositionEditInventories.get(i);
        } else {
            String title = MineMeMessageManager.translateTagsAndColor("$2" + i.getType() + "$3:$1" + i.getData().getData());
            Inventory inv = InventoryUtils.createInventory(title, globalInventorySize / 9);
            compositionEditInventories.put(i, inv);
            return inv;
        }
    }

    public boolean isCompositionEditor(Inventory i) {
        return compositionEditInventories.values().contains(i);
    }

    public ItemStack getItemStackOwnerOfEditor(Inventory inv) {
        for (ItemStack i : compositionEditInventories.keySet()) {
            if (compositionEditInventories.get(i).equals(inv)) {
                return i;
            }
        }
        return null;
    }

    public void openCompositionEditor(ItemStack i, Player p) {
        updateCompositionEditingInventory(i);
        p.openInventory(getCompositionEditInventory(i));
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Inventory inventory = e.getInventory();
        String name = ChatColor.stripColor(inventory.getTitle());
        if (isCompositionEditor(inventory)) {
            List<HumanEntity> viewers = e.getViewers();
            viewers.remove(e.getPlayer());
            MineMe.instance.debug("Checking viewers for composition editor " + name, 1);
            if (viewers.isEmpty()) {
                MineMe.instance.debug("No one is viewing composition editor " + name + ", removing from list.", 1);
                for (ItemStack i : compositionEditInventories.keySet()) {
                    if (compositionEditInventories.get(i).equals(inventory)) {
                        MineMe.instance.debug("Removing " + i + " from list", 1);
                        compositionEditInventories.remove(i);
                    }
                }
                MineMe.instance.debug("Composition editor removed!", 1);
                owner.reset();
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Inventory inventory = e.getInventory();
        Player p = (Player) e.getWhoClicked();
        //Composition editors
        if (isCompositionEditor(inventory)) {
            e.setCancelled(true);
            ItemStack i = e.getCurrentItem();
            ItemMeta itemMeta = i.getItemMeta();
            String itemName = itemMeta.getDisplayName();
            ItemStack invOwner = getItemStackOwnerOfEditor(inventory);
            //Check go back
            if (itemName.equalsIgnoreCase(GUIResourcesUtils.backButton.getItemMeta().getDisplayName())) {
                p.openInventory(getMainInventory());
            }
            //Check go back
            if (itemName.equalsIgnoreCase(GUIResourcesUtils.removeButton.getItemMeta().getDisplayName())) {
                owner.removeMaterial(MineUtils.getItemStackInComposition(owner, invOwner));
                p.closeInventory();
            }
            //Check change percentage
            if (InventoryUtils.wasClickedInLane(inventory, e.getRawSlot(), 0)
                    || InventoryUtils.wasClickedInLane(inventory, e.getRawSlot(), InventoryUtils.getTotalLanes(inventory) - 1)) {
                double compositionChangeValue = GUIResourcesUtils.getCompositionChangeValue(i);
                owner.setMaterialPercentage(invOwner, owner.getPercentage(invOwner) + compositionChangeValue);
                updateCompositionEditingInventory(invOwner);
            }
        }
    }

}
