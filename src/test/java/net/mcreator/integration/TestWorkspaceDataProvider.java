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

import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.parts.*;
import net.mcreator.element.parts.Particle;
import net.mcreator.element.parts.gui.*;
import net.mcreator.element.parts.gui.Button;
import net.mcreator.element.parts.gui.Checkbox;
import net.mcreator.element.parts.gui.Image;
import net.mcreator.element.parts.gui.Label;
import net.mcreator.element.parts.gui.TextField;
import net.mcreator.element.parts.procedure.*;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.types.*;
import net.mcreator.element.types.Dimension;
import net.mcreator.element.types.Enchantment;
import net.mcreator.element.types.Fluid;
import net.mcreator.element.types.bedrock.BEBlock;
import net.mcreator.element.types.bedrock.BEItem;
import net.mcreator.element.types.bedrock.BEScript;
import net.mcreator.element.types.interfaces.IBlockWithBoundingBox;
import net.mcreator.element.types.interfaces.NumericParameter;
import net.mcreator.element.util.AnnotationUtils;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.generator.mapping.MappableElement;
import net.mcreator.generator.mapping.NameMapper;
import net.mcreator.integration.generator.GTProcedureBlocks;
import net.mcreator.io.FileIO;
import net.mcreator.minecraft.*;
import net.mcreator.ui.action.impl.workspace.resources.AnimationImportActions;
import net.mcreator.ui.action.impl.workspace.resources.ModelImportActions;
import net.mcreator.ui.dialogs.wysiwyg.AbstractWYSIWYGDialog;
import net.mcreator.ui.minecraft.states.PropertyData;
import net.mcreator.ui.minecraft.states.PropertyDataWithValue;
import net.mcreator.ui.minecraft.states.StateMap;
import net.mcreator.ui.minecraft.states.block.BlockStatePropertyUtils;
import net.mcreator.ui.modgui.BlockGUI;
import net.mcreator.ui.modgui.ItemGUI;
import net.mcreator.ui.modgui.LivingEntityGUI;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.ListUtils;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.EmptyIcon;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.SoundElement;
import net.mcreator.workspace.elements.TagElement;
import net.mcreator.workspace.elements.VariableTypeLoader;
import net.mcreator.workspace.settings.WorkspaceSettings;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Range;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestWorkspaceDataProvider {

	public static Collection<ModElementType<?>> getOrderedModElementTypesForTests(
			GeneratorConfiguration generatorConfiguration) {
		Set<ModElementType<?>> retval = new LinkedHashSet<>();

		// We try to provide order so METs that depend on less of other MEs are first
		// So later MEs can reference them, improving test coverage
		retval.add(ModElementType.FUNCTION);
		retval.add(ModElementType.DAMAGETYPE);
		retval.add(ModElementType.GAMERULE);
		retval.add(ModElementType.ENCHANTMENT);
		retval.add(ModElementType.PARTICLE);
		retval.add(ModElementType.TAB);
		retval.add(ModElementType.PROJECTILE);
		retval.add(ModElementType.GUI);
		retval.add(ModElementType.ATTRIBUTE);
		retval.add(ModElementType.POTIONEFFECT);
		retval.add(ModElementType.BANNERPATTERN);
		retval.add(ModElementType.BESCRIPT);

		Collection<ModElementType<?>> supportedMETs = generatorConfiguration.getGeneratorStats()
				.getSupportedModElementTypes();

		// Remove METs not supported by the generator
		retval.retainAll(supportedMETs);

		// Add remaining types
		retval.addAll(supportedMETs);
		return retval;
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
			generatableElements.add(getToolExample(me(workspace, type, "13"), "Shield", random, true, false));
		} else if (type == ModElementType.BLOCK) {
			generatableElements.add(getBlockExample(me(workspace, type, "1"), random, true, true, 0, null));
			generatableElements.add(getBlockExample(me(workspace, type, "2"), random, true, false, 1, null));
			generatableElements.add(getBlockExample(me(workspace, type, "3"), random, false, true, 2, null));
			generatableElements.add(getBlockExample(me(workspace, type, "4"), random, false, false, 3, null));
			generatableElements.add(getBlockExample(me(workspace, type, "5"), random, true, true, 3, null));
			generatableElements.add(getBlockExample(me(workspace, type, "6"), random, true, false, 2, null));
			generatableElements.add(getBlockExample(me(workspace, type, "7"), random, false, true, 1, null));
			generatableElements.add(getBlockExample(me(workspace, type, "8"), random, false, false, 0, null));
			// due to numerous contraints in the UIs, we only test block bases in non-UI tests
			if (!uiTest) {
				int idx = 8;
				for (String blockBase : BlockGUI.blockBases) {
					generatableElements.add(
							getBlockExample(me(workspace, type, "" + ++idx), random, true, false, 1, blockBase));
				}
			}
		} else if (type == ModElementType.TAB || type == ModElementType.VILLAGERPROFESSION
				|| type == ModElementType.GAMERULE || type == ModElementType.BANNERPATTERN
				|| type == ModElementType.DAMAGETYPE) {
			generatableElements.add(getExampleFor(me(workspace, type, "1"), uiTest, random, true, true, 0));
			generatableElements.add(getExampleFor(me(workspace, type, "2"), uiTest, random, false, false, 1));
		} else if (type == ModElementType.COMMAND) {
			generatableElements.add(getCommandExample(me(workspace, type, "1"), "STANDARD", random));
			generatableElements.add(getCommandExample(me(workspace, type, "2"), "SINGLEPLAYER_ONLY", random));
			generatableElements.add(getCommandExample(me(workspace, type, "3"), "MULTIPLAYER_ONLY", random));
			generatableElements.add(getCommandExample(me(workspace, type, "4"), "CLIENTSIDE", random));
		} else if (type == ModElementType.SPECIALENTITY) {
			generatableElements.add(getSpecialEntityExample(me(workspace, type, "1"), "Boat", false));
			generatableElements.add(getSpecialEntityExample(me(workspace, type, "2"), "Boat", true));
			generatableElements.add(getSpecialEntityExample(me(workspace, type, "3"), "ChestBoat", false));
			generatableElements.add(getSpecialEntityExample(me(workspace, type, "4"), "ChestBoat", true));
		} else if (type == ModElementType.FUNCTION || type == ModElementType.PAINTING || type == ModElementType.KEYBIND
				|| type == ModElementType.PROCEDURE || type == ModElementType.FEATURE || type == ModElementType.CODE) {
			generatableElements.add(
					getExampleFor(new ModElement(workspace, "Example" + type.getRegistryName(), type), uiTest, random,
							true, true, 0));
		} else if (type == ModElementType.ADVANCEMENT || type == ModElementType.ITEMEXTENSION
				|| type == ModElementType.STRUCTURE || type == ModElementType.BEITEM || type == ModElementType.BEBLOCK
				|| type == ModElementType.BESCRIPT) {
			generatableElements.add(getExampleFor(me(workspace, type, "1"), uiTest, random, true, true, 0));
			generatableElements.add(getExampleFor(me(workspace, type, "2"), uiTest, random, true, false, 1));
			generatableElements.add(getExampleFor(me(workspace, type, "3"), uiTest, random, false, true, 2));
			generatableElements.add(getExampleFor(me(workspace, type, "4"), uiTest, random, false, false, 3));
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

	private static void fillWorkspaceWithResourcesAndData(Workspace workspace) {
		if (workspace.getGeneratorStats().hasBaseCoverage("sounds")) {
			for (int i = 1; i <= 3; i++) {
				SoundElement sound = new SoundElement("test" + i, List.of(), "neutral", null);
				workspace.addSoundElement(sound);
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
					workspace.getFolderManager().getTextureFile("other0", TextureType.OTHER));
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getTextureFile("example", TextureType.OTHER));
		}

		if (workspace.getFolderManager().getTexturesFolder(TextureType.ENTITY) != null) {
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getTextureFile("entity_texture_0", TextureType.ENTITY));
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getTextureFile("entity_texture_1", TextureType.ENTITY));
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getTextureFile("entity_texture_2", TextureType.ENTITY));
		}

		if (workspace.getFolderManager().getTexturesFolder(TextureType.EFFECT) != null) {
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getTextureFile("effect1", TextureType.EFFECT));
		}

		if (workspace.getFolderManager().getTexturesFolder(TextureType.PARTICLE) != null) {
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getTextureFile("particle1", TextureType.PARTICLE));
		}

		if (workspace.getFolderManager().getTexturesFolder(TextureType.SCREEN) != null) {
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getTextureFile("test", TextureType.SCREEN));
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getTextureFile("picture1", TextureType.SCREEN));
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getTextureFile("picture2", TextureType.SCREEN));
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(),
					workspace.getFolderManager().getTextureFile("picture3", TextureType.SCREEN));
		}

		if (workspace.getFolderManager().getTexturesFolder(TextureType.ARMOR) != null) {
			File[] armorPars = workspace.getFolderManager().getArmorTextureFilesForName("armor_texture");
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(), armorPars[0]);
			FileIO.writeImageToPNGFile((RenderedImage) imageIcon.getImage(), armorPars[1]);
		}

		if (workspace.getFolderManager().getStructuresDir() != null) {
			byte[] emptyNbtStructure;
			try {
				emptyNbtStructure = IOUtils.resourceToByteArray("/empty.nbt");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			FileIO.writeBytesToFile(emptyNbtStructure, new File(workspace.getFolderManager().getStructuresDir(),
					"test." + workspace.getGeneratorConfiguration().getStructureExtension()));
			FileIO.writeBytesToFile(emptyNbtStructure, new File(workspace.getFolderManager().getStructuresDir(),
					"test1." + workspace.getGeneratorConfiguration().getStructureExtension()));
			FileIO.writeBytesToFile(emptyNbtStructure, new File(workspace.getFolderManager().getStructuresDir(),
					"test2." + workspace.getGeneratorConfiguration().getStructureExtension()));
			FileIO.writeBytesToFile(emptyNbtStructure, new File(workspace.getFolderManager().getStructuresDir(),
					"test3." + workspace.getGeneratorConfiguration().getStructureExtension()));
		}

		if (workspace.getGeneratorStats().hasBaseCoverage("model_java")) {
			try {
				if (workspace.getGenerator().getGeneratorConfiguration().getCompatibleJavaModelKeys()
						.contains("mojmap-1.17.x"))
					ModelImportActions.importJavaModel(null, workspace,
							IOUtils.resourceToString("/entitymodel-mojmap-1.17.x.java", StandardCharsets.UTF_8));
				else
					ModelImportActions.importJavaModel(null, workspace, IOUtils.resourceToString(
							"/entitymodel-mojmap-" + workspace.getGenerator().getGeneratorConfiguration()
									.getCompatibleJavaModelKeys().getFirst() + ".java", StandardCharsets.UTF_8));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		if (workspace.getGeneratorStats().hasBaseCoverage("model_animations_java")) {
			try {
				AnimationImportActions.importJavaModelAnimation(null, workspace,
						IOUtils.resourceToString("/entityanimation-mojmap.java", StandardCharsets.UTF_8));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static void fillWorkspaceWithSampleTags(Workspace workspace) {
		if (workspace.getGeneratorStats().hasBaseCoverage("tags")) {
			TagElement tag = new TagElement(TagType.ITEMS, "minecraft:test");
			workspace.addTagElement(tag);
			workspace.getTagElements().get(tag).add("minecraft:stone");
			workspace.getTagElements().get(tag).add("~minecraft:dirt");
			workspace.getTagElements().get(tag).add("EXTERNAL:externalmod:item");
			if (workspace.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.ITEM)
					== GeneratorStats.CoverageStatus.FULL) {
				workspace.getTagElements().get(tag).add("CUSTOM:Exampleitem1");
				workspace.getTagElements().get(tag).add("~CUSTOM:Exampleitem2");
			}

			tag = new TagElement(TagType.BLOCKS, "minecraft:test");
			workspace.addTagElement(tag);
			workspace.getTagElements().get(tag).add("minecraft:stone");
			workspace.getTagElements().get(tag).add("~minecraft:dirt");
			workspace.getTagElements().get(tag).add("EXTERNAL:externalmod:block");
			if (workspace.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.BLOCK)
					== GeneratorStats.CoverageStatus.FULL) {
				workspace.getTagElements().get(tag).add("CUSTOM:Exampleblock1");
				workspace.getTagElements().get(tag).add("~CUSTOM:Exampleblock2");
			}

			tag = new TagElement(TagType.ENTITIES, "minecraft:test");
			workspace.addTagElement(tag);
			workspace.getTagElements().get(tag).add("minecraft:creeper");
			workspace.getTagElements().get(tag).add("~minecraft:zombie");
			workspace.getTagElements().get(tag).add("EXTERNAL:externalmod:entity");
			if (workspace.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.LIVINGENTITY)
					== GeneratorStats.CoverageStatus.FULL) {
				workspace.getTagElements().get(tag).add("CUSTOM:Examplelivingentity1");
				workspace.getTagElements().get(tag).add("~CUSTOM:Examplelivingentity2");
			}

			tag = new TagElement(TagType.BIOMES, "minecraft:test");
			workspace.addTagElement(tag);
			workspace.getTagElements().get(tag).add("minecraft:plains");
			workspace.getTagElements().get(tag).add("EXTERNAL:externalmod:biome");
			if (workspace.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.BIOME)
					== GeneratorStats.CoverageStatus.FULL) {
				workspace.getTagElements().get(tag).add("~CUSTOM:Examplebiome1");
			}

			tag = new TagElement(TagType.STRUCTURES, "minecraft:test");
			workspace.addTagElement(tag);
			workspace.getTagElements().get(tag).add("minecraft:stronghold");
			workspace.getTagElements().get(tag).add("~minecraft:mineshaft");
			workspace.getTagElements().get(tag).add("EXTERNAL:externalmod:structure");
			if (workspace.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.STRUCTURE)
					== GeneratorStats.CoverageStatus.FULL) {
				workspace.getTagElements().get(tag).add("CUSTOM:Examplestructure1");
				workspace.getTagElements().get(tag).add("~CUSTOM:Examplestructure2");
			}

			tag = new TagElement(TagType.DAMAGE_TYPES, "minecraft:test");
			workspace.addTagElement(tag);
			workspace.getTagElements().get(tag).add("EXTERNAL:externalmod:damage_type");
			if (workspace.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.DAMAGETYPE)
					== GeneratorStats.CoverageStatus.FULL) {
				workspace.getTagElements().get(tag).add("CUSTOM:Exampledamagetype1");
				workspace.getTagElements().get(tag).add("~CUSTOM:Exampledamagetype2");
			}

			tag = new TagElement(TagType.ENCHANTMENTS, "minecraft:test");
			workspace.addTagElement(tag);
			workspace.getTagElements().get(tag).add("EXTERNAL:externalmod:enchantment");
			if (workspace.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.ENCHANTMENT)
					== GeneratorStats.CoverageStatus.FULL) {
				workspace.getTagElements().get(tag).add("CUSTOM:Exampleenchantment1");
				workspace.getTagElements().get(tag).add("~CUSTOM:Exampleenchantment2");
			}

			tag = new TagElement(TagType.GAME_EVENTS, "minecraft:test");
			workspace.addTagElement(tag);
			workspace.getTagElements().get(tag).add("EXTERNAL:externalmod:game_event");
			workspace.getTagElements().get(tag).add("minecraft:block_attach");
			workspace.getTagElements().get(tag).add("~minecraft:container_open");

			tag = new TagElement(TagType.FUNCTIONS, "minecraft:test");
			workspace.addTagElement(tag);
			if (workspace.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.ENCHANTMENT)
					== GeneratorStats.CoverageStatus.FULL) {
				workspace.getTagElements().get(tag).add("ExampleFunction1");
				workspace.getTagElements().get(tag).add("~ExampleFunction2");
			}
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
		var blocksAndItems = ElementUtil.loadBlocksAndItems(modElement.getWorkspace());
		var blocksAndItemsAndTags = ElementUtil.loadBlocksAndItemsAndTags(modElement.getWorkspace());
		var blocks = ElementUtil.loadBlocks(modElement.getWorkspace());
		var blocksAndTags = ElementUtil.loadBlocksAndTags(modElement.getWorkspace());
		var biomes = ElementUtil.loadAllBiomes(modElement.getWorkspace());
		var tabs = ElementUtil.loadAllTabs(modElement.getWorkspace()).stream()
				.map(e -> new TabEntry(modElement.getWorkspace(), e)).toList();
		// Also prepare list of blocks that are "worldgen-safe"
		var worldgenBlocks = Stream.of("Blocks.STONE#0", "Blocks.DIRT#0", "Blocks.DIAMOND_BLOCK",
						"Blocks.EMERALD_BLOCK", "Blocks.SANDSTONE#0", "Blocks.WOOL#0", "Blocks.LEAVES#1")
				.map(n -> new MCItem(new DataListEntry.Dummy(n))).toList();
		var guis = modElement.getWorkspace().getModElements().stream()
				.filter(var -> var.getType() == ModElementType.GUI).map(ModElement::getName)
				.collect(Collectors.toList());

		if (ModElementType.ADVANCEMENT.equals(modElement.getType())) {
			return getAdvancementExample(modElement, random, _true, emptyLists, blocksAndItems);
		} else if (ModElementType.BANNERPATTERN.equals(modElement.getType())) {
			BannerPattern bannerPattern = new BannerPattern(modElement);
			bannerPattern.texture = new TextureHolder(modElement.getWorkspace(), "other0");
			bannerPattern.shieldTexture = new TextureHolder(modElement.getWorkspace(), "other0");
			bannerPattern.name = modElement.getName();
			bannerPattern.requireItem = _true;
			return bannerPattern;
		} else if (ModElementType.BIOME.equals(modElement.getType())) {
			Biome biome = new Biome(modElement);
			biome.name = modElement.getName();
			biome.groundBlock = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, worldgenBlocks).getName());
			biome.undergroundBlock = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, worldgenBlocks).getName());
			biome.underwaterBlock = new MItemBlock(modElement.getWorkspace(),
					emptyLists ? "" : getRandomMCItem(random, worldgenBlocks).getName());
			biome.vanillaTreeType = getRandomItem(random,
					new String[] { "Default", "Big trees", "Birch trees", "Savanna trees", "Mega pine trees",
							"Mega spruce trees" });
			biome.airColor = Color.red;
			if (!emptyLists) {
				biome.fogColor = Color.yellow;
				biome.grassColor = Color.green;
				biome.foliageColor = Color.magenta;
				biome.waterColor = Color.blue;
				biome.waterFogColor = Color.cyan;
			}
			biome.ambientSound = new Sound(modElement.getWorkspace(),
					getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			biome.moodSound = new Sound(modElement.getWorkspace(),
					getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			biome.moodSoundDelay = getRandomInt(random, Biome.class, "moodSoundDelay");
			biome.additionsSound = new Sound(modElement.getWorkspace(),
					getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			biome.music = new Sound(modElement.getWorkspace(),
					getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			biome.spawnParticles = _true;
			biome.particleToSpawn = new Particle(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadAllParticles(modElement.getWorkspace())));
			biome.particlesProbability = getRandomDouble(random, Biome.class, "particlesProbability");
			biome.treesPerChunk = getRandomInt(random, Biome.class, "treesPerChunk");
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
			biome.genDepth = new Biome.ClimatePoint(0.3, 1.2);

			biome.rainingPossibility = getRandomDouble(random, Biome.class, "rainingPossibility");
			biome.temperature = getRandomDouble(random, Biome.class, "temperature");

			List<Biome.SpawnEntry> entities = new ArrayList<>();
			if (!emptyLists) {
				Biome.SpawnEntry entry1 = new Biome.SpawnEntry();
				entry1.entity = new EntityEntry(modElement.getWorkspace(), getRandomDataListEntry(random,
						ElementUtil.loadAllSpawnableEntities(modElement.getWorkspace())));
				Range<Integer> groupSize1 = getRandomIntRange(random, Biome.SpawnEntry.class, "minGroup", "maxGroup");
				entry1.minGroup = groupSize1.getMinimum();
				entry1.maxGroup = groupSize1.getMaximum();
				entry1.weight = getRandomInt(random, Biome.SpawnEntry.class, "weight");
				entry1.spawnType = getRandomItem(random, ElementUtil.getDataListAsStringArray("mobspawntypes"));
				entities.add(entry1);

				Biome.SpawnEntry entry2 = new Biome.SpawnEntry();
				entry2.entity = new EntityEntry(modElement.getWorkspace(), getRandomDataListEntry(random,
						ElementUtil.loadAllSpawnableEntities(modElement.getWorkspace())));
				Range<Integer> groupSize2 = getRandomIntRange(random, Biome.SpawnEntry.class, "minGroup", "maxGroup");
				entry2.minGroup = groupSize2.getMinimum();
				entry2.maxGroup = groupSize2.getMaximum();
				entry2.weight = getRandomInt(random, Biome.SpawnEntry.class, "weight");
				entry2.spawnType = getRandomItem(random, ElementUtil.getDataListAsStringArray("mobspawntypes"));
				entities.add(entry2);

				Biome.SpawnEntry entry3 = new Biome.SpawnEntry();
				entry3.entity = new EntityEntry(modElement.getWorkspace(), getRandomDataListEntry(random,
						ElementUtil.loadAllSpawnableEntities(modElement.getWorkspace())));
				Range<Integer> groupSize3 = getRandomIntRange(random, Biome.SpawnEntry.class, "minGroup", "maxGroup");
				entry3.minGroup = groupSize3.getMinimum();
				entry3.maxGroup = groupSize3.getMaximum();
				entry3.weight = getRandomInt(random, Biome.SpawnEntry.class, "weight");
				entry3.spawnType = getRandomItem(random, ElementUtil.getDataListAsStringArray("mobspawntypes"));
				entities.add(entry3);

				Biome.SpawnEntry entry4 = new Biome.SpawnEntry();
				entry4.entity = new EntityEntry(modElement.getWorkspace(), getRandomDataListEntry(random,
						ElementUtil.loadAllSpawnableEntities(modElement.getWorkspace())));
				Range<Integer> groupSize4 = getRandomIntRange(random, Biome.SpawnEntry.class, "minGroup", "maxGroup");
				entry4.minGroup = groupSize4.getMinimum();
				entry4.maxGroup = groupSize4.getMaximum();
				entry4.weight = getRandomInt(random, Biome.SpawnEntry.class, "weight");
				entry4.spawnType = getRandomItem(random, ElementUtil.getDataListAsStringArray("mobspawntypes"));
				entities.add(entry4);
			}
			biome.spawnEntries = entities;
			biome.minHeight = getRandomInt(random, Biome.class, "minHeight");
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
			fluid.textureFlowing = new TextureHolder(modElement.getWorkspace(), "test");
			fluid.textureStill = new TextureHolder(modElement.getWorkspace(), "test2");
			fluid.textureRenderOverlay = new TextureHolder(modElement.getWorkspace(), emptyLists ? "" : "other0");
			fluid.hasFog = _true;
			fluid.fogColor = emptyLists ? null : Color.red;
			fluid.fogStartDistance = new NumberProcedure(emptyLists ? null : "number3", 8);
			fluid.fogEndDistance = new NumberProcedure(emptyLists ? null : "number3", 16);
			fluid.canMultiply = _true;
			fluid.flowRate = getRandomInt(random, Fluid.class, "flowRate");
			fluid.levelDecrease = getRandomInt(random, Fluid.class, "levelDecrease");
			fluid.slopeFindDistance = getRandomInt(random, Fluid.class, "slopeFindDistance");
			fluid.spawnParticles = !_true;
			fluid.dripParticle = new Particle(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadAllParticles(modElement.getWorkspace())));
			fluid.tintType = getRandomString(random,
					Arrays.asList("No tint", "Grass", "Foliage", "Birch foliage", "Spruce foliage", "Default foliage",
							"Water", "Sky", "Fog", "Water fog"));
			fluid.flowStrength = getRandomDouble(random, Fluid.class, "flowStrength");
			fluid.luminosity = getRandomInt(random, Fluid.class, "luminosity");
			fluid.density = getRandomInt(random, Fluid.class, "density");
			fluid.viscosity = getRandomInt(random, Fluid.class, "viscosity");
			fluid.temperature = getRandomInt(random, Fluid.class, "temperature");
			fluid.generateBucket = !_true;
			fluid.bucketName = modElement.getName() + " Bucket";
			fluid.textureBucket = new TextureHolder(modElement.getWorkspace(), emptyLists ? "" : "itest");
			fluid.creativeTabs = emptyLists ? List.of() : tabs;
			fluid.emptySound = !emptyLists ?
					new Sound(modElement.getWorkspace(), "") :
					new Sound(modElement.getWorkspace(),
							getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			fluid.rarity = getRandomString(random, Arrays.asList("COMMON", "UNCOMMON", "RARE", "EPIC"));
			fluid.specialInformation = new StringListProcedure(emptyLists ? null : "string1",
					Arrays.asList("info 1", "info 2", "test, is this", "another one"));
			fluid.resistance = getRandomDouble(random, Fluid.class, "resistance");
			fluid.emissiveRendering = _true;
			fluid.luminance = getRandomInt(random, Fluid.class, "luminance");
			fluid.tickRate = getRandomInt(random, Fluid.class, "tickRate");
			fluid.lightOpacity = getRandomInt(random, Fluid.class, "lightOpacity");
			fluid.ignitedByLava = !_true;
			fluid.flammability = getRandomInt(random, Fluid.class, "flammability");
			fluid.fireSpreadSpeed = getRandomInt(random, Fluid.class, "fireSpreadSpeed");
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
			keyBinding.keyBindingCategoryKey = "test_category";
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
					Color.red, _true, new Procedure("condition1"),
					getRandomItem(random, GUIComponent.AnchorPoint.values())));
			components.add(new Label("text2", 100, 150, new StringProcedure(!_true ? "string2" : null, "fixed value 2"),
					Color.white, !_true, new Procedure("condition4"),
					getRandomItem(random, GUIComponent.AnchorPoint.values())));

			components.add(new Image(20, 30, "picture1", true, new Procedure("condition1"),
					getRandomItem(random, GUIComponent.AnchorPoint.values())));
			components.add(new Image(22, 31, "picture2", false, new Procedure("condition2"),
					getRandomItem(random, GUIComponent.AnchorPoint.values())));
			components.add(new Sprite(25, 30, "picture1", 1, new Procedure(_true ? "condition1" : null),
					new NumberProcedure(!_true ? "number1" : null, 0),
					getRandomItem(random, GUIComponent.AnchorPoint.values())));
			components.add(new Sprite(30, 35, "picture3", 9, new Procedure(!_true ? "condition2" : null),
					new NumberProcedure(_true ? "number2" : null, 6),
					getRandomItem(random, GUIComponent.AnchorPoint.values())));
			components.add(new EntityModel(60, 20, new Procedure("entity1"), new Procedure("condition3"), 30, 0, false,
					getRandomItem(random, GUIComponent.AnchorPoint.values())));
			components.add(
					new EntityModel(60, 20, new Procedure("entity1"), new Procedure(!_true ? "condition4" : null), 30,
							90, false, getRandomItem(random, GUIComponent.AnchorPoint.values())));
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
			gui.width = getRandomInt(random, GUI.class, "width");
			gui.height = getRandomInt(random, GUI.class, "height");
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
						new StringProcedure(_true ? "string1" : null, "fixed value 1"), Color.red, _true,
						new Procedure("condition1")));
				components.add(new Label(AbstractWYSIWYGDialog.textToMachineName(components, null,
						"This is --...p a test string ŽĐĆ @ /test//\" tes___"), 100, 150,
						new StringProcedure(!_true ? "string2" : null, "fixed value 2"), Color.white, !_true,
						new Procedure("condition4")));

				components.add(new Image(20, 30, "picture1", true, new Procedure("condition1")));
				components.add(new Image(22, 31, "picture2", false, new Procedure("condition2")));
				components.add(new Sprite(25, 30, "picture1", 5, new Procedure(_true ? "condition2" : null),
						new NumberProcedure(!_true ? "number2" : null, 16)));
				components.add(new Sprite(30, 35, "picture3", 16, new Procedure(!_true ? "condition3" : null),
						new NumberProcedure(_true ? "number3" : null, 5)));
				components.add(new Button(AbstractWYSIWYGDialog.textToMachineName(components, null, "button"), 10, 10,
						"button1", 100, 200, _true, new Procedure("procedure10"), null));
				components.add(new Button("button2", 10, 10, "button2", 100, 200, !_true, null, null));
				components.add(
						new Button("button3", 10, 10, "button3", 100, 200, _true, null, new Procedure("condition3")));
				components.add(new Button(AbstractWYSIWYGDialog.textToMachineName(components, null, "button"), 10, 10,
						"button4", 100, 200, !_true, new Procedure("procedure2"), new Procedure("condition4")));
				components.add(
						new ImageButton("imagebutton1", 0, 48, "picture1", "picture2", new Procedure("procedure10"),
								null));
				components.add(
						new ImageButton("imagebutton2", 16, 32, "picture2", "", new Procedure(""), new Procedure("")));
				components.add(new ImageButton("imagebutton3", 32, 16, "picture1", "picture2", null,
						new Procedure("condition3")));
				components.add(new ImageButton("imagebutton4", 48, 0, "picture2", "", new Procedure("procedure2"),
						new Procedure("condition4")));
				components.add(
						new Slider(80, 80, 30, 10, "slider1", 0, 10, 5, 1, "Be ", " Af", new Procedure("procedure10")));
				components.add(
						new Slider(80, 90, 30, 10, "slider2", -10, 45, 1, 1, "Be ", "", new Procedure("procedure9")));
				components.add(new Slider(80, 100, 30, 10, "slider3", -1542, 1257, -145, 1, "", " Af",
						new Procedure("procedure8")));
				components.add(new Slider(80, 110, 30, 10, "slider4", 1.0, 10.0, 4.8, 0.1, "", "",
						new Procedure("procedure11")));
				components.add(new Slider(80, 120, 30, 10, "slider5", 1.0, 10.0, 4.8, 0.1, "Be", "Af", null));
				if (gui.type == 1) {
					components.add(new InputSlot(0, 20, 30, Color.red, new LogicProcedure("condition1", true),
							new LogicProcedure("condition1", true), _true, new Procedure("procedure3"),
							new Procedure("procedure10"), new Procedure("procedure2"),
							new MItemBlock(modElement.getWorkspace(), "")));
					components.add(new InputSlot(3, 20, 30, Color.white, new LogicProcedure(null, true),
							new LogicProcedure("condition1", true), !_true, new Procedure("procedure4"), null, null,
							new MItemBlock(modElement.getWorkspace(),
									getRandomMCItem(random, blocksAndItems).getName())));
					new InputSlot(5, 20, 30, Color.white, new LogicProcedure("condition1", true),
							new LogicProcedure(null, true), !_true, new Procedure("procedure4"), null, null,
							new MItemBlock(modElement.getWorkspace(),
									getRandomMCItem(random, blocksAndItems).getName()));
					components.add(new InputSlot(4, 20, 30, Color.green, new LogicProcedure(null, _true),
							new LogicProcedure("condition1", !_true), _true, new Procedure("procedure5"), null, null,
							new MItemBlock(modElement.getWorkspace(), "TAG:walls")));
					components.add(
							new OutputSlot(5, 10, 20, Color.black, new LogicProcedure("condition2", _true), !_true,
									new Procedure("procedure10"), new Procedure("procedure2"),
									new Procedure("procedure3")));
					components.add(
							new OutputSlot(6, 243, 563, Color.black, new LogicProcedure("condition2", true), _true,
									null, null, null));
				}
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
			return getLivingEntity(modElement, random, _true, emptyLists, valueIndex, blocksAndItems,
					blocksAndItemsAndTags, biomes, guis);
		} else if (ModElementType.DIMENSION.equals(modElement.getType())) {
			Dimension dimension = new Dimension(modElement);
			Range<Integer> monsterSpawnLightLimit = getRandomIntRange(random, Dimension.class,
					"minMonsterSpawnLightLimit", "maxMonsterSpawnLightLimit");
			dimension.texture = new TextureHolder(modElement.getWorkspace(), "test");
			dimension.portalTexture = new TextureHolder(modElement.getWorkspace(), "test2");
			dimension.enableIgniter = true; // we always want it as it can be referenced in other tests
			dimension.portalParticles = new Particle(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadAllParticles(modElement.getWorkspace())));
			dimension.creativeTabs = emptyLists ? List.of() : tabs;
			dimension.portalSound = new Sound(modElement.getWorkspace(),
					getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			dimension.biomesInDimension = new ArrayList<>();
			dimension.biomesInDimensionCaves = new ArrayList<>();
			if (!emptyLists) {
				dimension.biomesInDimension.addAll(
						subset(random, 10, biomes, e -> new BiomeEntry(modElement.getWorkspace(), e.getName())));
				dimension.biomesInDimensionCaves.addAll(
						subset(random, 10, biomes, e -> new BiomeEntry(modElement.getWorkspace(), e.getName())));
			} else {
				dimension.biomesInDimension.add(
						new BiomeEntry(modElement.getWorkspace(), getRandomDataListEntry(random, biomes)));
			}
			dimension.airColor = Color.cyan;
			dimension.canRespawnHere = _true;
			dimension.bedWorks = !_true;
			dimension.hasFog = _true;
			dimension.hasSkyLight = !_true;
			dimension.imitateOverworldBehaviour = _true;
			dimension.ambientLight = getRandomDouble(random, Dimension.class, "ambientLight");
			dimension.doesWaterVaporize = !_true;
			dimension.hasFixedTime = !_true;
			dimension.fixedTimeValue = getRandomInt(random, Dimension.class, "fixedTimeValue");
			dimension.coordinateScale = getRandomDouble(random, Dimension.class, "coordinateScale");
			dimension.infiniburnTag = "minecraft:infiniburn_end";
			dimension.piglinSafe = !_true;
			dimension.hasRaids = _true;
			dimension.minMonsterSpawnLightLimit = monsterSpawnLightLimit.getMinimum();
			dimension.maxMonsterSpawnLightLimit = monsterSpawnLightLimit.getMaximum();
			dimension.monsterSpawnBlockLightLimit = getRandomInt(random, Dimension.class, "monsterSpawnBlockLightLimit");
			dimension.defaultEffects = new String[] { "overworld", "overworld", "the_nether", "the_end" }[valueIndex];
			dimension.useCustomEffects = emptyLists;
			dimension.hasClouds = _true;
			dimension.cloudHeight = getRandomInt(random, Dimension.class, "cloudHeight");
			dimension.sunHeightAffectsFog = !_true;
			dimension.skyType = new String[] { "NONE", "NORMAL", "END", "NORMAL" }[valueIndex];
			dimension.enablePortal = true; // we always want it as it can be referenced in other tests
			dimension.portalLuminance = getRandomInt(random, Dimension.class, "portalLuminance");
			dimension.portalFrame = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocks).getName());
			dimension.igniterName = modElement.getName();
			dimension.igniterRarity = getRandomString(random, Arrays.asList("COMMON", "UNCOMMON", "RARE", "EPIC"));
			dimension.specialInformation = new StringListProcedure(emptyLists ? null : "string1",
					Arrays.asList("info 1", "info 2", "test, is this", "another one"));
			dimension.worldGenType = new String[] { "Nether like gen", "Normal world gen", "End like gen",
					"Normal world gen" }[valueIndex];
			dimension.mainFillerBlock = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, worldgenBlocks).getName());
			dimension.fluidBlock = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, worldgenBlocks).getName());
			dimension.seaLevel = getRandomInt(random, Dimension.class, "seaLevel");
			dimension.generateOreVeins = _true;
			dimension.generateAquifers = !_true;
			dimension.horizontalNoiseSize = new int[] { 1, 2, 1, 4 }[valueIndex];
			dimension.verticalNoiseSize = new int[] { 2, 2, 4, 1 }[valueIndex];
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
			structure.surfaceDetectionType = getRandomString(random,
					Arrays.asList("WORLD_SURFACE_WG", "WORLD_SURFACE", "OCEAN_FLOOR_WG", "OCEAN_FLOOR",
							"MOTION_BLOCKING", "MOTION_BLOCKING_NO_LEAVES"));
			structure.useStartHeight = _true;
			structure.startHeightProviderType = getRandomString(random,
					Arrays.asList("UNIFORM", "BIASED_TO_BOTTOM", "VERY_BIASED_TO_BOTTOM", "TRAPEZOID"));
			Range<Integer> startHeightRange = getRandomIntRange(random, Structure.class, "startHeightMin",
					"startHeightMax");
			structure.startHeightMin = startHeightRange.getMinimum();
			structure.startHeightMax = startHeightRange.getMaximum();
			structure.ignoredBlocks = new ArrayList<>();
			if (!emptyLists) {
				structure.ignoredBlocks = subset(random, 5, blocks,
						e -> new MItemBlock(modElement.getWorkspace(), e.getName()));
			}
			structure.terrainAdaptation = getRandomString(random,
					Arrays.asList("none", "beard_thin", "beard_box", "bury", "encapsulate"));
			structure.projection = getRandomString(random, Arrays.asList("rigid", "terrain_matching"));
			structure.restrictionBiomes = new ArrayList<>();
			Range<Integer> separationSpacingRange = getRandomIntRange(random, Structure.class, "separation",
					"spacing");
			structure.separation = separationSpacingRange.getMinimum();
			structure.spacing = separationSpacingRange.getMaximum();
			if (_true) {
				structure.restrictionBiomes = subset(random, 5, biomes,
						e -> new BiomeEntry(modElement.getWorkspace(), e.getName()));
			} else {
				structure.restrictionBiomes.add(new BiomeEntry(modElement.getWorkspace(), "#is_overworld"));
			}
			structure.generationStep = TestWorkspaceDataProvider.getRandomItem(random,
					ElementUtil.getDataListAsStringArray("generationsteps"));
			structure.size = getRandomInt(random, Structure.class, "size");
			structure.maxDistanceFromCenter = getRandomInt(random, Structure.class, "maxDistanceFromCenter");
			structure.jigsawPools = new ArrayList<>();
			if (!emptyLists) {
				Structure.JigsawPool pool = new Structure.JigsawPool();
				pool.poolName = "pool1";
				pool.fallbackPool = "test_mod:" + modElement.getRegistryName() + "_pool2";
				pool.poolParts = new ArrayList<>();
				Structure.JigsawPool.JigsawPart part = new Structure.JigsawPool.JigsawPart();
				part.weight = getRandomInt(random, Structure.JigsawPool.JigsawPart.class, "weight");
				part.structure = "test1";
				part.projection = "rigid";
				part.ignoredBlocks = subset(random, 5, blocks,
						e -> new MItemBlock(modElement.getWorkspace(), e.getName()));
				pool.poolParts.add(part);
				part = new Structure.JigsawPool.JigsawPart();
				part.weight = getRandomInt(random, Structure.JigsawPool.JigsawPart.class, "weight");
				part.structure = "test2";
				part.projection = "terrain_matching";
				part.ignoredBlocks = subset(random, 5, blocks,
						e -> new MItemBlock(modElement.getWorkspace(), e.getName()));
				pool.poolParts.add(part);
				structure.jigsawPools.add(pool);

				pool = new Structure.JigsawPool();
				pool.poolName = "pool2";
				pool.fallbackPool = "";
				pool.poolParts = new ArrayList<>();
				part = new Structure.JigsawPool.JigsawPart();
				part.weight = getRandomInt(random, Structure.JigsawPool.JigsawPart.class, "weight");
				part.structure = "test3";
				part.projection = "rigid";
				part.ignoredBlocks = subset(random, 5, blocks,
						e -> new MItemBlock(modElement.getWorkspace(), e.getName()));
				pool.poolParts.add(part);
				structure.jigsawPools.add(pool);
			}
			return structure;
		} else if (ModElementType.ARMOR.equals(modElement.getType())) {
			Armor armor = new Armor(modElement);
			armor.enableHelmet = random.nextBoolean();
			armor.textureHelmet = new TextureHolder(modElement.getWorkspace(), "test");
			armor.helmetModelTexture = emptyLists ? "From armor" : "entity_texture_0.png";
			if (random.nextBoolean()) {
				armor.helmetModelName = "ModelCustomJavaModel";
				armor.helmetModelPart = "head";
				armor.helmetTranslucency = _true;
			} else {
				armor.helmetModelName = "Default";
			}
			armor.enableBody = random.nextBoolean();
			armor.textureBody = new TextureHolder(modElement.getWorkspace(), "test2");
			armor.bodyModelTexture = emptyLists ? "From armor" : "entity_texture_0.png";
			if (random.nextBoolean()) {
				armor.bodyModelName = "ModelCustomJavaModel";
				armor.bodyModelPart = "head";
				armor.armsModelPartL = "leg0";
				armor.armsModelPartR = "leg1";
				armor.bodyTranslucency = _true;
			} else {
				armor.bodyModelName = "Default";
			}
			armor.enableLeggings = random.nextBoolean();
			armor.textureLeggings = new TextureHolder(modElement.getWorkspace(), "test2");
			armor.leggingsModelTexture = emptyLists ? "From armor" : "entity_texture_0.png";
			if (random.nextBoolean()) {
				armor.leggingsModelName = "ModelCustomJavaModel";
				armor.leggingsModelPartL = "leg0";
				armor.leggingsModelPartR = "leg1";
				armor.leggingsTranslucency = _true;
			} else {
				armor.leggingsModelName = "Default";
			}
			armor.enableBoots = random.nextBoolean();
			armor.textureBoots = new TextureHolder(modElement.getWorkspace(), "test4");
			armor.bootsModelTexture = emptyLists ? "From armor" : "entity_texture_0.png";
			if (random.nextBoolean()) {
				armor.bootsModelName = "ModelCustomJavaModel";
				armor.bootsModelPartL = "leg2";
				armor.bootsModelPartR = "leg3";
				armor.bootsTranslucency = _true;
			} else {
				armor.bootsModelName = "Default";
			}
			armor.helmetItemRenderType = 0;
			armor.helmetItemCustomModelName = "Normal";
			armor.bodyItemRenderType = 0;
			armor.bodyItemCustomModelName = "Normal";
			armor.leggingsItemRenderType = 0;
			armor.leggingsItemCustomModelName = "Normal";
			armor.bootsItemRenderType = 0;
			armor.bootsItemCustomModelName = "Normal";
			armor.helmetSpecialInformation = new StringListProcedure(emptyLists ? null : "string1",
					Arrays.asList("info 1", "info 2", "test, is this", "another one"));
			armor.bodySpecialInformation = new StringListProcedure(emptyLists ? null : "string2",
					Arrays.asList("info 1", "info 2", "test, is this", "another one"));
			armor.leggingsSpecialInformation = new StringListProcedure(emptyLists ? null : "string3",
					Arrays.asList("info 1", "info 2", "test, is this", "another one"));
			armor.bootsSpecialInformation = new StringListProcedure(emptyLists ? null : "string4",
					Arrays.asList("info 1", "info 2", "test, is this", "another one"));
			armor.helmetImmuneToFire = _true;
			armor.bodyImmuneToFire = !_true;
			armor.leggingsImmuneToFire = _true;
			armor.bootsImmuneToFire = !_true;
			armor.helmetGlowCondition = new LogicProcedure(_true ? "condition1" : null, _true);
			armor.bodyGlowCondition = new LogicProcedure(_true ? "condition2" : null, _true);
			armor.leggingsGlowCondition = new LogicProcedure(_true ? "condition3" : null, _true);
			armor.bootsGlowCondition = new LogicProcedure(_true ? "condition4" : null, _true);
			armor.helmetPiglinNeutral = new LogicProcedure(_true ? "condition1" : null, _true);
			armor.bodyPiglinNeutral = new LogicProcedure(_true ? "condition2" : null, _true);
			armor.leggingsPiglinNeutral = new LogicProcedure(_true ? "condition3" : null, _true);
			armor.bootsPiglinNeutral = new LogicProcedure(_true ? "condition4" : null, _true);
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
			armor.rarity = getRandomString(random, Arrays.asList("COMMON", "UNCOMMON", "RARE", "EPIC"));
			armor.creativeTabs = emptyLists ? List.of() : tabs;
			armor.armorTextureFile = "armor_texture";
			armor.maxDamage = getRandomInt(random, Armor.class, "maxDamage");
			armor.damageValueHelmet = getRandomInt(random, Armor.class, "damageValueHelmet");
			armor.damageValueBody = getRandomInt(random, Armor.class, "damageValueBody");
			armor.damageValueLeggings = getRandomInt(random, Armor.class, "damageValueLeggings");
			armor.damageValueBoots = getRandomInt(random, Armor.class, "damageValueBoots");
			armor.enchantability = getRandomInt(random, Armor.class, "enchantability");
			armor.toughness = getRandomDouble(random, Armor.class, "toughness");
			armor.knockbackResistance = getRandomDouble(random, Armor.class, "knockbackResistance");
			armor.repairItems = new ArrayList<>();
			if (!emptyLists) {
				armor.repairItems = subset(random, blocksAndItemsAndTags.size() / 8, blocksAndItemsAndTags,
						e -> new MItemBlock(modElement.getWorkspace(), e.getName()));
				armor.repairItems.add(new MItemBlock(modElement.getWorkspace(), "TAG:walls"));
			}
			return armor;
		} else if (ModElementType.PLANT.equals(modElement.getType())) {
			Plant plant = new Plant(modElement);
			plant.name = modElement.getName();
			plant.plantType = List.of("normal", "growapable", "double", "sapling").get(valueIndex);
			plant.creativeTabs = emptyLists ? List.of() : tabs;
			plant.texture = new TextureHolder(modElement.getWorkspace(), "test");
			plant.itemTexture = new TextureHolder(modElement.getWorkspace(), emptyLists ? "" : "itest");
			plant.particleTexture = new TextureHolder(modElement.getWorkspace(), emptyLists ? "" : "test3");
			plant.growapableSpawnType = getRandomItem(random, ElementUtil.getDataListAsStringArray("planttypes"));

			// Set some plant type properties
			switch (plant.plantType) {
			case "normal" -> {
				plant.suspiciousStewEffect = getRandomString(random,
						ElementUtil.loadAllPotionEffects(modElement.getWorkspace()).stream().map(DataListEntry::getName)
								.toList());
				plant.suspiciousStewDuration = getRandomInt(random, Plant.class, "suspiciousStewDuration");
			}
			case "double" -> plant.textureBottom = new TextureHolder(modElement.getWorkspace(), "test2");
			case "growapable" -> plant.growapableMaxHeight = getRandomInt(random, Plant.class, "growapableMaxHeight");
			case "sapling" -> {
				plant.secondaryTreeChance = getRandomDouble(random, Plant.class, "secondaryTreeChance");
				for (int i = 0; i < 2; i++) {
					plant.trees[i] = new ConfiguredFeatureEntry(modElement.getWorkspace(),
							getRandomItem(random, ElementUtil.loadAllConfiguredFeatures(modElement.getWorkspace())));
					if (_true) {
						plant.flowerTrees[i] = new ConfiguredFeatureEntry(modElement.getWorkspace(),
								getRandomItem(random,
										ElementUtil.loadAllConfiguredFeatures(modElement.getWorkspace())));
					}
					if (!emptyLists) {
						plant.megaTrees[i] = new ConfiguredFeatureEntry(modElement.getWorkspace(), getRandomItem(random,
								ElementUtil.loadAllConfiguredFeatures(modElement.getWorkspace())));
					}
				}
			}
			}

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
			plant.hardness = getRandomDouble(random, Plant.class, "hardness");
			plant.emissiveRendering = !_true;
			plant.resistance = getRandomDouble(random, Plant.class, "resistance");
			plant.luminance = getRandomInt(random, Plant.class, "luminance");
			plant.isReplaceable = !_true;
			plant.forceTicking = !_true;
			plant.hasTileEntity = !_true;
			plant.isSolid = _true;
			plant.isWaterloggable = emptyLists; // saplings with mega trees can't be waterloggable
			plant.hasBlockItem = !emptyLists;
			plant.maxStackSize = getRandomInt(random, Plant.class, "maxStackSize");
			plant.rarity = getRandomString(random, Arrays.asList("COMMON", "UNCOMMON", "RARE", "EPIC"));
			plant.immuneToFire = _true;
			plant.specialInformation = new StringListProcedure(emptyLists ? null : "string1",
					Arrays.asList("info 1", "info 2", "test, is this", "another one"));
			plant.creativePickItem = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocks).getName());
			plant.colorOnMap = getRandomItem(random, ElementUtil.getDataListAsStringArray("mapcolors"));
			plant.offsetType = getRandomString(random, Arrays.asList("NONE", "XZ", "XYZ"));
			plant.aiPathNodeType = getRandomItem(random, ElementUtil.getDataListAsStringArray("pathnodetypes"));
			plant.strippingResult = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocks).getName());
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
			Range<Integer> xpAmount = getRandomIntRange(random, Plant.class, "xpAmountMin", "xpAmountMax");
			plant.dropAmount = getRandomInt(random, Plant.class, "dropAmount");
			plant.xpAmountMin = xpAmount.getMinimum();
			plant.xpAmountMax = xpAmount.getMaximum();
			plant.useLootTableForDrops = !_true;
			plant.generateFeature = _true;
			plant.frequencyOnChunks = getRandomInt(random, Plant.class, "frequencyOnChunks");
			plant.patchSize = getRandomInt(random, Plant.class, "patchSize");
			plant.generateAtAnyHeight = _true;
			plant.generationType = getRandomItem(random, new String[] { "Grass", "Flower" });
			plant.ignitedByLava = !_true;
			plant.flammability = getRandomInt(random, Plant.class, "flammability");
			plant.fireSpreadSpeed = getRandomInt(random, Plant.class, "fireSpreadSpeed");
			plant.speedFactor = getRandomDouble(random, Plant.class, "speedFactor");
			plant.jumpFactor = getRandomDouble(random, Plant.class, "jumpFactor");
			plant.canBePlacedOn = new ArrayList<>();
			if (!emptyLists) {
				plant.canBePlacedOn = subset(random, blocksAndTags.size() / 16, blocksAndTags,
						e -> new MItemBlock(modElement.getWorkspace(), e.getName()));
				plant.canBePlacedOn.add(new MItemBlock(modElement.getWorkspace(), "TAG:walls"));
			}
			plant.restrictionBiomes = new ArrayList<>();
			if (!emptyLists) {
				if (_true) {
					plant.restrictionBiomes.add(new BiomeEntry(modElement.getWorkspace(), "#is_overworld"));
				} else {
					plant.restrictionBiomes = subset(random, 5, biomes,
							e -> new BiomeEntry(modElement.getWorkspace(), e.getName()));
				}
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
			plant.onEntityFallsOn = new Procedure("procedure12");
			plant.onHitByProjectile = new Procedure("procedure13");
			plant.placingCondition = emptyLists ? null : new Procedure("condition2");
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
			if (!emptyLists && !"sapling".equals(plant.plantType)) {
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
			item.creativeTabs = emptyLists ? List.of() : tabs;
			item.stackSize = getRandomInt(random, Item.class, "stackSize");
			item.enchantability = getRandomInt(random, Item.class, "enchantability");
			item.useDuration = getRandomInt(random, Item.class, "useDuration");
			item.toolType = getRandomDouble(random, Item.class, "toolType");
			item.damageCount = getRandomInt(random, Item.class, "damageCount");
			item.destroyAnyBlock = _true;
			item.inventorySize = getRandomInt(random, Item.class, "inventorySize");
			item.inventoryStackSize = getRandomInt(random, Item.class, "inventoryStackSize");
			item.guiBoundTo = emptyLists || guis.isEmpty() ? null : getRandomItem(random, guis);
			item.openGUIOnRightClick = new LogicProcedure(_true ? null : "condition3", _true);
			item.recipeRemainder = new MItemBlock(modElement.getWorkspace(),
					emptyLists ? "" : getRandomMCItem(random, blocksAndItems).getName());
			item.stayInGridWhenCrafting = _true;
			item.damageOnCrafting = _true;
			item.repairItems = new ArrayList<>();
			if (!emptyLists) {
				item.repairItems = subset(random, blocksAndItemsAndTags.size() / 8, blocksAndItemsAndTags,
						e -> new MItemBlock(modElement.getWorkspace(), e.getName()));
				item.repairItems.add(new MItemBlock(modElement.getWorkspace(), "TAG:walls"));
			}
			item.immuneToFire = _true;
			item.isPiglinCurrency = _true;
			item.glowCondition = new LogicProcedure(emptyLists ? "condition3" : null, _true);
			item.onRightClickedInAir = new Procedure("procedure1");
			item.onRightClickedOnBlock = emptyLists ? new Procedure("actionresulttype1") : new Procedure("procedure2");
			item.onCrafted = new Procedure("procedure3");
			item.onEntityHitWith = new Procedure("procedure4");
			item.onItemInInventoryTick = new Procedure("procedure5");
			item.onItemInUseTick = new Procedure("procedure6");
			item.onStoppedUsing = new Procedure("procedure7");
			item.onEntitySwing = new Procedure("procedure8");
			item.onDroppedByPlayer = new Procedure("procedure9");
			item.everyTickWhileUsing = new Procedure("procedure10");
			item.onItemEntityDestroyed = new Procedure("procedure11");
			item.enableMeleeDamage = !_true;
			item.damageVsEntity = getRandomDouble(random, Item.class, "damageVsEntity");
			item.attackSpeed = getRandomDouble(random, Item.class, "attackSpeed");
			item.specialInformation = new StringListProcedure(emptyLists ? null : "string1",
					Arrays.asList("info 1", "info 2", "test, is this", "another one"));
			item.texture = new TextureHolder(modElement.getWorkspace(), "test2");
			item.guiTexture = new TextureHolder(modElement.getWorkspace(), emptyLists ? "" : "test3");
			item.renderType = emptyLists ? 0 : 3;
			item.customModelName = emptyLists ?
					getRandomItem(random, ItemGUI.builtinitemmodels).getReadableName() :
					"ModelCustomJavaModel";

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
					stateEntry.setWorkspace(modElement.getWorkspace());
					stateEntry.customModelName = getRandomItem(random, ItemGUI.builtinitemmodels).getReadableName();
					stateEntry.texture = new TextureHolder(modElement.getWorkspace(), i == 0 ? "test" : "test" + i);
					stateEntry.renderType = 0;
					stateEntry.stateMap = stateMap;

					item.states.add(stateEntry);
				}
			}

			item.isFood = _true;
			item.nutritionalValue = getRandomInt(random, Item.class, "nutritionalValue");
			item.saturation = getRandomDouble(random, Item.class, "saturation");
			item.isMeat = _true;
			item.isAlwaysEdible = _true;
			item.animation = getRandomItem(random,
					new String[] { "block", "bow", "crossbow", "drink", "eat", "none", "spear" });
			item.eatResultItem = new MItemBlock(modElement.getWorkspace(),
					emptyLists ? "" : getRandomMCItem(random, blocksAndItems).getName());
			item.onFinishUsingItem = new Procedure("procedure3");
			item.enableRanged = _true;
			item.projectile = new ProjectileEntry(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadArrowProjectiles(modElement.getWorkspace())));
			item.shootConstantly = emptyLists;
			item.rangedItemChargesPower = !item.shootConstantly;
			item.projectileDisableAmmoCheck = random.nextBoolean();
			item.onRangedItemUsed = new Procedure("procedure4");
			item.rangedUseCondition = new Procedure("condition1");
			item.isMusicDisc = !_true;
			item.musicDiscDescription = modElement.getName();
			item.musicDiscLengthInTicks = getRandomInt(random, Item.class, "musicDiscLengthInTicks");
			item.musicDiscAnalogOutput = getRandomInt(random, Item.class, "musicDiscAnalogOutput");
			item.musicDiscMusic = new Sound(modElement.getWorkspace(),
					getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			if (!emptyLists) {
				item.providedBannerPatterns.add("Examplebannerpattern1");
				item.providedBannerPatterns.add("Examplebannerpattern2");
			}
			item.animations = new ArrayList<>();
			if (_true) {
				for (DataListEntry anim : ElementUtil.loadAnimations(modElement.getWorkspace())) {
					Item.AnimationEntry animation = new Item.AnimationEntry();
					animation.animation = new Animation(modElement.getWorkspace(), anim);
					animation.condition = random.nextBoolean() ? null : new Procedure("condition2");
					animation.speed = 12.3;
					item.animations.add(animation);
				}
			}
			item.attributeModifiers = new ArrayList<>();
			if (!emptyLists) {
				for (DataListEntry attribute : ElementUtil.loadAllAttributes(modElement.getWorkspace())) {
					AttributeModifierEntry entry = new AttributeModifierEntry();
					entry.equipmentSlot = getRandomItem(random, ElementUtil.getDataListAsStringArray("equipmentslots"));
					entry.attribute = new AttributeEntry(modElement.getWorkspace(), attribute);
					entry.amount = getRandomDouble(random, AttributeModifierEntry.class, "amount");
					entry.operation = getRandomItem(random,
							new String[] { "ADD_VALUE", "ADD_MULTIPLIED_BASE", "ADD_MULTIPLIED_TOTAL" });
					item.attributeModifiers.add(entry);
				}
			}
			return item;
		} else if (ModElementType.ITEMEXTENSION.equals(modElement.getType())) {
			ItemExtension itemExtension = new ItemExtension(modElement);
			itemExtension.item = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItems).getName());

			itemExtension.enableFuel = !emptyLists;
			itemExtension.fuelPower = new NumberProcedure(_true ? "number3" : null, 1600);
			itemExtension.fuelSuccessCondition = _true ? new Procedure("condition1") : null;
			itemExtension.compostLayerChance = getRandomDouble(random, ItemExtension.class, "compostLayerChance");
			itemExtension.hasDispenseBehavior = emptyLists;
			itemExtension.dispenseSuccessCondition = _true ? new Procedure("condition1") : null;
			itemExtension.dispenseResultItemstack = _true ? new Procedure("itemstack1") : null;
			return itemExtension;
		} else if (ModElementType.PROJECTILE.equals(modElement.getType())) {
			Projectile projectile = new Projectile(modElement);
			projectile.actionSound = new Sound(modElement.getWorkspace(),
					getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			projectile.power = getRandomDouble(random, Projectile.class, "power");
			projectile.damage = getRandomDouble(random, Projectile.class, "damage");
			projectile.knockback = getRandomInt(random, Projectile.class, "knockback");
			projectile.showParticles = _true;
			projectile.igniteFire = _true;
			projectile.disableGravity = emptyLists;
			projectile.disableDiscarding = _true;
			projectile.projectileItem = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItems).getName());
			projectile.entityModel = emptyLists ? "Default" : "ModelCustomJavaModel";
			if (!emptyLists) {
				projectile.customModelTexture = "entity_texture_2.png";
			} else {
				projectile.customModelTexture = "";
			}
			projectile.modelWidth = getRandomDouble(random, Projectile.class, "modelWidth");
			projectile.modelHeight = getRandomDouble(random, Projectile.class, "modelHeight");
			projectile.onHitsBlock = new Procedure("procedure1");
			projectile.onHitsEntity = new Procedure("procedure2");
			projectile.onHitsPlayer = new Procedure("procedure3");
			projectile.onFlyingTick = new Procedure("procedure4");
			return projectile;
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
				entry1.infinite = _true;
				entry1.amplifier = 1;
				entry1.ambient = !_true;
				entry1.showParticles = !_true;
				effects.add(entry1);

				Potion.CustomEffectEntry entry2 = new Potion.CustomEffectEntry();
				entry2.effect = new EffectEntry(modElement.getWorkspace(),
						getRandomDataListEntry(random, ElementUtil.loadAllPotionEffects(modElement.getWorkspace())));
				entry2.duration = 7200;
				entry2.infinite = !_true;
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
			potionEffect.icon = new TextureHolder(modElement.getWorkspace(), "effect1");
			potionEffect.isInstant = !_true;
			potionEffect.mobEffectCategory = getRandomItem(random, new String[] { "NEUTRAL", "HARMFUL", "BENEFICIAL" });
			potionEffect.renderStatusInHUD = _true;
			potionEffect.renderStatusInInventory = _true;
			potionEffect.isCuredbyHoney = _true;
			potionEffect.particle = emptyLists ?
					null :
					new Particle(modElement.getWorkspace(),
							getRandomDataListEntry(random, ElementUtil.loadAllParticles(modElement.getWorkspace())));
			potionEffect.onAddedSound = new Sound(modElement.getWorkspace(),
					emptyLists ? "" : getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			List<AttributeModifierEntry> modifiers = new ArrayList<>();
			if (!emptyLists) {
				for (DataListEntry attribute : ElementUtil.loadAllAttributes(modElement.getWorkspace())) {
					AttributeModifierEntry entry = new AttributeModifierEntry();
					entry.attribute = new AttributeEntry(modElement.getWorkspace(), attribute);
					entry.amount = getRandomDouble(random, AttributeModifierEntry.class, "amount");
					entry.operation = getRandomItem(random,
							new String[] { "ADD_VALUE", "ADD_MULTIPLIED_BASE", "ADD_MULTIPLIED_TOTAL" });
					modifiers.add(entry);
				}
			}
			potionEffect.modifiers = modifiers;
			potionEffect.onStarted = new Procedure("procedure1");
			potionEffect.onActiveTick = new Procedure("procedure2");
			potionEffect.onExpired = new Procedure("procedure3");
			potionEffect.activeTickCondition = new Procedure("condition1");
			potionEffect.onMobRemoved = new Procedure("procedure4");
			potionEffect.onMobHurt = new Procedure("procedure5");
			return potionEffect;
		} else if (ModElementType.LOOTTABLE.equals(modElement.getType())) {
			LootTable lootTable = new LootTable(modElement);

			lootTable.name = modElement.getName().toLowerCase(Locale.ENGLISH);
			lootTable.namespace = getRandomItem(random, new String[] { "minecraft", "mod" });
			lootTable.type = getRandomItem(random,
					new String[] { "Generic", "Entity", "Block", "Chest", "Fishing", "Empty", "Advancement reward" });

			lootTable.pools = new ArrayList<>();
			if (!emptyLists) {
				int pools = random.nextInt(4) + 1;
				for (int i = 0; i < pools; i++) {
					LootTable.Pool pool = new LootTable.Pool();
					Range<Integer> rollsRange = getRandomIntRange(random, LootTable.Pool.class, "minrolls", "maxrolls");
					pool.minrolls = rollsRange.getMinimum();
					pool.maxrolls = rollsRange.getMaximum();
					pool.hasbonusrolls = _true;
					Range<Integer> bonusRollsRange = getRandomIntRange(random, LootTable.Pool.class, "minbonusrolls",
							"maxbonusrolls");
					pool.minbonusrolls = bonusRollsRange.getMinimum();
					pool.maxbonusrolls = bonusRollsRange.getMaximum();
					pool.entries = new ArrayList<>();

					int entries = random.nextInt(4) + 1;
					for (int j = 0; j < entries; j++) {
						LootTable.Pool.Entry entry = new LootTable.Pool.Entry();

						entry.type = "item";
						entry.weight = getRandomInt(random, LootTable.Pool.Entry.class, "weight");

						entry.minCount = new int[] { 1, 2, 2, 3 }[valueIndex];
						entry.maxCount = new int[] { 4, 2, 7, 3 }[valueIndex];

						entry.affectedByFortune = _true;
						entry.explosionDecay = _true;

						entry.silkTouchMode = new int[] { 0, 1, 2, 1 }[valueIndex];

						Range<Integer> enchantmentLevelRange = getRandomIntRange(random, LootTable.Pool.Entry.class,
								"minEnchantmentLevel", "maxEnchantmentLevel");
						entry.minEnchantmentLevel = enchantmentLevelRange.getMinimum();
						entry.maxEnchantmentLevel = enchantmentLevelRange.getMaximum();

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
		} else if (ModElementType.ENCHANTMENT.equals(modElement.getType())) {
			Enchantment enchantment = new Enchantment(modElement);
			enchantment.name = modElement.getName().toLowerCase(Locale.ENGLISH);
			enchantment.supportedSlots = getRandomItem(random, ElementUtil.getDataListAsStringArray("equipmentslots"));
			enchantment.weight = getRandomInt(random, Enchantment.class, "weight");
			enchantment.anvilCost = getRandomInt(random, Enchantment.class, "anvilCost");
			enchantment.maxLevel = getRandomInt(random, Enchantment.class, "maxLevel");
			enchantment.damageModifier = getRandomInt(random, Enchantment.class, "damageModifier");
			enchantment.isTreasureEnchantment = _true;
			enchantment.isCurse = _true;
			enchantment.canGenerateInLootTables = !_true;
			enchantment.canVillagerTrade = _true;
			enchantment.supportedItems = new ArrayList<>();
			if (_true) {
				enchantment.supportedItems.add(new MItemBlock(modElement.getWorkspace(), "Items.WOODEN_PICKAXE"));
				enchantment.supportedItems.add(new MItemBlock(modElement.getWorkspace(), "Items.IRON_HELMET"));
				enchantment.supportedItems.add(new MItemBlock(modElement.getWorkspace(), "Items.CRIMSON_DOOR"));
			} else {
				enchantment.supportedItems.add(
						new MItemBlock(modElement.getWorkspace(), "TAG:minecraft:enchantable/fishing"));
			}
			enchantment.incompatibleEnchantments = new ArrayList<>();
			if (!emptyLists) {
				if (_true) {
					enchantment.incompatibleEnchantments = subset(random, 10,
							ElementUtil.loadAllEnchantments(modElement.getWorkspace()),
							e -> new net.mcreator.element.parts.Enchantment(modElement.getWorkspace(), e.getName()));
				} else {
					enchantment.incompatibleEnchantments.add(
							new net.mcreator.element.parts.Enchantment(modElement.getWorkspace(),
									"#minecraft:non_treasure"));
				}
			}
			return enchantment;
		} else if (ModElementType.PAINTING.equals(modElement.getType())) {
			Painting painting = new Painting(modElement);
			painting.texture = new TextureHolder(modElement.getWorkspace(), "other0");
			painting.title = modElement.getName();
			painting.author = modElement.getName() + " author";
			painting.width = getRandomInt(random, Painting.class, "width");
			painting.height = getRandomInt(random, Painting.class, "height");
			return painting;
		} else if (ModElementType.PARTICLE.equals(modElement.getType())) {
			net.mcreator.element.types.Particle particle = new net.mcreator.element.types.Particle(modElement);
			particle.texture = new TextureHolder(modElement.getWorkspace(), "particle1");
			particle.width = getRandomDouble(random, net.mcreator.element.types.Particle.class, "width");
			particle.frameDuration = getRandomInt(random, net.mcreator.element.types.Particle.class, "frameDuration");
			particle.emissiveRendering = _true;
			particle.height = getRandomDouble(random, net.mcreator.element.types.Particle.class, "height");
			particle.scale = new NumberProcedure(emptyLists ? null : "number1", 1.38);
			particle.fixedScale = _true;
			particle.gravity = getRandomDouble(random, net.mcreator.element.types.Particle.class, "gravity");
			particle.speedFactor = getRandomDouble(random, net.mcreator.element.types.Particle.class, "speedFactor");
			particle.canCollide = _true;
			particle.angularVelocity = getRandomDouble(random, net.mcreator.element.types.Particle.class,
					"angularVelocity");
			particle.angularAcceleration = getRandomDouble(random, net.mcreator.element.types.Particle.class,
					"angularAcceleration");
			particle.alwaysShow = !_true;
			particle.animate = _true;
			particle.maxAge = getRandomInt(random, net.mcreator.element.types.Particle.class, "maxAge");
			particle.maxAgeDiff = emptyLists ? 0 :
					getRandomInt(random, net.mcreator.element.types.Particle.class, "maxAgeDiff");
			particle.rotationProvider = emptyLists ? null : new Procedure("vector1");
			particle.renderType = new String[] { "OPAQUE", "OPAQUE", "TRANSLUCENT", "TRANSLUCENT" }[valueIndex];
			particle.additionalExpiryCondition = new Procedure("condition1");
			return particle;
		} else if (ModElementType.GAMERULE.equals(modElement.getType())) {
			GameRule gamerule = new GameRule(modElement);
			gamerule.displayName = modElement.getName();
			gamerule.description = modElement.getName() + " description";
			gamerule.category = getRandomString(random,
					Arrays.asList("PLAYER", "UPDATES", "CHAT", "DROPS", "MISC", "MOBS", "SPAWNING"));
			gamerule.type = _true ? "Number" : "Logic";
			gamerule.defaultValueLogic = random.nextBoolean();
			gamerule.defaultValueNumber = getRandomInt(random, GameRule.class, "defaultValueNumber");
			gamerule.getModElement().putMetadata("type", "Number".equals(gamerule.type) ?
					VariableTypeLoader.BuiltInTypes.NUMBER.getName() :
					VariableTypeLoader.BuiltInTypes.LOGIC.getName());
			return gamerule;
		} else if (ModElementType.VILLAGERPROFESSION.equals(modElement.getType())) {
			VillagerProfession profession = new VillagerProfession(modElement);
			profession.displayName = modElement.getName();
			List<MItemBlock> poiBlocks = ElementUtil.loadAllPOIBlocks(modElement.getWorkspace());
			// Try to select POI that is not commonly generated (stone, air, dirt, ...)
			profession.pointOfInterest = new MItemBlock(modElement.getWorkspace(), getRandomMCItem(random,
					blocks.stream()
							.filter(e -> !poiBlocks.contains(new MItemBlock(modElement.getWorkspace(), e.getName())))
							.filter(e -> !(e.getName().toLowerCase(Locale.ENGLISH).contains("air") || e.getName()
									.toLowerCase(Locale.ENGLISH).contains("stone") || e.getName()
									.toLowerCase(Locale.ENGLISH).contains("dirt") || e.getName()
									.toLowerCase(Locale.ENGLISH).contains("grass") || e.getName()
									.toLowerCase(Locale.ENGLISH).contains("sand") || e.getName()
									.toLowerCase(Locale.ENGLISH).contains("water")))
							.collect(Collectors.toList())).getName());
			profession.actionSound = new Sound(modElement.getWorkspace(),
					getRandomItem(random, ElementUtil.getAllSounds(modElement.getWorkspace())));
			profession.hat = getRandomString(random, Arrays.asList("None", "Partial", "Full"));
			profession.professionTextureFile = "entity_texture_0.png";
			profession.zombifiedProfessionTextureFile = "entity_texture_1.png";
			return profession;
		} else if (ModElementType.VILLAGERTRADE.equals(modElement.getType())) {
			VillagerTrade villagerTrade = new VillagerTrade(modElement);
			villagerTrade.tradeEntries = new ArrayList<>();
			if (!emptyLists) {
				int tradeEntries = random.nextInt(5) + 1;
				for (int i = 0; i < tradeEntries; i++) {
					VillagerTrade.CustomTradeEntry trade = new VillagerTrade.CustomTradeEntry();
					trade.villagerProfession = new ProfessionEntry(modElement.getWorkspace(),
							getRandomDataListEntry(random,
									ElementUtil.loadAllVillagerProfessions(modElement.getWorkspace())));
					trade.entries = new ArrayList<>();

					int entries = random.nextInt(5) + 1;
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

					int wanderingEntries = random.nextInt(5) + 1;
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
			procedure.procedurexml = AnnotationUtils.getBlocklyXMLDefaultValue(procedure.getClass(), "procedurexml");
			procedure.skipDependencyNullCheck = _true;
			return procedure;
		} else if (ModElementType.BESCRIPT.equals(modElement.getType())) {
			BEScript bescript = new BEScript(modElement);
			bescript.scriptxml = AnnotationUtils.getBlocklyXMLDefaultValue(bescript.getClass(), "scriptxml")
					.replace("no_ext_trigger", "be_global_world_loaded");
			return bescript;
		} else if (ModElementType.DAMAGETYPE.equals(modElement.getType())) {
			DamageType damageType = new DamageType(modElement);
			damageType.exhaustion = getRandomDouble(random, DamageType.class, "exhaustion");
			damageType.scaling = getRandomString(random,
					Arrays.asList("never", "always", "when_caused_by_living_non_player"));
			damageType.effects = getRandomString(random,
					Arrays.asList("hurt", "thorns", "drowning", "burning", "poking", "freezing"));
			damageType.normalDeathMessage = "%1$s was slain";
			damageType.itemDeathMessage = "%1$s was slain by %2$s using %3$s";
			damageType.playerDeathMessage = "%1$s was slain whilst escaping %2$s";
			return damageType;
		}
		// As a feature requires placement and feature to place, this GE is only returned for uiTests
		// For generator tests, it will be tested by GTFeatureBlocks anyway
		else if (ModElementType.FEATURE.equals(modElement.getType()) && uiTest) {
			Feature feature = new Feature(modElement);
			feature.generationStep = TestWorkspaceDataProvider.getRandomItem(random,
					ElementUtil.getDataListAsStringArray("generationsteps"));
			feature.restrictionBiomes = new ArrayList<>();
			if (!emptyLists) {
				feature.restrictionBiomes = subset(random, 5, biomes,
						e -> new BiomeEntry(modElement.getWorkspace(), e.getName()));
				feature.restrictionBiomes.add(new BiomeEntry(modElement.getWorkspace(), "#is_overworld"));
				feature.restrictionBiomes.add(new BiomeEntry(modElement.getWorkspace(), "#minecraft:test"));
			}
			feature.generateCondition = _true ? new Procedure("condition1") : null;
			feature.featurexml = AnnotationUtils.getBlocklyXMLDefaultValue(feature.getClass(), "featurexml");
			feature.skipPlacement = !_true;
			return feature;
		} else if (ModElementType.ATTRIBUTE.equals(modElement.getType())) {
			Attribute attribute = new Attribute(modElement);
			attribute.name = modElement.getName();
			Range<Double> value = getRandomDoubleRange(random, Attribute.class, "minValue", "maxValue");
			attribute.minValue = value.getMinimum();
			attribute.maxValue = value.getMaximum();
			attribute.defaultValue = getRandomDouble(random, attribute.minValue, attribute.maxValue);
			attribute.entities = new ArrayList<>();
			attribute.sentiment = new String[] { "POSITIVE", "NEUTRAL", "NEGATIVE", "NEUTRAL" }[valueIndex];
			if (!emptyLists) {
				attribute.entities = subset(random, 20, ElementUtil.loadAllSpawnableEntities(modElement.getWorkspace()),
						e -> new net.mcreator.element.parts.EntityEntry(modElement.getWorkspace(), e.getName()));
				attribute.addToPlayers = _true;
			} else {
				attribute.addToAllEntities = true;
			}
			return attribute;
		} else if (ModElementType.BEITEM.equals(modElement.getType())) {
			BEItem beitem = new BEItem(modElement);
			beitem.name = modElement.getName();
			beitem.texture = new TextureHolder(modElement.getWorkspace(), "test2");
			beitem.hasGlint = _true;
			beitem.stackSize = getRandomInt(random, BEItem.class, "stackSize");
			beitem.useDuration = getRandomDouble(random, BEItem.class, "useDuration");
			beitem.maxDurability = getRandomInt(random, BEItem.class, "maxDurability");
			beitem.enableMeleeDamage = !_true;
			beitem.damageVsEntity = getRandomInt(random, BEItem.class, "damageVsEntity");
			beitem.isFood = emptyLists;
			beitem.foodNutritionalValue = getRandomInt(random, BEItem.class, "foodNutritionalValue");
			beitem.foodSaturation = getRandomDouble(random, BEItem.class, "foodSaturation");
			beitem.foodCanAlwaysEat = _true;
			beitem.handEquipped = _true;
			beitem.rarity = getRandomString(random, Arrays.asList("common", "uncommon", "rare", "epic"));
			beitem.enableCreativeTab = !_true;
			beitem.creativeTab = getRandomItem(random, ElementUtil.loadAllTabs(modElement.getWorkspace())).toString();
			beitem.isHiddenInCommands = _true;
			beitem.movementModifier = getRandomDouble(random, BEItem.class, "movementModifier");
			beitem.allowOffHand = _true;
			beitem.fuelDuration = getRandomDouble(random, BEItem.class, "fuelDuration");
			beitem.shouldDespawn = _true;
			beitem.stackedByData = _true;
			beitem.usingConvertsTo = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, filterAir(blocksAndItems)).getName());
			beitem.animation = getRandomItem(random,
					new String[] { "block", "bow", "crossbow", "drink", "eat", "none", "spear", "camera", "brush",
							"spyglass" });
			beitem.blockToPlace = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, filterAir(blocks)).getName());
			beitem.blockPlaceableOn = new ArrayList<>();
			beitem.entityToPlace = new EntityEntry(modElement.getWorkspace(),
					getRandomItem(random, ElementUtil.loadAllSpawnableEntities(modElement.getWorkspace())));
			beitem.entityDispensableOn = new ArrayList<>();
			beitem.entityPlaceableOn = new ArrayList<>();
			if (!emptyLists) {
				beitem.blockPlaceableOn = subset(random, blocks.size() / 8, blocks,
						e -> new MItemBlock(modElement.getWorkspace(), e.getName()));
				beitem.entityDispensableOn = subset(random, blocks.size() / 8, blocks,
						e -> new MItemBlock(modElement.getWorkspace(), e.getName()));
				beitem.entityPlaceableOn = subset(random, blocks.size() / 8, blocks,
						e -> new MItemBlock(modElement.getWorkspace(), e.getName()));
			}
			beitem.localScripts = new ArrayList<>();
			if (!emptyLists) {
				beitem.localScripts.add("Examplebescript1");
				beitem.localScripts.add("Examplebescript3");
			}
			return beitem;
		} else if (ModElementType.BEBLOCK.equals(modElement.getType())) {
			BEBlock beblock = new BEBlock(modElement);
			beblock.name = modElement.getName();
			beblock.texture = new TextureHolder(modElement.getWorkspace(), "test");
			beblock.textureTop = new TextureHolder(modElement.getWorkspace(), "test2");
			beblock.textureLeft = new TextureHolder(modElement.getWorkspace(), "test3");
			beblock.textureFront = new TextureHolder(modElement.getWorkspace(), "test4");
			beblock.textureRight = new TextureHolder(modElement.getWorkspace(), "test5");
			beblock.textureBack = new TextureHolder(modElement.getWorkspace(), "test6");
			beblock.renderType = new int[] { 10, 11, 12, 10 }[valueIndex];
			beblock.customModelName = new String[] { "Normal", "Cross model", "Single texture", "Normal" }[valueIndex];
			beblock.enableCreativeTab = !_true;
			beblock.creativeTab = getRandomItem(random, ElementUtil.loadAllTabs(modElement.getWorkspace())).toString();
			beblock.isHiddenInCommands = _true;
			beblock.hardness = getRandomDouble(random, BEBlock.class, "hardness");
			beblock.resistance = getRandomDouble(random, BEBlock.class, "resistance");
			beblock.customDrop = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItems).getName());
			beblock.dropAmount = getRandomInt(random, BEBlock.class, "dropAmount");
			beblock.flammability = getRandomInt(random, BEBlock.class, "flammability");
			beblock.flammableDestroyChance = getRandomInt(random, BEBlock.class, "flammableDestroyChance");
			beblock.friction = getRandomDouble(random, BEBlock.class, "friction");
			beblock.soundOnStep = new StepSound(modElement.getWorkspace(),
					getRandomDataListEntry(random, ElementUtil.loadStepSounds()));
			beblock.lightEmission = getRandomInt(random, BEBlock.class, "lightEmission");
			beblock.colorOnMap = getRandomItem(random, ElementUtil.getDataListAsStringArray("mapcolors"));
			beblock.generateFeature = _true;
			beblock.generationShape = getRandomString(random, List.of("uniform", "triangle"));
			var generateHeight = getRandomIntRange(random, BEBlock.class, "minGenerateHeight", "maxGenerateHeight");
			beblock.minGenerateHeight = generateHeight.getMinimum();
			beblock.maxGenerateHeight = generateHeight.getMaximum();
			beblock.frequencyPerChunks = getRandomInt(random, BEBlock.class, "frequencyPerChunks");
			beblock.oreCount = getRandomInt(random, BEBlock.class, "oreCount");
			beblock.blocksToReplace = new ArrayList<>();
			if (!emptyLists) {
				beblock.blocksToReplace = subset(random, blocksAndTags.size() / 8, blocksAndTags,
						e -> new MItemBlock(modElement.getWorkspace(), e.getName()));
			}

			beblock.rotationMode = random.nextInt(0, 5);
			beblock.renderMethod = getRandomItem(random,
					List.of("opaque", "double_sided", "blend", "alpha_test_single_sided", "alpha_test",
							"alpha_test_to_opaque", "alpha_test_single_sided_to_opaque", "blend_to_opaque"));
			beblock.tintMethod = getRandomItem(random,
					List.of("(none)", "birch_foliage", "default_foliage", "dry_foliage", "evergreen_foliage", "grass",
							"water"));

			beblock.localScripts = new ArrayList<>();
			if (!emptyLists) {
				beblock.localScripts.add("Examplebescript1");
				beblock.localScripts.add("Examplebescript3");
			}
			return beblock;
		}
		return null;
	}

	private static GeneratableElement getCommandExample(ModElement modElement, String type, Random random) {
		Command command = new Command(modElement);
		command.commandName = modElement.getName();
		command.type = type;
		command.permissionLevel = getRandomString(random, List.of("No requirement", "1", "2", "3", "4"));
		command.argsxml = AnnotationUtils.getBlocklyXMLDefaultValue(command.getClass(), "argsxml");
		return command;
	}

	public static LivingEntity getLivingEntity(ModElement modElement, Random random, boolean _true, boolean emptyLists,
			int valueIndex, List<MCItem> blocksAndItems, List<MCItem> blocksAndItemsAndTags, List<DataListEntry> biomes,
			List<String> guis) {
		LivingEntity livingEntity = new LivingEntity(modElement);
		livingEntity.mobName = modElement.getName();
		livingEntity.mobLabel = "mod label " + StringUtils.machineToReadableName(modElement.getName());
		livingEntity.mobModelTexture = "entity_texture_1.png";
		livingEntity.transparentModelCondition = new LogicProcedure(emptyLists ? "condition1" : null, _true);
		livingEntity.isShakingCondition = new LogicProcedure(emptyLists ? "condition2" : null, !_true);
		livingEntity.solidBoundingBox = new LogicProcedure(emptyLists ? "condition3" : null, _true);
		livingEntity.visualScale = new NumberProcedure(emptyLists ? null : "number1", 8.123);
		livingEntity.boundingBoxScale = new NumberProcedure(emptyLists ? null : "number2", 4.223);
		livingEntity.mobModelName = emptyLists ?
				getRandomItem(random, LivingEntityGUI.builtinmobmodels).getReadableName() :
				"ModelCustomJavaModel";
		livingEntity.hasSpawnEgg = !_true;
		livingEntity.spawnEggBaseColor = Color.red;
		livingEntity.spawnEggDotColor = Color.green;
		livingEntity.spawnEggTexture = new TextureHolder(modElement.getWorkspace(), emptyLists ? "" : "test3");
		livingEntity.isBoss = _true;
		livingEntity.creativeTabs = emptyLists ?
				List.of() :
				ElementUtil.loadAllTabs(modElement.getWorkspace()).stream()
				.map(e -> new TabEntry(modElement.getWorkspace(), e)).toList();
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
		livingEntity.mobBehaviourType = getRandomString(random, List.of("Creature", "Mob", "Raider"));
		livingEntity.mobCreatureType = getRandomItem(random,
				new String[] { "UNDEFINED", "UNDEAD", "ARTHROPOD", "ILLAGER", "WATER" });
		livingEntity.attackStrength = getRandomInt(random, LivingEntity.class, "attackStrength");
		livingEntity.attackKnockback = getRandomDouble(random, LivingEntity.class, "attackKnockback");
		livingEntity.knockbackResistance = getRandomDouble(random, LivingEntity.class, "knockbackResistance");
		livingEntity.movementSpeed = getRandomDouble(random, LivingEntity.class, "movementSpeed");
		livingEntity.stepHeight = getRandomDouble(random, LivingEntity.class, "stepHeight");
		livingEntity.armorBaseValue = getRandomDouble(random, LivingEntity.class, "armorBaseValue");
		livingEntity.health = getRandomInt(random, LivingEntity.class, "health");
		livingEntity.trackingRange = getRandomInt(random, LivingEntity.class, "trackingRange");
		livingEntity.followRange = getRandomInt(random, LivingEntity.class, "followRange");
		livingEntity.waterMob = !_true;
		livingEntity.breatheUnderwater = new LogicProcedure(emptyLists ? null : "condition3", _true);
		livingEntity.pushedByFluids = new LogicProcedure(emptyLists ? null : "condition4", _true);
		livingEntity.flyingMob = !_true;
		livingEntity.inventorySize = getRandomInt(random, LivingEntity.class, "inventorySize");
		livingEntity.inventoryStackSize = getRandomInt(random, LivingEntity.class, "inventoryStackSize");
		livingEntity.disableCollisions = !_true;
		livingEntity.immuneToFire = _true;
		livingEntity.immuneToArrows = !_true;
		livingEntity.immuneToFallDamage = !_true;
		livingEntity.immuneToCactus = _true;
		livingEntity.immuneToDrowning = !_true;
		livingEntity.immuneToLightning = !_true;
		livingEntity.immuneToPotions = !_true;
		livingEntity.immuneToPlayer = !_true;
		livingEntity.immuneToExplosion = _true;
		livingEntity.immuneToTrident = !_true;
		livingEntity.immuneToAnvil = !_true;
		livingEntity.immuneToDragonBreath = !_true;
		livingEntity.immuneToWither = _true;
		livingEntity.xpAmount = getRandomInt(random, LivingEntity.class, "xpAmount");
		livingEntity.ridable = _true;
		livingEntity.canControlStrafe = !_true;
		livingEntity.canControlForward = _true;
		livingEntity.guiBoundTo = emptyLists || guis.isEmpty() ? null : getRandomItem(random, guis);
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
		livingEntity.raidCelebrationSound = new Sound(modElement.getWorkspace(),
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
		livingEntity.breedable = _true && !livingEntity.mobBehaviourType.equals("Raider");
		livingEntity.tameable = random.nextBoolean();
		livingEntity.aiBase = (livingEntity.breedable || livingEntity.mobBehaviourType.equals("Raider")) ?
				"(none)" :
				new String[] { "(none)", "Wolf", "Cow",
						"Zombie" }[valueIndex]; // index 0 must be none for GTAITaskBlocks
		livingEntity.aixml = "<xml xmlns=\"https://developers.google.com/blockly/xml\"><block type=\"aitasks_container\" deletable=\"false\" x=\"40\" y=\"40\"></block></xml>";
		livingEntity.breedTriggerItems = new ArrayList<>();
		if (!emptyLists) {
			livingEntity.breedTriggerItems = subset(random, 5, blocksAndItemsAndTags,
					e -> new MItemBlock(modElement.getWorkspace(), e.getName()));
		}
		livingEntity.ranged = _true;
		livingEntity.rangedAttackItem = new MItemBlock(modElement.getWorkspace(),
				getRandomMCItem(random, blocksAndItems).getName());
		livingEntity.rangedAttackInterval = getRandomInt(random, LivingEntity.class, "rangedAttackInterval");
		livingEntity.rangedAttackRadius = getRandomDouble(random, LivingEntity.class, "rangedAttackRadius");
		livingEntity.spawnThisMob = !_true;
		livingEntity.doesDespawnWhenIdle = _true;
		livingEntity.spawningProbability = getRandomInt(random, LivingEntity.class, "spawningProbability");
		livingEntity.mobSpawningType = getRandomItem(random, ElementUtil.getDataListAsStringArray("mobspawntypes"));
		var numberOfMobsPerGroup = getRandomIntRange(random, LivingEntity.class, "minNumberOfMobsPerGroup",
				"maxNumberOfMobsPerGroup");
		livingEntity.minNumberOfMobsPerGroup = numberOfMobsPerGroup.getMinimum();
		livingEntity.maxNumberOfMobsPerGroup = numberOfMobsPerGroup.getMaximum();
		livingEntity.restrictionBiomes = new ArrayList<>();
		if (!emptyLists) {
			if (_true) {
				livingEntity.restrictionBiomes = subset(random, 5, biomes,
						e -> new BiomeEntry(modElement.getWorkspace(), e.getName()));
			} else {
				livingEntity.restrictionBiomes.add(new BiomeEntry(modElement.getWorkspace(), "#is_overworld"));
			}
		}
		livingEntity.spawnInDungeons = _true;
		livingEntity.modelWidth = getRandomDouble(random, LivingEntity.class, "modelWidth");
		livingEntity.modelHeight = getRandomDouble(random, LivingEntity.class, "modelHeight");
		livingEntity.mountedYOffset = getRandomDouble(random, LivingEntity.class, "mountedYOffset");
		livingEntity.modelShadowSize = getRandomDouble(random, LivingEntity.class, "modelShadowSize");
		for (int i = 0; i < livingEntity.raidSpawnsCount.length; i++)
			livingEntity.raidSpawnsCount[i] = (4 + i);
		livingEntity.modelLayers = new ArrayList<>();
		if (!emptyLists) {
			livingEntity.entityDataEntries.add(new PropertyDataWithValue<>(new PropertyData.LogicType("Logic"), _true));
			livingEntity.entityDataEntries.add(
					new PropertyDataWithValue<>(new PropertyData.IntegerType("Integer"), random.nextInt()));
			livingEntity.entityDataEntries.add(new PropertyDataWithValue<>(new PropertyData.StringType("String"),
					getRandomItem(random, new String[] { "value1", "value2", "\"value3\"" })));
			LivingEntity.ModelLayerEntry modelLayer = new LivingEntity.ModelLayerEntry();
			modelLayer.setWorkspace(modElement.getWorkspace());
			modelLayer.model = "Default";
			modelLayer.texture = "entity_texture_2.png";
			modelLayer.disableHurtOverlay = false;
			modelLayer.glow = true;
			modelLayer.condition = null;
			livingEntity.modelLayers.add(modelLayer);
			modelLayer = new LivingEntity.ModelLayerEntry();
			modelLayer.setWorkspace(modElement.getWorkspace());
			modelLayer.model = "Default";
			modelLayer.texture = "entity_texture_0.png";
			modelLayer.disableHurtOverlay = false;
			modelLayer.glow = false;
			modelLayer.condition = new Procedure("condition1");
			livingEntity.modelLayers.add(modelLayer);
			modelLayer = new LivingEntity.ModelLayerEntry();
			modelLayer.setWorkspace(modElement.getWorkspace());
			modelLayer.model = "Default";
			modelLayer.texture = "entity_texture_2.png";
			modelLayer.disableHurtOverlay = true;
			modelLayer.glow = true;
			modelLayer.condition = null;
			livingEntity.modelLayers.add(modelLayer);
		}
		livingEntity.animations = new ArrayList<>();
		if (!emptyLists) {
			for (DataListEntry anim : ElementUtil.loadAnimations(modElement.getWorkspace())) {
				LivingEntity.AnimationEntry animation = new LivingEntity.AnimationEntry();
				animation.animation = new Animation(modElement.getWorkspace(), anim);
				animation.condition = random.nextBoolean() ? null : new Procedure("condition1");
				animation.speed = 12.3;
				animation.amplitude = 15.4;
				animation.walking = _true;
				livingEntity.animations.add(animation);
			}
		}
		livingEntity.sensitiveToVibration = _true;
		livingEntity.vibrationalEvents = new ArrayList<>();
		if (!emptyLists) {
			livingEntity.vibrationalEvents.addAll(ElementUtil.loadAllGameEvents().stream()
					.map(e -> new GameEventEntry(modElement.getWorkspace(), e.getName())).toList());
			livingEntity.vibrationalEvents.add(new GameEventEntry(modElement.getWorkspace(), "#allay_can_listen"));
		}
		livingEntity.vibrationSensitivityRadius = new NumberProcedure(emptyLists ? null : "number1", 11);
		livingEntity.canReceiveVibrationCondition = new Procedure("condition1");
		livingEntity.onReceivedVibration = new Procedure("procedure1");
		return livingEntity;
	}

	private static GeneratableElement getBlockExample(ModElement modElement, Random random, boolean _true,
			boolean emptyLists, int valueIndex, @Nullable String blockBase) {
		var blocksAndItems = ElementUtil.loadBlocksAndItems(modElement.getWorkspace());
		var blocks = ElementUtil.loadBlocks(modElement.getWorkspace());
		var blocksAndTags = ElementUtil.loadBlocksAndTags(modElement.getWorkspace());
		var blocksWithItemForm = ElementUtil.loadBlocksWithItemForm(modElement.getWorkspace());
		var biomes = ElementUtil.loadAllBiomes(modElement.getWorkspace());
		var tabs = ElementUtil.loadAllTabs(modElement.getWorkspace()).stream()
				.map(e -> new TabEntry(modElement.getWorkspace(), e)).toList();
		var guis = modElement.getWorkspace().getModElements().stream()
				.filter(var -> var.getType() == ModElementType.GUI).map(ModElement::getName)
				.collect(Collectors.toList());

		Block block = new Block(modElement);
		block.blockBase = blockBase;
		block.name = modElement.getName();
		block.connectedSides = _true;
		block.displayFluidOverlay = _true;
		block.emissiveRendering = _true;
		block.transparencyType = new String[] { "SOLID", "CUTOUT", "CUTOUT_MIPPED", "TRANSLUCENT" }[valueIndex];
		block.disableOffset = random.nextBoolean();
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
				box.subtract = random.nextBoolean();

				block.boundingBoxes.add(box);
			}
		}
		block.rotationMode = blockBase == null ? new int[] { 0, 1, 4, 5 }[valueIndex] : 0;
		block.enablePitch = !_true;
		block.customProperties = new ArrayList<>();
		if (!emptyLists) {
			block.customProperties.add(
					new PropertyDataWithValue<>(new PropertyData.LogicType("CUSTOM:bool_prop"), _true));
			block.customProperties.add(
					new PropertyDataWithValue<>(new PropertyData.LogicType("CUSTOM:bool_prop2"), !_true));
			block.customProperties.add(
					new PropertyDataWithValue<>(new PropertyData.IntegerType("CUSTOM:int_prop", 3, 5), 4));
			block.customProperties.add(new PropertyDataWithValue<>(
					new PropertyData.StringType("CUSTOM:enum_prop", new String[] { "logic", "integer", "string" }),
					"string"));

			Map<String, List<String>> blockBaseProperties = BlockStatePropertyUtils.getBlockBaseProperties(
					modElement.getGeneratorConfiguration());
			Set<String> usedRegistryNames = new HashSet<>(blockBaseProperties.getOrDefault(blockBase, List.of()));
			for (DataListEntry entry : DataListLoader.loadDataMap("blockstateproperties").values()) {
				Map<?, ?> other = (Map<?, ?>) entry.getOther();
				PropertyDataWithValue<?> property = BlockStatePropertyUtils.fromDataListEntry(entry);
				if (property != null) {
					String registryName = BlockStatePropertyUtils.propertyRegistryName(property.property());
					if (List.of("axis", "facing", "face", "waterlogged").contains(registryName)
							|| usedRegistryNames.contains(registryName))
						continue;
					switch (property.property()) {
					case PropertyData.LogicType logicType ->
							block.customProperties.add(new PropertyDataWithValue<>(logicType, random.nextBoolean()));
					case PropertyData.IntegerType integerType -> {
						int min = Integer.parseInt((String) other.get("min"));
						int max = Integer.parseInt((String) other.get("max"));
						block.customProperties.add(
								new PropertyDataWithValue<>(integerType, random.nextInt(max - min) + min));
					}
					case PropertyData.StringType stringType -> {
						String[] data = ((List<?>) other.get("values")).stream().map(Object::toString)
								.toArray(String[]::new);
						block.customProperties.add(
								new PropertyDataWithValue<>(stringType, data[random.nextInt(data.length)]));
					}
					default -> {
					}
					}
					usedRegistryNames.add(registryName);
				}

				if (BlockStatePropertyUtils.getNumberOfPropertyCombinations(
						block.customProperties.stream().map(e -> (PropertyData<?>) e.property())
								.collect(Collectors.toList())) > BlockStatePropertyUtils.MAX_PROPERTY_COMBINATIONS) {
					break;
				}
			}
			// Remove last entry as it causes combinations to exceed MAX_PROPERTY_COMBINATIONS
			block.customProperties.removeLast();
		}
		block.animations = new ArrayList<>();
		if (!emptyLists) {
			for (DataListEntry anim : ElementUtil.loadAnimations(modElement.getWorkspace())) {
				Block.AnimationEntry animation = new Block.AnimationEntry();
				animation.animation = new Animation(modElement.getWorkspace(), anim);
				animation.condition = random.nextBoolean() ? null : new Procedure("condition1");
				animation.speed = 12.3;
				block.animations.add(animation);
			}
		}
		block.hardness = getRandomDouble(random, Block.class, "hardness");
		block.resistance = getRandomDouble(random, Block.class, "resistance");
		block.hasGravity = _true && blockBase == null;
		block.useLootTableForDrops = !_true;
		block.requiresCorrectTool = _true;
		block.hasBlockItem = !emptyLists;
		block.maxStackSize = getRandomInt(random, Block.class, "maxStackSize");
		block.rarity = getRandomString(random, Arrays.asList("COMMON", "UNCOMMON", "RARE", "EPIC"));
		block.immuneToFire = _true;
		block.creativeTabs = emptyLists ? List.of() : tabs;
		block.destroyTool = getRandomItem(random, new String[] { "Not specified", "pickaxe", "axe", "shovel", "hoe" });
		block.customDrop = new MItemBlock(modElement.getWorkspace(), getRandomMCItem(random, blocksAndItems).getName());
		block.ignitedByLava = _true;
		block.flammability = getRandomInt(random, Block.class, "flammability");
		block.fireSpreadSpeed = getRandomInt(random, Block.class, "fireSpreadSpeed");
		block.dropAmount = getRandomInt(random, Block.class, "dropAmount");
		Range<Integer> xpAmount = getRandomIntRange(random, Block.class, "xpAmountMin", "xpAmountMax");
		block.xpAmountMin = xpAmount.getMinimum();
		block.xpAmountMax = xpAmount.getMaximum();
		block.plantsGrowOn = _true;
		block.isNotColidable = _true && blockBase == null;
		block.canRedstoneConnect = _true;
		block.isWaterloggable = !block.hasGravity && blockBase == null;
		block.isLadder = _true;
		block.enchantPowerBonus = getRandomDouble(random, Block.class, "enchantPowerBonus");
		block.reactionToPushing = getRandomItem(random,
				new String[] { "NORMAL", "DESTROY", "BLOCK", "PUSH_ONLY", "IGNORE" });
		block.slipperiness = getRandomDouble(random, Block.class, "slipperiness");
		block.speedFactor = getRandomDouble(random, Block.class, "speedFactor");
		block.jumpFactor = getRandomDouble(random, Block.class, "jumpFactor");
		block.strippingResult = new MItemBlock(modElement.getWorkspace(), getRandomMCItem(random, blocks).getName());
		block.blockSetType = getRandomItem(random, new String[] { "OAK", "STONE", "IRON" });
		block.tickRate = _true ? 0 : getRandomInt(random, Block.class, "tickRate");
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
		block.luminance = new NumberProcedure(emptyLists ? null : "number3", 3);
		block.isReplaceable = !_true;
		block.canProvidePower = !_true;
		block.emittedRedstonePower = new NumberProcedure(emptyLists ? null : "number1", 8);
		block.creativePickItem = new MItemBlock(modElement.getWorkspace(), getRandomMCItem(random, blocks).getName());
		block.colorOnMap = getRandomItem(random, ElementUtil.getDataListAsStringArray("mapcolors"));
		block.noteBlockInstrument = getRandomItem(random, ElementUtil.getDataListAsStringArray("noteblockinstruments"));
		block.offsetType = blockBase == null ? getRandomString(random, Arrays.asList("NONE", "XZ", "XYZ")) : "NONE";
		block.aiPathNodeType = getRandomItem(random, ElementUtil.getDataListAsStringArray("pathnodetypes"));
		block.beaconColorModifier = emptyLists ? null : Color.cyan;
		block.unbreakable = _true;
		block.vanillaToolTier = getRandomString(random, Arrays.asList("NONE", "STONE", "IRON", "DIAMOND"));
		block.tickRandomly = _true;
		block.guiBoundTo = emptyLists || guis.isEmpty() ? null : getRandomItem(random, guis);
		block.openGUIOnRightClick = random.nextBoolean();
		block.inventorySize = getRandomInt(random, Block.class, "inventorySize");
		block.inventoryAutomationTakeCondition = random.nextBoolean() ? null : new Procedure("condition1");
		block.inventoryAutomationPlaceCondition = random.nextBoolean() ? null : new Procedure("condition2");
		block.inventoryStackSize = getRandomInt(random, Block.class, "inventoryStackSize");
		block.inventoryDropWhenDestroyed = random.nextBoolean();
		block.inventoryComparatorPower = random.nextBoolean();
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
		block.sensitiveToVibration = _true;
		block.vibrationalEvents = new ArrayList<>();
		if (!emptyLists) {
			block.vibrationalEvents.addAll(ElementUtil.loadAllGameEvents().stream()
					.map(e -> new GameEventEntry(modElement.getWorkspace(), e.getName())).toList());
			block.vibrationalEvents.add(new GameEventEntry(modElement.getWorkspace(), "#allay_can_listen"));
		}
		block.vibrationSensitivityRadius = new NumberProcedure(emptyLists ? null : "number1", 11);
		block.canReceiveVibrationCondition = new Procedure("condition1");
		block.onReceivedVibration = new Procedure("procedure1");
		block.hasEnergyStorage = _true;
		block.energyCapacity = getRandomInt(random, Block.class, "energyCapacity");
		block.energyInitial = getRandomInt(random, Block.class, "energyInitial");
		block.energyMaxExtract = getRandomInt(random, Block.class, "energyMaxExtract");
		block.energyMaxReceive = getRandomInt(random, Block.class, "energyMaxReceive");
		block.isFluidTank = !_true;
		block.fluidCapacity = getRandomInt(random, Block.class, "fluidCapacity");
		block.fluidRestrictions = new ArrayList<>();
		if (!emptyLists) {
			block.fluidRestrictions.addAll(ElementUtil.loadAllFluids(modElement.getWorkspace()).stream()
					.map(e -> new net.mcreator.element.parts.Fluid(modElement.getWorkspace(), e.getName())).toList());
		}
		block.restrictionBiomes = new ArrayList<>();
		if (!emptyLists) {
			if (_true) {
				block.restrictionBiomes = subset(random, 5, biomes,
						e -> new BiomeEntry(modElement.getWorkspace(), e.getName()));
			} else {
				block.restrictionBiomes.add(new BiomeEntry(modElement.getWorkspace(), "#is_overworld"));
			}
		}
		block.generateFeature = _true;
		block.blocksToReplace = new ArrayList<>();
		if (!emptyLists) {
			block.blocksToReplace = subset(random, blocksAndTags.size() / 8, blocksAndTags,
					e -> new MItemBlock(modElement.getWorkspace(), e.getName()));
			block.blocksToReplace.add(new MItemBlock(modElement.getWorkspace(), "TAG:walls"));
		}
		block.generationShape = _true ? "UNIFORM" : "TRIANGLE";
		block.frequencyPerChunks = getRandomInt(random, Block.class, "frequencyPerChunks");
		block.frequencyOnChunk = getRandomInt(random, Block.class, "frequencyOnChunk");
		Range<Integer> generateHeight = getRandomIntRange(random, Block.class, "minGenerateHeight", "maxGenerateHeight");
		block.minGenerateHeight = generateHeight.getMinimum();
		block.maxGenerateHeight = generateHeight.getMaximum();
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
			block.onEntityFallsOn = new Procedure("procedure14");
			block.onHitByProjectile = new Procedure("procedure15");
			block.placingCondition = new Procedure("condition2");
			block.additionalHarvestCondition = new Procedure("condition1");
			block.isBonemealTargetCondition = new Procedure("condition3");
			block.bonemealSuccessCondition = new Procedure("condition4");
			block.onBonemealSuccess = new Procedure("procedure15");
		}
		block.itemTexture = new TextureHolder(modElement.getWorkspace(), emptyLists ? "" : "itest");
		block.particleTexture = new TextureHolder(modElement.getWorkspace(), emptyLists ? "" : "test7");

		// Set some block base properties
		if ("Leaves".equals(blockBase)) {
			block.leavesParticleType = emptyLists ?
					null :
					new Particle(modElement.getWorkspace(),
							getRandomDataListEntry(random, ElementUtil.loadAllParticles(modElement.getWorkspace())));
			block.leavesParticleChance = getRandomDouble(random, Block.class, "leavesParticleChance");
		} else if ("FlowerPot".equals(blockBase)) {
			block.pottedPlant = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksWithItemForm).getName());
		} else if ("Sign".equals(blockBase)) {
			block.signEntityTexture = new TextureHolder(modElement.getWorkspace(),
					emptyLists ? "" : "entity_texture_0");
		} else if ("HangingSign".equals(blockBase)) {
			block.signEntityTexture = new TextureHolder(modElement.getWorkspace(),
					emptyLists ? "" : "entity_texture_0");
			block.signGUITexture = new TextureHolder(modElement.getWorkspace(), emptyLists ? "" : "picture1");
		}

		block.texture = new TextureHolder(modElement.getWorkspace(), "test");
		block.textureTop = new TextureHolder(modElement.getWorkspace(), "test2");
		block.textureLeft = new TextureHolder(modElement.getWorkspace(), "test3");
		block.textureFront = new TextureHolder(modElement.getWorkspace(), "test4");
		block.textureRight = new TextureHolder(modElement.getWorkspace(), "test5");
		block.textureBack = new TextureHolder(modElement.getWorkspace(), "test6");
		block.specialInformation = new StringListProcedure(emptyLists ? null : "string1",
				Arrays.asList("info 1", "info 2", "test, is this", "another one"));
		block.tintType = getRandomString(random,
				Arrays.asList("No tint", "Grass", "Foliage", "Birch foliage", "Spruce foliage", "Default foliage",
						"Water", "Sky", "Fog", "Water fog"));
		block.isItemTinted = _true;
		block.renderType = emptyLists ?
				new int[] { 10, block.isBlockTinted() ? 110 : 11, block.isBlockTinted() ? 120 : 12, 14 }[valueIndex] :
				4;
		block.customModelName = emptyLists ?
				new String[] { "Normal", "Single texture", "Cross model", "Grass block" }[valueIndex] :
				"ModelCustomJavaModel";
		block.lightOpacity = block.renderType == 4 ? 0 : new int[] { 0, 2, 0, 3 }[valueIndex];
		block.hasInventory = _true || block.renderType == 4; // Java models require tile entity
		block.hasTransparency = block.renderType == 4 || new boolean[] { _true, _true, true,
				false }[valueIndex]; // third is true because third index for model is cross which requires transparency
		block.hasCustomOpacity =
				block.hasTransparency || valueIndex == 3; // Test custom opacity with non-transparent block
		block.states = new ArrayList<>();
		if (!emptyLists) {
			int size2 = random.nextInt(4) + 1;

			List<PropertyDataWithValue<?>> stateProperties = new ArrayList<>();
			for (PropertyDataWithValue<?> property : block.customProperties) {
				if (random.nextBoolean()) {
					stateProperties.add(property);
				}
			}

			for (int i = 0; i < size2; i++) {
				StateMap stateMap = new StateMap();
				for (PropertyDataWithValue<?> property : stateProperties) {
					if (property.property() instanceof PropertyData.IntegerType) {
						stateMap.put(property.property(), random.nextInt(1, 10));
					} else if (property.property() instanceof PropertyData.LogicType) {
						stateMap.put(property.property(), random.nextBoolean());
					}
				}

				Block.StateEntry stateEntry = new Block.StateEntry();
				stateEntry.setWorkspace(modElement.getWorkspace());
				stateEntry.stateMap = stateMap;

				stateEntry.customModelName = "Normal";
				stateEntry.renderType = 10;

				stateEntry.texture = new TextureHolder(modElement.getWorkspace(), i == 0 ? "test" : "test" + i);
				stateEntry.textureTop = new TextureHolder(modElement.getWorkspace(), i == 0 ? "test" : "test" + i);
				stateEntry.textureBack = new TextureHolder(modElement.getWorkspace(), i == 0 ? "test" : "test" + i);
				stateEntry.textureLeft = new TextureHolder(modElement.getWorkspace(), i == 0 ? "test" : "test" + i);
				stateEntry.textureFront = new TextureHolder(modElement.getWorkspace(), i == 0 ? "test" : "test" + i);
				stateEntry.textureRight = new TextureHolder(modElement.getWorkspace(), i == 0 ? "test" : "test" + i);

				stateEntry.particleTexture = new TextureHolder(modElement.getWorkspace(),
						random.nextBoolean() ? null : "test3");

				stateEntry.hasCustomBoundingBox = _true;
				stateEntry.boundingBoxes = new ArrayList<>();
				if (stateEntry.hasCustomBoundingBox) {
					int boxes = random.nextInt(4) + 1;
					for (int i2 = 0; i2 < boxes; i2++) {
						IBlockWithBoundingBox.BoxEntry box = new IBlockWithBoundingBox.BoxEntry();
						box.mx = new double[] { 0, 5 + i2, 1.2, 7.1 }[valueIndex];
						box.my = new double[] { 0, 2, 3.6, 12.2 }[valueIndex];
						box.mz = new double[] { 0, 3.1, 0, 2.2 }[valueIndex];
						box.Mx = new double[] { 16, 15.2, 4, 7.1 + i2 }[valueIndex];
						box.My = new double[] { 16, 12.2, 16, 13 }[valueIndex];
						box.Mz = new double[] { 16, 12, 2.4, 1.2 }[valueIndex];
						box.subtract = random.nextBoolean();

						stateEntry.boundingBoxes.add(box);
					}
				}

				block.states.add(stateEntry);
			}
		}
		return block;
	}

	private static GeneratableElement getToolExample(ModElement modElement, String toolType, Random random,
			boolean _true, boolean emptyLists) {
		Tool tool = new Tool(modElement);
		tool.name = modElement.getName();
		tool.rarity = getRandomString(random, Arrays.asList("COMMON", "UNCOMMON", "RARE", "EPIC"));
		tool.creativeTabs = emptyLists ?
				List.of() :
				ElementUtil.loadAllTabs(modElement.getWorkspace()).stream()
				.map(e -> new TabEntry(modElement.getWorkspace(), e)).toList();
		tool.toolType = toolType;
		tool.blockDropsTier = getRandomString(random,
				Arrays.asList("WOOD", "STONE", "IRON", "DIAMOND", "GOLD", "NETHERITE"));
		tool.additionalDropCondition = new Procedure("condition3");
		tool.efficiency = getRandomDouble(random, Tool.class, "efficiency");
		tool.attackSpeed = getRandomDouble(random, Tool.class, "attackSpeed");
		tool.enchantability = getRandomInt(random, Tool.class, "enchantability");
		tool.damageVsEntity = getRandomDouble(random, Tool.class, "damageVsEntity");
		tool.usageCount = getRandomInt(random, Tool.class, "usageCount");
		tool.stayInGridWhenCrafting = _true;
		tool.damageOnCrafting = emptyLists;
		tool.immuneToFire = _true;
		tool.blocksAffected = new ArrayList<>();
		tool.glowCondition = new LogicProcedure(emptyLists ? "condition2" : null, _true);
		tool.specialInformation = new StringListProcedure(emptyLists ? null : "string1",
				Arrays.asList("info 1", "info 2", "test, is this", "another one"));
		if (!emptyLists && "Special".equals(toolType)) {
			List<MCItem> blocksAndTags = ElementUtil.loadBlocksAndTags(modElement.getWorkspace());
			tool.blocksAffected.addAll(
					blocksAndTags.stream().map(e -> new MItemBlock(modElement.getWorkspace(), e.getName())).toList());
			tool.blocksAffected.add(new MItemBlock(modElement.getWorkspace(), "TAG:walls"));
			tool.blocksAffected.add(new MItemBlock(modElement.getWorkspace(), "TAG:dirt"));
		}
		tool.repairItems = new ArrayList<>();
		if (!emptyLists) {
			List<MCItem> blocksAndItemsAndTags = ElementUtil.loadBlocksAndItemsAndTags(modElement.getWorkspace());
			tool.repairItems = subset(random, blocksAndItemsAndTags.size() / 8, blocksAndItemsAndTags,
					e -> new MItemBlock(modElement.getWorkspace(), e.getName()));
			tool.repairItems.add(new MItemBlock(modElement.getWorkspace(), "TAG:walls"));
		}
		tool.attributeModifiers = new ArrayList<>();
		if (!emptyLists) {
			for (DataListEntry attribute : ElementUtil.loadAllAttributes(modElement.getWorkspace())) {
				AttributeModifierEntry entry = new AttributeModifierEntry();
				entry.equipmentSlot = getRandomItem(random, ElementUtil.getDataListAsStringArray("equipmentslots"));
				entry.attribute = new AttributeEntry(modElement.getWorkspace(), attribute);
				entry.amount = getRandomDouble(random, AttributeModifierEntry.class, "amount");
				entry.operation = getRandomItem(random,
						new String[] { "ADD_VALUE", "ADD_MULTIPLIED_BASE", "ADD_MULTIPLIED_TOTAL" });
				tool.attributeModifiers.add(entry);
			}
		}
		tool.onRightClickedInAir = new Procedure("procedure1");
		tool.onRightClickedOnBlock = emptyLists ? new Procedure("actionresulttype1") : new Procedure("procedure2");
		tool.onCrafted = new Procedure("procedure3");
		tool.onBlockDestroyedWithTool = new Procedure("procedure4");
		tool.onEntityHitWith = new Procedure("procedure5");
		tool.onItemInInventoryTick = new Procedure("procedure6");
		tool.onItemInUseTick = new Procedure("procedure7");
		tool.onEntitySwing = new Procedure("procedure11");
		tool.onDroppedByPlayer = new Procedure("procedure8");
		tool.onItemEntityDestroyed = new Procedure("procedure9");
		tool.texture = new TextureHolder(modElement.getWorkspace(), "test");
		tool.guiTexture = new TextureHolder(modElement.getWorkspace(), emptyLists ? "" : "test3");
		tool.renderType = 0;
		tool.customModelName = "Normal";
		tool.blockingRenderType = 0;
		tool.blockingModelName = "Normal blocking";
		return tool;
	}

	private static GeneratableElement getRecipeExample(ModElement modElement, String recipeType, Random random,
			boolean _true) {
		var blocksAndItemsAndTags = ElementUtil.loadBlocksAndItemsAndTags(modElement.getWorkspace());
		Recipe recipe = new Recipe(modElement);
		recipe.group = modElement.getName().toLowerCase(Locale.ENGLISH);
		recipe.cookingBookCategory = getRandomItem(random, new String[] { "MISC", "FOOD", "BLOCKS" });
		recipe.craftingBookCategory = getRandomItem(random,
				new String[] { "MISC", "BUILDING", "REDSTONE", "EQUIPMENT" });
		recipe.recipeType = recipeType;

		List<MCItem> blocksAndItemsAndTagsNoAir = filterAir(blocksAndItemsAndTags);
		List<MCItem> blocksAndItemsNoAir = filterAir(ElementUtil.loadBlocksAndItems(modElement.getWorkspace()));

		switch (recipe.recipeType) {
		case "Crafting" -> {
			MItemBlock[] recipeSlots = new MItemBlock[9];
			Arrays.fill(recipeSlots, new MItemBlock(modElement.getWorkspace(), ""));
			recipeSlots[0] = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItemsAndTagsNoAir).getName());
			if (random.nextBoolean())
				recipeSlots[3] = new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, blocksAndItemsAndTagsNoAir).getName());
			if (random.nextBoolean())
				recipeSlots[6] = new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, blocksAndItemsAndTagsNoAir).getName());
			if (random.nextBoolean())
				recipeSlots[1] = new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, blocksAndItemsAndTagsNoAir).getName());
			if (random.nextBoolean())
				recipeSlots[4] = new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, blocksAndItemsAndTagsNoAir).getName());
			if (random.nextBoolean())
				recipeSlots[7] = new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, blocksAndItemsAndTagsNoAir).getName());
			if (random.nextBoolean())
				recipeSlots[2] = new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, blocksAndItemsAndTagsNoAir).getName());
			if (random.nextBoolean())
				recipeSlots[5] = new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, blocksAndItemsAndTagsNoAir).getName());
			if (random.nextBoolean())
				recipeSlots[8] = new MItemBlock(modElement.getWorkspace(),
						getRandomMCItem(random, blocksAndItemsAndTagsNoAir).getName());
			recipe.recipeRetstackSize = getRandomInt(random, Recipe.class, "recipeRetstackSize");
			recipe.recipeShapeless = _true;
			recipe.recipeReturnStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItemsNoAir).getName());
			recipe.recipeSlots = recipeSlots;

			recipe.unlockingItems = subset(random, 4, blocksAndItemsAndTags,
					e -> new MItemBlock(modElement.getWorkspace(), e.getName()));
		}
		case "Smelting" -> {
			recipe.smeltingInputStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItemsAndTagsNoAir).getName());
			recipe.smeltingReturnStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItemsNoAir).getName());
			recipe.xpReward = getRandomDouble(random, Recipe.class, "xpReward");
			recipe.cookingTime = getRandomInt(random, Recipe.class, "cookingTime");
			recipe.unlockingItems = List.of(recipe.smeltingInputStack);
		}
		case "Smoking" -> {
			recipe.smokingInputStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItemsAndTagsNoAir).getName());
			recipe.smokingReturnStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItemsNoAir).getName());
			recipe.xpReward = getRandomDouble(random, Recipe.class, "xpReward");
			recipe.cookingTime = getRandomInt(random, Recipe.class, "cookingTime");
			recipe.unlockingItems = List.of(recipe.smokingInputStack);
		}
		case "Blasting" -> {
			recipe.blastingInputStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItemsAndTagsNoAir).getName());
			recipe.blastingReturnStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItemsNoAir).getName());
			recipe.xpReward = getRandomDouble(random, Recipe.class, "xpReward");
			recipe.cookingTime = getRandomInt(random, Recipe.class, "cookingTime");
			recipe.unlockingItems = List.of(recipe.blastingInputStack);
		}
		case "Stone cutting" -> {
			recipe.stoneCuttingInputStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItemsAndTagsNoAir).getName());
			recipe.stoneCuttingReturnStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItemsNoAir).getName());
			recipe.recipeRetstackSize = getRandomInt(random, Recipe.class, "recipeRetstackSize");
			recipe.unlockingItems = List.of(recipe.stoneCuttingInputStack);
		}
		case "Campfire cooking" -> {
			recipe.campfireCookingInputStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItemsAndTagsNoAir).getName());
			recipe.campfireCookingReturnStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItemsNoAir).getName());
			recipe.xpReward = getRandomDouble(random, Recipe.class, "xpReward");
			recipe.cookingTime = getRandomInt(random, Recipe.class, "cookingTime");
			recipe.unlockingItems = List.of(recipe.campfireCookingInputStack);
		}
		case "Smithing" -> {
			recipe.smithingInputStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItemsAndTagsNoAir).getName());
			recipe.smithingInputAdditionStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItemsAndTagsNoAir).getName());
			recipe.smithingInputTemplateStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItemsAndTagsNoAir).getName());
			recipe.smithingReturnStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItemsNoAir).getName());

			recipe.unlockingItems = subset(random, 4, blocksAndItemsAndTags,
					e -> new MItemBlock(modElement.getWorkspace(), e.getName()));
		}
		case "Brewing" -> {
			recipe.brewingInputStack = new MItemBlock(modElement.getWorkspace(), getRandomMCItem(random,
					filterAir(ElementUtil.loadBlocksAndItemsAndTagsAndPotions(modElement.getWorkspace()))).getName());
			recipe.brewingIngredientStack = new MItemBlock(modElement.getWorkspace(),
					getRandomMCItem(random, blocksAndItemsAndTagsNoAir).getName());
			recipe.brewingReturnStack = new MItemBlock(modElement.getWorkspace(), getRandomMCItem(random,
					filterAir(ElementUtil.loadBlocksAndItemsAndPotions(modElement.getWorkspace()))).getName());
		}
		default -> throw new RuntimeException("Unknown recipe type");
		}
		return recipe;
	}

	public static Achievement getAdvancementExample(ModElement modElement, Random random, boolean _true,
			boolean emptyLists, List<MCItem> blocksAndItems) {
		Achievement achievement = new Achievement(modElement);
		achievement.achievementName = "Test Achievement";
		achievement.achievementDescription = "Description of it";
		achievement.achievementIcon = new MItemBlock(modElement.getWorkspace(),
				getRandomMCItem(random, blocksAndItems).getName());
		achievement.achievementType = ListUtils.getRandomItem(random,
				List.of("task", "goal", "challenge", "challenge"));
		achievement.parent = new AchievementEntry(modElement.getWorkspace(),
				getRandomDataListEntry(random, ElementUtil.loadAllAchievements(modElement.getWorkspace())));
		achievement.announceToChat = _true;
		achievement.showPopup = _true;
		achievement.disableDisplay = !_true;
		achievement.rewardXP = getRandomInt(random, Achievement.class, "rewardXP");
		achievement.hideIfNotCompleted = !_true;
		var functions = modElement.getWorkspace().getModElements().stream()
				.filter(var -> var.getType() == ModElementType.FUNCTION).map(ModElement::getName)
				.collect(Collectors.toList());
		achievement.rewardFunction = emptyLists || functions.isEmpty() ? null : getRandomItem(random, functions);
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
		achievement.triggerxml = AnnotationUtils.getBlocklyXMLDefaultValue(achievement.getClass(), "triggerxml");
		;

		return achievement;
	}

	public static SpecialEntity getSpecialEntityExample(ModElement modElement, String entityType, boolean emptyLists) {
		SpecialEntity specialEntity = new SpecialEntity(modElement);
		specialEntity.name = modElement.getName();
		specialEntity.entityType = entityType;
		specialEntity.entityTexture = new TextureHolder(modElement.getWorkspace(), "entity_texture_0");
		specialEntity.itemTexture = new TextureHolder(modElement.getWorkspace(), "itest");
		specialEntity.creativeTabs = emptyLists ?
				List.of() :
				ElementUtil.loadAllTabs(modElement.getWorkspace()).stream()
				.map(e -> new TabEntry(modElement.getWorkspace(), e)).toList();

		return specialEntity;
	}

	public static <T> T getRandomItem(Random random, T[] list) {
		int listSize = list.length;
		int randomIndex = random.nextInt(listSize);
		return list[randomIndex];
	}

	public static <T> T getRandomItem(Random random, List<T> list) {
		int listSize = list.size();
		int randomIndex = random.nextInt(listSize);
		return list.get(randomIndex);
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

	private static double getRandomDouble(Random random, double min, double max) {
		double r = random.nextDouble();
		return min * (1 - r) + max * r;
	}

	private static int getRandomInt(Random random, Class<?> type, String field) {
		NumericParameter annotation = AnnotationUtils.getAnnotation(type, field, NumericParameter.class);
		return random.nextInt((int) annotation.min(), (int) (annotation.max() + 1));
	}

	private static double getRandomDouble(Random random, Class<?> type, String field) {
		NumericParameter annotation = AnnotationUtils.getAnnotation(type, field, NumericParameter.class);
		return getRandomDouble(random, annotation.min(), annotation.max());
	}

	private static Range<Integer> getRandomIntRange(Random random, Class<?> type, String minField, String maxField) {
		int v1 = getRandomInt(random, type, minField);
		int v2 = getRandomInt(random, type, maxField);
		return Range.of(Math.min(v1, v2), Math.max(v1, v2));
	}

	private static Range<Double> getRandomDoubleRange(Random random, Class<?> type, String minField, String maxField) {
		double v1 = getRandomDouble(random, type, minField);
		double v2 = getRandomDouble(random, type, maxField);
		return Range.of(Math.min(v1, v2), Math.max(v1, v2));
	}

	public static void provideAndGenerateSampleElements(Random random, Workspace workspace) {
		// add sample procedures (used by test mod elements) if supported
		if (workspace.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.PROCEDURE)
				!= GeneratorStats.CoverageStatus.NONE) {
			for (int i = 1; i <= 15; i++) {
				ModElement me = new ModElement(workspace, "procedure" + i, ModElementType.PROCEDURE);
				if (i == 1) {
					me.putMetadata("dependencies", Arrays.asList(
							Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity")));
				} else {
					me.putMetadata("dependencies",
							Arrays.asList(Dependency.fromString("x:number/y:number/z:number/world:world")));
				}

				net.mcreator.element.types.Procedure procedure = new net.mcreator.element.types.Procedure(me);
				procedure.skipDependencyRegeneration();
				procedure.procedurexml = GTProcedureBlocks.wrapWithBaseTestXML("");

				addGeneratableElementAndAssert(workspace, procedure);
			}

			for (int i = 1; i <= 4; i++) {
				ModElement me = new ModElement(workspace, "condition" + i, ModElementType.PROCEDURE);
				me.putMetadata("return_type", "LOGIC");
				me.putMetadata("dependencies",
						Arrays.asList(Dependency.fromString("x:number/y:number/z:number/world:world")));

				net.mcreator.element.types.Procedure procedure = new net.mcreator.element.types.Procedure(me);
				procedure.procedurexml = GTProcedureBlocks.wrapWithBaseTestXML(
						"<block type=\"return_logic\"><value name=\"return\">"
								+ "<block type=\"logic_boolean\"><field name=\"BOOL\">FALSE</field></block>"
								+ "</value></block>");

				addGeneratableElementAndAssert(workspace, procedure);
			}

			for (int i = 1; i <= 3; i++) {
				ModElement me = new ModElement(workspace, "number" + i, ModElementType.PROCEDURE);
				me.putMetadata("return_type", "NUMBER");
				if (i == 3) {
					me.putMetadata("dependencies", Collections.emptyList());
				} else {
					me.putMetadata("dependencies",
							Arrays.asList(Dependency.fromString("x:number/y:number/z:number/world:world")));
				}

				net.mcreator.element.types.Procedure procedure = new net.mcreator.element.types.Procedure(me);
				procedure.skipDependencyRegeneration();
				procedure.procedurexml = GTProcedureBlocks.wrapWithBaseTestXML(
						"<block type=\"return_number\"><value name=\"return\">"
								+ "<block type=\"math_number\"><field name=\"NUM\">100</field></block>"
								+ "</value></block>");

				addGeneratableElementAndAssert(workspace, procedure);
			}

			for (int i = 1; i <= 4; i++) {
				ModElement me = new ModElement(workspace, "string" + i, ModElementType.PROCEDURE);
				me.putMetadata("return_type", "STRING");
				me.putMetadata("dependencies",
						Arrays.asList(Dependency.fromString("x:number/y:number/z:number/world:world")));

				net.mcreator.element.types.Procedure procedure = new net.mcreator.element.types.Procedure(me);
				procedure.skipDependencyRegeneration();
				procedure.procedurexml = GTProcedureBlocks.wrapWithBaseTestXML(
						"<block type=\"return_string\"><value name=\"return\">"
								+ "<block type=\"text\"><field name=\"TEXT\">demo text</field></block>"
								+ "</value></block>");

				addGeneratableElementAndAssert(workspace, procedure);
			}

			for (int i = 1; i <= 2; i++) {
				ModElement me = new ModElement(workspace, "itemstack" + i, ModElementType.PROCEDURE);
				me.putMetadata("return_type", "ITEMSTACK");
				me.putMetadata("dependencies",
						Arrays.asList(Dependency.fromString("x:number/y:number/z:number/world:world")));

				net.mcreator.element.types.Procedure procedure = new net.mcreator.element.types.Procedure(me);
				procedure.skipDependencyRegeneration();
				procedure.procedurexml = GTProcedureBlocks.wrapWithBaseTestXML(
						"<block type=\"return_itemstack\"><value name=\"return\">"
								+ "<block type=\"empty_itemstack\"></block></value></block>");

				addGeneratableElementAndAssert(workspace, procedure);
			}

			for (int i = 1; i <= 2; i++) {
				ModElement me = new ModElement(workspace, "actionresulttype" + i, ModElementType.PROCEDURE);
				me.putMetadata("return_type", "ACTIONRESULTTYPE");
				me.putMetadata("dependencies",
						Arrays.asList(Dependency.fromString("x:number/y:number/z:number/world:world")));

				net.mcreator.element.types.Procedure procedure = new net.mcreator.element.types.Procedure(me);
				procedure.skipDependencyRegeneration();
				procedure.procedurexml = GTProcedureBlocks.wrapWithBaseTestXML(
						"<block type=\"return_actionresulttype\"><value name=\"return\">"
								+ "<block type=\"action_result_type\"><field name=\"type\">SUCCESS</field></block>"
								+ "</value></block>");

				addGeneratableElementAndAssert(workspace, procedure);
			}

			for (int i = 1; i <= 2; i++) {
				ModElement me = new ModElement(workspace, "entity" + i, ModElementType.PROCEDURE);
				me.putMetadata("return_type", "ENTITY");
				me.putMetadata("dependencies",
						Arrays.asList(Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity")));

				net.mcreator.element.types.Procedure procedure = new net.mcreator.element.types.Procedure(me);
				procedure.skipDependencyRegeneration();
				procedure.procedurexml = GTProcedureBlocks.wrapWithBaseTestXML(
						"<block type=\"return_entity\"><value name=\"return\">"
								+ "<block type=\"entity_from_deps\"></block></value></block>");

				addGeneratableElementAndAssert(workspace, procedure);
			}

			for (int i = 1; i <= 2; i++) {
				ModElement me = new ModElement(workspace, "vector" + i, ModElementType.PROCEDURE);
				me.putMetadata("return_type", "VECTOR");
				me.putMetadata("dependencies", Dependency.fromString(
						"x:number/y:number/z:number/world:world/speedX:number/speedY:number/speedZ:number/angularVelocity:number/angularAcceleration:number/age:number"));

				net.mcreator.element.types.Procedure procedure = new net.mcreator.element.types.Procedure(me);

				procedure.procedurexml = GTProcedureBlocks.wrapWithBaseTestXML(
						"<block type=\"return_vector\"><value name=\"return\"><block type=\"vector_new_vector\">"
								+ "<value name=\"x\"><block type=\"math_number\"><field name=\"NUM\">1</field></block></value>"
								+ "<value name=\"y\"><block type=\"math_number\"><field name=\"NUM\">2</field></block></value>"
								+ "<value name=\"z\"><block type=\"math_number\"><field name=\"NUM\">3</field></block></value>"
								+ "</block></value></block>");

				addGeneratableElementAndAssert(workspace, procedure);
			}
		}

		// add sample recipes (used by test mod elements) if supported
		if (workspace.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.RECIPE)
				!= GeneratorStats.CoverageStatus.NONE) {
			for (int i = 1; i <= 2; i++) {
				ModElement me = new ModElement(workspace, "ExampleRecipe" + i, ModElementType.RECIPE);

				Recipe recipe = new Recipe(me);
				recipe.recipeType = "smelting";
				recipe.smeltingInputStack = new MItemBlock(workspace,
						getRandomMCItem(random, ElementUtil.loadBlocksAndItems(workspace)).getName());
				recipe.smeltingReturnStack = new MItemBlock(workspace,
						getRandomMCItem(random, ElementUtil.loadBlocksAndItems(workspace)).getName());
				recipe.name = me.getRegistryName();
				recipe.namespace = "mod";

				addGeneratableElementAndAssert(workspace, recipe);
			}
		}

		// add sample functions (used by test mod elements) if supported
		if (workspace.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.FUNCTION)
				!= GeneratorStats.CoverageStatus.NONE) {
			for (int i = 1; i <= 2; i++) {
				ModElement me = new ModElement(workspace, "ExampleFunction" + i, ModElementType.FUNCTION);

				Function function = new Function(me);
				function.code = "";
				function.name = me.getRegistryName();
				function.namespace = "mod";

				addGeneratableElementAndAssert(workspace, function);
			}
		}

		// add sample loot tables (used by test mod elements) if supported
		if (workspace.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.LOOTTABLE)
				!= GeneratorStats.CoverageStatus.NONE) {
			for (int i = 1; i <= 2; i++) {
				ModElement me = new ModElement(workspace, "ExampleLootTable" + i, ModElementType.LOOTTABLE);

				LootTable lootTable = new LootTable(me);
				lootTable.type = "Generic";
				lootTable.name = me.getRegistryName();
				lootTable.namespace = "mod";
				lootTable.pools = Collections.emptyList();

				addGeneratableElementAndAssert(workspace, lootTable);
			}
		}
	}

	public static Workspace createTestWorkspace(File directory, GeneratorConfiguration generatorConfiguration,
			boolean fillWithResourcesAndData, boolean provideAndGenerateSampleElements, @Nullable Random random) {
		WorkspaceSettings workspaceSettings = new WorkspaceSettings("test_mod");
		workspaceSettings.setVersion("1.0.0");
		workspaceSettings.setDescription("Test mod");
		workspaceSettings.setAuthor("Unit tests");
		workspaceSettings.setLicense("GPL 3.0");
		workspaceSettings.setWebsiteURL("https://mcreator.net/");
		workspaceSettings.setUpdateURL("");
		workspaceSettings.setModPicture("example");
		workspaceSettings.setModName("Test mod");
		workspaceSettings.setCurrentGenerator(generatorConfiguration.getGeneratorName());

		Workspace workspace = Workspace.createWorkspace(new File(directory, "test_mod.mcreator"), workspaceSettings);

		if (fillWithResourcesAndData)
			fillWorkspaceWithResourcesAndData(workspace);

		if (provideAndGenerateSampleElements)
			provideAndGenerateSampleElements(random != null ? random : new Random(), workspace);

		return workspace;
	}

	private static ModElement me(Workspace workspace, ModElementType<?> type, String suffix) {
		return new ModElement(workspace, "Example" + type.getRegistryName() + suffix, type);
	}

	private static void addGeneratableElementAndAssert(Workspace workspace, GeneratableElement generatableElement) {
		workspace.addModElement(generatableElement.getModElement());
		assertTrue(workspace.getGenerator().generateElement(generatableElement));
		workspace.getModElementManager().storeModElement(generatableElement);
	}

	public static <T extends MappableElement> List<T> subset(Random random, int n,
			Collection<? extends DataListEntry> all, java.util.function.Function<DataListEntry, T> mapper) {
		List<DataListEntry> pool = new ArrayList<>(all);

		DataListEntry chosenCustom = pool.stream().filter(e -> {
			String v = e.getName();
			return v != null && v.startsWith(NameMapper.MCREATOR_PREFIX);
		}).findAny().orElse(null);

		List<T> result = new ArrayList<>(n);

		if (chosenCustom != null) {
			result.add(mapper.apply(chosenCustom));
			pool.remove(chosenCustom);
			n--;
		}

		Collections.shuffle(pool, random);
		result.addAll(pool.subList(0, n).stream().map(mapper).toList());

		return result;
	}

}
