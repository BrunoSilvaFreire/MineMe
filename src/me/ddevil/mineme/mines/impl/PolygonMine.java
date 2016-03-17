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
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.events.MineResetEvent;
import me.ddevil.mineme.messages.MineMeMessageManager;
import me.ddevil.mineme.mines.MineRepopulator;
import me.ddevil.mineme.mines.MineType;
import me.ddevil.mineme.mines.configs.MineConfig;
import me.ddevil.mineme.storage.StorageManager;
import me.ddevil.mineme.utils.WorldEditIterator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.json.simple.parser.ParseException;

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
        for (String pointPath : pointsConfig.getKeys(enabled)) {
            int x = fileConfig.getInt(pointPath + ".x");
            int y = fileConfig.getInt(pointPath + ".y");
            int z = fileConfig.getInt(pointPath + ".z");
            vectors.add(new Vector(x, y, z));
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

    public PolygonMine(Vector[] points, int height, String name, World world, ItemStack icon) {
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

    @Override
    public void placeHolograms() {
        for (Vector v : points) {
            holograms.add(MineMe.hologramAdapter.createHologram(v.clone().setY(getUpperY()).toLocation(world)));
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
        } else {
            MineMe.getInstance().debug("Reset event for mine " + name + " was cancelled", 2);
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
        return area.contains(new com.sk89q.worldedit.Vector(x, y, z));
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
        return area.getArea();
    }

    @Override
    public Iterator<Block> iterator() {
        return new WorldEditIterator(area);
    }

}
