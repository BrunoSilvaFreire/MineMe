/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ddevil.mineme.gui.menus;

import java.util.HashMap;
import java.util.List;
import me.ddevil.core.events.inventory.InventoryObjectClickEvent;
import me.ddevil.core.thread.ThreadFinishListener;
import me.ddevil.mineme.gui.objects.MineDisplay;
import me.ddevil.core.utils.inventory.BasicInventoryMenu;
import me.ddevil.core.utils.inventory.InventoryUtils;
import me.ddevil.core.utils.inventory.objects.BasicClickableInventoryObject;
import me.ddevil.core.utils.inventory.objects.BasicInventoryContainer;
import me.ddevil.core.utils.inventory.objects.BasicInventoryItem;
import me.ddevil.core.utils.inventory.objects.interfaces.InventoryObjectClickListener;
import me.ddevil.core.utils.items.ItemUtils;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.MineMeConfiguration;
import me.ddevil.mineme.gui.GUIResourcesUtils;
import me.ddevil.mineme.messages.MineMeMessageManager;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.mines.MineManager;
import me.ddevil.mineme.mines.configs.MineConfig;
import me.ddevil.mineme.thread.MineFilesFinder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Selma
 */
public class MainMenu extends BasicInventoryMenu {

    private static final int UNLOADED_MINE_LIST_SLOT = InventoryUtils.getFirstSlotInLane(4);

    public MainMenu(String name) {
        super(name, GUIResourcesUtils.INVENTORY_SIZE);
        this.mineFilesFinder = new MineFilesFinder();
        this.mineList = new BasicInventoryContainer(this, 0, 24);
        this.unloadedMineList = new BasicInventoryContainer(this, UNLOADED_MINE_LIST_SLOT, UNLOADED_MINE_LIST_SLOT + 8);
        this.refreshButton = new BasicClickableInventoryObject(GUIResourcesUtils.REFRESH, new InventoryObjectClickListener() {

            @Override
            public void onInteract(InventoryObjectClickEvent e) {
                update();
            }
        }, this);
    }
    protected ItemStack mainInventoryIcon;
    private final BasicClickableInventoryObject refreshButton;
    private final BasicInventoryContainer mineList;
    private final BasicInventoryContainer unloadedMineList;
    private final MineFilesFinder mineFilesFinder;

    @Override
    public void update() {
        //Already loaded mines
        mineList.clearAndFill(GUIResourcesUtils.NO_MINE_TO_DISPLAY);
        for (Mine mine : MineManager.getMines()) {
            try {
                ItemStack is = new ItemStack(mine.getIcon());
                mineList.addObject(new MineDisplay(mine, is, this));
            } catch (Exception e) {
                MineMe.instance.printException("There was a problem while loading mine " + mine.getName() + "'s icon", e);
            }
        }
        MineMe.instance.broadcastDebug(ItemUtils.toString(GUIResourcesUtils.NOT_LOADED_MINES));

        mineFilesFinder.addListener(new ThreadFinishListener() {

            @Override
            public void onFinish() {
                unloadedMineList.clearAndFill(GUIResourcesUtils.NOT_LOADED_MINES);
                unloadedMineList.setObject(
                        InventoryUtils.getLastSlotInLane(4),
                        new BasicInventoryItem(MainMenu.this, mineFilesFinder.getNotLoadedItemStat())
                );
                List<MineConfig> mines = mineFilesFinder.getMines();
                MineMe.instance.debug("Found " + mines.size() + " files in " + mineFilesFinder.getTotalTimeSeconds() + " seconds (" + mineFilesFinder.getTotalTime() + "ms)");
                for (final MineConfig mine : mines) {
                    ItemStack icon = generateItemStack(mine);
                    icon = ItemUtils.addToLore(icon, new String[]{GUIResourcesUtils.CLICK_TO_LOAD});
                    unloadedMineList.addObject(new BasicClickableInventoryObject(icon, new InventoryObjectClickListener() {
                        @Override
                        public void onInteract(InventoryObjectClickEvent e) {
                            Mine loadMine = MineManager.loadMine(mine);
                            loadMine.setEnabled(true);
                            loadMine.save();
                            Player player = e.getPlayer();
                            MineMe.chatManager.sendMessage(player, "Mine " + loadMine.getName() + " was loaded!");
                            update();
                        }
                    }, MainMenu.this));
                }
            }
        });
        mineFilesFinder.start();
    }

    @Override
    protected void setupItems() {
        this.mainInventoryIcon = MineMeMessageManager.getInstance().createIcon(MineMeConfiguration.guiConfig.getConfigurationSection("mainMenu.icons.main"));
        for (int i : InventoryUtils.getLane(mainInventory, InventoryUtils.getTotalLanes(mainInventory) - 1)) {
            setItem(GUIResourcesUtils.SPLITTER, i - 18);
            setItem(GUIResourcesUtils.SPLITTER, i);
        }
        setItem(mainInventoryIcon, InventoryUtils.getBottomMiddlePoint(mainInventory));
        setItem(ItemUtils.NA, 8);
        setItem(ItemUtils.NA, 17);
        setItem(GUIResourcesUtils.SPLITTER, 7);
        setItem(GUIResourcesUtils.SPLITTER, 16);
        setItem(GUIResourcesUtils.SPLITTER, 25);
        registerInventoryObject(refreshButton, 26);
        registerInventoryObject(mineList, 0);
        registerInventoryObject(unloadedMineList, UNLOADED_MINE_LIST_SLOT);
    }

    private ItemStack generateItemStack(MineConfig config) {
        FileConfiguration xconfig = config.getConfig();
        ItemStack createItem = ItemUtils.createItem(
                new ItemStack(
                        Material.valueOf(xconfig.getString("icon.type")),
                        1,
                        ((Integer) xconfig.getInt("icon.data")).shortValue()
                ),
                "§a" + config.getName() + ".yml"
        );
        createItem = ItemUtils.addToLore(createItem, new String[]{
            "§7Alias: " + MineMeMessageManager.getInstance().translateAll(config.getAlias()),
            "§7Type: §d" + config.getType(),
            "§7World: §e" + config.getWorld().getName()
        });
        return createItem;
    }
    protected final HashMap<Mine, MineMenu> inventories = new HashMap();

    public void openMineMenu(Mine m, Player p) {
        getMineInventory(m).open(p);
    }

    public MineMenu getMineInventory(Mine m) {
        if (inventories.containsKey(m)) {
            return inventories.get(m);
        } else {
            MineMe.instance.debug("Creating new MineMenu for mine " + m.getName(), 2);
            MineMenu inv = new MineMenu(m);
            inv.initialSetup();
            inventories.put(m, inv);
            return inv;
        }
    }
}
