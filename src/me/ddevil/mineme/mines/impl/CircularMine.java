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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.mines.MineType;
import me.ddevil.mineme.mines.configs.MineConfig;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 *
 * @author Selma
 */
public class CircularMine extends BasicMine {

    //General
    private Vector center;
    private Vector radius;
    private int minY;
    private int maxY;
    protected File saveFile;

    public CircularMine(MineConfig config) {
        super(config);
    }

    @Override
    public void delete() {
        saveFile.delete();
        deleted = true;

    }

    public CircularMine(String name, World world, boolean broadcastOnReset, boolean nearbyBroadcast, double broadcastRadius, int resetMinutesDelay, Vector center, double radius) {
        super(name, world, broadcastOnReset, nearbyBroadcast, broadcastRadius, resetMinutesDelay);
        this.center = center;
        this.radius = new Vector(center.getX(), center.getY() - radius, center.getZ());
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void save() {
        FileConfiguration file = getBasicSavedConfig();
        file.set("centerX", center.getX());
        file.set("centerY", center.getY());
        file.set("centerZ", center.getZ());
        file.set("radius", radius.distance(center));
        try {
            file.save(MineMe.getMineFile(this));
        } catch (IOException ex) {
            Logger.getLogger(CuboidMine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public MineType getType() {
        return MineType.CIRCULAR;
    }

    @Override
    public boolean contains(double x, double y, double z) {
        if (y < minY || y > maxY) {
            return false;
        }
        Vector v = new Vector(x, y, z).subtract(center).divide(radius);
        return v.getX() * v.getX() + v.getZ() * v.getZ() <= 1;
    }

    @Override
    public boolean contains(Block b) {
        return contains(b.getLocation());
    }

    @Override
    public boolean contains(Location l) {
        return contains(l.getX(), l.getY(), l.getZ());
    }

    @Override
    public boolean contains(Player p) {
        return contains(p.getLocation());
    }

    @Override
    public Location getLocation() {
        return center.toLocation(world);
    }

    @Override
    public int getVolume() {
        return getArea() * getHeight();
    }

    @Override
    public Iterator<Block> iterator() {
        return new CircularIterator(this);
    }

    public int getArea() {
        return (int) Math.floor(radius.getX() * radius.getZ() * Math.PI * getHeight());
    }

    public int getHeight() {
        return maxY - minY + 1;
    }

    public int getLength() {
        return (int) (2 * radius.getZ());
    }

    public Vector getRadius() {
        return radius;
    }

    public Vector getCenter() {
        return center;
    }

    @Override
    public int getMinimumY() {
        return minY;
    }

    @Override
    public int getMaximumY() {
        return maxY;
    }

    @Override
    public Vector getMinimumPoint() {
        Vector base = center.subtract(getRadius());
        return new Vector(base.getX(), minY, base.getY());
    }

    @Override
    public Vector getMaximumPoint() {
        Vector base = center.add(getRadius());
        return new Vector(base.getX(), maxY, base.getY());
    }

    public class CircularIterator implements Iterator<Block> {

        private final CircularMine mine;
        //X
        private final int minX;
        private final int maxX;
        private int nextX;

        //Y
        private final int minY;
        private final int maxY;
        private int nextY;
        //Z
        private final int minZ;
        private final int maxZ;
        private int nextZ;
        //World
        private final World world;

        public CircularIterator(CircularMine mine) {
            this.mine = mine;
            Vector min = mine.getCenter();
            Vector max = mine.getMaximumPoint();
            //X
            this.minX = min.getBlockX();
            this.maxX = max.getBlockX();
            this.nextX = minX;
            //Y
            this.minY = min.getBlockY();
            this.maxY = max.getBlockY();
            this.nextY = minY;
            //Z
            this.minZ = max.getBlockZ();
            this.maxZ = max.getBlockZ();
            this.nextZ = minZ;
            this.world = mine.getWorld();
            forward();
        }

        @Override
        public boolean hasNext() {
            return nextX != Integer.MIN_VALUE;
        }

        private void forward() {
            while (hasNext() && !mine.contains(new Location(mine.world, nextX, minY, nextZ))) {
                forwardOne();
            }
        }

        @Override
        public Block next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException();
            }

            Block b = new Location(world, nextX, nextY, nextZ).getBlock();
            forwardOne();
            forward();
            if (nextY < maxY) {
                nextY++;
            } else if (hasNext()) {

                nextY = minY;
            }
            return b;
        }

        private void forwardOne() {
            if (++nextX <= maxX) {
                return;
            }
            nextX = minX;

            if (++nextZ <= maxZ) {
                return;
            }
            nextX = Integer.MIN_VALUE;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}
