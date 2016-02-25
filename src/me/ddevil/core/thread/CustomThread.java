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
package me.ddevil.core.thread;

import java.util.ArrayList;

/**
 *
 * @author Selma
 */
public abstract class CustomThread extends Thread {

    private final ArrayList<FinishListener> listeners = new ArrayList<>();

    public final void addListener(final FinishListener listener) {
        listeners.add(listener);
    }

    public final void removeListener(final FinishListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (FinishListener listener : listeners) {
            listener.onFinish();
        }
    }

    @Override
    public final void run() {
        try {
            doRun();
        } finally {
            notifyListeners();
        }
    }

    public abstract void doRun();
}
