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
package me.ddevil.mineme.events;

import me.ddevil.core.events.CustomEvent;
import me.ddevil.mineme.mines.Mine;
import org.bukkit.event.Cancellable;

/**
 *
 * @author Selma
 */
public class MineResetEvent extends CustomEvent implements Cancellable {

    private final Mine mine;
    private boolean cancelled;

    public MineResetEvent(Mine mine) {
        this.mine = mine;
    }

    public Mine getMine() {
        return mine;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
