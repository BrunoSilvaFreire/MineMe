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

    public MineCommand() {
        super("mineme", "mine.admin", Arrays.asList(new String[]{"mrl", "mm", "mine", "mines"}), "Command to manage MineMe mines");
        usageMessages = MessageManager.translateColors(new String[]{
            "%prefix%",
            "$2Others cool aliases: $1mrl, mm, mine, mines",
            "$1- /mineme $2create (name) $3Creates a new mine full of stone :D",
            "$1- /mineme $2delete (name) $3Deletes the specified mine",
            "$1- /mineme $2info (name) $3Deletes the specified mine",
            "$1- /mineme $2list $3List all the loaded mines.",
            "$1- /mineme $2help $3Shows this.",
            "$1- /mineme $2reload $3Reloads the config. :)",
            "$4NEVER USE /RELOAD (Sincerely, every Minecraft Developer ever)"

        });
    }

    @Override
    public boolean handleExecute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            p.getServer().getVersion();
            if (args.length > 0) {
                String func = args[0];
                if (func.equals("create")) {
                    //create
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
                } else if (func.equals("delete")) {
                    //delete
                } else if (func.equals("reload")) {
                    //reload
                    MineMe.getInstance().reload(p);
                } else if (func.equals("list")) {
                    //list
                    String s = "";
                    for (Mine m : MineManager.getMines()) {
                        s = s.concat(MessageManager.translateTagsAndColors(m.getName(), m));
                        if (MineManager.getMines().indexOf(m) != MineManager.getMines().size() - 1) {
                            s = s.concat("§f, $1");
                        }
                    }
                    MineMe.sendMessage(p, "%prefix% $1Available mines: " + s);
                } else if (func.equals("help")) {
                    MineMe.sendMessage(p, "If you thought this showed the help, sorry, I lied, use /mineme");
                } else {
                    //none
                    sendUsage(p);
                }

            } else {
                sendUsage(p);
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
        Mine m = new CuboidMine(name, loc1, loc2, map, true);
        m.reset();
        MineManager.registerMine(m);
    }

}
