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
import me.ddevil.mineme.challenge.Challenge;
import me.ddevil.mineme.mines.Mine;
import org.bukkit.event.Cancellable;

/**
 *
 * @author Selma
 */
public class ChallengeStartEvent extends CustomEvent implements Cancellable {

    public final Mine mine;
    public final Challenge challenge;
    private boolean cancelled;

    public ChallengeStartEvent(Mine mine, Challenge challenge) {
        this.mine = mine;
        this.challenge = challenge;
    }

    public Challenge getChallenge() {
        return challenge;
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
