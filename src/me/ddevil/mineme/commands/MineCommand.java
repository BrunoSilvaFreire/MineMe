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
package me.ddevil.mineme.commands;

import java.util.Arrays;
import java.util.HashMap;
import me.ddevil.core.commands.CustomCommand;
import me.ddevil.core.commands.SubCommand;
import me.ddevil.mineme.MineMeMessageManager;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.mines.MineManager;
import me.ddevil.mineme.mines.impl.CuboidMine;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MineCommand extends CustomCommand {

    public class EditCommand extends SubCommand {

        public EditCommand(MineCommand minecmd) {
            super("edit", minecmd, Arrays.asList(new String[]{}));
            usageMessages = MineMeMessageManager.translateTagsAndColors(new String[]{
                "$4 () = Obligatory $1/$4 [] = optional",
                "$1/mineme $2edit (name) add (material) (number from 0 to 1) $3s",
                "$2Example: $1/mineme edit examplemine add COBBLESTONE 0.3 $4Sets 30% cobble.",
                "$1/mineme $2edit (name) ()$3Displays infos about the specified mine",
                "$1/mineme $2edit (name) ()$3Displays infos about the specified mine",});
        }

        @Override
        public boolean handleExecute(CommandSender sender, String[] args) {
            if (sender instanceof Player) {
                Player p = (Player) sender;

            } else {
                sender.sendMessage("You can only use this command ingame");
            }
            return true;
        }
    }

    public MineCommand() {
        super("mineme", "mine.admin", Arrays.asList(new String[]{"mrl", "mm", "mine", "mines"}), "Command to manage MineMe mines");
        usageMessages = MineMeMessageManager.translateTagsAndColors(new String[]{
            "$2Others cool aliases: $1mrl, mm, mine, mines",
            "$4 () = Obligatory $1/$4 [] = optional",
            "$1/mineme $2create (name) [broadcast message] [nearbyBroadcast] [broadcastRadius] $3Creates a new mine full of stone :D",
            "$1/mineme $2delete (name) $3Deletes the specified mine",
            "$1/mineme $2info (name) $3Displays infos about the specified mine",
            "$1/mineme $2edit (name) ()$3Displays infos about the specified mine",
            "$1/mineme $2list $3List all the loaded mines.",
            "$1/mineme $2help $3Shows this.",
            "$1/mineme $2reload $3Reloads the config. :)",
            "$4NEVER USE /RELOAD (Sincerely, every Minecraft Developer ever)"

        });
    }

    @Override
    public boolean handleExecute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length > 0) {
                String func = args[0];
                if (func.equals("create")) {
                    //create
                    if (args.length > 1) {
                        String mineName = args[1];
                        Location loc1 = MineMe.WEP.getSelection(p).getMaximumPoint();
                        Location loc2 = MineMe.WEP.getSelection(p).getMinimumPoint();
                        if (loc1 == null) {
                            MineMe.messageManager.sendMessage(p, "You don't have a position 1 selected in WorldEdit!");
                            return true;
                        }
                        if (loc2 == null) {
                            MineMe.messageManager.sendMessage(p, "You don't have a position 2 selected in WorldEdit!");
                            return true;
                        }
                        int delay;
                        if (args.length > 2) {
                            delay = Integer.valueOf(args[2]);
                        } else {
                            delay = 5;
                        }
                        boolean broadcast;
                        if (args.length > 3) {
                            broadcast = Boolean.valueOf(args[3]);
                        } else {
                            broadcast = true;
                        }
                        boolean nearby;
                        if (args.length > 4) {
                            nearby = Boolean.valueOf(args[4]);
                        } else {
                            nearby = true;
                        }
                        double radius;
                        if (args.length > 5) {
                            try {
                                radius = Double.valueOf(args[5]);
                            } catch (Exception e) {
                                radius = 50;
                            }
                        } else {
                            radius = 50;
                        }
                        createNewMine(p, mineName, loc1, loc2, delay, broadcast, nearby, radius);
                    } else {
                        MineMe.messageManager.sendMessage(p, new String[]{
                            MineMeMessageManager.invalidArguments,
                            "You need to specify a name!"
                        });
                    }
                } else if (func.equals("delete")) {
                    //delete
                } else if (func.equals("info")) {
                    if (args.length > 1) {
                        String name = args[1];
                        Mine m = MineManager.getMine(name);
                        if (m != null) {
                            MineManager.sendInfo(p, m);
                        } else {
                            MineMe.messageManager.sendMessage(p, "Could not find mine " + name + ".");
                            listMines(p);
                        }
                    } else {
                        MineMe.messageManager.sendMessage(p, new String[]{
                            MineMeMessageManager.invalidArguments,
                            "You need to specify a name!"
                        });
                    }
                } else if (func.equals("reload")) {
                    //reload
                    MineMe.getInstance().reload(p);
                } else if (func.equals("list")) {
                    //list
                    listMines(p);
                } else if (func.equals("help")) {
                    //lies
                    MineMe.messageManager.sendMessage(p, MineMeMessageManager.translateTagsAndColor("$4The help is a lie! $1Use /mineme"));
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

    private void createNewMine(Player p, String name, Location loc1, Location loc2, Integer delay, boolean broadcastMessage, boolean nearby, double radius) {
        if (!checkPerm(p)) {
            MineMe.messageManager.sendMessage(p, MineMeMessageManager.noPermission);
            return;
        }
        HashMap<Material, Double> map = new HashMap<>();
        map.put(Material.STONE, 100d);
        Mine m = new CuboidMine(name, loc1, loc2, map, delay, broadcastMessage, nearby, radius);
        m.reset();
        MineManager.registerMine(m);
        MineMe.messageManager.sendMessage(p, MineMeMessageManager.translateTagsAndColors(MineMeMessageManager.mineCreateMessage, m));
    }

    private void listMines(Player p) {
        String s = "";
        for (Mine m : MineManager.getMines()) {
            s = s.concat(MineMeMessageManager.translateTagsAndColors(m.getName(), m));
            if (MineManager.getMines().indexOf(m) != MineManager.getMines().size() - 1) {
                s = s.concat("§f, $1");
            }
        }
        MineMe.messageManager.sendMessage(p, MineMeMessageManager.translateTagsAndColor("%prefix% $1Available mines: " + s));
    }

}
