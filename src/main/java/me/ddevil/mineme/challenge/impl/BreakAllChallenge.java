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
package me.ddevil.mineme.challenge.impl;

import me.ddevil.core.events.CustomEvent;
import me.ddevil.mineme.MineMe;
import me.ddevil.mineme.challenge.BasicChallenge;
import me.ddevil.mineme.challenge.ChallengeEndListener;
import me.ddevil.mineme.events.ChallengeStartEvent;
import me.ddevil.mineme.mines.Mine;
import org.bukkit.Bukkit;

/**
 *
 * @author Selma
 */
public class BreakAllChallenge extends BasicChallenge {

    protected final Mine mine;
    protected final int timeLimit;
    protected int currentTime;
    protected int repeatID;

    public BreakAllChallenge(Mine mine, int timeLimit) {
        this.mine = mine;
        this.timeLimit = timeLimit;
        this.currentTime = timeLimit;
    }

    @Override
    public void start() {
        if (mine.isRunningAChallenge()) {

        }
        ChallengeStartEvent event = (ChallengeStartEvent) new ChallengeStartEvent(mine, this).call();
        if (!event.isCancelled()) {
            repeatID = Bukkit.getScheduler().scheduleSyncRepeatingTask(MineMe.instance, new Runnable() {

                @Override
                public void run() {
                    currentTime--;
                    checkCompletion();
                }
            }, 20l, 20l);
        }
    }

    @Override
    public void checkCompletion() {
        if (mine.isCompletelyBroken()) {
            Bukkit.getScheduler().cancelTask(repeatID);
            complete(ChallengeEndListener.ChallengeResult.COMPLETED);
        } else if (currentTime == 0) {
            complete(ChallengeEndListener.ChallengeResult.FAILED);
        }
    }

}
