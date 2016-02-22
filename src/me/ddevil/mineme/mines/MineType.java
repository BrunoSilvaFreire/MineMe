/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ddevil.mineme.mines;

import me.ddevil.mineme.mines.impl.Cuboid;
import me.ddevil.mineme.mines.impl.CuboidMine;

/**
 *
 * @author Selma
 */
public enum MineType {

    CUBOID(CuboidMine.class),
    CIRCULAR(null),
    CUSTOM(null);

    private final Class<? extends Mine> mineClass;

    private MineType(Class<? extends Mine> mineClass) {
        this.mineClass = mineClass;
    }

    public Class<? extends Mine> getMineClass() {
        return mineClass;
    }

}
