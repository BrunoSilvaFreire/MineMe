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
 * along with this progra  If not, see <http://www.gnu.org/licenses/>.
 */
package me.ddevil.mineme.mines.impl;

import me.ddevil.mineme.messages.MineMeMessageManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.events.MineHologramUpdateEvent;
import me.ddevil.mineme.events.MineResetEvent;
import me.ddevil.mineme.holograms.CompatibleHologram;
import me.ddevil.mineme.mines.HologramCompatible;
import me.ddevil.mineme.mines.MineManager;
import me.ddevil.mineme.mines.MineRepopulator;
import me.ddevil.mineme.mines.MineType;
import me.ddevil.mineme.mines.configs.MineConfig;
import me.ddevil.mineme.storage.StorageManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.json.simple.parser.ParseException;

public class CuboidMine extends BasicMine implements HologramCompatible {

    protected final Vector pos1;
    protected final Vector pos2;
    private List<String> hologramsLines;

    public CuboidMine(String name, Location l1, Location l2, Map<Material, Double> composition, Integer delay, boolean broadcastOnReset, boolean nearbyBroadcast, double broadcastRadius) {
        super(name, l1.getWorld(), broadcastOnReset, nearbyBroadcast, broadcastRadius, delay);
        if (!l1.getWorld().equals(l2.getWorld())) {
            throw new IllegalArgumentException("Locations must be on the same world");
        }
        Location fl1 = l1.clone();
        Location fl2 = l2.clone();
        fl1.setX(Math.min(l1.getBlockX(), l2.getBlockX()));
        fl1.setY(Math.min(l1.getBlockY(), l2.getBlockY()));
        fl1.setZ(Math.min(l1.getBlockZ(), l2.getBlockZ()));
        fl2.setX(Math.max(l1.getBlockX(), l2.getBlockX()));
        fl2.setY(Math.max(l1.getBlockY(), l2.getBlockY()));
        fl2.setZ(Math.max(l1.getBlockZ(), l2.getBlockZ()));
        this.pos1 = l1.toVector();
        this.pos2 = l2.toVector();
        this.composition = composition;
        this.config = MineMe.getYAMLMineFile(this);
    }

    public CuboidMine(String name, Location l1, Location l2, Map<Material, Double> composition, Integer delay, boolean broadcastOnReset, boolean nearbyBroadcast, double broadcastRadius, String resetMsg) {
        super(name, l1.getWorld(), broadcastOnReset, nearbyBroadcast, resetMsg, broadcastRadius, delay);
        if (!l1.getWorld().equals(l2.getWorld())) {
            throw new IllegalArgumentException("Locations must be on the same world");
        }
        Location fl1 = l1.clone();
        Location fl2 = l2.clone();
        fl1.setX(Math.min(l1.getBlockX(), l2.getBlockX()));
        fl1.setY(Math.min(l1.getBlockY(), l2.getBlockY()));
        fl1.setZ(Math.min(l1.getBlockZ(), l2.getBlockZ()));
        fl2.setX(Math.max(l1.getBlockX(), l2.getBlockX()));
        fl2.setY(Math.max(l1.getBlockY(), l2.getBlockY()));
        fl2.setZ(Math.max(l1.getBlockZ(), l2.getBlockZ()));
        this.pos1 = l1.toVector();
        this.pos2 = l2.toVector();
        this.composition = composition;
        this.config = MineMe.getYAMLMineFile(this);
    }

    public CuboidMine(MineConfig config) {
        super(config);
        this.pos1 = new Location(world,
                config.getConfig().getDouble("X1"),
                config.getConfig().getDouble("Y1"),
                config.getConfig().getDouble("Z1")).toVector();
        this.pos2 = new Location(world,
                config.getConfig().getDouble("X2"),
                config.getConfig().getDouble("Y2"),
                config.getConfig().getDouble("Z2")).toVector();
        composition = config.getComposition();
        this.config = config.getConfig();

    }

    public Vector getPos2() {
        return pos2;
    }

    public Vector getPos1() {
        return pos1;
    }

    public File getSaveFile() {
        return saveFile;
    }

    public void setSaveFile(File saveFile) {
        this.saveFile = saveFile;
    }

    /**
     * Get the size of this Cuboid along the X axis
     *
     * @return Size of Cuboid along the X axis
     */
    public int getSizeX() {
        return (this.pos2.getBlockX() - this.pos1.getBlockX()) + 1;
    }

    /**
     * Get the size of this Cuboid along the Y axis
     *
     * @return Size of Cuboid along the Y axis
     */
    public int getSizeY() {
        return (this.pos2.getBlockY() - this.pos1.getBlockY()) + 1;
    }

    /**
     * Get the size of this Cuboid along the Z axis
     *
     * @return Size of Cuboid along the Z axis
     */
    public int getSizeZ() {
        return (this.pos2.getBlockZ() - this.pos1.getBlockZ()) + 1;
    }

    /**
     * Get the Location of the lower northeast corner of the Mine (minimum XYZ
     * co-ordinates).
     *
     * @return Location of the lower northeast corner
     */
    public Location getLowerNE() {
        return new Location(getWorld(), pos1.getX(), pos1.getY(), pos1.getZ());
    }

    /**
     * Get the Location of the upper southwest corner of the Mine (maximum XYZ
     * co-ordinates).
     *
     * @return Location of the upper southwest corner
     */
    public Location getUpperSW() {
        return new Location(getWorld(), pos2.getX(), pos2.getY(), pos2.getZ());
    }

    /**
     * Get the the centre of the Mine.
     *
     * @return Location at the centre of the Mine
     */
    public Location getCenter() {
        int x1 = this.getUpperX() + 1;
        int y1 = this.getUpperY() + 1;
        int z1 = this.getUpperZ() + 1;
        return new Location(this.getWorld(), this.getLowerX() + (x1 - this.getLowerX()) / 2.0, this.getMinimumY() + (y1 - this.getMinimumY()) / 2.0, this.getMaximumY() + (z1 - this.getMaximumY()) / 2.0);
    }

    /**
     * Get the minimum X co-ordinate of this Mine
     *
     * @return the minimum X co-ordinate
     */
    public int getLowerX() {
        return this.pos1.getBlockX();
    }

    /**
     * Get the minimum Y co-ordinate of this Mine
     *
     * @return the minimum Y co-ordinate
     */
    @Override
    public int getMinimumY() {
        return this.pos1.getBlockY();
    }

    /**
     * Get the minimum Z co-ordinate of this Mine
     *
     * @return the minimum Z co-ordinate
     */
    @Override
    public int getMaximumY() {
        return this.pos1.getBlockZ();
    }

    /**
     * Get the maximum X co-ordinate of this Mine
     *
     * @return the maximum X co-ordinate
     */
    public int getUpperX() {
        return this.pos2.getBlockX();
    }

    /**
     * Get the maximum Y co-ordinate of this Mine
     *
     * @return the maximum Y co-ordinate
     */
    public int getUpperY() {
        return this.pos2.getBlockY();
    }

    /**
     * Get the maximum Z co-ordinate of this Mine
     *
     * @return the maximum Z co-ordinate
     */
    public int getUpperZ() {
        return this.pos2.getBlockZ();
    }

    @Override
    public Iterator<Block> iterator() {
        return new CuboidMineIterator(this.getWorld(), this.pos1.getBlockX(), this.pos1.getBlockY(), this.pos1.getBlockZ(), this.pos2.getBlockX(), this.pos2.getBlockY(), this.pos2.getBlockZ());
    }

    @Override
    public void delete() {
        setEnabled(false);
        save();
        MineManager.unregisterMine(this);
        deleted = true;
        for (CompatibleHologram hologram : holograms) {
            hologram.delete();
        }
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
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                Location l = p.getLocation();
                if (contains(p)) {
                    l.setY(getUpperY() + 2);
                    p.teleport(l);
                }
            }
            currentResetDelay = totalResetDelay;
            new MineRepopulator().repopulate(this);
            if (broadcastOnReset) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (broadcastNearby) {
                        if (p.getLocation().distance(getCenter()) <= broadcastRadius) {
                            p.sendMessage(
                                    MineMeMessageManager.translateTagsAndColors(
                                            broadcastMessage,
                                            this));
                        }
                    } else {
                        p.sendMessage(MineMeMessageManager.translateTagsAndColors(broadcastMessage, this));
                    }
                }
            }
            try {
                StorageManager.addReset(this);
            } catch (IOException | ParseException ex) {
                MineMe.instance.printException("There was an error trying to update the total broken blocks in " + name + "'s storage file!", ex);
            }
            brokenBlocks.clear();
            if (MineMe.useHolograms) {
                updateHolograms();
            }
        } else {
            MineMe.getInstance().debug("Reset event for mine " + name + " was cancelled", 2);
        }
    }

    @Override
    public Location getLocation() {
        return getCenter();
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     *
     * @param resetMinutesDelay
     */
    public void setResetMinutesDelay(int resetMinutesDelay) {
        this.totalResetDelay = resetMinutesDelay;
    }

    @Override
    public int getResetMinutesDelay() {
        return totalResetDelay;
    }

    public FileConfiguration toConfig() {
        try {
            File f = new File(MineMe.minesFolder.getCanonicalPath() + "/" + name + ".yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(f);
            config.set("X1", pos1.getBlockX());
            config.set("Y1", pos1.getBlockY());
            config.set("Z1", pos1.getBlockZ());
            config.set("X2", pos2.getBlockX());
            config.set("Y2", pos2.getBlockY());
            config.set("Z2", pos2.getBlockZ());
            config.set("name", name);
            config.set("world", world);
            ArrayList<String> al = new ArrayList<>();
            for (Material m : getMaterials()) {
                al.add(m + "=" + composition.get(m));
            }
            config.set("composition", al);
            config.set("resetDelay", totalResetDelay);
            return config;
        } catch (IOException ex) {
            return null;
        }

    }

    @Override
    public MineType getType() {
        return MineType.CUBOID;
    }

    /**
     * Return true if the point at (x,y,z) is contained within this Cuboid.
     *
     * @param x - The X co-ordinate
     * @param y - The Y co-ordinate
     * @param z - The Z co-ordinate
     * @return true if the given point is within this Cuboid, false otherwise
     */
    @Override
    public boolean contains(double x, double y, double z) {
        return x >= this.pos1.getBlockX() && x <= this.pos2.getBlockX()
                && y >= this.pos1.getBlockY() && y <= this.pos2.getBlockY()
                && z >= this.pos1.getBlockZ() && z <= this.pos2.getBlockZ();
    }

    /**
     * Check if the given Block is contained within this Cuboid.
     *
     * @param b - The Block to check for
     * @return true if the Block is within this Cuboid, false otherwise
     */
    @Override
    public boolean contains(Block b) {
        return this.contains(b.getLocation());
    }

    /**
     * Check if the given Location is contained within this Cuboid.
     *
     * @param l - The Location to check for
     * @return true if the Location is within this Cuboid, false otherwise
     */
    @Override
    public boolean contains(Location l) {
        if (!world.equals(l.getWorld())) {
            return false;
        }
        return contains(l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

    @Override
    public boolean contains(Player p) {
        return contains(p.getLocation());
    }

    //Holograms
    private final ArrayList<CompatibleHologram> holograms = new ArrayList();
    private boolean hologramsReady = false;

    @Override
    public void setupHolograms() {
        MineMe.getInstance().debug("Creating holograms for " + name + "...");
        Location l = getCenter();
        Location temp;
        temp = l.clone();
        temp.setY(getUpperY() + 4);
        holograms.add(MineMe.hologramAdapter.createHologram(temp));
        temp = l.clone();
        temp.add(getSizeX() / 2 + 1, 0, 0);
        holograms.add(MineMe.hologramAdapter.createHologram(temp));
        temp = l.clone();
        temp.add(((getSizeX() / 2) * -1) - 1, 0, 0);
        holograms.add(MineMe.hologramAdapter.createHologram(temp));
        temp = l.clone();
        temp.add(0, 0, (getSizeZ() / 2) + 1);
        holograms.add(MineMe.hologramAdapter.createHologram(temp));
        temp = l.clone();
        temp.add(0, 0, ((getSizeZ() / 2) * -1) - 1);
        holograms.add(MineMe.hologramAdapter.createHologram(temp));
        MineMe.getInstance().debug("Created " + holograms.size() + " holograms.");
        hologramsLines = MineMe.forceDefaultHolograms
                ? MineMe.defaultHologramText
                : config.getBoolean("useCustomHologramText")
                        ? config.getStringList("hologramsText")
                        : MineMe.defaultHologramText;
        updateHolograms();

        hologramsReady = true;
    }

    @Override
    public void showHolograms() {
        updateHolograms();

    }

    @Override
    public void hideHolograms() {
        for (CompatibleHologram m : holograms) {
            m.clearLines();
        }
    }

    @Override
    public void updateHolograms() {
        if (isDeleted()) {
            return;
        }
        if (holograms.isEmpty()) {
            return;
        }
        MineMe.getInstance().debug("Updating holograms for " + name, 2);
        MineMe.getInstance().debug("Total lines: " + hologramsLines.size(), 2);
        MineHologramUpdateEvent event = (MineHologramUpdateEvent) new MineHologramUpdateEvent(this).call();
        if (!event.isCancelled()) {
            for (CompatibleHologram h : holograms) {
                h.clearLines();
                for (int i = 0; i < hologramsLines.size(); i++) {
                    String text = hologramsLines.get(i);
                    h.appendTextLine(MineMeMessageManager.translateTagsAndColors(text, this));
                }
            }
            MineMe.getInstance().debug("Holograms updated", 2);
        } else {
            MineMe.getInstance().debug("Hologram Update Event for mine " + name + " was cancelled", 2);
        }
    }
    private Integer lightHologramUpdateId;

    @Override
    public void softHologramUpdate() {
        if (lightHologramUpdateId == null) {
            MineMe.getInstance().debug("Updating hologram softly.");
            lightHologramUpdateId = Bukkit.getScheduler().scheduleSyncDelayedTask(MineMe.instance, new Runnable() {

                @Override
                public void run() {
                    updateHolograms();
                    lightHologramUpdateId = null;
                }
            }, 60l);
        }
    }

    @Override
    public boolean isHologramsVisible() {
        return hologramsReady;
    }

    //Statistics
    @Override
    public int getVolume() {
        return this.getSizeX() * this.getSizeY() * this.getSizeZ();
    }

    @Override
    public Vector getMaximumPoint() {
        return pos2;
    }

    @Override
    public Vector getMinimumPoint() {
        return pos1;
    }

    public class CuboidMineIterator implements Iterator<Block> {

        private final World w;
        private final int baseX, baseY, baseZ;
        private int x, y, z;
        private final int sizeX, sizeY, sizeZ;

        public CuboidMineIterator(World w, int x1, int y1, int z1, int x2, int y2, int z2) {
            this.w = w;
            this.baseX = x1;
            this.baseY = y1;
            this.baseZ = z1;
            this.sizeX = Math.abs(x2 - x1) + 1;
            this.sizeY = Math.abs(y2 - y1) + 1;
            this.sizeZ = Math.abs(z2 - z1) + 1;
            this.x = this.y = this.z = 0;
        }

        @Override
        public boolean hasNext() {
            return this.x < this.sizeX && this.y < this.sizeY && this.z < this.sizeZ;
        }

        @Override
        public Block next() {
            Block b = this.w.getBlockAt(this.baseX + this.x, this.baseY + this.y, this.baseZ + this.z);
            if (++x >= this.sizeX) {
                this.x = 0;
                if (++this.y >= this.sizeY) {
                    this.y = 0;
                    ++this.z;
                }
            }
            return b;
        }

        @Override
        public void remove() {
        }
    }

    public enum CuboidDirection {

        North, East, South, West, Up, Down, Horizontal, Vertical, Both, Unknown;

        public CuboidDirection opposite() {
            switch (this) {
                case North:
                    return South;
                case East:
                    return West;
                case South:
                    return North;
                case West:
                    return East;
                case Horizontal:
                    return Vertical;
                case Vertical:
                    return Horizontal;
                case Up:
                    return Down;
                case Down:
                    return Up;
                case Both:
                    return Both;
                default:
                    return Unknown;
            }
        }
    }

    @Override
    public void save() {
        FileConfiguration file = getBasicSavedConfig();
        file.set("X1", pos1.getBlockX());
        file.set("Y1", pos1.getBlockY());
        file.set("Z1", pos1.getBlockZ());
        file.set("X2", pos2.getBlockX());
        file.set("Y2", pos2.getBlockY());
        file.set("Z2", pos2.getBlockZ());
        try {
            file.save(MineMe.getMineFile(this));
        } catch (IOException ex) {
            Logger.getLogger(CuboidMine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
