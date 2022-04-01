/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

package net.mcreator.workspace.types;

import net.mcreator.element.ModElementType;
import net.mcreator.element.ModElementTypeLoader;

import java.util.List;
import java.util.stream.Collectors;

public class WorkspaceType {

	private String id;
	private List<String> modElements;

	public WorkspaceType(String id, List<String> modElements) {
		this.id = id;
		this.modElements = modElements;
	}

	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public List<ModElementType<?>> getModElements() {
		return modElements.stream().map(ModElementTypeLoader::getModElementType).collect(Collectors.toList());
	}
}
