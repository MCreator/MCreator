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

package net.mcreator.integration;

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.parts.Particle;
import net.mcreator.element.parts.Procedure;
import net.mcreator.element.parts.*;
import net.mcreator.element.parts.gui.Button;
import net.mcreator.element.parts.gui.Image;
import net.mcreator.element.parts.gui.TextField;
import net.mcreator.element.parts.gui.*;
import net.mcreator.element.types.Dimension;
import net.mcreator.element.types.Enchantment;
import net.mcreator.element.types.Fluid;
import net.mcreator.element.types.*;
import net.mcreator.io.FileIO;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.DataListLoader;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.minecraft.MCItem;
import net.mcreator.ui.modgui.LivingEntityGUI;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.EmptyIcon;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableElement;
import net.mcreator.workspace.elements.VariableElementType;

import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class TestWorkspaceDataProvider {

	private static ModElement me(Workspace workspace, ModElementType type, String sufix) {
		return new ModElement(workspace, "Example" + type.name() + sufix, type);
	}

	public static List<GeneratableElement> getModElementExamplesFor(Workspace workspace, ModElementType type,
			Random random) {
		List<GeneratableElement> generatableElements = new ArrayList<>();

		if (type == ModElementType.RECIPE) {
			generatableElements.add(getRecipeExample(me(workspace, type, "1"), "Crafting", random, true));
			generatableElements.add(getRecipeExample(me(workspace, type, "2"), "Crafting", random, false));
			generatableElements.add(getRecipeExample(me(workspace, type, "3"), "Smelting", random, true));
			generatableElements.add(getRecipeExample(me(workspace, type, "4"), "Blasting", random, true));
			generatableElements.add(getRecipeExample(me(workspace, type, "5"), "Smoking", random, true));
			generatableElements.add(getRecipeExample(me(workspace, type, "6"), "Stone cutting", random, true));
			generatableElements.add(getRecipeExample(me(workspace, type, "7"), "Campfire cooking", random, true));
			generatableElements.add(getRecipeExample(me(workspace, type, "8"), "Smithing", random, true));
			generatableElements.add(getRecipeExample(me(workspace, type, "9"), "Brewing", random, true));
		} else if (type == ModElementType.TOOL) {
			generatableElements.add(getToolExample(me(workspace, type, "1"), "Pickaxe", random, false, false));
			generatableElements.add(getToolExample(me(workspace, type, "2"), "Pickaxe", random, true, false));
			generatableElements.add(getToolExample(me(workspace, type, "3"), "Pickaxe", random, false, true));
			generatableElements.add(getToolExample(me(workspace, type, "4"), "Pickaxe", random, true, true));
			generatableElements.add(getToolExample(me(workspace, type, "5"), "Axe", random, true, false));
			generatableElements.add(getToolExample(me(workspace, type, "6"), "Sword", random, true, false));
			generatableElements.add(getToolExample(me(workspace, type, "7"), "Spade", random, true, false));
			generatableElements.add(getToolExample(me(workspace, type, "8"), "Hoe", random, true, false));
			generatableElements.add(getToolExample(me(workspace, type, "9"), "Special", random, true, false));
			generatableElements.add(getToolExample(me(workspace, type, "10"), "MultiTool", random, true, false));
			generatableElements.add(getToolExample(me(workspace, type, "11"), "Shears", random, true, false));
		} else if (type == ModElementType.FUEL || type == ModElementType.TAB || type == ModElementType.OVERLAY) {
			generatableElements.add(getExampleFor(me(workspace, type, "1"), random, true, true, 0));
			generatableElements.add(getExampleFor(me(workspace, type, "2"), random, true, false, 1));
		} else if (type == ModElementType.COMMAND || type == ModElementType.FUNCTION || type == ModElementType.PAINTING
				|| type == ModElementType.KEYBIND) {
			generatableElements
					.add(getExampleFor(new ModElement(workspace, "Example" + type.name(), type), random, true, true,
							0));
		} else {
			generatableElements.add(getExampleFor(me(workspace, type, "1"), random, true, true, 0));
			generatableElements.add(getExampleFor(me(workspace, type, "2"), random, true, false, 1));
			generatableElements.add(getExampleFor(me(workspace, type, "3"), random, false, true, 2));
			generatableElements.add(getExampleFor(me(workspace, type, "4"), random, false, false, 3));
			generatableElements.add(getExampleFor(me(workspace, type, "5"), random, true, true, 3));
			generatableElements.add(getExampleFor(me(workspace, type, "6"), random, true, false, 2));
			generatableElements.add(getExampleFor(me(workspace, type, "7"), random, false, true, 1));
			generatableElements.add(getExampleFor(me(workspace, type, "8"), random, false, false, 0));
		}

		generatableElements.removeAll(Collections.singleton(null));
		return generatableElements;
	}

	public static void fillWorkspaceWithTestData(Workspace workspace) {
		VariableElement sampleVariable1 = new VariableElement();
		sampleVariable1.setName("test");
		sampleVariable1.setValue("true");
		sampleVariable1.setType(VariableElementType.LOGIC);
		sampleVariable1.setScope(VariableElementType.Scope.GLOBAL_WORLD);
		workspace.addVariableElement(sampleVariable1);

		EmptyIcon.ImageIcon imageIcon = new EmptyIcon.ImageIcon(16, 16);

		if (workspace.getFolderManager().getBlocksTexturesDir() != null) {
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getBlockTextureFile("test"));
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getBlockTextureFile("test2"));
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getBlockTextureFile("test3"));
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getBlockTextureFile("test4"));
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getBlockTextureFile("test5"));
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getBlockTextureFile("test6"));
		}

		if (workspace.getFolderManager().getItemsTexturesDir() != null) {
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getItemTextureFile("test"));
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getItemTextureFile("test2"));
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getItemTextureFile("test3"));
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getItemTextureFile("test4"));
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getItemTextureFile("itest"));

		}

		if (workspace.getFolderManager().getOtherTexturesDir() != null) {
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getOtherTextureFile("test"));
		}

		if (workspace.getFolderManager().getArmorTexturesDir() != null) {
			File[] armorPars = workspace.getFolderManager().getArmorTextureFilesForName("test");
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(), armorPars[0]);
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(), armorPars[1]);
		}
	}

	private static GeneratableElement getExampleFor(ModElement modElement, Random random, boolean _true,
			boolean emptyLists, int valueIndex) {
		switch (modElement.getType()) {
		case ACHIEVEMENT:
			Achievement achievement = new Achievement(modElement);
			achievement.achievementName = "Test Achievement";
			achievement.achievementDescription = "Description of it";
			achievement.achievementIcon = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName());
			achievement.achievementType = new String[] { "task", "goal", "challenge", "challenge" }[valueIndex];
			achievement.parent = new AchievementEntry(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadAllAchievements(modElement.getWorkspace())));
			achievement.announceToChat = _true;
			achievement.showPopup = _true;
			achievement.disableDisplay = !_true;
			achievement.rewardXP = 14;
			achievement.hideIfNotCompleted = !_true;
			achievement.rewardFunction = "No function";
			achievement.background = emptyLists ? "Default" : "test.png";
			achievement.rewardLoot = new ArrayList<>();
			if (!emptyLists) {
				achievement.rewardLoot.add("test1");
				achievement.rewardLoot.add("test2");
			}
			achievement.rewardRecipes = new ArrayList<>();
			if (!emptyLists) {
				achievement.rewardRecipes.add("test1");
				achievement.rewardRecipes.add("test2");
			}
			achievement.triggerxml = "<xml><block type=\"tick\" x=\"40\" y=\"80\"><next>"
					+ "<block type=\"advancement_trigger\" deletable=\"false\"/></next></block></xml>";
			return achievement;
		case FUEL:
			Fuel fuel = new Fuel(modElement);
			fuel.power = new int[] { 0, 100, 1000, 4000 }[valueIndex];
			fuel.block = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName());
			return fuel;
		case BIOME:
			Biome biome = new Biome(modElement);
			biome.name = modElement.getName();
			biome.groundBlock = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocks(modElement.getWorkspace())).getName());
			biome.undergroundBlock = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocks(modElement.getWorkspace())).getName());
			biome.vanillaTreeType = getRandomItem(random,
					new String[] { "Default", "Big trees", "Birch trees", "Savanna trees", "Mega pine trees",
							"Mega spruce trees" });
			biome.airColor = Color.red;
			biome.treeType = _true ? 0 : 1;
			if (!emptyLists) {
				biome.grassColor = Color.green;
				biome.foliageColor = Color.magenta;
				biome.waterColor = Color.blue;
				biome.waterFogColor = Color.cyan;
			}
			biome.ambientSound = new Sound(modElement.getWorkspace(),
					getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			biome.moodSound = new Sound(modElement.getWorkspace(),
					getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			biome.moodSoundDelay = new int[] { 1, 266, 479, 393 }[valueIndex];
			biome.additionsSound = new Sound(modElement.getWorkspace(),
					getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			biome.music = new Sound(modElement.getWorkspace(),
					getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			biome.spawnParticles = _true;
			biome.particleToSpawn = new Particle(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadAllParticles(modElement.getWorkspace())));
			biome.particlesProbability = 0.0123;
			biome.treesPerChunk = new int[] { 0, 5, 10, 16 }[valueIndex];
			biome.grassPerChunk = new int[] { 0, 5, 10, 16 }[valueIndex] + 1;
			biome.seagrassPerChunk = new int[] { 0, 5, 10, 16 }[valueIndex] + 2;
			biome.flowersPerChunk = new int[] { 0, 5, 10, 16 }[valueIndex] + 3;
			biome.mushroomsPerChunk = new int[] { 0, 5, 10, 16 }[valueIndex] + 4;
			biome.bigMushroomsChunk = new int[] { 0, 5, 10, 16 }[valueIndex] + 5;
			biome.sandPatchesPerChunk = new int[] { 0, 5, 10, 16 }[valueIndex] + 6;
			biome.gravelPatchesPerChunk = new int[] { 0, 5, 10, 16 }[valueIndex] + 7;
			biome.reedsPerChunk = new int[] { 0, 5, 10, 16 }[valueIndex] + 8;
			biome.cactiPerChunk = new int[] { 0, 5, 10, 16 }[valueIndex] + 9;
			biome.rainingPossibility = 3.5;
			biome.baseHeight = -0.3;
			biome.maxWaterDepth = 4;
			biome.heightVariation = 0.7;
			biome.temperature = 4.0;
			biome.spawnShipwreck = _true;
			biome.oceanRuinType = getRandomItem(random, new String[] { "NONE", "COLD", "WARM" });
			biome.spawnOceanMonument = !_true;
			biome.spawnWoodlandMansion = _true;
			biome.spawnJungleTemple = !_true;
			biome.spawnDesertPyramid = !_true;
			biome.spawnIgloo = !_true;
			biome.spawnPillagerOutpost = !_true;
			biome.spawnStronghold = _true;
			biome.spawnMineshaft = !_true;
			biome.villageType = getRandomItem(random,
					new String[] { "none", "desert", "plains", "savanna", "snowy", "taiga" });
			biome.biomeWeight = new int[] { 0, 9, 45, 50 }[valueIndex];
			biome.biomeType = getRandomItem(random, new String[] { "WARM", "DESERT", "COOL", "ICY" });
			biome.biomeCategory = getRandomItem(random,
					new String[] { "NONE", "TAIGA", "EXTREME_HILLS", "JUNGLE", "MESA", "PLAINS", "SAVANNA" });

			if (!emptyLists) {
				biome.parent = new BiomeEntry(modElement.getWorkspace(),
						getRandomDataListEntry(random, ElementUtil.loadAllBiomes(modElement.getWorkspace())));
			} else {
				biome.parent = new BiomeEntry(modElement.getWorkspace(), "No parent");
			}

			List<Biome.SpawnEntry> entities = new ArrayList<>();
			if (!emptyLists) {
				Biome.SpawnEntry entry1 = new Biome.SpawnEntry();
				entry1.entity = new EntityEntry(modElement.getWorkspace(),
						getRandomDataListEntry(random, ElementUtil.loadAllEntities(modElement.getWorkspace())));
				entry1.minGroup = 10;
				entry1.minGroup = 134;
				entry1.weight = 13;
				entry1.spawnType = "creature";
				entities.add(entry1);

				Biome.SpawnEntry entry2 = new Biome.SpawnEntry();
				entry2.entity = new EntityEntry(modElement.getWorkspace(),
						getRandomDataListEntry(random, ElementUtil.loadAllEntities(modElement.getWorkspace())));
				entry2.minGroup = 23;
				entry2.minGroup = 145;
				entry2.weight = 11;
				entry2.spawnType = "monster";
				entities.add(entry2);
			}
			biome.spawnEntries = entities;
			biome.minHeight = 2;
			List<String> biomeDictTypes = new ArrayList<>();
			if (!emptyLists) {
				biomeDictTypes.add(getRandomItem(random, ElementUtil.loadBiomeDictionaryTypes()));
				biomeDictTypes.add(getRandomItem(random, ElementUtil.loadBiomeDictionaryTypes()));
				biomeDictTypes.add(getRandomItem(random, ElementUtil.loadBiomeDictionaryTypes()));
				biomeDictTypes.add(getRandomItem(random, ElementUtil.loadBiomeDictionaryTypes()));
				biomeDictTypes.add(getRandomItem(random, ElementUtil.loadBiomeDictionaryTypes()));
				biomeDictTypes.add(getRandomItem(random, ElementUtil.loadBiomeDictionaryTypes()));
				biomeDictTypes.add(getRandomItem(random, ElementUtil.loadBiomeDictionaryTypes()));
			}
			biome.biomeDictionaryTypes = biomeDictTypes;
			List<String> biomeDefaultFeatures = new ArrayList<>();
			if (!emptyLists) {
				biomeDefaultFeatures.add(getRandomItem(random, ElementUtil.loadDefaultFeatures()));
				biomeDefaultFeatures.add(getRandomItem(random, ElementUtil.loadDefaultFeatures()));
				biomeDefaultFeatures.add(getRandomItem(random, ElementUtil.loadDefaultFeatures()));
				biomeDefaultFeatures.add(getRandomItem(random, ElementUtil.loadDefaultFeatures()));
				biomeDefaultFeatures.add(getRandomItem(random, ElementUtil.loadDefaultFeatures()));
				biomeDefaultFeatures.add(getRandomItem(random, ElementUtil.loadDefaultFeatures()));
				biomeDefaultFeatures.add(getRandomItem(random, ElementUtil.loadDefaultFeatures()));
				biomeDefaultFeatures.add(getRandomItem(random, ElementUtil.loadDefaultFeatures()));
				biomeDefaultFeatures.add(getRandomItem(random, ElementUtil.loadDefaultFeatures()));
				biomeDefaultFeatures.add(getRandomItem(random, ElementUtil.loadDefaultFeatures()));
				biomeDefaultFeatures.add(getRandomItem(random, ElementUtil.loadDefaultFeatures()));
				biomeDefaultFeatures.add(getRandomItem(random, ElementUtil.loadDefaultFeatures()));
				biomeDefaultFeatures.add(getRandomItem(random, ElementUtil.loadDefaultFeatures()));
			}
			biome.defaultFeatures = biomeDefaultFeatures;
			biome.treeVines = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocks(modElement.getWorkspace())).getName());
			biome.treeStem = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocks(modElement.getWorkspace())).getName());
			biome.treeBranch = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocks(modElement.getWorkspace())).getName());
			biome.treeFruits = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocks(modElement.getWorkspace())).getName());
			biome.spawnBiome = !_true;
			return biome;
		case FLUID:
			Fluid fluid = new Fluid(modElement);
			fluid.name = modElement.getName();
			fluid.textureFlowing = "test";
			fluid.textureStill = "test2";
			fluid.luminosity = 3;
			fluid.density = 5;
			fluid.viscosity = 10;
			fluid.isGas = _true;
			fluid.generateBucket = !_true;
			fluid.creativeTab = new TabEntry(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadAllTabs(modElement.getWorkspace())));
			fluid.onBlockAdded = new Procedure("procedure1");
			fluid.onNeighbourChanges = new Procedure("procedure2");
			fluid.onTickUpdate = new Procedure("procedure3");
			fluid.onEntityCollides = new Procedure("procedure4");
			fluid.type = _true ? "WATER" : "LAVA";
			fluid.spawnWorldTypes = new ArrayList<>(Arrays.asList("Nether", "End"));
			fluid.restrictionBiomes = new ArrayList<>();
			if (!emptyLists) {
				fluid.restrictionBiomes.add(new BiomeEntry(modElement.getWorkspace(),
						getRandomDataListEntry(random, ElementUtil.loadAllBiomes(modElement.getWorkspace()))));
				fluid.restrictionBiomes.add(new BiomeEntry(modElement.getWorkspace(),
						getRandomDataListEntry(random, ElementUtil.loadAllBiomes(modElement.getWorkspace()))));
				fluid.restrictionBiomes.add(new BiomeEntry(modElement.getWorkspace(),
						getRandomDataListEntry(random, ElementUtil.loadAllBiomes(modElement.getWorkspace()))));
				fluid.restrictionBiomes.add(new BiomeEntry(modElement.getWorkspace(),
						getRandomDataListEntry(random, ElementUtil.loadAllBiomes(modElement.getWorkspace()))));
			}
			fluid.frequencyOnChunks = 13;
			fluid.generateCondition = emptyLists ? null : new Procedure("condition1");
			return fluid;
		case FOOD:
			Food food = new Food(modElement);
			food.name = modElement.getName();
			food.rarity = getRandomString(random, Arrays.asList("COMMON", "UNCOMMON", "RARE", "EPIC"));
			food.specialInfo = new ArrayList<>();
			if (!emptyLists) {
				food.specialInfo = StringUtils
						.splitCommaSeparatedStringListWithEscapes("info 1, info 2, test \\, is this, another one");
			} else {
				food.specialInfo = new ArrayList<>();
			}
			food.texture = "test";
			food.creativeTab = new TabEntry(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadAllTabs(modElement.getWorkspace())));
			food.resultItem = new MItemBlock(modElement.getWorkspace(), emptyLists ?
					"" :
					getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName());
			food.stackSize = 32;
			food.eatingSpeed = 123;
			food.nutritionalValue = 5;
			food.saturation = 0.8f;
			food.forDogs = _true;
			food.isAlwaysEdible = _true;
			food.animation = getRandomItem(random,
					new String[] { "block", "bow", "crossbow", "drink", "eat", "none", "spear" });
			food.hasGlow = _true;
			food.onRightClicked = new Procedure("procedure1");
			food.onRightClickedOnBlock = new Procedure("procedure2");
			food.onEaten = new Procedure("procedure3");
			food.onEntityHitWith = new Procedure("procedure4");
			food.onItemInInventoryTick = new Procedure("procedure5");
			food.onItemInUseTick = new Procedure("procedure6");
			food.onCrafted = new Procedure("procedure7");
			food.onEntitySwing = new Procedure("procedure8");
			food.onDroppedByPlayer = new Procedure("procedure9");
			food.renderType = 0;
			food.customModelName = "Normal";
			return food;
		case COMMAND:
			Command command = new Command(modElement);
			command.commandName = modElement.getName();
			command.permissionLevel = getRandomItem(random, new String[] { "No requirement", "1", "2", "3", "4" });
			command.onCommandExecuted = new Procedure("procedure2");
			return command;
		case KEYBIND:
			KeyBinding keyBinding = new KeyBinding(modElement);
			keyBinding.triggerKey = getRandomItem(random,
					DataListLoader.loadDataList("keybuttons").stream().map(DataListEntry::getName)
							.toArray(String[]::new));
			keyBinding.keyBindingName = modElement.getName();
			keyBinding.keyBindingCategoryKey = "key.categories.misc";
			keyBinding.onKeyPressed = new Procedure("procedure3");
			keyBinding.onKeyReleased = new Procedure("procedure2");
			return keyBinding;
		case TAB:
			Tab tab = new Tab(modElement);
			tab.name = modElement.getName();
			tab.icon = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName());
			tab.showSearch = _true;
			return tab;
		case OVERLAY:
			Overlay overlay = new Overlay(modElement);
			overlay.priority = getRandomItem(random, new String[] { "NORMAL", "HIGH", "HIGHEST", "LOW", "LOWEST" });
			ArrayList<GUIComponent> components = new ArrayList<>();
			if (!emptyLists) {
				components.add(new net.mcreator.element.parts.gui.Label("text", 100, 150, "text", Color.red,
						new Procedure("condition1")));
				components.add(new net.mcreator.element.parts.gui.Label("text2", 100, 150, "text2", Color.white,
						new Procedure("condition4")));
				components
						.add(new net.mcreator.element.parts.gui.Label("text3 <VAR:test>", 100, 150, "text3 <VAR:test>",
								Color.black, new Procedure("condition1")));
				components.add(new Image("picture1", 20, 30, "pricture1", true, new Procedure("condition1")));
				components.add(new Image("picture2", 22, 31, "pricture2", false, new Procedure("condition2")));
			}
			overlay.displayCondition = new Procedure("condition1");
			overlay.components = components;
			overlay.baseTexture = "test.png";
			return overlay;
		case GUI:
			GUI gui = new GUI(modElement);
			gui.type = new int[] { 0, 0, 1, 1 }[valueIndex];
			gui.width = new int[] { 600, 400, 352, 500 }[valueIndex];
			gui.height = new int[] { 500, 400, 353, 450 }[valueIndex];
			gui.renderBgLayer = !_true;
			gui.doesPauseGame = _true;
			gui.inventoryOffsetX = 20;
			gui.inventoryOffsetY = 123;
			if (!emptyLists) {
				gui.onOpen = new Procedure("procedure12");
				gui.onTick = new Procedure("procedure7");
				gui.onClosed = new Procedure("procedure10");
			}
			components = new ArrayList<>();
			if (!emptyLists) {
				components.add(new net.mcreator.element.parts.gui.Label("text", 100, 150, "text", Color.red,
						new Procedure("condition2")));
				components.add(new net.mcreator.element.parts.gui.Label("text2", 100, 150, "text2", Color.white,
						new Procedure("condition1")));
				components
						.add(new net.mcreator.element.parts.gui.Label("text3 <VAR:test>", 100, 150, "text3 <VAR:test>",
								Color.black, new Procedure("condition4")));
				components.add(new Image("picture1", 20, 30, "picture1", true, new Procedure("condition1")));
				components.add(new Image("picture2", 22, 31, "picture2", false, new Procedure("condition2")));
				components.add(new Button("button1", 10, 10, "button1", 100, 200, new Procedure("procedure1")));
				components.add(new Button("button2", 10, 10, "button2", 100, 200, null));
				components.add(new InputSlot(0, "slot1", 20, 30, Color.red, _true, _true, new Procedure("procedure3"),
						new Procedure("procedure1"), new Procedure("procedure2"),
						new MItemBlock(modElement.getWorkspace(), "")));
				components
						.add(new InputSlot(4, "slot2", 20, 30, Color.white, !_true, !_true, new Procedure("procedure4"),
								null, null, new MItemBlock(modElement.getWorkspace(),
								getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace()))
										.getName())));
				components.add(new OutputSlot(5, "slot out", 10, 20, Color.black, !_true, _true,
						new Procedure("procedure1"), new Procedure("procedure2"), new Procedure("procedure3")));
				components.add(new OutputSlot(6, "sot", 243, 563, Color.black, _true, _true, null, null, null));
				components.add(new TextField("text1", 0, 10, 100, 20, "Input value ..."));
				components.add(new TextField("text2", 55, 231, 90, 20, ""));
			}
			gui.components = components;
			return gui;
		case MOB:
			Mob mob = new Mob(modElement);
			mob.mobName = modElement.getName();
			mob.mobLabel = "mod label " + StringUtils.machineToReadableName(modElement.getName());
			if (emptyLists) {
				mob.mobModelTexture = "test.png";
			} else {
				mob.mobModelTexture = "";
				mob.entityTextureSelector = new Procedure("textureSelector1");
			}
			mob.mobModelGlowTexture = emptyLists ? "" : "test.png";
			mob.mobModelName = getRandomItem(random, LivingEntityGUI.builtinmobmodels).getReadableName();
			mob.spawnEggBaseColor = Color.red;
			mob.spawnEggDotColor = Color.green;
			mob.isBoss = _true;
			mob.creativeTab = new TabEntry(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadAllTabs(modElement.getWorkspace())));
			mob.bossBarColor = getRandomItem(random,
					new String[] { "PINK", "BLUE", "RED", "GREEN", "YELLOW", "PURPLE", "WHITE" });
			mob.bossBarType = getRandomItem(random,
					new String[] { "PROGRESS", "NOTCHED_6", "NOTCHED_10", "NOTCHED_12", "NOTCHED_20" });
			mob.equipmentMainHand = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName());
			mob.equipmentOffHand = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName());
			mob.equipmentHelmet = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName());
			mob.equipmentBody = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName());
			mob.equipmentLeggings = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName());
			mob.equipmentBoots = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName());
			mob.mobBehaviourType = _true ? "Creature" : "Mob";
			mob.mobCreatureType = getRandomItem(random,
					new String[] { "UNDEFINED", "UNDEAD", "ARTHROPOD", "ILLAGER", "WATER" });
			mob.attackStrength = 4;
			mob.attackKnockback = 1.5;
			mob.knockbackResistance = 0.5;
			mob.movementSpeed = 0.76;
			mob.armorBaseValue = 0.123;
			mob.health = 42;
			mob.trackingRange = 27;
			mob.waterMob = !_true;
			mob.flyingMob = !_true;
			mob.inventorySize = 10;
			mob.inventoryStackSize = 42;
			mob.disableCollisions = !_true;
			mob.immuneToFire = _true;
			mob.immuneToArrows = !_true;
			mob.immuneToFallDamage = !_true;
			mob.immuneToCactus = !_true;
			mob.immuneToDrowning = !_true;
			mob.immuneToLightning = !_true;
			mob.immuneToPotions = !_true;
			mob.immuneToPlayer = !_true;
			mob.immuneToExplosion = !_true;
			mob.immuneToTrident = !_true;
			mob.immuneToAnvil = !_true;
			mob.immuneToDragonBreath = !_true;
			mob.immuneToWither = !_true;
			mob.hasSpawnEgg = !_true;
			mob.xpAmount = 8;
			mob.ridable = _true;
			mob.canControlStrafe = !_true;
			mob.canControlForward = _true;
			mob.guiBoundTo = "<NONE>";
			mob.mobDrop = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName());
			mob.livingSound = new Sound(modElement.getWorkspace(),
					getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			mob.hurtSound = new Sound(modElement.getWorkspace(),
					getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			mob.deathSound = new Sound(modElement.getWorkspace(),
					getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			if (!emptyLists) {
				mob.stepSound = new Sound(modElement.getWorkspace(),
						getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			} else {
				mob.stepSound = new Sound(modElement.getWorkspace(), "");
			}
			mob.spawnParticles = _true;
			mob.particleToSpawn = new Particle(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadAllParticles(modElement.getWorkspace())));
			mob.particleSpawningShape = new String[] { "Spread", "Top", "Tube", "Plane" }[valueIndex];
			mob.rangedItemType = "Default item";
			mob.particleSpawningRadious = 4;
			mob.particleAmount = 13;
			if (!emptyLists) {
				mob.particleCondition = new Procedure("condition2");
				mob.spawningCondition = new Procedure("condition3");
				mob.onStruckByLightning = new Procedure("procedure1");
				mob.whenMobFalls = new Procedure("procedure2");
				mob.whenMobDies = new Procedure("procedure3");
				mob.whenMobIsHurt = new Procedure("procedure4");
				mob.onRightClickedOn = new Procedure("procedure5");
				mob.whenThisMobKillsAnother = new Procedure("procedure6");
				mob.onMobTickUpdate = new Procedure("procedure7");
				mob.onPlayerCollidesWith = new Procedure("procedure8");
				mob.onInitialSpawn = new Procedure("procedure9");
			}
			mob.hasAI = _true;
			mob.aiBase = "(none)";
			if (!emptyLists) {
				Set<String> aiTasks = modElement.getGeneratorStats().getGeneratorAITasks();
				if (aiTasks.contains("wander") && aiTasks.contains("look_around") && aiTasks
						.contains("panic_when_attacked") && aiTasks.contains("attack_action")) {
					mob.aixml = "<xml><block type=\"aitasks_container\" deletable=\"!_true\">"
							+ "<next><block type=\"wander\"><field name=\"speed\">1</field>"
							+ "<next><block type=\"look_around\"><next><block type=\"swim_in_water\">"
							+ "<next><block type=\"panic_when_attacked\"><field name=\"speed\">1.2</field>"
							+ "<next><block type=\"attack_action\"><field name=\"callhelp\">!_true</field>"
							+ "</block></next></block></next></block></next></block></next></block></next></block></xml>";
				}
			}
			// fallback
			if (mob.aixml == null) {
				mob.aixml = "<xml><block type=\"aitasks_container\" deletable=\"!_true\"></block></xml>";
			}
			mob.breedable = _true;
			mob.tameable = _true;
			mob.breedTriggerItems = new ArrayList<>();
			if (!emptyLists) {
				mob.breedTriggerItems.add(new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName()));
				mob.breedTriggerItems.add(new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName()));
				mob.breedTriggerItems.add(new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName()));
				mob.breedTriggerItems.add(new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName()));
				mob.breedTriggerItems.add(new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName()));
			}
			mob.ranged = _true;
			mob.rangedAttackItem = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName());
			mob.spawnThisMob = !_true;
			mob.doesDespawnWhenIdle = _true;
			mob.spawningProbability = 23;
			mob.mobSpawningType = new String[] { "monster", "creature", "ambient", "waterCreature" }[valueIndex];
			mob.minNumberOfMobsPerGroup = 4;
			mob.maxNumberOfMobsPerGroup = 40;
			mob.restrictionBiomes = new ArrayList<>();
			if (!emptyLists) {
				mob.restrictionBiomes.add(new BiomeEntry(modElement.getWorkspace(),
						getRandomDataListEntry(random, ElementUtil.loadAllBiomes(modElement.getWorkspace()))));
				mob.restrictionBiomes.add(new BiomeEntry(modElement.getWorkspace(),
						getRandomDataListEntry(random, ElementUtil.loadAllBiomes(modElement.getWorkspace()))));
				mob.restrictionBiomes.add(new BiomeEntry(modElement.getWorkspace(),
						getRandomDataListEntry(random, ElementUtil.loadAllBiomes(modElement.getWorkspace()))));
				mob.restrictionBiomes.add(new BiomeEntry(modElement.getWorkspace(),
						getRandomDataListEntry(random, ElementUtil.loadAllBiomes(modElement.getWorkspace()))));
			}
			mob.spawnInDungeons = _true;
			mob.modelWidth = 0.4;
			mob.modelHeight = 1.3;
			mob.mountedYOffset = -3.1;
			mob.modelShadowSize = 1.8;
			return mob;
		case DIMENSION:
			Dimension dimension = new Dimension(modElement);
			dimension.texture = "test";
			dimension.portalTexture = "test2";
			dimension.portalParticles = new Particle(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadAllParticles(modElement.getWorkspace())));
			dimension.igniterTab = new TabEntry(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadAllTabs(modElement.getWorkspace())));
			dimension.portalSound = new Sound(modElement.getWorkspace(),
					getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			dimension.biomesInDimension = new ArrayList<>();
			dimension.biomesInDimension.add(new BiomeEntry(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadAllBiomes(modElement.getWorkspace()))));
			dimension.biomesInDimension.add(new BiomeEntry(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadAllBiomes(modElement.getWorkspace()))));
			dimension.biomesInDimension.add(new BiomeEntry(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadAllBiomes(modElement.getWorkspace()))));
			dimension.biomesInDimension.add(new BiomeEntry(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadAllBiomes(modElement.getWorkspace()))));
			dimension.airColor = Color.cyan;
			dimension.canRespawnHere = _true;
			dimension.hasFog = _true;
			dimension.hasSkyLight = !_true;
			dimension.imitateOverworldBehaviour = _true;
			dimension.isDark = _true;
			dimension.doesWaterVaporize = !_true;
			dimension.enablePortal = true; // we always want it as it can be referenced in other tests
			dimension.portalLuminance = 8;
			dimension.portalFrame = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocks(modElement.getWorkspace())).getName());
			dimension.igniterName = modElement.getName();
			dimension.worldGenType = new String[] { "Nether like gen", "Normal world gen", "End like gen",
					"Normal world gen" }[valueIndex];
			dimension.mainFillerBlock = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocks(modElement.getWorkspace())).getName());
			dimension.fluidBlock = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocks(modElement.getWorkspace())).getName());
			dimension.whenPortaTriggerlUsed = new Procedure("procedure1");
			dimension.onPortalTickUpdate = new Procedure("procedure3");
			dimension.onPlayerEntersDimension = new Procedure("procedure4");
			dimension.onPlayerLeavesDimension = new Procedure("procedure5");
			dimension.portalMakeCondition = new Procedure("condition3");
			dimension.portalUseCondition = new Procedure("condition4");
			return dimension;
		case STRUCTURE:
			Structure structure = new Structure(modElement);
			structure.spawnProbability = 310000;
			structure.minCountPerChunk = 1;
			structure.maxCountPerChunk = 3;
			structure.spawnHeightOffset = new int[] { 0, -3, 10, -10 }[valueIndex];
			structure.spawnXOffset = new int[] { 0, -3, 10, -10 }[valueIndex];
			structure.spawnZOffset = new int[] { 0, -3, 10, -10 }[valueIndex];
			structure.spawnWorldTypes = new ArrayList<>(Arrays.asList("Nether", "Surface", "End"));
			structure.spawnLocation = getRandomString(random, Arrays.asList("Ground", "Air", "Underground"));
			structure.surfaceDetectionType = getRandomString(random,
					Arrays.asList("First motion blocking block", "First block"));
			structure.ignoreBlocks = getRandomString(random,
					Arrays.asList("STRUCTURE_BLOCK", "AIR_AND_STRUCTURE_BLOCK", "AIR"));
			structure.restrictionBlocks = new ArrayList<>();
			structure.restrictionBiomes = new ArrayList<>();
			if (!emptyLists) {
				structure.restrictionBlocks.add(new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, ElementUtil.loadBlocks(modElement.getWorkspace())).getName()));
				structure.restrictionBlocks.add(new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, ElementUtil.loadBlocks(modElement.getWorkspace())).getName()));
				structure.restrictionBlocks.add(new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, ElementUtil.loadBlocks(modElement.getWorkspace())).getName()));
				structure.restrictionBlocks.add(new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, ElementUtil.loadBlocks(modElement.getWorkspace())).getName()));
				structure.restrictionBiomes.add(new BiomeEntry(modElement.getWorkspace(),
						getRandomDataListEntry(random, ElementUtil.loadAllBiomes(modElement.getWorkspace()))));
				structure.restrictionBiomes.add(new BiomeEntry(modElement.getWorkspace(),
						getRandomDataListEntry(random, ElementUtil.loadAllBiomes(modElement.getWorkspace()))));
				structure.restrictionBiomes.add(new BiomeEntry(modElement.getWorkspace(),
						getRandomDataListEntry(random, ElementUtil.loadAllBiomes(modElement.getWorkspace()))));
				structure.restrictionBiomes.add(new BiomeEntry(modElement.getWorkspace(),
						getRandomDataListEntry(random, ElementUtil.loadAllBiomes(modElement.getWorkspace()))));
			}
			if (_true) {
				structure.generateCondition = new Procedure("condition1");
				structure.onStructureGenerated = new Procedure("procedure3");
			}
			structure.structure = null;
			return structure;
		case ARMOR:
			Armor armor = new Armor(modElement);
			armor.enableHelmet = !_true;
			armor.textureHelmet = "test";
			armor.helmetModelTexture = emptyLists ? "From armor" : "test.png";
			armor.enableBody = !_true;
			armor.textureBody = "test2";
			armor.bodyModelTexture = emptyLists ? "From armor" : "test.png";
			armor.enableLeggings = !_true;
			armor.textureLeggings = "test3";
			armor.leggingsModelTexture = emptyLists ? "From armor" : "test.png";
			armor.enableBoots = !_true;
			armor.textureBoots = "test4";
			armor.bootsModelTexture = emptyLists ? "From armor" : "test.png";
			if (!emptyLists) {
				armor.helmetSpecialInfo = StringUtils
						.splitCommaSeparatedStringListWithEscapes("info 1, info 2, test \\, is this, another one");
				armor.bodySpecialInfo = StringUtils
						.splitCommaSeparatedStringListWithEscapes("info 1, info 2, test \\, is this, another one");
				armor.leggingsSpecialInfo = StringUtils
						.splitCommaSeparatedStringListWithEscapes("info 1, info 2, test \\, is this, another one");
				armor.bootsSpecialInfo = StringUtils
						.splitCommaSeparatedStringListWithEscapes("info 1, info 2, test \\, is this, another one");
			} else {
				armor.helmetSpecialInfo = new ArrayList<>();
				armor.bodySpecialInfo = new ArrayList<>();
				armor.leggingsSpecialInfo = new ArrayList<>();
				armor.bootsSpecialInfo = new ArrayList<>();
			}
			armor.equipSound = new Sound(modElement.getWorkspace(),
					getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			armor.onHelmetTick = new Procedure("procedure1");
			armor.onBodyTick = new Procedure("procedure2");
			armor.onLeggingsTick = new Procedure("procedure3");
			armor.onBootsTick = new Procedure("procedure4");
			armor.helmetName = modElement.getName() + " appendix1";
			armor.bodyName = modElement.getName() + " appendix2";
			armor.bootsName = modElement.getName() + " appendix3";
			armor.leggingsName = modElement.getName() + " appendix4";
			armor.creativeTab = new TabEntry(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadAllTabs(modElement.getWorkspace())));
			armor.armorTextureFile = "test";
			armor.maxDamage = 12;
			armor.damageValueHelmet = 3;
			armor.damageValueBody = 4;
			armor.damageValueLeggings = 5;
			armor.damageValueBoots = 6;
			armor.enchantability = 7;
			armor.toughness = 1.23;
			armor.knockbackResistance = 3.148;
			armor.repairItems = new ArrayList<>();
			if (!emptyLists) {
				armor.repairItems.add(new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName()));
				armor.repairItems.add(new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName()));
				armor.repairItems.add(new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName()));
				armor.repairItems.add(new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName()));
				armor.repairItems.add(new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName()));
			}
			return armor;
		case PLANT:
			Plant plant = new Plant(modElement);
			plant.name = modElement.getName();
			plant.spawnWorldTypes = new ArrayList<>();
			plant.creativeTab = new TabEntry(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadAllTabs(modElement.getWorkspace())));
			plant.texture = "test";
			plant.textureBottom = "test2";
			plant.itemTexture = emptyLists ? "" : "itest";
			plant.particleTexture = emptyLists ? "" : "test3";
			plant.plantType = new String[] { "normal", "growapable", "double", "normal" }[valueIndex];
			plant.growapableSpawnType = getRandomItem(random,
					new String[] { "Plains", "Desert", "Beach", "Cave", "Water", "Nether", "Crop" });
			plant.staticPlantGenerationType = getRandomItem(random, new String[] { "Grass", "Flower" });
			plant.doublePlantGenerationType = getRandomItem(random, new String[] { "Grass", "Flower" });
			plant.growapableMaxHeight = 5;
			plant.hardness = 0.03;
			plant.emissiveRendering = !_true;
			plant.resistance = 3;
			plant.luminance = 3;
			plant.isReplaceable = !_true;
			plant.forceTicking = !_true;
			plant.hasTileEntity = !_true;
			plant.specialInfo = new ArrayList<>();
			if (!emptyLists) {
				plant.specialInfo = StringUtils
						.splitCommaSeparatedStringListWithEscapes("info 1, info 2, test \\, is this, another one");
			} else {
				plant.specialInfo = new ArrayList<>();
			}
			plant.creativePickItem = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocks(modElement.getWorkspace())).getName());
			plant.colorOnMap = getRandomItem(random, ElementUtil.loadMapColors());
			plant.offsetType = getRandomString(random, Arrays.asList("NONE", "XZ", "XYZ"));
			plant.aiPathNodeType = getRandomItem(random, ElementUtil.loadPathNodeTypes());
			plant.unbreakable = _true;
			plant.soundOnStep = new StepSound(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadStepSounds()));
			plant.customDrop = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName());
			plant.dropAmount = 4;
			plant.useLootTableForDrops = !_true;
			plant.frequencyOnChunks = 13;
			plant.flammability = 5;
			plant.fireSpreadSpeed = 12;
			plant.restrictionBiomes = new ArrayList<>();
			if (!emptyLists) {
				plant.restrictionBiomes.add(new BiomeEntry(modElement.getWorkspace(),
						getRandomDataListEntry(random, ElementUtil.loadAllBiomes(modElement.getWorkspace()))));
				plant.restrictionBiomes.add(new BiomeEntry(modElement.getWorkspace(),
						getRandomDataListEntry(random, ElementUtil.loadAllBiomes(modElement.getWorkspace()))));
				plant.restrictionBiomes.add(new BiomeEntry(modElement.getWorkspace(),
						getRandomDataListEntry(random, ElementUtil.loadAllBiomes(modElement.getWorkspace()))));
				plant.restrictionBiomes.add(new BiomeEntry(modElement.getWorkspace(),
						getRandomDataListEntry(random, ElementUtil.loadAllBiomes(modElement.getWorkspace()))));
			}
			plant.onNeighbourBlockChanges = new Procedure("procedure1");
			plant.onTickUpdate = new Procedure("procedure2");
			plant.onDestroyedByPlayer = new Procedure("procedure3");
			plant.onDestroyedByExplosion = new Procedure("procedure4");
			plant.onStartToDestroy = new Procedure("procedure5");
			plant.onEntityCollides = new Procedure("procedure6");
			plant.onRightClicked = new Procedure("procedure7");
			plant.onBlockAdded = new Procedure("procedure8");
			plant.onBlockPlacedBy = new Procedure("procedure9");
			plant.onRandomUpdateEvent = new Procedure("procedure10");
			plant.generateCondition = emptyLists ? null : new Procedure("condition1");
			plant.tintType = getRandomString(random, Arrays.asList("No tint", "Grass", "Foliage", "Water"));
			plant.renderType = new int[] { 13, !"No tint".equals(plant.tintType) ? 120 : 12, 13,
					!"No tint".equals(plant.tintType) ? 120 : 12 }[valueIndex];
			plant.customModelName = new String[] { "Crop model", "Cross model", "Crop model",
					"Cross model" }[valueIndex];
			plant.isItemTinted = _true;
			return plant;
		case ITEM:
			Item item = new Item(modElement);
			item.name = modElement.getName();
			item.rarity = getRandomString(random, Arrays.asList("COMMON", "UNCOMMON", "RARE", "EPIC"));
			item.creativeTab = new TabEntry(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadAllTabs(modElement.getWorkspace())));
			item.stackSize = 52;
			item.enchantability = 3;
			item.useDuration = 8;
			item.toolType = 1.4;
			item.damageCount = 4;
			item.destroyAnyBlock = _true;
			item.inventorySize = 10;
			item.inventoryStackSize = 42;
			item.guiBoundTo = "<NONE>";
			item.recipeRemainder = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName());
			item.stayInGridWhenCrafting = _true;
			item.damageOnCrafting = _true;
			item.immuneToFire = _true;
			item.hasGlow = _true;
			item.onRightClickedInAir = new Procedure("procedure1");
			item.onRightClickedOnBlock = new Procedure("procedure2");
			item.onCrafted = new Procedure("procedure3");
			item.onEntityHitWith = new Procedure("procedure4");
			item.onItemInInventoryTick = new Procedure("procedure5");
			item.onItemInUseTick = new Procedure("procedure6");
			item.onStoppedUsing = new Procedure("procedure7");
			item.onEntitySwing = new Procedure("procedure8");
			item.onDroppedByPlayer = new Procedure("procedure9");
			item.enableMeleeDamage = !_true;
			item.damageVsEntity = 3;
			if (!emptyLists) {
				item.specialInfo = StringUtils
						.splitCommaSeparatedStringListWithEscapes("info 1, info 2, test \\, is this, another one");
			} else {
				item.specialInfo = new ArrayList<>();
			}
			item.texture = "test2";
			item.renderType = 0;
			item.customModelName = "Normal";
			return item;
		case RANGEDITEM:
			RangedItem rangedItem = new RangedItem(modElement);
			rangedItem.name = modElement.getName();
			rangedItem.creativeTab = new TabEntry(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadAllTabs(modElement.getWorkspace())));
			rangedItem.ammoItem = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName());
			rangedItem.specialInfo = new ArrayList<>();
			if (!emptyLists) {
				rangedItem.specialInfo = StringUtils
						.splitCommaSeparatedStringListWithEscapes("info 1, info 2, test \\, is this, another one");
			} else {
				rangedItem.specialInfo = new ArrayList<>();
			}
			rangedItem.animation = getRandomItem(random,
					new String[] { "block", "bow", "crossbow", "drink", "eat", "none", "spear" });
			rangedItem.shootConstantly = _true;
			rangedItem.usageCount = 67;
			rangedItem.stackSize = 41;
			rangedItem.actionSound = new Sound(modElement.getWorkspace(),
					getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			rangedItem.bulletPower = 1.5;
			rangedItem.bulletDamage = 2.3;
			rangedItem.bulletKnockback = 5;
			rangedItem.bulletParticles = _true;
			rangedItem.bulletIgnitesFire = _true;
			rangedItem.bulletItemTexture = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName());
			rangedItem.onBulletHitsBlock = new Procedure("procedure1");
			rangedItem.onBulletHitsPlayer = new Procedure("procedure2");
			rangedItem.onBulletFlyingTick = new Procedure("procedure3");
			rangedItem.onRangedItemUsed = new Procedure("procedure4");
			rangedItem.useCondition = new Procedure("condition1");
			rangedItem.onEntitySwing = new Procedure("procedure8");
			rangedItem.bulletModel = "Default";
			rangedItem.customBulletModelTexture = "";
			rangedItem.texture = "test3";
			rangedItem.renderType = 0;
			rangedItem.customModelName = "Normal";
			rangedItem.hasGlow = _true;
			rangedItem.enableMeleeDamage = !_true;
			rangedItem.damageVsEntity = 2;
			return rangedItem;
		case POTION:
			Potion potion = new Potion(modElement);
			potion.name = modElement.getName();
			potion.effectName = modElement.getName() + " Effect Name";
			potion.color = Color.magenta;
			potion.icon = "test.png";
			potion.isInstant = !_true;
			potion.isBad = _true;
			potion.isBenefitical = !_true;
			potion.renderStatusInHUD = _true;
			potion.renderStatusInInventory = _true;
			potion.registerPotionType = _true;
			potion.onStarted = new Procedure("procedure1");
			potion.onActiveTick = new Procedure("procedure2");
			potion.onExpired = new Procedure("procedure3");
			return potion;
		case BLOCK:
			Block block = new Block(modElement);
			block.name = modElement.getName();
			block.hasTransparency = new boolean[] { _true, _true, true,
					_true }[valueIndex]; // third is true because third index for model is cross which requires transparency
			block.connectedSides = _true;
			block.displayFluidOverlay = _true;
			block.emissiveRendering = _true;
			block.transparencyType = new String[] { "SOLID", "CUTOUT", "CUTOUT_MIPPED", "TRANSLUCENT" }[valueIndex];
			block.mx = 0.1;
			block.my = 0.1;
			block.mz = 0.1;
			block.Mx = 0.9;
			block.My = 0.9;
			block.Mz = 0.9;
			block.rotationMode = new int[] { 0, 1, 4, 5 }[valueIndex];
			block.hardness = 2.3;
			block.resistance = 3.1;
			block.hasGravity = _true;
			block.useLootTableForDrops = !_true;
			block.creativeTab = new TabEntry(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadAllTabs(modElement.getWorkspace())));
			block.destroyTool = getRandomItem(random,
					new String[] { "Not specified", "pickaxe", "axe", "shovel", "hoe" });
			block.customDrop = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName());
			block.flammability = 5;
			block.fireSpreadSpeed = 12;
			block.dropAmount = 3;
			block.plantsGrowOn = _true;
			block.isNotColidable = _true;
			block.canProvidePower = _true;
			block.isWaterloggable = !block.hasGravity; // only works if block has no gravity, emptyLists for more randomness
			block.isLadder = _true;
			block.enchantPowerBonus = 1.2342;
			block.reactionToPushing = getRandomItem(random,
					new String[] { "NORMAL", "DESTROY", "BLOCK", "PUSH_ONLY", "IGNORE" });
			block.slipperiness = 12.342;
			block.lightOpacity = new int[] { 123, 25, 0,
					35 }[valueIndex]; // third is 0 because third index for model is cross which requires transparency;
			block.material = new Material(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadMaterials()));
			block.tickRate = 24;
			block.soundOnStep = new StepSound(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadStepSounds()));
			block.luminance = 3;
			block.isReplaceable = !_true;
			block.creativePickItem = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocks(modElement.getWorkspace())).getName());
			block.colorOnMap = getRandomItem(random, ElementUtil.loadMapColors());
			block.offsetType = getRandomString(random, Arrays.asList("NONE", "XZ", "XYZ"));
			block.aiPathNodeType = getRandomItem(random, ElementUtil.loadPathNodeTypes());
			if (!emptyLists)
				block.beaconColorModifier = Color.cyan;
			else
				block.beaconColorModifier = null;
			block.unbreakable = _true;
			block.breakHarvestLevel = 4;
			block.spawnParticles = _true;
			block.tickRandomly = _true;
			block.particleToSpawn = new Particle(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadAllParticles(modElement.getWorkspace())));
			block.particleSpawningShape = new String[] { "Spread", "Top", "Tube", "Plane" }[valueIndex];
			block.particleSpawningRadious = 4;
			block.particleAmount = 13;
			block.hasInventory = _true;
			block.guiBoundTo = "<NONE>";
			block.openGUIOnRightClick = !_true;
			block.inventorySize = 10;
			block.inventoryStackSize = 42;
			block.inventoryDropWhenDestroyed = !_true;
			block.inventoryComparatorPower = !_true;
			block.inventoryOutSlotIDs = new ArrayList<>();
			if (!emptyLists) {
				block.inventoryOutSlotIDs.add(1);
				block.inventoryOutSlotIDs.add(2);
				block.inventoryOutSlotIDs.add(3);
				block.inventoryOutSlotIDs.add(7);
				block.inventoryOutSlotIDs.add(8);
			}
			block.inventoryInSlotIDs = new ArrayList<>();
			if (!emptyLists) {
				block.inventoryInSlotIDs.add(2);
				block.inventoryInSlotIDs.add(7);
				block.inventoryInSlotIDs.add(11);
			}
			block.hasEnergyStorage = _true;
			block.energyCapacity = 123;
			block.energyInitial = 22;
			block.energyMaxExtract = 4245;
			block.energyMaxReceive = 1234;
			block.isFluidTank = !_true;
			block.fluidCapacity = 451;
			block.fluidRestrictions = new ArrayList<>();
			if (!emptyLists) {
				block.fluidRestrictions.add(new net.mcreator.element.parts.Fluid(modElement.getWorkspace(),
						getRandomItem(random, ElementUtil.loadAllFluids(modElement.getWorkspace()))));
				block.fluidRestrictions.add(new net.mcreator.element.parts.Fluid(modElement.getWorkspace(),
						getRandomItem(random, ElementUtil.loadAllFluids(modElement.getWorkspace()))));
			}
			block.spawnWorldTypes = new ArrayList<>(Arrays.asList("Nether", "Surface", "End"));
			block.restrictionBiomes = new ArrayList<>();
			if (!emptyLists) {
				block.restrictionBiomes.add(new BiomeEntry(modElement.getWorkspace(),
						getRandomDataListEntry(random, ElementUtil.loadAllBiomes(modElement.getWorkspace()))));
				block.restrictionBiomes.add(new BiomeEntry(modElement.getWorkspace(),
						getRandomDataListEntry(random, ElementUtil.loadAllBiomes(modElement.getWorkspace()))));
				block.restrictionBiomes.add(new BiomeEntry(modElement.getWorkspace(),
						getRandomDataListEntry(random, ElementUtil.loadAllBiomes(modElement.getWorkspace()))));
				block.restrictionBiomes.add(new BiomeEntry(modElement.getWorkspace(),
						getRandomDataListEntry(random, ElementUtil.loadAllBiomes(modElement.getWorkspace()))));
			}
			block.blocksToReplace = new ArrayList<>();
			if (!emptyLists) {
				block.blocksToReplace.add(new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, ElementUtil.loadBlocks(modElement.getWorkspace())).getName()));
				block.blocksToReplace.add(new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, ElementUtil.loadBlocks(modElement.getWorkspace())).getName()));
				block.blocksToReplace.add(new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, ElementUtil.loadBlocks(modElement.getWorkspace())).getName()));
			}
			block.frequencyPerChunks = 6;
			block.frequencyOnChunk = 7;
			block.minGenerateHeight = 21;
			block.maxGenerateHeight = 92;
			if (emptyLists) {
				block.onBlockAdded = new Procedure("procedure1");
				block.onNeighbourBlockChanges = new Procedure("procedure2");
				block.onTickUpdate = new Procedure("procedure3");
				block.onRandomUpdateEvent = new Procedure("procedure4");
				block.onDestroyedByPlayer = new Procedure("procedure5");
				block.onDestroyedByExplosion = new Procedure("procedure6");
				block.onStartToDestroy = new Procedure("procedure7");
				block.onEntityCollides = new Procedure("procedure8");
				block.onBlockPlayedBy = new Procedure("procedure9");
				block.onRightClicked = new Procedure("procedure10");
				block.onRedstoneOn = new Procedure("procedure11");
				block.onRedstoneOff = new Procedure("procedure12");
				block.onEntityWalksOn = new Procedure("procedure13");
				block.generateCondition = new Procedure("condition1");
				block.particleCondition = new Procedure("condition4");
			}
			block.itemTexture = emptyLists ? "" : "itest";
			block.particleTexture = emptyLists ? "" : "test7";
			block.texture = "test";
			block.textureTop = "test2";
			block.textureLeft = "test3";
			block.textureFront = "test4";
			block.textureRight = "test5";
			block.textureBack = "test6";
			block.specialInfo = new ArrayList<>();
			if (!emptyLists) {
				block.specialInfo = StringUtils
						.splitCommaSeparatedStringListWithEscapes("info 1, info 2, test \\, is this, another one");
			} else {
				block.specialInfo = new ArrayList<>();
			}
			block.tintType = getRandomString(random, Arrays.asList("No tint", "Grass", "Foliage", "Water"));
			block.isItemTinted = _true;
			block.renderType = new int[] { 10, block.isBlockTinted() ? 110 : 11, block.isBlockTinted() ? 120 : 12,
					14 }[valueIndex];
			block.customModelName = new String[] { "Normal", "Single texture", "Cross model",
					"Grass block" }[valueIndex];
			return block;
		case TAG:
			Tag tag = new Tag(modElement);
			tag.namespace = getRandomItem(random, new String[] { "forge", "minecraft", "test1", "test2" });
			tag.type = getRandomItem(random, new String[] { "Items", "Blocks", "Entities", "Functions" });
			tag.name = modElement.getName();
			tag.items = new ArrayList<>();
			tag.blocks = new ArrayList<>();
			tag.functions = new ArrayList<>();
			tag.entities = new ArrayList<>();
			if (!emptyLists) {
				tag.items.add(new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName()));
				tag.items.add(new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName()));
				tag.items.add(new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName()));

				tag.blocks.add(new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, ElementUtil.loadBlocks(modElement.getWorkspace())).getName()));
				tag.blocks.add(new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, ElementUtil.loadBlocks(modElement.getWorkspace())).getName()));
				tag.functions.add("test1");
				tag.functions.add("test2");
				tag.entities.add(new EntityEntry(modElement.getWorkspace(),
						getRandomDataListEntry(random, ElementUtil.loadAllEntities(modElement.getWorkspace()))));
				tag.entities.add(new EntityEntry(modElement.getWorkspace(),
						getRandomDataListEntry(random, ElementUtil.loadAllEntities(modElement.getWorkspace()))));
			}
			return tag;
		case LOOTTABLE:
			LootTable lootTable = new LootTable(modElement);

			lootTable.name = modElement.getName().toLowerCase(Locale.ENGLISH);
			lootTable.namespace = getRandomItem(random, new String[] { "minecraft", "mod" });
			lootTable.type = getRandomItem(random,
					new String[] { "Generic", "Entity", "Block", "Chest", "Fishing", "Empty", "Advancement reward" });

			lootTable.pools = new ArrayList<>();

			if (!emptyLists) {
				int pools = random.nextInt(5) + 1;
				for (int i = 0; i < pools; i++) {
					LootTable.Pool pool = new LootTable.Pool();
					pool.minrolls = new int[] { 2, 4, 1, 3 }[valueIndex];
					pool.maxrolls = new int[] { 3, 4, 6, 3 }[valueIndex];
					pool.hasbonusrolls = _true;
					pool.minbonusrolls = new int[] { 0, 9, 4, 6 }[valueIndex];
					pool.maxbonusrolls = new int[] { 2, 9, 4, 7 }[valueIndex];
					pool.entries = new ArrayList<>();

					int entries = random.nextInt(5) + 1;
					for (int j = 0; j < entries; j++) {
						LootTable.Pool.Entry entry = new LootTable.Pool.Entry();

						entry.type = "item";
						entry.weight = new int[] { 1, 2, 3, -3 }[valueIndex];

						entry.minCount = new int[] { 1, 6, 2, 8 }[valueIndex];
						entry.maxCount = new int[] { 4, 6, 7, 8 }[valueIndex];

						entry.affectedByFortune = _true;
						entry.explosionDecay = _true;

						entry.silkTouchMode = new int[] { 0, 1, 2, 1 }[valueIndex];

						entry.minEnchantmentLevel = new int[] { 2, 5, 1, 6 }[valueIndex];
						entry.maxEnchantmentLevel = new int[] { 3, 9, 5, 6 }[valueIndex];

						entry.item = new MItemBlock(modElement.getWorkspace(),
								getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace()))
										.getName());

						pool.entries.add(entry);
					}

					lootTable.pools.add(pool);
				}
			}

			return lootTable;
		case FUNCTION:
			Function function = new Function(modElement);
			function.name = modElement.getName().toLowerCase(Locale.ENGLISH);
			function.namespace = getRandomItem(random, new String[] { "minecraft", "mod" });
			function.code = "execute as @a at @s run function custom:test\n";
			return function;
		case MUSICDISC:
			MusicDisc musicDisc = new MusicDisc(modElement);
			musicDisc.name = modElement.getName();
			musicDisc.description = modElement.getName();
			musicDisc.creativeTab = new TabEntry(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadAllTabs(modElement.getWorkspace())));
			musicDisc.hasGlow = _true;
			musicDisc.onRightClickedInAir = new Procedure("procedure1");
			musicDisc.onRightClickedOnBlock = new Procedure("procedure2");
			musicDisc.onCrafted = new Procedure("procedure3");
			musicDisc.onEntityHitWith = new Procedure("procedure4");
			musicDisc.onItemInInventoryTick = new Procedure("procedure5");
			musicDisc.onItemInUseTick = new Procedure("procedure6");
			musicDisc.onStoppedUsing = new Procedure("procedure7");
			musicDisc.onEntitySwing = new Procedure("procedure8");
			musicDisc.music = new Sound(modElement.getWorkspace(),
					getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			if (!emptyLists) {
				musicDisc.specialInfo = StringUtils
						.splitCommaSeparatedStringListWithEscapes("info 1, info 2, test \\, is this, another one");
			} else {
				musicDisc.specialInfo = new ArrayList<>();
			}
			musicDisc.texture = "itest";
			return musicDisc;
		case ENCHANTMENT:
			Enchantment enchantment = new Enchantment(modElement);
			enchantment.name = modElement.getName().toLowerCase(Locale.ENGLISH);
			enchantment.rarity = getRandomItem(random, new String[] { "COMMON", "UNCOMMON", "RARE", "VERY_RARE" });
			enchantment.type = getRandomString(random,
					ElementUtil.loadEnchantmentTypes().stream().map(DataListEntry::getName)
							.collect(Collectors.toList()));
			enchantment.minLevel = 13;
			enchantment.maxLevel = 45;
			enchantment.damageModifier = 3;
			enchantment.isTreasureEnchantment = _true;
			enchantment.isAllowedOnBooks = !_true;
			enchantment.isCurse = _true;
			enchantment.compatibleItems = new ArrayList<>();
			if (!emptyLists) {
				enchantment.compatibleItems.add(new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName()));
				enchantment.compatibleItems.add(new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName()));
				enchantment.compatibleItems.add(new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName()));
			}
			enchantment.compatibleEnchantments = new ArrayList<>();
			if (!emptyLists) {
				enchantment.compatibleEnchantments
						.add(new net.mcreator.element.parts.Enchantment(modElement.getWorkspace(),
								getRandomDataListEntry(random,
										ElementUtil.loadAllEnchantments(modElement.getWorkspace()))));
				enchantment.compatibleEnchantments
						.add(new net.mcreator.element.parts.Enchantment(modElement.getWorkspace(),
								getRandomDataListEntry(random,
										ElementUtil.loadAllEnchantments(modElement.getWorkspace()))));
				enchantment.compatibleEnchantments
						.add(new net.mcreator.element.parts.Enchantment(modElement.getWorkspace(),
								getRandomDataListEntry(random,
										ElementUtil.loadAllEnchantments(modElement.getWorkspace()))));
				enchantment.compatibleEnchantments
						.add(new net.mcreator.element.parts.Enchantment(modElement.getWorkspace(),
								getRandomDataListEntry(random,
										ElementUtil.loadAllEnchantments(modElement.getWorkspace()))));
			}
			return enchantment;
		case PAINTING:
			Painting painting = new Painting(modElement);
			painting.texture = "test.png";
			painting.width = 16;
			painting.height = 16;
			return painting;
		case PARTICLE:
			net.mcreator.element.types.Particle particle = new net.mcreator.element.types.Particle(modElement);
			particle.texture = "test.png";
			particle.width = 2.3;
			particle.frameDuration = 2;
			particle.height = 1.38;
			particle.scale = 1.38;
			particle.gravity = 12.3;
			particle.speedFactor = 1.3;
			particle.canCollide = _true;
			particle.alwaysShow = !_true;
			particle.animate = _true;
			particle.maxAge = 12;
			particle.maxAgeDiff = emptyLists ? 0 : 15;
			particle.renderType = new String[] { "OPAQUE", "OPAQUE", "TRANSLUCENT", "LIT" }[valueIndex];
			particle.additionalExpiryCondition = new Procedure("condition1");
			return particle;
		case GAMERULE:
			GameRule gamerule = new GameRule(modElement);
			gamerule.name = modElement.getName();
			gamerule.description = modElement.getName();
			gamerule.category = getRandomString(random,
					Arrays.asList("PLAYER", "UPDATES", "CHAT", "DROPS", "MISC", "MOBS", "SPAWNING"));
			gamerule.type = new String[] { "Number", "Logic", "Number", "Logic" }[valueIndex];
			gamerule.defaultValueLogic = _true;
			gamerule.defaultValueNumber = -45;
		default:
			return null;
		}
	}

	private static GeneratableElement getToolExample(ModElement modElement, String recipeType, Random random,
			boolean _true, boolean emptyLists) {
		Tool tool = new Tool(modElement);
		tool.name = modElement.getName();
		tool.creativeTab = new TabEntry(modElement.getWorkspace(),
				getRandomDataListEntry(random, ElementUtil.loadAllTabs(modElement.getWorkspace())));
		tool.toolType = recipeType;
		tool.harvestLevel = 3;
		tool.efficiency = 6;
		tool.attackSpeed = 4.8;
		tool.enchantability = 4;
		tool.damageVsEntity = 2;
		tool.usageCount = 24;
		tool.stayInGridWhenCrafting = _true;
		tool.damageOnCrafting = _true;
		tool.immuneToFire = _true;
		tool.blocksAffected = new ArrayList<>();
		tool.hasGlow = _true;
		tool.specialInfo = new ArrayList<>();
		if (!emptyLists) {
			tool.specialInfo = StringUtils
					.splitCommaSeparatedStringListWithEscapes("info 1, info 2, test \\, is this, another one");
		} else {
			tool.specialInfo = new ArrayList<>();
		}
		if (!emptyLists) {
			tool.blocksAffected.add(new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocks(modElement.getWorkspace())).getName()));
			tool.blocksAffected.add(new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocks(modElement.getWorkspace())).getName()));
			tool.blocksAffected.add(new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocks(modElement.getWorkspace())).getName()));
			tool.blocksAffected.add(new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocks(modElement.getWorkspace())).getName()));
			tool.blocksAffected.add(new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocks(modElement.getWorkspace())).getName()));
			tool.blocksAffected.add(new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocks(modElement.getWorkspace())).getName()));
		}
		tool.repairItems = new ArrayList<>();
		if (!emptyLists) {
			tool.repairItems.add(new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName()));
			tool.repairItems.add(new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName()));
			tool.repairItems.add(new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName()));
			tool.repairItems.add(new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName()));
			tool.repairItems.add(new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName()));
		}
		tool.onRightClickedInAir = new Procedure("procedure1");
		tool.onRightClickedOnBlock = new Procedure("procedure2");
		tool.onCrafted = new Procedure("procedure3");
		tool.onBlockDestroyedWithTool = new Procedure("procedure4");
		tool.onEntityHitWith = new Procedure("procedure5");
		tool.onItemInInventoryTick = new Procedure("procedure6");
		tool.onItemInUseTick = new Procedure("procedure7");
		tool.onStoppedUsing = new Procedure("procedure8");
		tool.onEntitySwing = new Procedure("procedure11");
		tool.texture = "test";
		tool.renderType = 0;
		tool.customModelName = "Normal";
		return tool;
	}

	private static GeneratableElement getRecipeExample(ModElement modElement, String recipeType, Random random,
			boolean _true) {
		Recipe recipe = new Recipe(modElement);
		recipe.group = modElement.getName().toLowerCase(Locale.ENGLISH);
		recipe.recipeType = recipeType;
		if ("Crafting".equals(recipe.recipeType)) {
			MItemBlock[] recipeSlots = new MItemBlock[9];

			Arrays.fill(recipeSlots, new MItemBlock(modElement.getWorkspace(), ""));

			recipeSlots[0] = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItemsAndTags(modElement.getWorkspace()))
							.getName());

			if (random.nextBoolean())
				recipeSlots[3] = new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, ElementUtil.loadBlocksAndItemsAndTags(modElement.getWorkspace()))
								.getName());
			if (random.nextBoolean())
				recipeSlots[6] = new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, ElementUtil.loadBlocksAndItemsAndTags(modElement.getWorkspace()))
								.getName());
			if (random.nextBoolean())
				recipeSlots[1] = new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, ElementUtil.loadBlocksAndItemsAndTags(modElement.getWorkspace()))
								.getName());
			if (random.nextBoolean())
				recipeSlots[4] = new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, ElementUtil.loadBlocksAndItemsAndTags(modElement.getWorkspace()))
								.getName());
			if (random.nextBoolean())
				recipeSlots[7] = new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, ElementUtil.loadBlocksAndItemsAndTags(modElement.getWorkspace()))
								.getName());
			if (random.nextBoolean())
				recipeSlots[2] = new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, ElementUtil.loadBlocksAndItemsAndTags(modElement.getWorkspace()))
								.getName());
			if (random.nextBoolean())
				recipeSlots[5] = new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, ElementUtil.loadBlocksAndItemsAndTags(modElement.getWorkspace()))
								.getName());
			if (random.nextBoolean())
				recipeSlots[8] = new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, ElementUtil.loadBlocksAndItemsAndTags(modElement.getWorkspace()))
								.getName());
			recipe.recipeRetstackSize = 11;
			recipe.recipeShapeless = _true;
			recipe.recipeReturnStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName());
			recipe.recipeSlots = recipeSlots;
		} else if ("Smelting".equals(recipe.recipeType)) {
			recipe.smeltingInputStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItemsAndTags(modElement.getWorkspace()))
							.getName());
			recipe.smeltingReturnStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName());
			recipe.xpReward = 1.234;
			recipe.cookingTime = 123;
		} else if ("Smoking".equals(recipe.recipeType)) {
			recipe.smokingInputStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItemsAndTags(modElement.getWorkspace()))
							.getName());
			recipe.smokingReturnStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName());
			recipe.xpReward = 12.34;
			recipe.cookingTime = 42;
		} else if ("Blasting".equals(recipe.recipeType)) {
			recipe.blastingInputStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItemsAndTags(modElement.getWorkspace()))
							.getName());
			recipe.blastingReturnStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName());
			recipe.xpReward = 21.234;
			recipe.cookingTime = 1000;
		} else if ("Stone cutting".equals(recipe.recipeType)) {
			recipe.stoneCuttingInputStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItemsAndTags(modElement.getWorkspace()))
							.getName());
			recipe.stoneCuttingReturnStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName());
			recipe.recipeRetstackSize = 32;
		} else if ("Campfire cooking".equals(recipe.recipeType)) {
			recipe.campfireCookingInputStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItemsAndTags(modElement.getWorkspace()))
							.getName());
			recipe.campfireCookingReturnStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName());
			recipe.xpReward = 21.234;
			recipe.cookingTime = 2983;
		} else if ("Smithing".equals(recipe.recipeType)) {
			recipe.smithingInputStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItemsAndTags(modElement.getWorkspace()))
							.getName());
			recipe.smithingInputAdditionStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItemsAndTags(modElement.getWorkspace()))
							.getName());
			recipe.smithingReturnStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName());
		} else if ("Brewing".equals(recipe.recipeType)) {
			recipe.brewingInputStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItemsAndTags(modElement.getWorkspace()))
							.getName());
			recipe.brewingIngredientStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItemsAndTags(modElement.getWorkspace()))
							.getName());
			recipe.brewingReturnStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, ElementUtil.loadBlocksAndItems(modElement.getWorkspace())).getName());
		} else {
			throw new RuntimeException("Unknown recipe type");
		}
		return recipe;
	}

	public static <T> T getRandomItem(Random random, T[] list) {
		int listSize = list.length;
		int randomIndex = random.nextInt(listSize);
		return list[randomIndex];
	}

	public static DataListEntry getRandomDataListEntry(Random random, List<DataListEntry> list) {
		if (list.isEmpty())
			return new DataListEntry.Null();

		int listSize = list.size();
		int randomIndex = random.nextInt(listSize);
		return list.get(randomIndex);
	}

	public static MCItem getRandomMCItem(Random random, List<MCItem> list) {
		if (list.isEmpty())
			return new MCItem(new DataListEntry.Dummy("STONE"));

		int listSize = list.size();
		int randomIndex = random.nextInt(listSize);
		return list.get(randomIndex);
	}

	public static String getRandomString(Random random, List<String> list) {
		if (list.isEmpty())
			return "";

		int listSize = list.size();
		int randomIndex = random.nextInt(listSize);
		return list.get(randomIndex);
	}

}
