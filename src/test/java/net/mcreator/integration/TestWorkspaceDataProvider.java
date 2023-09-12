/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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
import net.mcreator.element.parts.*;
import net.mcreator.element.parts.gui.Button;
import net.mcreator.element.parts.gui.Checkbox;
import net.mcreator.element.parts.gui.Image;
import net.mcreator.element.parts.gui.Label;
import net.mcreator.element.parts.gui.TextField;
import net.mcreator.element.parts.gui.*;
import net.mcreator.element.parts.procedure.LogicProcedure;
import net.mcreator.element.parts.procedure.NumberProcedure;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.parts.procedure.StringProcedure;
import net.mcreator.element.types.Dimension;
import net.mcreator.element.types.Enchantment;
import net.mcreator.element.types.Fluid;
import net.mcreator.element.types.*;
import net.mcreator.element.types.interfaces.IBlockWithBoundingBox;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.io.FileIO;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.DataListLoader;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.minecraft.MCItem;
import net.mcreator.ui.dialogs.wysiwyg.AbstractWYSIWYGDialog;
import net.mcreator.ui.minecraft.states.PropertyData;
import net.mcreator.ui.minecraft.states.StateMap;
import net.mcreator.ui.modgui.ItemGUI;
import net.mcreator.ui.modgui.LivingEntityGUI;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.EmptyIcon;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableElement;
import net.mcreator.workspace.elements.VariableType;
import net.mcreator.workspace.elements.VariableTypeLoader;

import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class TestWorkspaceDataProvider {

	private static ModElement me(Workspace workspace, ModElementType<?> type, String suffix) {
		return new ModElement(workspace, "Example" + type.getRegistryName() + suffix, type);
	}

	public static List<GeneratableElement> getModElementExamplesFor(Workspace workspace, ModElementType<?> type,
			boolean uiTest, Random random) {
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
			generatableElements.add(getToolExample(me(workspace, type, "12"), "Fishing rod", random, true, false));
		} else if (type == ModElementType.TAB) {
			generatableElements.add(getExampleFor(me(workspace, type, "1"), uiTest, random, true, true, 0));
			generatableElements.add(getExampleFor(me(workspace, type, "2"), uiTest, random, true, false, 1));
		} else if (type == ModElementType.COMMAND || type == ModElementType.FUNCTION || type == ModElementType.PAINTING
				|| type == ModElementType.KEYBIND || type == ModElementType.PROCEDURE || type == ModElementType.FEATURE
				|| type == ModElementType.CODE) {
			generatableElements.add(
					getExampleFor(new ModElement(workspace, "Example" + type.getRegistryName(), type), uiTest, random,
							true, true, 0));
		} else {
			generatableElements.add(getExampleFor(me(workspace, type, "1"), uiTest, random, true, true, 0));
			generatableElements.add(getExampleFor(me(workspace, type, "2"), uiTest, random, true, false, 1));
			generatableElements.add(getExampleFor(me(workspace, type, "3"), uiTest, random, false, true, 2));
			generatableElements.add(getExampleFor(me(workspace, type, "4"), uiTest, random, false, false, 3));
			generatableElements.add(getExampleFor(me(workspace, type, "5"), uiTest, random, true, true, 3));
			generatableElements.add(getExampleFor(me(workspace, type, "6"), uiTest, random, true, false, 2));
			generatableElements.add(getExampleFor(me(workspace, type, "7"), uiTest, random, false, true, 1));
			generatableElements.add(getExampleFor(me(workspace, type, "8"), uiTest, random, false, false, 0));
		}

		generatableElements.removeAll(Collections.singleton(null));
		return generatableElements;
	}

	public static void fillWorkspaceWithTestData(Workspace workspace) {
		if (workspace.getGeneratorStats().getBaseCoverageInfo().get("variables")
				== GeneratorStats.CoverageStatus.FULL) {
			VariableElement sampleVariable1 = new VariableElement("test");
			sampleVariable1.setValue("true");
			sampleVariable1.setType(VariableTypeLoader.BuiltInTypes.LOGIC);
			sampleVariable1.setScope(VariableType.Scope.GLOBAL_WORLD);
			workspace.addVariableElement(sampleVariable1);

			int idx = 0;
			for (VariableType.Scope scope : VariableType.Scope.values()) {
				if (scope != VariableType.Scope.LOCAL) {
					VariableElement variable = new VariableElement("logic" + (idx++));
					variable.setValue("true");
					variable.setType(VariableTypeLoader.BuiltInTypes.LOGIC);
					variable.setScope(scope);
					workspace.addVariableElement(variable);
				}
			}

			idx = 0;
			for (VariableType.Scope scope : VariableType.Scope.values()) {
				if (scope != VariableType.Scope.LOCAL) {
					VariableElement variable = new VariableElement("number" + (idx++));
					variable.setValue("12");
					variable.setType(VariableTypeLoader.BuiltInTypes.NUMBER);
					variable.setScope(scope);
					workspace.addVariableElement(variable);
				}
			}

			idx = 0;
			for (VariableType.Scope scope : VariableType.Scope.values()) {
				if (scope != VariableType.Scope.LOCAL) {
					VariableElement variable = new VariableElement("string" + (idx++));
					variable.setValue("test");
					variable.setType(VariableTypeLoader.BuiltInTypes.STRING);
					variable.setScope(scope);
					workspace.addVariableElement(variable);
				}
			}

			idx = 0;
			for (VariableType.Scope scope : VariableType.Scope.values()) {
				if (scope != VariableType.Scope.LOCAL) {
					VariableElement variable = new VariableElement("itemstack" + (idx++));
					variable.setValue("ItemStack.EMPTY");
					variable.setType(VariableTypeLoader.BuiltInTypes.ITEMSTACK);
					variable.setScope(scope);
					workspace.addVariableElement(variable);
				}
			}

			idx = 0;
			for (VariableType.Scope scope : VariableType.Scope.values()) {
				if (scope != VariableType.Scope.LOCAL) {
					VariableElement variable = new VariableElement("direction" + (idx++));
					variable.setValue("UP");
					variable.setType(VariableTypeLoader.BuiltInTypes.DIRECTION);
					variable.setScope(scope);
					workspace.addVariableElement(variable);
				}
			}

			idx = 0;
			for (VariableType.Scope scope : VariableType.Scope.values()) {
				if (scope != VariableType.Scope.LOCAL) {
					VariableElement variable = new VariableElement("blockstate" + (idx++));
					variable.setValue("Blocks.AIR");
					variable.setType(VariableTypeLoader.BuiltInTypes.BLOCKSTATE);
					variable.setScope(scope);
					workspace.addVariableElement(variable);
				}
			}
		}

		EmptyIcon.ImageIcon imageIcon = new EmptyIcon.ImageIcon(16, 16);

		if (workspace.getFolderManager().getTexturesFolder(TextureType.BLOCK) != null) {
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getTextureFile("test", TextureType.BLOCK));
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getTextureFile("test2", TextureType.BLOCK));
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getTextureFile("test3", TextureType.BLOCK));
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getTextureFile("test4", TextureType.BLOCK));
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getTextureFile("test5", TextureType.BLOCK));
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getTextureFile("test6", TextureType.BLOCK));
		}

		if (workspace.getFolderManager().getTexturesFolder(TextureType.ITEM) != null) {
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getTextureFile("test", TextureType.ITEM));
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getTextureFile("test2", TextureType.ITEM));
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getTextureFile("test3", TextureType.ITEM));
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getTextureFile("test4", TextureType.ITEM));
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getTextureFile("itest", TextureType.ITEM));

		}

		if (workspace.getFolderManager().getTexturesFolder(TextureType.OTHER) != null) {
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getTextureFile("test", TextureType.OTHER));
		}

		if (workspace.getFolderManager().getTexturesFolder(TextureType.ENTITY) != null) {
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getTextureFile("test", TextureType.ENTITY));
		}

		if (workspace.getFolderManager().getTexturesFolder(TextureType.EFFECT) != null) {
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getTextureFile("test", TextureType.EFFECT));
		}

		if (workspace.getFolderManager().getTexturesFolder(TextureType.PARTICLE) != null) {
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getTextureFile("test", TextureType.PARTICLE));
		}

		if (workspace.getFolderManager().getTexturesFolder(TextureType.SCREEN) != null) {
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getTextureFile("test", TextureType.SCREEN));
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getTextureFile("picture1", TextureType.SCREEN));
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getTextureFile("picture2", TextureType.SCREEN));
		}

		if (workspace.getFolderManager().getTexturesFolder(TextureType.ARMOR) != null) {
			File[] armorPars = workspace.getFolderManager().getArmorTextureFilesForName("test");
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(), armorPars[0]);
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(), armorPars[1]);
		}

		if (workspace.getFolderManager().getStructuresDir() != null) {
			FileIO.writeBytesToFile(new byte[0], new File(workspace.getFolderManager().getStructuresDir(), "test.nbt"));
		}
	}

	/**
	 * Provides list of GEs for tests
	 *
	 * @param modElement ME for this GE
	 * @param uiTest     true if test is UI test and not generator test
	 * @return List of GEs for tests
	 */
	private static GeneratableElement getExampleFor(ModElement modElement, boolean uiTest, Random random, boolean _true,
			boolean emptyLists, int valueIndex) {
		List<MCItem> blocksAndItems = ElementUtil.loadBlocksAndItems(modElement.getWorkspace());
		List<MCItem> blocksAndItemsAndTags = ElementUtil.loadBlocksAndItemsAndTags(modElement.getWorkspace());
		List<MCItem> blocks = ElementUtil.loadBlocks(modElement.getWorkspace());
		List<MCItem> blocksAndTags = ElementUtil.loadBlocksAndTags(modElement.getWorkspace());
		List<DataListEntry> biomes = ElementUtil.loadAllBiomes(modElement.getWorkspace());

		if (ModElementType.ADVANCEMENT.equals(modElement.getType())) {
			Achievement achievement = new Achievement(modElement);
			achievement.achievementName = "Test Achievement";
			achievement.achievementDescription = "Description of it";
			achievement.achievementIcon = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItems).getName());
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
				achievement.rewardLoot.add("ExampleLootTable1");
				achievement.rewardLoot.add("ExampleLootTable2");
			}
			achievement.rewardRecipes = new ArrayList<>();
			if (!emptyLists) {
				achievement.rewardRecipes.add("ExampleRecipe1");
				achievement.rewardRecipes.add("ExampleRecipe2");
			}
			achievement.triggerxml =
					"<xml xmlns=\"https://developers.google.com/blockly/xml\"><block type=\"tick\" x=\"40\" y=\"80\"><next>"
							+ "<block type=\"advancement_trigger\" deletable=\"false\"></block></next></block></xml>";
			return achievement;
		} else if (ModElementType.BIOME.equals(modElement.getType())) {
			Biome biome = new Biome(modElement);
			biome.name = modElement.getName();
			biome.groundBlock = new MItemBlock(modElement.getWorkspace(), getRandomMCItem(random, blocks).getName());
			biome.undergroundBlock = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocks).getName());
			biome.underwaterBlock = new MItemBlock(modElement.getWorkspace(),
					emptyLists ? "" : getRandomMCItem(random, blocks).getName());
			biome.vanillaTreeType = getRandomItem(random,
					new String[] { "Default", "Big trees", "Birch trees", "Savanna trees", "Mega pine trees",
							"Mega spruce trees" });
			biome.airColor = Color.red;
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
			biome.spawnShipwreck = _true;
			biome.spawnShipwreckBeached = _true;
			biome.oceanRuinType = getRandomItem(random, new String[] { "NONE", "COLD", "WARM" });
			biome.spawnOceanMonument = !_true;
			biome.spawnBuriedTreasure = !_true;
			biome.spawnWoodlandMansion = _true;
			biome.spawnJungleTemple = !_true;
			biome.spawnDesertPyramid = !_true;
			biome.spawnSwampHut = !_true;
			biome.spawnIgloo = !_true;
			biome.spawnPillagerOutpost = !_true;
			biome.spawnStronghold = _true;
			biome.spawnMineshaft = !_true;
			biome.spawnMineshaftMesa = !_true;
			biome.spawnNetherBridge = !_true;
			biome.spawnNetherFossil = !_true;
			biome.spawnBastionRemnant = !_true;
			biome.spawnEndCity = !_true;
			biome.spawnRuinedPortal = getRandomItem(random,
					new String[] { "NONE", "STANDARD", "DESERT", "JUNGLE", "SWAMP", "MOUNTAIN", "OCEAN", "NETHER" });
			biome.villageType = getRandomItem(random,
					new String[] { "none", "desert", "plains", "savanna", "snowy", "taiga" });
			biome.genTemperature = new Biome.ClimatePoint(0.1, 0.4);
			biome.genHumidity = new Biome.ClimatePoint(-0.1, 0.4);
			biome.genContinentalness = new Biome.ClimatePoint(-2.0, 2.0);
			biome.genErosion = new Biome.ClimatePoint(0.4, 1.4);
			biome.genWeirdness = new Biome.ClimatePoint(1.0, 1.1);

			biome.rainingPossibility = 1.1;
			biome.temperature = 2.1;

			List<Biome.SpawnEntry> entities = new ArrayList<>();
			if (!emptyLists) {
				Biome.SpawnEntry entry1 = new Biome.SpawnEntry();
				entry1.entity = new EntityEntry(modElement.getWorkspace(), getRandomDataListEntry(random,
						ElementUtil.loadAllSpawnableEntities(modElement.getWorkspace())));
				entry1.minGroup = 10;
				entry1.maxGroup = 134;
				entry1.weight = 13;
				entry1.spawnType = getRandomItem(random, ElementUtil.getDataListAsStringArray("mobspawntypes"));
				entities.add(entry1);

				Biome.SpawnEntry entry2 = new Biome.SpawnEntry();
				entry2.entity = new EntityEntry(modElement.getWorkspace(), getRandomDataListEntry(random,
						ElementUtil.loadAllSpawnableEntities(modElement.getWorkspace())));
				entry2.minGroup = 23;
				entry2.maxGroup = 145;
				entry2.weight = 11;
				entry2.spawnType = getRandomItem(random, ElementUtil.getDataListAsStringArray("mobspawntypes"));
				entities.add(entry2);

				Biome.SpawnEntry entry3 = new Biome.SpawnEntry();
				entry3.entity = new EntityEntry(modElement.getWorkspace(), getRandomDataListEntry(random,
						ElementUtil.loadAllSpawnableEntities(modElement.getWorkspace())));
				entry3.minGroup = 23;
				entry3.maxGroup = 145;
				entry3.weight = 11;
				entry3.spawnType = getRandomItem(random, ElementUtil.getDataListAsStringArray("mobspawntypes"));
				entities.add(entry3);

				Biome.SpawnEntry entry4 = new Biome.SpawnEntry();
				entry4.entity = new EntityEntry(modElement.getWorkspace(), getRandomDataListEntry(random,
						ElementUtil.loadAllSpawnableEntities(modElement.getWorkspace())));
				entry4.minGroup = 23;
				entry4.maxGroup = 145;
				entry4.weight = 11;
				entry4.spawnType = getRandomItem(random, ElementUtil.getDataListAsStringArray("mobspawntypes"));
				entities.add(entry4);
			}
			biome.spawnEntries = entities;
			biome.minHeight = 2;
			List<String> biomeDefaultFeatures = new ArrayList<>();
			if (!emptyLists)
				biomeDefaultFeatures.addAll(Arrays.asList(ElementUtil.getDataListAsStringArray("defaultfeatures")));
			biome.defaultFeatures = biomeDefaultFeatures;
			if (!emptyLists) {
				biome.treeType = biome.TREES_CUSTOM;
				biome.treeVines = new MItemBlock(modElement.getWorkspace(), getRandomMCItem(random, blocks).getName());
				biome.treeStem = new MItemBlock(modElement.getWorkspace(), getRandomMCItem(random, blocks).getName());
				biome.treeBranch = new MItemBlock(modElement.getWorkspace(), getRandomMCItem(random, blocks).getName());
				biome.treeFruits = new MItemBlock(modElement.getWorkspace(), getRandomMCItem(random, blocks).getName());
			} else {
				biome.treeType = biome.TREES_VANILLA;
				biome.treeVines = new MItemBlock(modElement.getWorkspace(), "");
				biome.treeStem = new MItemBlock(modElement.getWorkspace(), "");
				biome.treeBranch = new MItemBlock(modElement.getWorkspace(), "");
				biome.treeFruits = new MItemBlock(modElement.getWorkspace(), "");
			}
			biome.spawnBiome = !_true;
			biome.spawnInCaves = _true;
			biome.spawnBiomeNether = !_true && emptyLists;
			return biome;
		} else if (ModElementType.FLUID.equals(modElement.getType())) {
			Fluid fluid = new Fluid(modElement);
			fluid.name = modElement.getName();
			fluid.textureFlowing = "test";
			fluid.textureStill = "test2";
			fluid.canMultiply = _true;
			fluid.flowRate = 8;
			fluid.levelDecrease = 2;
			fluid.slopeFindDistance = 3;
			fluid.spawnParticles = !_true;
			fluid.dripParticle = new Particle(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadAllParticles(modElement.getWorkspace())));
			fluid.tintType = getRandomString(random,
					Arrays.asList("No tint", "Grass", "Foliage", "Birch foliage", "Spruce foliage", "Default foliage",
							"Water", "Sky", "Fog", "Water fog"));
			fluid.flowStrength = 2.3;
			fluid.luminosity = 3;
			fluid.density = 5;
			fluid.viscosity = 10;
			fluid.temperature = 375;
			fluid.generateBucket = !_true;
			fluid.bucketName = modElement.getName() + " Bucket";
			fluid.textureBucket = emptyLists ? "" : "itest";
			fluid.creativeTab = new TabEntry(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadAllTabs(modElement.getWorkspace())));
			fluid.emptySound = !emptyLists ?
					new Sound(modElement.getWorkspace(), "") :
					new Sound(modElement.getWorkspace(),
							getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			fluid.rarity = getRandomString(random, Arrays.asList("COMMON", "UNCOMMON", "RARE", "EPIC"));
			fluid.specialInfo = new ArrayList<>();
			if (!emptyLists) {
				fluid.specialInfo = StringUtils.splitCommaSeparatedStringListWithEscapes(
						"info 1, info 2, test \\, is this, another one");
			} else {
				fluid.specialInfo = new ArrayList<>();
			}
			fluid.resistance = 52.2;
			fluid.emissiveRendering = _true;
			fluid.luminance = 6;
			fluid.tickRate = _true ? 0 : 13;
			fluid.lightOpacity = 2;
			fluid.flammability = 5;
			fluid.fireSpreadSpeed = 12;
			fluid.colorOnMap = getRandomItem(random, ElementUtil.getDataListAsStringArray("mapcolors"));
			fluid.onBlockAdded = new Procedure("procedure5");
			fluid.onNeighbourChanges = new Procedure("procedure2");
			fluid.onTickUpdate = new Procedure("procedure3");
			fluid.onEntityCollides = new Procedure("procedure1");
			fluid.onRandomUpdateEvent = new Procedure("procedure5");
			fluid.onDestroyedByExplosion = new Procedure("procedure6");
			fluid.flowCondition = new Procedure("condition1");
			fluid.beforeReplacingBlock = new Procedure("procedure7");
			fluid.type = _true ? "WATER" : "LAVA";
			return fluid;
		} else if (ModElementType.KEYBIND.equals(modElement.getType())) {
			KeyBinding keyBinding = new KeyBinding(modElement);
			keyBinding.triggerKey = getRandomString(random,
					DataListLoader.loadDataList("keybuttons").stream().map(DataListEntry::getName).toList());
			keyBinding.keyBindingName = modElement.getName();
			keyBinding.keyBindingCategoryKey = "key.categories.misc";
			if (!emptyLists)
				keyBinding.onKeyPressed = new Procedure("procedure3");
			if (_true)
				keyBinding.onKeyReleased = new Procedure("procedure2");
			return keyBinding;
		} else if (ModElementType.TAB.equals(modElement.getType())) {
			Tab tab = new Tab(modElement);
			tab.name = modElement.getName();
			tab.icon = new MItemBlock(modElement.getWorkspace(), getRandomMCItem(random, blocksAndItems).getName());
			tab.showSearch = _true;
			return tab;
		} else if (ModElementType.OVERLAY.equals(modElement.getType())) {
			Overlay overlay = new Overlay(modElement);
			overlay.priority = getRandomItem(random, new String[] { "NORMAL", "HIGH", "HIGHEST", "LOW", "LOWEST" });
			ArrayList<GUIComponent> components = new ArrayList<>();

			components.add(new Label("text", 100, 150, new StringProcedure(_true ? "string1" : null, "fixed value 1"),
					Color.red, new Procedure("condition1")));
			components.add(new Label("text2", 100, 150, new StringProcedure(!_true ? "string2" : null, "fixed value 2"),
					Color.white, new Procedure("condition4")));

			components.add(new Image(20, 30, "pricture1", true, new Procedure("condition1")));
			components.add(new Image(22, 31, "pricture2", false, new Procedure("condition2")));
			components.add(
					new EntityModel(60, 20, new Procedure("entity1"), new Procedure("condition3"), 30, 0, false));
			components.add(
					new EntityModel(60, 20, new Procedure("entity1"), new Procedure(!_true ? "condition4" : null), 30,
							90, false));
			overlay.displayCondition = new Procedure("condition1");
			overlay.components = components;
			overlay.baseTexture = emptyLists ? "" : "test.png";
			if (_true)
				overlay.overlayTarget = "Ingame";
			else
				overlay.overlayTarget = getRandomItem(random, ElementUtil.getDataListAsStringArray("screens"));
			return overlay;
		} else if (ModElementType.GUI.equals(modElement.getType())) {
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
			ArrayList<GUIComponent> components = new ArrayList<>();
			if (!emptyLists) {
				components.add(new Label(AbstractWYSIWYGDialog.textToMachineName(components, null,
						"This is --...p a test string ŽĐĆ @ /test//\" tes___"), 100, 150,
						new StringProcedure(_true ? "string1" : null, "fixed value 1"), Color.red,
						new Procedure("condition1")));
				components.add(new Label(AbstractWYSIWYGDialog.textToMachineName(components, null,
						"This is --...p a test string ŽĐĆ @ /test//\" tes___"), 100, 150,
						new StringProcedure(!_true ? "string2" : null, "fixed value 2"), Color.white,
						new Procedure("condition4")));

				components.add(new Image(20, 30, "picture1", true, new Procedure("condition1")));
				components.add(new Image(22, 31, "picture2", false, new Procedure("condition2")));
				components.add(new Button(AbstractWYSIWYGDialog.textToMachineName(components, null, "button"), 10, 10,
						"button1", 100, 200, new Procedure("procedure10"), null));
				components.add(new Button("button2", 10, 10, "button2", 100, 200, null, null));
				components.add(new Button("button3", 10, 10, "button3", 100, 200, null, new Procedure("condition3")));
				components.add(new Button(AbstractWYSIWYGDialog.textToMachineName(components, null, "button"), 10, 10,
						"button4", 100, 200, new Procedure("procedure2"), new Procedure("condition4")));
				components.add(
						new ImageButton("imagebutton1", 0, 48, "picture1", "picture2", new Procedure("procedure10"),
								null));
				components.add(
						new ImageButton("imagebutton2", 16, 32, "picture2", "", new Procedure(""), new Procedure("")));
				components.add(new ImageButton("imagebutton3", 32, 16, "picture1", "picture2", null,
						new Procedure("condition3")));
				components.add(new ImageButton("imagebutton4", 48, 0, "picture2", "", new Procedure("procedure2"),
						new Procedure("condition4")));
				components.add(new InputSlot(0, 20, 30, Color.red, new LogicProcedure("condition1", true),
						new LogicProcedure("condition1", true), _true, new Procedure("procedure3"),
						new Procedure("procedure10"), new Procedure("procedure2"),
						new MItemBlock(modElement.getWorkspace(), "")));
				components.add(new InputSlot(3, 20, 30, Color.white, new LogicProcedure(null, true),
						new LogicProcedure("condition1", true), !_true, new Procedure("procedure4"), null, null,
						new MItemBlock(modElement.getWorkspace(), getRandomMCItem(random, blocksAndItems).getName())));
				new InputSlot(5, 20, 30, Color.white, new LogicProcedure("condition1", true),
						new LogicProcedure(null, true), !_true, new Procedure("procedure4"), null, null,
						new MItemBlock(modElement.getWorkspace(), getRandomMCItem(random, blocksAndItems).getName()));
				components.add(new InputSlot(4, 20, 30, Color.green, new LogicProcedure(null, _true),
						new LogicProcedure("condition1", !_true), _true, new Procedure("procedure5"), null, null,
						new MItemBlock(modElement.getWorkspace(), "TAG:flowers")));
				components.add(new OutputSlot(5, 10, 20, Color.black, new LogicProcedure("condition2", _true), !_true,
						new Procedure("procedure10"), new Procedure("procedure2"), new Procedure("procedure3")));
				components.add(
						new OutputSlot(6, 243, 563, Color.black, new LogicProcedure("condition2", true), _true, null,
								null, null));
				components.add(new TextField("text1", 0, 10, 100, 20, "Input value ..."));
				components.add(new TextField("text2", 55, 231, 90, 20, ""));
				components.add(new Checkbox("checkbox1", 100, 100, "Text", new Procedure("condition1")));
				components.add(new Checkbox("checkbox2", 125, 125, "Other text", new Procedure("condition2")));
				components.add(
						new EntityModel(60, 20, new Procedure("entity1"), new Procedure("condition3"), 30, 0, _true));
				components.add(
						new EntityModel(60, 20, new Procedure("entity1"), new Procedure(!_true ? "condition4" : null),
								30, 270, !_true));
				components.add(new Tooltip(AbstractWYSIWYGDialog.textToMachineName(components, null,
						"This is --...p a test string ŽĐĆ @ /test//\" tes___"), 20, 40, 70, 10,
						new StringProcedure(_true ? "string1" : null, "fixed value 1"), new Procedure("condition4")));
			}
			gui.components = components;
			return gui;
		} else if (ModElementType.LIVINGENTITY.equals(modElement.getType())) {
			return getLivingEntity(modElement, random, _true, emptyLists, valueIndex, blocksAndItems, biomes);
		} else if (ModElementType.DIMENSION.equals(modElement.getType())) {
			Dimension dimension = new Dimension(modElement);
			dimension.texture = "test";
			dimension.portalTexture = "test2";
			dimension.enableIgniter = true; // we always want it as it can be referenced in other tests
			dimension.portalParticles = new Particle(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadAllParticles(modElement.getWorkspace())));
			dimension.igniterTab = new TabEntry(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadAllTabs(modElement.getWorkspace())));
			dimension.portalSound = new Sound(modElement.getWorkspace(),
					getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			dimension.biomesInDimension = new ArrayList<>();
			if (!emptyLists) {
				dimension.biomesInDimension.addAll(
						biomes.stream().skip(_true ? 0 : ((long) (biomes.size() / 4) * valueIndex))
								.limit(biomes.size() / 4)
								.map(e -> new BiomeEntry(modElement.getWorkspace(), e.getName())).toList());
			} else {
				dimension.biomesInDimension.add(
						new BiomeEntry(modElement.getWorkspace(), getRandomDataListEntry(random, biomes)));
			}
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
					getRandomMCItem(random, blocks).getName());
			dimension.igniterName = modElement.getName();
			if (!emptyLists) {
				dimension.specialInfo = StringUtils.splitCommaSeparatedStringListWithEscapes(
						"info 1, info 2, test \\, is this, another one");
			} else {
				dimension.specialInfo = new ArrayList<>();
			}
			dimension.worldGenType = new String[] { "Nether like gen", "Normal world gen", "End like gen",
					"Normal world gen" }[valueIndex];
			dimension.mainFillerBlock = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocks).getName());
			dimension.fluidBlock = new MItemBlock(modElement.getWorkspace(), getRandomMCItem(random, blocks).getName());
			dimension.whenPortaTriggerlUsed = emptyLists ?
					new Procedure("actionresulttype1") :
					new Procedure("procedure1");
			dimension.onPortalTickUpdate = new Procedure("procedure3");
			dimension.onPlayerEntersDimension = new Procedure("procedure4");
			dimension.onPlayerLeavesDimension = new Procedure("procedure5");
			dimension.portalMakeCondition = new Procedure("condition3");
			dimension.portalUseCondition = new Procedure("condition4");
			return dimension;
		} else if (ModElementType.STRUCTURE.equals(modElement.getType())) {
			Structure structure = new Structure(modElement);
			structure.structure = "test";
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
				structure.restrictionBlocks.addAll(
						blocks.stream().skip(_true ? 0 : ((blocks.size() / 4) * valueIndex)).limit(blocks.size() / 4)
								.map(e -> new MItemBlock(modElement.getWorkspace(), e.getName())).toList());
			}
			if (_true) {
				structure.generateCondition = new Procedure("condition1");
				structure.onStructureGenerated = new Procedure("procedure3");
			}
			return structure;
		} else if (ModElementType.ARMOR.equals(modElement.getType())) {
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
			armor.helmetItemRenderType = 0;
			armor.helmetItemCustomModelName = "Normal";
			armor.bodyItemRenderType = 0;
			armor.bodyItemCustomModelName = "Normal";
			armor.leggingsItemRenderType = 0;
			armor.leggingsItemCustomModelName = "Normal";
			armor.bootsItemRenderType = 0;
			armor.bootsItemCustomModelName = "Normal";
			if (!emptyLists) {
				armor.helmetSpecialInfo = StringUtils.splitCommaSeparatedStringListWithEscapes(
						"info 1, info 2, test \\, is this, another one");
				armor.bodySpecialInfo = StringUtils.splitCommaSeparatedStringListWithEscapes(
						"info 1, info 2, test \\, is this, another one");
				armor.leggingsSpecialInfo = StringUtils.splitCommaSeparatedStringListWithEscapes(
						"info 1, info 2, test \\, is this, another one");
				armor.bootsSpecialInfo = StringUtils.splitCommaSeparatedStringListWithEscapes(
						"info 1, info 2, test \\, is this, another one");
			} else {
				armor.helmetSpecialInfo = new ArrayList<>();
				armor.bodySpecialInfo = new ArrayList<>();
				armor.leggingsSpecialInfo = new ArrayList<>();
				armor.bootsSpecialInfo = new ArrayList<>();
			}
			armor.helmetImmuneToFire = _true;
			armor.bodyImmuneToFire = !_true;
			armor.leggingsImmuneToFire = _true;
			armor.bootsImmuneToFire = !_true;
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
				armor.repairItems = new ArrayList<>(blocksAndItemsAndTags.stream()
						.skip(_true ? 0 : ((long) (blocksAndItemsAndTags.size() / 4) * valueIndex))
						.limit(blocksAndItemsAndTags.size() / 4)
						.map(e -> new MItemBlock(modElement.getWorkspace(), e.getName())).toList());
				armor.repairItems.add(new MItemBlock(modElement.getWorkspace(), "TAG:flowers"));
			}
			return armor;
		} else if (ModElementType.PLANT.equals(modElement.getType())) {
			Plant plant = new Plant(modElement);
			plant.name = modElement.getName();
			plant.plantType = new String[] { "normal", "growapable", "double", "normal" }[valueIndex];
			plant.spawnWorldTypes = new ArrayList<>(Arrays.asList("Nether", "Surface", "End"));
			plant.creativeTab = new TabEntry(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadAllTabs(modElement.getWorkspace())));
			plant.texture = "test";
			plant.textureBottom = "test2";
			plant.itemTexture = emptyLists ? "" : "itest";
			plant.particleTexture = emptyLists ? "" : "test3";
			plant.growapableSpawnType = getRandomItem(random, ElementUtil.getDataListAsStringArray("planttypes"));
			plant.suspiciousStewEffect = getRandomString(random,
					ElementUtil.loadAllPotionEffects(modElement.getWorkspace()).stream().map(DataListEntry::getName)
							.toList());
			plant.suspiciousStewDuration = 24;
			plant.growapableMaxHeight = 5;
			plant.customBoundingBox = !_true;
			plant.disableOffset = !_true;
			plant.boundingBoxes = new ArrayList<>();
			if (!emptyLists) {
				int boxes = random.nextInt(4) + 1;
				for (int i = 0; i < boxes; i++) {
					IBlockWithBoundingBox.BoxEntry box = new IBlockWithBoundingBox.BoxEntry();
					box.mx = new double[] { 0, 5 + i, 1.2, 7.1 }[valueIndex];
					box.my = new double[] { 0, 2, 3.6, 12.2 }[valueIndex];
					box.mz = new double[] { 0, 3.1, 0, 2.2 }[valueIndex];
					box.Mx = new double[] { 16, 15.2, 4, 7.1 + i }[valueIndex];
					box.My = new double[] { 16, 12.2, 16, 13 }[valueIndex];
					box.Mz = new double[] { 16, 12, 2.4, 1.2 }[valueIndex];
					box.subtract = new boolean[] { false, _true, _true, random.nextBoolean() }[valueIndex];

					plant.boundingBoxes.add(box);
				}
			}
			plant.hardness = 0.03;
			plant.emissiveRendering = !_true;
			plant.resistance = 3.45;
			plant.luminance = 7;
			plant.isReplaceable = !_true;
			plant.forceTicking = !_true;
			plant.hasTileEntity = !_true;
			plant.isSolid = _true;
			plant.specialInfo = new ArrayList<>();
			if (!emptyLists) {
				plant.specialInfo = StringUtils.splitCommaSeparatedStringListWithEscapes(
						"info 1, info 2, test \\, is this, another one");
			} else {
				plant.specialInfo = new ArrayList<>();
			}
			plant.creativePickItem = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocks).getName());
			plant.colorOnMap = getRandomItem(random, ElementUtil.getDataListAsStringArray("mapcolors"));
			plant.offsetType = getRandomString(random, Arrays.asList("NONE", "XZ", "XYZ"));
			plant.aiPathNodeType = getRandomItem(random, ElementUtil.getDataListAsStringArray("pathnodetypes"));
			plant.unbreakable = _true;
			plant.isCustomSoundType = !_true;
			plant.soundOnStep = new StepSound(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadStepSounds()));
			plant.breakSound = new Sound(modElement.getWorkspace(),
					getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			plant.stepSound = new Sound(modElement.getWorkspace(),
					getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			plant.placeSound = new Sound(modElement.getWorkspace(),
					getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			plant.hitSound = new Sound(modElement.getWorkspace(),
					getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			plant.fallSound = new Sound(modElement.getWorkspace(),
					getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			plant.customDrop = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItems).getName());
			plant.dropAmount = 4;
			plant.useLootTableForDrops = !_true;
			plant.frequencyOnChunks = 13;
			plant.patchSize = 46;
			plant.generateAtAnyHeight = _true;
			plant.generationType = getRandomItem(random, new String[] { "Grass", "Flower" });
			plant.flammability = 5;
			plant.fireSpreadSpeed = 12;
			plant.speedFactor = 34.632;
			plant.jumpFactor = 17.732;
			plant.canBePlacedOn = new ArrayList<>();
			if (!emptyLists) {
				plant.canBePlacedOn.addAll(
						blocks.stream().skip(_true ? 0 : ((blocks.size() / 4) * valueIndex)).limit(blocks.size() / 4)
								.map(e -> new MItemBlock(modElement.getWorkspace(), e.getName())).toList());
			}
			plant.restrictionBiomes = new ArrayList<>();
			if (!emptyLists) {
				plant.restrictionBiomes.addAll(
						biomes.stream().skip(_true ? 0 : ((biomes.size() / 4) * valueIndex)).limit(biomes.size() / 4)
								.map(e -> new BiomeEntry(modElement.getWorkspace(), e.getName())).toList());
			}
			plant.onNeighbourBlockChanges = new Procedure("procedure7");
			plant.onTickUpdate = new Procedure("procedure2");
			plant.onDestroyedByPlayer = new Procedure("procedure3");
			plant.onDestroyedByExplosion = new Procedure("procedure4");
			plant.onStartToDestroy = new Procedure("procedure5");
			plant.onEntityCollides = new Procedure("procedure6");
			plant.onRightClicked = emptyLists ? new Procedure("actionresulttype1") : new Procedure("procedure1");
			plant.onBlockAdded = new Procedure("procedure8");
			plant.onBlockPlacedBy = new Procedure("procedure9");
			plant.onRandomUpdateEvent = new Procedure("procedure10");
			plant.onEntityWalksOn = new Procedure("procedure11");
			plant.onHitByProjectile = new Procedure("procedure12");
			plant.placingCondition = _true ? null : new Procedure("condition2");
			plant.tintType = getRandomString(random,
					Arrays.asList("No tint", "Grass", "Foliage", "Birch foliage", "Spruce foliage", "Default foliage",
							"Water", "Sky", "Fog", "Water fog"));

			if ("double".equals(plant.plantType)) {
				plant.renderType = !"No tint".equals(plant.tintType) ? 120 : 12;
				plant.customModelName = "Cross model";
			} else {
				plant.renderType = new int[] { 13, !"No tint".equals(plant.tintType) ? 120 : 12, 13,
						!"No tint".equals(plant.tintType) ? 120 : 12 }[valueIndex];
				plant.customModelName = new String[] { "Crop model", "Cross model", "Crop model",
						"Cross model" }[valueIndex];
			}
			plant.isItemTinted = _true;
			if (!emptyLists) {
				plant.isBonemealable = true;
				plant.isBonemealTargetCondition = new Procedure("condition3");
				plant.bonemealSuccessCondition = new Procedure("condition4");
				plant.onBonemealSuccess = new Procedure("procedure13");
			}
			return plant;
		} else if (ModElementType.ITEM.equals(modElement.getType())) {
			Item item = new Item(modElement);
			item.name = modElement.getName();
			item.rarity = getRandomString(random, Arrays.asList("COMMON", "UNCOMMON", "RARE", "EPIC"));
			item.creativeTab = new TabEntry(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadAllTabs(modElement.getWorkspace())));
			item.stackSize = 52;
			item.enchantability = 3;
			item.useDuration = 8;
			item.toolType = 1.43;
			item.damageCount = 4;
			item.destroyAnyBlock = _true;
			item.inventorySize = 10;
			item.inventoryStackSize = 42;
			item.guiBoundTo = "<NONE>";
			item.recipeRemainder = new MItemBlock(modElement.getWorkspace(),
					emptyLists ? "" : getRandomMCItem(random, blocksAndItems).getName());
			item.stayInGridWhenCrafting = _true;
			item.damageOnCrafting = _true;
			item.immuneToFire = _true;
			item.hasGlow = _true;
			item.onRightClickedInAir = new Procedure("procedure1");
			item.onRightClickedOnBlock = emptyLists ? new Procedure("actionresulttype1") : new Procedure("procedure2");
			item.onCrafted = new Procedure("procedure3");
			item.onEntityHitWith = new Procedure("procedure4");
			item.onItemInInventoryTick = new Procedure("procedure5");
			item.onItemInUseTick = new Procedure("procedure6");
			item.onStoppedUsing = new Procedure("procedure7");
			item.onEntitySwing = new Procedure("procedure8");
			item.onDroppedByPlayer = new Procedure("procedure9");
			item.enableMeleeDamage = !_true;
			item.damageVsEntity = 6.53;

			if (!emptyLists) {
				item.specialInfo = StringUtils.splitCommaSeparatedStringListWithEscapes(
						"info 1, info 2, test \\, is this, another one");
			} else {
				item.specialInfo = new ArrayList<>();
			}
			item.texture = "test2";
			item.renderType = 0;
			item.customModelName = getRandomItem(random, ItemGUI.builtinitemmodels).getReadableName();

			item.customProperties = new HashMap<>();
			item.states = new ArrayList<>();
			if (!emptyLists) {
				int size1 = random.nextInt(3) + 1;
				for (int i = 1; i <= size1; i++)
					item.customProperties.put("property" + i, new Procedure("number" + i));

				int size2 = random.nextInt(4) + 1;
				for (int i = 0; i < size2; i++) {
					StateMap stateMap = new StateMap();

					for (int j = 2; j <= size1; j++) {
						if (random.nextBoolean()) {
							stateMap.put(new PropertyData.NumberType("CUSTOM:property" + j), random.nextDouble());
						}
					}

					Item.StateEntry stateEntry = new Item.StateEntry();
					stateEntry.customModelName = getRandomItem(random, ItemGUI.builtinitemmodels).getReadableName();
					stateEntry.texture = i == 0 ? "test" : "test" + i;
					stateEntry.renderType = 0;
					stateEntry.stateMap = stateMap;

					item.states.add(stateEntry);
				}
			}

			item.isFood = _true;
			item.nutritionalValue = 5;
			item.saturation = 0.82;
			item.isMeat = _true;
			item.isAlwaysEdible = _true;
			item.animation = getRandomItem(random,
					new String[] { "block", "bow", "crossbow", "drink", "eat", "none", "spear" });
			item.eatResultItem = new MItemBlock(modElement.getWorkspace(),
					emptyLists ? "" : getRandomMCItem(random, blocksAndItems).getName());
			item.onFinishUsingItem = new Procedure("procedure3");
			return item;
		} else if (ModElementType.ITEMEXTENSION.equals(modElement.getType())) {
			ItemExtension itemExtension = new ItemExtension(modElement);
			itemExtension.item = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItems).getName());

			itemExtension.enableFuel = !emptyLists;
			itemExtension.fuelPower = new NumberProcedure(_true ? "number1" : null, 1600);
			itemExtension.fuelSuccessCondition = _true ? new Procedure("condition1") : null;
			itemExtension.compostLayerChance = new double[] { 0d, 0.3d, 0.5d, 1d }[valueIndex];
			itemExtension.hasDispenseBehavior = emptyLists;
			itemExtension.dispenseSuccessCondition = _true ? new Procedure("condition1") : null;
			itemExtension.dispenseResultItemstack = _true ? new Procedure("itemstack1") : null;
			return itemExtension;
		} else if (ModElementType.RANGEDITEM.equals(modElement.getType())) {
			RangedItem rangedItem = new RangedItem(modElement);
			rangedItem.name = modElement.getName();
			rangedItem.creativeTab = new TabEntry(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadAllTabs(modElement.getWorkspace())));
			rangedItem.ammoItem = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItems).getName());
			rangedItem.specialInfo = new ArrayList<>();
			if (!emptyLists) {
				rangedItem.specialInfo = StringUtils.splitCommaSeparatedStringListWithEscapes(
						"info 1, info 2, test \\, is this, another one");
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
					getRandomMCItem(random, blocksAndItems).getName());
			rangedItem.onBulletHitsBlock = new Procedure("procedure2");
			rangedItem.onBulletHitsPlayer = new Procedure("procedure1");
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
			rangedItem.damageVsEntity = 2.16;
			return rangedItem;
		} else if (ModElementType.POTION.equals(modElement.getType())) {
			Potion potion = new Potion(modElement);
			potion.potionName = modElement.getName() + " Potion";
			potion.splashName = modElement.getName() + " Splash";
			potion.lingeringName = modElement.getName() + " Lingering";
			potion.arrowName = modElement.getName() + " Arrow";
			List<Potion.CustomEffectEntry> effects = new ArrayList<>();
			if (!emptyLists) {
				Potion.CustomEffectEntry entry1 = new Potion.CustomEffectEntry();
				entry1.effect = new EffectEntry(modElement.getWorkspace(),
						getRandomDataListEntry(random, ElementUtil.loadAllPotionEffects(modElement.getWorkspace())));
				entry1.duration = 3600;
				entry1.amplifier = 1;
				entry1.ambient = !_true;
				entry1.showParticles = !_true;
				effects.add(entry1);

				Potion.CustomEffectEntry entry2 = new Potion.CustomEffectEntry();
				entry2.effect = new EffectEntry(modElement.getWorkspace(),
						getRandomDataListEntry(random, ElementUtil.loadAllPotionEffects(modElement.getWorkspace())));
				entry2.duration = 7200;
				entry2.amplifier = 0;
				entry2.ambient = _true;
				entry2.showParticles = _true;
				effects.add(entry2);
			}
			potion.effects = effects;
			return potion;
		} else if (ModElementType.POTIONEFFECT.equals(modElement.getType())) {
			PotionEffect potionEffect = new PotionEffect(modElement);
			potionEffect.effectName = modElement.getName() + " Effect Name";
			potionEffect.color = Color.magenta;
			potionEffect.icon = "test.png";
			potionEffect.isInstant = !_true;
			potionEffect.isBad = _true;
			potionEffect.isBenefitical = !_true;
			potionEffect.renderStatusInHUD = _true;
			potionEffect.renderStatusInInventory = _true;
			potionEffect.onStarted = new Procedure("procedure1");
			potionEffect.onActiveTick = new Procedure("procedure2");
			potionEffect.onExpired = new Procedure("procedure3");
			potionEffect.activeTickCondition = new Procedure("condition1");
			return potionEffect;
		} else if (ModElementType.BLOCK.equals(modElement.getType())) {
			Block block = new Block(modElement);
			block.name = modElement.getName();
			block.hasTransparency = new boolean[] { _true, _true, true,
					!emptyLists }[valueIndex]; // third is true because third index for model is cross which requires transparency
			block.connectedSides = _true;
			block.displayFluidOverlay = _true;
			block.emissiveRendering = _true;
			block.transparencyType = new String[] { "SOLID", "CUTOUT", "CUTOUT_MIPPED", "TRANSLUCENT" }[valueIndex];
			block.disableOffset = !_true;
			block.boundingBoxes = new ArrayList<>();
			if (!emptyLists) {
				int boxes = random.nextInt(4) + 1;
				for (int i = 0; i < boxes; i++) {
					IBlockWithBoundingBox.BoxEntry box = new IBlockWithBoundingBox.BoxEntry();
					box.mx = new double[] { 0, 5 + i, 1.2, 7.1 }[valueIndex];
					box.my = new double[] { 0, 2, 3.6, 12.2 }[valueIndex];
					box.mz = new double[] { 0, 3.1, 0, 2.2 }[valueIndex];
					box.Mx = new double[] { 16, 15.2, 4, 7.1 + i }[valueIndex];
					box.My = new double[] { 16, 12.2, 16, 13 }[valueIndex];
					box.Mz = new double[] { 16, 12, 2.4, 1.2 }[valueIndex];
					box.subtract = new boolean[] { false, _true, _true, random.nextBoolean() }[valueIndex];

					block.boundingBoxes.add(box);
				}
			}
			block.rotationMode = new int[] { 0, 1, 4, 5 }[valueIndex];
			block.enablePitch = !_true;
			block.hardness = 2.3;
			block.resistance = 3.1;
			block.hasGravity = _true;
			block.useLootTableForDrops = !_true;
			block.requiresCorrectTool = _true;
			block.creativeTab = new TabEntry(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadAllTabs(modElement.getWorkspace())));
			block.destroyTool = getRandomItem(random,
					new String[] { "Not specified", "pickaxe", "axe", "shovel", "hoe" });
			block.customDrop = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItems).getName());
			block.flammability = 5;
			block.fireSpreadSpeed = 12;
			block.dropAmount = 3;
			block.plantsGrowOn = _true;
			block.isNotColidable = _true;
			block.canRedstoneConnect = _true;
			block.isWaterloggable = !block.hasGravity; // only works if block has no gravity, emptyLists for more randomness
			block.isLadder = _true;
			block.enchantPowerBonus = 1.2342;
			block.reactionToPushing = getRandomItem(random,
					new String[] { "NORMAL", "DESTROY", "BLOCK", "PUSH_ONLY", "IGNORE" });
			block.slipperiness = 12.342;
			block.speedFactor = 34.632;
			block.jumpFactor = 17.732;
			block.lightOpacity = new int[] { 7, 2, 0,
					3 }[valueIndex]; // third is 0 because third index for model is cross which requires transparency;
			block.material = new Material(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadMaterials()));
			block.tickRate = _true ? 0 : 24;
			block.isCustomSoundType = !_true;
			block.soundOnStep = new StepSound(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadStepSounds()));
			block.breakSound = new Sound(modElement.getWorkspace(),
					getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			block.stepSound = new Sound(modElement.getWorkspace(),
					getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			block.placeSound = new Sound(modElement.getWorkspace(),
					getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			block.hitSound = new Sound(modElement.getWorkspace(),
					getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			block.fallSound = new Sound(modElement.getWorkspace(),
					getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			block.luminance = 3;
			block.isReplaceable = !_true;
			block.canProvidePower = !_true;
			block.emittedRedstonePower = new NumberProcedure(emptyLists ? null : "number1", 8);
			block.creativePickItem = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocks).getName());
			block.colorOnMap = getRandomItem(random, ElementUtil.getDataListAsStringArray("mapcolors"));
			block.offsetType = getRandomString(random, Arrays.asList("NONE", "XZ", "XYZ"));
			block.aiPathNodeType = getRandomItem(random, ElementUtil.getDataListAsStringArray("pathnodetypes"));
			block.beaconColorModifier = emptyLists ? null : Color.cyan;
			block.unbreakable = _true;
			block.breakHarvestLevel = 4;
			block.tickRandomly = _true;
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
				block.restrictionBiomes.addAll(
						biomes.stream().skip(_true ? 0 : ((biomes.size() / 4) * valueIndex)).limit(biomes.size() / 4)
								.map(e -> new BiomeEntry(modElement.getWorkspace(), e.getName())).toList());
			}
			block.blocksToReplace = new ArrayList<>();
			if (!emptyLists) {
				block.blocksToReplace = new ArrayList<>(
						blocksAndTags.stream().skip(_true ? 0 : ((blocksAndTags.size() / 4) * valueIndex))
								.limit(blocksAndTags.size() / 4)
								.map(e -> new MItemBlock(modElement.getWorkspace(), e.getName())).toList());
				block.blocksToReplace.add(new MItemBlock(modElement.getWorkspace(), "TAG:flowers"));
			}
			block.generationShape = _true ? "UNIFORM" : "TRIANGLE";
			block.frequencyPerChunks = 6;
			block.frequencyOnChunk = 7;
			block.minGenerateHeight = 21;
			block.maxGenerateHeight = 92;
			if (!emptyLists) {
				block.isBonemealable = true;
				block.onBlockAdded = new Procedure("procedure10");
				block.onNeighbourBlockChanges = new Procedure("procedure2");
				block.onTickUpdate = new Procedure("procedure3");
				block.onRandomUpdateEvent = new Procedure("procedure4");
				block.onDestroyedByPlayer = new Procedure("procedure5");
				block.onDestroyedByExplosion = new Procedure("procedure6");
				block.onStartToDestroy = new Procedure("procedure7");
				block.onEntityCollides = new Procedure("procedure8");
				block.onBlockPlayedBy = new Procedure("procedure9");
				block.onRightClicked = _true ? new Procedure("actionresulttype1") : new Procedure("procedure1");
				block.onRedstoneOn = new Procedure("procedure11");
				block.onRedstoneOff = new Procedure("procedure12");
				block.onEntityWalksOn = new Procedure("procedure13");
				block.onHitByProjectile = new Procedure("procedure14");
				block.placingCondition = new Procedure("condition2");
				block.isBonemealTargetCondition = new Procedure("condition3");
				block.bonemealSuccessCondition = new Procedure("condition4");
				block.onBonemealSuccess = new Procedure("procedure15");
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
				block.specialInfo = StringUtils.splitCommaSeparatedStringListWithEscapes(
						"info 1, info 2, test \\, is this, another one");
			} else {
				block.specialInfo = new ArrayList<>();
			}
			block.tintType = getRandomString(random,
					Arrays.asList("No tint", "Grass", "Foliage", "Birch foliage", "Spruce foliage", "Default foliage",
							"Water", "Sky", "Fog", "Water fog"));
			block.isItemTinted = _true;
			block.renderType = new int[] { 10, block.isBlockTinted() ? 110 : 11, block.isBlockTinted() ? 120 : 12,
					14 }[valueIndex];
			block.customModelName = new String[] { "Normal", "Single texture", "Cross model",
					"Grass block" }[valueIndex];
			return block;
		} else if (ModElementType.TAG.equals(modElement.getType())) {
			Tag tag = new Tag(modElement);
			tag.namespace = getRandomItem(random, new String[] { "forge", "minecraft", "test1", "test2" });
			tag.type = getRandomItem(random, new String[] { "Items", "Blocks", "Entities", "Functions", "Biomes" });
			tag.name = modElement.getName();
			tag.items = new ArrayList<>();
			tag.blocks = new ArrayList<>();
			tag.functions = new ArrayList<>();
			tag.entities = new ArrayList<>();
			tag.biomes = new ArrayList<>();
			if (!emptyLists) {
				tag.items.addAll(
						blocksAndItems.stream().map(e -> new MItemBlock(modElement.getWorkspace(), e.getName()))
								.toList());
				tag.blocks.addAll(
						blocks.stream().map(e -> new MItemBlock(modElement.getWorkspace(), e.getName())).toList());
				tag.entities.addAll(ElementUtil.loadAllEntities(modElement.getWorkspace()).stream()
						.map(e -> new EntityEntry(modElement.getWorkspace(), e.getName())).toList());
				tag.biomes.addAll(ElementUtil.loadAllBiomes(modElement.getWorkspace()).stream()
						.map(e -> new BiomeEntry(modElement.getWorkspace(), e.getName())).toList());

				tag.functions.add("ExampleFunction1");
				tag.functions.add("ExampleFunction2");
			}
			return tag;
		} else if (ModElementType.LOOTTABLE.equals(modElement.getType())) {
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
								getRandomMCItem(random, filterAir(blocksAndItems)).getName());

						pool.entries.add(entry);
					}

					lootTable.pools.add(pool);
				}
			}

			return lootTable;
		} else if (ModElementType.FUNCTION.equals(modElement.getType())) {
			Function function = new Function(modElement);
			function.name = modElement.getName().toLowerCase(Locale.ENGLISH);
			function.namespace = getRandomItem(random, new String[] { "minecraft", "mod" });
			function.code = "execute as @a at @s run function custom:test\n";
			return function;
		} else if (ModElementType.MUSICDISC.equals(modElement.getType())) {
			MusicDisc musicDisc = new MusicDisc(modElement);
			musicDisc.name = modElement.getName();
			musicDisc.description = modElement.getName();
			musicDisc.creativeTab = new TabEntry(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadAllTabs(modElement.getWorkspace())));
			musicDisc.hasGlow = _true;
			musicDisc.onRightClickedInAir = new Procedure("procedure1");
			musicDisc.onRightClickedOnBlock = emptyLists ?
					new Procedure("actionresulttype1") :
					new Procedure("procedure2");
			musicDisc.onCrafted = new Procedure("procedure3");
			musicDisc.onEntityHitWith = new Procedure("procedure4");
			musicDisc.onItemInInventoryTick = new Procedure("procedure5");
			musicDisc.onItemInUseTick = new Procedure("procedure6");
			musicDisc.onEntitySwing = new Procedure("procedure8");
			musicDisc.lengthInTicks = 13;
			musicDisc.analogOutput = 6;
			musicDisc.music = new Sound(modElement.getWorkspace(),
					getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			if (!emptyLists) {
				musicDisc.specialInfo = StringUtils.splitCommaSeparatedStringListWithEscapes(
						"info 1, info 2, test \\, is this, another one");
			} else {
				musicDisc.specialInfo = new ArrayList<>();
			}
			musicDisc.texture = "itest";
			return musicDisc;
		} else if (ModElementType.ENCHANTMENT.equals(modElement.getType())) {
			Enchantment enchantment = new Enchantment(modElement);
			enchantment.name = modElement.getName().toLowerCase(Locale.ENGLISH);
			enchantment.rarity = getRandomItem(random, new String[] { "COMMON", "UNCOMMON", "RARE", "VERY_RARE" });
			enchantment.type = getRandomString(random,
					ElementUtil.loadEnchantmentTypes().stream().map(DataListEntry::getName).toList());
			enchantment.minLevel = 13;
			enchantment.maxLevel = 45;
			enchantment.damageModifier = 3;
			enchantment.isTreasureEnchantment = _true;
			enchantment.isAllowedOnBooks = !_true;
			enchantment.isCurse = _true;
			enchantment.canGenerateInLootTables = !_true;
			enchantment.canVillagerTrade = _true;
			enchantment.compatibleItems = new ArrayList<>();
			if (!emptyLists) {
				enchantment.compatibleItems = new ArrayList<>(blocksAndItemsAndTags.stream()
						.skip(_true ? 0 : ((long) (blocksAndItemsAndTags.size() / 4) * valueIndex))
						.limit(blocksAndItemsAndTags.size() / 4)
						.map(e -> new MItemBlock(modElement.getWorkspace(), e.getName())).toList());
				enchantment.compatibleItems.add(new MItemBlock(modElement.getWorkspace(), "TAG:flowers"));
				enchantment.excludeEnchantments = _true;
			}
			enchantment.compatibleEnchantments = new ArrayList<>();
			if (!emptyLists) {
				enchantment.compatibleEnchantments.addAll(
						ElementUtil.loadAllEnchantments(modElement.getWorkspace()).stream()
								.map(e -> new net.mcreator.element.parts.Enchantment(modElement.getWorkspace(),
										e.getName())).toList());
				enchantment.excludeItems = _true;
			}
			return enchantment;
		} else if (ModElementType.PAINTING.equals(modElement.getType())) {
			Painting painting = new Painting(modElement);
			painting.texture = "test.png";
			painting.title = modElement.getName();
			painting.author = modElement.getName() + " author";
			painting.width = 16;
			painting.height = 16;
			return painting;
		} else if (ModElementType.PARTICLE.equals(modElement.getType())) {
			net.mcreator.element.types.Particle particle = new net.mcreator.element.types.Particle(modElement);
			particle.texture = "test.png";
			particle.width = 2.3;
			particle.frameDuration = 2;
			particle.height = 1.38;
			particle.scale = 1.38;
			particle.gravity = 12.3;
			particle.speedFactor = 1.3;
			particle.canCollide = _true;
			particle.angularVelocity = 0.23;
			particle.angularAcceleration = -0.09;
			particle.alwaysShow = !_true;
			particle.animate = _true;
			particle.maxAge = 12;
			particle.maxAgeDiff = emptyLists ? 0 : 15;
			particle.renderType = new String[] { "OPAQUE", "OPAQUE", "TRANSLUCENT", "LIT" }[valueIndex];
			particle.additionalExpiryCondition = new Procedure("condition1");
			return particle;
		} else if (ModElementType.GAMERULE.equals(modElement.getType())) {
			GameRule gamerule = new GameRule(modElement);
			gamerule.displayName = modElement.getName();
			gamerule.description = modElement.getName() + " description";
			gamerule.category = getRandomString(random,
					Arrays.asList("PLAYER", "UPDATES", "CHAT", "DROPS", "MISC", "MOBS", "SPAWNING"));
			gamerule.type = new String[] { "Number", "Logic", "Number", "Logic" }[valueIndex];
			gamerule.defaultValueLogic = _true;
			gamerule.defaultValueNumber = -45;
			gamerule.getModElement().putMetadata("type", "Number".equals(gamerule.type) ?
					VariableTypeLoader.BuiltInTypes.NUMBER.getName() :
					VariableTypeLoader.BuiltInTypes.LOGIC.getName());
			return gamerule;
		} else if (ModElementType.VILLAGERPROFESSION.equals(modElement.getType())) {
			VillagerProfession profession = new VillagerProfession(modElement);
			profession.displayName = modElement.getName();
			List<MItemBlock> poiBlocks = ElementUtil.loadAllPOIBlocks(modElement.getWorkspace());
			profession.pointOfInterest = new MItemBlock(modElement.getWorkspace(), getRandomMCItem(random,
					blocks.stream()
							.filter(e -> !poiBlocks.contains(new MItemBlock(modElement.getWorkspace(), e.getName())))
							.collect(Collectors.toList())).getName());
			profession.actionSound = new Sound(modElement.getWorkspace(),
					getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			profession.hat = getRandomString(random, Arrays.asList("None", "Partial", "Full"));
			profession.professionTextureFile = "test.png";
			profession.zombifiedProfessionTextureFile = "test.png";
			return profession;
		} else if (ModElementType.VILLAGERTRADE.equals(modElement.getType())) {
			VillagerTrade villagerTrade = new VillagerTrade(modElement);
			villagerTrade.tradeEntries = new ArrayList<>();
			if (!emptyLists) {
				int tradeEntries = random.nextInt(10) + 1;
				for (int i = 0; i < tradeEntries; i++) {
					VillagerTrade.CustomTradeEntry trade = new VillagerTrade.CustomTradeEntry();
					trade.villagerProfession = new ProfessionEntry(modElement.getWorkspace(),
							getRandomDataListEntry(random,
									ElementUtil.loadAllVillagerProfessions(modElement.getWorkspace())));
					trade.entries = new ArrayList<>();

					int entries = random.nextInt(10) + 1;
					for (int j = 0; j < entries; j++) {
						VillagerTrade.CustomTradeEntry.Entry entry = new VillagerTrade.CustomTradeEntry.Entry();
						entry.price1 = new MItemBlock(modElement.getWorkspace(),
								getRandomMCItem(random, filterAir(blocksAndItems)).getName());
						entry.price2 = new MItemBlock(modElement.getWorkspace(),
								_true ? getRandomMCItem(random, blocksAndItems).getName() : "");
						entry.offer = new MItemBlock(modElement.getWorkspace(),
								getRandomMCItem(random, filterAir(blocksAndItems)).getName());
						entry.countPrice1 = new int[] { 3, 57, 34, 28 }[valueIndex];
						entry.countPrice2 = new int[] { 9, 61, 17, 45 }[valueIndex];
						entry.countOffer = new int[] { 8, 13, 23, 60 }[valueIndex];
						entry.level = new int[] { 1, 2, 3, 4, 5 }[valueIndex];
						entry.maxTrades = new int[] { 3, 10, 46, 27 }[valueIndex];
						entry.xp = new int[] { 2, 5, 10, 15 }[valueIndex];
						entry.priceMultiplier = new double[] { 0.01, 0.05, 0.1, 0.5 }[valueIndex];

						trade.entries.add(entry);
					}
					VillagerTrade.CustomTradeEntry wanderingTrade = new VillagerTrade.CustomTradeEntry();
					wanderingTrade.villagerProfession = new ProfessionEntry(modElement.getWorkspace(),
							"WANDERING_TRADER");
					wanderingTrade.entries = new ArrayList<>();

					int wanderingEntries = random.nextInt(10) + 1;
					for (int j = 0; j < wanderingEntries; j++) {
						VillagerTrade.CustomTradeEntry.Entry entry = new VillagerTrade.CustomTradeEntry.Entry();
						entry.price1 = new MItemBlock(modElement.getWorkspace(),
								getRandomMCItem(random, filterAir(blocksAndItems)).getName());
						entry.price2 = new MItemBlock(modElement.getWorkspace(),
								_true ? getRandomMCItem(random, blocksAndItems).getName() : "");
						entry.offer = new MItemBlock(modElement.getWorkspace(),
								getRandomMCItem(random, filterAir(blocksAndItems)).getName());
						entry.countPrice1 = new int[] { 3, 57, 34, 28 }[valueIndex];
						entry.countPrice2 = new int[] { 9, 61, 17, 45 }[valueIndex];
						entry.countOffer = new int[] { 8, 13, 23, 60 }[valueIndex];
						entry.level = new int[] { 1, 2, 3, 4, 5 }[valueIndex];
						entry.maxTrades = new int[] { 3, 10, 46, 27 }[valueIndex];
						entry.xp = new int[] { 2, 5, 10, 15 }[valueIndex];
						entry.priceMultiplier = new double[] { 0.01, 0.05, 0.1, 0.5 }[valueIndex];

						wanderingTrade.entries.add(entry);
					}
					villagerTrade.tradeEntries.add(trade);
					villagerTrade.tradeEntries.add(wanderingTrade);
				}
			}
			return villagerTrade;
		} else if (ModElementType.PROCEDURE.equals(modElement.getType())) {
			net.mcreator.element.types.Procedure procedure = new net.mcreator.element.types.Procedure(modElement);
			procedure.procedurexml = net.mcreator.element.types.Procedure.XML_BASE;
			return procedure;
		} else if (ModElementType.COMMAND.equals(modElement.getType())) {
			Command command = new Command(modElement);
			command.commandName = modElement.getName();
			command.permissionLevel = getRandomString(random, List.of("No requirement", "1", "2", "3", "4"));
			command.argsxml = Command.XML_BASE;
			return command;
		}
		// As feature requires placement and feature to place, this GE is only returned for uiTests
		// For generator tests, it will be tested by GTFeatureBlocks anyway
		else if (ModElementType.FEATURE.equals(modElement.getType()) && uiTest) {
			Feature feature = new Feature(modElement);
			feature.generationStep = TestWorkspaceDataProvider.getRandomItem(random,
					ElementUtil.getDataListAsStringArray("generationsteps"));
			feature.restrictionDimensions = emptyLists ?
					new ArrayList<>() :
					new ArrayList<>(Arrays.asList("Surface", "Nether"));
			feature.restrictionBiomes = new ArrayList<>();
			if (!emptyLists) {
				feature.restrictionBiomes.addAll(
						biomes.stream().skip(_true ? 0 : ((long) (biomes.size() / 4) * valueIndex))
								.limit(biomes.size() / 4)
								.map(e -> new BiomeEntry(modElement.getWorkspace(), e.getName())).toList());
			}
			feature.generateCondition = _true ? new Procedure("condition1") : null;
			feature.featurexml = Feature.XML_BASE;
			return feature;
		}
		return null;
	}

	public static LivingEntity getLivingEntity(ModElement modElement, Random random, boolean _true, boolean emptyLists,
			int valueIndex, List<MCItem> blocksAndItems, List<DataListEntry> biomes) {
		LivingEntity livingEntity = new LivingEntity(modElement);
		livingEntity.mobName = modElement.getName();
		livingEntity.mobLabel = "mod label " + StringUtils.machineToReadableName(modElement.getName());
		livingEntity.mobModelTexture = "test.png";
		livingEntity.mobModelGlowTexture = emptyLists ? "" : "test.png";
		livingEntity.transparentModelCondition = new Procedure("condition1");
		livingEntity.isShakingCondition = new Procedure("condition2");
		livingEntity.solidBoundingBox = new LogicProcedure(_true ? "condition3" : null, _true);
		livingEntity.mobModelName = getRandomItem(random, LivingEntityGUI.builtinmobmodels).getReadableName();
		livingEntity.spawnEggBaseColor = Color.red;
		livingEntity.spawnEggDotColor = Color.green;
		livingEntity.isBoss = _true;
		livingEntity.creativeTab = new TabEntry(modElement.getWorkspace(),
				getRandomDataListEntry(random, ElementUtil.loadAllTabs(modElement.getWorkspace())));
		livingEntity.bossBarColor = getRandomItem(random,
				new String[] { "PINK", "BLUE", "RED", "GREEN", "YELLOW", "PURPLE", "WHITE" });
		livingEntity.bossBarType = getRandomItem(random,
				new String[] { "PROGRESS", "NOTCHED_6", "NOTCHED_10", "NOTCHED_12", "NOTCHED_20" });
		livingEntity.equipmentMainHand = new MItemBlock(modElement.getWorkspace(),
				getRandomMCItem(random, blocksAndItems).getName());
		livingEntity.equipmentOffHand = new MItemBlock(modElement.getWorkspace(),
				getRandomMCItem(random, blocksAndItems).getName());
		livingEntity.equipmentHelmet = new MItemBlock(modElement.getWorkspace(),
				getRandomMCItem(random, blocksAndItems).getName());
		livingEntity.equipmentBody = new MItemBlock(modElement.getWorkspace(),
				getRandomMCItem(random, blocksAndItems).getName());
		livingEntity.equipmentLeggings = new MItemBlock(modElement.getWorkspace(),
				getRandomMCItem(random, blocksAndItems).getName());
		livingEntity.equipmentBoots = new MItemBlock(modElement.getWorkspace(),
				getRandomMCItem(random, blocksAndItems).getName());
		livingEntity.mobBehaviourType = _true ? "Creature" : "Mob";
		livingEntity.mobCreatureType = getRandomItem(random,
				new String[] { "UNDEFINED", "UNDEAD", "ARTHROPOD", "ILLAGER", "WATER" });
		livingEntity.attackStrength = 4;
		livingEntity.attackKnockback = 1.5;
		livingEntity.knockbackResistance = 0.5;
		livingEntity.movementSpeed = 0.76;
		livingEntity.stepHeight = 2.24;
		livingEntity.armorBaseValue = 0.123;
		livingEntity.health = 42;
		livingEntity.trackingRange = 27;
		livingEntity.followRange = 11;
		livingEntity.waterMob = !_true;
		livingEntity.flyingMob = !_true;
		livingEntity.inventorySize = 10;
		livingEntity.inventoryStackSize = 42;
		livingEntity.disableCollisions = !_true;
		livingEntity.immuneToFire = _true;
		livingEntity.immuneToArrows = !_true;
		livingEntity.immuneToFallDamage = !_true;
		livingEntity.immuneToCactus = !_true;
		livingEntity.immuneToDrowning = !_true;
		livingEntity.immuneToLightning = !_true;
		livingEntity.immuneToPotions = !_true;
		livingEntity.immuneToPlayer = !_true;
		livingEntity.immuneToExplosion = !_true;
		livingEntity.immuneToTrident = !_true;
		livingEntity.immuneToAnvil = !_true;
		livingEntity.immuneToDragonBreath = !_true;
		livingEntity.immuneToWither = !_true;
		livingEntity.hasSpawnEgg = !_true;
		livingEntity.xpAmount = 8;
		livingEntity.ridable = _true;
		livingEntity.canControlStrafe = !_true;
		livingEntity.canControlForward = _true;
		livingEntity.guiBoundTo = "<NONE>";
		livingEntity.mobDrop = new MItemBlock(modElement.getWorkspace(),
				getRandomMCItem(random, blocksAndItems).getName());
		livingEntity.livingSound = new Sound(modElement.getWorkspace(),
				getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
		livingEntity.hurtSound = new Sound(modElement.getWorkspace(),
				getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
		livingEntity.deathSound = new Sound(modElement.getWorkspace(),
				getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
		livingEntity.stepSound = new Sound(modElement.getWorkspace(),
				emptyLists ? "" : getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
		livingEntity.rangedItemType = "Default item";
		if (!emptyLists) {
			livingEntity.spawningCondition = new Procedure("condition3");
			livingEntity.onStruckByLightning = new Procedure("procedure1");
			livingEntity.whenMobFalls = new Procedure("procedure2");
			livingEntity.whenMobDies = new Procedure("procedure3");
			livingEntity.whenMobIsHurt = new Procedure("procedure4");
			livingEntity.onRightClickedOn = _true ? new Procedure("actionresulttype1") : new Procedure("procedure5");
			livingEntity.whenThisMobKillsAnother = new Procedure("procedure6");
			livingEntity.onMobTickUpdate = new Procedure("procedure7");
			livingEntity.onPlayerCollidesWith = new Procedure("procedure8");
			livingEntity.onInitialSpawn = new Procedure("procedure9");
		}
		livingEntity.hasAI = _true;
		livingEntity.aiBase = "(none)";
		livingEntity.aixml = "<xml xmlns=\"https://developers.google.com/blockly/xml\"><block type=\"aitasks_container\" deletable=\"false\" x=\"40\" y=\"40\"></block></xml>";
		livingEntity.breedable = _true;
		livingEntity.tameable = _true;
		livingEntity.breedTriggerItems = new ArrayList<>();
		if (!emptyLists) {
			livingEntity.breedTriggerItems.add(
					new MItemBlock(modElement.getWorkspace(), getRandomMCItem(random, blocksAndItems).getName()));
			livingEntity.breedTriggerItems.add(
					new MItemBlock(modElement.getWorkspace(), getRandomMCItem(random, blocksAndItems).getName()));
			livingEntity.breedTriggerItems.add(
					new MItemBlock(modElement.getWorkspace(), getRandomMCItem(random, blocksAndItems).getName()));
			livingEntity.breedTriggerItems.add(
					new MItemBlock(modElement.getWorkspace(), getRandomMCItem(random, blocksAndItems).getName()));
			livingEntity.breedTriggerItems.add(
					new MItemBlock(modElement.getWorkspace(), getRandomMCItem(random, blocksAndItems).getName()));
		}
		livingEntity.ranged = _true;
		livingEntity.rangedAttackItem = new MItemBlock(modElement.getWorkspace(),
				getRandomMCItem(random, blocksAndItems).getName());
		livingEntity.rangedAttackInterval = 15;
		livingEntity.rangedAttackRadius = 8.75;
		livingEntity.spawnThisMob = !_true;
		livingEntity.doesDespawnWhenIdle = _true;
		livingEntity.spawningProbability = 23;
		livingEntity.mobSpawningType = getRandomItem(random, ElementUtil.getDataListAsStringArray("mobspawntypes"));
		livingEntity.minNumberOfMobsPerGroup = 4;
		livingEntity.maxNumberOfMobsPerGroup = 40;
		livingEntity.restrictionBiomes = new ArrayList<>();
		if (!emptyLists) {
			livingEntity.restrictionBiomes.addAll(
					biomes.stream().skip(_true ? 0 : ((long) (biomes.size() / 4) * valueIndex)).limit(biomes.size() / 4)
							.map(e -> new BiomeEntry(modElement.getWorkspace(), e.getName())).toList());
		}
		livingEntity.spawnInDungeons = _true;
		livingEntity.modelWidth = 0.4;
		livingEntity.modelHeight = 1.3;
		livingEntity.mountedYOffset = -3.1;
		livingEntity.modelShadowSize = 1.8;
		return livingEntity;
	}

	private static GeneratableElement getToolExample(ModElement modElement, String toolType, Random random,
			boolean _true, boolean emptyLists) {
		Tool tool = new Tool(modElement);
		tool.name = modElement.getName();
		tool.creativeTab = new TabEntry(modElement.getWorkspace(),
				getRandomDataListEntry(random, ElementUtil.loadAllTabs(modElement.getWorkspace())));
		tool.toolType = toolType;
		tool.harvestLevel = 3;
		tool.efficiency = 6.5;
		tool.attackSpeed = 4.8;
		tool.enchantability = 4;
		tool.damageVsEntity = 2.45;
		tool.usageCount = 24;
		tool.stayInGridWhenCrafting = _true;
		tool.damageOnCrafting = emptyLists;
		tool.immuneToFire = _true;
		tool.blocksAffected = new ArrayList<>();
		tool.hasGlow = _true;
		tool.specialInfo = new ArrayList<>();
		if (!emptyLists) {
			tool.specialInfo = StringUtils.splitCommaSeparatedStringListWithEscapes(
					"info 1, info 2, test \\, is this, another one");
		} else {
			tool.specialInfo = new ArrayList<>();
		}
		if (!emptyLists) {
			List<MCItem> blocksAndTags = ElementUtil.loadBlocksAndTags(modElement.getWorkspace());
			tool.blocksAffected.addAll(
					blocksAndTags.stream().map(e -> new MItemBlock(modElement.getWorkspace(), e.getName())).toList());
			tool.blocksAffected.add(new MItemBlock(modElement.getWorkspace(), "TAG:flowers"));
			tool.blocksAffected.add(new MItemBlock(modElement.getWorkspace(), "TAG:minecraft/test/path"));
		}
		tool.repairItems = new ArrayList<>();
		if (!emptyLists) {
			List<MCItem> blocksAndItemsAndTags = ElementUtil.loadBlocksAndItemsAndTags(modElement.getWorkspace());
			tool.repairItems.addAll(
					blocksAndItemsAndTags.stream().map(e -> new MItemBlock(modElement.getWorkspace(), e.getName()))
							.toList());
			tool.repairItems.add(new MItemBlock(modElement.getWorkspace(), "TAG:flowers"));
			tool.repairItems.add(new MItemBlock(modElement.getWorkspace(), "TAG:minecraft/test/path"));
		}
		tool.onRightClickedInAir = new Procedure("procedure1");
		tool.onRightClickedOnBlock = emptyLists ? new Procedure("actionresulttype1") : new Procedure("procedure2");
		tool.onCrafted = new Procedure("procedure3");
		tool.onBlockDestroyedWithTool = new Procedure("procedure4");
		tool.onEntityHitWith = new Procedure("procedure5");
		tool.onItemInInventoryTick = new Procedure("procedure6");
		tool.onItemInUseTick = new Procedure("procedure7");
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
		recipe.cookingBookCategory = getRandomItem(random, new String[] { "MISC", "FOOD", "BLOCKS" });
		recipe.craftingBookCategory = getRandomItem(random,
				new String[] { "MISC", "BUILDING", "REDSTONE", "EQUIPMENT" });
		recipe.recipeType = recipeType;

		List<MCItem> blocksAndItemsAndTags = ElementUtil.loadBlocksAndItemsAndTags(modElement.getWorkspace());
		List<MCItem> blocksAndItems = ElementUtil.loadBlocksAndItems(modElement.getWorkspace());

		switch (recipe.recipeType) {
		case "Crafting" -> {
			MItemBlock[] recipeSlots = new MItemBlock[9];
			Arrays.fill(recipeSlots, new MItemBlock(modElement.getWorkspace(), ""));
			recipeSlots[0] = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItemsAndTags).getName());
			if (random.nextBoolean())
				recipeSlots[3] = new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, blocksAndItemsAndTags).getName());
			if (random.nextBoolean())
				recipeSlots[6] = new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, blocksAndItemsAndTags).getName());
			if (random.nextBoolean())
				recipeSlots[1] = new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, blocksAndItemsAndTags).getName());
			if (random.nextBoolean())
				recipeSlots[4] = new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, blocksAndItemsAndTags).getName());
			if (random.nextBoolean())
				recipeSlots[7] = new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, blocksAndItemsAndTags).getName());
			if (random.nextBoolean())
				recipeSlots[2] = new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, blocksAndItemsAndTags).getName());
			if (random.nextBoolean())
				recipeSlots[5] = new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, blocksAndItemsAndTags).getName());
			if (random.nextBoolean())
				recipeSlots[8] = new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, blocksAndItemsAndTags).getName());
			recipe.recipeRetstackSize = 11;
			recipe.recipeShapeless = _true;
			recipe.recipeReturnStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItems).getName());
			recipe.recipeSlots = recipeSlots;
		}
		case "Smelting" -> {
			recipe.smeltingInputStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItemsAndTags).getName());
			recipe.smeltingReturnStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItems).getName());
			recipe.xpReward = 1.234;
			recipe.cookingTime = 123;
		}
		case "Smoking" -> {
			recipe.smokingInputStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItemsAndTags).getName());
			recipe.smokingReturnStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItems).getName());
			recipe.xpReward = 1.34;
			recipe.cookingTime = 42;
		}
		case "Blasting" -> {
			recipe.blastingInputStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItemsAndTags).getName());
			recipe.blastingReturnStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItems).getName());
			recipe.xpReward = 6.45;
			recipe.cookingTime = 1000;
		}
		case "Stone cutting" -> {
			recipe.stoneCuttingInputStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItemsAndTags).getName());
			recipe.stoneCuttingReturnStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItems).getName());
			recipe.recipeRetstackSize = 32;
		}
		case "Campfire cooking" -> {
			recipe.campfireCookingInputStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItemsAndTags).getName());
			recipe.campfireCookingReturnStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItems).getName());
			recipe.xpReward = 24.234;
			recipe.cookingTime = 2983;
		}
		case "Smithing" -> {
			recipe.smithingInputStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItemsAndTags).getName());
			recipe.smithingInputAdditionStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItemsAndTags).getName());
			recipe.smithingReturnStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItems).getName());
		}
		case "Brewing" -> {
			recipe.brewingInputStack = new MItemBlock(modElement.getWorkspace(), getRandomMCItem(random,
					ElementUtil.loadBlocksAndItemsAndTagsAndPotions(modElement.getWorkspace())).getName());
			recipe.brewingIngredientStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItemsAndTags).getName());
			recipe.brewingReturnStack = new MItemBlock(modElement.getWorkspace(), getRandomMCItem(random,
					ElementUtil.loadBlocksAndItemsAndPotions(modElement.getWorkspace())).getName());
		}
		default -> throw new RuntimeException("Unknown recipe type");
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

	private static List<MCItem> filterAir(List<MCItem> source) {
		return source.stream()
				.filter(e -> !(e.getName().equals("Blocks.AIR") || e.getName().equals("Blocks.VOID_AIR") || e.getName()
						.equals("Blocks.CAVE_AIR"))).toList();
	}

}
