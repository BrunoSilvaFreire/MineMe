package me.ddevil.mineme;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import java.io.File;
import me.ddevil.core.CustomPlugin;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.mines.MineManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class MineMe extends CustomPlugin {
    
    public static FileConfiguration config;
    private static File pluginFolder;
    private static File minesFolder;
    private WorldEditPlugin worldEdit;
    
    @Override
    public void onEnable() {
        super.onEnable();
        setupConfig();
        if (getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
            worldEdit = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
        }
        debug("Starting to load mines");
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
        config = loadConfig();
        if (!pluginFolder.exists()) {
            pluginFolder.mkdir();
        }
        minesFolder = new File(pluginFolder.getPath() + "/mines");
        if (!pluginFolder.exists()) {
            pluginFolder.mkdir();
        }
    }
}
