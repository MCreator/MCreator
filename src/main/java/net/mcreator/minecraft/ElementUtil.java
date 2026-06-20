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
import net.mcreator.generator.mapping.NameMapper;
import net.mcreator.ui.minecraft.states.PropertyData;
import net.mcreator.util.ListUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.SoundElement;
import net.mcreator.workspace.elements.VariableType;
import net.mcreator.workspace.elements.VariableTypeLoader;
import net.mcreator.workspace.resources.Animation;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ElementUtil {

	/**
	 * Provides a predicate to check the type of data list entries
	 *
	 * @param type The type that the entry has to match
	 * @return A predicate that checks if the type matches the parameter
	 */
	public static Predicate<DataListEntry> typeMatches(String... type) {
		if (type.length == 1) {
			String singleType = type[0];
			return e -> singleType.equals(e.getType());
		} else {
			List<String> typeList = Arrays.asList(type);
			return e -> typeList.contains(e.getType());
		}
	}

	/**
	 * Loads all items (also blocks if they have item representation), but without those
	 * that are wildcard elements to subtypes (wood: oak wood, cherry wood, ...)
	 * so only oak wood, cherry wood, ... are loaded, without wildcard wood element.
	 * This will not load blocks without item representation (example fire, water, ...).
	 *
	 * @return All Blocks and Items from both Minecraft and custom elements with or without metadata
	 */
	public static List<MCItem> loadBlocksAndItems(Workspace workspace) {
		List<MCItem> elements = new ArrayList<>();
		workspace.getModElements().forEach(modElement -> elements.addAll(modElement.getMCItems()));
		elements.sort(MCItem.getComparator(workspace, elements)); // sort custom elements
		elements.addAll(
				DataListLoader.loadDataList("blocksitems").stream().map(e -> (MCItem) e).filter(MCItem::hasNoSubtypes)
						.toList());
		return elements.stream().filter(typeMatches("block", "item")).filter(e -> e.isSupportedInWorkspace(workspace))
				.collect(Collectors.toList());
	}

	/**
	 * Loads all items (also blocks if they have item representation), including elements
	 * that are wildcard elements to subtypes (wood: oak wood, cherry wood, ...).
	 * This will not load blocks without item representation (example fire, water, ...).
	 *
	 * @return All Blocks and Items from both Minecraft and custom elements with or without metadata
	 */
	public static List<MCItem> loadBlocksAndItemsAndTags(Workspace workspace) {
		List<MCItem> elements = new ArrayList<>();
		workspace.getModElements().forEach(modElement -> elements.addAll(modElement.getMCItems()));
		elements.sort(MCItem.getComparator(workspace, elements)); // sort custom elements
		elements.addAll(DataListLoader.loadDataList("blocksitems").stream().map(e -> (MCItem) e).toList());
		return elements.stream().filter(typeMatches("block", "item", "tag"))
				.filter(e -> e.isSupportedInWorkspace(workspace)).collect(Collectors.toList());
	}

	/**
	 * Loads all blocks without those that are wildcard elements to subtypes (wood: oak wood, cherry wood, ...)
	 * so only oak wood, cherry wood, ... are loaded, without wildcard wood element
	 *
	 * @return All Blocks from both Minecraft and custom elements with or without metadata
	 */
	public static List<MCItem> loadBlocks(Workspace workspace) {
		List<MCItem> elements = new ArrayList<>();
		workspace.getModElements().forEach(modElement -> elements.addAll(modElement.getMCItems()));
		elements.sort(MCItem.getComparator(workspace, elements)); // sort custom elements
		elements.addAll(
				DataListLoader.loadDataList("blocksitems").stream().map(e -> (MCItem) e).filter(MCItem::hasNoSubtypes)
						.toList());
		return elements.stream().filter(typeMatches("block", "block_without_item"))
				.filter(e -> e.isSupportedInWorkspace(workspace)).collect(Collectors.toList());
	}

	/**
	 * Loads all blocks with an item form, without those that are wildcard elements to subtypes
	 * (wood: oak wood, cherry wood, ...), so only oak wood, cherry wood, ... are loaded, without wildcard wood element
	 *
	 * @return All Blocks from both Minecraft and custom elements with or without metadata
	 */
	public static List<MCItem> loadBlocksWithItemForm(Workspace workspace) {
		List<MCItem> elements = new ArrayList<>();
		workspace.getModElements().forEach(modElement -> elements.addAll(modElement.getMCItems()));
		elements.sort(MCItem.getComparator(workspace, elements)); // sort custom elements
		elements.addAll(
				DataListLoader.loadDataList("blocksitems").stream().map(e -> (MCItem) e).filter(MCItem::hasNoSubtypes)
						.toList());
		return elements.stream().filter(typeMatches("block")).filter(e -> e.isSupportedInWorkspace(workspace))
				.collect(Collectors.toList());
	}

	/**
	 * Loads all mod elements and all Minecraft blocks, including those that
	 * are wildcard elements to subtypes (wood: oak wood, cherry wood, ...)
	 *
	 * @return All Blocks from both Minecraft and custom elements with or without metadata
	 */
	public static List<MCItem> loadBlocksAndTags(Workspace workspace) {
		List<MCItem> elements = new ArrayList<>();
		workspace.getModElements().forEach(modElement -> elements.addAll(modElement.getMCItems()));
		elements.sort(MCItem.getComparator(workspace, elements)); // sort custom elements
		elements.addAll(DataListLoader.loadDataList("blocksitems").stream().map(e -> (MCItem) e).toList());
		return elements.stream().filter(typeMatches("block", "block_without_item", "tag"))
				.filter(e -> e.isSupportedInWorkspace(workspace)).collect(Collectors.toList());
	}

	/**
	 * Loads all items (also blocks if they have item representation), including those
	 * that are wildcard elements to subtypes (wood: oak wood, cherry wood, ...)
	 * This list also provides potions from both Minecraft elements and mod elements.
	 * This will not load blocks without item representation (example fire, water, ...).
	 *
	 * @return All Blocks and Items and Potions from both Minecraft and custom elements with or without metadata
	 */
	public static List<MCItem> loadBlocksAndItemsAndTagsAndPotions(Workspace workspace) {
		List<MCItem> elements = loadBlocksAndItemsAndTags(workspace);
		getAllEntriesFor(workspace, "potions").forEach(potion -> elements.add(new MCItem.Potion(workspace, potion)));
		return elements;
	}

	/**
	 * Loads all items (also blocks if they have item representation), without those
	 * that are wildcard elements to subtypes (wood: oak wood, cherry wood, ...)
	 * so only oak wood, cherry wood, ... are loaded, without wildcard wood element
	 * This list also provides potions from both Minecraft elements and mod elements.
	 * This will not load blocks without item representation (example fire, water, ...).
	 *
	 * @return All Blocks and Items and Potions from both Minecraft and custom elements with or without metadata
	 */
	public static List<MCItem> loadBlocksAndItemsAndPotions(Workspace workspace) {
		List<MCItem> elements = loadBlocksAndItems(workspace);
		getAllEntriesFor(workspace, "potions").forEach(potion -> elements.add(new MCItem.Potion(workspace, potion)));
		return elements;
	}

	public static List<String> loadEntityDataListFromCustomEntity(Workspace workspace, String entityName,
			Class<? extends PropertyData<?>> type) {
		if (entityName != null) {
			ModElement modElement = workspace.getModElementByName(entityName.replace(NameMapper.MCREATOR_PREFIX, ""));
			if (modElement != null) {
				if (modElement.getGeneratableElement() instanceof LivingEntity entity) {
					return entity.entityDataEntries.stream().filter(e -> e.property().getClass().equals(type))
							.map(e -> e.property().getName()).toList();
				}
			}
		}
		return new ArrayList<>();
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

	public static ArrayList<String> loadBasicGUIs(Workspace workspace) {
		ArrayList<String> blocks = new ArrayList<>();
		for (ModElement mu : workspace.getModElementsByType(ModElementType.GUI)) {
			blocks.add(mu.getName());
		}
		return blocks;
	}

	public static List<DataListEntry> loadAllEquipmentSlots() {
		return loadAllEquipmentSlots(false);
	}

	public static List<DataListEntry> loadAllEquipmentSlots(boolean addDefault) {
		return addDefault ?
				ListUtils.merge(List.of(new DataListEntry.Dummy("default")),
						DataListLoader.loadDataList("equipmentslots")) :
				List.copyOf(DataListLoader.loadDataList("equipmentslots"));
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
		return workspace.getModElementsByType(type).stream().map(DataListEntry.Custom::new)
				.collect(Collectors.toList());
	}

	/**
	 * <p>Returns an array with the names of procedures that return the given variable type</p>
	 *
	 * @param workspace <p>The current workspace</p>
	 * @param type      <p>The {@link VariableType} that the procedures must return</p>
	 * @return <p>An array of strings containing the names of the procedures</p>
	 */
	public static String[] getProceduresOfType(Workspace workspace, VariableType type) {
		return workspace.getModElementsByType(ModElementType.PROCEDURE).stream().filter(mod -> {
			VariableType returnTypeCurrent = mod.getMetadata("return_type") != null ?
					VariableTypeLoader.INSTANCE.fromName((String) mod.getMetadata("return_type")) :
					null;
			return returnTypeCurrent == type;
		}).map(ModElement::getName).toArray(String[]::new);
	}

	private static final Map<String, Function<Workspace, Collection<DataListEntry>>> vanillaEntryProviders = new HashMap<>() {{
		put("entities_spawnable",
				workspace -> DataListLoader.loadDataList("entities").stream().filter(typeMatches("spawnable"))
						.filter(e -> e.isSupportedInWorkspace(workspace)).toList());
		put("entities_custom", _ -> List.of());
		put("gamerules_boolean", workspace -> DataListLoader.loadDataList("gamerules").stream()
				.filter(typeMatches(VariableTypeLoader.BuiltInTypes.LOGIC.getName()))
				.filter(e -> e.isSupportedInWorkspace(workspace)).toList());
		put("gamerules_number", workspace -> DataListLoader.loadDataList("gamerules").stream()
				.filter(typeMatches(VariableTypeLoader.BuiltInTypes.NUMBER.getName()))
				.filter(e -> e.isSupportedInWorkspace(workspace)).toList());
		put("projectiles_arrow",
				workspace -> DataListLoader.loadDataList("projectiles").stream().filter(typeMatches("arrow"))
						.filter(e -> e.isSupportedInWorkspace(workspace)).toList());
	}};

	private static final Map<String, Function<Workspace, Collection<DataListEntry>>> customEntryProviders = new HashMap<>() {{
		put("achievements", workspace -> getCustomElementsOfType(workspace, ModElementType.ADVANCEMENT));
		put("tabs", workspace -> getCustomElementsOfType(workspace, ModElementType.TAB));
		put("biomes", workspace -> getCustomElementsOfType(workspace, ModElementType.BIOME));
		put("enchantments", workspace -> getCustomElementsOfType(workspace, ModElementType.ENCHANTMENT));
		put("structures", workspace -> getCustomElementsOfType(workspace, ModElementType.STRUCTURE));
		put("animations", workspace -> {
			List<DataListEntry> animations = new ArrayList<>();
			for (Animation animation : Animation.getAnimations(workspace)) {
				for (String subanimation : animation.getSubanimations()) {
					animations.add(new DataListEntry.Dummy(
							NameMapper.MCREATOR_PREFIX + animation.getName() + "." + subanimation));
				}
			}
			return animations;
		});
		put("entities",
				workspace -> getCustomElements(workspace, mu -> mu.getBaseTypesProvided().contains(BaseType.ENTITY)));
		put("entities_spawnable",
				workspace -> getCustomElements(workspace, mu -> mu.getBaseTypesProvided().contains(BaseType.ENTITY)));
		put("entities_custom",
				workspace -> getCustomElements(workspace, mu -> mu.getBaseTypesProvided().contains(BaseType.ENTITY)));
		put("particles", workspace -> getCustomElementsOfType(workspace, ModElementType.PARTICLE));
		put("effects", workspace -> getCustomElementsOfType(workspace, ModElementType.POTIONEFFECT));
		put("potions", workspace -> getCustomElementsOfType(workspace, ModElementType.POTION));
		put("villagerprofessions", workspace -> getCustomElementsOfType(workspace, ModElementType.VILLAGERPROFESSION));
		put("attributes", workspace -> getCustomElementsOfType(workspace, ModElementType.ATTRIBUTE));
		put("damagesources", workspace -> getCustomElementsOfType(workspace, ModElementType.DAMAGETYPE));
		put("gamerules_boolean", workspace -> getCustomElements(workspace, modelement -> {
			if (modelement.getType() == ModElementType.GAMERULE)
				return VariableTypeLoader.BuiltInTypes.LOGIC.getName().equals(modelement.getMetadata("type"));
			return false;
		}));
		put("gamerules_number", workspace -> getCustomElements(workspace, modelement -> {
			if (modelement.getType() == ModElementType.GAMERULE)
				return VariableTypeLoader.BuiltInTypes.NUMBER.getName().equals(modelement.getMetadata("type"));
			return false;
		}));
		put("fluids", workspace -> {
			List<DataListEntry> retval = new ArrayList<>();
			for (ModElement modElement : workspace.getModElementsByType(ModElementType.FLUID)) {
				retval.add(new DataListEntry.Custom(modElement));
				retval.add(new DataListEntry.Custom(modElement, ":Flowing"));
			}
			return retval;
		});
		put("sounds", workspace -> {
			List<DataListEntry> retval = new ArrayList<>();
			for (SoundElement soundElement : workspace.getSoundElements()) {
				retval.add(new DataListEntry.Dummy(NameMapper.MCREATOR_PREFIX + soundElement.getName()));
			}
			return retval;
		});
		put("configuredfeatures", workspace -> getCustomElements(workspace,
				mu -> mu.getBaseTypesProvided().contains(BaseType.CONFIGUREDFEATURE)));
		put("projectiles_arrow", workspace -> getCustomElementsOfType(workspace, ModElementType.PROJECTILE));
	}};

	public static List<DataListEntry> getAllEntriesFor(Workspace workspace, String datalist) {
		List<DataListEntry> result = new ArrayList<>();

		var provider = customEntryProviders.get(datalist);
		if (provider != null) {
			result.addAll(provider.apply(workspace));
		}

		provider = vanillaEntryProviders.get(datalist);
		if (provider != null) {
			result.addAll(provider.apply(workspace));
		} else {
			for (DataListEntry entry : DataListLoader.loadDataList(datalist)) {
				if (entry.isSupportedInWorkspace(workspace)) {
					result.add(entry);
				}
			}
		}

		result.sort(DataListEntry.getComparator(workspace, result));
		return result;
	}

	public static List<DataListEntry> loadAllAchievements(Workspace workspace) {
		return getAllEntriesFor(workspace, "achievements");
	}

	public static List<DataListEntry> loadAllTabs(Workspace workspace) {
		return getAllEntriesFor(workspace, "tabs");
	}

	public static List<DataListEntry> loadAllBiomes(Workspace workspace) {
		return getAllEntriesFor(workspace, "biomes");
	}

	public static List<DataListEntry> loadAllEnchantments(Workspace workspace) {
		return getAllEntriesFor(workspace, "enchantments");
	}

	public static List<DataListEntry> loadAllStructures(Workspace workspace) {
		return getAllEntriesFor(workspace, "structures");
	}

	public static List<DataListEntry> loadItemUseAnimations(Workspace workspace) {
		return getAllEntriesFor(workspace, "itemuseanimations");
	}

	public static List<DataListEntry> loadAnimations(Workspace workspace) {
		return getAllEntriesFor(workspace, "animations");
	}

	public static List<DataListEntry> loadAllEntities(Workspace workspace) {
		return getAllEntriesFor(workspace, "entities");
	}

	public static List<DataListEntry> loadAllSpawnableEntities(Workspace workspace) {
		return getAllEntriesFor(workspace, "entities_spawnable");
	}

	public static List<DataListEntry> loadCustomEntities(Workspace workspace) {
		return getAllEntriesFor(workspace, "entities_custom");
	}

	public static List<DataListEntry> loadAllParticles(Workspace workspace) {
		return getAllEntriesFor(workspace, "particles");
	}

	public static List<DataListEntry> loadAllPotionEffects(Workspace workspace) {
		return getAllEntriesFor(workspace, "effects");
	}

	public static List<DataListEntry> loadAllVillagerProfessions(Workspace workspace) {
		return getAllEntriesFor(workspace, "villagerprofessions");
	}

	public static List<DataListEntry> loadAllAttributes(Workspace workspace) {
		return getAllEntriesFor(workspace, "attributes");
	}

	public static List<DataListEntry> loadAllFluids(Workspace workspace) {
		return getAllEntriesFor(workspace, "fluids");
	}

	public static List<DataListEntry> loadAllSounds(Workspace workspace) {
		return getAllEntriesFor(workspace, "sounds");
	}

	public static List<DataListEntry> loadAllConfiguredFeatures(Workspace workspace) {
		return getAllEntriesFor(workspace, "configuredfeatures");
	}

	public static List<DataListEntry> loadArrowProjectiles(Workspace workspace) {
		return getAllEntriesFor(workspace, "projectiles_arrow");
	}

	// Allow plugins to alter providers

	public static Map<String, Function<Workspace, Collection<DataListEntry>>> getVanillaEntryProviders() {
		return vanillaEntryProviders;
	}

	public static Map<String, Function<Workspace, Collection<DataListEntry>>> getCustomEntryProviders() {
		return customEntryProviders;
	}

}
