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
import me.ddevil.mineme.messages.MineMeMessageManager;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.messages.MessageColor;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.mines.MineManager;
import me.ddevil.mineme.mines.impl.CuboidMine;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MineCommand extends CustomCommand {

    private final EditCommand editCommand;

    public class EditCommand extends SubCommand {

        public EditCommand(MineCommand minecmd) {
            super("edit", minecmd, Arrays.asList(new String[]{}));
            usageMessages = MineMeMessageManager.translateTagsAndColors(new String[]{
                MessageColor.ERROR + " () = Obligatory " + MessageColor.PRIMARY + "/" + MessageColor.ERROR + " [] = optional",
                MessageColor.PRIMARY + "/mineme " + MessageColor.SECONDARY + "edit (name) add (material) (number from 0 to 100) " + MessageColor.NEUTRAL + "Add's this material to the mines composition.",
                MessageColor.SECONDARY + "Example: " + MessageColor.SECONDARY + "/mineme edit examplemine add IRON_ORE 30 " + MessageColor.NEUTRAL + "Sets 30% iron ore.",
                MessageColor.PRIMARY + "/mineme " + MessageColor.SECONDARY + "edit (name) remove (material) " + MessageColor.NEUTRAL + "Removes this material to the mines composition."
            });
        }

        @Override
        public boolean handleExecute(CommandSender sender, String[] args) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (args.length > 1) {
                    String mineName = args[1];
                    Mine mine = MineManager.getMine(mineName);
                    if (mine != null) {
                        if (args.length > 2) {
                            String toDo = args[2];
                            if (toDo.equalsIgnoreCase("add")) {
                                //Add block to mine
                                if (args.length > 3) {
                                    String mname = args[3].toUpperCase();
                                    try {
                                        Material material = Material.valueOf(mname);
                                        if (material.isBlock()) {
                                            if (args.length > 4) {
                                                String stringpercent = args[4].replace("%", "");
                                                try {
                                                    double percent = Double.valueOf(stringpercent);
                                                    mine.setMaterial(material, percent);
                                                    mine.reset();
                                                    MineMe.messageManager.sendMessage(p, MessageColor.PRIMARY + material.name() + MessageColor.SECONDARY + " was set to " + MessageColor.PRIMARY + percent + MessageColor.SECONDARY + " in mine " + MessageColor.PRIMARY + mine.getName() + MessageColor.SECONDARY + " !");
                                                } catch (NumberFormatException e) {
                                                    sendInvalidArguments(p, MessageColor.ERROR + stringpercent + MessageColor.NEUTRAL + " isn't a number!");
                                                }
                                            } else {
                                                sendInvalidArguments(p, MessageColor.NEUTRAL + "Please give us a number!");
                                            }
                                        } else {
                                            sendInvalidArguments(p, MessageColor.ERROR + material.name() + MessageColor.NEUTRAL + " isn't a placeable block!");
                                        }
                                    } catch (Exception e) {
                                        sendInvalidArguments(p, MessageColor.ERROR + mname + MessageColor.NEUTRAL + " isn't a Material!");
                                    }
                                } else {
                                    sendInvalidArguments(p, MessageColor.NEUTRAL + "Please give us a material!");
                                }
                            } else if (toDo.equalsIgnoreCase("remove") || toDo.equalsIgnoreCase("delete")) {
                                //Removes block from composition
                                if (args.length > 3) {
                                    String mname = args[3].toUpperCase();
                                    try {
                                        Material material = Material.valueOf(mname);
                                        if (mine.containsMaterial(material)) {
                                            mine.removeMaterial(material);
                                            mine.reset();
                                            MineMe.messageManager.sendMessage(p, MessageColor.PRIMARY + material.name() + MessageColor.SECONDARY + " was removed from " + MessageColor.PRIMARY + mine.getName() + MessageColor.SECONDARY + " !");
                                        } else {
                                            sendInvalidArguments(p, MessageColor.NEUTRAL + "Mine " + MessageColor.ERROR + mine.getName() + MessageColor.NEUTRAL + " doesn't contain any " + MessageColor.ERROR + material.name() + MessageColor.NEUTRAL + "!");
                                        }
                                    } catch (Exception e) {
                                        sendInvalidArguments(p, MessageColor.ERROR + mname + MessageColor.NEUTRAL + " isn't a Material!");
                                    }
                                } else {
                                    sendInvalidArguments(p, MessageColor.NEUTRAL + "Please give us a material!");
                                }
                            } else {
                                sendUsage(p);
                            }
                        } else {
                            sendUsage(p);
                        }
                    } else {
                        sendInvalidArguments(p,
                                "The mine " + mineName + " doesn't exists!"
                        );
                    }
                } else {
                    sendUsage(p);
                }
            } else {
                sender.sendMessage("You can only use this command ingame");
            }
            return true;
        }
    }

    public MineCommand() {
        super("mineme", "mine.admin", Arrays.asList(new String[]{"mrl", "mm", "mine", "mines"}), "Command to manage MineMe mines");
        editCommand = new EditCommand(this);
        addSubCommand(editCommand);
        usageMessages = MineMeMessageManager.translateTagsAndColors(new String[]{
            MessageColor.SECONDARY + "Others cool aliases: " + MessageColor.PRIMARY + "mrl, mm, mine, mines",
            MessageColor.ERROR + " () = Obligatory " + MessageColor.PRIMARY + "/" + MessageColor.ERROR + " [] = optional",
            MessageColor.PRIMARY + "/mineme " + MessageColor.SECONDARY + "create (name) [broadcast message] [nearbyBroadcast] [broadcastRadius] " + MessageColor.NEUTRAL + "Creates a new mine full of stone :D",
            MessageColor.PRIMARY + "/mineme " + MessageColor.SECONDARY + "delete (name) " + MessageColor.NEUTRAL + "Deletes the specified mine",
            MessageColor.PRIMARY + "/mineme " + MessageColor.SECONDARY + "info (name) " + MessageColor.NEUTRAL + "Displays infos about the specified mine",
            MessageColor.PRIMARY + "/mineme " + MessageColor.SECONDARY + "edit (name) ()" + MessageColor.NEUTRAL + "Displays infos about the specified mine",
            MessageColor.PRIMARY + "/mineme " + MessageColor.SECONDARY + "list " + MessageColor.NEUTRAL + "List all the loaded mines.",
            MessageColor.PRIMARY + "/mineme " + MessageColor.SECONDARY + "help " + MessageColor.NEUTRAL + "Shows this.",
            MessageColor.PRIMARY + "/mineme " + MessageColor.SECONDARY + "reload " + MessageColor.NEUTRAL + "Reloads the config. :)",
            MessageColor.ERROR + "NEVER USE /RELOAD (Sincerely, every Minecraft Developer ever)"

        });
    }

    @Override
    public boolean handleExecute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length > 0) {
                String func = args[0];
                if (func.equalsIgnoreCase("create")) {
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
                } else if (func.equalsIgnoreCase("delete")) {
                    //delete
                } else if (func.equalsIgnoreCase("info")) {
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
                } else if (func.equalsIgnoreCase("reload")) {
                    //reload
                    MineMe.getInstance().reload(p);
                } else if (func.equalsIgnoreCase("list")) {
                    //list
                    listMines(p);
                } else if (func.equalsIgnoreCase("edit")) {
                    //edit mine
                    editCommand.handleExecute(p, args);
                } else if (func.equalsIgnoreCase("help")) {
                    //lies
                    MineMe.messageManager.sendMessage(p, MineMeMessageManager.translateTagsAndColor(MessageColor.ERROR + "The help is a lie! " + MessageColor.PRIMARY + "Use /mineme"));
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
                s = s.concat(MessageColor.SECONDARY + ", " + MessageColor.PRIMARY);
            }
        }
        MineMe.messageManager.sendMessage(p, MineMeMessageManager.translateTagsAndColor(MessageColor.SECONDARY + "Available mines: " + MessageColor.PRIMARY + "" + s));
    }

    @Override
    public void sendInvalidArguments(Player p, String msg) {
        MineMe.messageManager.sendMessage(p, new String[]{
            MineMeMessageManager.invalidArguments,
            msg
        });
    }

}
