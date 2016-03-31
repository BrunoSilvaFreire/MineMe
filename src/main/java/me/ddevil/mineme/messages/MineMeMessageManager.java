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
import me.ddevil.core.exceptions.ItemConversionException;
import me.ddevil.core.utils.StringUtils;
import me.ddevil.core.utils.items.ItemUtils;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.storage.StorageManager;
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
            mineMe.printException("Something went wrong while loading messages :(", e);

        }
    }

    public List<String> translateAll(Iterable<String> get, Mine m) {
        ArrayList<String> strings = new ArrayList();
        for (String s : get) {
            strings.add(translateAll(s, m));
        }
        return strings;
    }

    public String translateAll(String input, Mine m) {
        //Mine
        if (m != null) {
            input = input.replace("%mine%", m.getName());
            input = input.replace("%minedblocks%", String.valueOf(m.getMinedBlocks()));
            input = input.replace("%minedblockspercent%", String.valueOf(m.getPercentageMined()));
            input = input.replace("%remainingblocks%", String.valueOf(m.getRemainingBlocks()));
            input = input.replace("%remainingblockspercent%", String.valueOf(m.getPercentageRemaining()));
            input = input.replace("%volume%", m.getVolume() + "");
            input = input.replace("%resettime%", StringUtils.secondsToString(m.getTimeToNextReset()));
            input = input.replace("%alias%", m.getAlias());
            input = input.replace("%type%", m.getType().name());
            input = input.replace("%totalpercentage%", String.valueOf(m.getTotalPercentage()));
            input = input.replace("%totalmaterials%", String.valueOf(m.getTotalMaterials()));
            input = input.replace("%avgspeed%", String.valueOf(m.averageBreakSpeed()));
            //LifeTime stats
            input = input.replace("%totalminedblocks%", String.valueOf(StorageManager.getTotalBrokenBlocks(m)));
            input = input.replace("%totalresets%", String.valueOf(StorageManager.getTotalResets(m)));
        } else {
            MineMe.instance.debug("The mine used to translate " + input + " is null! Skipping mine tags...", true);
        }
        boolean translateComposition = input.contains("%composition:");
        while (translateComposition) {
            int start = input.indexOf("%composition:") + 13;
            Integer end = null;
            for (int i = start - 12; i < input.length(); i++) {
                char c = input.charAt(i);
                if (c == '%') {
                    end = i;
                }
            }
            if (end == null) {
                break;
            }
            String itemName = input.substring(start, end);
            ItemStack item;

            try {
                item = ItemUtils.convertFromInput(itemName);
            } catch (ItemConversionException ex) {
                MineMe.instance.printException(input, ex);
                break;
            }

            String find = "%composition:" + itemName + "%";
            input = input.replace(find, String.valueOf(m.getPercentage(item)));
            translateComposition = input.contains("%composition:");

        }
        return translateAll(input);
    }

    public String getResetMessage(Mine m) {
        return translateAll(globalResetMessage, m);
    }

    @Override
    public String translateTags(String input) {
        if (pluginPrefix != null) {
            input = input.replace("%prefix%", pluginPrefix);
        }
        if (messageSeparator != null) {
            input = input.replace("%separator%", messageSeparator);
        }
        if (header != null) {
            input = input.replace("%header%", header);
        }
        return input;
    }

}
