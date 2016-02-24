package me.ddevil.mineme;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.ddevil.core.CustomPlugin;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.mines.MineManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class MineMe extends CustomPlugin {

    public static FileConfiguration pluginConfig;
    public static FileConfiguration messagesConfig;
    private static File pluginFolder;
    private static File minesFolder;
    public static WorldEditPlugin WEP;

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

        //load mines
        debug("Loading mines");
        minesFolder = new File(getDataFolder(), "mines");
        if (!minesFolder.exists()) {
            minesFolder.mkdir();
            Reader defConfigStream;
            try {
                defConfigStream = new InputStreamReader(this.getResource("examplemine.yml"), "UTF8");
                YamlConfiguration.loadConfiguration(defConfigStream);
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(MineMe.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        File[] mineFiles = minesFolder.listFiles();
        int i = 0;
        for (File file : mineFiles) {
            FileConfiguration fileConf = YamlConfiguration.loadConfiguration(file);
            try {
                Object o = fileConf.get("mine");
                if (!(o instanceof Mine)) {
                    debug("Hey man, take a look at " + file.getName() + ".");
                    debug("It isn't a Mine file!");
                    continue;
                }
                Mine mine = (Mine) o;
                mine.reset();
                MineManager.registerMine(mine);
                debug("Loaded mine " + mine.getName() + ".");
                i++;
            } catch (Throwable t) {
                debug("Something went wrong while loading " + file.getName() + " :(");
            }
            debug("Loaded  " + i + " mines :D");
        }
        long minute = 60 * 20L;

        //Start timer
        resetId = getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for (Mine mine : MineManager.getMines()) {
                    mine.tictoc();
                }
            }
        }, minute, minute);
    }

    public void debug(String msg) {
        getLogger().info(msg);
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

}
