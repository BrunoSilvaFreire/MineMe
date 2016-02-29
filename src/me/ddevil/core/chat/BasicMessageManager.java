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
package me.ddevil.core.chat;

import java.util.List;
import org.bukkit.entity.Player;

/**
 *
 * @author Selma
 */
public abstract class BasicMessageManager implements MessageManager {

    //Global Messages
    public static String pluginPrefix;
    public static String messageSeparator;

    @Override
    public void sendMessage(Player p, String string) {
        p.sendMessage(pluginPrefix + messageSeparator + string);
    }

    @Override
    public void sendMessage(Player p, String[] messages) {
        for (String usageMessage : messages) {
            sendMessage(p, usageMessage);
        }
    }

    @Override
    public void sendMessage(Player p, List<String> messages) {
        for (String usageMessage : messages) {
            sendMessage(p, usageMessage);
        }
    }
}
