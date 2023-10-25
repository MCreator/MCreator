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
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.types.interfaces.IItemWithTexture;
import net.mcreator.element.types.interfaces.ITabContainedElement;
import net.mcreator.generator.GeneratorWrapper;
import net.mcreator.generator.mapping.MappableElement;
import net.mcreator.minecraft.MCItem;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableType;
import net.mcreator.workspace.resources.Model;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.*;

@SuppressWarnings("unused") public record WorkspaceInfo(Workspace workspace) {

	private static final Logger LOG = LogManager.getLogger("Workspace info");

	public List<ModElement> getElementsOfType(String typeString) {
		try {
			ModElementType<?> type = ModElementTypeLoader.getModElementType(typeString);
			return workspace.getModElements().parallelStream().filter(e -> e.getType() == type).toList();
		} catch (IllegalArgumentException e) {
			LOG.warn("Failed to list elements of non-existent type", e);
			return Collections.emptyList();
		}
	}

	public List<GeneratableElement> getGElementsOfType(String typeString) {
		try {
			ModElementType<?> type = ModElementTypeLoader.getModElementType(typeString);
			// getGeneratableElement is not thread safe, so we can't use parallelStream here
			return workspace.getModElements().stream().filter(e -> e.getType() == type)
					.map(ModElement::getGeneratableElement).filter(Objects::nonNull).toList();
		} catch (IllegalArgumentException e) {
			LOG.warn("Failed to list elements of non-existent type", e);
			return Collections.emptyList();
		}
	}

	public boolean hasElementsOfBaseType(String baseTypeString) {
		BaseType baseType = BaseType.valueOf(baseTypeString.toUpperCase(Locale.ENGLISH));
		for (ModElement modElement : workspace.getModElements()) {
			// getBaseTypesProvided is not thread safe, so we can't use parallelStream here
			if (modElement.getBaseTypesProvided().contains(baseType))
				return true;
		}
		return false;
	}

	public boolean hasElementsOfType(String typeString) {
		try {
			ModElementType<?> type = ModElementTypeLoader.getModElementType(typeString);
			return workspace.getModElements().parallelStream().anyMatch(e -> e.getType() == type);
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	public boolean hasVariablesOfScope(String type) {
		return workspace.getVariableElements().parallelStream()
				.anyMatch(e -> e.getScope() == VariableType.Scope.valueOf(type));
	}

	public boolean hasVariables() {
		return !workspace.getVariableElements().isEmpty();
	}

	public boolean hasSounds() {
		return !workspace.getSoundElements().isEmpty();
	}

	public boolean hasJavaModels() {
		return Model.getModels(workspace).parallelStream().anyMatch(model -> model.getType() == Model.Type.JAVA);
	}

	public <T extends MappableElement> Set<MappableElement.Unique> filterBrokenReferences(List<T> input) {
		if (input == null)
			return Collections.emptySet();

		Set<MappableElement.Unique> retval = new HashSet<>();
		for (T t : input) {
			if (t.getUnmappedValue().startsWith("CUSTOM:")) {
				if (workspace.getModElementByName(GeneratorWrapper.getElementPlainName(t.getUnmappedValue())) != null) {
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

	public boolean hasItemsInTabs() {
		List<GeneratableElement> elementsList = workspace.getModElements().stream()
				.map(ModElement::getGeneratableElement).filter(Objects::nonNull).toList();

		for (GeneratableElement element : elementsList) {
			if (element instanceof ITabContainedElement tabElement) {
				TabEntry tab = tabElement.getCreativeTab();
				if (tab != null && !tab.getUnmappedValue().equals("No creative tab entry")) {
					if (!tabElement.getCreativeTabItems().isEmpty())
						return true;
				}
			}
		}

		return false;
	}

	public Map<String, List<MItemBlock>> getCreativeTabMap() {
		List<GeneratableElement> elementsList = workspace.getModElements().stream()
				.sorted(Comparator.comparing(ModElement::getSortID)).map(ModElement::getGeneratableElement)
				.filter(Objects::nonNull).toList();

		Map<String, List<MItemBlock>> tabMap = new HashMap<>();

		for (GeneratableElement element : elementsList) {
			if (element instanceof ITabContainedElement tabElement) {
				TabEntry tabEntry = tabElement.getCreativeTab();
				List<MCItem> tabItems = tabElement.getCreativeTabItems();

				if (tabEntry != null && tabItems != null && !tabItems.isEmpty()) {
					String tab = tabEntry.getUnmappedValue();
					if (tab != null && !tab.equals("No creative tab entry")) {
						if (!tabMap.containsKey(tab)) {
							tabMap.put(tab, new ArrayList<>());
						}

						tabMap.get(tab)
								.addAll(tabItems.stream().map(e -> new MItemBlock(workspace, e.getName())).toList());
					}
				}
			}
		}

		return tabMap;
	}

	public String getUUID(String offset) {
		return UUID.nameUUIDFromBytes(
				(offset + workspace.getWorkspaceSettings().getModID()).getBytes(StandardCharsets.UTF_8)).toString();
	}

	public String getUUID() {
		return UUID.nameUUIDFromBytes(workspace.getWorkspaceSettings().getModID().getBytes(StandardCharsets.UTF_8))
				.toString();
	}

	public MItemBlock itemBlock(String itemBlock) {
		return new MItemBlock(workspace, itemBlock);
	}

	public Workspace getWorkspace() {
		return workspace;
	}

}
