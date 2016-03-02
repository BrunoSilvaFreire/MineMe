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
import me.ddevil.mineme.holograms.CompatibleHologram;

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

}
