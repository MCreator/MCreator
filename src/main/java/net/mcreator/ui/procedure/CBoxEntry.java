/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.mcreator.ui.procedure;

class CBoxEntry {
	String string;
	boolean correctDependencies;

	CBoxEntry(String string) {
		this(string, true);
	}

	CBoxEntry(String string, boolean correctDependencies) {
		this.string = string;
		this.correctDependencies = correctDependencies;
	}

	@Override public boolean equals(Object o) {
		return o instanceof CBoxEntry && ((CBoxEntry) o).string.equals(this.string);
	}

	@Override public String toString() {
		return string;
	}

}
