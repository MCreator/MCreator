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

import net.mcreator.workspace.elements.VariableType;

import javax.annotation.Nullable;

class CBoxEntry {
	String string;
	final boolean correctDependencies;
	@Nullable private final VariableType variableType;

	CBoxEntry(String string, @Nullable VariableType variableType) {
		this(string, variableType, true);
	}

	CBoxEntry(String string, @Nullable VariableType variableType, boolean correctDependencies) {
		this.string = string;
		if (this.string == null)
			this.string = "";
		this.correctDependencies = correctDependencies;
		this.variableType = variableType;
	}

	@Nullable public VariableType getVariableType() {
		return variableType;
	}

	@Override public boolean equals(Object o) {
		return o instanceof CBoxEntry && ((CBoxEntry) o).string.equals(this.string);
	}

	@Override public String toString() {
		return string;
	}

}
