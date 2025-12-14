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
	private final JComboBox<String> type = new JComboBox<>(new String[] { "Gem based", "Dust based", "Ingot based" });

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
		String oreItemName = switch (type) {
			case "Dust based" -> name + "Dust";
			case "Gem based" -> name;
			default -> name + "Ingot";
		};

		if (!checkIfNamesAvailable(workspace, oreItemName, name + "Ore", name + "Block",
				name + "OreBlockRecipe", name + "BlockOreRecipe", name + "OreSmelting"))
			return null;

		String registryName = RegistryNameFixer.fromCamelCase(name);
		String readableName = StringUtils.machineToReadableName(name);

		// select folder the mod pack should be in
		FolderElement folder = mcreator instanceof ModMaker modMaker ?
				modMaker.getWorkspacePanel().currentFolder :
				null;

		// first we generate ore texture
		ImageIcon ore = ImageUtils.drawOver(getCachedTexture("noise5"),
				ImageUtils.colorize(getCachedTexture("ore10"), color, true));
		String oreTextureName = registryName + "_ore";
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(ore.getImage()),
				mcreator.getFolderManager().getTextureFile(oreTextureName, TextureType.BLOCK));

		// next, ore block texture
		ImageIcon oreBlockIc = ImageUtils.colorize(
				getCachedTexture("oreblock1", "oreblock2", "oreblock3", "oreblock4", "oreblock5", "oreblock6",
						"oreblock7", "oreblock8"), color, true);
		String oreBlockTextureName = registryName + "_ore_block";
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(oreBlockIc.getImage()), mcreator.getFolderManager()
				.getTextureFile(oreBlockTextureName, TextureType.BLOCK));

		// next, gem texture
		ImageIcon gem;
		String gemTextureName;
		if (type.equals("Gem based")) {
			gem = ImageUtils.colorize(getCachedTexture("gem4", "gem6", "gem7", "gem9", "gem13"), color, true);
			gemTextureName = registryName;
		} else if (type.equals("Dust based")) {
			gem = ImageUtils.drawOver(ImageUtils.colorize(getCachedTexture("dust_base"), color, true),
					ImageUtils.colorize(getCachedTexture("dust_sprinkles"), color, true));
			gemTextureName = registryName + "_dust";
		} else {
			gem = ImageUtils.colorize(getCachedTexture("ingot_dark", "ingot_bright"), color, true);
			gemTextureName = registryName + "_ingot";
		}
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(gem.getImage()),
				mcreator.getFolderManager().getTextureFile(gemTextureName, TextureType.ITEM));

		Item oreItem = (Item) ModElementType.ITEM.getModElementGUI(mcreator,
				new ModElement(workspace, oreItemName, ModElementType.ITEM), false).getElementFromGUI();
		oreItem.name = readableName;
		oreItem.texture = new TextureHolder(workspace, gemTextureName);
		oreItem.creativeTabs = List.of(new TabEntry(workspace, "MATERIALS"));
		addGeneratableElementToWorkspace(workspace, folder, oreItem);

		// We use element GUIs to get the default values for the elements
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
		if (type.equals("Dust based")) {
			oreBlock.dropAmount = 3;
		}
		oreBlock.customDrop = new MItemBlock(workspace, "CUSTOM:" + oreItemName);
		addGeneratableElementToWorkspace(workspace, folder, oreBlock);

		Block oreBlockBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Block", ModElementType.BLOCK), false).getElementFromGUI();
		oreBlockBlock.name = "Block of " + readableName;
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

		Recipe blockToItemRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "BlockOreRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		blockToItemRecipe.recipeSlots[4] = new MItemBlock(workspace, "CUSTOM:" + name + "Block");
		blockToItemRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + oreItemName);
		blockToItemRecipe.recipeShapeless = true;
		blockToItemRecipe.recipeRetstackSize = 9;
		blockToItemRecipe.unlockingItems.add(new MItemBlock(workspace, "CUSTOM:" + name + "Block"));
		addGeneratableElementToWorkspace(workspace, folder, blockToItemRecipe);

		Recipe oreSmeltingRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "OreSmelting", ModElementType.RECIPE), false).getElementFromGUI();
		oreSmeltingRecipe.recipeType = "Smelting";
		oreSmeltingRecipe.smeltingInputStack = new MItemBlock(workspace, "CUSTOM:" + name + "Ore");
		oreSmeltingRecipe.smeltingReturnStack = new MItemBlock(workspace, "CUSTOM:" + oreItemName);
		oreSmeltingRecipe.xpReward = 0.7 * factor;
		oreSmeltingRecipe.cookingTime = 200;
		oreSmeltingRecipe.unlockingItems.add(new MItemBlock(workspace, "CUSTOM:" + name + "Ore"));
		addGeneratableElementToWorkspace(workspace, folder, oreSmeltingRecipe);

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
