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

import net.mcreator.element.GeneratableElement;
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
import net.mcreator.ui.minecraft.TextureSelectionButton;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.ModElementNameValidator;
import net.mcreator.ui.variants.modmaker.ModMaker;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OrePackMakerTool extends AbstractPackMakerTool {

	private final VTextField name = new VTextField(25);
	private final JColor color;
	private final JSpinner power = new JSpinner(new SpinnerNumberModel(1, 0.1, 10, 0.1));
	private final JComboBox<String> type = new JComboBox<>(new String[] { "Gem based", "Dust based", "Ingot based" });

	private final TextureSelectionButton oreTexture;
	private final TextureSelectionButton blockTexture;
	private final TextureSelectionButton itemTexture;

	private OrePackMakerTool(MCreator mcreator) {
		super(mcreator, "ore_pack", UIRES.get("16px.orepack").getImage());

		// Main properties page
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

		// Textures page
		JPanel texturesPanel = new JPanel(new GridLayout(2, 4, 5, 2));

		oreTexture = new TextureSelectionButton(mcreator, TextureType.BLOCK, 64);
		blockTexture = new TextureSelectionButton(mcreator, TextureType.BLOCK, 64);
		itemTexture = new TextureSelectionButton(mcreator, TextureType.ITEM, 64);

		texturesPanel.add(L10N.label("dialog.tools.ore_pack_textures.ore"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(oreTexture));

		texturesPanel.add(L10N.label("dialog.tools.ore_pack_textures.block"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(blockTexture));

		texturesPanel.add(L10N.label("dialog.tools.ore_pack_textures.item"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(itemTexture));

		texturesPanel.add(new JLabel());
		texturesPanel.add(new JLabel());

		addPage(L10N.t("dialog.tools.pack_makers.properties"), PanelUtils.totalCenterInPanel(props));
		addPage(L10N.t("dialog.tools.pack_makers.textures"), PanelUtils.totalCenterInPanel(texturesPanel));

		this.add("Center", tabs);

		this.setSize(600, 280);
		this.setLocationRelativeTo(mcreator);
		this.setVisible(true);
	}

	@Override protected void generatePack(MCreator mcreator) {
		addOrePackToWorkspace(toGenerate, mcreator, mcreator.getWorkspace(), name.getText(),
				(String) Objects.requireNonNull(type.getSelectedItem()), color.getColor(), (Double) power.getValue(),
				makeTextureMap());
	}

	public static String getOreItemName(String name, String type) {
		return switch (type) {
			case "Dust based" -> name + "Dust";
			case "Gem based" -> name;
			default -> name + "Ingot";
		};
	}

	public static String[] getPackElementNames(String name, String type) {
		return new String[] { getOreItemName(name, type), name + "Ore", name + "Block", name + "OreBlockRecipe",
				name + "BlockOreRecipe", name + "OreSmelting" };
	}

	public static boolean addOrePackToWorkspace(@Nullable List<GeneratableElement> generationQueue, MCreator mcreator,
			Workspace workspace, String name, String type, Color color, double factor,
			@Nonnull Map<String, TextureHolder> textureMap) {
		String oreItemName = getOreItemName(name, type);

		if (!checkIfNamesAvailable(workspace, getPackElementNames(name, type)))
			return false;

		String registryName = RegistryNameFixer.fromCamelCase(name);
		String readableName = StringUtils.machineToReadableName(name);

		// select folder the mod pack should be in
		FolderElement folder = mcreator instanceof ModMaker modMaker ?
				modMaker.getWorkspacePanel().currentFolder :
				null;

		// first we generate ore texture
		if (!textureMap.containsKey("ore")) {
			ImageIcon ore = baseAndColoredOverlay("noise5", "ore10", color);
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(ore.getImage()),
					mcreator.getFolderManager().getTextureFile(registryName + "_ore", TextureType.BLOCK));
			textureMap.put("ore", new TextureHolder(workspace, registryName + "_ore"));
		}

		// next, ore block texture
		if (!textureMap.containsKey("block")) {
			ImageIcon oreBlock = ImageUtils.colorize(
					getCachedTexture("oreblock1", "oreblock2", "oreblock3", "oreblock4", "oreblock5", "oreblock6",
							"oreblock7", "oreblock8"), color, true);
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(oreBlock.getImage()),
					mcreator.getFolderManager().getTextureFile(registryName + "_block", TextureType.BLOCK));
			textureMap.put("block", new TextureHolder(workspace, registryName + "_block"));
		}

		// next, gem texture
		if (!textureMap.containsKey("item")) {
			String gemTextureName = generatedItemTextureName(registryName, type);
			ImageIcon gem = switch(type) {
				case "Gem based" ->
						ImageUtils.colorize(getCachedTexture("gem4", "gem6", "gem7", "gem9", "gem13"), color, true);
				case "Dust based" ->
						ImageUtils.drawOver(ImageUtils.colorize(getCachedTexture("dust_base"), color, true),
								ImageUtils.colorize(getCachedTexture("dust_sprinkles"), color, true));
				default -> ImageUtils.colorize(getCachedTexture("ingot_dark", "ingot_bright"), color, true);
			};
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(gem.getImage()),
					mcreator.getFolderManager().getTextureFile(gemTextureName, TextureType.ITEM));
			textureMap.put("item", new TextureHolder(workspace, gemTextureName));
		}

		Item oreItem = (Item) ModElementType.ITEM.getModElementGUI(mcreator,
				new ModElement(workspace, oreItemName, ModElementType.ITEM), false).getElementFromGUI();
		oreItem.name = readableName;
		oreItem.texture = textureMap.get("item");
		oreItem.creativeTabs = List.of(new TabEntry(workspace, "MATERIALS"));
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, oreItem);

		// We use element GUIs to get the default values for the elements
		Block oreBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Ore", ModElementType.BLOCK), false).getElementFromGUI();
		oreBlock.name = readableName + " Ore";
		oreBlock.texture = textureMap.get("ore");
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
		oreBlock.noteBlockInstrument = new NoteBlockInstrument(workspace, "basedrum");
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
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, oreBlock);

		Block oreBlockBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Block", ModElementType.BLOCK), false).getElementFromGUI();
		oreBlockBlock.name = "Block of " + readableName;
		oreBlockBlock.customModelName = "Single texture";
		oreBlockBlock.soundOnStep = new StepSound(workspace, "METAL");
		oreBlockBlock.hardness = 5.0;
		oreBlockBlock.resistance = 6.0;
		oreBlockBlock.texture = textureMap.get("block");
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
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, oreBlockBlock);

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
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, itemToBlockRecipe);

		Recipe blockToItemRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "BlockOreRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		blockToItemRecipe.recipeSlots[4] = new MItemBlock(workspace, "CUSTOM:" + name + "Block");
		blockToItemRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + oreItemName);
		blockToItemRecipe.recipeShapeless = true;
		blockToItemRecipe.recipeRetstackSize = 9;
		blockToItemRecipe.unlockingItems.add(new MItemBlock(workspace, "CUSTOM:" + name + "Block"));
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, blockToItemRecipe);

		Recipe oreSmeltingRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "OreSmelting", ModElementType.RECIPE), false).getElementFromGUI();
		oreSmeltingRecipe.recipeType = "Smelting";
		oreSmeltingRecipe.smeltingInputStack = new MItemBlock(workspace, "CUSTOM:" + name + "Ore");
		oreSmeltingRecipe.smeltingReturnStack = new MItemBlock(workspace, "CUSTOM:" + oreItemName);
		oreSmeltingRecipe.xpReward = 0.7 * factor;
		oreSmeltingRecipe.cookingTime = 200;
		oreSmeltingRecipe.unlockingItems.add(new MItemBlock(workspace, "CUSTOM:" + name + "Ore"));
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, oreSmeltingRecipe);

		return true;
	}

	private Map<String, TextureHolder> makeTextureMap() {
		HashMap<String, TextureHolder> map = new HashMap<>();

		addToTextureMap(map, oreTexture, "ore");
		addToTextureMap(map, blockTexture, "block");
		addToTextureMap(map, itemTexture, "item");

		return map;
	}

	private static String generatedItemTextureName(String registryName, String type) {
		return switch(type) {
			case "Dust based" -> registryName + "_dust";
			case "Ingot based" -> registryName + "_ingot";
			default -> registryName;
		};
	}

	public static boolean isSupported(GeneratorConfiguration gc) {
		return gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.RECIPE)
				!= GeneratorStats.CoverageStatus.NONE
				&& gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.ITEM)
				!= GeneratorStats.CoverageStatus.NONE
				&& gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.BLOCK)
				!= GeneratorStats.CoverageStatus.NONE;
	}

	public static BasicAction getAction(ActionRegistry actionRegistry) {
		return new BasicAction(actionRegistry, L10N.t("action.pack_tools.ore"),
				_ -> new OrePackMakerTool(actionRegistry.getMCreator())) {
			@Override public boolean isEnabled() {
				return isSupported(actionRegistry.getMCreator().getGeneratorConfiguration());
			}
		}.setIcon(UIRES.get("16px.orepack"));
	}

}
