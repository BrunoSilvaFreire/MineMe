/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ddevil.mineme.gui.objects;

import java.util.List;
import me.ddevil.core.CustomPlugin;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

/**
 *
 * @author BRUNO II
 */
public class MineEffectDisplay extends BasicClickableInventoryObject {
    
    protected PotionEffect potionEffect;
    private final Mine mine;
    
    public void setPotionEffect(PotionEffect potionEffect) {
        this.potionEffect = potionEffect;
    }
    
    public PotionEffect getPotionEffect() {
        return potionEffect;
    }
    
    public MineEffectDisplay(Mine m, final MineMenu menu) {
        super(GUIResourcesUtils.EMPTY_EFFECTS, menu);
        this.mine = m;
        this.interactListener = new InventoryObjectClickListener() {
            
            @Override
            public void onInteract(InventoryObjectClickEvent e) {
                if (potionEffect == null) {
                    ItemStack item = e.getItemInHand();
                    if (item != null) {
                        if (item.getType() != Material.AIR) {
                            ItemMeta meta = item.getItemMeta();
                            if (meta instanceof PotionMeta) {
                                PotionMeta potionMeta = (PotionMeta) meta;
                                List<PotionEffect> customEffects = potionMeta.getCustomEffects();
                                if (!customEffects.isEmpty()) {
                                    potionEffect = customEffects.get(0);
                                }
                            }
                            update();
                        }
                    }
                } else if (e.getClickType() == InventoryObjectClickEvent.InteractionType.INVENTORY_CLICK_RIGHT) {
                    mine.removePotionEffect(potionEffect.getType());
                }
            }
        };
    }
    
    public Mine getMine() {
        return mine;
    }
    
    private static ItemStack generateIcon(PotionEffect e) {
        ItemStack createItem = ItemUtils.createItem(Material.POTION, "§e" + e.getType().getName());
        createItem = ItemUtils.addToLore(createItem, new String[]{
            "§7Amplifier: §a" + e.getAmplifier(),
            "§7Duration: §b" + e.getDuration()
        });
        return createItem;
    }
    
    @Override
    public void update() {
        if (potionEffect != null) {
            icon = generateIcon(potionEffect);
        } else {
            icon = GUIResourcesUtils.EMPTY_MATERIAL;
        }
        MineMe.instance.broadcastDebug(icon.serialize().toString());
    }
}
