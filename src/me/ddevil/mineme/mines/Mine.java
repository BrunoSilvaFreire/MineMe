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
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

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

    /**
     * Check if the point on which all the 3 variables cross is inside the mine
     *
     * @param x X coordinate
     * @param y Y coordinate
     * @param z Z coordinate
     * @return true if is inside the mine
     */
    public boolean contains(double x, double y, double z);

    /**
     * Check if the block is
     *
     * @param block The block to check.
     * @return true if it's inside, Oooh yeah baby
     */
    public boolean contains(Block block);

    /**
     * Check if the location is inside this mine
     *
     * @param location The location to test
     * @return true if the location is inside the mine
     */
    public boolean contains(Location location);

    /**
     * Check if the player is inside this mine
     *
     * @param player The player to test
     * @return true is the player is inside the mine
     */
    public boolean contains(Player player);

    /**
     * Check if the mine is set to broadcast a message on reset
     *
     * @return true if it is set
     */
    public boolean broadcastOnReset();

    /**
     * Check if the mine is set to broadcast the reset message to nearby players
     * only
     *
     * @return true if it is set
     */
    public boolean broadcastToNearbyOnly();

    /**
     * Get's the broadcast radius of the reset message.
     *
     * @return The broadcast eadius
     */
    public double broadcastRadius();

    /**
     * Get's a list of information about the mine
     *
     * @return The mine's technical info
     */
    public List<String> getInfo();

    /**
     * Get's a map of each material of the composition of the mine and it's
     * percentage
     *
     * @return The mine's composition
     */
    public Map<Material, Double> getComposition();

    /**
     * Get's the material's percentage in the mines composition
     *
     * @param material The material to check
     * @return The percentage of the material in the mines composition, returns
     * 0 if the mine doesn't contains the material;
     */
    public double getPercentage(Material material);

    /**
     * Remove's a second from the mines countdown, also updates the holograms,
     * if it contains any
     */
    public void secondCountdown();

    /**
     * Set the block as a broken block, so the mine contabilazes it in the
     * statistics
     *
     * @param block The block to set as broken
     */
    public void setBlockAsBroken(Block block);

    /**
     * Get's the mine's center.
     *
     * @return The mine's center
     */
    public Location getLocation();

    /**
     * Get the actual name that is displayed os broadcast messages, etc.
     *
     * @return The mine's alias.
     */
    public String getAlias();

    /**
     * Check if the mine contains said material.
     *
     * @param material The material to check
     * @return true if the mine contains this material in it's composition.
     */
    public boolean containsMaterial(Material material);

    /**
     * Delete's the old mine composition as set's the map as the new composition
     *
     * @param composition The composition map to set to the mine
     */
    public void setComposition(Map<Material, Double> composition);

    /**
     * Set's the material percentage in the mine's composition, override the
     * previous percentage if there was any, if the mine dind't contain the
     * material, adds it to the composition.
     *
     * @param material The material to set
     * @param percentage The percentage to set
     */
    public void setMaterial(Material material, double percentage);

    /**
     * Remove the material from the mine's composition, if the material is party
     * of the composition.
     *
     * @param material The material to remove
     */
    public void removeMaterial(Material material);

    /**
     * Get's the world the mine is in.
     *
     * @return The mine's world.
     */
    public World getWorld();

    /**
     * Gets the minimum Y value
     *
     * @return the Y value
     */
    public int getMinimumY();

    /**
     * Gets the maximum Y value
     *
     * @return the Y value
     */
    public int getMaximumY();

    /**
     * Gets the minimum Y value
     *
     * @return the Y value
     */
    public Vector getMaximumPoint();

    /**
     * Gets the maximum Y value
     *
     * @return the Y value
     */
    public Vector getMinimumPoint();

    /**
     * Get's the time till next reset.
     *
     * @return The remaining time till the next reset.
     */
    public int getTimeToNextReset();

    /**
     * Check if the mine is enabled, this only work when you've deleted a mine
     * and haven't restarted the server yet, because mine's only get loaded if
     * enabled is set to true in it's config.
     *
     * @return true if it's enabled
     */
    public boolean isEnabled();

    /**
     * Set's the enabled in the config
     *
     * @param enabled The boolean to set
     */
    public void setEnabled(boolean enabled);
    //Statistics

    /**
     * Get the mine's volume, duuuh
     *
     * @return the mine's volume
     */
    public int getVolume();

    /**
     * Get the total number of blocks that haven't been broken yet
     *
     * @return The total remaining blocks
     */
    public int getRemainingBlocks();

    /**
     * Get the total number of blocks that haven't been broken yet as a
     * percentage
     *
     * @return The total remaining blocks as a percentage
     */
    public float getPercentageRemaining();

    /**
     * Get the total number of blocks that have been broken
     *
     * @return The total broken blocks
     */
    public int getMinedBlocks();

    /**
     * Get the total number of blocks that have been broken yet as a percentage
     *
     * @return The total broken blocks as a percentage
     */
    public float getPercentageMined();

    /**
     * Checks if the broken was already broken in the mine.
     *
     * @param block The block to check
     * @return true if the block was already broken.
     */
    public boolean wasAlreadyBroken(Block block);

}
