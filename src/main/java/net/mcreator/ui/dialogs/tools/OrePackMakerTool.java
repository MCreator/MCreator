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

package net.mcreator.ui.dialogs.tools;

import net.mcreator.element.ModElementType;
import net.mcreator.element.parts.*;
import net.mcreator.element.types.Block;
import net.mcreator.element.types.Item;
import net.mcreator.element.types.Recipe;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.io.FileIO;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.BasicAction;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.ModElementNameValidator;
import net.mcreator.ui.variants.modmaker.ModMaker;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

public class OrePackMakerTool extends AbstractPackMakerTool {

	private final VTextField name = new VTextField(25);
	private final JColor color;
	private final JSpinner power = new JSpinner(new SpinnerNumberModel(1, 0.1, 10, 0.1));
	private final JComboBox<String> type = new JComboBox<>(new String[] { "Gem based", "Ingot based" });

	private OrePackMakerTool(MCreator mcreator) {
		super(mcreator, "ore_pack", UIRES.get("16px.orepack").getImage());
		JPanel props = new JPanel(new GridLayout(4, 2, 5, 2));

		color = new JColor(mcreator, false, false);

		color.setColor(Theme.current().getInterfaceAccentColor());
		name.enableRealtimeValidation();

		props.add(L10N.label("dialog.tools.ore_pack_name"));
		props.add(name);

		props.add(L10N.label("dialog.tools.ore_pack_type"));
		props.add(type);

		props.add(L10N.label("dialog.tools.ore_pack_color_accent"));
		props.add(color);

		props.add(L10N.label("dialog.tools.ore_pack_power_factor"));
		props.add(power);

		name.setValidator(new ModElementNameValidator(mcreator.getWorkspace(), name,
				L10N.t("dialog.tools.ore_pack_name_validator")));

		validableElements.addValidationElement(name);

		this.add("Center", PanelUtils.centerInPanel(props));

		this.setSize(600, 280);
		this.setLocationRelativeTo(mcreator);
		this.setVisible(true);
	}

	@Override protected void generatePack(MCreator mcreator) {
		addOrePackToWorkspace(mcreator, mcreator.getWorkspace(), name.getText(),
				(String) Objects.requireNonNull(type.getSelectedItem()), color.getColor(), (Double) power.getValue());
	}

	static MItemBlock addOrePackToWorkspace(MCreator mcreator, Workspace workspace, String name, String type,
			Color color, double factor) {
		String oreItemName;
		if (type.equals("Gem based")) {
			oreItemName = name;
		} else {
			oreItemName = name + "Ingot";
		} 

		if (type.equals("Ingot based")) {
			if (!checkIfNamesAvailable(workspace, oreItemName, name + "Ore", name + "Block",
					name + "OreBlockRecipe", name + "BlockOreRecipe", name + "OreSmelting", name + "OreBlasting",
					"Raw" + name, "Raw" + name + "Block", name + "DeepslateOreSmelting", name + "DeepslateOreBlasting",
					name + "DeepslateOre", name + "RawBlockRecipe", name + "BlockRawRecipe", name + "RawOreSmelting", name + "RawOreBlasting"))
				return null;
		} else {
			if (!checkIfNamesAvailable(workspace, oreItemName, name + "Ore", name + "Block",
					name + "OreBlockRecipe", name + "BlockOreRecipe", name + "OreSmelting", name + "OreBlasting",
					name + "DeepslateOreSmelting", name + "DeepslateOreBlasting", name + "DeepslateOre"))
				return null;
		}

		String registryName = RegistryNameFixer.fromCamelCase(name);
		String readableName = StringUtils.machineToReadableName(name);

		FolderElement folder = mcreator instanceof ModMaker modMaker ?
				modMaker.getWorkspacePanel().currentFolder :
				null;

		// Ore texture
		ImageIcon ore = ImageUtils.drawOver(getCachedTexture("noise2","noise5","noise16"),
				ImageUtils.colorize(getCachedTexture("ore1","ore2","ore3","ore4","ore5","ore6","ore7","ore8","ore9","ore10"), color, true));
		String oreTextureName = registryName + "_ore";
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(ore.getImage()),
				mcreator.getFolderManager().getTextureFile(oreTextureName, TextureType.BLOCK));

		// Deepslate Ore texture
		ImageIcon oreDeepslate = ImageUtils.drawOver(getCachedTexture("noise8","noise12","noise13","noise15","noise17"),
				ImageUtils.colorize(getCachedTexture("ore1","ore2","ore3","ore4","ore5","ore6","ore7","ore8","ore9","ore10"), color, true));
		String oreDeepslateTextureName = registryName + "_deepslate_ore";
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(oreDeepslate.getImage()),
				mcreator.getFolderManager().getTextureFile(oreDeepslateTextureName, TextureType.BLOCK));

		// Raw Block texture
		ImageIcon rawOreBlockIc = ImageUtils.colorize(
				getCachedTexture("new_cobblestone1"), color, true);
		String rawOreBlockTextureName = registryName + "_raw_ore_block";
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(rawOreBlockIc.getImage()), mcreator.getFolderManager()
				.getTextureFile(rawOreBlockTextureName, TextureType.BLOCK));

		// Ore block texture
		ImageIcon oreBlockIc = ImageUtils.colorize(
				getCachedTexture("oreblock1", "oreblock2", "oreblock3", "oreblock4", "oreblock5", "oreblock6",
						"oreblock7", "oreblock8"), color, true);
		String oreBlockTextureName = registryName + "_ore_block";
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(oreBlockIc.getImage()), mcreator.getFolderManager()
				.getTextureFile(oreBlockTextureName, TextureType.BLOCK));

		ImageIcon gem;
		ImageIcon raw = null;
		String gemTextureName;
		String rawTextureName = null;
		Item rawItem = null;
		if (type.equals("Gem based")) {
			gem = ImageUtils.colorize(getCachedTexture("gem4", "gem6", "gem7", "gem9", "gem13"), color, true);
			gemTextureName = registryName;
		} else {
			gem = ImageUtils.colorize(getCachedTexture("ingot_dark", "ingot_bright"), color, true);
			gemTextureName = registryName + "_ingot";
			// Need to change the texture for raw item as well
			raw = ImageUtils.colorize(getCachedTexture("raw"), color, true);
			rawTextureName = "raw_" + registryName;
		} 

		if (type.equals("Ingot based")) {
	
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(raw.getImage()),
					mcreator.getFolderManager().getTextureFile(rawTextureName, TextureType.ITEM));

			rawItem = (Item) ModElementType.ITEM.getModElementGUI(mcreator,
					new ModElement(workspace, "Raw" + name, ModElementType.ITEM), false).getElementFromGUI();
			rawItem.name = "Raw " + readableName;
			rawItem.texture = new TextureHolder(workspace, rawTextureName);
			rawItem.creativeTabs = List.of(new TabEntry(workspace, "MATERIALS"));
			addGeneratableElementToWorkspace(workspace, folder, rawItem);
		}

		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(gem.getImage()),
				mcreator.getFolderManager().getTextureFile(gemTextureName, TextureType.ITEM));

		Item oreItem = (Item) ModElementType.ITEM.getModElementGUI(mcreator,
				new ModElement(workspace, oreItemName, ModElementType.ITEM), false).getElementFromGUI();
		oreItem.name = readableName;
		oreItem.texture = new TextureHolder(workspace, gemTextureName);
		oreItem.creativeTabs = List.of(new TabEntry(workspace, "MATERIALS"));
		addGeneratableElementToWorkspace(workspace, folder, oreItem);

		// Ore Block
		Block oreBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Ore", ModElementType.BLOCK), false).getElementFromGUI();
		oreBlock.name = readableName + " Ore";
		oreBlock.texture = new TextureHolder(workspace, oreTextureName);
		oreBlock.renderType = 11; // single texture
		oreBlock.customModelName = "Single texture";
		oreBlock.soundOnStep = new StepSound(workspace, "STONE");
		oreBlock.hardness = 3.0 * factor;
		oreBlock.resistance = 3.0 * Math.pow(factor, 0.8);
		oreBlock.destroyTool = "pickaxe";
		if (factor < 1) {
			oreBlock.vanillaToolTier = "STONE";
		} else if (factor == 1) {
			oreBlock.vanillaToolTier = "IRON";
		} else {
			oreBlock.vanillaToolTier = "DIAMOND";
		}
		oreBlock.requiresCorrectTool = true;
		oreBlock.noteBlockInstrument = "basedrum";
		oreBlock.generateFeature = true;
		oreBlock.restrictionBiomes = List.of(new BiomeEntry(mcreator.getWorkspace(), "#is_overworld"));
		oreBlock.minGenerateHeight = 1;
		oreBlock.maxGenerateHeight = (int) (63 / Math.pow(factor, 0.9));
		oreBlock.frequencyPerChunks = (int) (11 / Math.pow(factor, 0.9));
		oreBlock.frequencyOnChunk = (int) (7 / Math.pow(factor, 0.9));
		oreBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		if (type.equals("Ingot based")) {
			// Ingot-based ores drop an amount based on factor (default 1 for normal ores)
			oreBlock.dropAmount = factor <= 1 ? 1 : 3;
			oreBlock.customDrop = new MItemBlock(workspace, "CUSTOM:" + "Raw" + name);
		} else {
			// Gem-based ores drop 1 of the gem item 
			oreBlock.dropAmount = 1;
			oreBlock.customDrop = new MItemBlock(workspace, "CUSTOM:" + oreItemName);
		}
		addGeneratableElementToWorkspace(workspace, folder, oreBlock);

		if (type.equals("Ingot based")) {

			// Raw Ore Block
			Block rawBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
					new ModElement(workspace, "Raw" + name + "Block", ModElementType.BLOCK), false).getElementFromGUI();
			rawBlock.name = "Raw " + readableName + " Block";
			rawBlock.texture = new TextureHolder(workspace, rawOreBlockTextureName);
			rawBlock.renderType = 11; // single texture
			rawBlock.customModelName = "Single texture";
			rawBlock.soundOnStep = new StepSound(workspace, "STONE");
			rawBlock.hardness = 3.0 * factor;
			rawBlock.resistance = 3.0 * Math.pow(factor, 0.8);
			rawBlock.destroyTool = "pickaxe";
			if (factor < 1) {
				rawBlock.vanillaToolTier = "STONE";
			} else if (factor == 1) {
				rawBlock.vanillaToolTier = "IRON";
			} else {
				rawBlock.vanillaToolTier = "DIAMOND";
			}
			rawBlock.requiresCorrectTool = true;
			rawBlock.noteBlockInstrument = "basedrum";
			rawBlock.generateFeature = true;
			rawBlock.restrictionBiomes = List.of(new BiomeEntry(mcreator.getWorkspace(), "#is_overworld"));
			rawBlock.minGenerateHeight = 1;
			rawBlock.maxGenerateHeight = (int) (63 / Math.pow(factor, 0.9));
			rawBlock.frequencyPerChunks = (int) (11 / Math.pow(factor, 0.9));
			rawBlock.frequencyOnChunk = (int) (7 / Math.pow(factor, 0.9));
			rawBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
			addGeneratableElementToWorkspace(workspace, folder, rawBlock);
		}

		// Metal/Storage Block
		Block oreBlockBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Block", ModElementType.BLOCK), false).getElementFromGUI();
		oreBlockBlock.name = readableName + " Block";
		oreBlockBlock.customModelName = "Single texture";
		oreBlockBlock.soundOnStep = new StepSound(workspace, "METAL");
		oreBlockBlock.hardness = 5.0;
		oreBlockBlock.resistance = 6.0;
		oreBlockBlock.texture = new TextureHolder(workspace, oreBlockTextureName);
		oreBlockBlock.destroyTool = "pickaxe";
		if (factor < 1) {
			oreBlockBlock.vanillaToolTier = "STONE";
		} else if (factor == 1) {
			oreBlockBlock.vanillaToolTier = "IRON";
		} else {
			oreBlockBlock.vanillaToolTier = "DIAMOND";
		}
		oreBlockBlock.requiresCorrectTool = true;
		oreBlockBlock.renderType = 11; // single texture
		oreBlockBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		addGeneratableElementToWorkspace(workspace, folder, oreBlockBlock);

		// Ore Deepslate Block
		Block oreBlockDeepslateBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "DeepslateOre", ModElementType.BLOCK), false).getElementFromGUI();
		oreBlockDeepslateBlock.name = readableName + " Deepslate Ore";
		oreBlockDeepslateBlock.customModelName = "Single texture";
		oreBlockDeepslateBlock.soundOnStep = new StepSound(workspace, "METAL");
		oreBlockDeepslateBlock.hardness = 5.0;
		oreBlockDeepslateBlock.resistance = 6.0;
		oreBlockDeepslateBlock.texture = new TextureHolder(workspace, oreDeepslateTextureName);
		oreBlockDeepslateBlock.destroyTool = "pickaxe";
		if (factor < 1) {
			oreBlockDeepslateBlock.vanillaToolTier = "STONE";
		} else if (factor == 1) {
			oreBlockDeepslateBlock.vanillaToolTier = "IRON";
		} else {
			oreBlockDeepslateBlock.vanillaToolTier = "DIAMOND";
		}
		oreBlockDeepslateBlock.requiresCorrectTool = true;
		oreBlockDeepslateBlock.renderType = 11; // single texture
		oreBlockDeepslateBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		if (type.equals("Ingot based")) {
			oreBlockDeepslateBlock.dropAmount = 3;
			oreBlockDeepslateBlock.customDrop = new MItemBlock(workspace, "CUSTOM:" + "Raw" + name);
		} else {
			oreBlockDeepslateBlock.dropAmount = 1;
			oreBlockDeepslateBlock.customDrop = new MItemBlock(workspace, "CUSTOM:" + oreItemName);
		}
		addGeneratableElementToWorkspace(workspace, folder, oreBlockDeepslateBlock);

		// Recipes

		// Metal Block ~ Storage Block
		Recipe itemToBlockRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "OreBlockRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		itemToBlockRecipe.craftingBookCategory = "BUILDING";
		itemToBlockRecipe.recipeSlots[0] = new MItemBlock(workspace, "CUSTOM:" + oreItemName);
		itemToBlockRecipe.recipeSlots[1] = new MItemBlock(workspace, "CUSTOM:" + oreItemName);
		itemToBlockRecipe.recipeSlots[2] = new MItemBlock(workspace, "CUSTOM:" + oreItemName);
		itemToBlockRecipe.recipeSlots[3] = new MItemBlock(workspace, "CUSTOM:" + oreItemName);
		itemToBlockRecipe.recipeSlots[4] = new MItemBlock(workspace, "CUSTOM:" + oreItemName);
		itemToBlockRecipe.recipeSlots[5] = new MItemBlock(workspace, "CUSTOM:" + oreItemName);
		itemToBlockRecipe.recipeSlots[6] = new MItemBlock(workspace, "CUSTOM:" + oreItemName);
		itemToBlockRecipe.recipeSlots[7] = new MItemBlock(workspace, "CUSTOM:" + oreItemName);
		itemToBlockRecipe.recipeSlots[8] = new MItemBlock(workspace, "CUSTOM:" + oreItemName);
		itemToBlockRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Block");
		itemToBlockRecipe.unlockingItems.add(new MItemBlock(workspace, "CUSTOM:" + oreItemName));
		addGeneratableElementToWorkspace(workspace, folder, itemToBlockRecipe);

		// Storage Block -> Ingots
		Recipe blockToItemRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "BlockOreRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		blockToItemRecipe.recipeSlots[4] = new MItemBlock(workspace, "CUSTOM:" + name + "Block");
		blockToItemRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + oreItemName);
		blockToItemRecipe.recipeShapeless = true;
		blockToItemRecipe.recipeRetstackSize = 9;
		blockToItemRecipe.unlockingItems.add(new MItemBlock(workspace, "CUSTOM:" + name + "Block"));
		addGeneratableElementToWorkspace(workspace, folder, blockToItemRecipe);

		if (type.equals("Ingot based")) {

		// Raw Storage Block (9 raw -> 1 raw block and vice versa)
		Recipe rawItemToBlockRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "RawBlockRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		rawItemToBlockRecipe.craftingBookCategory = "BUILDING";
		rawItemToBlockRecipe.recipeSlots[0] = new MItemBlock(workspace, "CUSTOM:" + "Raw" + name);
		rawItemToBlockRecipe.recipeSlots[1] = new MItemBlock(workspace, "CUSTOM:" + "Raw" + name);
		rawItemToBlockRecipe.recipeSlots[2] = new MItemBlock(workspace, "CUSTOM:" + "Raw" + name);
		rawItemToBlockRecipe.recipeSlots[3] = new MItemBlock(workspace, "CUSTOM:" + "Raw" + name);
		rawItemToBlockRecipe.recipeSlots[4] = new MItemBlock(workspace, "CUSTOM:" + "Raw" + name);
		rawItemToBlockRecipe.recipeSlots[5] = new MItemBlock(workspace, "CUSTOM:" + "Raw" + name);
		rawItemToBlockRecipe.recipeSlots[6] = new MItemBlock(workspace, "CUSTOM:" + "Raw" + name);
		rawItemToBlockRecipe.recipeSlots[7] = new MItemBlock(workspace, "CUSTOM:" + "Raw" + name);
		rawItemToBlockRecipe.recipeSlots[8] = new MItemBlock(workspace, "CUSTOM:" + "Raw" + name);
		rawItemToBlockRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + "Raw" + name + "Block");
		rawItemToBlockRecipe.unlockingItems.add(new MItemBlock(workspace, "CUSTOM:" + "Raw" + name));
		addGeneratableElementToWorkspace(workspace, folder, rawItemToBlockRecipe);

		// Raw Storage Block -> Raw Items
		Recipe rawBlockToItemRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "BlockRawRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		rawBlockToItemRecipe.recipeSlots[4] = new MItemBlock(workspace, "CUSTOM:" + "Raw" + name + "Block");
		rawBlockToItemRecipe.recipeShapeless = true;
		rawBlockToItemRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + "Raw" + name);
		rawBlockToItemRecipe.recipeRetstackSize = 9;
		rawBlockToItemRecipe.unlockingItems.add(new MItemBlock(workspace, "CUSTOM:" + "Raw" + name + "Block"));
		addGeneratableElementToWorkspace(workspace, folder, rawBlockToItemRecipe);
		}

		// Ore smelting (ore -> ingot/gem)
		Recipe oreSmeltingRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "OreSmelting", ModElementType.RECIPE), false).getElementFromGUI();
		oreSmeltingRecipe.recipeType = "Smelting";
		oreSmeltingRecipe.smeltingInputStack = new MItemBlock(workspace, "CUSTOM:" + name + "Ore");
		oreSmeltingRecipe.smeltingReturnStack = new MItemBlock(workspace, "CUSTOM:" + oreItemName);
		oreSmeltingRecipe.xpReward = 0.7 * factor;
		oreSmeltingRecipe.cookingTime = 200;
		oreSmeltingRecipe.unlockingItems.add(new MItemBlock(workspace, "CUSTOM:" + name + "Ore"));
		addGeneratableElementToWorkspace(workspace, folder, oreSmeltingRecipe);

		// Ore blasting (ore -> ingot/gem)
		Recipe oreBlastingRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "OreBlasting", ModElementType.RECIPE), false).getElementFromGUI();
		oreBlastingRecipe.recipeType = "Blasting";
		oreBlastingRecipe.blastingInputStack = new MItemBlock(workspace, "CUSTOM:" + name + "Ore");
		oreBlastingRecipe.blastingReturnStack = new MItemBlock(workspace, "CUSTOM:" + oreItemName);
		oreBlastingRecipe.xpReward = oreSmeltingRecipe.xpReward;
		oreBlastingRecipe.cookingTime = 100; // faster than smelting
		oreBlastingRecipe.unlockingItems.add(new MItemBlock(workspace, "CUSTOM:" + name + "Ore"));
		addGeneratableElementToWorkspace(workspace, folder, oreBlastingRecipe);

		// Ore smelting (deepslate ore -> ingot/gem)
		Recipe deepslateOreSmeltingRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "DeepslateOreSmelting", ModElementType.RECIPE), false).getElementFromGUI();
		deepslateOreSmeltingRecipe.recipeType = "Smelting";
		deepslateOreSmeltingRecipe.smeltingInputStack = new MItemBlock(workspace, "CUSTOM:" + name + "DeepslateOre");
		deepslateOreSmeltingRecipe.smeltingReturnStack = new MItemBlock(workspace, "CUSTOM:" + oreItemName);
		deepslateOreSmeltingRecipe.xpReward = 0.7 * factor;
		deepslateOreSmeltingRecipe.cookingTime = 200;
		deepslateOreSmeltingRecipe.unlockingItems.add(new MItemBlock(workspace, "CUSTOM:" + name + "DeepslateOre"));
		addGeneratableElementToWorkspace(workspace, folder, deepslateOreSmeltingRecipe);

		// Deepslate ore blasting (deepslate ore -> ingot/gem)
		Recipe deepslateOreBlastingRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
			new ModElement(workspace, name + "DeepslateOreBlasting", ModElementType.RECIPE), false).getElementFromGUI();
		deepslateOreBlastingRecipe.recipeType = "Blasting";
		deepslateOreBlastingRecipe.blastingInputStack = new MItemBlock(workspace, "CUSTOM:" + name + "DeepslateOre");
		deepslateOreBlastingRecipe.blastingReturnStack = new MItemBlock(workspace, "CUSTOM:" + oreItemName);
		deepslateOreBlastingRecipe.xpReward = deepslateOreSmeltingRecipe.xpReward;
		deepslateOreBlastingRecipe.cookingTime = 100;
		deepslateOreBlastingRecipe.unlockingItems.add(new MItemBlock(workspace, "CUSTOM:" + name + "DeepslateOre"));
		addGeneratableElementToWorkspace(workspace, folder, deepslateOreBlastingRecipe);

		// Raw ore smelting (raw -> ingot)
		if (type.equals("Ingot based")) {
			Recipe rawOreSmeltingRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "RawOreSmelting", ModElementType.RECIPE), false).getElementFromGUI();
			rawOreSmeltingRecipe.recipeType = "Smelting";
			rawOreSmeltingRecipe.smeltingInputStack = new MItemBlock(workspace, "CUSTOM:" + "Raw" + name);
			rawOreSmeltingRecipe.smeltingReturnStack = new MItemBlock(workspace, "CUSTOM:" + oreItemName);
			rawOreSmeltingRecipe.xpReward = 0.7 * factor;
			rawOreSmeltingRecipe.cookingTime = 200;
			rawOreSmeltingRecipe.unlockingItems.add(new MItemBlock(workspace, "CUSTOM:" + "Raw" + name));
			addGeneratableElementToWorkspace(workspace, folder, rawOreSmeltingRecipe);

			// Raw blasting (raw -> ingot via blast furnace)
			Recipe rawOreBlastingRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "RawOreBlasting", ModElementType.RECIPE), false).getElementFromGUI();
			rawOreBlastingRecipe.recipeType = "Blasting";
			rawOreBlastingRecipe.blastingInputStack = new MItemBlock(workspace, "CUSTOM:" + "Raw" + name);
			rawOreBlastingRecipe.blastingReturnStack = new MItemBlock(workspace, "CUSTOM:" + oreItemName);
			rawOreBlastingRecipe.xpReward = rawOreSmeltingRecipe.xpReward;
			rawOreBlastingRecipe.cookingTime = 100;
			rawOreBlastingRecipe.unlockingItems.add(new MItemBlock(workspace, "CUSTOM:" + "Raw" + name));
			addGeneratableElementToWorkspace(workspace, folder, rawOreBlastingRecipe);
		}

		return new MItemBlock(workspace, "CUSTOM:" + oreItemName);
	}

	public static BasicAction getAction(ActionRegistry actionRegistry) {
		return new BasicAction(actionRegistry, L10N.t("action.pack_tools.ore"),
				e -> new OrePackMakerTool(actionRegistry.getMCreator())) {
			@Override public boolean isEnabled() {
				GeneratorConfiguration gc = actionRegistry.getMCreator().getGeneratorConfiguration();
				return gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.RECIPE)
						!= GeneratorStats.CoverageStatus.NONE
						&& gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.ITEM)
						!= GeneratorStats.CoverageStatus.NONE
						&& gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.BLOCK)
						!= GeneratorStats.CoverageStatus.NONE;
			}
		}.setIcon(UIRES.get("16px.orepack"));
	}

}
