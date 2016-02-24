package me.ddevil.mineme.mines;

import me.ddevil.mineme.utils.RandomCollection;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class MineRepopulator {

    public void repopulate(Mine m) {
        RepopulateMap map = new RepopulateMap(m);
        for (Block b : m.getBlocks()) {
            b.setType(map.getRandomBlock());
        }
    }

    private static class RepopulateMap {

        private final RandomCollection<Material> randomCollection;

        private RepopulateMap(Mine m) {
            randomCollection = new RandomCollection<>();
            for (Material m1 : m.getComposition().keySet()) {
                randomCollection.add(m.getComposition().get(m1), m1);
            }

        }

        private Material getRandomBlock() {
            return randomCollection.next();
        }
    }
}
