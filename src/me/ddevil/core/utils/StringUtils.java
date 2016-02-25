package me.ddevil.core.utils;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

public class StringUtils {

    public static String arrayToMessage(String[] array) {
        return arrayToMessage(0, array);
    }

    public static String arrayToMessage(int start, String[] array) {
        String msg = "";
        for (int i = start; i < array.length; i++) {
            String m = array[i];
            msg = msg.concat(m);
            if (i != array.length - 1) {
                msg = msg.concat(" ");
            }
        }
        return msg;
    }

    public static String optimizeColors(String msg) {
        ChatColor ultimaCor = null;
        for (int i = 0; i < msg.length(); i++) {
            char c = msg.charAt(i);
            if (c == '§') {
                ChatColor cor = ChatColor.getByChar(msg.charAt(i + 1));
                if (cor != null) {
                    if (cor.isColor()) {
                        if (ultimaCor == null) {
                            ultimaCor = cor;
                            continue;
                        }
                        if (cor == ultimaCor) {
                            msg = new StringBuffer(msg).replace(i, i + 2, "").toString();
                            i -= 2;
                        } else {
                            ultimaCor = cor;
                        }
                    }
                }
            }
        }
        return msg;
    }

}
