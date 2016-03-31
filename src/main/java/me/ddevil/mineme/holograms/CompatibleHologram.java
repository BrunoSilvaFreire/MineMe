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
package me.ddevil.mineme.holograms;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Selma
 */
public interface CompatibleHologram {

    public void clearLines();

    public void appendItemLine(ItemStack icon);

    public void appendTextLine(String translateTagsAndColors);

    public void setLine(int line, String text);

    public void delete();

    public void removeLine(int line);

    public void move(World w, double x, double y, double z);

    public void move(Location l);

    public Location getLocation();

    public int size();

}
