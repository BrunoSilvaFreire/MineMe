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
package me.ddevil.mineme.messages;

/**
 *
 * @author Selma
 */
public enum MessageColor {

    PRIMARY(1),
    SECONDARY(2),
    NEUTRAL(3),
    ERROR(4);

    private MessageColor(Integer id) {
        this.id = id;
    }

    private final Integer id;

    @Override
    public String toString() {
        return "$" + id;
    }

}
