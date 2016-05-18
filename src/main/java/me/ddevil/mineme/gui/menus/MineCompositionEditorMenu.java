/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ddevil.mineme.gui.menus;

import java.util.Arrays;
import me.ddevil.core.utils.inventory.BasicInventoryMenu;
import me.ddevil.core.utils.inventory.InventoryUtils;
import me.ddevil.core.utils.inventory.objects.BackButton;
import me.ddevil.core.utils.items.ItemUtils;
import me.ddevil.mineme.gui.GUIResourcesUtils;
import me.ddevil.mineme.messages.MineMeMessageManager;
import me.ddevil.mineme.mines.Mine;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author HP
 */
public class MineCompositionEditorMenu extends BasicInventoryMenu {

    private final ItemStack material;
    private final Mine owner;

    public MineCompositionEditorMenu(Mine owner, ItemStack material) {
        super(MineMeMessageManager.getInstance().translateAll("$2" + material.getType() + "$3:$1" + material.getData().getData()), GUIResourcesUtils.INVENTORY_SIZE);
        this.owner = owner;
        this.material = owner.getItemStackInComposition(material);
    }
    private final BackButton backButton = new BackButton(null, GUIResourcesUtils.BACK_BUTTON, null);

    @Override
    protected void setupItems() {
        ItemStack invIcon = ItemUtils.createItem(
                //Reference item stack
                material,
                //Item name
                mainInventory.getTitle(),
                //Lore
                MineMeMessageManager.getInstance().translateAll(
                        Arrays.asList(new String[]{
                            "$3Current: $1" + owner.getPercentage(material) + "%"
                        })));
        InventoryUtils.drawSquare(mainInventory, 18, 35, GUIResourcesUtils.generateInformationItem(owner));
        int middle = InventoryUtils.getMiddlePoint(mainInventory);
        registerInventoryObject(backButton, middle - 9);
        setItem(owner.getIcon(), middle);
        setItem(GUIResourcesUtils.generateInformationItem(owner), middle - 18);
        mainInventory.setItem(middle + 9, GUIResourcesUtils.REMOVE_BUTTON);
    }

    @Override
    public void update() {

        double currentAddPercentage = 50;
        //Top left 4 add buttons
        for (int i : InventoryUtils.getPartialLane(mainInventory, 0, 0, 3)) {
            mainInventory.setItem(i, GUIResourcesUtils.generateCompositionChangeItemStack(currentAddPercentage));
            currentAddPercentage /= 2;
        }
        //Bottom left 4 remove buttons
        double currentRemovePercentage = -50;
        for (int i : InventoryUtils.getPartialLane(mainInventory, InventoryUtils.getTotalLanes(mainInventory) - 1, 0, 3)) {
            mainInventory.setItem(i, GUIResourcesUtils.generateCompositionChangeItemStack(currentRemovePercentage));
            currentRemovePercentage /= 2;
        }
        //Top right 4 add buttons
        int[] customValues = {1, 5, 10, 20};
        int currentCustomUpid = 0;
        for (int i : InventoryUtils.getPartialLane(mainInventory, 0, 5, 8)) {
            mainInventory.setItem(i, GUIResourcesUtils.generateCompositionChangeItemStack(customValues[currentCustomUpid]));
            currentCustomUpid++;
        }
        //Bottom right 4 add buttons
        int currentCustomBottomid = 0;
        for (int i : InventoryUtils.getPartialLane(mainInventory, InventoryUtils.getTotalLanes(mainInventory) - 1, 5, 8)) {
            mainInventory.setItem(i, GUIResourcesUtils.generateCompositionChangeItemStack(customValues[currentCustomBottomid] * -1));
            currentCustomBottomid++;
        }
        //Containers
        for (int i : InventoryUtils.getLane(mainInventory, InventoryUtils.getTotalLanes(mainInventory) - 2)) {
            mainInventory.setItem(i, GUIResourcesUtils.SPLITTER);
        }
        for (int i : InventoryUtils.getLane(mainInventory, 1)) {
            mainInventory.setItem(i, GUIResourcesUtils.SPLITTER);
        }
        //Extra items
        mainInventory.setItem(InventoryUtils.getTopMiddlePoint(mainInventory), GUIResourcesUtils.SPLITTER);
        mainInventory.setItem(InventoryUtils.getBottomMiddlePoint(mainInventory), GUIResourcesUtils.SPLITTER);
    }

}
