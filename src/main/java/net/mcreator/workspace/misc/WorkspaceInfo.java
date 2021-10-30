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

import net.mcreator.element.BaseType;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.ModElementTypeLoader;
import net.mcreator.element.types.Block;
import net.mcreator.element.types.GameRule;
import net.mcreator.element.types.Recipe;
import net.mcreator.element.types.Tool;
import net.mcreator.element.types.interfaces.ICommonType;
import net.mcreator.element.types.interfaces.IItemWithTexture;
import net.mcreator.generator.GeneratorWrapper;
import net.mcreator.generator.mapping.MappableElement;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableType;
import net.mcreator.workspace.resources.Model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused") public class WorkspaceInfo {

	private static final Logger LOG = LogManager.getLogger("Workspace info");

	private final Workspace workspace;

	private final GeneratorWrapper internalWrapper;

	public WorkspaceInfo(Workspace workspace) {
		this.workspace = workspace;
		this.internalWrapper = new GeneratorWrapper(workspace.getGenerator());
	}

	public boolean hasVariables() {
		return workspace.getVariableElements().size() > 0;
	}

	public boolean hasJavaModels() {
		return Model.getModels(workspace).stream().anyMatch(model -> model.getType() == Model.Type.JAVA);
	}

	public boolean hasSounds() {
		return workspace.getSoundElements().size() > 0;
	}

	public boolean hasVariablesOfScope(String type) {
		return workspace.getVariableElements().stream().anyMatch(e -> e.getScope() == VariableType.Scope.valueOf(type));
	}

	public Map<String, String> getItemTextureMap() {
		Map<String, String> textureMap = new HashMap<>();
		for (ModElement element : workspace.getModElements()) {
			if (element.getType().getBaseType() == BaseType.ITEM) {
				GeneratableElement generatableElement = element.getGeneratableElement();
				if (generatableElement instanceof IItemWithTexture) {
					textureMap.put(element.getRegistryName(), ((IItemWithTexture) generatableElement).getTexture());
				}
			}
		}
		return textureMap;
	}

	public String getUUID(String offset) {
		return UUID.nameUUIDFromBytes(
				(offset + workspace.getWorkspaceSettings().getModID()).getBytes(StandardCharsets.UTF_8)).toString();
	}

	public String getUUID() {
		return UUID.nameUUIDFromBytes(workspace.getWorkspaceSettings().getModID().getBytes(StandardCharsets.UTF_8))
				.toString();
	}

	public <T extends MappableElement> Set<MappableElement.Unique> filterBrokenReferences(List<T> input) {
		if (input == null)
			return Collections.emptySet();

		Set<MappableElement.Unique> retval = new HashSet<>();
		for (T t : input) {
			if (t.getUnmappedValue().startsWith("CUSTOM:")) {
				if (workspace.getModElementByName(internalWrapper.getElementPlainName(t.getUnmappedValue())) != null) {
					retval.add(new MappableElement.Unique(t));
				} else {
					LOG.warn("Broken reference found. Referencing non-existent element: " + t.getUnmappedValue()
							.replaceFirst("CUSTOM:", ""));
				}
			} else {
				retval.add(new MappableElement.Unique(t));
			}
		}
		return retval;
	}

	public List<ModElement> getElementsOfType(String typestring) {
		return getElementsOfType(ModElementTypeLoader.getModElementType(typestring));
	}

	public List<ModElement> getElementsOfType(ModElementType<?> type) {
		try {
			return workspace.getModElements().parallelStream().filter(e -> e.getType() == type)
					.collect(Collectors.toList());
		} catch (IllegalArgumentException e) {
			LOG.warn("Failed to list elements of non-existent type", e);
			return Collections.emptyList();
		}
	}

	public List<ModElement> getRecipesOfType(String typestring) {
		try {
			return workspace.getModElements().parallelStream().filter(e -> e.getType() == ModElementType.RECIPE)
					.filter(e -> {
						GeneratableElement ge = e.getGeneratableElement();
						if (ge instanceof Recipe)
							return ((Recipe) ge).recipeType.equals(typestring);
						return false;
					}).collect(Collectors.toList());
		} catch (IllegalArgumentException e) {
			LOG.warn("Failed to list elements of non-existent type", e);
			return Collections.emptyList();
		}
	}

	public boolean hasElementsOfBaseType(BaseType baseType) {
		for (ModElement modElement : workspace.getModElements()) {
			GeneratableElement generatableElement = modElement.getGeneratableElement();
			if (generatableElement instanceof ICommonType) {
				Collection<BaseType> baseTypes = ((ICommonType) generatableElement).getBaseTypesProvided();
				if (baseTypes.contains(baseType))
					return true;
			}
		}
		return false;
	}

	public boolean hasElementsOfBaseType(String baseType) {
		return hasElementsOfBaseType(BaseType.valueOf(baseType.toUpperCase(Locale.ENGLISH)));
	}

	public boolean hasElementsOfType(ModElementType<?> type) {
		try {
			return workspace.getModElements().parallelStream().anyMatch(e -> e.getType() == type);
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	public boolean hasElementsOfType(String typestring) {
		return hasElementsOfType(ModElementTypeLoader.getModElementType(typestring));
	}

	public boolean hasGameRulesOfType(String type) {
		for (ModElement element : workspace.getModElements())
			if (element.getType() == ModElementType.GAMERULE) {
				if (element.getGeneratableElement() instanceof GameRule gr)
					if (gr.type.equals(type))
						return true;
			}
		return false;
	}

	public boolean hasBlocksMineableWith(String tool) {
		for (ModElement element : workspace.getModElements())
			if (element.getType() == ModElementType.BLOCK) {
				if (element.getGeneratableElement() instanceof Block block)
					if (block.destroyTool.equals(tool))
						return true;
			}
		return false;
	}

	public boolean hasToolsOfType(String type) {
		for (ModElement element : workspace.getModElements())
			if (element.getType() == ModElementType.TOOL) {
				if (element.getGeneratableElement() instanceof Tool tool)
					if (tool.toolType.equals(type))
						return true;
			}
		return false;
	}

	public Workspace getWorkspace() {
		return workspace;
	}

}
