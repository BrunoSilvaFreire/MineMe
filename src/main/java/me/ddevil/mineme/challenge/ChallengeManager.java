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
package me.ddevil.mineme.challenge;

import me.ddevil.mineme.mines.Mine;

/**
 *
 * @author Selma
 */
public class ChallengeManager {

    public static void setChallenge(final Mine m, final Challenge challenge) {
        if (m.isRunningAChallenge()) {
            m.getCurrentChallenge().addListener(new ChallengeEndListener() {
                @Override
                public void onComplete(ChallengeEndListener.ChallengeResult result) {
                    m.addChallengeToQueue(challenge);
                    challenge.start();
                }
            });
        } else {
            m.addChallengeToQueue(challenge);
            challenge.start();
        }
    }
}
