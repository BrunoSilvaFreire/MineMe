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
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.regions.CylinderRegion;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.events.MineHologramUpdateEvent;
import me.ddevil.mineme.events.MineResetEvent;
import me.ddevil.mineme.holograms.CompatibleHologram;
import me.ddevil.mineme.messages.MineMeMessageManager;
import me.ddevil.mineme.mines.HologramCompatible;
import me.ddevil.mineme.mines.MineManager;
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
public class CircularMine extends BasicMine implements HologramCompatible {

    //General
    private final Vector center;
    private final double radius;
    private final int minY;
    private final int maxY;
    private final CylinderRegion area;

    public CircularMine(MineConfig config) {
        super(config);
        Vector fakecenter = new Vector(config.getConfig().getDouble("X"), config.getConfig().getDouble("Y"), config.getConfig().getDouble("Z"));
        //Selection
        this.radius = config.getConfig().getDouble("radius");
        this.minY = (int) fakecenter.getY();
        this.maxY = (int) (config.getConfig().getInt("height") + fakecenter.getY());
        this.area = new CylinderRegion(
                new BukkitWorld(world),
                new com.sk89q.worldedit.Vector(
                        fakecenter.getX(),
                        fakecenter.getY(),
                        fakecenter.getZ()),
                new Vector2D(radius, radius),
                minY,
                maxY);
        this.center = new Vector(
                area.getCenter().getX(),
                area.getCenter().getY(),
                area.getCenter().getZ()
        );
        center.setX(center.getX() + 0.5);
        center.setZ(center.getZ() + 0.5);
    }

    public CircularMine(String name, Location center, double radius, int height) {
        super(name, center.getWorld());
        Vector fakecenter = center.toVector();
        //Selection
        this.radius = radius;
        this.minY = (int) fakecenter.getY();
        this.maxY = height + minY;
        this.area = new CylinderRegion(
                new BukkitWorld(world),
                new com.sk89q.worldedit.Vector(
                        fakecenter.getX(),
                        fakecenter.getY(),
                        fakecenter.getZ()),
                new Vector2D(radius, radius),
                minY,
                maxY);
        this.center = new Vector(
                area.getCenter().getX(),
                area.getCenter().getY(),
                area.getCenter().getZ()
        );
        center.setX(center.getX() + 0.5);
        center.setZ(center.getZ() + 0.5);
        this.config = getBasicSavedConfig();
        config.set("X", center.getBlockX());
        config.set("Y", center.getBlockY());
        config.set("Z", center.getBlockZ());

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
        return area.getArea();
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
    //Holograms
    private final ArrayList<CompatibleHologram> holograms = new ArrayList();
    private boolean hologramsReady = false;
    private List<String> hologramsLines;

    @Override
    public void setupHolograms() {
        MineMe.getInstance().debug("Creating holograms for " + name + "...");
        if (MineMe.forceDefaultHolograms) {
            MineMe.getInstance().debug("Setting default hologram text for mine " + name + " because forceDefaultHologramOnAllMines is enabled on the config");
            hologramsLines = MineMe.defaultHologramText;
        } else if (config.getBoolean("useCustomHologramText")) {
            MineMe.getInstance().debug("Setting custom hologram text for mine " + name);
            hologramsLines = config.getStringList("hologramsText");
        } else {
            MineMe.getInstance().debug("Setting default hologram text for mine " + name + " since useCustomHologramText is disabled");
            hologramsLines = MineMe.defaultHologramText;
        }
        Location l = getCenter().toLocation(world);
        Location temp;
        temp = l.clone();
        temp.setY(getMaximumY() + 4 + (hologramsLines.size() * 0.15));
        holograms.add(MineMe.hologramAdapter.createHologram(temp));
        temp = l.clone();
        temp.add(area.getRadius().getX() + 1, 0, 0);
        holograms.add(MineMe.hologramAdapter.createHologram(temp));
        temp = l.clone();
        temp.add((area.getRadius().getX() * -1) - 1, 0, 0);
        holograms.add(MineMe.hologramAdapter.createHologram(temp));
        temp = l.clone();
        temp.add(0, 0, area.getRadius().getZ() + 1);
        holograms.add(MineMe.hologramAdapter.createHologram(temp));
        temp = l.clone();
        temp.add(0, 0, (area.getRadius().getZ() * -1) - 1);
        holograms.add(MineMe.hologramAdapter.createHologram(temp));
        MineMe.getInstance().debug("Created " + holograms.size() + " holograms.");
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
    private Integer lightHologramUpdateId = null;

    @Override
    public List<CompatibleHologram> getHolograms() {
        return holograms;
    }

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

    public class CircularIterator implements Iterator<Block> {

        private final Iterator<BlockVector> bviterator;

        public CircularIterator() {
            bviterator = area.iterator();
        }

        @Override
        public boolean hasNext() {
            return bviterator.hasNext();
        }

        @Override
        public Block next() {
            BlockVector next = bviterator.next();
            return new Location(world, next.getX(), next.getY(), next.getZ()).getBlock();
        }
    }
}
