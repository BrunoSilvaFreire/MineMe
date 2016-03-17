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
package me.ddevil.mineme.utils;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.regions.Region;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 *
 * @author Selma
 */
public class WorldEditIterator implements Iterator<Block> {

    private final Iterator<BlockVector> bviterator;
    private final World world;

    public WorldEditIterator(Region area) {
        bviterator = area.iterator();
        this.world = Bukkit.getWorld(area.getWorld().getName());
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
