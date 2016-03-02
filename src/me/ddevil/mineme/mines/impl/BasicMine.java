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
package me.ddevil.mineme.mines.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import me.ddevil.mineme.messages.MineMeMessageManager;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.mines.HologramCompatible;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.mines.configs.MineConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public abstract class BasicMine implements Mine {

    //General
    protected final String name;
    protected final String alias;
    protected FileConfiguration config;

    //Messages
    protected boolean broadcastOnReset;
    protected String broadcastMessage;
    protected double broadcastRadius;
    protected boolean broadcastNearby;

    //Blocks
    protected Map<Material, Double> composition;
    protected final ArrayList<Block> brokenBlocks = new ArrayList();

    //Clock
    protected int currentResetDelay;
    protected int totalResetDelay;
    protected boolean deleted = false;

    public BasicMine(MineConfig config) {
        this.broadcastMessage = MineMe.forceDefaultBroadcastMessage
                ? MineMeMessageManager.globalResetMessage
                : config.isUseCustomBroadcast() ? config.getBroadcastMessage() : MineMeMessageManager.globalResetMessage;
        this.broadcastOnReset = config.isBroadcastOnReset();
        this.broadcastRadius = config.getBroadcastRadius();
        this.broadcastNearby = config.isNearbyBroadcast();
        this.currentResetDelay = totalResetDelay;
        this.totalResetDelay = config.getResetDelay();
        this.name = config.getName();
        this.alias = MineMeMessageManager.translateColors(config.getAlias());
        this.world = config.getWorld();
    }

    public BasicMine(String name, World world, boolean broadcastOnReset, boolean nearbyBroadcast, String broadcastMessage, double broadcastRadius, int resetMinutesDelay) {
        this.broadcastOnReset = broadcastOnReset;
        this.broadcastNearby = nearbyBroadcast;
        this.broadcastRadius = broadcastRadius;
        this.totalResetDelay = resetMinutesDelay;
        this.currentResetDelay = totalResetDelay;
        this.broadcastMessage = broadcastMessage;
        this.name = name;
        this.alias = MineMeMessageManager.translateColors(name);
        this.world = world;
    }

    public BasicMine(String name, World world, boolean broadcastOnReset, boolean nearbyBroadcast, double broadcastRadius, int resetMinutesDelay) {
        this.broadcastOnReset = broadcastOnReset;
        this.broadcastNearby = nearbyBroadcast;
        this.broadcastRadius = broadcastRadius;
        this.totalResetDelay = resetMinutesDelay;
        this.currentResetDelay = totalResetDelay;
        broadcastMessage = MineMe.messagesConfig.getString("messages.resetMessage");
        this.name = name;
        this.alias = MineMeMessageManager.translateColors(name);
        this.world = world;
    }

    public BasicMine(String name, World world, String alias, boolean broadcastOnReset, boolean nearbyBroadcast, String broadcastMessage, double broadcastRadius, int resetMinutesDelay) {
        this.broadcastOnReset = broadcastOnReset;
        this.broadcastNearby = nearbyBroadcast;
        this.broadcastRadius = broadcastRadius;
        this.totalResetDelay = resetMinutesDelay;
        this.currentResetDelay = totalResetDelay;
        this.broadcastMessage = broadcastMessage;
        this.name = name;
        this.alias = MineMeMessageManager.translateColors(alias);
        this.world = world;
    }

    public BasicMine(String name, World world, String alias, boolean broadcastOnReset, boolean nearbyBroadcast, double broadcastRadius, int resetMinutesDelay) {
        this.broadcastOnReset = broadcastOnReset;
        this.broadcastNearby = nearbyBroadcast;
        this.broadcastRadius = broadcastRadius;
        this.totalResetDelay = resetMinutesDelay;
        this.currentResetDelay = totalResetDelay;
        broadcastMessage = MineMe.messagesConfig.getString("messages.resetMessage");
        this.name = name;
        this.alias = MineMeMessageManager.translateColors(alias);
        this.world = world;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getResetMinutesDelay() {
        return totalResetDelay;
    }

    public void setResetMinutesDelay(int resetMinutesDelay) {
        this.totalResetDelay = resetMinutesDelay;
        save();
    }

    @Override
    public boolean broadcastOnReset() {
        return broadcastOnReset;
    }

    @Override
    public boolean broadcastToNearbyOnly() {
        return broadcastNearby;
    }

    @Override
    public double broadcastRadius() {
        return broadcastRadius;
    }

    @Override
    public boolean isBroadcastOnReset() {
        return broadcastOnReset;
    }

    @Override
    public void setBroadcastOnReset(boolean broadcastOnReset) {
        this.broadcastOnReset = broadcastOnReset;
        save();

    }

    public void setResetDelay(int resetDelay) {
        this.currentResetDelay = resetDelay;
        save();
    }

    public void setNearbyBroadcast(boolean nearbyBroadcast) {
        this.broadcastNearby = nearbyBroadcast;
        save();
    }

    public void setBroadcastRadius(double broadcastRadius) {
        this.broadcastRadius = broadcastRadius;
        save();
    }

    public void setBroadcastMessage(String broadcastMessage) {
        this.broadcastMessage = broadcastMessage;
        save();
    }

    public double getBroadcastRadius() {
        return broadcastRadius;
    }

    public String getBroadcastMessage() {
        return broadcastMessage;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public void minuteCountdown() {
        currentResetDelay--;
        if (currentResetDelay <= 0) {
            reset();
        }
    }

    @Override
    public List<String> getInfo() {
        String[] basic = new String[]{
            "$1Mine: $2" + getName(),
            "$1World: $2" + getLocation().getWorld().getName(),
            "$1Location: $2" + getLocation().getBlockX() + ", " + getLocation().getBlockY() + ", " + getLocation().getBlockZ() + ", ",
            "$1Broadcast on reset: $2" + broadcastOnReset(),
            "$1Nearby broadcast: $2" + broadcastToNearbyOnly(),
            "$1Broadcast radius: $2" + broadcastRadius(),
            "$1Composition: $2"
        };
        ArrayList<String> comp = new ArrayList();

        comp.addAll(Arrays.asList(basic));
        for (Material ma
                : getMaterials()) {
            comp.add("$1" + ma.name() + " $2= $1" + getComposition().get(ma));
        }
        return comp;
    }

    @Override
    public boolean wasAlreadyBroken(Block b) {
        return brokenBlocks.contains(b);
    }

    @Override
    public float getPercentageMined() {
        if (brokenBlocks.isEmpty()) {
            return 0;
        } else {
            double percentage = (brokenBlocks.size() * 100f) / getVolume();
            return Math.round(percentage);
        }
    }

    @Override
    public double getPercentage(Material m) {
        if (composition.containsKey(m)) {
            return composition.get(m);
        } else {
            return 0;
        }
    }

    @Override
    public float getPercentageRemaining() {
        if (getRemainingBlocks() == 0) {
            return 0;
        } else {
            double percentage = (getRemainingBlocks() * 100) / getVolume();
            return Math.round(percentage);
        }
    }

    @Override
    public int getMinedBlocks() {
        return getVolume() - getRemainingBlocks();
    }

    @Override
    public int getRemainingBlocks() {
        return getVolume() - brokenBlocks.size();
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Block b = e.getBlock();
        if (contains(b)) {
            if (!wasAlreadyBroken(b)) {
                brokenBlocks.add(b);
                if (this instanceof HologramCompatible) {
                    HologramCompatible hc = (HologramCompatible) this;
                    hc.updateHolograms();
                }
            }
        }
    }

    @EventHandler
    public void onBreak(BlockExplodeEvent e) {
        Block b = e.getBlock();
        if (contains(b)) {
            if (!wasAlreadyBroken(b)) {
                brokenBlocks.add(b);
                if (this instanceof HologramCompatible) {
                    HologramCompatible hc = (HologramCompatible) this;
                    hc.updateHolograms();
                }
            }
        }
    }

    @EventHandler
    public void onBreak(EntityExplodeEvent e) {
        for (Block b : e.blockList()) {
            if (contains(b)) {
                if (!wasAlreadyBroken(b)) {
                    brokenBlocks.add(b);
                }
            }
        }
        if (this instanceof HologramCompatible) {
            HologramCompatible hc = (HologramCompatible) this;
            hc.softHologramUpdate();
        }
    }

    @Override
    public void setBlockAsBroken(Block block) {
        brokenBlocks.add(block);
        if (this instanceof HologramCompatible) {
            HologramCompatible hc = (HologramCompatible) this;
            hc.softHologramUpdate();
        }
    }

    @Override
    public boolean containsMaterial(Material material) {
        return composition.containsKey(material);
    }

    @Override
    public void removeMaterial(Material material) {
        if (containsMaterial(material)) {
            composition.remove(material);
        }
        save();
    }

    @Override
    public void setMaterial(Material material, double percentage) {
        composition.put(material, percentage);
        save();
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public Map<Material, Double> getComposition() {
        return composition;
    }

    @Override
    public void setComposition(Map<Material, Double> composition) {
        this.composition = composition;
    }

    /**
     * Get the Mine's world.
     *
     * @return The World object representing this Mine's world
     */
    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public Material[] getMaterials() {
        return composition.keySet().toArray(new Material[composition.keySet().size()]);
    }
    protected final World world;

    public FileConfiguration getBasicSavedConfig() {
        FileConfiguration config = MineMe.getYAMLMineFile(this);
        config.set("name", name);
        config.set("alias", alias);
        config.set("world", world.getName());
        config.set("type", getType().name());
        config.set("resetDelay", totalResetDelay);
        config.set("broadcastOnReset", broadcastOnReset);
        config.set("broadcastToNearbyOnly", broadcastNearby);
        config.set("broadcastRadius", broadcastRadius);
        ArrayList<String> comp = new ArrayList();
        for (Material m : composition.keySet()) {
            comp.add(m + "=" + composition.get(m));
        }
        config.set("composition", comp);
        return config;
    }
}
