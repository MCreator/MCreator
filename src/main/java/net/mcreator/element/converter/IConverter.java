/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
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

package net.mcreator.element.converter;

import com.google.gson.JsonElement;
import net.mcreator.element.GeneratableElement;
import net.mcreator.workspace.Workspace;
import org.jetbrains.annotations.NotNull;

public interface IConverter extends Comparable<IConverter> {

	GeneratableElement convert(Workspace workspace, GeneratableElement input, JsonElement jsonElementInput);

	int getVersionConvertingTo();

	@Override default int compareTo(@NotNull IConverter o) {
		return this.getVersionConvertingTo() - o.getVersionConvertingTo();
	}
}
