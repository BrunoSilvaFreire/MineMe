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

import me.ddevil.mineme.gui.menus.MineMenu;
import me.ddevil.mineme.mines.Mine;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

/**
 *
 * @author Selma
 */
public interface MineEditorGUI extends Listener {

    public MineEditorGUI setup();

    public void end();

    public void openMineMenu(Mine m, Player p);

    public void open(Player p);

    public void updateMainInventory();

    public void updateMineInventory(Mine m);

    public Mine ownerOf(Inventory inv);

    public boolean isMainInventory(Inventory inv);

    public boolean isMainMineInventory(Inventory inv);

    public MineMenu getMineInventory(Mine m);
}
