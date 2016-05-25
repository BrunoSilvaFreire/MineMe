/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ddevil.mineme.thread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import me.ddevil.core.thread.CustomThread;
import me.ddevil.core.utils.items.ItemUtils;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.gui.GUIResourcesUtils;
import me.ddevil.mineme.mines.MineManager;
import me.ddevil.mineme.mines.configs.MineConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author BRUNO II
 */
public class MineFilesFinder extends CustomThread {

    private final List<MineConfig> mines = new ArrayList();
    private final MineMe plugin = MineMe.getInstance();
    private final List<String> notLoadedFiles = new ArrayList();

    @Override
    public void doRun() {
        File[] mineFiles = MineMe.minesFolder.listFiles();
        MineMe.instance.debug("Searching for disabled mines...");
        //Start loading mines
        for (File file : mineFiles) {
            String filename = file.getName();
            String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
            if (!"yml".equals(extension)) {
                plugin.debug(filename + " isn't a .yml file! This shouldn't be here! Skipping.", true);
                continue;
            }
            try {
                FileConfiguration mine = YamlConfiguration.loadConfiguration(file);
                //Get name
                String name = mine.getString("name");
                if (MineManager.getMine(name) == null) {
                    mines.add(new MineConfig(mine));
                }
            } catch (Exception e) {
                plugin.debug("Could not read file " + filename + " while searching for mine files! Maybe you're editing the file? :P", true);
                notLoadedFiles.add("§c" + filename);
            }
        }
    }

    public List<MineConfig> getMines() {
        return mines;
    }

    public ItemStack getNotLoadedItemStat() {
        ItemStack i;
        if (notLoadedFiles.isEmpty()) {
            i = ItemUtils.addToLore(GUIResourcesUtils.FILES_SEARCH_RESULT, GUIResourcesUtils.NO_MISFORMATTED_FILES);
        } else {
            i = ItemUtils.addToLore(GUIResourcesUtils.FILES_SEARCH_RESULT, notLoadedFiles);
        }
        i = ItemUtils.addToLore(
                GUIResourcesUtils.FILES_SEARCH_RESULT,
                GUIResourcesUtils.FOUND_MINE_FILES.replace("%total%", String.valueOf(mines.size()))
        );
        return i;
    }

    public List<String> getNotLoadedFiles() {
        return notLoadedFiles;
    }

}
