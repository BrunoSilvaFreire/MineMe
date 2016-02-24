package me.ddevil.mineme;

import me.ddevil.mineme.mines.Mine;
import net.md_5.bungee.api.ChatColor;

public class MessageManager {

    private static final char colorChar = ' ';
    //Mine Messages
    public static final String globalResetMessage = translateAlternateColorCodes(MineMe.messagesConfig.getString("messages.resetMessage"));

    //Global Messages
    public static final String pluginPrefix = translateAlternateColorCodes(MineMe.messagesConfig.getString("messages.messageSeparator"));
    public static final String messageSeparator = translateAlternateColorCodes(MineMe.messagesConfig.getString("messages.messagePrefix"));

    //Error Messages
    public static final String noPermission = translateAlternateColorCodes(MineMe.messagesConfig.getString("messages.noPermission"));
    public static final String invalidArguments = translateAlternateColorCodes(MineMe.messagesConfig.getString("messages.invalidArguments"));

    //Colors
    public static final String primaryColor = translateAlternateColorCodes(MineMe.messagesConfig.getString("primaryColor"));
    public static final String secondaryColor = translateAlternateColorCodes(MineMe.messagesConfig.getString("secondaryColor"));
    public static final String neutralColor = translateAlternateColorCodes(MineMe.messagesConfig.getString("neutralColor"));
    public static final String warningColor = translateAlternateColorCodes(MineMe.messagesConfig.getString("warningColor"));

    public static String translateTags(String get, Mine m) {
        get = get.replaceAll("%mine%", m.getName());
        return translateAlternateColorCodes(get);
    }

    public static String getResetMessage(Mine m) {
        return translateTags(globalResetMessage, m);
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
                return "";
        }
    }

    public static String translateAlternateColorCodes(String trans) {
        char[] b = trans.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == '$' && "1234".indexOf(b[i + 1]) > -1) {
                b[i] = ChatColor.COLOR_CHAR;
                b[i + 1] = getColor(i).charAt(0);
            }
        }
        return ChatColor.translateAlternateColorCodes(colorChar, new String(b));
    }

}
