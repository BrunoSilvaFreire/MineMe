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
import org.bukkit.event.Listener;

/**
 *
 * @author Selma
 */
public interface Mine extends Iterable<Block>, Listener {

    /**
     * Deletes the mine
     */
    public void delete();

    /**
     * Resets the mine
     */
    public void reset();

    /**
     * Save the mine to it's config file
     */
    public void save();

    /**
     * The the mine's <b>name</b>, not alias.
     *
     * @return The mine's name
     */
    public String getName();

    /**
     *
     * @return The materials used in the mine's composition
     */
    public Material[] getMaterials();

    /**
     * Checks if this mine is deleted (Garbage Collector hasn't finalized this
     * yet)
     *
     * @return true if this mine is deleted.
     */
    public boolean isDeleted();

    /**
     * Return if the mine is set to broadcast it's reset message.
     *
     * @return true if mine is set to send message on reset, false otherwise
     */
    public boolean isBroadcastOnReset();

    /**
     * Set's if the mine should broadcast it's reset message.
     *
     * @param broadcastOnReset boolean to set
     */
    public void setBroadcastOnReset(boolean broadcastOnReset);

    /**
     * Get the mine's type enum.
     *
     * @see MineType
     *
     * @return This mine's type
     */
    public MineType getType();

    public boolean contains(int x, int y, int z);

    public boolean contains(Block b);

    public boolean contains(Location l);

    public boolean contains(Player p);

    public boolean broadcastOnReset();

    public boolean broadcastToNearbyOnly();

    public double broadcastRadius();

    public List<Block> getBlocks();

    public List<String> getInfo();

    public Map<Material, Double> getComposition();

    public void tictoc();

    public Location getLocation();

    public String getAlias();

    public boolean containsMaterial(Material material);

    public void setComposition(Map<Material, Double> composition);

    public void setMaterial(Material material, double percentage);

    public void removeMaterial(Material material);

    //Statistics
    public int getVolume();

    public int getRemainingBlocks();

    public float getPercentageRemaining();

    public int getMinedBlocks();

    public float getPercentageMined();

    public boolean wasAlreadyBroken(Block b);

}
