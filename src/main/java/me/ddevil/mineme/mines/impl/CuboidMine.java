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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.ddevil.core.utils.items.ItemUtils;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.holograms.CompatibleHologram;
import me.ddevil.mineme.mines.MineType;
import me.ddevil.mineme.mines.configs.MineConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class CuboidMine extends BasicHologramMine {

    protected final Vector pos1;
    protected final Vector pos2;

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
    }

    public CuboidMine(String name, Location loc1, Location loc2) {
        super(name, loc1.getWorld(), ItemUtils.createItem(Material.STONE, name));
        this.pos1 = loc1.toVector();
        this.pos2 = loc2.toVector();
        config.set("X1", pos1.getBlockX());
        config.set("Y1", pos1.getBlockY());
        config.set("Z1", pos1.getBlockZ());
        config.set("X2", pos2.getBlockX());
        config.set("Y2", pos2.getBlockY());
        config.set("Z2", pos2.getBlockZ());
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
    @Override
    public Location getCenter() {
        int x1 = this.getUpperX() + 1;
        int y1 = this.getUpperY() + 1;
        int z1 = this.getUpperZ() + 1;

        return new Location(this.getWorld(), this.getLowerX() + (x1 - this.getLowerX()) / 2.0, this.getLowerY() + (y1 - this.getLowerY()) / 2.0, this.getLowerZ() + (z1 - this.getLowerZ()) / 2.0);
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
     * Get the minimum Z co-ordinate of this Mine
     *
     * @return the minimum Z co-ordinate
     */
    public int getLowerZ() {
        return this.pos1.getBlockZ();
    }

    /**
     * Get the minimum Y co-ordinate of this Mine
     *
     * @return the minimum Y co-ordinate
     */
    @Override
    public int getLowerY() {
        return this.pos1.getBlockY();
    }

    /**
     * Get the minimum Z co-ordinate of this Mine
     *
     * @return the minimum Z co-ordinate
     */
    @Override
    public int getUpperY() {
        return this.pos2.getBlockY();
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
    public String getName() {
        return name;
    }

    public FileConfiguration toConfig() {
        try {
            File f = new File(MineMe.minesFolder.getCanonicalPath() + "/" + name + ".yml");
            FileConfiguration tempConfig = YamlConfiguration.loadConfiguration(f);
            tempConfig.set("X1", pos1.getBlockX());
            tempConfig.set("Y1", pos1.getBlockY());
            tempConfig.set("Z1", pos1.getBlockZ());
            tempConfig.set("X2", pos2.getBlockX());
            tempConfig.set("Y2", pos2.getBlockY());
            tempConfig.set("Z2", pos2.getBlockZ());
            tempConfig.set("name", name);
            tempConfig.set("world", world);
            ArrayList<String> al = new ArrayList<>();
            for (ItemStack m : getMaterials()) {
                al.add(m.getType() + ":" + m.getData().getData() + "=" + composition.get(m));
            }
            tempConfig.set("composition", al);
            tempConfig.set("resetDelay", totalResetDelay);
            return tempConfig;
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
                && y >= this.pos1.getBlockY() && y <= this.pos2.getBlockY() + 1
                && z >= this.pos1.getBlockZ() && z <= this.pos2.getBlockZ();
    }

    //Holograms
    @Override
    public List<String> getHologramsLines() {
        return hologramsLines;
    }

    @Override
    public void setHologramsLines(List<String> lines) {
        this.hologramsLines = lines;
    }

    @Override
    public void placeHolograms() {
        MineMe.getInstance().debug("Creating holograms for " + name + "...");
        try {
            Location l = getCenter();
            Location hololoc;
            hololoc = l.clone();
            hololoc.setY(getUpperY());
            holograms.add(MineMe.hologramAdapter.createHologram(hololoc));
            hololoc = l.clone();
            hololoc.add(getSizeX() / 2 + 1, 0, 0);
            holograms.add(MineMe.hologramAdapter.createHologram(hololoc));
            hololoc = l.clone();
            hololoc.add(((getSizeX() / 2) * -1) - 1, 0, 0);
            holograms.add(MineMe.hologramAdapter.createHologram(hololoc));
            hololoc = l.clone();
            hololoc.add(0, 0, (getSizeZ() / 2) + 1);
            holograms.add(MineMe.hologramAdapter.createHologram(hololoc));
            hololoc = l.clone();
            hololoc.add(0, 0, ((getSizeZ() / 2) * -1) - 1);
            holograms.add(MineMe.hologramAdapter.createHologram(hololoc));
            MineMe.getInstance().debug("Created " + holograms.size() + " holograms.");
            hologramsReady = true;
        } catch (Exception e) {
            MineMe.instance.printException("There was an error creating hologram for mine " + name + "!", e);
        }
    }

    //Statistics
    @Override
    public int getVolume() {
        if (pos1 == null || pos2 == null) {
            return 0;
        }
        return this.getSizeX() * this.getSizeY() * this.getSizeZ();

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
    public List<CompatibleHologram> getHolograms() {
        return holograms;
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
            Logger.getLogger(CuboidMine.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
}
