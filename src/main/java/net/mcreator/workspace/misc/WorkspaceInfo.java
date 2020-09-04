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

package net.mcreator.workspace.misc;

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.IItemWithTexture;
import net.mcreator.element.ModElementType;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableElementType;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("unused") public class WorkspaceInfo {

	private final Workspace workspace;

	public WorkspaceInfo(Workspace workspace) {
		this.workspace = workspace;
	}

	public boolean hasVariables() {
		return workspace.getVariableElements().size() > 0;
	}

	public boolean hasVariablesOfScope(String type) {
		return workspace.getVariableElements().stream()
				.anyMatch(e -> e.getScope() == VariableElementType.Scope.valueOf(type));
	}

	public boolean hasFluids() {
		for (ModElement element : workspace.getModElements())
			if (element.getType() == ModElementType.FLUID)
				return true;
		return false;
	}

	public Map<String, String> getItemTextureMap() {
		Map<String, String> textureMap = new HashMap<>();
		for (ModElement element : workspace.getModElements()) {
			if (element.getType().getBaseType() == ModElementType.BaseType.ITEM) {
				GeneratableElement generatableElement = element.getGeneratableElement();
				if (generatableElement instanceof IItemWithTexture) {
					textureMap.put(element.getRegistryName(), ((IItemWithTexture) generatableElement).getTexture());
				}
			}
		}
		return textureMap;
	}

	public List<ModElement> getElementsOfType(String typestring) {
		ModElementType type = ModElementType.valueOf(typestring);
		return workspace.getModElements().parallelStream().filter(e -> e.getType() == type)
				.collect(Collectors.toList());
	}

	public String getUUID(String offset) {
		return UUID.nameUUIDFromBytes(
				(offset + workspace.getWorkspaceSettings().getModID()).getBytes(StandardCharsets.UTF_8)).toString();
	}

	public String getUUID() {
		return UUID.nameUUIDFromBytes(workspace.getWorkspaceSettings().getModID().getBytes(StandardCharsets.UTF_8))
				.toString();
	}

	public Workspace getWorkspace() {
		return workspace;
	}

}
