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

import java.util.HashMap;
import me.ddevil.core.utils.InventoryUtils;
import me.ddevil.mineme.events.MineUpdateEvent;
import me.ddevil.mineme.gui.GUIManager;
import me.ddevil.mineme.gui.MineEditorGUI;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.mines.MineManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.Inventory;

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
    public void setup() {
        mainInventory = Bukkit.createInventory(null, mainInventorySize, mainInventoryName);
        for (Mine m : MineManager.getMines()) {

        }
    }
    //MainInventory
    protected final int mainInventorySize;
    protected final String mainInventoryName;
    protected Inventory mainInventory;
    //MinesInventory
    protected final int minesInventorySize;
    protected final HashMap<Mine, Inventory> inventories = new HashMap();

    @Override
    public void openMineMenu(Mine m, Player p) {
    }

    @Override
    public Inventory getMineInventory(Mine m) {
        if (inventories.containsKey(m)) {
            return inventories.get(m);
        } else {
            Inventory inv = Bukkit.createInventory(null, minesInventorySize, m.getAlias());
            inventories.put(m, inv);
            updateInventory(m);
            return inv;
        }
    }

    @EventHandler
    public void onMineUpdate(MineUpdateEvent e) {
        updateInventory(e.getMine());
    }

    @Override
    public void updateInventory(Mine mine) {
        Inventory inv = getMineInventory(mine);
        for (int i : InventoryUtils.getLane(inv, InventoryUtils.getTotalLanes(inv) - 1)) {
            inv.setItem(i, GUIManager.splitter);
        }
        inv.setItem(mainInventorySize, null);
    }

    @Override
    public void open(Player p) {
        p.openInventory(mainInventory);
    }

    @Override
    public void updateMainInventory() {
        for (int i : InventoryUtils.getLane(mainInventory, InventoryUtils.getTotalLanes(mainInventory) - 1)) {
            mainInventory.setItem(i, GUIManager.splitter);
        }
    }
}
