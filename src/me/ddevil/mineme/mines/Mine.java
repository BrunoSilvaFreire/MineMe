/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ddevil.mineme.mines;

import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 *
 * @author Selma
 */
public interface Mine extends Iterable<Block> {

    public abstract void delete();

    public abstract void reset();

    public abstract void save();

    public String getName();

    public abstract Material[] getMaterials();

    public abstract boolean isBroadcastOnReset();

    public abstract void setBroadcastOnReset(boolean broadcastOnReset);

}
