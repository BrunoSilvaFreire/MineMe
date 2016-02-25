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
package me.ddevil.mineme.mines.configs;

import java.util.HashMap;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.mines.MineType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author Selma
 */
public class MineConfig {

    private final FileConfiguration config;
    private final World world;
    private final MineType type;
    private final boolean broadcastOnReset;
    private final boolean nearbyBroadcast;
    private final boolean enabled;

    private final String broadcastMessage;
    private final double broadcastRadius;
    private final int resetDelay;
    private final String name;
    private final HashMap<Material, Double> composition;
    private final boolean useCustomBroadcast;

    public MineConfig(FileConfiguration mine) {
        this.config = mine;
        world = Bukkit.getWorld(mine.getString("world"));
        type = MineType.valueOf(mine.getString("type"));
        name = mine.getString("name");
        enabled = mine.getBoolean("enabled");
        broadcastOnReset = mine.getBoolean("broadcastOnReset");
        nearbyBroadcast = mine.getBoolean("broadcastToNearbyOnly");
        useCustomBroadcast = mine.getBoolean("useCustomBroadcast");
        broadcastMessage = mine.getString("customBroadcast");
        broadcastRadius = mine.getDouble("broadcastRadius");
        resetDelay = mine.getInt("resetDelay");
        HashMap<Material, Double> comp = new HashMap();
        for (String s : mine.getStringList("composition")) {
            String[] split = s.split("=");
            try {
                comp.put(Material.valueOf(split[0]), Double.valueOf(split[1]));
            } catch (NumberFormatException e) {
                MineMe.getInstance().debug(split[1] + " in " + s + "isn't a number!");
            }
        }
        this.composition = comp;

    }

    public boolean isUseCustomBroadcast() {
        return useCustomBroadcast;
    }

    public HashMap<Material, Double> getComposition() {
        return composition;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }

    public boolean isNearbyBroadcast() {
        return nearbyBroadcast;
    }

    public boolean isBroadcastOnReset() {
        return broadcastOnReset;
    }

    public int getResetDelay() {
        return resetDelay;
    }

    public double getBroadcastRadius() {
        return broadcastRadius;
    }

    public String getBroadcastMessage() {
        return broadcastMessage;
    }

    public World getWorld() {
        return world;
    }

    public MineType getType() {
        return type;
    }

    public FileConfiguration getConfig() {
        return config;
    }

}
