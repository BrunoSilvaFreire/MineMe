package me.ddevil.mineme.commands;

import java.util.Arrays;
import java.util.HashMap;
import me.ddevil.core.commands.CustomCommand;
import me.ddevil.mineme.MessageManager;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.mines.MineManager;
import me.ddevil.mineme.mines.impl.CuboidMine;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MineCommand extends CustomCommand {

    private static final String[] usageMessages = {
        "§a/mineme §ecreate (name) §7Creates a new mine full of stone :D"
    };

    public MineCommand() {
        super("mineme", "mine.admin", Arrays.asList(new String[]{}), "Command to manage MineMe mines");
    }

    @Override
    public boolean handleExecute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            p.getServer().getVersion();
            if (args.length > 0) {
                switch (args[0]) {
                    case "create":
                        if (args.length > 1) {
                            String mineName = args[0];
                            Location loc1 = MineMe.WEP.getSelection(p).getMaximumPoint();
                            Location loc2 = MineMe.WEP.getSelection(p).getMinimumPoint();
                            if (loc1 == null) {
                                MineMe.sendMessage(p, "You don't have a location 1 selected in WorldEdit!");
                                return true;
                            }
                            if (loc2 == null) {
                                MineMe.sendMessage(p, "You don't have a location 2 selected in WorldEdit!");
                                return true;
                            }

                            createNewMine(p, mineName, loc1, loc2);
                        } else {
                            MineMe.sendMessage(p, new String[]{
                                MessageManager.invalidArguments,
                                "You need to specify a name!"
                            });
                        }
                    case "delete":
                    case "help":
                        sendUsage(p);
                    default:
                        sendUsage(p);
                }
            } else {
                MineMe.sendMessage(p, usageMessages);
            }
        } else {
            sender.sendMessage("You can only use this command ingame");
        }
        return true;
    }

    private void createNewMine(Player p, String name, Location loc1, Location loc2) {
        if (!checkPerm(p)) {
            MineMe.sendMessage(p, MessageManager.noPermission);
            return;
        }

        HashMap<Material, Double> map = new HashMap<>();
        map.put(Material.STONE, 100d);
        Mine m = new CuboidMine(name, loc1, loc2, map);
        m.reset();
        MineManager.registerMine(m);
    }

}
