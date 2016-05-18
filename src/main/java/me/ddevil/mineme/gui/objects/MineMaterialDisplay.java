/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ddevil.mineme.gui.objects;

import me.ddevil.core.events.inventory.InventoryObjectClickEvent;
import me.ddevil.core.utils.inventory.objects.BasicClickableInventoryObject;
import me.ddevil.core.utils.inventory.objects.interfaces.InventoryObjectClickListener;
import me.ddevil.core.utils.items.ItemUtils;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.gui.GUIResourcesUtils;
import me.ddevil.mineme.gui.menus.MineMenu;
import me.ddevil.mineme.mines.Mine;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author BRUNO II
 */
public class MineMaterialDisplay extends BasicClickableInventoryObject {

    protected ItemStack material;
    private final Mine mine;

    public ItemStack getMaterial() {
        return material;
    }

    public void setMaterial(ItemStack material) {
        String name;
        if (material == null) {
            name = "null";
        } else {
            name = ItemUtils.toString(material);
        }
        MineMe.instance.broadcastDebug("Setting material to " + name);
        this.material = material;
        update();
    }

    public MineMaterialDisplay(Mine m, final MineMenu menu) {
        super(GUIResourcesUtils.EMPTY_MATERIAL, menu);
        interactListener = new InventoryObjectClickListener() {

            @Override
            public void onInteract(InventoryObjectClickEvent e) {
                Player p = e.getPlayer();
                if (material == null) {
                    ItemStack item = e.getItemInHand();
                    if (item != null) {
                        if (item.getType() != Material.AIR) {
                            setMaterial(item);
                            update();
                        }
                    }
                } else {
                    menu.openCompositionEditor(material, p);
                }
            }
        };
        this.mine = m;
    }

    @Override
    public void update() {
        if (material != null) {
            icon = GUIResourcesUtils.generateCompositionItemStack(mine, material);
        } else {
            icon = GUIResourcesUtils.EMPTY_MATERIAL;
        }
    }

    public Mine getMine() {
        return mine;
    }

}
