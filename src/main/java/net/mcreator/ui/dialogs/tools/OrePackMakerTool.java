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
import net.mcreator.io.ResourcePointer;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.BasicAction;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.MCreatorDialog;
import net.mcreator.ui.init.ImageMakerTexturesCache;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.ModElementNameValidator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.ListUtils;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class OrePackMakerTool {

	private static void open(MCreator mcreator) {
		MCreatorDialog dialog = new MCreatorDialog(mcreator, L10N.t("dialog.tools.ore_pack_title"), true);

		dialog.setIconImage(UIRES.get("16px.orepack").getImage());

		dialog.setLayout(new BorderLayout(10, 10));

		dialog.add("North", PanelUtils.centerInPanel(L10N.label("dialog.tools.ore_pack_info")));

		JPanel props = new JPanel(new GridLayout(4, 2, 5, 2));

		VTextField name = new VTextField(25);
		JColor color = new JColor(mcreator, false, false);
		JSpinner power = new JSpinner(new SpinnerNumberModel(1, 0.1, 10, 0.1));
		JComboBox<String> type = new JComboBox<>(new String[] { "Gem based", "Dust based", "Ingot based" });

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

		dialog.add("Center", PanelUtils.centerInPanel(props));
		JButton ok = L10N.button("dialog.tools.ore_pack_create");
		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		cancel.addActionListener(e -> dialog.setVisible(false));
		dialog.add("South", PanelUtils.join(ok, cancel));

		ok.addActionListener(e -> {
			if (name.getValidationStatus().getValidationResultType() != Validator.ValidationResultType.ERROR) {
				dialog.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				addOrePackToWorkspace(mcreator, mcreator.getWorkspace(), name.getText(),
						(String) Objects.requireNonNull(type.getSelectedItem()), color.getColor(),
						(Double) power.getValue());
				mcreator.mv.reloadElementsInCurrentTab();
				dialog.setCursor(Cursor.getDefaultCursor());
				dialog.setVisible(false);
			}
		});

		dialog.getRootPane().setDefaultButton(ok);
		dialog.setSize(600, 280);
		dialog.setLocationRelativeTo(mcreator);
		dialog.setVisible(true);
	}

	static MItemBlock addOrePackToWorkspace(MCreator mcreator, Workspace workspace, String name, String type,
			Color color, double factor) {
		String oreItemName;
		if (type.equals("Dust based")) {
			oreItemName = name + "Dust";
		} else if (type.equals("Gem based")) {
			oreItemName = name;
		} else {
			oreItemName = name + "Ingot";
		}

		if (!PackMakerToolUtils.checkIfNamesAvailable(workspace, oreItemName, name + "Ore", name + "Block",
				name + "OreBlockRecipe", name + "BlockOreRecipe", name + "OreSmelting"))
			return null;

		// select folder the mod pack should be in
		FolderElement folder = mcreator.mv.currentFolder;

		// first we generate ore texture
		ImageIcon ore = ImageUtils.drawOver(
				ImageMakerTexturesCache.CACHE.get(new ResourcePointer("templates/textures/texturemaker/noise5.png")),
				ImageUtils.colorize(ImageMakerTexturesCache.CACHE.get(
						new ResourcePointer("templates/textures/texturemaker/ore10.png")), color, true));
		String oreTextureName = (name + "_ore").toLowerCase(Locale.ENGLISH);
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(ore.getImage()),
				mcreator.getFolderManager().getTextureFile(RegistryNameFixer.fix(oreTextureName), TextureType.BLOCK));

		// next, ore block texture
		ImageIcon oreBlockIc = ImageUtils.colorize(ImageMakerTexturesCache.CACHE.get(new ResourcePointer(
				"templates/textures/texturemaker/" + ListUtils.getRandomItem(
						Arrays.asList("oreblock1", "oreblock2", "oreblock3", "oreblock4", "oreblock5", "oreblock6",
								"oreblock7", "oreblock8")) + ".png")), color, true);
		String oreBlockTextureName = (oreTextureName + "_block").toLowerCase(Locale.ENGLISH);
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(oreBlockIc.getImage()), mcreator.getFolderManager()
				.getTextureFile(RegistryNameFixer.fix(oreBlockTextureName), TextureType.BLOCK));

		// next, gem texture
		ImageIcon gem;
		String gemTextureName;
		if (type.equals("Gem based")) {
			gem = ImageUtils.colorize(ImageMakerTexturesCache.CACHE.get(new ResourcePointer(
					"templates/textures/texturemaker/" + ListUtils.getRandomItem(
							Arrays.asList("gem4", "gem6", "gem7", "gem9", "gem13")) + ".png")), color, true);
			gemTextureName = (name + "_gem").toLowerCase(Locale.ENGLISH);
		} else if (type.equals("Dust based")) {
			gem = ImageUtils.drawOver(ImageUtils.colorize(ImageMakerTexturesCache.CACHE.get(
							new ResourcePointer("templates/textures/texturemaker/dust_base.png")), color, true),
					ImageUtils.colorize(ImageMakerTexturesCache.CACHE.get(
							new ResourcePointer("templates/textures/texturemaker/dust_sprinkles.png")), color, true));
			gemTextureName = (name + "_dust").toLowerCase(Locale.ENGLISH);
		} else {
			gem = ImageUtils.colorize(ImageMakerTexturesCache.CACHE.get(new ResourcePointer(
					"templates/textures/texturemaker/" + ListUtils.getRandomItem(
							Arrays.asList("ingot_dark", "ingot_bright")) + ".png")), color, true);
			gemTextureName = (name + "_ingot").toLowerCase(Locale.ENGLISH);
		}
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(gem.getImage()),
				mcreator.getFolderManager().getTextureFile(RegistryNameFixer.fix(gemTextureName), TextureType.ITEM));

		Item oreItem = (Item) ModElementType.ITEM.getModElementGUI(mcreator,
				new ModElement(workspace, oreItemName, ModElementType.ITEM), false).getElementFromGUI();
		oreItem.name = name;
		oreItem.texture = gemTextureName;
		oreItem.creativeTab = new TabEntry(workspace, "MATERIALS");
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, oreItem);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block oreBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Ore", ModElementType.BLOCK), false).getElementFromGUI();
		oreBlock.name = name + " Ore";
		oreBlock.material = new Material(workspace, "ROCK");
		oreBlock.texture = oreTextureName;
		oreBlock.renderType = 11; // single texture
		oreBlock.customModelName = "Single texture";
		oreBlock.soundOnStep = new StepSound(workspace, "STONE");
		oreBlock.hardness = 3.0 * factor;
		oreBlock.resistance = 5.0 * Math.pow(factor, 0.8);
		oreBlock.destroyTool = "pickaxe";
		oreBlock.breakHarvestLevel = (int) Math.round(2 * factor);
		oreBlock.requiresCorrectTool = true;
		oreBlock.generateFeature = true;
		oreBlock.restrictionBiomes = List.of(new BiomeEntry(mcreator.getWorkspace(), "#is_overworld"));
		oreBlock.minGenerateHeight = 1;
		oreBlock.maxGenerateHeight = (int) (63 / Math.pow(factor, 0.9));
		oreBlock.frequencyPerChunks = (int) (11 / Math.pow(factor, 0.9));
		oreBlock.frequencyOnChunk = (int) (7 / Math.pow(factor, 0.9));
		oreBlock.creativeTab = new TabEntry(workspace, "BUILDING_BLOCKS");
		if (type.equals("Dust based")) {
			oreBlock.dropAmount = 3;
		}
		oreBlock.customDrop = new MItemBlock(workspace, "CUSTOM:" + oreItemName);
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, oreBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block oreBlockBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Block", ModElementType.BLOCK), false).getElementFromGUI();
		oreBlockBlock.name = "Block of " + name;
		oreBlockBlock.customModelName = "Single texture";
		oreBlockBlock.material = new Material(workspace, "IRON");
		oreBlockBlock.soundOnStep = new StepSound(workspace, "METAL");
		oreBlockBlock.hardness = 5.0;
		oreBlockBlock.resistance = 10.0;
		oreBlockBlock.texture = oreBlockTextureName;
		oreBlockBlock.destroyTool = "pickaxe";
		oreBlockBlock.breakHarvestLevel = (int) Math.round(2 * factor);
		oreBlockBlock.requiresCorrectTool = true;
		oreBlockBlock.renderType = 11; // single texture
		oreBlockBlock.creativeTab = new TabEntry(workspace, "BUILDING_BLOCKS");
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, oreBlockBlock);

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
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, itemToBlockRecipe);

		Recipe blockToItemRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "BlockOreRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		blockToItemRecipe.recipeSlots[4] = new MItemBlock(workspace, "CUSTOM:" + name + "Block");
		blockToItemRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + oreItemName);
		blockToItemRecipe.recipeShapeless = true;
		blockToItemRecipe.recipeRetstackSize = 9;
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, blockToItemRecipe);

		Recipe oreSmeltingRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "OreSmelting", ModElementType.RECIPE), false).getElementFromGUI();
		oreSmeltingRecipe.recipeType = "Smelting";
		oreSmeltingRecipe.smeltingInputStack = new MItemBlock(workspace, "CUSTOM:" + name + "Ore");
		oreSmeltingRecipe.smeltingReturnStack = new MItemBlock(workspace, "CUSTOM:" + oreItemName);
		oreSmeltingRecipe.xpReward = 0.7 * factor;
		oreSmeltingRecipe.cookingTime = 200;
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, oreSmeltingRecipe);

		return new MItemBlock(workspace, "CUSTOM:" + oreItemName);
	}

	public static BasicAction getAction(ActionRegistry actionRegistry) {
		return new BasicAction(actionRegistry, L10N.t("action.pack_tools.ore"),
				e -> open(actionRegistry.getMCreator())) {
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
