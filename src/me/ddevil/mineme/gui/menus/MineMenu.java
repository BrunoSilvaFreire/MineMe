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

import java.util.HashMap;
import java.util.List;
import me.ddevil.core.utils.InventoryUtils;
import me.ddevil.core.utils.ItemUtils;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.gui.GUIManager;
import me.ddevil.mineme.gui.GUIResourcesUtils;
import me.ddevil.mineme.messages.MineMeMessageManager;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.mines.MineUtils;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
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
    private final HashMap<ItemStack, CompositionEditorMenu> compositionEditInventories = new HashMap();
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
        for (CompositionEditorMenu i : compositionEditInventories.values()) {
            if (i.getMainInventory().equals(inv)) {
                return true;
            }
        }
        return mainInventory.equals(inv);
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
                    if (compositionEditInventories.get(i).getMainInventory().equals(inventory)) {
                        MineMe.instance.debug("Removing " + i + " from list", 1);
                        compositionEditInventories.remove(i);
                    }
                }
                MineMe.instance.debug("Composition editor removed!", 1);
                owner.reset();
            }
        }
    }

    public ItemStack getItemStackOwnerOfEditor(Inventory inv) {
        for (ItemStack i : compositionEditInventories.keySet()) {
            if (compositionEditInventories.get(i).getMainInventory().equals(inv)) {
                return i;
            }
        }
        return null;
    }

    public boolean isCompositionEditor(Inventory inv) {
        for (ItemStack i : compositionEditInventories.keySet()) {
            if (compositionEditInventories.get(i).getMainInventory().equals(inv)) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Inventory inventory = e.getInventory();
        Player p = (Player) e.getWhoClicked();
        //Composition editors
        if (inventory != null) {
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
                    owner.removeMaterial(invOwner);
                    GUIManager.mineEditorGUI.openMineMenu(owner, p);
                }
                //Check change percentage
                if (InventoryUtils.wasClickedInLane(inventory, e.getRawSlot(), 0)
                        || InventoryUtils.wasClickedInLane(inventory, e.getRawSlot(), InventoryUtils.getTotalLanes(inventory) - 1)) {
                    double compositionChangeValue = GUIResourcesUtils.getCompositionChangeValue(i);
                    owner.setMaterialPercentage(invOwner, owner.getPercentage(invOwner) + compositionChangeValue);
                    getCompositionEditorMenu(invOwner).update();
                }
            }
        }
    }

    public void openCompositionEditor(ItemStack itemStackInComposition, Player p) {
        getCompositionEditorMenu(itemStackInComposition).open(p);
    }

    private CompositionEditorMenu getCompositionEditorMenu(ItemStack item) {
        item = MineUtils.getItemStackInComposition(owner, item);
        for (ItemStack i : compositionEditInventories.keySet()) {
            if (i.equals(item)) {
                return compositionEditInventories.get(i);
            }
        }
        CompositionEditorMenu menu = new CompositionEditorMenu(owner, item, globalInventorySize);
        compositionEditInventories.put(item, menu);
        return menu;
    }

}
