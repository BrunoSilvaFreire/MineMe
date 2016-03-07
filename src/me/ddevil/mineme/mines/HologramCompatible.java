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
package me.ddevil.mineme.mines;

import java.util.List;
import me.ddevil.mineme.holograms.CompatibleHologram;

public interface HologramCompatible {

    public abstract void setupHolograms();

    public abstract void showHolograms();

    public abstract void hideHolograms();

    public abstract void updateHolograms();

    public abstract void softHologramUpdate();

    public abstract boolean isHologramsVisible();

    public List<CompatibleHologram> getHolograms();

    public List<String> getHologramsLines();

    public void setHologramsLines(List<String> lines);
}
