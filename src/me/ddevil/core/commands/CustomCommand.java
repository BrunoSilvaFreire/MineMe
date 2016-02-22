package me.ddevil.core.commands;

import java.util.List;
import me.ddevil.core.CustomPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class CustomCommand extends Command {

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

    public abstract boolean handleExecute(CommandSender sender, String[] args);
}
