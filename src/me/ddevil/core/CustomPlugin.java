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
package me.ddevil.core;

import me.ddevil.core.chat.MessageManager;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.ddevil.mineme.MineMe;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomPlugin extends JavaPlugin implements Listener {

    public static CustomPlugin instance;
    protected static CommandMap commandMap;
    public static MessageManager messageManager;
    public int minimumDebugPriotity = 0;

    public static String getVersion() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        String version = name.substring(name.lastIndexOf('.') + 1) + ".";
        return version;
    }

    public static Class<?> getNMSClass(String className) {
        String fullName = "net.minecraft.server." + getVersion() + className;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(fullName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clazz;
    }

    public static Class<?> getOBCClass(String className) {
        String fullName = "org.bukkit.craftbukkit." + getVersion() + className;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(fullName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clazz;
    }

    @Override
    public void onEnable() {
        instance = this;
        try {
            Class craftServerClass = getOBCClass("CraftServer");
            final Field f = craftServerClass.getDeclaredField("commandMap");
            f.setAccessible(true);
            commandMap = (CommandMap) f.get(Bukkit.getServer());
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public static void registerCommand(Command cmd) {
        CustomPlugin.registerCommand(instance, cmd);
    }

    public static void registerCommand(Plugin pl, Command cmd) {
        CustomPlugin.registerCommand(pl.getName(), cmd);
    }

    private static void registerCommand(String pl, Command cmd) {
        commandMap.register(pl, cmd);
    }

    public static boolean isPermissionRegistered(String permission) {
        for (Permission p : Bukkit.getPluginManager().getPermissions()) {
            if (p.getName().equalsIgnoreCase(permission)) {
                return true;
            }
        }
        return false;
    }

    public static void registerPermission(String permission) {
        if (!isPermissionRegistered(permission)) {
            Bukkit.getPluginManager().addPermission(new Permission(permission));
        }
    }

    public static void registerListener(Listener l) {
        Bukkit.getPluginManager().registerEvents(l, instance);
        instance.debug("Listener " + l.getClass().getSimpleName() + " registered.");
    }

    public static void unregisterListener(Listener l) {
        HandlerList.unregisterAll(l);
    }

    public CommandMap getCommandMap() {
        return commandMap;
    }

    public FileConfiguration loadConfig() {
        FileConfiguration fc = getConfig();
        saveConfig();
        return fc;
    }

    public FileConfiguration loadResource(File config, String resource) {
        if (!config.exists()) {
            //Load from plugin
            saveResource(resource, false);
        }
        return YamlConfiguration.loadConfiguration(config);
    }

    public void debug() {
        debug("");
    }

    public void debug(String[] msg) {
        for (String m : msg) {
            debug(m);
        }
    }

    public void debug(String msg) {
        debug(msg, 0);
    }

    public void debug(String msg, int priority) {
        if (priority >= minimumDebugPriotity) {
            getLogger().info(msg);
        }
    }

    public void debug(String msg, boolean force) {
        if (force) {
            getLogger().info(msg);
        } else {
            debug(msg);
        }
    }

    public void setInConfig(FileConfiguration configuration, String path, Object toSet) {
        setInConfig(new File(getDataFolder(), configuration.getName()), configuration, path, toSet);
    }

    public void setInConfig(File f, FileConfiguration configuration, String path, Object toSet) {
        try {
            MineMe.pluginConfig.set(path, toSet);
            MineMe.pluginConfig.save(f);
        } catch (IOException ex) {
            Logger.getLogger(CustomPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void printException(String msg, Throwable t) {
        debug(msg, true);
        debug("--== Error ==--", true);
        t.printStackTrace();
        debug("--== Error ==--", true);
        debug();
    }
}
