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

import me.ddevil.mineme.MessageManager;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.mines.HologramCompatible;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.mines.MineRepopulator;
import me.ddevil.mineme.mines.MineType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class CuboidMine implements Mine, HologramCompatible {

    protected final String name;
    protected final String world;
    protected final Vector pos1;
    protected final Vector pos2;
    protected Map<Material, Double> composition;
    protected int resetMinutesDelay;
    protected boolean broadcastOnReset;
    protected String broadcastMessage;
    protected File saveFile;
    private int resetDelay;

    public CuboidMine(String name, Location l1, Location l2, Map<Material, Double> composition, boolean broadcastOnReset) {
        if (!l1.getWorld().equals(l2.getWorld())) {
            throw new IllegalArgumentException("Locations must be on the same world");
        }
        l1.setX(Math.min(l1.getBlockX(), l2.getBlockX()));
        l1.setY(Math.min(l1.getBlockY(), l2.getBlockY()));
        l1.setZ(Math.min(l1.getBlockZ(), l2.getBlockZ()));
        l2.setX(Math.max(l1.getBlockX(), l2.getBlockX()));
        l2.setY(Math.max(l1.getBlockY(), l2.getBlockY()));
        l2.setZ(Math.max(l1.getBlockZ(), l2.getBlockZ()));
        broadcastMessage = MineMe.messagesConfig.getString("messages.resetMessage");
        this.world = l1.getWorld().getName();
        this.name = name;
        this.resetMinutesDelay = 1;
        resetDelay = resetMinutesDelay;
        this.pos1 = l1.toVector();
        this.pos2 = l2.toVector();
        this.composition = composition;
        this.broadcastOnReset = broadcastOnReset;
    }

    public void setResetMinutesDelay(int resetMinutesDelay) {
        this.resetMinutesDelay = resetMinutesDelay;
    }

    public int getResetMinutesDelay() {
        return resetMinutesDelay;
    }

    @Override
    public void tictoc() {
        resetDelay--;
        if (resetDelay <= 0) {
            reset();
        }
    }

    public void setComposition(Map<Material, Double> composition) {
        this.composition = composition;
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
     * Get the Mine's world.
     *
     * @return The World object representing this Mine's world
     * @throws IllegalStateException if the world is not loaded
     */
    public World getWorld() {
        World world = Bukkit.getWorld(this.world);
        if (world == null) {
            throw new IllegalStateException("World '" + this.world + "' is not loaded");
        }
        return world;
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
     * Get the minimum Y co-ordinate of this Mine
     *
     * @return the minimum Y co-ordinate
     */
    public int getLowerY() {
        return this.pos1.getBlockY();
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
        saveFile.delete();

    }

    @Override
    public void reset() {

        //Pull players up
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            Location l = p.getLocation();
            if (contains(p)) {
                l.setY(getUpperY() + 2);
                p.teleport(l);
            }
        }
        resetDelay = resetMinutesDelay;
        new MineRepopulator().repopulate(this);
        if (broadcastOnReset) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(MessageManager.translateTagsAndColors(broadcastMessage, this));
            }
        }
    }

    @Override
    public Map<Material, Double> getComposition() {
        return composition;
    }

    @Override
    public synchronized List<Block> getBlocks() {
        ArrayList<Block> blocks = new ArrayList<>();
        for (Block thi : this) {
            blocks.add(thi);
        }
        return blocks;
    }

    @Override
    public void save() {
        File mineFile = MineMe.getMineFile(this);
        YamlConfiguration file = YamlConfiguration.loadConfiguration(mineFile);
        file.set("mine", this);
        try {
            file.save(mineFile);
        } catch (IOException ex) {
            Logger.getLogger(CuboidMine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Material[] getMaterials() {
        return composition.keySet().toArray(new Material[composition.keySet().size()]);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isBroadcastOnReset() {
        return broadcastOnReset;
    }

    @Override
    public void setBroadcastOnReset(boolean broadcastOnReset) {
        this.broadcastOnReset = broadcastOnReset;
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
            config.set("resetDelay", resetMinutesDelay);
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
    public boolean contains(int x, int y, int z) {
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
        if (!world.equals(l.getWorld().getName())) {
            return false;
        }
        return contains(l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }

    @Override
    public boolean contains(Player p) {
        return contains(p.getLocation());
    }

    //Holograms
    private final ArrayList<Hologram> holograms = new ArrayList();

    @Override
    public void setupHolograms() {
        Location l = getCenter();
        Location temp;

        temp = l.clone();
        temp.setY(getUpperY() + 3);
        holograms.add(HologramsAPI.createHologram(MineMe.instance, temp));
        holograms.add(HologramsAPI.createHologram(MineMe.instance, l));
        temp = l.clone();
        temp.add(getSizeX() / 2, 0, 0);
        holograms.add(HologramsAPI.createHologram(MineMe.instance, temp));
        temp = l.clone();
        temp.add((getSizeX() / 2) * -1, 0, 0);
        holograms.add(HologramsAPI.createHologram(MineMe.instance, temp));
        temp = l.clone();
        temp.add(0, 0, getSizeZ() / 2);
        holograms.add(HologramsAPI.createHologram(MineMe.instance, temp));
        temp = l.clone();
        temp.add(0, 0, (getSizeZ() / 2) * -1);
        holograms.add(HologramsAPI.createHologram(MineMe.instance, temp));

        updateHolograms();
    }

    @Override
    public void showHolograms() {
        setupHolograms();
    }

    @Override
    public void hideHolograms() {
        for (Hologram m : holograms) {
            m.delete();
        }
    }

    @Override
    public void updateHolograms() {
        for (Hologram m : holograms) {
            m.clearLines();
            List<String> list = MineMe.getYAMLMineFile(this).getStringList("hologramsText");
            for (int i = 0; i < list.size(); i++) {
                m.appendTextLine(MessageManager.translateTagsAndColors(list.get(i), this));
            }
        }
    }

    @Override
    public boolean isHologramsVisible() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
}
