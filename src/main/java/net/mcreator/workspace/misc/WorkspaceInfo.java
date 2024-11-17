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
import net.mcreator.element.parts.TextureHolder;
import net.mcreator.element.types.interfaces.IItemWithTexture;
import net.mcreator.element.types.interfaces.ITabContainedElement;
import net.mcreator.generator.GeneratorWrapper;
import net.mcreator.generator.mapping.MappableElement;
import net.mcreator.generator.mapping.NonMappableElement;
import net.mcreator.generator.mapping.UniquelyMappedElement;
import net.mcreator.minecraft.MCItem;
import net.mcreator.util.TraceUtil;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.TagElement;
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

	public Map<String, TextureHolder> getItemTextureMap() {
		Map<String, TextureHolder> textureMap = new HashMap<>();
		for (ModElement element : workspace.getModElements()) {
			if (element.getGeneratableElement() instanceof IItemWithTexture itemWithTexture) {
				textureMap.put(element.getRegistryName(), itemWithTexture.getTexture());
			}
		}
		return textureMap;
	}

	public boolean hasItemsInTabs() {
		List<GeneratableElement> elementsList = workspace.getModElements().stream()
				.map(ModElement::getGeneratableElement).filter(Objects::nonNull).toList();

		for (GeneratableElement element : elementsList) {
			if (element instanceof ITabContainedElement tabElement) {
				if (!tabElement.getCreativeTabs().isEmpty() && !tabElement.getCreativeTabItems().isEmpty()) {
					return true;
				}
			}
		}

		return false;
	}

	public Map<String, List<MItemBlock>> getCreativeTabMap() {
		List<GeneratableElement> elementsList = workspace.getModElements().stream()
				.map(ModElement::getGeneratableElement).toList();

		Map<String, List<MItemBlock>> tabMap = new LinkedHashMap<>();

		// Can't use parallelStream here because getCreativeTabItems
		// call MCItem.Custom::new that calls getBlockIconBasedOnName which calls
		// ModElement#getGeneratableElement that is not thread safe
		for (GeneratableElement element : elementsList) {
			if (element instanceof ITabContainedElement tabElement) {
				List<MItemBlock> tabItems = tabElement.getCreativeTabItems().stream()
						.map(e -> new MItemBlock(workspace, e.getName())).toList();
				if (!tabItems.isEmpty()) {
					for (TabEntry tabEntry : tabElement.getCreativeTabs()) {
						String tab = tabEntry.getUnmappedValue();

						// If tab does not have custom order, add items to the end of the list
						if (workspace.getCreativeTabsOrder().get(tab) == null)
							tabMap.computeIfAbsent(tab, key -> new ArrayList<>()).addAll(tabItems);
					}
				}
			}
		}

		// Last, we add items to tabs with custom order
		for (Map.Entry<String, ArrayList<String>> entry : workspace.getCreativeTabsOrder().entrySet()) {
			String tab = entry.getKey();
			ModElement tabME = workspace.getModElementByName(tab.replace("CUSTOM:", ""));
			if (tabME != null && tabME.getType() == ModElementType.TAB) {
				for (String element : entry.getValue()) {
					ModElement me = workspace.getModElementByName(element);
					if (me != null && me.getGeneratableElement() instanceof ITabContainedElement tabElement) {
						List<MCItem> tabItems = tabElement.getCreativeTabItems();
						if (tabItems != null && !tabItems.isEmpty()) {
							tabMap.computeIfAbsent(tab, key -> new ArrayList<>())
									.addAll(tabItems.stream().map(e -> new MItemBlock(workspace, e.getName()))
											.toList());
						}
					}
				}
			}
		}

		return tabMap;
	}

	public <T extends MappableElement> Set<MappableElement> filterBrokenReferences(Collection<T> input) {
		if (input == null)
			return Collections.emptySet();

		Set<MappableElement> retval = new LinkedHashSet<>();
		for (T t : input) {
			if (t instanceof NonMappableElement) {
				retval.add(t);
			} else if (t.getUnmappedValue().startsWith("CUSTOM:")) {
				if (workspace.containsModElement(GeneratorWrapper.getElementPlainName(t.getUnmappedValue()))) {
					retval.add(new UniquelyMappedElement(t));
				} else {
					LOG.warn("({}) Broken reference found. Referencing non-existent element: {}",
							TraceUtil.tryToFindMCreatorInvoker(), t.getUnmappedValue().replaceFirst("CUSTOM:", ""));
				}
			} else {
				retval.add(new UniquelyMappedElement(t));
			}
		}
		return retval;
	}

	/**
	 * Returns a set of mappable elements that do not trigger circular dependency to tag
	 *
	 * @param tag          Tag name with namespace to check. A plain name without # or TAG: prefix is required.
	 * @param mappingTable Mapping table to use for getting mapped values of the elements
	 * @param elements     Collection of elements to normalize/filter
	 * @param <T>          Type of elements
	 * @return Set of elements that do not trigger circular dependency to tag
	 */
	public <T extends MappableElement> Set<MappableElement> normalizeTagElements(String tag, int mappingTable,
			Collection<T> elements) {
		tag = TagElement.normalizeTag("#" + tag);
		Set<MappableElement> filtered = filterBrokenReferences(elements);

		Set<MappableElement> retval = new LinkedHashSet<>();
		for (MappableElement element : filtered) {
			if (!tag.equals(TagElement.normalizeTag(element.getMappedValue(mappingTable)))) {
				retval.add(element);
			}
		}
		return retval;
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
