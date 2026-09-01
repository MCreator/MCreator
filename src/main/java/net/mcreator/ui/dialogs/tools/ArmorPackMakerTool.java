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
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.parts.TextureHolder;
import net.mcreator.element.types.Armor;
import net.mcreator.element.types.Recipe;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.io.FileIO;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.BasicAction;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.minecraft.MCItemHolder;
import net.mcreator.ui.minecraft.TextureComboBox;
import net.mcreator.ui.minecraft.TextureSelectionButton;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.ModElementNameValidator;
import net.mcreator.ui.variants.modmaker.ModMaker;
import net.mcreator.ui.views.ArmorImageMakerView;
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
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArmorPackMakerTool extends AbstractPackMakerTool {

	private final VTextField name = new VTextField(25);
	private final JColor color;
	private final JSpinner power = new JSpinner(new SpinnerNumberModel(1, 0.1, 10, 0.1));
	private final MCItemHolder base;

	private final TextureSelectionButton helmetTexture;
	private final TextureSelectionButton chestplateTexture;
	private final TextureSelectionButton leggingsTexture;
	private final TextureSelectionButton bootsTexture;
	private final TextureComboBox armorTextureFile;

	private ArmorPackMakerTool(MCreator mcreator) {
		super(mcreator, "armor_pack", UIRES.get("16px.armorpack").getImage());

		JTabbedPane tabPanel = new JTabbedPane();

		// Main properties page
		JPanel props = new JPanel(new GridLayout(4, 2, 5, 2));

		color = new JColor(mcreator, false, false);
		base = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems).requireValue(
				"dialog.tools.armor_pack_base_item_validator");

		color.setColor(Theme.current().getInterfaceAccentColor());
		name.enableRealtimeValidation();

		props.add(L10N.label("dialog.tools.armor_pack_base_item"));
		props.add(PanelUtils.centerInPanel(base));

		base.addBlockSelectedListener(_ -> {
			try {
				if (base.getBlock() != null) {
					color.setColor(ImageUtils.getAverageColor(
							ImageUtils.toBufferedImage(((ImageIcon) base.getIcon()).getImage())).brighter().brighter());
					if (base.getBlock().getUnmappedValue().startsWith("CUSTOM:")) {
						name.setText(StringUtils.machineToReadableName(
								base.getBlock().getUnmappedValue().replace("CUSTOM:", "")).split(" ")[0]);
					}
				}
			} catch (Exception ignored) {
			}
		});

		props.add(L10N.label("dialog.tools.armor_pack_name"));
		props.add(name);

		props.add(L10N.label("dialog.tools.armor_pack_color_accent"));
		props.add(color);

		props.add(L10N.label("dialog.tools.armor_pack_power_factor"));
		props.add(power);

		name.setValidator(new ModElementNameValidator(mcreator.getWorkspace(), name,
				L10N.t("dialog.tools.armor_pack_name_validator")));

		validableElements.addValidationElement(base);
		validableElements.addValidationElement(name);

		// Textures page
		JPanel texturesPanel = new JPanel(new GridLayout(2, 4, 5, 2));

		helmetTexture = new TextureSelectionButton(mcreator, TextureType.ITEM, 64);
		chestplateTexture = new TextureSelectionButton(mcreator, TextureType.ITEM, 64);
		leggingsTexture = new TextureSelectionButton(mcreator, TextureType.ITEM, 64);
		bootsTexture = new TextureSelectionButton(mcreator, TextureType.ITEM, 64);
		armorTextureFile = new TextureComboBox(mcreator, TextureType.ARMOR, true);
		armorTextureFile.setAddPNGExtension(false);

		texturesPanel.add(L10N.label("dialog.tools.armor_pack_textures.helmet"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(helmetTexture));

		texturesPanel.add(L10N.label("dialog.tools.armor_pack_textures.chestplate"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(chestplateTexture));

		texturesPanel.add(L10N.label("dialog.tools.armor_pack_textures.leggings"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(leggingsTexture));

		texturesPanel.add(L10N.label("dialog.tools.armor_pack_textures.boots"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(bootsTexture));

		tabPanel.add(L10N.t("dialog.tools.pack_makers.properties"), PanelUtils.totalCenterInPanel(props));
		tabPanel.add(L10N.t("dialog.tools.pack_makers.textures"), PanelUtils.totalCenterInPanel(
				PanelUtils.centerAndSouthElement(texturesPanel,
						PanelUtils.centerAndEastElement(L10N.label("dialog.tools.armor_pack_textures.armor"),
								armorTextureFile))));

		this.add("Center", PanelUtils.centerInPanel(tabPanel));

		this.setSize(600, 290);
		this.setLocationRelativeTo(mcreator);
		this.setVisible(true);
	}

	@Override protected void generatePack(MCreator mcreator) {
		addArmorPackToWorkspace(this, mcreator, mcreator.getWorkspace(), name.getText(), base.getBlock(),
				color.getColor(), (Double) power.getValue(), makeTextureMap());
	}

	static void addArmorPackToWorkspace(@Nullable AbstractPackMakerTool packMaker, MCreator mcreator,
			Workspace workspace, String name, MItemBlock base, Color color, double factor,
			@Nonnull Map<String, TextureHolder> textureMap) {
		if (!checkIfNamesAvailable(workspace, name + "Armor", name + "ArmorHelmetRecipe",
				name + "ArmorChestplateRecipe", name + "ArmorLeggingsRecipe", name + "ArmorBootsRecipe"))
			return;

		String registryName = RegistryNameFixer.fromCamelCase(name);
		String readableName = StringUtils.machineToReadableName(name);

		// select folder the mod pack should be in
		FolderElement folder = mcreator instanceof ModMaker modMaker ?
				modMaker.getWorkspacePanel().currentFolder :
				null;

		// generate armor textures
		Image[] generatedTextures = ArmorImageMakerView.getImages("Standard", color, true);
		if (!textureMap.containsKey("helmet")) {
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(generatedTextures[2]),
					mcreator.getFolderManager().getTextureFile(registryName + "_helmet", TextureType.ITEM));
		}
		if (!textureMap.containsKey("chestplate")) {
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(generatedTextures[3]),
					mcreator.getFolderManager().getTextureFile(registryName + "_chestplate", TextureType.ITEM));
		}
		if (!textureMap.containsKey("leggings")) {
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(generatedTextures[4]),
					mcreator.getFolderManager().getTextureFile(registryName + "_leggings", TextureType.ITEM));
		}
		if (!textureMap.containsKey("boots")) {
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(generatedTextures[5]),
					mcreator.getFolderManager().getTextureFile(registryName + "_boots", TextureType.ITEM));
		}
		if (!textureMap.containsKey("armor")) {
			File[] armorTextureFiles = workspace.getFolderManager().getArmorTextureFilesForName(registryName);
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(generatedTextures[0]), armorTextureFiles[0]);
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(generatedTextures[1]), armorTextureFiles[1]);
		}

		// generate armor item
		Armor armor = (Armor) ModElementType.ARMOR.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Armor", ModElementType.ARMOR), false).getElementFromGUI();
		armor.helmetName = readableName + " Helmet";
		armor.bodyName = readableName + " Chestplate";
		armor.leggingsName = readableName + " Leggings";
		armor.bootsName = readableName + " Boots";
		armor.textureHelmet = textureMap.containsKey("helmet") ?
				textureMap.get("helmet") :
				new TextureHolder(workspace, registryName + "_helmet");
		armor.textureBody = textureMap.containsKey("chestplate") ?
				textureMap.get("chestplate") :
				new TextureHolder(workspace, registryName + "_chestplate");
		armor.textureLeggings = textureMap.containsKey("leggings") ?
				textureMap.get("leggings") :
				new TextureHolder(workspace, registryName + "_leggings");
		armor.textureBoots = textureMap.containsKey("boots") ?
				textureMap.get("boots") :
				new TextureHolder(workspace, registryName + "_boots");
		armor.armorTextureFile = textureMap.containsKey("armor") ?
				textureMap.get("armor").getRawTextureName() :
				registryName;
		armor.creativeTabs = List.of(new TabEntry(workspace, "COMBAT"));
		armor.maxDamage = (int) Math.round(15 * factor);
		armor.enchantability = (int) Math.round(9 * factor);
		armor.toughness = 0;
		armor.knockbackResistance = 0;
		armor.damageValueHelmet = (int) Math.round(2 * factor);
		armor.damageValueBody = (int) Math.round(6 * factor);
		armor.damageValueLeggings = (int) Math.round(5 * factor);
		armor.damageValueBoots = (int) Math.round(2 * factor);
		armor.repairItems = Collections.singletonList(base);
		addGeneratableElementToWorkspace(packMaker, workspace, folder, armor);

		// generate recipes
		Recipe armorHelmetRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
						new ModElement(workspace, name + "ArmorHelmetRecipe", ModElementType.RECIPE), false)
				.getElementFromGUI();
		armorHelmetRecipe.craftingBookCategory = "EQUIPMENT";
		armorHelmetRecipe.recipeSlots[0] = base;
		armorHelmetRecipe.recipeSlots[1] = base;
		armorHelmetRecipe.recipeSlots[2] = base;
		armorHelmetRecipe.recipeSlots[3] = base;
		armorHelmetRecipe.recipeSlots[5] = base;
		armorHelmetRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Armor" + ".helmet");
		armorHelmetRecipe.unlockingItems.add(base);
		addGeneratableElementToWorkspace(packMaker, workspace, folder, armorHelmetRecipe);

		Recipe armorBodyRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
						new ModElement(workspace, name + "ArmorChestplateRecipe", ModElementType.RECIPE), false)
				.getElementFromGUI();
		armorBodyRecipe.craftingBookCategory = "EQUIPMENT";
		armorBodyRecipe.recipeSlots[0] = base;
		armorBodyRecipe.recipeSlots[2] = base;
		armorBodyRecipe.recipeSlots[3] = base;
		armorBodyRecipe.recipeSlots[4] = base;
		armorBodyRecipe.recipeSlots[5] = base;
		armorBodyRecipe.recipeSlots[6] = base;
		armorBodyRecipe.recipeSlots[7] = base;
		armorBodyRecipe.recipeSlots[8] = base;
		armorBodyRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Armor" + ".body");
		armorBodyRecipe.unlockingItems.add(base);
		addGeneratableElementToWorkspace(packMaker, workspace, folder, armorBodyRecipe);

		Recipe armorLeggingsRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
						new ModElement(workspace, name + "ArmorLeggingsRecipe", ModElementType.RECIPE), false)
				.getElementFromGUI();
		armorLeggingsRecipe.craftingBookCategory = "EQUIPMENT";
		armorLeggingsRecipe.recipeSlots[0] = base;
		armorLeggingsRecipe.recipeSlots[1] = base;
		armorLeggingsRecipe.recipeSlots[2] = base;
		armorLeggingsRecipe.recipeSlots[3] = base;
		armorLeggingsRecipe.recipeSlots[5] = base;
		armorLeggingsRecipe.recipeSlots[6] = base;
		armorLeggingsRecipe.recipeSlots[8] = base;
		armorLeggingsRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Armor" + ".legs");
		armorLeggingsRecipe.unlockingItems.add(base);
		addGeneratableElementToWorkspace(packMaker, workspace, folder, armorLeggingsRecipe);

		Recipe armorBootsRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "ArmorBootsRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		armorBootsRecipe.craftingBookCategory = "EQUIPMENT";
		armorBootsRecipe.recipeSlots[3] = base;
		armorBootsRecipe.recipeSlots[5] = base;
		armorBootsRecipe.recipeSlots[6] = base;
		armorBootsRecipe.recipeSlots[8] = base;
		armorBootsRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Armor" + ".boots");
		armorBootsRecipe.unlockingItems.add(base);
		addGeneratableElementToWorkspace(packMaker, workspace, folder, armorBootsRecipe);
	}

	private Map<String, TextureHolder> makeTextureMap() {
		HashMap<String, TextureHolder> map = new HashMap<>();

		addToTextureMap(map, helmetTexture, "helmet");
		addToTextureMap(map, chestplateTexture, "chestplate");
		addToTextureMap(map, leggingsTexture, "leggings");
		addToTextureMap(map, bootsTexture, "boots");
		addToTextureMap(map, armorTextureFile, "armor");

		return map;
	}

	public static BasicAction getAction(ActionRegistry actionRegistry) {
		return new BasicAction(actionRegistry, L10N.t("action.pack_tools.armor"),
				_ -> new ArmorPackMakerTool(actionRegistry.getMCreator())) {
			@Override public boolean isEnabled() {
				GeneratorConfiguration gc = actionRegistry.getMCreator().getGeneratorConfiguration();
				return gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.RECIPE)
						!= GeneratorStats.CoverageStatus.NONE
						&& gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.ARMOR)
						!= GeneratorStats.CoverageStatus.NONE;
			}
		}.setIcon(UIRES.get("16px.armorpack"));
	}

}
