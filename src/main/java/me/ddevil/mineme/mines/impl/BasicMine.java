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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.ddevil.core.utils.CustomListener;
import me.ddevil.core.utils.items.ItemUtils;
import me.ddevil.mineme.messages.MineMeMessageManager;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.MineMeConfiguration;
import me.ddevil.mineme.challenge.Challenge;
import me.ddevil.mineme.challenge.ChallengeEndListener;
import me.ddevil.mineme.events.MineResetEvent;
import me.ddevil.mineme.events.MineUpdateEvent;
import me.ddevil.mineme.holograms.CompatibleHologram;
import me.ddevil.mineme.mines.HologramCompatible;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.mines.MineManager;
import me.ddevil.mineme.mines.MineRepopulator;
import me.ddevil.mineme.mines.MineUtils;
import me.ddevil.mineme.mines.configs.MineConfig;
import me.ddevil.mineme.storage.StorageManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public abstract class BasicMine extends CustomListener implements Mine {

    //General
    protected final String name;
    protected String alias;
    protected final FileConfiguration config;
    protected File saveFile = MineMe.getMineFile(this);
    protected boolean enabled = true;

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
    protected ItemStack icon;

    //Potions
    private boolean useEffects;
    private final List<PotionEffect> potionEffects;

    public BasicMine(final MineConfig config) {
        this.broadcastMessage = MineMeConfiguration.forceDefaultBroadcastMessage
                ? MineMeMessageManager.globalResetMessage
                : config.isUseCustomBroadcast() ? config.getBroadcastMessage() : MineMeMessageManager.globalResetMessage;
        this.broadcastOnReset = config.isBroadcastOnReset();
        this.broadcastRadius = config.getBroadcastRadius();
        this.broadcastNearby = config.isNearbyBroadcast();
        this.currentResetDelay = totalResetDelay;
        this.totalResetDelay = config.getResetDelay() * 60;
        this.name = config.getName();
        this.alias = MineMeMessageManager.getInstance().translateColors(config.getAlias());
        this.world = config.getWorld();
        this.composition = config.getComposition();
        this.config = config.getConfig();
        this.useEffects = config.getConfig().getBoolean("effects.use");
        this.potionEffects = config.getEffects();
        ConfigurationSection iconsection = config.getConfig().getConfigurationSection("icon");
        this.icon = MineMeMessageManager.getInstance().createIcon(iconsection, this);
    }

    /**
     * Creates a new mine with the default settings
     *
     * @param name The name of the mine
     * @param world The world of the mine
     * @param icon The icon of the mine
     */
    public BasicMine(String name, World world, ItemStack icon) {
        //General
        this.enabled = true;
        this.name = name;
        this.alias = name;
        this.world = world;
        this.composition = new HashMap();
        composition.put(new ItemStack(Material.STONE), 100d);
        //Broadcast
        this.broadcastOnReset = true;
        this.broadcastRadius = 50;
        this.broadcastMessage = MineMeMessageManager.globalResetMessage;
        this.broadcastNearby = false;
        //Resets
        this.totalResetDelay = 300;
        this.currentResetDelay = totalResetDelay;
        this.icon = icon;
        this.config = MineMe.getYAMLMineFile(this);
        this.potionEffects = new ArrayList();
    }

    @Override
    public String getName() {
        return name;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    @Override
    public ItemStack getIcon() {
        return icon;
    }

    public int getResetMinutesDelay() {
        return totalResetDelay / 60;
    }

    public void setResetMinutesDelay(int resetMinutesDelay) {
        this.totalResetDelay = resetMinutesDelay * 60;
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

    @Override
    public void setResetDelay(int resetDelay) {
        this.totalResetDelay = resetDelay * 60;
        save();
        reset();
    }

    public void setNearbyBroadcast(boolean nearbyBroadcast) {
        this.broadcastNearby = nearbyBroadcast;
        save();
    }

    @Override
    public void setBroadcastRange(double broadcastRadius) {
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
        if (useEffects) {
            for (PotionEffect e : getEffects()) {
                for (Player p : getPlayersInside()) {
                    p.addPotionEffect(e, true);
                }
            }
        }
        if (currentResetDelay <= 0) {
            reset();
        }
        lastSecond.clear();
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
            "$3World: $2" + getCenter().getWorld().getName(),
            "$3Location: $2" + getCenter().getBlockX() + ", " + getCenter().getBlockY() + ", " + getCenter().getBlockZ() + ", ",
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
    public double getPercentage(ItemStack m) {
        if (MineUtils.containsRelativeItemStackInComposition(this, m)) {
            m = MineUtils.getItemStackInComposition(this, m);
            for (ItemStack i : composition.keySet()) {
                if (ItemUtils.equalMaterial(i, m)) {
                    if (composition.get(i) == null) {
                        return 0;
                    }
                    return composition.get(i);
                }
            }
            return 0;
        } else {
            return 0;
        }

    }

    @Override
    public float getPercentageRemaining() {
        if (getRemainingBlocks() <= 0) {
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

    @Override
    public List<Player> getPlayersInside() {
        ArrayList<Player> players = new ArrayList();
        for (Player p : world.getPlayers()) {
            if (contains(p)) {
                players.add(p);
            }
        }
        return players;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Block b = e.getBlock();
        if (contains(b)) {
            if (!wasAlreadyBroken(b)) {
                brokenBlocks.add(b);
                lastSecond.add(b);
                new MineUpdateEvent(this).call();
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
                new MineUpdateEvent(this).call();
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
        new MineUpdateEvent(this).call();
        if (this instanceof HologramCompatible) {
            HologramCompatible hc = (HologramCompatible) this;
            hc.softHologramUpdate();
        }
    }

    @Override
    public void setBlockAsBroken(Block block) {
        brokenBlocks.add(block);
        lastSecond.add(block);
        new MineUpdateEvent(this).call();
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
    public boolean containsMaterial(ItemStack material) {
        return MineUtils.containsRelativeItemStackInComposition(this, material);
    }

    @Override
    public void removeMaterial(ItemStack material) {
        material = MineUtils.getItemStackInComposition(this, material);
        boolean found = false;
        for (ItemStack i : getMaterials()) {
            if (i.equals(material)) {
                composition.remove(i);
                found = true;
                break;
            }
        }
        if (!found) {
            MineMe.instance.debug("Tried to remove material "
                    + material.getType() + ":"
                    + material.getData().getData() + " from mine "
                    + name + ", but it isn't a part of the composition.", 2);
        }
        save();
    }

    @Override
    public double getFreePercentage() {
        if (isExceedingMaterials()) {
            return 0;
        }
        return 100 - getTotalPercentage();
    }

    @Override
    public double getExceedingTotal() {
        if (isExceedingMaterials()) {
            return getTotalPercentage() - 100;
        } else {
            return 0;
        }
    }

    @Override
    public void setMaterialPercentage(ItemStack material, double percentage) {
        if (MineUtils.containsRelativeItemStackInComposition(this, material)) {
            material = MineUtils.getItemStackInComposition(this, material);
        }
        if (percentage < 0) {
            percentage = 0;
        }
        MineMe.instance.debug("The percentage of " + material.getType() + ":" + material.getData().getData() + " of mine " + name + " was changed to " + percentage + ".", 2);
        composition.put(material, percentage);
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
    public List<ItemStack> getMaterials() {
        ArrayList<ItemStack> s = new ArrayList();
        s.addAll(composition.keySet());
        return s;
    }
    protected final World world;

    @Override
    public String toString() {
        JSONObject obj = new JSONObject();
        obj.put("name", name);
        obj.put("location", getCenter().toString());
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
    public void setAlias(String alias) {
        this.alias = alias;
        save();
    }

    @Override
    public void clear() {
        for (Block b : this) {
            b.setType(Material.AIR);
        }
    }

    @Override
    public void fill(ItemStack item) {
        for (Block b : this) {
            b.setType(item.getType());
            b.setData(item.getData().getData());
        }
    }

    @Override
    public void disable() {
        clear();
        setEnabled(false);
        if (this instanceof HologramCompatible) {
            HologramCompatible hc = (HologramCompatible) this;
            for (CompatibleHologram hologram : hc.getHolograms()) {
                hologram.delete();
            }
        }
        MineManager.unregisterMine(this);
        save();
    }

    @Override
    public void delete() {
        clear();
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

    @Override
    public Location getTopCenterLocation() {
        return new Location(world, getCenter().getX(), getUpperY() + 1, getCenter().getZ());
    }

    @Override
    public double getTotalPercentage() {
        if (composition == null) {
            return 0;
        }
        double total = 0;
        for (double i : composition.values()) {
            total += i;
        }
        return total;
    }

    @Override
    public int getTotalMaterials() {
        if (composition == null) {
            return 0;
        }
        return composition.keySet().size();
    }

    @Override
    public boolean contains(Block block) {
        return contains(block.getLocation());
    }

    @Override
    public boolean contains(Location l) {
        if (!world.equals(l.getWorld())) {
            return false;
        }
        return contains(l.getX(), l.getY(), l.getZ());
    }

    @Override
    public boolean contains(Player p) {
        return contains(p.getLocation());
    }

    @Override
    public boolean useEffects() {
        return useEffects;
    }

    @Override
    public List<PotionEffect> getEffects() {
        return potionEffects;
    }

    @Override
    public void addPotionEffect(PotionEffect effect) {
        PotionEffectType type = effect.getType();
        if (getEffect(type) != null) {
            removePotionEffect(type);
        }
        potionEffects.add(effect);

    }

    @Override
    public void removePotionEffect(PotionEffectType effectType) {
        for (Iterator<PotionEffect> it = potionEffects.iterator(); it.hasNext();) {
            PotionEffect e = it.next();
            if (e.getType().equals(effectType)) {
                it.remove();
            }
        }
    }

    @Override
    public void clearEffects() {
        potionEffects.clear();
    }

    //Config
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
        if (!c.contains("icon")) {
            c.createSection("icon");
            c.set("icon.type", icon.getType().toString());
            c.set("icon.data", icon.getData().getData());
            c.set("icon.name", icon.getItemMeta().getDisplayName());
            c.set("icon.lore", ItemUtils.getLore(icon));
        }
        ArrayList<String> effects = new ArrayList();
        for (PotionEffect e : potionEffects) {
            effects.add(e.getType().getName() + ":" + (e.getDuration() / 20) + ":" + e.getAmplifier());
        }
        c.createSection("effects");
        c.set("effects.use", useEffects);
        c.set("effects.list", effects);
        ArrayList<String> comp = new ArrayList();
        for (ItemStack m : composition.keySet()) {
            String s = m.getType().name() + ":" + m.getData().getData();
            comp.add(s + "=" + composition.get(m));
        }

        c.set("composition", comp);
        if (this instanceof HologramCompatible) {
            HologramCompatible hc = (HologramCompatible) this;
            c.set("hologramsText", hc.getHologramsLines());
            c.set("useCustomHologramText", hc.useCustomHologramText());

        }
        return c;
    }

    @Override
    public void reset() {
        if (isDeleted()) {
            return;
        }
        MineResetEvent event = (MineResetEvent) new MineResetEvent(this).call();
        if (!event.isCancelled()) {
            MineMe.getInstance().debug("Reseting mine " + name, 2);
            //Pull players up
            for (Player p : getPlayersInside()) {
                Location l = p.getLocation();
                l.setY(getUpperY() + 2);
                p.teleport(l);
            }
            currentResetDelay = totalResetDelay;
            new MineRepopulator().repopulate(this);
            if (broadcastOnReset) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (broadcastNearby) {
                        if (p.getLocation().getWorld() == getCenter().getWorld()) {
                            if (p.getLocation().distance(getCenter()) <= broadcastRadius) {
                                p.sendMessage(
                                        MineMeMessageManager.getInstance().translateAll(
                                                broadcastMessage,
                                                this));
                            }
                        }
                    } else {
                        p.sendMessage(MineMeMessageManager.getInstance().translateAll(broadcastMessage, this));
                    }
                }
            }
            try {
                StorageManager.addReset(this);
            } catch (IOException | ParseException ex) {
                MineMe.instance.printException("There was an error trying to update the total broken blocks in " + name + "'s storage file!", ex);
            }
            brokenBlocks.clear();
        } else {
            MineMe.getInstance().debug("Reset event for mine " + name + " was cancelled", 2);
        }
    }

    @Override
    public void clearMaterials() {
        composition.clear();
    }

    @Override
    public ItemStack getItemStackInComposition(ItemStack item) {
        if (item != null) {
            for (ItemStack a : composition.keySet()) {
                if (a.getType() == item.getType()) {
                    if (a.getData().getData() == item.getData().getData()) {
                        return a;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public boolean containsRelativeItemStackInComposition(ItemStack i) {
        return getItemStackInComposition(i) != null;
    }

    @Override
    public void addMaterial(ItemStack material) {
        if (!containsRelativeItemStackInComposition(material)) {
            setMaterialPercentage(material, 1);
        }
    }

    @Override
    public PotionEffect getEffect(PotionEffectType type) {

        for (PotionEffect effect : getEffects()) {
            if (effect.getType().equals(type)) {
                return effect;
            }
        }
        return null;
    }

}
