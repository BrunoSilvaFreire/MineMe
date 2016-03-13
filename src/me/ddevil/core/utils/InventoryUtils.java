/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ddevil.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Selma
 */
public class InventoryUtils {

    public static boolean wasClickedInLane(Inventory i, int clickedSlot, int lane) {
        return Arrays.asList(getLane(i, lane)).contains(clickedSlot);
    }

    public static Inventory createInventory(String title, int totalLanes) {
        return Bukkit.createInventory(null, totalLanes * 9, title);
    }

    public static int getMiddlePoint(Inventory m) {
        return getMiddlePoint(m.getSize());
    }

    public static int getBottomMiddlePoint(Inventory m) {
        return getBottomMiddlePoint(m.getSize());
    }

    public static int getTopMiddlePoint(Inventory m) {
        return getTopMiddlePoint(m.getSize());
    }

    public static Integer[] getLane(Inventory i, int lane) {
        return getLane(i.getSize(), lane);
    }

    public static Integer[] getPartialLane(Inventory i, int lane, int start, int end) {
        return getPartialLane(i.getSize(), lane, start, end);
    }

    public static void drawSquare(Inventory inv, int pos1, int pos2, ItemStack item) {
        drawSquare(inv, pos1, pos2, item, true);
    }

    public static void drawSquare(Inventory inv, int pos1, int pos2, ItemStack item, boolean replace) {
        for (int i : getSquare(inv, pos1, pos2)) {
            if (replace) {
                inv.setItem(i, item);
            } else {
                if (inv.getItem(i) != null) {
                    inv.setItem(i, item);
                }
            }
        }
    }

    public static Integer[] getSquare(Inventory inv, int pos1, int pos2) {
        return getSquare(inv.getSize(), pos1, pos2);
    }

    public static Integer[] getSquare(int size, int pos1, int pos2) {
        int minx = Math.min(pos1 % 9, pos2 % 9);
        int miny = Math.min(pos1 / 9, pos2 / 9);
        int maxx = Math.max(pos1 % 9, pos2 % 9);
        int maxy = Math.max(pos1 / 9, pos2 / 9);
        ArrayList<Integer> points = new ArrayList();
        for (int i = miny; i <= maxy; i++) {
            for (int j = minx; j <= maxx; j++) {
                points.add(i * 9 + j);
            }
        }
        return points.toArray(new Integer[points.size()]);
    }

    public static Integer[] getPartialLane(int size, int lane, int start, int end) {
        if (size % 9 != 0 || size == 0) {
            return null;
        }
        int totalLanes = size / 9;
        if (lane > totalLanes) {
            return null;
        } else {
            int startingpoint = lane * 9 + start;
            int endpoint = lane * 9 + end + 1;
            ArrayList<Integer> locations = new ArrayList();
            for (int i = startingpoint; i < endpoint; i++) {
                locations.add(i);
            }
            return locations.toArray(new Integer[locations.size()]);
        }
    }

    public static int getTotalLanes(Inventory i) {
        return getTotalLanes(i.getSize());
    }

    public static int getTotalLanes(int i) {
        return i / 9;
    }

    public static int getFirstSlotInLane(int lane) {
        return lane * 9;
    }

    public static int getLastSlotInLane(int lane) {
        return lane * 9 + 8;
    }

    public static int getMiddlePoint(int size) {
        if (size % 9 != 0 || size == 0) {
            return 0;
        }
        int totalLanes = size / 9;
        int middleLane = (totalLanes / 2) + 1;
        return (middleLane * 9) - 5;
    }

    public static int getBottomMiddlePoint(int size) {
        if (size % 9 != 0 || size == 0) {
            return 0;
        }
        int totalLanes = size / 9;
        return (totalLanes * 9) - 5;
    }

    public static int getTopMiddlePoint(int size) {
        if (size % 9 != 0 || size == 0) {
            return 0;
        }
        return 4;
    }

    public static Integer[] getLane(int size, int lane) {
        if (size % 9 != 0 || size == 0) {
            return null;
        }
        int totalLanes = size / 9;
        if (lane > totalLanes) {
            return null;
        } else {
            int startingpoint = lane * 9;
            int endpoint = startingpoint + 9;
            ArrayList<Integer> locations = new ArrayList();
            for (int i = startingpoint; i < endpoint; i++) {
                locations.add(i);
            }
            return locations.toArray(new Integer[locations.size()]);
        }
    }
}
