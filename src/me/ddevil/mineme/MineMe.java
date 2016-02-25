package me.ddevil.mineme;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.ddevil.core.CustomPlugin;
import me.ddevil.mineme.commands.MineCommand;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.mines.MineManager;
import me.ddevil.mineme.mines.impl.CuboidMine;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class MineMe extends CustomPlugin {

    public static FileConfiguration pluginConfig;
    public static FileConfiguration messagesConfig;
    public static File pluginFolder;
    public static File minesFolder;
    public static WorldEditPlugin WEP;

    public static MineMe getInstance() {
        return (MineMe) instance;
    }

    public static void sendMessage(Player p, String string) {
        p.sendMessage(string);
    }

    public static void sendMessage(Player p, String[] messages) {
        for (String usageMessage : messages) {
            sendMessage(p, usageMessage);
        }
    }
    private int resetId;

    @Override
    public void onEnable() {
        super.onEnable();
        setupConfig();
        MessageManager.setup();
        //Try to get dependencies
        if (getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
            WEP = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
        }
        if (WEP == null) {

            return;
        }
        if (getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
            enableHolograms();
            setForceHologramsUse(pluginConfig.getBoolean("global.forceHologramOnAllMine"));
        }
        //Get mines folder
        minesFolder = new File(getDataFolder(), "mines");
        if (!minesFolder.exists()) {
            minesFolder.mkdir();
        }
        if (minesFolder.listFiles().length == 0) {
            saveResource("examplemine.yml", false);
            File f = new File(getDataFolder() + "/examplemine.yml");
            try {
                FileUtils.moveFileToDirectory(f, minesFolder, false);
            } catch (IOException ex) {
                f.delete();
                debug("There was a problem trying to copy examplemine.yml to the mines folder. Skipping.");
            }
        }
        //load mines
        debug("Loading mines");
        File[] mineFiles = minesFolder.listFiles();
        int i = 0;
        for (File file : mineFiles) {
            String filename = file.getName();

            String extension = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
            if (!"yml".equals(extension)) {
                debug(filename + " isn't a .yml file! Skipping.");
                continue;
            }
            FileConfiguration mine = YamlConfiguration.loadConfiguration(file);
            //Get name
            String name = mine.getString("name");
            if (!mine.getBoolean("enabled")) {
                debug("Mine " + name + " is disabled, skipping.");
                continue;
            }
            //Load mine
            try {
                //Get world
                World w = Bukkit.getWorld(mine.getString("world"));

                //Get Composition
                HashMap<Material, Double> comp = new HashMap();
                for (String s : mine.getStringList("composition")) {
                    String[] split = s.split("=");
                    try {
                        comp.put(Material.valueOf(split[0]), Double.valueOf(split[1]));
                    } catch (NumberFormatException e) {
                        debug(split[1] + " in " + s + "isn't a number!");
                        debug("Skipping mine " + name);
                    }

                }

                //Instanciate
                Mine m = new CuboidMine(
                        name,
                        new Location(w,
                                mine.getDouble("X1"),
                                mine.getDouble("Y1"),
                                mine.getDouble("Z1")),
                        new Location(w,
                                mine.getDouble("X2"),
                                mine.getDouble("Y2"),
                                mine.getDouble("Z2")),
                        comp,
                        mine.getBoolean("broadcastOnReset")
                );
                m.reset();
                MineManager.registerMine(m);
                debug("Loaded mine " + m.getName() + ".");
                i++;
            } catch (Throwable t) {
                debug("Something went wrong while loading " + file.getName() + " :(");
                debug("--== Error ==--");
                t.printStackTrace();
                debug("--== Error ==--");
            }
            debug("Loaded  " + i + " mines :D");
        }
        long minute = 60 * 20L;
        //Register commands
        registerBaseCommands();
        //Start timer
        resetId = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for (Mine mine : MineManager.getMines()) {
                    mine.tictoc();
                }
            }
        }, minute, minute);
        debug("Plugin loaded!");
        debug("It's all right, it's all favorable :D");
    }

    public void debug(String[] msg) {
        for (String m : msg) {
            debug(m);
        }
    }

    public void debug(String msg) {
        getLogger().info(msg);
    }

    public void debug() {
        getLogger().info("");
    }

    public static FileConfiguration getYAMLMineFile(Mine m) {
        return YamlConfiguration.loadConfiguration(getMineFile(m));
    }

    public static File getMineFile(Mine m) {
        return new File(pluginFolder.getPath() + "/" + m.getName() + ".yml");

    }

    private void setupConfig() {
        pluginFolder = getDataFolder();
        pluginConfig = loadConfig();
        if (!pluginFolder.exists()) {
            pluginFolder.mkdir();
        }
        minesFolder = new File(pluginFolder.getPath() + "/mines");
        if (!pluginFolder.exists()) {
            pluginFolder.mkdir();
        }
        File messages = new File(getDataFolder() + "/messages.yml");
        if (!messages.exists()) {
            //Load from plugin
            saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messages);
    }

    public static boolean hologramsUsable = false;
    public static boolean forceHologramsUse = false;

    public static void enableHolograms() {
        hologramsUsable = true;
    }

    public static void setHologramsUsable(boolean hologramsUsable) {
        MineMe.hologramsUsable = hologramsUsable;
    }

    public static void setForceHologramsUse(boolean forceHologramsUse) {
        MineMe.forceHologramsUse = forceHologramsUse;
    }

    public static boolean isForceHologramsUse() {
        return forceHologramsUse;
    }

    private void registerBaseCommands() {
        registerCommand(new MineCommand());
    }

    public void reload(Player p) {
        sendMessage(p, "Reloading config...");
        File messages = new File(getDataFolder() + "/messages.yml");
        if (!messages.exists()) {
            //Load from plugin
            saveResource("messages.yml", false);
        }
        File config = new File(getDataFolder() + "/config.yml");
        if (!config.exists()) {
            //Load from plugin
            saveResource("config.yml", false);
        }

        try {
            pluginConfig.save(config);
            messagesConfig.save(messages);
        } catch (IOException ex) {
            Logger.getLogger(MineMe.class.getName()).log(Level.SEVERE, null, ex);
        }
        pluginConfig = loadConfig();
        MessageManager.setup();
        sendMessage(p, "Reloaded! :D");
    }
}
