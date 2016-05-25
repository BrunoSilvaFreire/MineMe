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
import me.ddevil.mineme.gui.GUIResourcesUtils;
import me.ddevil.mineme.gui.menus.MineMenu;
import me.ddevil.mineme.mines.Mine;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

/**
 *
 * @author BRUNO II
 */
public class MineEffectDisplay extends BasicClickableInventoryObject {

    protected PotionEffect potionEffect;
    private final Mine mine;

    public void setPotionEffect(PotionEffect potionEffect) {
        this.potionEffect = potionEffect;
        update();
    }

    public PotionEffect getPotionEffect() {
        return potionEffect;
    }

    public MineEffectDisplay(Mine m, final MineMenu menu) {
        super(GUIResourcesUtils.EMPTY_EFFECT, menu);
        this.mine = m;
        this.interactListener = new InventoryObjectClickListener() {

            @Override
            public void onInteract(InventoryObjectClickEvent e) {
                if (potionEffect == null) {
                    ItemStack itemInHand = e.getItemInHand();
                    if (itemInHand != null) {
                        if (itemInHand.hasItemMeta()) {
                            ItemMeta itemMeta = itemInHand.getItemMeta();
                            if (itemMeta instanceof PotionMeta) {
                                PotionMeta m = (PotionMeta) itemMeta;
                                PotionType type = m.getBasePotionData().getType();
                                potionEffect = type.getEffectType().createEffect(21, type.getMaxLevel());
                                mine.addPotionEffect(potionEffect);
                                menu.update();
                            }
                        }
                    }
                } else if (e.getClickType() == InventoryObjectClickEvent.InteractionType.INVENTORY_CLICK_LEFT) {
                    PotionEffectType type = potionEffect.getType();
                    int amplifier = potionEffect.getAmplifier();
                    PotionEffect newEffect;
                    if (amplifier >= 10) {
                        newEffect = new PotionEffect(type, potionEffect.getDuration(), 0);
                    } else {
                        newEffect = new PotionEffect(type, potionEffect.getDuration(), amplifier + 1);
                    }
                    potionEffect = newEffect;
                    mine.addPotionEffect(potionEffect);
                    menu.update();
                } else if (e.getClickType() == InventoryObjectClickEvent.InteractionType.INVENTORY_CLICK_RIGHT) {
                    mine.removePotionEffect(potionEffect.getType());
                    potionEffect = null;
                    menu.update();
                }
            }
        };
    }

    public Mine getMine() {
        return mine;
    }

    private static ItemStack generateIcon(PotionEffect e) {
        ItemStack createItem = ItemUtils.createItem(Material.POTION, "§e" + e.getType().getName());
        createItem = ItemUtils.addToLore(createItem, "§7Amplifier: §a" + e.getAmplifier());
        return createItem;
    }

    @Override
    public void update() {
        if (potionEffect != null) {
            icon = generateIcon(potionEffect);
        } else {
            icon = GUIResourcesUtils.EMPTY_EFFECT;
        }
    }
}
