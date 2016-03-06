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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.ddevil.mineme.messages.MineMeMessageManager;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.challenge.Challenge;
import me.ddevil.mineme.challenge.ChallengeEndListener;
import me.ddevil.mineme.holograms.CompatibleHologram;
import me.ddevil.mineme.mines.HologramCompatible;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.mines.MineManager;
import me.ddevil.mineme.mines.configs.MineConfig;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

public abstract class BasicMine implements Mine {

    //General
    protected final String name;
    protected final String alias;
    protected FileConfiguration config;
    protected File saveFile = MineMe.getMineFile(this);
    protected boolean enabled = false;

    //Messages
    protected boolean broadcastOnReset;
    protected String broadcastMessage;
    protected double broadcastRadius;
    protected boolean broadcastNearby;

    //Blocks
    protected Map<ItemStack, Double> composition;
    protected final ArrayList<Block> brokenBlocks = new ArrayList();
    protected final ArrayList<Block> lastSecond = new ArrayList();

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
        this.totalResetDelay = config.getResetDelay() * 60;
        this.name = config.getName();
        this.alias = MineMeMessageManager.translateColors(config.getAlias());
        this.world = config.getWorld();
        this.composition = config.getComposition();
        this.config = config.getConfig();
    }

    /**
     * Creates a new mine with the default settings
     *
     * @param name The name of the mine
     * @param world The world of the mine
     */
    public BasicMine(String name, World world) {
        //General
        this.enabled = true;
        this.name = name;
        this.alias = name;
        this.world = world;
        HashMap<ItemStack, Double> composition = new HashMap();
        composition.put(new ItemStack(Material.STONE), 100d);
        this.composition = composition;
        //Broadcast
        this.broadcastOnReset = true;
        this.broadcastRadius = 50;
        this.broadcastMessage = MineMeMessageManager.globalResetMessage;
        this.broadcastNearby = false;
        //Resets
        this.totalResetDelay = 300;
        this.currentResetDelay = totalResetDelay;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getResetMinutesDelay() {
        return totalResetDelay / 60;
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
    public void secondCountdown() {
        currentResetDelay--;
        lastSecond.clear();
        if (currentResetDelay <= 0) {
            reset();
        }
    }

    @Override
    public int averageBreakSpeed() {
        return lastSecond.size();
    }

    @Override
    public List<String> getInfo() {
        String[] basic = new String[]{
            "%header%",
            "$3Name: $2" + getName(),
            "$3Alias: $2" + getAlias(),
            "$3Type: $2" + getType(),
            "$3World: $2" + getLocation().getWorld().getName(),
            "$3Location: $2" + getLocation().getBlockX() + ", " + getLocation().getBlockY() + ", " + getLocation().getBlockZ() + ", ",
            "$3Broadcast on reset: $2" + broadcastOnReset(),
            "$3Nearby broadcast: $2" + broadcastToNearbyOnly(),
            "$3Broadcast radius: $2" + broadcastRadius(),
            "$3Composition: $2"
        };
        ArrayList<String> comp = new ArrayList();

        comp.addAll(Arrays.asList(basic));
        for (ItemStack m : getMaterials()) {
            comp.add("$3" + m.getType() + "$1:$3" + m.getData().getData() + " $2= $1" + composition.get(m));
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
        for (ItemStack i : getMaterials()) {
            if (i.getType() == m) {
                return composition.get(i);
            }
        }
        return 0;

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
                lastSecond.add(b);
            }
        }
    }

    @EventHandler
    public void onBreak(BlockExplodeEvent e) {
        Block b = e.getBlock();
        if (contains(b)) {
            if (!wasAlreadyBroken(b)) {
                brokenBlocks.add(b);
                lastSecond.add(b);
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
                    lastSecond.add(b);
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
        lastSecond.add(block);
        if (this instanceof HologramCompatible) {
            HologramCompatible hc = (HologramCompatible) this;
            hc.softHologramUpdate();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean containsMaterial(Material material) {
        for (ItemStack i : getMaterials()) {
            if (i.getType() == material) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void removeMaterial(Material material) {
        for (ItemStack i : getMaterials()) {
            if (i.getType() == material) {
                composition.remove(i);
                break;
            }
        }
        save();
    }

    @Override
    public void setMaterial(Material material, double percentage) {
        composition.put(new ItemStack(material), percentage);
        save();
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public Map<ItemStack, Double> getComposition() {
        return composition;
    }

    @Override
    public void setComposition(Map<ItemStack, Double> composition) {
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
    public ItemStack[] getMaterials() {
        return composition.keySet().toArray(new ItemStack[composition.keySet().size()]);
    }
    protected final World world;

    public FileConfiguration getBasicSavedConfig() {
        FileConfiguration c = MineMe.getYAMLMineFile(this);
        c.set("enabled", enabled);
        c.set("name", name);
        c.set("alias", alias);
        c.set("world", world.getName());
        c.set("type", getType().name());
        c.set("resetDelay", getResetMinutesDelay());
        c.set("broadcastOnReset", broadcastOnReset);
        c.set("broadcastToNearbyOnly", broadcastNearby);
        c.set("broadcastRadius", broadcastRadius);
        ArrayList<String> comp = new ArrayList();
        for (ItemStack m : composition.keySet()) {
            String s = m.getType().name() + ":" + m.getData().getData();
            comp.add(s + "=" + composition.get(m));
        }
        c.set("composition", comp);
        return c;
    }

    @Override
    public String toString() {
        JSONObject obj = new JSONObject();
        obj.put("name", name);
        obj.put("location", getLocation().toString());
        return obj.toJSONString();
    }

    @Override
    public int getTimeToNextReset() {
        return currentResetDelay;
    }

    @Override
    public boolean isCompletelyBroken() {
        return getRemainingBlocks() <= 0;
    }
    //Challenges
    protected Challenge currentChallenge;
    protected ArrayList<Challenge> challengeQueue = new ArrayList();

    @Override
    public void addChallengeToQueue(Challenge challenge) {
        challengeQueue.add(challenge);
    }

    @Override
    public void forceSetCurrentChallenge(Challenge challenge) {
        if (currentChallenge != null) {
            currentChallenge.complete(ChallengeEndListener.ChallengeResult.FAILED);
        }
        currentChallenge = challenge;
    }

    @Override
    public void delete() {
        setEnabled(false);
        MineMe.getMineFile(this).delete();
        MineManager.unregisterMine(this);
        deleted = true;
        if (this instanceof HologramCompatible) {
            HologramCompatible hc = (HologramCompatible) this;
            for (CompatibleHologram hologram : hc.getHolograms()) {
                hologram.delete();
            }
        }
    }

    @Override
    public Challenge getCurrentChallenge() {
        return currentChallenge;
    }

    @Override
    public boolean isRunningAChallenge() {
        return currentChallenge != null;
    }

}
