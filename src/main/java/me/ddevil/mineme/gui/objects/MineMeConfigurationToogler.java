/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ddevil.mineme.gui.objects;

import me.ddevil.core.utils.inventory.InventoryMenu;
import me.ddevil.core.utils.inventory.objects.BasicClickableInventoryObject;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author BRUNO II
 */
public class MineMeConfigurationToogler extends BasicClickableInventoryObject {

    private final FileConfiguration config;
    private final String path;
    private final boolean value;

    public MineMeConfigurationToogler(ItemStack itemStack, InventoryMenu menu, FileConfiguration configuration, String path) {
        super(itemStack, menu);
        this.config = configuration;
        this.path = path;
        this.value = config.getBoolean(path);
    }

    public void toogle() {
    }

    public String getPath() {
        return path;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public boolean getValue() {
        return value;
    }

}
