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

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.CylinderSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import java.util.Arrays;
import me.ddevil.core.commands.CustomCommand;
import me.ddevil.core.commands.SubCommand;
import me.ddevil.mineme.messages.MineMeMessageManager;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.gui.GUIManager;
import me.ddevil.mineme.messages.MessageColor;
import me.ddevil.mineme.mines.HologramCompatible;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.mines.MineManager;
import me.ddevil.mineme.mines.impl.CircularMine;
import me.ddevil.mineme.mines.impl.CuboidMine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MineCommand extends CustomCommand {

    private final EditCommand editCommand;

    public class EditCommand extends SubCommand {

        public EditCommand(MineCommand minecmd) {
            super("edit", minecmd, Arrays.asList(new String[]{}));
            usageMessages = MineMeMessageManager.translateTagsAndColors(new String[]{
                "&r",
                "%header%",
                MessageColor.ERROR + " () = Obligatory " + MessageColor.PRIMARY + "/" + MessageColor.ERROR + " [] = optional",
                MessageColor.PRIMARY + "/mineme " + MessageColor.SECONDARY + "edit (name) add (material) (number from 0 to 100) " + MessageColor.NEUTRAL + "Add's this material to the mines composition.",
                MessageColor.PRIMARY + "/mineme " + MessageColor.SECONDARY + "edit (name) remove (material) " + MessageColor.NEUTRAL + "Removes this material to the mines composition.",
                MessageColor.PRIMARY + "/mineme " + MessageColor.SECONDARY + "edit (name) set (alias|resetdelay|nearbybroadcast|broadcastRange) (value) " + MessageColor.NEUTRAL + "Set's the selected parameter to the said value.",
                "%header%"});
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
                                    String[] split = mname.split(":");
                                    try {
                                        Material material = Material.valueOf(split[0]);
                                        if (material.isBlock()) {
                                            if (args.length > 4) {
                                                String stringpercent = args[4].replace("%", "");
                                                try {
                                                    double percent = Double.valueOf(stringpercent);
                                                    byte b;
                                                    if (split.length > 1) {
                                                        String bs = split[1];
                                                        try {
                                                            b = Byte.valueOf(bs);
                                                        } catch (NumberFormatException e) {
                                                            sendInvalidArguments(p, MessageColor.ERROR + bs + MessageColor.NEUTRAL + " isn't a valid number!");
                                                            return true;
                                                        }
                                                    } else {
                                                        b = 0;
                                                    }
                                                    mine.setMaterial(new ItemStack(material, 1, (short) 0, b), percent);
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
                            } else if (toDo.equalsIgnoreCase("set")) {
                                //Removes block from composition
                                //edit (name) set (alias|resetdelay|nearbybroadcast|broadcastRange) (value)
                                if (args.length > 3) {
                                    String param = args[3].toUpperCase();
                                    if (param.equalsIgnoreCase("alias")) {
                                        if (args.length > 4) {
                                            String alias = "";
                                            for (int i = 4; i < args.length; i++) {
                                                alias += " " + args[i];
                                            }
                                            mine.setAlias(alias);
                                            MineMe.messageManager.sendMessage(p, MessageColor.PRIMARY + mine.getName() + MessageColor.SECONDARY + "'s alias was set to " + MessageColor.PRIMARY + alias + MessageColor.PRIMARY + "! :)");
                                        }
                                    } else if (param.equalsIgnoreCase("resetdelay")) {
                                        if (args.length > 4) {
                                            String stringdelay = args[4];
                                            try {
                                                int minutes = Integer.valueOf(stringdelay);
                                                mine.setResetDelay(minutes);
                                                MineMe.messageManager.sendMessage(p, MessageColor.PRIMARY + mine.getName() + MessageColor.SECONDARY + "'s reset delay was set to " + MessageColor.PRIMARY + minutes + MessageColor.PRIMARY + "! :)");
                                            } catch (NumberFormatException e) {
                                                sendInvalidArguments(p, MessageColor.PRIMARY + stringdelay + MessageColor.NEUTRAL + " isn't a number!");
                                            }
                                        }
                                    } else if (param.equalsIgnoreCase("nearbybroadcast")) {
                                        if (args.length > 4) {
                                            String stringboolean = args[4];
                                            try {
                                                boolean bol = Boolean.valueOf(stringboolean);
                                                mine.setBroadcastOnReset(bol);
                                                MineMe.messageManager.sendMessage(p, MessageColor.PRIMARY + mine.getName() + MessageColor.SECONDARY + "'s nearbyBroadcast was set to " + MessageColor.PRIMARY + bol + MessageColor.PRIMARY + "! :)");
                                            } catch (Exception e) {
                                                sendInvalidArguments(p, MessageColor.PRIMARY + stringboolean + MessageColor.NEUTRAL + " isn't a boolean (true/false)!");
                                            }
                                        }
                                    } else if (param.equalsIgnoreCase("broadcastRange")) {
                                        if (args.length > 4) {
                                            String stringdouble = args[4];
                                            try {
                                                double range = Double.valueOf(stringdouble);
                                                mine.setBroadcastRange(range);
                                                MineMe.messageManager.sendMessage(p, MessageColor.PRIMARY + mine.getName() + MessageColor.SECONDARY + "'s broadcast range was set to " + MessageColor.PRIMARY + range + MessageColor.PRIMARY + "! :)");
                                            } catch (Exception e) {
                                                sendInvalidArguments(p, MessageColor.PRIMARY + stringdouble + MessageColor.NEUTRAL + " isn't a boolean (true/false)!");
                                            }
                                        }
                                    } else {
                                        sendInvalidArguments(p, MessageColor.NEUTRAL + "Please give us a alias to set to the mine!");
                                    }
                                } else {
                                    sendInvalidArguments(p, MessageColor.NEUTRAL + "Please give us a parameter to change!");
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
                    GUIManager.mineEditorGUI.open(p);
                }
            } else {
                sender.sendMessage("You can only use this command ingame");
            }
            return true;
        }

        @Override
        public void sendUsage(Player p) {
            for (String msg : usageMessages) {
                p.sendMessage(MineMeMessageManager.translateColors(msg));
            }
        }
    }

    public MineCommand() {
        super("mineme", "mine.admin", Arrays.asList(new String[]{"mrl", "mm", "mine", "mines"}), "Command to manage MineMe mines");
        editCommand = new EditCommand(this);
        addSubCommand(editCommand);
        usageMessages = MineMeMessageManager.translateTagsAndColors(new String[]{
            "&r",
            "%header%",
            MessageColor.SECONDARY + "Others cool aliases: " + MessageColor.PRIMARY + "mrl, mm, mine, mines",
            MessageColor.ERROR + " () = Obligatory " + MessageColor.PRIMARY + "/" + MessageColor.ERROR + " [] = optional",
            MessageColor.PRIMARY + "/mineme " + MessageColor.SECONDARY + "create (name) [broadcast message] [nearbyBroadcast] [broadcastRadius] " + MessageColor.NEUTRAL + "Creates a new mine full of stone :D",
            MessageColor.PRIMARY + "/mineme " + MessageColor.SECONDARY + "delete (name) " + MessageColor.NEUTRAL + "Deletes the specified mine",
            MessageColor.PRIMARY + "/mineme " + MessageColor.SECONDARY + "info (name) " + MessageColor.NEUTRAL + "Displays infos about the specified mine",
            MessageColor.PRIMARY + "/mineme " + MessageColor.SECONDARY + "edit (name)" + MessageColor.NEUTRAL + "Edits the mine",
            MessageColor.PRIMARY + "/mineme " + MessageColor.SECONDARY + "list " + MessageColor.NEUTRAL + "List all the loaded mines.",
            MessageColor.PRIMARY + "/mineme " + MessageColor.SECONDARY + "reset (name|all) " + MessageColor.NEUTRAL + "Reset the mine. (If you put all as the name, will reset all the mines)",
            MessageColor.PRIMARY + "/mineme " + MessageColor.SECONDARY + "help " + MessageColor.NEUTRAL + "Shows this.",
            MessageColor.PRIMARY + "/mineme " + MessageColor.SECONDARY + "gui " + MessageColor.NEUTRAL + "Open's MEGUI (MineEditorGUI).",
            MessageColor.PRIMARY + "/mineme " + MessageColor.SECONDARY + "reload " + MessageColor.NEUTRAL + "Reloads the config. :)",
            MessageColor.ERROR + "NEVER USE /RELOAD (Sincerely, every Minecraft Developer ever)",
            "%header%"}
        );
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
                        if (!MineManager.isNameAvailable(mineName)) {
                            sendInvalidArguments(p, "There is already a mine named " + MessageColor.PRIMARY + mineName + MessageColor.NEUTRAL + "! Please select another one.");
                            return true;
                        }
                        Selection sel = MineMe.WEP.getSelection(p);
                        if (sel == null) {
                            MineMe.messageManager.sendMessage(p, "You don't have a selection in WorldEdit! Type //wand and select 2 points");
                            return true;
                        }
                        Location loc1 = sel.getMaximumPoint();
                        Location loc2 = sel.getMinimumPoint();
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
                        createNewMine(p, mineName, sel);
                    } else {
                        MineMe.messageManager.sendMessage(p, new String[]{
                            MineMeMessageManager.invalidArguments,
                            "You need to specify a name!"
                        });
                    }
                } else if (func.equalsIgnoreCase("delete")) {
                    if (args.length > 1) {
                        String name = args[1];
                        Mine m = MineManager.getMine(name);
                        if (m != null) {
                            m.delete();
                            MineMe.messageManager.sendMessage(p, "Mine " + MessageColor.PRIMARY + m.getName() + MessageColor.NEUTRAL + " was deleted! :D");
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
                } else if (func.equalsIgnoreCase("reset")) {
                    if (args.length > 1) {
                        String name = args[1];
                        Mine m = MineManager.getMine(name);
                        if (m != null) {
                            m.reset();
                            MineMe.messageManager.sendMessage(p, "Mine " + MessageColor.PRIMARY + m.getName() + MessageColor.NEUTRAL + " was reseted! :D");
                        } else if (name.equalsIgnoreCase("all")) {
                            for (Mine mb : MineManager.getMines()) {
                                mb.reset();
                            }
                            MineMe.messageManager.sendMessage(p, "Reseted all mines!");

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
                } else if (func.equalsIgnoreCase("gui") || func.equalsIgnoreCase("megui")) {
                    //MineEditorGUI
                    GUIManager.mineEditorGUI.open(p);
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

    private void createNewMine(Player p, String name, Selection selection) {
        if (!checkPerm(p)) {
            MineMe.messageManager.sendMessage(p, MineMeMessageManager.noPermission);
            return;
        }
        Mine m = null;
        MineMe.messageManager.sendMessage(p, "$2WorldEdit selection type: $4" + selection.getClass().getSimpleName());
        if (selection instanceof CylinderSelection) {
            CylinderSelection cs = (CylinderSelection) selection;
            Location center = new Location(p.getWorld(), cs.getCenter().getX(), cs.getMinimumPoint().getBlockY(), cs.getCenter().getZ());
            int height = cs.getMaximumPoint().getBlockY() - cs.getMinimumPoint().getBlockY();
            m = new CircularMine(name, center, cs.getRadius().getX(), height);
        } else if (selection instanceof CuboidSelection) {
            CuboidSelection cuboidSelection = (CuboidSelection) selection;
            Location loc1 = cuboidSelection.getMinimumPoint();
            Location loc2 = cuboidSelection.getMaximumPoint();
            Location fl1 = loc1.clone();
            Location fl2 = loc2.clone();
            fl1.setX(Math.min(loc1.getBlockX(), loc2.getBlockX()));
            fl1.setY(Math.min(loc1.getBlockY(), loc2.getBlockY()));
            fl1.setZ(Math.min(loc1.getBlockZ(), loc2.getBlockZ()));
            fl2.setX(Math.max(loc1.getBlockX(), loc2.getBlockX()));
            fl2.setY(Math.max(loc1.getBlockY(), loc2.getBlockY()));
            fl2.setZ(Math.max(loc1.getBlockZ(), loc2.getBlockZ()));
            m = new CuboidMine(name, fl1, fl2);
        }
        if (m == null) {
            MineMe.messageManager.sendMessage(p, "$4There was an error in mine creation! Check the console!");
            MineMe.instance.printException(name, new Error("Could not instancializate mine"));
            return;
        }
        for (Block m1 : m) {
            if (MineManager.isPartOfMine(m1)) {
                MineMe.messageManager.sendMessage(p, "It seems that there is already a mine here! Please try somewhere else :D");
                return;
            }
        }
        MineManager.registerMine(m);
        MineMe.messageManager.sendMessage(p, MineMeMessageManager.translateTagsAndColors(MineMeMessageManager.mineCreateMessage, m));
        m.save();
        if (MineMe.useHolograms) {
            if (m instanceof HologramCompatible) {
                HologramCompatible h = (HologramCompatible) m;
                h.setupHolograms();
            }
        }
        m.reset();
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

    @Override
    public void sendUsage(Player p) {
        for (String msg : usageMessages) {
            p.sendMessage(MineMeMessageManager.translateColors(msg));
        }
    }

}
