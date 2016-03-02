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
package me.ddevil.core.commands;

import java.util.List;
import me.ddevil.core.CustomPlugin;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.messages.MineMeMessageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class SubCommand {

    protected String[] usageMessages;
    public final CustomCommand command;
    protected final String name;
    protected List<String> aliases;

    public SubCommand(String name, CustomCommand permission) {
        this.name = name;
        this.command = permission;
    }

    public SubCommand(String name, CustomCommand command, List<String> aliases) {
        this.aliases = aliases;
        this.command = command;
        this.name = name;
    }

    public String[] getUsageMessages() {
        return usageMessages;
    }

    public CustomCommand getCommand() {
        return command;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public String getName() {
        return name;
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }

    public boolean checkPerm(Player p) {
        return p.hasPermission(command.permission);
    }

    public void sendUsage(Player p) {
        CustomPlugin.messageManager.sendMessage(p, usageMessages);
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    public abstract boolean handleExecute(CommandSender sender, String[] args);
}
