/* 
 * Copyright (C) 2016 Selma
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.ddevil.mineme.messages;

import java.util.ArrayList;
import java.util.List;
import me.ddevil.core.chat.BasicMessageManager;
import me.ddevil.core.utils.StringUtils;
import me.ddevil.core.utils.items.ItemUtils;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.storage.StorageManager;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class MineMeMessageManager extends BasicMessageManager {

    private static MineMeMessageManager instance;

    public static MineMeMessageManager getInstance() {
        if (instance == null) {
            instance = new MineMeMessageManager();
        }
        return instance;
    }

    //Mine Messages
    public static String globalResetMessage;
    public static String mineCreateMessage;

    //Error Messages
    public static String noPermission;
    public static String invalidArguments;

    //Colors
    private MineMe mineMe;

    @Override
    public void postSetup() {
        mineMe = MineMe.getInstance();
        try {
            mineMe.debug("Loading messages...");
            //Mine Messages
            globalResetMessage = translateColors(MineMe.messagesConfig.getString("messages.mineReset"));
            mineCreateMessage = translateColors(MineMe.messagesConfig.getString("messages.mineCreate"));

            //Global Messages
            messageSeparator = translateColors(MineMe.messagesConfig.getString("messages.messageSeparator"));
            pluginPrefix = translateColors(MineMe.messagesConfig.getString("messages.messagePrefix"));
            header = translateAll(MineMe.messagesConfig.getString("messages.header"));

            //Error Messages
            noPermission = translateColors(MineMe.messagesConfig.getString("messages.noPermission"));
            invalidArguments = translateColors(MineMe.messagesConfig.getString("messages.invalidArguments"));
            mineMe.debug("Messages loaded!");
            mineMe.debug();
        } catch (Exception e) {
            mineMe.debug("Something went wrong while loading messages :(");
            mineMe.debug("--== Error ==--");
            e.printStackTrace();
            mineMe.debug("--== Error ==--");
        }
    }

    public List<String> translateAll(Iterable<String> get, Mine m) {
        ArrayList<String> strings = new ArrayList();
        for (String s : get) {
            strings.add(translateAll(s));
        }
        return strings;
    }

    public String translateAll(String get, Mine m) {
        //Mine
        if (m != null) {
            get = get.replaceAll("%mine%", m.getName());
            get = get.replaceAll("%minedblocks%", String.valueOf(m.getMinedBlocks()));
            get = get.replaceAll("%minedblockspercent%", String.valueOf(m.getPercentageMined()));
            get = get.replaceAll("%remainingblocks%", String.valueOf(m.getRemainingBlocks()));
            get = get.replaceAll("%remainingblockspercent%", String.valueOf(m.getPercentageRemaining()));
            get = get.replaceAll("%volume%", m.getVolume() + "");
            get = get.replaceAll("%resettime%", StringUtils.secondsToString(m.getTimeToNextReset()));
            get = get.replaceAll("%alias%", m.getAlias());
            get = get.replaceAll("%type%", m.getType().name());
            get = get.replaceAll("%totalpercentage%", String.valueOf(m.getTotalPercentage()));
            get = get.replaceAll("%totalmaterials%", String.valueOf(m.getTotalMaterials()));
            get = get.replaceAll("%avgspeed%", String.valueOf(m.averageBreakSpeed()));
            //LifeTime stats
            get = get.replaceAll("%totalminedblocks%", String.valueOf(StorageManager.getTotalBrokenBlocks(m)));
            get = get.replaceAll("%totalresets%", String.valueOf(StorageManager.getTotalResets(m)));
        } else {
            MineMe.instance.debug("The mine used to translate " + get + " is null! Skipping mine tags...", true);
        }
        boolean translateComposition = get.contains("%composition:");
        while (translateComposition) {
            int start = get.indexOf("%composition:") + 13;
            Integer end = null;
            for (int i = start - 12; i < get.length(); i++) {
                char c = get.charAt(i);
                if (c == '%') {
                    end = i;
                }
            }
            if (end == null) {
                break;
            }
            String itemName = get.substring(start, end);
            ItemStack item = null;

            try {
                item = ItemUtils.convertFromInput(itemName);
            } catch (me.ddevil.core.exceptions.ItemConversionException ex) {
                MineMe.instance.printException(get, ex);

            }

            String find = "%composition:" + itemName + "%";
            get = get.replace(find, String.valueOf(m.getPercentage(item)));
            translateComposition = get.contains("%composition:");

        }
        return translateAll(get);
    }

    public String getResetMessage(Mine m) {
        return translateAll(globalResetMessage, m);
    }

    @Override
    public String translateTags(String input) {
        if (pluginPrefix != null) {
            input = input.replaceAll("%prefix%", pluginPrefix);
        }
        if (messageSeparator != null) {
            input = input.replaceAll("%separator%", messageSeparator);
        }
        if (header != null) {
            input = input.replaceAll("%header%", header);
        }
        return input;
    }

}
