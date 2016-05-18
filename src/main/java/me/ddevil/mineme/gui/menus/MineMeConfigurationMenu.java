/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ddevil.mineme.gui.menus;

import me.ddevil.core.utils.inventory.BasicInventoryMenu;
import me.ddevil.core.utils.inventory.objects.BasicClickableInventoryObject;
import me.ddevil.mineme.gui.GUIResourcesUtils;
import me.ddevil.mineme.gui.objects.MineMeConfigurationToogler;

/**
 *
 * @author BRUNO II
 */
public class MineMeConfigurationMenu extends BasicInventoryMenu {

    public MineMeConfigurationMenu(String name) {
        super(name, GUIResourcesUtils.INVENTORY_SIZE);
        holograms = new MineMeConfigurationToogler(null, this, null, name);
    }
    private final BasicClickableInventoryObject holograms;

    @Override
    protected void setupItems() {
        registerInventoryObject(holograms, 0);
    }

    @Override
    public void update() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
