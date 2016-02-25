package me.ddevil.mineme;

import java.util.ArrayList;
import me.ddevil.core.utils.StringUtils;
import me.ddevil.mineme.mines.Mine;
import net.md_5.bungee.api.ChatColor;

public class MessageManager {

    private static char colorChar = '&';
    //Mine Messages
    public static String globalResetMessage = translateColors(MineMe.messagesConfig.getString("messages.resetMessage"));

    //Global Messages
    public static String pluginPrefix;
    public static String messageSeparator;

    //Error Messages
    public static String noPermission;
    public static String invalidArguments;

    //Colors
    public static String primaryColor;
    public static String secondaryColor;
    public static String neutralColor;
    public static String warningColor;

    public static void setup() {
        MineMe.getInstance().debug("Loading colors...");
        //Colors
        primaryColor = MineMe.messagesConfig.getString("primaryColor");
        secondaryColor = MineMe.messagesConfig.getString("secondaryColor");
        neutralColor = MineMe.messagesConfig.getString("neutralColor");
        warningColor = MineMe.messagesConfig.getString("warningColor");
        MineMe.getInstance().debug(new String[]{
            "Colors set to:",
            "Primary: " + primaryColor,
            "Secondary: " + secondaryColor,
            "Neutral: " + neutralColor,
            "Warning: " + warningColor,
            "Colors loaded!"});
        MineMe.getInstance().debug();
        MineMe.getInstance().debug("Loading messages...");
        //Mine Messages
        globalResetMessage = translateColors(MineMe.messagesConfig.getString("messages.resetMessage"));

        //Global Messages
        messageSeparator = translateColors(MineMe.messagesConfig.getString("messages.messageSeparator"));
        pluginPrefix = translateColors(MineMe.messagesConfig.getString("messages.messagePrefix"));

        //Error Messages
        noPermission = translateColors(MineMe.messagesConfig.getString("messages.noPermission"));
        invalidArguments = translateColors(MineMe.messagesConfig.getString("messages.invalidArguments"));
        MineMe.getInstance().debug("Messages loaded!");
        MineMe.getInstance().debug();
    }

    public static String translateTagsAndColors(String get, Mine m) {
        get = get.replaceAll("%mine%", m.getName());
        get = get.replaceAll("%prefix%", pluginPrefix);
        get = get.replaceAll("%sepatator%", messageSeparator);
        return translateColors(get);
    }

    public static String translateTagsAndColor(String get) {
        get = get.replaceAll("%prefix%", pluginPrefix);
        get = get.replaceAll("%sepatator%", messageSeparator);
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
                    MineMe.getInstance().debug("Message \"" + trans + "\" is badly color coded! Remeber to only use $1 to $4 !");
                }
            }
        }
        return StringUtils.optimizeColors(ChatColor.translateAlternateColorCodes(colorChar, new String(b)));
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
}
