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
package me.ddevil.mineme.api;

import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.messages.MineMeMessageManager;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.mines.MineManager;
import org.bukkit.entity.Player;

/**
 * Do you like potatoes?
 *
 * @author Selma
 */
public class MineMeAPI {

    /**
     * Searches the MineManager for a mine with this name.
     * <b>Will return null if there isn't a mine with this name!</b>
     *
     * @param name the name to search for
     * @return A mine will this name
     */
    public static Mine getMine(String name) {
        return MineManager.getMine(name);
    }

    /**
     * Searches the MineManager for a mine that contains this players
     * <b>Will return null if the player isn't on a mine!</b>
     *
     * @param player The player to use
     * @return The mine that contains this player
     */
    public static Mine getMineWith(Player player) {
        return MineManager.getMineWith(player);
    }

    /**
     * Translates the tags and colors in this String :D
     * <b>Note this will only work with static tags! (Tags that don't require
     * anything else but the input String to be translated)</b>
     *
     * @param input The String to be translated
     * @return The string with translated tags and colors
     */
    public static String translateTags(String input) {
        return MineMeMessageManager.translateTagsAndColor(input);
    }

    /**
     * Translates the tags and colors in this String using the mine param as the
     * tags replacement reference :)
     *
     * @param mine The mine reference used for tag translation
     * @param input The String to be translated
     * @return The string with translated tags and colors
     */
    public static String translateTagsWithMine(Mine mine, String input) {
        return MineMeMessageManager.translateTagsAndColors(input, mine);
    }

}
