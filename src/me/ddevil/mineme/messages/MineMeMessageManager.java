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
import me.ddevil.core.chat.BasicMessageManager;
import me.ddevil.core.utils.ItemUtils;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.mines.MineUtils;
import me.ddevil.mineme.storage.StorageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MineMeMessageManager extends BasicMessageManager {

    private static final char colorChar = '&';
    //Mine Messages
    public static String globalResetMessage;
    public static String mineCreateMessage;

    //Error Messages
    public static String noPermission;
    public static String invalidArguments;

    //Colors
    public static String primaryColor;
    public static String secondaryColor;
    public static String neutralColor;
    public static String warningColor;
    private static MineMe mineMe;

    @Override
    public void setup() {
        mineMe = MineMe.getInstance();
        mineMe.debug("Loading colors...");
        try {
            //Colors
            primaryColor = MineMe.messagesConfig.getString("primaryColor");
            secondaryColor = MineMe.messagesConfig.getString("secondaryColor");
            neutralColor = MineMe.messagesConfig.getString("neutralColor");
            warningColor = MineMe.messagesConfig.getString("warningColor");
            mineMe.debug(new String[]{
                "Colors set to:",
                "Primary: " + primaryColor,
                "Secondary: " + secondaryColor,
                "Neutral: " + neutralColor,
                "Warning: " + warningColor,
                "Colors loaded!"});
            mineMe.debug();
            mineMe.debug("Loading messages...");
            //Mine Messages
            globalResetMessage = translateColors(MineMe.messagesConfig.getString("messages.mineReset"));
            mineCreateMessage = translateColors(MineMe.messagesConfig.getString("messages.mineCreate"));

            //Global Messages
            messageSeparator = translateColors(MineMe.messagesConfig.getString("messages.messageSeparator"));
            pluginPrefix = translateColors(MineMe.messagesConfig.getString("messages.messagePrefix"));
            header = translateTagsAndColor(MineMe.messagesConfig.getString("messages.header"));

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

    public static String translateTagsAndColors(String get, Mine m) {
        //Mine
        if (m != null) {
            get = get.replaceAll("%mine%", m.getName());
            get = get.replaceAll("%minedblocks%", String.valueOf(m.getMinedBlocks()));
            get = get.replaceAll("%minedblockspercent%", String.valueOf(m.getPercentageMined()));
            get = get.replaceAll("%remainingblocks%", String.valueOf(m.getRemainingBlocks()));
            get = get.replaceAll("%remainingblockspercent%", String.valueOf(m.getPercentageRemaining()));
            get = get.replaceAll("%volume%", m.getVolume() + "");
            get = get.replaceAll("%resettime%", secondsToString(m.getTimeToNextReset()));
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
        //Plugin
        get = get.replaceAll("%prefix%", pluginPrefix);
        get = get.replaceAll("%header%", header);
        get = get.replaceAll("%separator%", messageSeparator);
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
            try {
                ItemStack item = ItemUtils.convertFromInput(itemName);
                get = get.replaceAll("%composition:" + ItemUtils.toString(item) + "%", String.valueOf(m.getPercentage(item)));
                translateComposition = get.contains("%composition:");
            } catch (Exception e) {
                break;
            }

        }
        return translateColors(get);
    }

    public static String translateTagsAndColor(String get) {
        if (pluginPrefix != null) {
            get = get.replaceAll("%prefix%", pluginPrefix);
        }
        if (messageSeparator != null) {
            get = get.replaceAll("%separator%", messageSeparator);
        }
        if (header != null) {
            get = get.replaceAll("%header%", header);
        }
        return translateColors(get);
    }

    public static String getResetMessage(Mine m) {
        return translateTagsAndColors(globalResetMessage, m);
    }

    public static String getColor(int i) {
        switch (i) {
            case 1:
                return primaryColor;
            case 2:
                return secondaryColor;
            case 3:
                return neutralColor;
            case 4:
                return warningColor;
            default:
                return null;
        }
    }

    private static boolean isValidNumber(char c) {
        return c == '1' || c == '2' || c == '3' || c == '4';
    }

    public static String translateColors(String trans) {
        char[] b = trans.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == '$' && isValidNumber(b[i + 1])) {
                int a = Character.getNumericValue(b[i + 1]);
                String s = getColor(a);
                if (s != null) {
                    b[i] = ChatColor.COLOR_CHAR;
                    b[i + 1] = s.charAt(0);
                } else {
                    mineMe.debug("Message \"" + trans + "\" is badly color coded! Remeber to only use $1 to $4 !");
                }
            }
        }
        return ChatColor.translateAlternateColorCodes(colorChar, new String(b));
    }

    public static String[] translateColors(String[] trans) {
        ArrayList<String> afinal = new ArrayList();
        for (String s : trans) {
            afinal.add(translateColors(s));
        }
        return afinal.toArray(new String[afinal.size()]);
    }

    public static String[] translateTagsAndColors(String[] trans) {
        ArrayList<String> afinal = new ArrayList();
        for (String s : trans) {
            afinal.add(translateTagsAndColor(s));
        }
        return afinal.toArray(new String[afinal.size()]);
    }

    public static String[] translateTagsAndColors(String[] trans, Mine m) {
        ArrayList<String> afinal = new ArrayList();
        for (String s : trans) {
            afinal.add(translateTagsAndColors(s, m));
        }
        return afinal.toArray(new String[afinal.size()]);
    }

    @Override
    public void sendMessage(Player p, String string) {
        p.sendMessage(translateColors(pluginPrefix + messageSeparator + string));
    }

}
