/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ddevil.mineme;

/**
 *
 * @author Selma
 */
public class ConfigManager {

    public static boolean hologramsUsable = false;
    public static boolean forceHologramsUse = false;

    public static void enableHolograms() {
        hologramsUsable = true;
    }

    public static void setHologramsUsable(boolean hologramsUsable) {
        ConfigManager.hologramsUsable = hologramsUsable;
    }

    public static void setForceHologramsUse(boolean forceHologramsUse) {
        ConfigManager.forceHologramsUse = forceHologramsUse;
    }

    public static boolean isForceHologramsUse() {
        return forceHologramsUse;
    }

}
