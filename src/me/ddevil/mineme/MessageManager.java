package me.ddevil.mineme;

import me.ddevil.mineme.mines.Mine;
import net.md_5.bungee.api.ChatColor;

public class MessageManager {

    public static String translateTags(String get, Mine m) {
        get = get.replaceAll("%mine%", m.getName());
        get = get.replaceAll("%mine%", m.getName());
        get = get.replaceAll("%mine%", m.getName());
        get = get.replaceAll("%mine%", m.getName());
        get = get.replaceAll("%mine%", m.getName());
        return ChatColor.translateAlternateColorCodes('&', get);
    }

}
