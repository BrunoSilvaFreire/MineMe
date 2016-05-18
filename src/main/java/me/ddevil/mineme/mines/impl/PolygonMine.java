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

import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.selections.Polygonal2DSelection;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.ddevil.core.utils.items.ItemUtils;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.mines.MineType;
import me.ddevil.mineme.mines.configs.MineConfig;
import me.ddevil.mineme.utils.WorldEditIterator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 *
 * @author Selma
 */
public class PolygonMine extends BasicHologramMine {

    private final Vector[] points;
    private final int height;
    private final Polygonal2DRegion area;

    public PolygonMine(MineConfig config) {
        super(config);
        FileConfiguration fileConfig = config.getConfig();
        ConfigurationSection pointsConfig = fileConfig.getConfigurationSection("points");
        ArrayList<BlockVector2D> bvectors = new ArrayList();
        ArrayList<Vector> vectors = new ArrayList();

        Integer lowestPoint = null;
        Integer hightestPoint = null;
        for (String pointsPath : pointsConfig.getKeys(false)) {
            String pointPath = "points." + pointsPath;
            int x = fileConfig.getInt(pointPath + ".x");
            int y = fileConfig.getInt(pointPath + ".y");
            int z = fileConfig.getInt(pointPath + ".z");
            Vector v = new Vector(x, y, z);
            vectors.add(v);
            BlockVector2D bv = new BlockVector2D(x, z);
            bvectors.add(bv);
            if (lowestPoint == null) {
                lowestPoint = y;
            } else if (y < lowestPoint) {
                lowestPoint = y;
            }
            if (hightestPoint == null) {
                hightestPoint = y;
            } else if (y > hightestPoint) {
                hightestPoint = y;
            }
        }
        this.area = new Polygonal2DRegion(new BukkitWorld(world), bvectors, lowestPoint, hightestPoint);
        this.points = vectors.toArray(new Vector[vectors.size()]);
        this.height = hightestPoint - lowestPoint;
    }

    public PolygonMine(Vector[] points, String name, World world, ItemStack icon) {
        super(name, world, icon);
        this.points = points;
        ArrayList<BlockVector2D> bvectors = new ArrayList();
        Integer lowestPoint = null;
        Integer hightestPoint = null;

        for (Vector point : points) {
            BlockVector2D bv = new BlockVector2D(point.getX(), point.getZ());
            bvectors.add(bv);
            if (lowestPoint == null) {
                lowestPoint = point.getBlockY();
            } else if (point.getBlockY() < lowestPoint) {
                lowestPoint = point.getBlockY();
            }
            if (hightestPoint == null) {
                hightestPoint = point.getBlockY();
            } else if (point.getBlockY() > hightestPoint) {
                hightestPoint = point.getBlockY();
            }
        }
        this.area = new Polygonal2DRegion(new BukkitWorld(world), bvectors, lowestPoint, hightestPoint);
        this.height = hightestPoint - lowestPoint;

    }

    public PolygonMine(Polygonal2DRegion region, String name, World world) {
        super(name, world, ItemUtils.createItem(Material.REDSTONE_BLOCK, name));
        Integer lowestPoint = null;
        Integer hightestPoint = null;
        ArrayList<Vector> vectors = new ArrayList();
        boolean min = false;
        for (BlockVector2D bv : region.getPoints()) {
            Vector point = new Vector(bv.getBlockX(), min ? region.getMinimumY() : region.getMaximumY(), bv.getBlockZ());
            min = !min;
            vectors.add(point);
            if (lowestPoint == null) {
                lowestPoint = point.getBlockY();
            } else if (point.getBlockY() < lowestPoint) {
                lowestPoint = point.getBlockY();
            }
            if (hightestPoint == null) {
                hightestPoint = point.getBlockY();
            } else if (point.getBlockY() > hightestPoint) {
                hightestPoint = point.getBlockY();
            }
        }
        this.points = vectors.toArray(new Vector[vectors.size()]);
        this.area = region;
        this.height = hightestPoint - lowestPoint;

    }

    public PolygonMine(Polygonal2DSelection region, String name, World world) {
        super(name, world, ItemUtils.createItem(Material.REDSTONE_BLOCK, name));
        Integer lowestPoint = null;
        Integer hightestPoint = null;
        ArrayList<Vector> vectors = new ArrayList();
        boolean min = false;
        for (BlockVector2D bv : region.getNativePoints()) {
            Vector point = new Vector(bv.getBlockX(), min ? region.getMinimumPoint().getBlockY() : region.getMaximumPoint().getBlockY(), bv.getBlockZ());
            min = !min;
            vectors.add(point);
            if (lowestPoint == null) {
                lowestPoint = point.getBlockY();
            } else if (point.getBlockY() < lowestPoint) {
                lowestPoint = point.getBlockY();
            }
            if (hightestPoint == null) {
                hightestPoint = point.getBlockY();
            } else if (point.getBlockY() > hightestPoint) {
                hightestPoint = point.getBlockY();
            }
        }
        this.points = vectors.toArray(new Vector[vectors.size()]);
        this.area = new Polygonal2DRegion(new BukkitWorld(world), region.getNativePoints(), lowestPoint, hightestPoint);
        this.height = hightestPoint - lowestPoint;

    }

    @Override
    public void placeHolograms() {
        for (Vector v : points) {
            holograms.add(MineMe.hologramAdapter.createHologram(v.clone().setY(getUpperY()).toLocation(world)));
        }
    }

    @Override
    public void save() {
        FileConfiguration file = getBasicSavedConfig();
        int i = 0;
        for (Vector v : points) {
            String point = "points.point" + i;
            file.set(point + ".x", v.getBlockX());
            file.set(point + ".y", v.getBlockY());
            file.set(point + ".z", v.getBlockZ());
            i++;
        }
        try {
            file.save(MineMe.getMineFile(this));
        } catch (IOException ex) {
            Logger.getLogger(CuboidMine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public MineType getType() {
        return MineType.POLYGON;
    }

    @Override
    public boolean contains(double x, double y, double z) {
        boolean downGrade = y <= getUpperY() + 1 && y >= getUpperY();
        com.sk89q.worldedit.Vector vector = new com.sk89q.worldedit.Vector(
                x,
                downGrade ? y - 1 : y,
                z);
        return area.contains(vector);
    }

    @Override
    public Location getCenter() {
        com.sk89q.worldedit.Vector center = area.getCenter();
        return new Location(world, center.getX(), getUpperY() - height / 2, center.getZ());
    }

    @Override
    public int getLowerY() {
        return area.getMinimumY();
    }

    @Override
    public int getUpperY() {
        return area.getMaximumY();
    }

    @Override
    public int getVolume() {
        if (area == null) {
            return 0;
        }
        return area.getArea();
    }

    @Override
    public Iterator<Block> iterator() {
        return new WorldEditIterator(area);
    }

}
