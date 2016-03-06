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

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.logging.AbstractLoggingExtent;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import com.sk89q.worldedit.world.World;
import java.util.ArrayList;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.utils.MVdWPlaceholderManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 *
 * @author Selma
 */
public class MineManager {

    private static final ArrayList<Mine> mines = new ArrayList();
    private static final MineMe mineMe = MineMe.getInstance();

    public static ArrayList<Mine> getMines() {
        return mines;
    }

    public static boolean isNameAvailable(String name) {
        for (Mine mine : mines) {
            if (mine.getName().equalsIgnoreCase(name)) {
                return false;
            }
        }
        return true;
    }

    public static void registerMine(Mine m) {
        mines.add(m);
        MineMe.registerListener(m);
        if (MineMe.useMVdWPlaceholderAPI) {
            if (!MVdWPlaceholderManager.isPlaceholderRegistered(m)) {
                MVdWPlaceholderManager.registerMinePlaceholders(m);
            }
        }
        mineMe.debug("Mine " + m.getName() + " registered to manager!");
    }

    public static void unregisterMines() {
        mines.clear();
        for (Mine mine : mines) {
            MineMe.unregisterListener(mine);
        }
        MineMe.getInstance().debug("Unloaded all mines!");
    }

    public static void unregisterMine(Mine mine) {
        mines.remove(mine);
        MineMe.unregisterListener(mine);
        MineMe.getInstance().debug("Unloaded mine " + mine.getName() + "!");
    }

    public static Mine getMine(String name) {
        for (Mine mine : mines) {
            if (mine.getName().equalsIgnoreCase(name)) {
                return mine;
            }
        }
        return null;
    }

    public static boolean isPlayerInAMine(Player p) {
        return getMineWith(p) != null;
    }

    public static Mine getMineWith(Player p) {
        for (Mine m : mines) {
            if (m.contains(p)) {
                return m;
            }
        }
        return null;
    }

    public static void sendInfo(Player p, Mine m) {
        MineMe.messageManager.sendMessage(p, m.getInfo());
    }

    private static void notifyBlockChange(Block b) {
        for (Mine mine : mines) {
            if (mine.contains(b)) {
                if (!mine.wasAlreadyBroken(b)) {
                    mine.setBlockAsBroken(b);
                }
            }
        }
    }

    public static boolean isPartOfMine(Block b) {
        for (Mine mine : mines) {
            if (mine.contains(b)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @author Selma
     */
    public static class WorldEditManager {

        public class WorldEditLogger extends AbstractLoggingExtent {

            private final Actor actor;
            private final World world;

            public Actor getActor() {
                return actor;
            }

            public WorldEditLogger(World world, Actor actor, Extent extent) {
                super(extent);
                this.actor = actor;
                this.world = world;
            }

            @Override
            protected void onBlockChange(Vector position, BaseBlock newBlock) {
                Block block = new Location(Bukkit.getWorld(world.getName()), position.getBlockX(), position.getBlockY(), position.getBlockZ()).getBlock();
                notifyBlockChange(block);
            }
        }

        @Subscribe
        public void wrapForLogging(EditSessionEvent event) {
            Actor actor = event.getActor();
            World world = event.getWorld();
            if (actor != null && actor.isPlayer()) {
                event.setExtent(new WorldEditLogger(world, actor, event.getExtent()));
            }
        }
    }
}
