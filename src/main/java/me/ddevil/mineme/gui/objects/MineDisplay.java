/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ddevil.mineme.gui.objects;

import me.ddevil.core.events.inventory.InventoryObjectClickEvent;
import me.ddevil.core.utils.inventory.InventoryMenu;
import me.ddevil.core.utils.inventory.objects.BasicClickableInventoryObject;
import me.ddevil.core.utils.inventory.objects.interfaces.InventoryObjectClickListener;
import me.ddevil.core.utils.items.ItemUtils;
import me.ddevil.mineme.gui.GUIManager;
import me.ddevil.mineme.gui.GUIResourcesUtils;
import me.ddevil.mineme.gui.menus.MainMenu;
import me.ddevil.mineme.mines.Mine;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author BRUNO II
 */
public class MineDisplay extends BasicClickableInventoryObject {

    private final Mine mine;

    public MineDisplay(Mine m, InventoryMenu menu) {
        super(m.getIcon(), menu);
        this.mine = m;
        this.interactListener = new InventoryObjectClickListener() {

            @Override
            public void onInteract(InventoryObjectClickEvent e) {
                GUIManager.mainMenu.openMineMenu(mine, e.getPlayer());

            }
        };
    }

    public MineDisplay(final Mine mine, ItemStack is, MainMenu menu) {
        super(ItemUtils.addToLore(is, new String[]{GUIResourcesUtils.CLICK_TO_EDIT}), menu);
        this.mine = mine;
        this.interactListener = new InventoryObjectClickListener() {

            @Override
            public void onInteract(InventoryObjectClickEvent e) {
                GUIManager.mainMenu.openMineMenu(mine, e.getPlayer());
            }
        };
    }

    public Mine getMine() {
        return mine;
    }

}
