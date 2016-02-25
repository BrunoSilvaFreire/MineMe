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
package me.ddevil.mineme.mines;

import java.util.List;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 *
 * @author Selma
 */
public interface Mine extends Iterable<Block> {

    public abstract void delete();

    public abstract void reset();

    public abstract void save();

    public abstract String getName();

    public abstract Material[] getMaterials();

    public abstract boolean isBroadcastOnReset();

    public abstract void setBroadcastOnReset(boolean broadcastOnReset);

    public abstract MineType getType();

    public abstract boolean contains(int x, int y, int z);

    public abstract boolean contains(Block b);

    public abstract boolean contains(Location l);

    public abstract boolean contains(Player p);

    public abstract List<Block> getBlocks();

    public abstract Map<Material, Double> getComposition();

    public abstract void tictoc();
}
