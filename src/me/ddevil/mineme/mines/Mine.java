/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ddevil.mineme.mines;

import java.util.List;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 *
 * @author Selma
 */
public interface Mine extends Iterable<Block> {

    public abstract void delete();

    public abstract void reset();

    public abstract void save();

    public abstract String getName();

    public abstract Material[] getMaterials();

    public abstract boolean isBroadcastOnReset();

    public abstract void setBroadcastOnReset(boolean broadcastOnReset);

    public abstract MineType getType();

    public abstract boolean contains(int x, int y, int z);

    public abstract boolean contains(Block b);

    public abstract boolean contains(Location l);

    public abstract boolean contains(Player p);

    public abstract List<Block> getBlocks();

    public abstract Map<Material, Double> getComposition();

    public abstract void tictoc();
}
