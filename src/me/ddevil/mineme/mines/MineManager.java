/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ddevil.mineme.mines;

import java.util.ArrayList;
import org.bukkit.entity.Player;

/**
 *
 * @author Selma
 */
public class MineManager {

    private static final ArrayList<Mine> MINES = new ArrayList();

    public static void registerMine(Mine m) {
        MINES.add(m);
    }

    public static Mine getMine(String name) {
        for (Mine mine : MINES) {
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
        for (Mine m : MINES) {
            if (m.contains(p)) {
                return m;
            }
        }
        return null;
    }
}
