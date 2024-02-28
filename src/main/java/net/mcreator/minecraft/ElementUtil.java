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

package net.mcreator.minecraft;

import net.mcreator.element.BaseType;
import net.mcreator.element.ModElementType;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.types.LivingEntity;
import net.mcreator.element.types.interfaces.IPOIProvider;
import net.mcreator.ui.minecraft.states.PropertyData;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.SoundElement;
import net.mcreator.workspace.elements.VariableType;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ElementUtil {

	/**
	 * Provides a predicate to check the type of data list entries
	 *
	 * @param type The type that the entry has to match
	 * @return A predicate that checks if the type matches the parameter
	 */
	public static Predicate<DataListEntry> typeMatches(String type) {
		return e -> type.equals(e.getType());
	}

	/**
	 * Loads a list of entries, with optional custom entries from the given mod element types,
	 * and with an optional filter for the entry type.
	 *
	 * <p>NOTE: custom entries cannot specify a type yet, so the type filter will remove any custom entry</p>
	 *
	 * @param workspace            The current workspace
	 * @param dataList             The datalist from which to load the entries
	 * @param sorted               Whether the list should be sorted alphabetically
	 * @param typeFilter           If present, only entries whose type matches this parameter are loaded
	 * @param customEntryProviders The string id of the mod element types that provide custom entries
	 * @return All entries from the given data list and the given mod element types, matching the optional filter
	 */
	public static List<DataListEntry> loadDataListAndElements(Workspace workspace, String dataList, boolean sorted,
			@Nullable String typeFilter, @Nullable String... customEntryProviders) {
		List<DataListEntry> retval = new ArrayList<>();

		// We add custom entries before normal ones, so that they are on top even if the list isn't sorted
		if (customEntryProviders != null) {
			retval.addAll(getCustomElements(workspace,
					me -> Arrays.asList(customEntryProviders).contains(me.getTypeString())));
		}
		retval.addAll(DataListLoader.loadDataList(dataList));

		Stream<DataListEntry> retvalStream = retval.stream();
		if (typeFilter != null) {
			retvalStream = retvalStream.filter(typeMatches(typeFilter));
		}
		if (sorted) {
			return retvalStream.filter(e -> e.isSupportedInWorkspace(workspace)).sorted().toList();
		}
		return retvalStream.filter(e -> e.isSupportedInWorkspace(workspace)).toList();
	}

	/**
	 * Loads all mod elements and all Minecraft elements (blocks and items), including elements
	 * that are wildcard elements to subtypes (wood -&gt; oak wood, birch wood, ...)
	 *
	 * @return All Blocks and Items from both Minecraft and custom elements with or without metadata
	 */
	public static List<MCItem> loadBlocksAndItemsAndTags(Workspace workspace) {
		List<MCItem> elements = new ArrayList<>();
		workspace.getModElements().forEach(modElement -> elements.addAll(modElement.getMCItems()));
		elements.addAll(
				DataListLoader.loadDataList("blocksitems").stream().filter(e -> e.isSupportedInWorkspace(workspace))
						.map(e -> (MCItem) e).toList());
		return elements;
	}

	/**
	 * Loads all mod elements and all Minecraft elements (blocks and items), including elements
	 * that are wildcard elements to subtypes (wood -&gt; oak wood, birch wood, ...)
	 * This list also provides potions from both Minecraft elements and mod elements
	 *
	 * @return All Blocks and Items and Potions from both Minecraft and custom elements with or without metadata
	 */
	public static List<MCItem> loadBlocksAndItemsAndTagsAndPotions(Workspace workspace) {
		List<MCItem> elements = loadBlocksAndItemsAndTags(workspace);
		loadAllPotions(workspace).forEach(potion -> elements.add(new MCItem.Potion(workspace, potion)));
		return elements;
	}

	/**
	 * Loads all mod elements and all Minecraft elements (blocks and items) without elements
	 * that are wildcard elements to subtypes (wood -&gt; oak wood, birch wood, ...)
	 * so only oak wood, birch wood, ... are loaded, without wildcard wood element
	 * This list also provides potions from both Minecraft elements and mod elements
	 *
	 * @return All Blocks and Items and Potions from both Minecraft and custom elements with or without metadata
	 */
	public static List<MCItem> loadBlocksAndItemsAndPotions(Workspace workspace) {
		List<MCItem> elements = loadBlocksAndItems(workspace);
		loadAllPotions(workspace).forEach(potion -> elements.add(new MCItem.Potion(workspace, potion)));
		return elements;
	}

	/**
	 * Loads all mod elements and all Minecraft elements (blocks and items) without elements
	 * that are wildcard elements to subtypes (wood -&gt; oak wood, birch wood, ...)
	 * so only oak wood, birch wood, ... are loaded, without wildcard wood element
	 *
	 * @return All Blocks and Items from both Minecraft and custom elements with or without metadata
	 */
	public static List<MCItem> loadBlocksAndItems(Workspace workspace) {
		List<MCItem> elements = new ArrayList<>();
		workspace.getModElements().forEach(modElement -> elements.addAll(modElement.getMCItems()));
		elements.addAll(
				DataListLoader.loadDataList("blocksitems").stream().filter(e -> e.isSupportedInWorkspace(workspace))
						.map(e -> (MCItem) e).filter(MCItem::hasNoSubtypes).toList());
		return elements;
	}

	/**
	 * Loads all mod elements and all Minecraft blocks without elements
	 * that are wildcard elements to subtypes (wood -&gt; oak wood, birch wood, ...)
	 * so only oak wood, birch wood, ... are loaded, without wildcard wood element
	 *
	 * @return All Blocks from both Minecraft and custom elements with or without metadata
	 */
	public static List<MCItem> loadBlocks(Workspace workspace) {
		List<MCItem> elements = new ArrayList<>();
		workspace.getModElements().forEach(modElement -> elements.addAll(
				modElement.getMCItems().stream().filter(e -> e.getType().equals("block")).toList()));
		elements.addAll(
				DataListLoader.loadDataList("blocksitems").stream().filter(e -> e.isSupportedInWorkspace(workspace))
						.filter(typeMatches("block")).map(e -> (MCItem) e).filter(MCItem::hasNoSubtypes).toList());
		return elements;
	}

	/**
	 * Loads all mod elements and all Minecraft blocks, including elements
	 * that are wildcard elements to subtypes (wood -&gt; oak wood, birch wood, ...)
	 *
	 * @return All Blocks from both Minecraft and custom elements with or without metadata
	 */
	public static List<MCItem> loadBlocksAndTags(Workspace workspace) {
		List<MCItem> elements = new ArrayList<>();
		workspace.getModElements().forEach(modElement -> elements.addAll(
				modElement.getMCItems().stream().filter(e -> e.getType().equals("block")).toList()));
		elements.addAll(
				DataListLoader.loadDataList("blocksitems").stream().filter(e -> e.isSupportedInWorkspace(workspace))
						.filter(typeMatches("block")).map(e -> (MCItem) e).toList());
		return elements;
	}

	public static List<DataListEntry> loadAllAchievements(Workspace workspace) {
		return loadDataListAndElements(workspace, "achievements", false, null, "achievement");
	}

	public static List<DataListEntry> loadAllTabs(Workspace workspace) {
		return loadDataListAndElements(workspace, "tabs", false, null, "tab");
	}

	public static List<DataListEntry> loadAllBiomes(Workspace workspace) {
		List<DataListEntry> biomes = getCustomElementsOfType(workspace, ModElementType.BIOME);
		biomes.addAll(DataListLoader.loadDataList("biomes"));
		Collections.sort(biomes);
		return biomes;
	}

	public static List<DataListEntry> loadAllEnchantments(Workspace workspace) {
		return loadDataListAndElements(workspace, "enchantments", false, null, "enchantment");
	}

	public static List<DataListEntry> loadMaterials() {
		return DataListLoader.loadDataList("materials");
	}

	public static List<DataListEntry> loadMapColors() {
		return DataListLoader.loadDataList("mapcolors");
	}

	public static List<DataListEntry> loadEnchantmentTypes() {
		return DataListLoader.loadDataList("enchantmenttypes");
	}

	public static List<DataListEntry> loadAllEntities(Workspace workspace) {
		List<DataListEntry> retval = getCustomElements(workspace,
				mu -> mu.getBaseTypesProvided().contains(BaseType.ENTITY));
		retval.addAll(DataListLoader.loadDataList("entities"));
		Collections.sort(retval);
		return retval;
	}

	/**
	 * Returns all the spawnable entities, which include custom living entities and entities marked as "spawnable"
	 * in the data lists
	 *
	 * @param workspace The workspace from which to gather the entities
	 * @return All entities that can be spawned
	 */
	public static List<DataListEntry> loadAllSpawnableEntities(Workspace workspace) {
		List<DataListEntry> retval = getCustomElements(workspace,
				mu -> mu.getBaseTypesProvided().contains(BaseType.ENTITY));
		retval.addAll(DataListLoader.loadDataList("entities").stream().filter(typeMatches("spawnable")).toList());
		Collections.sort(retval);
		return retval;
	}

	public static List<DataListEntry> loadCustomEntities(Workspace workspace) {
		List<DataListEntry> retval = getCustomElements(workspace,
				mu -> mu.getBaseTypesProvided().contains(BaseType.ENTITY));
		Collections.sort(retval);
		return retval;
	}

	public static List<String> loadEntityDataListFromCustomEntity(Workspace workspace, String entityName,
			Class<? extends PropertyData<?>> type) {
		if (entityName != null) {
			LivingEntity entity = (LivingEntity) workspace.getModElementByName(entityName.replace("CUSTOM:", ""))
					.getGeneratableElement();
			if (entity != null) {
				return entity.entityDataEntries.stream().filter(e -> e.property().getClass().equals(type))
						.map(e -> e.property().getName()).toList();
			}
		}
		return new ArrayList<>();
	}

	public static List<DataListEntry> loadAllParticles(Workspace workspace) {
		return loadDataListAndElements(workspace, "particles", false, null, "particle");
	}

	public static List<DataListEntry> loadAllPotionEffects(Workspace workspace) {
		return loadDataListAndElements(workspace, "effects", false, null, "potioneffect");
	}

	public static List<DataListEntry> loadAllPotions(Workspace workspace) {
		return loadDataListAndElements(workspace, "potions", false, null, "potion");
	}

	public static List<DataListEntry> loadAllVillagerProfessions(Workspace workspace) {
		return loadDataListAndElements(workspace, "villagerprofessions", false, null, "villagerprofession");
	}

	/**
	 * Returns list of blocks attached to a POI for this workspace
	 *
	 * @param workspace Workspace to return for
	 * @return List of blocks attached to a POI for this workspace
	 */
	public static List<MItemBlock> loadAllPOIBlocks(Workspace workspace) {
		List<MItemBlock> elements = loadBlocks(workspace).stream().filter(MCItem::isPOI)
				.map(e -> new MItemBlock(workspace, e.getName())).collect(Collectors.toList());

		for (ModElement modElement : workspace.getModElements()) {
			if (modElement.getGeneratableElement() instanceof IPOIProvider poiProvider)
				elements.addAll(poiProvider.poiBlocks());
		}

		return elements;
	}

	public static List<DataListEntry> getAllBooleanGameRules(Workspace workspace) {
		List<DataListEntry> retval = getCustomElements(workspace, modelement -> {
			if (modelement.getType() == ModElementType.GAMERULE)
				return modelement.getMetadata("type").equals(VariableTypeLoader.BuiltInTypes.LOGIC.getName());
			return false;
		});

		retval.addAll(DataListLoader.loadDataList("gamerules").stream()
				.filter(typeMatches(VariableTypeLoader.BuiltInTypes.LOGIC.getName())).toList());
		return retval;
	}

	public static List<DataListEntry> getAllNumberGameRules(Workspace workspace) {
		List<DataListEntry> retval = getCustomElements(workspace, modelement -> {
			if (modelement.getType() == ModElementType.GAMERULE)
				return modelement.getMetadata("type").equals(VariableTypeLoader.BuiltInTypes.NUMBER.getName());
			return false;
		});

		retval.addAll(DataListLoader.loadDataList("gamerules").stream()
				.filter(typeMatches(VariableTypeLoader.BuiltInTypes.NUMBER.getName())).toList());
		return retval;
	}

	public static List<DataListEntry> loadAllFluids(Workspace workspace) {
		List<DataListEntry> retval = new ArrayList<>();

		for (ModElement modElement : workspace.getModElements()) {
			if (modElement.getType() == ModElementType.FLUID) {
				retval.add(new DataListEntry.Custom(modElement));
				retval.add(new DataListEntry.Custom(modElement, ":Flowing"));
			}
		}

		retval.addAll(DataListLoader.loadDataList("fluids"));

		return retval.stream().filter(e -> e.isSupportedInWorkspace(workspace)).toList();
	}

	public static String[] getAllSounds(Workspace workspace) {
		ArrayList<String> retval = new ArrayList<>();

		for (SoundElement soundElement : workspace.getSoundElements()) {
			retval.add("CUSTOM:" + soundElement.getName());
		}

		retval.addAll(DataListLoader.loadDataList("sounds").stream().sorted().map(DataListEntry::getName).toList());

		return retval.toArray(new String[0]);
	}

	public static List<DataListEntry> loadStepSounds() {
		return DataListLoader.loadDataList("stepsounds");
	}

	public static List<DataListEntry> loadArrowProjectiles(Workspace workspace) {
		List<DataListEntry> retval = getCustomElementsOfType(workspace, ModElementType.PROJECTILE);

		retval.addAll(DataListLoader.loadDataList("projectiles").stream().filter(typeMatches("arrow")).toList());
		return retval;
	}

	public static String[] loadAllDimensions(Workspace workspace) {
		ArrayList<String> dimensions = new ArrayList<>();
		dimensions.add("Surface");
		dimensions.add("Nether");
		dimensions.add("End");

		for (ModElement mu : workspace.getModElements())
			if (mu.getType() == ModElementType.DIMENSION)
				dimensions.add("CUSTOM:" + mu.getName());

		return dimensions.toArray(new String[0]);
	}

	public static String[] loadDirections() {
		return new String[] { "DOWN", "UP", "NORTH", "SOUTH", "WEST", "EAST" };
	}

	public static ArrayList<String> loadBasicGUIs(Workspace workspace) {
		ArrayList<String> blocks = new ArrayList<>();
		for (ModElement mu : workspace.getModElements()) {
			if (mu.getType() == ModElementType.GUI)
				blocks.add(mu.getName());
		}
		return blocks;
	}

	public static String[] getDataListAsStringArray(String dataList) {
		return DataListLoader.loadDataList(dataList).stream().map(DataListEntry::getName).toArray(String[]::new);
	}

	private static List<DataListEntry> getCustomElements(@Nonnull Workspace workspace,
			Predicate<ModElement> predicate) {
		return workspace.getModElements().stream().filter(predicate).map(DataListEntry.Custom::new)
				.collect(Collectors.toList());
	}

	private static List<DataListEntry> getCustomElementsOfType(@Nonnull Workspace workspace, ModElementType<?> type) {
		return getCustomElements(workspace, modelement -> modelement.getType() == type);
	}

	/**
	 * <p>Returns an array with the names of procedures that return the given variable type</p>
	 *
	 * @param workspace <p>The current workspace</p>
	 * @param type      <p>The {@link VariableType} that the procedures must return</p>
	 * @return <p>An array of strings containing the names of the procedures</p>
	 */
	public static String[] getProceduresOfType(Workspace workspace, VariableType type) {
		return workspace.getModElements().stream().filter(mod -> {
			if (mod.getType() == ModElementType.PROCEDURE) {
				VariableType returnTypeCurrent = mod.getMetadata("return_type") != null ?
						VariableTypeLoader.INSTANCE.fromName((String) mod.getMetadata("return_type")) :
						null;
				return returnTypeCurrent == type;
			}
			return false;
		}).map(ModElement::getName).toArray(String[]::new);
	}
}
