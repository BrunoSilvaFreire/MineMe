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
import me.ddevil.core.utils.inventory.InventoryUtils;
import me.ddevil.core.utils.items.ItemUtils;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.gui.GUIManager;
import me.ddevil.mineme.gui.GUIResourcesUtils;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.mines.MineUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
        update();
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

    public boolean isThis(Inventory i) {
        return mainInventory.equals(i);
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
        menu.setup();
        compositionEditInventories.put(item, menu);
        return menu;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Inventory inv = e.getClickedInventory();
        Player p = (Player) e.getWhoClicked();
        ItemStack i = e.getCurrentItem();
        if (inv != null) {
            if (isThis(inv)) {
                e.setCancelled(true);
                //Clicked an Mine Inventory
                if (ItemUtils.checkDisplayName(i)) {
                    //Item has display name
                    ItemMeta itemMeta = i.getItemMeta();
                    String itemName = itemMeta.getDisplayName();
                    //Check go back
                    if (itemName.equalsIgnoreCase(GUIResourcesUtils.backButton.getItemMeta().getDisplayName())) {
                        GUIManager.mineEditorGUI.open(p);
                    } //Check teleport
                    else if (itemName.equalsIgnoreCase(GUIResourcesUtils.teleporter.getItemMeta().getDisplayName())) {
                        p.closeInventory();
                        p.teleport(owner.getTopCenterLocation());
                    } //Check adding material 
                    else if (itemName.equalsIgnoreCase(GUIResourcesUtils.resetButton.getItemMeta().getDisplayName())) {
                        owner.reset();
                    } else if (itemName.equalsIgnoreCase(GUIResourcesUtils.deleteMineButton.getItemMeta().getDisplayName())) {
                        owner.delete();
                    } else if (itemName.equalsIgnoreCase(GUIResourcesUtils.clearMaterials.getItemMeta().getDisplayName())) {
                        owner.clearMaterials();
                        update();
                    } else if (InventoryUtils.wasClickedInLane(inv, e.getRawSlot(), InventoryUtils.getTotalLanes(inv) - 2) && ItemUtils.equals(i, GUIResourcesUtils.empty)) {
                        //Check is item is valid
                        if (ItemUtils.equals(i, GUIResourcesUtils.empty)) {
                            if (e.getCursor() != null) {
                                ItemStack cursor = new ItemStack(e.getCursor());
                                if (cursor.getType() != Material.AIR) {
                                    if (cursor.getType().isBlock()) {
                                        //Check was clicked on an empty panel
                                        //Check if already contains material
                                        if (!owner.containsMaterial(cursor)) {
                                            cursor.setAmount(1);
                                            owner.setMaterialPercentage(cursor, 0.0d);
                                            update();
                                        } else {
                                            MineMe.chatManager.sendMessage(p, "$1" + owner.getName() + " $4already contains $2" + ItemUtils.toString(cursor) + "$4!");
                                        }
                                    } else {
                                        MineMe.chatManager.sendMessage(p, "$1" + ItemUtils.toString(cursor) + "$1 isn't a placeable item!");
                                    }
                                } else {
                                    MineMe.chatManager.sendMessage(p, "$4Please have an item in your hand for us to add!");
                                }
                            } else {
                                MineMe.chatManager.sendMessage(p, "$4Please have an item in your hand for us to add!");
                            }
                        }
                    } //Check edit percentage
                    else if (MineUtils.containsRelativeItemStackInComposition(owner, i) && !ItemUtils.equals(i, GUIResourcesUtils.empty)) {
                        openCompositionEditor(MineUtils.getItemStackInComposition(owner, i), p);
                    }
                }
            }
        }
    }

    public void update() {
        for (int i : InventoryUtils.getLane(mainInventory, InventoryUtils.getTotalLanes(mainInventory) - 1)) {
            mainInventory.setItem(i, GUIResourcesUtils.splitter);
            mainInventory.setItem(i - 18, GUIResourcesUtils.splitter);
        }
        ItemStack[] materials = owner.getMaterials();
        int currentLoop = 0;
        for (int i : InventoryUtils.getLane(mainInventory, InventoryUtils.getTotalLanes(mainInventory) - 2)) {
            ItemStack is = materials.length > currentLoop ? GUIResourcesUtils.generateCompositionItemStack(owner, materials[currentLoop]) : GUIResourcesUtils.empty;
            mainInventory.setItem(i, is);
            currentLoop++;
        }
        mainInventory.setItem(InventoryUtils.getBottomMiddlePoint(mainInventory) - 4, GUIResourcesUtils.backButton);
        mainInventory.setItem(InventoryUtils.getBottomMiddlePoint(mainInventory) + 4, GUIResourcesUtils.generateInformationItem(owner));
        mainInventory.setItem(InventoryUtils.getBottomMiddlePoint(mainInventory), owner.getIcon());
        mainInventory.setItem(0, GUIResourcesUtils.teleporter);
        mainInventory.setItem(1, GUIResourcesUtils.resetButton);
        mainInventory.setItem(2, GUIResourcesUtils.clearMaterials);
        Integer[] lane = InventoryUtils.getLane(mainInventory, 2);
        mainInventory.setItem(lane[lane.length - 1], GUIResourcesUtils.deleteMineButton);
    }

    public void open(Player p) {
        update();
        p.openInventory(mainInventory);
    }
}
