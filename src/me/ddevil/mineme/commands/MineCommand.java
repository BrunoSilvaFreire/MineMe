package me.ddevil.mineme.commands;

import java.util.Arrays;
import java.util.List;
import me.ddevil.core.commands.CustomCommand;
import me.ddevil.mineme.MineMe;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MineCommand extends CustomCommand {

    public MineCommand() {
        super("mineme", "mine.admin", Arrays.asList(new String[]{}), "Command to manage MineMe mines");
    }

    @Override
    public boolean handleExecute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length > 0) {
                switch (args[0]) {
                }
            } else {
                MineMe.sendMessage(p, "");
            }
        } else {
            sender.sendMessage("You can only use this command ingame");
        }
        return true;
    }

}
