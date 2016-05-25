/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ddevil.mineme.gui.objects;

import java.util.List;
import me.ddevil.core.events.inventory.InventoryObjectClickEvent;
import me.ddevil.core.utils.inventory.objects.BasicClickableInventoryObject;
import me.ddevil.core.utils.inventory.objects.interfaces.InventoryObjectClickListener;
import me.ddevil.core.utils.items.ItemUtils;
import me.ddevil.mineme.gui.GUIResourcesUtils;
import me.ddevil.mineme.gui.menus.MineMenu;
import me.ddevil.mineme.messages.MineMeMessageManager;
import me.ddevil.mineme.mines.Mine;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author BRUNO II
 */
public class MineMaterialDisplay extends BasicClickableInventoryObject {

    protected ItemStack material = null;
    private final Mine mine;

    public ItemStack getMaterial() {
        return material;
    }

    public void setMaterial(ItemStack material) {
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
                            item.setAmount(1);
                            mine.addMaterial(item);
                            menu.update();
                        }
                    }
                } else if (e.getClickType() == InventoryObjectClickEvent.InteractionType.INVENTORY_CLICK_LEFT) {
                    menu.openCompositionEditor(material, p);
                } else if (e.getClickType() == InventoryObjectClickEvent.InteractionType.INVENTORY_CLICK_RIGHT) {
                    mine.removeMaterial(material);
                    menu.update();
                }
            }
        };
        this.mine = m;
    }

    @Override
    public void update() {
        if (material != null) {
            icon = generateCompositionItemStack(mine, material);
        } else {
            icon = GUIResourcesUtils.EMPTY_MATERIAL;
        }
        icon.setAmount(1);
    }

    public Mine getMine() {
        return mine;
    }

    private static ItemStack generateCompositionItemStack(Mine m, ItemStack i) {
        ItemStack is = new ItemStack(i);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(
                MineMeMessageManager.getInstance().translateAll("$1" + is.getType() + "$3:$2" + i.getData().getData() + "$3-$1" + m.getComposition().get(i) + "%")
        );
        List<String> lore = ItemUtils.getLore(i);
        lore.add(GUIResourcesUtils.CLICK_TO_EDIT);
        im.setLore(lore);
        is.setItemMeta(im);
        return is;
    }
}
