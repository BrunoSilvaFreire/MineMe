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
package me.ddevil.mineme.holograms.impl;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import com.gmail.filoghost.holographicdisplays.object.CraftHologram;
import com.gmail.filoghost.holographicdisplays.object.line.CraftHologramLine;
import com.gmail.filoghost.holographicdisplays.object.line.CraftTextLine;
import me.ddevil.mineme.holograms.CompatibleHologram;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Selma
 */
public class HolographicDisplaysHologram implements CompatibleHologram {

    private final Hologram hologram;

    public HolographicDisplaysHologram(Hologram hologram) {
        this.hologram = hologram;
    }

    public Hologram getHologram() {
        return hologram;
    }

    @Override
    public void clearLines() {
        hologram.clearLines();
    }

    @Override
    public void appendTextLine(String translateTagsAndColors) {
        hologram.appendTextLine(translateTagsAndColors);
    }

    @Override
    public void delete() {
        hologram.delete();
    }

    @Override
    public void setLine(int line, String text) {
        if (hologram instanceof CraftHologram) {
            CraftHologram h = (CraftHologram) hologram;
            if (h.size() > line) {
                if (h.getLine(line) instanceof CraftHologramLine) {
                    CraftHologramLine hline = h.getLine(line);
                    if (hline instanceof CraftTextLine) {
                        CraftTextLine ctl = (CraftTextLine) hline;
                        ctl.setText(text);
                    }
                }
            }
        }
    }

    @Override
    public Location getLocation() {
        return hologram.getLocation();
    }

    @Override
    public int size() {
        return hologram.size();
    }

    @Override
    public void removeLine(int line) {
        hologram.removeLine(line);
    }

    @Override
    public void appendItemLine(ItemStack icon) {
        hologram.appendItemLine(icon);
    }

    public boolean isItemLine(int line) {
        return hologram.getLine(line) instanceof ItemLine;
    }

    @Override
    public void move(World w, double x, double y, double z) {
        hologram.teleport(w, x, y, z);
    }

    @Override
    public void move(Location l) {
        hologram.teleport(l);
    }

}
