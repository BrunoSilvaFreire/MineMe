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
package me.ddevil.core.utils;

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
        ChatColor lastColor = null;
        for (int i = 0; i < msg.length(); i++) {
            char c = msg.charAt(i);
            if (c == '§') {
                ChatColor cor = ChatColor.getByChar(msg.charAt(i + 1));
                if (cor != null) {
                    if (cor.isColor()) {
                        if (lastColor == null) {
                            lastColor = cor;
                            continue;
                        }
                        if (cor == lastColor) {
                            msg = new StringBuffer(msg).replace(i, i + 2, "").toString();
                            i -= 2;
                        } else {
                            lastColor = cor;
                        }
                    }
                }
            }
        }
        return msg;
    }

}
