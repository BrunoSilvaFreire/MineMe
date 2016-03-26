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

import java.util.ArrayList;
import java.util.List;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.events.MineHologramUpdateEvent;
import me.ddevil.mineme.holograms.CompatibleHologram;
import me.ddevil.mineme.messages.MineMeMessageManager;
import me.ddevil.mineme.mines.HologramCompatible;
import me.ddevil.mineme.mines.configs.MineConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Selma
 */
public abstract class BasicHologramMine extends BasicMine implements HologramCompatible {

    protected boolean hologramsReady = false;
    private boolean useCustomHologramText;

    public BasicHologramMine(String name, World world, ItemStack icon) {
        super(name, world, icon);
        useCustomHologramText = false;
    }

    public BasicHologramMine(MineConfig config) {
        super(config);
        useCustomHologramText = config.getConfig().getBoolean("useCustomHologramText");
    }
    protected final List<Integer> tagLines = new ArrayList();

    @Override
    public void setupHolograms() {
        //Load hologram pattern
        if (MineMe.forceDefaultHolograms) {
            MineMe.getInstance().debug("Setting default hologram text for mine " + name + " because forceDefaultHologramOnAllMines is enabled on the config");
            setHologramsLines(MineMe.defaultHologramText);
        } else if (config.getBoolean("useCustomHologramText")) {
            MineMe.getInstance().debug("Setting custom hologram text for mine " + name);
            setHologramsLines(config.getStringList("hologramsText"));
        } else {
            MineMe.getInstance().debug("Setting default hologram text for mine " + name + " since useCustomHologramText is disabled");
            setHologramsLines(MineMe.defaultHologramText);
        }
        //Detect placeholder lines
        for (int i = 0; i < getHologramsLines().size(); i++) {
            String text = getHologramsLines().get(i);
            if (text.contains("%") && !text.equalsIgnoreCase("%item%")) {
                tagLines.add(i);
            }
        }
        //Place holograms
        placeHolograms();
        //Inicial holograms setup
        for (CompatibleHologram h : getHolograms()) {
            h.clearLines();
            for (String text : getHologramsLines()) {
                if (text.equalsIgnoreCase("%icon%")) {
                    h.appendItemLine(icon);
                } else {
                    h.appendTextLine(MineMeMessageManager.getInstance().translateAll(text, this));
                }
            }
        }
        //Update
        updateHolograms();
    }

    public abstract void placeHolograms();

    @Override
    public void showHolograms() {
        updateHolograms();
    }

    @Override
    public void hideHolograms() {
        for (CompatibleHologram m : getHolograms()) {
            m.clearLines();
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

    @Override
    public void updateHolograms() {
        if (isDeleted()) {
            return;
        }
        if (getHolograms().isEmpty()) {
            return;
        }
        MineMe.getInstance().debug("Updating holograms for " + name, 1);
        MineMe.getInstance().debug("Total lines: " + getHologramsLines().size(), 1);
        MineHologramUpdateEvent event = (MineHologramUpdateEvent) new MineHologramUpdateEvent(this).call();
        if (!event.isCancelled()) {
            for (CompatibleHologram h : getHolograms()) {
                for (int i : tagLines) {
                    String text = getHologramsLines().get(i);
                    h.setLine(i, MineMeMessageManager.getInstance().translateAll(text, this));
                }
            }
            MineMe.getInstance().debug("Holograms updated", 1);
        } else {
            MineMe.getInstance().debug("Hologram Update Event for mine " + name + " was cancelled", 1);
        }
    }

    @Override
    public boolean useCustomHologramText() {
        return useCustomHologramText;
    }
    protected final ArrayList<CompatibleHologram> holograms = new ArrayList();
    protected List<String> hologramsLines;

    @Override
    public List<CompatibleHologram> getHolograms() {
        return holograms;
    }

    @Override
    public List<String> getHologramsLines() {
        return hologramsLines;
    }

    @Override
    public void setHologramsLines(List<String> lines) {
        this.hologramsLines = lines;
    }

    @Override
    public void reset() {
        super.reset();
        if (MineMe.useHolograms) {
            updateHolograms();
        }
    }

}
