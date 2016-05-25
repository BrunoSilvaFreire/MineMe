/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ddevil.mineme.gui.menus;

import java.util.ArrayList;
import java.util.List;
import me.ddevil.core.events.inventory.InventoryObjectClickEvent;
import me.ddevil.core.utils.inventory.BasicInventoryMenu;
import me.ddevil.core.utils.inventory.objects.BackButton;
import me.ddevil.core.utils.inventory.objects.BasicClickableInventoryObject;
import me.ddevil.core.utils.inventory.objects.BasicInventoryContainer;
import me.ddevil.core.utils.inventory.objects.interfaces.InventoryObjectClickListener;
import me.ddevil.core.utils.items.ItemUtils;
import me.ddevil.mineme.gui.GUIResourcesUtils;
import me.ddevil.mineme.gui.objects.MaterialCompositionChanger;
import me.ddevil.mineme.messages.MineMeMessageManager;
import me.ddevil.mineme.mines.Mine;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author HP
 */
public class MineCompositionEditorMenu extends BasicInventoryMenu {

    private final ItemStack material;
    private final Mine mine;
    private final MineMenu mineMenu;

    private static String getName(ItemStack material) {
        return MineMeMessageManager.getInstance().translateAll("$2" + material.getType() + "$3:$1" + material.getData().getData());
    }

    public MineCompositionEditorMenu(final Mine owner, final ItemStack material, MineMenu menu) {
        super(getName(material), GUIResourcesUtils.INVENTORY_SIZE);
        this.mine = owner;
        this.mineMenu = menu;
        this.material = owner.getItemStackInComposition(material);
        this.backButton = new BackButton(mineMenu, GUIResourcesUtils.BACK_BUTTON, this);
        this.removeButton = new BasicClickableInventoryObject(GUIResourcesUtils.REMOVE_MATERIAL_BUTTON, new InventoryObjectClickListener() {

            @Override
            public void onInteract(InventoryObjectClickEvent e) {
                mine.removeMaterial(material);
                mineMenu.open(e.getPlayer());
            }
        }, this);
        this.changers = new BasicInventoryContainer(this, 9, 25);
        this.options = new BasicInventoryContainer(this, 36, 43);
    }
    private final BackButton backButton;
    private final BasicInventoryContainer options;
    private final BasicClickableInventoryObject removeButton;
    private final double[] percentages = {50, 25, 10, 5, 2, 1, 0.5, 0.1};
    private final BasicInventoryContainer changers;

    @Override
    protected void setupItems() {
        clearAndFill(GUIResourcesUtils.SPLITTER);
        registerInventoryObject(changers, 9);
        registerInventoryObject(options, 36);
        registerInventoryObject(backButton, 45);
        options.clearAndFill(GUIResourcesUtils.EMPTY_NEUTRAL);
        options.addObject(removeButton);
        int slot = 0;
        for (double d : percentages) {
            MaterialCompositionChanger pos = new MaterialCompositionChanger(material, this, mine, d);
            MaterialCompositionChanger neg = new MaterialCompositionChanger(material, this, mine, d * -1);
            changers.setObject(slot, pos);
            changers.setObject(slot + 8, neg);
            slot++;
        }
    }

    @Override
    public void update() {
        changers.update();
        options.setItem(7, generateItemStat());
    }

    private ItemStack generateItemStat() {
        ItemStack is = ItemUtils.createItem(material,
                MineMeMessageManager.getInstance().translateAll(generateItemPrefix()));
        ArrayList<String> lore = new ArrayList();
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
                    lore.add(generateItemPrefix(s));
                }
            }
        }
        is = ItemUtils.addToLore(
                is,
                MineMeMessageManager.getInstance().translateAll(lore, mine)
        );
        return is;
    }

    private String generateItemPrefix() {
        return "$3* $1" + material.getType() + "$3:$2" + material.getData().getData() + "$3-$1" + mine.getPercentage(material) + "%";
    }

    private String generateItemPrefix(ItemStack material) {
        return "$3* $1" + material.getType() + "$3:$2" + material.getData().getData() + "$3-$1" + mine.getPercentage(material) + "%";
    }
}
