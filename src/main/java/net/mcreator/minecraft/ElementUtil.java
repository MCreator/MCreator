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

package net.mcreator.minecraft;

import net.mcreator.element.BaseType;
import net.mcreator.element.ModElementType;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.SoundElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ElementUtil {

	/**
	 * Loads all mod elements and all Minecraft elements (blocks and items), including elements
	 * that are wildcard elements to subtypes (wood -> oak wood, birch wood, ...)
	 *
	 * @return All Blocks and Items from both Minecraft and custom elements with or without metadata
	 */
	public static List<MCItem> loadBlocksAndItemsAndTags(Workspace workspace) {
		List<MCItem> elements = new ArrayList<>();
		workspace.getModElements().forEach(modElement -> elements.addAll(modElement.getMCItems()));
		elements.addAll(
				DataListLoader.loadDataList("blocksitems").stream().filter(e -> e.isSupportedInWorkspace(workspace))
						.map(e -> (MCItem) e).collect(Collectors.toList()));
		return elements;
	}

	/**
	 * Loads all mod elements and all Minecraft elements (blocks and items) without elements
	 * that are wildcard elements to subtypes (wood -> oak wood, birch wood, ...)
	 * so only oak wood, birch wood, ... are loaded, without wildcard wood element
	 *
	 * @return All Blocks and Items from both Minecraft and custom elements with or without metadata
	 */
	public static List<MCItem> loadBlocksAndItems(Workspace workspace) {
		List<MCItem> elements = new ArrayList<>();
		workspace.getModElements().forEach(modElement -> elements.addAll(modElement.getMCItems()));
		elements.addAll(
				DataListLoader.loadDataList("blocksitems").stream().filter(e -> e.isSupportedInWorkspace(workspace))
						.map(e -> (MCItem) e).filter(MCItem::hasNoSubtypes).collect(Collectors.toList()));
		return elements;
	}

	/**
	 * Loads all mod elements and all Minecraft blocks without elements
	 * that are wildcard elements to subtypes (wood -> oak wood, birch wood, ...)
	 * so only oak wood, birch wood, ... are loaded, without wildcard wood element
	 *
	 * @return All Blocks from both Minecraft and custom elements with or without metadata
	 */
	public static List<MCItem> loadBlocks(Workspace workspace) {
		List<MCItem> elements = new ArrayList<>();
		workspace.getModElements().stream().filter(element -> element.getType().getBaseType() == BaseType.BLOCK)
				.forEach(modElement -> elements.addAll(modElement.getMCItems()));
		elements.addAll(
				DataListLoader.loadDataList("blocksitems").stream().filter(e -> e.isSupportedInWorkspace(workspace))
						.filter(e -> e.getType().equals("block")).map(e -> (MCItem) e).filter(MCItem::hasNoSubtypes)
						.collect(Collectors.toList()));
		return elements;
	}

	public static List<DataListEntry> loadAllAchievements(Workspace workspace) {
		List<DataListEntry> achievements = getCustomElementsOfType(workspace, ModElementType.ACHIEVEMENT);
		achievements.addAll(DataListLoader.loadDataList("achievements"));
		return achievements;
	}

	public static List<DataListEntry> loadAllTabs(Workspace workspace) {
		List<DataListEntry> tabs = getCustomElementsOfType(workspace, BaseType.TAB);
		tabs.addAll(DataListLoader.loadDataList("tabs"));
		return tabs;
	}

	public static List<DataListEntry> loadAllBiomes(Workspace workspace) {
		List<DataListEntry> biomes = getCustomElementsOfType(workspace, BaseType.BIOME);
		biomes.addAll(DataListLoader.loadDataList("biomes"));
		return biomes;
	}

	public static List<DataListEntry> loadAllEnchantments(Workspace workspace) {
		List<DataListEntry> retval = getCustomElementsOfType(workspace, BaseType.ENCHANTMENT);
		retval.addAll(DataListLoader.loadDataList("enchantments"));
		return retval;
	}

	public static List<DataListEntry> loadMaterials() {
		return DataListLoader.loadDataList("materials");
	}

	public static List<DataListEntry> loadEnchantmentTypes() {
		return DataListLoader.loadDataList("enchantmenttypes");
	}

	public static List<DataListEntry> loadAllEntities(Workspace workspace) {
		List<DataListEntry> retval = getCustomElementsOfType(workspace, BaseType.ENTITY);
		retval.addAll(DataListLoader.loadDataList("entities"));
		return retval;
	}

	public static List<DataListEntry> loadAllParticles(Workspace workspace) {
		List<DataListEntry> retval = getCustomElementsOfType(workspace, BaseType.PARTICLE);
		retval.addAll(DataListLoader.loadDataList("particles"));
		return retval;
	}

	public static List<DataListEntry> loadAllPotionEffects(Workspace workspace) {
		List<DataListEntry> retval = getCustomElementsOfType(workspace, BaseType.POTION);
		retval.addAll(DataListLoader.loadDataList("potions"));
		return retval;
	}

	public static List<DataListEntry> getAllBooleanGameRules(Workspace workspace) {
		List<DataListEntry> retval = getCustomElements(workspace, modelement -> {
			if (modelement.getType() == ModElementType.GAMERULE)
				return modelement.getMetadata("type").equals("boolean");
			return false;
		});

		retval.addAll(DataListLoader.loadDataList("gamerules").stream().filter(e -> e.getType().equals("boolean"))
				.collect(Collectors.toList()));
		return retval;
	}

	public static List<DataListEntry> getAllNumberGameRules(Workspace workspace) {
		List<DataListEntry> retval = getCustomElements(workspace, modelement -> {
			if (modelement.getType() == ModElementType.GAMERULE)
				return modelement.getMetadata("type").equals("number");
			return false;
		});

		retval.addAll(DataListLoader.loadDataList("gamerules").stream().filter(e -> e.getType().equals("number"))
				.collect(Collectors.toList()));
		return retval;
	}

	public static String[] loadAllFluids(Workspace workspace) {
		ArrayList<String> retval = new ArrayList<>();

		for (ModElement modElement : workspace.getModElements()) {
			if (modElement.getType() == ModElementType.FLUID) {
				retval.add("CUSTOM:" + modElement.getName());
				retval.add("CUSTOM:" + modElement.getName() + ":Flowing");
			}
		}

		retval.addAll(DataListLoader.loadDataList("fluids").stream().map(DataListEntry::getName)
				.collect(Collectors.toList()));

		return retval.toArray(new String[0]);
	}

	public static String[] getAllSounds(Workspace workspace) {
		ArrayList<String> retval = new ArrayList<>();

		for (SoundElement soundElement : workspace.getSoundElements()) {
			retval.add("CUSTOM:" + soundElement.getName());
		}

		retval.addAll(DataListLoader.loadDataList("sounds").stream().map(DataListEntry::getName)
				.collect(Collectors.toList()));

		return retval.toArray(new String[0]);
	}

	public static String[] getAllDamageSources() {
		return DataListLoader.loadDataList("damagesources").stream().map(DataListEntry::getName).toArray(String[]::new);
	}

	public static String[] getAllGameModes() {
		return DataListLoader.loadDataList("gamemodes").stream().map(DataListEntry::getName).toArray(String[]::new);
	}

	public static List<DataListEntry> loadStepSounds() {
		return DataListLoader.loadDataList("stepsounds");
	}

	public static String[] loadBiomeDictionaryTypes() {
		return DataListLoader.loadDataList("biomedictionarytypes").stream().map(DataListEntry::getName)
				.toArray(String[]::new);
	}

	public static String[] loadDefaultFeatures() {
		return DataListLoader.loadDataList("defaultfeatures").stream().map(DataListEntry::getName)
				.toArray(String[]::new);
	}

	public static String[] loadPathNodeTypes() {
		return DataListLoader.loadDataList("pathnodetypes").stream().map(DataListEntry::getName).toArray(String[]::new);
	}

	public static String[] loadMapColors() {
		return DataListLoader.loadDataList("mapcolors").stream().map(DataListEntry::getName).toArray(String[]::new);
	}

	public static String[] loadAllDimensions(Workspace workspace) {
		ArrayList<String> dimensions = new ArrayList<>();
		dimensions.add("Surface");
		dimensions.add("Nether");
		dimensions.add("End");

		for (ModElement mu : workspace.getModElements())
			if (mu.getType().getBaseType() == BaseType.DIMENSION)
				dimensions.add("CUSTOM:" + mu.getName());

		return dimensions.toArray(new String[0]);
	}

	public static String[] loadDirections() {
		return new String[] { "DOWN", "UP", "NORTH", "SOUTH", "WEST", "EAST" };
	}

	public static ArrayList<String> loadBasicGUI(Workspace workspace) {
		ArrayList<String> blocks = new ArrayList<>();

		for (ModElement mu : workspace.getModElements()) {
			if (mu.getType().getBaseType() == BaseType.GUI)
				blocks.add(mu.getName());
		}

		return blocks;
	}

	private static List<DataListEntry> getCustomElements(@NotNull Workspace workspace,
			Predicate<ModElement> predicate) {
		return workspace.getModElements().stream().filter(predicate).map(DataListEntry.Custom::new)
				.collect(Collectors.toList());
	}

	private static List<DataListEntry> getCustomElementsOfType(@NotNull Workspace workspace, ModElementType type) {
		return workspace.getModElements().stream().filter(mu -> mu.getType() == type).map(DataListEntry.Custom::new)
				.collect(Collectors.toList());
	}

	private static List<DataListEntry> getCustomElementsOfType(@NotNull Workspace workspace, BaseType type) {
		return workspace.getModElements().stream().filter(mu -> mu.getType().getBaseType() == type)
				.map(DataListEntry.Custom::new).collect(Collectors.toList());
	}

}
