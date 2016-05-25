/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ddevil.mineme.gui.menus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.ddevil.core.events.inventory.InventoryObjectClickEvent;
import me.ddevil.core.utils.inventory.BasicInventoryMenu;
import me.ddevil.core.utils.inventory.InventoryUtils;
import me.ddevil.core.utils.inventory.objects.BackButton;
import me.ddevil.core.utils.inventory.objects.BasicClickableInventoryObject;
import me.ddevil.core.utils.inventory.objects.interfaces.InventoryObjectClickListener;
import me.ddevil.core.utils.items.ItemUtils;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.gui.GUIManager;
import me.ddevil.mineme.gui.GUIResourcesUtils;
import me.ddevil.mineme.gui.objects.MineMEController;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.mines.MineUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author HP
 */
public class MineMenu extends BasicInventoryMenu {

    private final HashMap<ItemStack, MineCompositionEditorMenu> compositionEditInventories = new HashMap();
    private final Mine mine;

    public MineMenu(Mine m) {
        super(m.getAlias(), GUIResourcesUtils.INVENTORY_SIZE);
        this.mine = m;
        this.backButton = new BackButton(GUIManager.mainMenu, GUIResourcesUtils.BACK_BUTTON, this);
        this.mineMEController = new MineMEController(this, mine);
        this.teleporter = new BasicClickableInventoryObject(GUIResourcesUtils.TELEPORT_TO_MINE_BUTTON, new InventoryObjectClickListener() {

            @Override
            public void onInteract(InventoryObjectClickEvent e) {
                e.getPlayer().teleport(mine.getTopCenterLocation());
            }
        }, this);
        this.reseter = new BasicClickableInventoryObject(GUIResourcesUtils.RESET_MINE_BUTTON, new InventoryObjectClickListener() {

            @Override
            public void onInteract(InventoryObjectClickEvent e) {
                mine.reset();
                update();
            }
        }, this);
        materialClearer = new BasicClickableInventoryObject(GUIResourcesUtils.MINE_CLEAR_MATERIALS, new InventoryObjectClickListener() {

            @Override
            public void onInteract(InventoryObjectClickEvent e) {
                mine.clearMaterials();
                update();
            }
        }, this);
        deleteMineButton = new BasicClickableInventoryObject(GUIResourcesUtils.DELETE_MINE_BUTTON, new InventoryObjectClickListener() {

            @Override
            public void onInteract(InventoryObjectClickEvent e) {
                Player player = e.getPlayer();
                player.closeInventory();
                MineMe.chatManager.sendMessage(player, "Mine " + mine.getName() + " was deleted!");
                mine.delete();
            }
        }, this);
        disableMineButton = new BasicClickableInventoryObject(GUIResourcesUtils.DISABLE_MINE_BUTTON, new InventoryObjectClickListener() {

            @Override
            public void onInteract(InventoryObjectClickEvent e) {
                Player player = e.getPlayer();
                MineMe.chatManager.sendMessage(player, "Mine " + mine.getName() + " was disabled!");
                mine.disable();
                player.closeInventory();
            }
        }, this);
        clearMine = new BasicClickableInventoryObject(GUIResourcesUtils.CLEAR_MINE, new InventoryObjectClickListener() {

            @Override
            public void onInteract(InventoryObjectClickEvent e) {
                mine.clear();
            }
        }, this);
        this.fillMine = new BasicClickableInventoryObject(GUIResourcesUtils.FILL_MINE, new InventoryObjectClickListener() {

            @Override
            public void onInteract(InventoryObjectClickEvent e) {
                ItemStack item = e.getItemInHand();
                if (item != null) {
                    if (item.getType() != Material.AIR) {
                        mine.fill(item);
                        MineMe.chatManager.sendMessage(e.getPlayer(), mine.getName() + " was filled with $2" + ItemUtils.toString(item));
                    }
                }
            }
        }, this);
        this.balanceMaterials = new BasicClickableInventoryObject(GUIResourcesUtils.BALANCE_MATERIALS, new InventoryObjectClickListener() {

            @Override
            public void onInteract(InventoryObjectClickEvent e) {
                List<ItemStack> materials = mine.getMaterials();
                double percentage = 100 / materials.size();
                for (ItemStack material : materials) {
                    mine.setMaterialPercentage(material, percentage);
                }
                MineMe.chatManager.sendMessage(e.getPlayer(), "Materials were balanced with $2" + percentage + " $3percent each");
                update();
            }
        }, this);
        this.resetMaterials = new BasicClickableInventoryObject(GUIResourcesUtils.RESET_MATERIALS, new InventoryObjectClickListener() {

            @Override
            public void onInteract(InventoryObjectClickEvent e) {
                List<ItemStack> materials = mine.getMaterials();

                for (ItemStack material : materials) {
                    mine.setMaterialPercentage(material, 5);
                }
                MineMe.chatManager.sendMessage(e.getPlayer(), "Materials were reseted to $25% $3each");
                update();
            }
        }, this);
    }
    private final BackButton backButton;
    private final BasicClickableInventoryObject resetMaterials;
    private final BasicClickableInventoryObject balanceMaterials;
    private final BasicClickableInventoryObject clearMine;
    private final BasicClickableInventoryObject teleporter;
    private final BasicClickableInventoryObject reseter;
    private final BasicClickableInventoryObject materialClearer;
    private final BasicClickableInventoryObject disableMineButton;
    private final BasicClickableInventoryObject deleteMineButton;
    private final BasicClickableInventoryObject fillMine;
    private final MineMEController mineMEController;

    @Override
    protected void setupItems() {
        for (int i : InventoryUtils.getLane(mainInventory, InventoryUtils.getTotalLanes(mainInventory) - 1)) {
            setItem(GUIResourcesUtils.SPLITTER, i);
            setItem(GUIResourcesUtils.SPLITTER, i - 18);
        }
        registerInventoryObject(backButton, InventoryUtils.getBottomMiddlePoint(mainInventory) - 4);
        registerInventoryObject(teleporter, 0);
        registerInventoryObject(reseter, 1);
        registerInventoryObject(clearMine, 2);
        registerInventoryObject(fillMine, 3);
        registerInventoryObject(materialClearer, 9);
        registerInventoryObject(balanceMaterials, 10);
        registerInventoryObject(resetMaterials, 11);
        registerInventoryObject(disableMineButton, InventoryUtils.getLastSlotInLane(2) - 1);
        registerInventoryObject(deleteMineButton, InventoryUtils.getLastSlotInLane(2));
        registerInventoryObject(mineMEController, InventoryUtils.getFirstSlotInLane(4));
        setItem(GUIResourcesUtils.generateInformationItem(mine), InventoryUtils.getBottomMiddlePoint(mainInventory) + 4);
        setItem(mine.getIcon(), InventoryUtils.getBottomMiddlePoint(mainInventory));
    }

    @Override
    public void update() {
        Map<ItemStack, Double> composition = mine.getComposition();
        for (ItemStack material : mine.getMaterials()) {
            if (composition.get(material) == null) {
                composition.put(material, 0d);
            }
        }
        setItem(GUIResourcesUtils.generateInformationItem(mine), InventoryUtils.getBottomMiddlePoint(mainInventory) + 4);
        mineMEController.update();
    }

    public Mine getMine() {
        return mine;
    }

    public void openCompositionEditor(ItemStack itemStackInComposition, Player p) {
        getMineCompositionEditorMenu(itemStackInComposition).open(p);
    }

    private MineCompositionEditorMenu getMineCompositionEditorMenu(ItemStack item) {
        item = MineUtils.getItemStackInComposition(mine, item);
        MineCompositionEditorMenu menu;
        if (compositionEditInventories.containsKey(item)) {
            menu = compositionEditInventories.get(item);
        } else {
            menu = new MineCompositionEditorMenu(mine, item, this);
            menu.initialSetup();
        }
        compositionEditInventories.put(item, menu);
        return menu;
    }

}
