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
package me.ddevil.mineme.gui;

import java.io.File;
import java.util.HashMap;
import me.ddevil.core.utils.ItemUtils;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.mines.Mine;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Selma
 */
public class GUIManager {

    public static void setup() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(MineMe.pluginFolder, "guiconfig.yml"));
        ConfigurationSection slipperItem = config.getConfigurationSection("global.splitter");
        splitter = ItemUtils.createItem(Material.valueOf(slipperItem.getString("type")), slipperItem.getString("name"));
    }
    private static final HashMap<String, ItemStack> customItems = new HashMap();
    public static ItemStack splitter;

    public static void registerItem(String name, ItemStack i) {
        name = name.toUpperCase();
        customItems.put(name, i);
    }

    public static ItemStack getItem(String name) {
        return customItems.get(name);
    }

    public static ItemStack getItem(String name, Mine m) {
        return customItems.get(name);
    }
}
