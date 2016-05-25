/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ddevil.mineme.gui.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.ddevil.core.events.inventory.InventoryObjectClickEvent;
import me.ddevil.core.utils.inventory.InventoryMenu;
import me.ddevil.core.utils.inventory.objects.BasicClickableInventoryObject;
import me.ddevil.core.utils.inventory.objects.interfaces.InventoryObjectClickListener;
import me.ddevil.core.utils.items.ItemUtils;
import me.ddevil.mineme.messages.MineMeMessageManager;
import me.ddevil.mineme.mines.Mine;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author BRUNO II
 */
public class MaterialCompositionChanger extends BasicClickableInventoryObject {

    private final Mine mine;
    private final double percentage;
    private final ItemStack material;

    public MaterialCompositionChanger(final ItemStack material, final InventoryMenu menu, final Mine m, final double percentage) {
        super(generateCompositionChangeItemStack(percentage, m, material), new InventoryObjectClickListener() {

            @Override
            public void onInteract(InventoryObjectClickEvent e) {
                m.setMaterialPercentage(material, m.getPercentage(material) + percentage);
                menu.update();
            }
        }, menu);
        this.material = material;
        this.mine = m;
        this.percentage = percentage;
    }

    public double getPercentage() {
        return percentage;
    }

    @Override
    public void update() {
        icon = generateCompositionChangeItemStack(percentage, mine, material);
    }

    private static ItemStack generateCompositionChangeItemStack(double change, Mine mine, ItemStack material) {
        boolean add = change > 0;
        String prefix = add ? "§a+" : "§c";
        Material m = add ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK;
        ItemStack is = ItemUtils.createItem(m, prefix + change + "%");
        double finalP = (mine.getPercentage(material) + change);
        if (finalP < 0) {
            finalP = 0;
        }
        ArrayList<String> lore = new ArrayList(Arrays.asList(
                new String[]{
                    "$3Current percentage: $2" + mine.getPercentage(material),
                    "$3Final percentage: $1" + finalP
                }
        )
        );
        lore.add("$3Mine total percentage: $2" + mine.getTotalPercentage());
        if (mine.isExceedingMaterials()) {
            lore.add("$4Mine is exceeding materials by $2" + mine.getExceedingTotal() + "%$4!");
        } else {
            double free = mine.getFreePercentage();
            if (free == 0) {
                lore.add("$1Mine is completely filled!");
            } else {
                lore.add("$3Space left for materials:$1 " + free + "%");
            }
        }
        lore.add("§r");
        lore.add("$3Other materials:");
        List<ItemStack> materials = mine.getMaterials();
        if (materials.size() - 1 <= 0) {
            lore.add("$4There are no other materials!!!");
        } else {
            for (ItemStack s : materials) {
                if (!s.equals(material)) {
                    lore.add(generateItemPrefix(s, mine));
                }
            }
        }
        is = ItemUtils.addToLore(
                is,
                MineMeMessageManager.getInstance().translateAll(lore, mine)
        );
        return is;
    }

    private static String generateItemPrefix(ItemStack material, Mine mine) {
        return "$3* $1" + material.getType() + "$3:$2" + material.getData().getData() + "$3-$1" + mine.getPercentage(material) + "%";
    }

    public Mine getMine() {
        return mine;
    }

    public ItemStack getMaterial() {
        return material;
    }

}
