package me.ddevil.mineme;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import java.io.File;
import me.ddevil.core.CustomPlugin;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.mines.MineManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class MineMe extends CustomPlugin {

    public static FileConfiguration pluginConfig;
    private static File pluginFolder;
    private static File minesFolder;

    public static void sendMessage(Player p, String string) {
        p.sendMessage(string);
    }

    public static void sendMessage(Player p, String[] usageMessages) {
        for (String usageMessage : usageMessages) {
            sendMessage(p, usageMessage);
        }
    }
    private WorldEditPlugin worldEdit;

    @Override
    public void onEnable() {
        super.onEnable();
        setupConfig();

        //Try to get dependencies
        if (getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
            worldEdit = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
        }
        if (getServer().getPluginManager().isPluginEnabled("HolographicDisplays")) {
            ConfigManager.enableHolograms();
            ConfigManager.setForceHologramsUse(pluginConfig.getBoolean("global.forceHologramOnAllMine"));
        }
        //load mines
        debug("Loading mines");
        File[] mineFiles = new File(getDataFolder(), "mines").listFiles();
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
                MineManager.registerMine(mine);
            } catch (Throwable t) {
                debug("Something went wrong while loading " + file.getName() + " :(");
            }
        }
    }

    public void debug(String msg) {
        getLogger().info(msg);
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
}
