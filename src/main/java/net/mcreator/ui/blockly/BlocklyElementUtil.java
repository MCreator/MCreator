/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

package net.mcreator.ui.blockly;

import net.mcreator.element.ModElementType;
import net.mcreator.element.types.Dimension;
import net.mcreator.generator.mapping.NameMapper;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.DataListLoader;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.minecraft.states.PropertyData;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableTypeLoader;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BlocklyElementUtil {

	private static final Map<String, StringArrayEntrySelectorProvider> stringArrayEntryProviders = new HashMap<>() {{
		put("entitydata_logic",
				(workspace, customEntryProviders) -> ElementUtil.loadEntityDataListFromCustomEntity(workspace,
						customEntryProviders, PropertyData.LogicType.class).toArray(String[]::new));
		put("entitydata_integer",
				(workspace, customEntryProviders) -> ElementUtil.loadEntityDataListFromCustomEntity(workspace,
						customEntryProviders, PropertyData.IntegerType.class).toArray(String[]::new));
		put("entitydata_string",
				(workspace, customEntryProviders) -> ElementUtil.loadEntityDataListFromCustomEntity(workspace,
						customEntryProviders, PropertyData.StringType.class).toArray(String[]::new));
		put("gui", (workspace, _) -> ElementUtil.loadBasicGUIs(workspace).toArray(String[]::new));
		put("dimensionCustom", (workspace, _) -> workspace.getModElementsByType(ModElementType.DIMENSION).stream()
				.map(m -> NameMapper.MCREATOR_PREFIX + m.getName()).toArray(String[]::new));
		put("dimensionCustomWithPortal",
				(workspace, _) -> workspace.getModElementsByType(ModElementType.DIMENSION).stream()
						.map(ModElement::getGeneratableElement).filter(ge -> ge instanceof Dimension)
						.map(ge -> (Dimension) ge).filter(dimension -> dimension.enablePortal)
						.map(m -> NameMapper.MCREATOR_PREFIX + m.getModElement().getName()).toArray(String[]::new));
		put("structure", (workspace, _) -> workspace.getFolderManager().getStructureList().toArray(String[]::new));
		put("procedure", (workspace, _) -> workspace.getModElementsByType(ModElementType.PROCEDURE).stream()
				.map(ModElement::getName).toArray(String[]::new));
	}};

	/**
	 * Loads data list entries for Blockly entry selectors that use {@link DataListEntry} lists.
	 *
	 * @return The list of entries, or {@code null} if the selector type uses plain strings instead
	 */
	@Nullable public static List<DataListEntry> getDataListEntriesForEntrySelector(Workspace workspace,
			@Nonnull String type, @Nullable String typeFilter, @Nullable String customEntryProviders) {
		return switch (type) {
			case "entity" ->
					ElementUtil.loadAllEntities(workspace).stream().filter(e -> e.isSupportedInWorkspace(workspace))
							.toList();
			case "spawnableEntity" -> ElementUtil.loadAllSpawnableEntities(workspace);
			case "customEntity" -> ElementUtil.loadCustomEntities(workspace);
			case "biome" -> ElementUtil.loadAllBiomes(workspace);
			case "fluid" -> ElementUtil.loadAllFluids(workspace);
			case "gamerulesboolean" -> ElementUtil.getAllEntriesFor(workspace, "gamerules_boolean");
			case "gamerulesnumber" -> ElementUtil.getAllEntriesFor(workspace, "gamerules_number");
			case "eventparametersnumber" -> DataListLoader.loadDataList("eventparameters").stream()
					.filter(ElementUtil.typeMatches(VariableTypeLoader.BuiltInTypes.NUMBER.getName()))
					.filter(e -> e.isSupportedInWorkspace(workspace)).toList();
			case "eventparametersboolean" -> DataListLoader.loadDataList("eventparameters").stream()
					.filter(ElementUtil.typeMatches(VariableTypeLoader.BuiltInTypes.LOGIC.getName()))
					.filter(e -> e.isSupportedInWorkspace(workspace)).toList();
			case "arrowProjectile" -> ElementUtil.loadArrowProjectiles(workspace);
			case "configuredfeature" -> ElementUtil.loadAllConfiguredFeatures(workspace);
			case "sound" ->
					ElementUtil.loadAllSounds(workspace).stream().filter(e -> e.isSupportedInWorkspace(workspace))
							.toList();
			case "direction" -> List.copyOf(DataListLoader.loadDataList("directions"));
			default -> {
				if (!DataListLoader.loadDataList(type).isEmpty()) {
					yield loadDataListAndElements(workspace, type, typeFilter,
							StringUtils.split(customEntryProviders, ','));
				}
				yield null;
			}
		};
	}

	@Nullable
	public static String[] getStringArrayForEntrySelector(Workspace workspace, @Nonnull String type,
			@Nullable String customEntryProviders) {
		StringArrayEntrySelectorProvider provider = stringArrayEntryProviders.get(type);
		return provider != null ? provider.provide(workspace, customEntryProviders) : null;
	}

	@FunctionalInterface public interface StringArrayEntrySelectorProvider {
		@Nullable String[] provide(Workspace workspace, @Nullable String customEntryProviders);
	}

	/**
	 * Loads a list of entries, with optional custom entries from the given mod element types,
	 * and with an optional filter for the entry type.
	 *
	 * <p>NOTE: custom entries cannot specify a type yet, so the type filter will remove any custom entry</p>
	 *
	 * @param workspace            The current workspace
	 * @param dataList             The datalist from which to load the entries
	 * @param typeFilter           If present, only entries whose type matches this parameter are loaded
	 * @param customEntryProviders The string id of the mod element types that provide custom entries
	 * @return All entries from the given data list and the given mod element types, matching the optional filter
	 */
	private static List<DataListEntry> loadDataListAndElements(Workspace workspace, String dataList,
			@Nullable String typeFilter, @Nullable String... customEntryProviders) {
		List<DataListEntry> retval = new ArrayList<>();

		// We add custom entries before normal ones, so that they are on top even if the list isn't sorted
		if (customEntryProviders != null) {
			retval.addAll(workspace.getModElements().stream()
					.filter(me -> Arrays.asList(customEntryProviders).contains(me.getTypeString()))
					.map(DataListEntry.Custom::new).toList());
		}
		retval.addAll(DataListLoader.loadDataList(dataList));

		Stream<DataListEntry> retvalStream = retval.stream();
		if (typeFilter != null) {
			retvalStream = retvalStream.filter(ElementUtil.typeMatches(typeFilter));
		}
		retval = retvalStream.filter(e -> e.isSupportedInWorkspace(workspace)).collect(Collectors.toList());
		retval.sort(DataListEntry.getComparator(workspace, retval));
		return retval;
	}

	// Allow plugins to alter providers

	public static Map<String, StringArrayEntrySelectorProvider> getStringArrayEntryProviders() {
		return stringArrayEntryProviders;
	}

}
