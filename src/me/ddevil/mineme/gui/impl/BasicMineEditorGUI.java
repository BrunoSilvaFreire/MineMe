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
package me.ddevil.mineme.gui.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import me.ddevil.core.CustomPlugin;
import me.ddevil.core.utils.InventoryUtils;
import me.ddevil.core.utils.ItemUtils;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.events.MineUpdateEvent;
import me.ddevil.mineme.gui.GUIManager;
import me.ddevil.mineme.gui.GUIResourcesUtils;
import me.ddevil.mineme.gui.MineEditorGUI;
import me.ddevil.mineme.gui.MineMenu;
import me.ddevil.mineme.messages.MineMeMessageManager;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.mines.MineManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Selma
 */
public class BasicMineEditorGUI implements MineEditorGUI {

    public BasicMineEditorGUI(String inventoryName, int mainInventorySize, int minesInventorySize) {
        this.mainInventoryName = inventoryName;
        this.mainInventorySize = mainInventorySize;
        this.minesInventorySize = minesInventorySize;
    }

    @Override
    public MineEditorGUI setup() {
        MineMe.registerListener(this);
        mainInventory = Bukkit.createInventory(null, mainInventorySize, mainInventoryName);
        mainInventoryConfig = MineMe.guiConfig.getConfigurationSection("mainMenu");
        ConfigurationSection miIconConfig = mainInventoryConfig.getConfigurationSection("icons.main");
        String name = MineMeMessageManager.translateTagsAndColor(miIconConfig.getString("name"));
        List<String> loreList = miIconConfig.getStringList("lore");
        String[] lore = MineMeMessageManager.translateTagsAndColors(loreList.toArray(new String[loreList.size()]));
        this.mainInventoryIcon = ItemUtils.createItem(
                Material.valueOf(miIconConfig.getString("type")),
                name,
                lore);
        ItemMeta mainIconMeta = mainInventoryIcon.getItemMeta();
        mainIconMeta.addItemFlags(ItemFlag.values());
        mainInventoryIcon.setItemMeta(mainIconMeta);
        for (Mine m : MineManager.getMines()) {
            getMineInventory(m);
        }
        updateMainInventory();
        return this;
    }

    @Override
    public void end() {
        mainInventory = null;
        mainInventoryConfig = null;
        mainInventoryIcon = null;
        inventories.clear();
    }

    //MainInventory
    protected final int mainInventorySize;
    protected final String mainInventoryName;
    protected Inventory mainInventory;
    protected ItemStack mainInventoryIcon;
    protected ConfigurationSection mainInventoryConfig;

    //MinesInventory
    protected final int minesInventorySize;
    protected final HashMap<Mine, MineMenu> inventories = new HashMap();

    @Override
    public void openMineMenu(Mine m, Player p) {
        updateMineInventory(m);
        p.openInventory(getMineInventory(m).getMainInventory());
    }

    @Override
    public MineMenu getMineInventory(Mine m) {
        if (inventories.containsKey(m)) {
            return inventories.get(m);
        } else {
            MineMenu inv = new MineMenu(minesInventorySize, m.getAlias(), m);
            inventories.put(m, inv);
            updateMineInventory(m);
            return inv;
        }
    }

    @EventHandler
    public void onMineUpdate(MineUpdateEvent e) {
        MineMe.instance.debug("Updating " + e.getMine().getName() + "'s inventory.");
        updateMineInventory(e.getMine());
    }

    @Override
    public void updateMineInventory(Mine mine) {
        Inventory inv = getMineInventory(mine).getMainInventory();
        for (int i : InventoryUtils.getLane(inv, InventoryUtils.getTotalLanes(inv) - 1)) {
            inv.setItem(i, GUIResourcesUtils.splitter);
            inv.setItem(i - 18, GUIResourcesUtils.splitter);
        }
        ItemStack[] materials = mine.getMaterials();
        int currentLoop = 0;
        for (int i : InventoryUtils.getLane(inv, InventoryUtils.getTotalLanes(inv) - 2)) {
            ItemStack is = materials.length > currentLoop ? GUIResourcesUtils.generateCompositionItemStack(mine, materials[currentLoop]) : GUIResourcesUtils.empty;
            inv.setItem(i, is);
            currentLoop++;
        }
        inv.setItem(InventoryUtils.getBottomMiddlePoint(inv) - 4, GUIResourcesUtils.backButton);
        inv.setItem(InventoryUtils.getBottomMiddlePoint(inv), mine.getIcon());
    }

    @Override
    public void open(Player p
    ) {
        updateMainInventory();
        p.openInventory(mainInventory);
    }
    private final HashMap<Integer, Mine> mineReference = new HashMap();

    @Override
    public void updateMainInventory() {
        for (int i : InventoryUtils.getLane(mainInventory, InventoryUtils.getTotalLanes(mainInventory) - 1)) {
            mainInventory.setItem(i, GUIResourcesUtils.splitter);
        }
        mainInventory.setItem(InventoryUtils.getBottomMiddlePoint(mainInventory), mainInventoryIcon);
        int i = 0;
        mineReference.clear();
        for (Mine mine : MineManager.getMines()) {
            ItemStack is = new ItemStack(mine.getIcon());
            List<String> lore;
            if (ItemUtils.checkLore(is)) {
                lore = is.getItemMeta().getLore();
            } else {
                lore = new ArrayList();
            }
            lore.add(GUIResourcesUtils.clickToSee);
            ItemMeta im = is.getItemMeta();
            im.setLore(lore);
            is.setItemMeta(im);
            mainInventory.setItem(i, is);
            mineReference.put(i, mine);
            i++;
        }
    }

    public boolean isMainInventory(Inventory inv) {
        return inv.equals(mainInventory);
    }

    public boolean isMainMineInventory(Inventory inv) {
        for (Mine m : inventories.keySet()) {
            if (inventories.get(m).getMainInventory().equals(inv)) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Inventory inv = e.getClickedInventory();
        if (isMainInventory(inv)) {
            e.setCancelled(true);
            Mine mine = mineReference.get(e.getSlot());
            if (mine != null) {
                openMineMenu(mine, (Player) e.getWhoClicked());
            }
        } else if (isMainMineInventory(inv)) {
            e.setCancelled(true);
            //Clicked an Mine Inventory
            ItemStack i = e.getCurrentItem();
            if (ItemUtils.checkDisplayName(i)) {
                //Item has display name
                ItemMeta itemMeta = i.getItemMeta();
                String itemName = itemMeta.getDisplayName();
                Bukkit.broadcastMessage(itemName);
                Bukkit.broadcastMessage(GUIResourcesUtils.backButton.getItemMeta().getDisplayName());
                if (itemName.equalsIgnoreCase(GUIResourcesUtils.backButton.getItemMeta().getDisplayName())) {
                    open((Player) e.getWhoClicked());
                }
            }
        }
    }

}