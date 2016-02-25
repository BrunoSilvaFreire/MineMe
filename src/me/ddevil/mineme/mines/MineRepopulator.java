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

import me.ddevil.mineme.utils.RandomCollection;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class MineRepopulator {

    public void repopulate(Mine m) {
        RepopulateMap map = new RepopulateMap(m);
        for (Block b : m.getBlocks()) {
            b.setType(map.getRandomBlock());
        }
    }

    private static class RepopulateMap {

        private final RandomCollection<Material> randomCollection;

        private RepopulateMap(Mine m) {
            randomCollection = new RandomCollection<>();
            for (Material m1 : m.getComposition().keySet()) {
                randomCollection.add(m.getComposition().get(m1), m1);
            }

        }

        private Material getRandomBlock() {
            return randomCollection.next();
        }
    }
}