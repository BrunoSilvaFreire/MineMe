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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class CustomCommand extends Command {

    protected String[] usageMessages;

    public CustomCommand(String name, String permission) {
        super(name);
        CustomPlugin.registerPermission(permission);
        this.permission = permission;
    }

    public CustomCommand(String name, String permission, List<String> aliases) {
        super(name);
        CustomPlugin.registerPermission(permission);
        setAliases(aliases);
        this.permission = permission;
    }

    public CustomCommand(String name, String permission, List<String> aliases, String description) {
        super(name);
        CustomPlugin.registerPermission(permission);
        setAliases(aliases);
        setDescription(description);
        this.permission = permission;
    }

    public CustomCommand(String name, String permission, List<String> aliases, String description, String usage) {
        super(name);
        CustomPlugin.registerPermission(permission);
        setAliases(aliases);
        setDescription(description);
        setUsage(usage);
        this.permission = permission;
    }

    public String permission;

    @Override
    public boolean execute(CommandSender cs, String string, String[] strings) {
        return handleExecute(cs, strings);
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
        return p.hasPermission(permission);
    }

    public void sendUsage(Player p) {
        MineMe.sendMessage(p, usageMessages);
    }

    public abstract boolean handleExecute(CommandSender sender, String[] args);
}
