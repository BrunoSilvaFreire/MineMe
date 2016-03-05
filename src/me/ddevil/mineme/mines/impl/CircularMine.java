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

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.internal.LocalWorldAdapter;
import com.sk89q.worldedit.regions.CylinderRegion;
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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Selma
 */
public class CircularMine extends BasicMine {

    //General
    private final Vector center;
    private final double radius;
    private final int minY;
    private final int maxY;
    private final CylinderRegion area;

    public CircularMine(MineConfig config) {
        super(config);
        this.center = new Vector(config.getConfig().getDouble("X"), config.getConfig().getDouble("Y"), config.getConfig().getDouble("Z"));
        this.radius = config.getConfig().getDouble("radius");
        this.minY = (int) center.getY();
        this.maxY = (int) (config.getConfig().getInt("height") + center.getY());
        this.composition = config.getComposition();
        area = new CylinderRegion(
                new BukkitWorld(world),
                new com.sk89q.worldedit.Vector(
                        center.getX(),
                        center.getY(),
                        center.getZ()),
                new Vector2D(radius, radius),
                minY,
                maxY);
    }

    @Override
    public void delete() {
        saveFile.delete();
        deleted = true;

    }

    @Override
    public void reset() {
        if (isDeleted()) {
            return;
        }
        System.out.println(area.getArea());
        System.out.println(area.getWorld().getName());
        System.out.println(area.getCenter().toString());
        MineResetEvent event = (MineResetEvent) new MineResetEvent(this).call();
        if (!event.isCancelled()) {
            MineMe.getInstance().debug("Reseting mine " + name, 2);
            //Pull players up
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                Location l = p.getLocation();
                if (contains(p)) {
                    l.setY(getMaximumY() + 2);
                    p.teleport(l);
                }
            }
            currentResetDelay = totalResetDelay;
            new MineRepopulator().repopulate(this);
            if (broadcastOnReset) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (broadcastNearby) {
                        if (p.getLocation().distance(getLocation()) <= broadcastRadius) {
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
        file.set("X", center.getX());
        file.set("Y", center.getY());
        file.set("Z", center.getZ());
        file.set("radius", radius);
        file.set("height", getHeight());
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
        return area.contains(new com.sk89q.worldedit.Vector(x, y, z));
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
        return new CircularIterator();
    }

    public int getArea() {
        return area.getArea();
    }

    public int getHeight() {
        return maxY - minY + 1;
    }

    public double getRadius() {
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

    public class CircularIterator implements Iterator<Block> {

        public CircularIterator() {
            for (BlockVector bv : area) {
                blocks.add(new Location(world, bv.getX(), bv.getX(), bv.getX()).getBlock());
            }
        }
        private final ArrayList<Block> blocks = new ArrayList<>();
        private int current = 0;

        @Override
        public boolean hasNext() {
            return current != blocks.size() - 1;
        }

        @Override
        public Block next() {
            Block get = blocks.get(current);
            current++;
            return get;
        }
    }
}
