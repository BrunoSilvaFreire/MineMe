/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ddevil.core;

import java.lang.reflect.Field;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author BRUNO II
 */
public class CustomPlugin extends JavaPlugin implements Listener {

    public static CustomPlugin instance;
    protected static CommandMap commandMap;

    @Override
    public void onEnable() {
        instance = this;
        try {
            if (Bukkit.getServer() instanceof CraftServer) {
                final Field f = CraftServer.class.getDeclaredField("commandMap");
                f.setAccessible(true);
                commandMap = (CommandMap) f.get(Bukkit.getServer());
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            Bukkit.getServer().shutdown();
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

    public static boolean isPermissionRegistered(String nome) {
        for (Permission p : Bukkit.getPluginManager().getPermissions()) {
            if (p.getName().equalsIgnoreCase(nome)) {
                return true;
            }
        }
        return false;
    }

    public static void registrarPermissao(String p) {
        if (!isPermissionRegistered(p)) {
            Bukkit.getPluginManager().addPermission(new Permission(p));
        }
    }

    public CommandMap getCommandMap() {
        return commandMap;
    }

    public FileConfiguration loadConfig() {
        FileConfiguration fc = getConfig();
        fc.addDefault("lol", "I did!");
        fc.options().copyDefaults(true);
        saveConfig();
        return fc;
    }
}
