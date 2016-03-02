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
package me.ddevil.mineme.conversion;

import com.koletar.jj.mineresetlite.MineResetLite;
import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.mines.Mine;
import me.ddevil.mineme.mines.MineManager;
import me.ddevil.mineme.mines.configs.MineConfig;
import me.ddevil.mineme.mines.impl.CuboidMine;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Selma
 */
public class MRLConverter {

    public static List<Mine> convert() {
        ArrayList<Mine> mines = new ArrayList<>();
        for (com.koletar.jj.mineresetlite.Mine m : MineResetLite.instance.mines) {
            MineMe.instance.debug("Converting mine " + m.getName() + "...", true);
            CuboidMine cm = new CuboidMine(new MineConfig(createBasicConfig(m)));
            MineManager.registerMine(cm);
            mines.add(cm);
            MineMe.instance.debug("Converted!", true);
        }
        MineMe.convertMineResetLite = false;
        MineMe.pluginConfig.set("global.convertFromMineResetLite", false);
        MineMe.instance.saveConfig();
        return mines;
    }

    public static FileConfiguration createBasicConfig(com.koletar.jj.mineresetlite.Mine mine) {
        MineMe.getInstance().saveResource("examplemine.yml", true);
        File template = new File(MineMe.pluginFolder, "examplemine.yml");
        try {
            Files.move(template.toPath(), new File(MineMe.minesFolder, mine.getName() + ".yml").toPath(), (CopyOption) null);
        } catch (IOException ex) {
            Logger.getLogger(MRLConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
        FileConfiguration c = YamlConfiguration.loadConfiguration(template);
        c.set("enabled", true);
        c.set("name", mine.getName());
        c.set("alias", mine.getName());
        c.set("world", "");
        c.set("type", "CUBOID");
        c.set("resetDelay", mine.getResetDelay());
        c.set("broadcastOnReset", true);
        c.set("broadcastToNearbyOnly", false);
        c.set("broadcastRadius", 50.0);
        ArrayList<String> comp = new ArrayList();
        comp.add("STONE=100");
        c.set("composition", comp);
        c.set("X1", mine.getMinX());
        c.set("Y1", mine.getMinY());
        c.set("Z1", mine.getMinZ());
        c.set("X2", mine.getMaxX());
        c.set("Y2", mine.getMaxY());
        c.set("Z2", mine.getMaxZ());
        return c;
    }
}
