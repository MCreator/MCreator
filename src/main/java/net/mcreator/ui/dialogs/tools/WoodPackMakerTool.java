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
import net.mcreator.element.types.ItemExtension;
import net.mcreator.element.types.Recipe;
import net.mcreator.element.types.SpecialEntity;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.io.FileIO;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.minecraft.TagType;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.BasicAction;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.minecraft.TextureComboBox;
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
import java.util.Random;

public class WoodPackMakerTool extends AbstractPackMakerTool {

	private final VTextField name = new VTextField(25);
	private final JColor color;
	private final JColor barkColor;
	private final JSpinner power = new JSpinner(new SpinnerNumberModel(1, 0.1, 10, 0.1));

	// Texture selectors
	private final TextureSelectionButton logSideTexture;
	private final TextureSelectionButton logTopTexture;
	private final TextureSelectionButton strippedLogSideTexture;
	private final TextureSelectionButton strippedLogTopTexture;
	private final TextureSelectionButton planksTexture;
	private final TextureSelectionButton leavesTexture;
	private final TextureSelectionButton doorTopTexture;
	private final TextureSelectionButton doorBottomTexture;
	private final TextureSelectionButton doorItemTexture;
	private final TextureSelectionButton trapdoorTexture;
	private final TextureSelectionButton signItemTexture;
	private final TextureComboBox signEntityTexture;
	private final TextureSelectionButton hangingSignItemTexture;
	private final TextureComboBox hangingSignEntityTexture;
	private final TextureComboBox hangingSignGUITexture;
	private final TextureSelectionButton boatItemTexture;
	private final TextureComboBox boatEntityTexture;
	private final TextureSelectionButton chestBoatItemTexture;
	private final TextureComboBox chestBoatEntityTexture;

	private WoodPackMakerTool(MCreator mcreator) {
		super(mcreator, "wood_pack", UIRES.get("16px.woodpack").getImage());

		// Main properties page
		JPanel props = new JPanel(new GridLayout(4, 2, 5, 2));

		color = new JColor(mcreator, false, false);
		barkColor = new JColor(mcreator, true, false);
		name.enableRealtimeValidation();

		props.add(L10N.label("dialog.tools.wood_pack_name"));
		props.add(name);

		props.add(L10N.label("dialog.tools.wood_pack_color_accent"));
		props.add(color);

		props.add(L10N.label("dialog.tools.wood_pack_bark_color"));
		props.add(barkColor);

		props.add(L10N.label("dialog.tools.wood_pack_power_factor"));
		props.add(power);

		name.setValidator(new ModElementNameValidator(mcreator.getWorkspace(), name,
				L10N.t("dialog.tools.wood_pack_name_validator")));

		validableElements.addValidationElement(name);

		// Textures page
		JPanel texturesPanel = new JPanel(new GridLayout(7, 4, 5, 2));

		logSideTexture = new TextureSelectionButton(mcreator, TextureType.BLOCK, 64);
		logTopTexture = new TextureSelectionButton(mcreator, TextureType.BLOCK, 64);
		strippedLogSideTexture = new TextureSelectionButton(mcreator, TextureType.BLOCK, 64);
		strippedLogTopTexture = new TextureSelectionButton(mcreator, TextureType.BLOCK, 64);
		planksTexture = new TextureSelectionButton(mcreator, TextureType.BLOCK, 64);
		leavesTexture = new TextureSelectionButton(mcreator, TextureType.BLOCK, 64);
		doorTopTexture = new TextureSelectionButton(mcreator, TextureType.BLOCK, 64);
		doorBottomTexture = new TextureSelectionButton(mcreator, TextureType.BLOCK, 64);
		doorItemTexture = new TextureSelectionButton(mcreator, TextureType.ITEM, 64);
		trapdoorTexture = new TextureSelectionButton(mcreator, TextureType.BLOCK, 64);
		signItemTexture = new TextureSelectionButton(mcreator, TextureType.ITEM, 64);
		signEntityTexture = new TextureComboBox(mcreator, TextureType.ENTITY, true).setAddPNGExtension(false);
		hangingSignItemTexture = new TextureSelectionButton(mcreator, TextureType.ITEM, 64);
		hangingSignEntityTexture = new TextureComboBox(mcreator, TextureType.ENTITY, true).setAddPNGExtension(false);
		hangingSignGUITexture = new TextureComboBox(mcreator, TextureType.SCREEN, true).setAddPNGExtension(false);
		boatItemTexture = new TextureSelectionButton(mcreator, TextureType.ITEM, 64);
		boatEntityTexture = new TextureComboBox(mcreator, TextureType.ENTITY, true).setAddPNGExtension(false);
		chestBoatItemTexture = new TextureSelectionButton(mcreator, TextureType.ITEM, 64);
		chestBoatEntityTexture = new TextureComboBox(mcreator, TextureType.ENTITY, true).setAddPNGExtension(false);

		texturesPanel.add(L10N.label("dialog.tools.wood_pack_textures.log_side"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(logSideTexture));

		texturesPanel.add(L10N.label("dialog.tools.wood_pack_textures.log_top"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(logTopTexture));

		texturesPanel.add(L10N.label("dialog.tools.wood_pack_textures.stripped_log_side"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(strippedLogSideTexture));

		texturesPanel.add(L10N.label("dialog.tools.wood_pack_textures.stripped_log_top"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(strippedLogTopTexture));

		texturesPanel.add(L10N.label("dialog.tools.wood_pack_textures.planks"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(planksTexture));

		texturesPanel.add(L10N.label("dialog.tools.wood_pack_textures.leaves"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(leavesTexture));

		texturesPanel.add(L10N.label("dialog.tools.wood_pack_textures.door_top"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(doorTopTexture));

		texturesPanel.add(L10N.label("dialog.tools.wood_pack_textures.door_bottom"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(doorBottomTexture));

		texturesPanel.add(L10N.label("dialog.tools.wood_pack_textures.door_item"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(doorItemTexture));

		texturesPanel.add(L10N.label("dialog.tools.wood_pack_textures.trapdoor"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(trapdoorTexture));

		texturesPanel.add(L10N.label("dialog.tools.wood_pack_textures.sign_item"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(signItemTexture));

		texturesPanel.add(L10N.label("dialog.tools.wood_pack_textures.hanging_sign_item"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(hangingSignItemTexture));

		texturesPanel.add(L10N.label("dialog.tools.wood_pack_textures.boat_item"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(boatItemTexture));

		texturesPanel.add(L10N.label("dialog.tools.wood_pack_textures.chest_boat_item"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(chestBoatItemTexture));

		addPage(L10N.t("dialog.tools.pack_makers.properties"), PanelUtils.totalCenterInPanel(props));
		addPage(L10N.t("dialog.tools.pack_makers.textures"), PanelUtils.totalCenterInPanel(texturesPanel));

		this.add("Center", tabs);

		this.setSize(600, 400);
		this.setLocationRelativeTo(mcreator);
		this.setVisible(true);
	}

	@Override protected void generatePack(MCreator mcreator) {
		addWoodPackToWorkspace(toGenerate, mcreator, mcreator.getWorkspace(), name.getText(), color.getColor(),
				barkColor.getColor(), (Double) power.getValue(), makeTextureMap());
	}

	public static String[] getPackElementNames(String name) {
		return new String[] { name + "Wood", name + "Log", "Stripped" + name + "Wood", "Stripped" + name + "Log",
				name + "Planks", name + "Leaves", name + "Stairs", name + "Slab", name + "Fence", name + "FenceGate",
				name + "Door", name + "Trapdoor", name + "PressurePlate", name + "Button", name + "Sign",
				name + "HangingSign", name + "WallSign", name + "Boat", name + "ChestBoat", name + "WoodRecipe",
				"Stripped" + name + "WoodRecipe", name + "PlanksRecipe", name + "StairsRecipe", name + "SlabRecipe",
				name + "FenceRecipe", name + "FenceGateRecipe", name + "DoorRecipe", name + "TrapdoorRecipe",
				name + "PressurePlateRecipe", name + "ButtonRecipe", name + "SignRecipe", name + "HangingSignRecipe",
				name + "BoatRecipe", name + "ChestBoatRecipe", name + "LeavesComposting" };
	}

	public static boolean addWoodPackToWorkspace(@Nullable List<GeneratableElement> generationQueue, MCreator mcreator,
			Workspace workspace, String name, Color color, Color barkColor, double factor,
			@Nonnull Map<String, TextureHolder> textureMap) {
		String registryName = RegistryNameFixer.fromCamelCase(name);
		String readableName = StringUtils.machineToReadableName(name);

		// If bark color is not specified, use default color
		if (barkColor == null)
			barkColor = color;

		// Use a slightly desaturated, darker color for stripped log textures
		float[] colorHSB = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
		Color strippedColor = Color.getHSBColor(colorHSB[0], colorHSB[1] * 0.9f, colorHSB[2] * 0.85f);

		if (!checkIfNamesAvailable(workspace, getPackElementNames(name)))
			return false;

		// select folder the mod pack should be in
		FolderElement folder = mcreator instanceof ModMaker modMaker ?
				modMaker.getWorkspacePanel().currentFolder :
				null;

		// first we generate wood texture
		ImageIcon logSide = ImageUtils.colorize(
				getCachedTexture("log_side_1", "log_side_2", "log_side_3", "log_side_4", "log_side_5"), barkColor,
				true);
		if (!textureMap.containsKey("log_side")) {
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(logSide.getImage()),
					mcreator.getFolderManager().getTextureFile(registryName + "_log", TextureType.BLOCK));
			textureMap.put("log_side", new TextureHolder(workspace, registryName + "_log"));
		}

		//then we generate the missing log texture
		ImageIcon logTop = ImageUtils.colorize(getCachedTexture("log_top"), color, true);
		if (!textureMap.containsKey("log_top")) {
			if (barkColor != color) { // Redraw the bark rind if bark color is different from default color
				logTop = ImageUtils.drawOver(logTop, ImageUtils.colorize(getCachedTexture("log_top_1"), barkColor, true));
			}
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(logTop.getImage()),
					mcreator.getFolderManager().getTextureFile(registryName + "_log_top", TextureType.BLOCK));
			textureMap.put("log_top", new TextureHolder(workspace, registryName + "_log_top"));
		}

		// then we generate the stripped log side texture
		if (!textureMap.containsKey("stripped_log_side")) {
			ImageIcon strippedLogSide = ImageUtils.colorize(getCachedTexture("stripped_log_side"), strippedColor, true);
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(strippedLogSide.getImage()),
					mcreator.getFolderManager().getTextureFile("stripped_" + registryName + "_log", TextureType.BLOCK));
			textureMap.put("stripped_log_side", new TextureHolder(workspace, "stripped_" + registryName + "_log"));
		}

		// then we generate the stripped log top texture
		if (!textureMap.containsKey("stripped_log_top")) {
			ImageIcon strippedLogTop = ImageUtils.drawOver(logTop,
					ImageUtils.colorize(getCachedTexture("stripped_log_top_outside"), strippedColor, true));
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(strippedLogTop.getImage()),
					mcreator.getFolderManager()
							.getTextureFile("stripped_" + registryName + "_log_top", TextureType.BLOCK));
			textureMap.put("stripped_log_top", new TextureHolder(workspace, "stripped_" + registryName + "_log_top"));
		}

		//then we generate the planks texture
		if (!textureMap.containsKey("planks")) {
			ImageIcon planks = ImageUtils.colorize(getCachedTexture("planks_0", "planks_1"), color, true);
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(planks.getImage()),
					mcreator.getFolderManager().getTextureFile(registryName + "_planks", TextureType.BLOCK));
			textureMap.put("planks", new TextureHolder(workspace, registryName + "_planks"));
		}

		//then we generate the leaves texture
		if (!textureMap.containsKey("leaves")) {
			ImageIcon leaves = ImageUtils.colorize(
					getCachedTexture("leaves_0", "leaves_1", "leaves_2", "leaves_3", "leaves_4", "leaves_5",
							"leaves_new1", "leaves_new2", "leaves2"), color, true);
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(leaves.getImage()),
					mcreator.getFolderManager().getTextureFile(registryName + "_leaves", TextureType.BLOCK));
			textureMap.put("leaves", new TextureHolder(workspace, registryName + "_leaves"));
		}

		// Generate door and trapdoor textures (matching textures have the same suffix)
		int doorSuffix = new Random().nextInt(2) + 1;

		if (!textureMap.containsKey("door_bottom")) {
			ImageIcon doorBottom = coloredBaseAndOverlay("door_bottom_" + doorSuffix, color, "door_hinges_bottom");
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(doorBottom.getImage()),
					mcreator.getFolderManager().getTextureFile(registryName + "_door_bottom", TextureType.BLOCK));
			textureMap.put("door_bottom", new TextureHolder(workspace, registryName + "_door_bottom"));
		}

		if (!textureMap.containsKey("door_top")) {
			ImageIcon doorTop = coloredBaseAndOverlay("door_top_" + doorSuffix, color, "door_hinges_top");
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(doorTop.getImage()),
					mcreator.getFolderManager().getTextureFile(registryName + "_door_top", TextureType.BLOCK));
			textureMap.put("door_top", new TextureHolder(workspace, registryName + "_door_top"));
		}

		if (!textureMap.containsKey("door_item")) {
			ImageIcon doorItem = ImageUtils.colorize(getCachedTexture("door_item_" + doorSuffix), color, true);
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(doorItem.getImage()),
					mcreator.getFolderManager().getTextureFile(registryName + "_door_item", TextureType.ITEM));
			textureMap.put("door_item", new TextureHolder(workspace, registryName + "_door_item"));
		}

		if (!textureMap.containsKey("trapdoor")) {
			ImageIcon trapdoor = ImageUtils.colorize(getCachedTexture("trapdoor_" + doorSuffix), color, true);
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(trapdoor.getImage()),
					mcreator.getFolderManager().getTextureFile(registryName + "_trapdoor", TextureType.BLOCK));
			textureMap.put("trapdoor", new TextureHolder(workspace, registryName + "_trapdoor"));
		}

		// Sign textures
		if (!textureMap.containsKey("sign_item")) {
			ImageIcon signItem = ImageUtils.colorize(getCachedTexture("sign_item"), color, true);
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(signItem.getImage()),
					mcreator.getFolderManager().getTextureFile(registryName + "_sign", TextureType.ITEM));
			textureMap.put("sign_item", new TextureHolder(workspace, registryName + "_sign"));
		}

		if (!textureMap.containsKey("sign_entity")) {
			ImageIcon signEntity = ImageUtils.drawOver(
					ImageUtils.colorize(getCachedTexture("sign_entity"), color, true), new ImageIcon(
							ImageUtils.crop(ImageUtils.toBufferedImage(logSide.getImage()),
									new Rectangle(4, 0, 8, 14))), 0, 16, 8,
					14); // Vanilla signs use part of the log texture for the post
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(signEntity.getImage()),
					mcreator.getFolderManager().getTextureFile(registryName + "_sign", TextureType.ENTITY));
			textureMap.put("sign_entity", new TextureHolder(workspace, registryName + "_sign"));
		}

		// Hanging sign textures
		if (!textureMap.containsKey("hanging_sign_item")) {
			ImageIcon hangingSignItem = coloredBaseAndOverlay("hanging_sign_item_base", color,
					"hanging_sign_item_overlay");
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(hangingSignItem.getImage()),
					mcreator.getFolderManager().getTextureFile(registryName + "_hanging_sign", TextureType.ITEM));
			textureMap.put("hanging_sign_item", new TextureHolder(workspace, registryName + "_hanging_sign"));
		}

		if (!textureMap.containsKey("hanging_sign_entity")) {
			ImageIcon hangingSignEntity = coloredBaseAndOverlay("hanging_sign_entity_base", color,
					"hanging_sign_entity_overlay");
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(hangingSignEntity.getImage()),
					mcreator.getFolderManager().getTextureFile(registryName + "_hanging_sign", TextureType.ENTITY));
			textureMap.put("hanging_sign_entity", new TextureHolder(workspace, registryName + "_hanging_sign"));
		}

		if (!textureMap.containsKey("hanging_sign_gui")) {
			ImageIcon hangingSignGUI = coloredBaseAndOverlay("hanging_sign_gui_base", color,
					"hanging_sign_gui_overlay");
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(hangingSignGUI.getImage()),
					mcreator.getFolderManager().getTextureFile(registryName + "_hanging_sign", TextureType.SCREEN));
			textureMap.put("hanging_sign_gui", new TextureHolder(workspace, registryName + "_hanging_sign"));
		}

		// Boat textures
		ImageIcon boatItem = ImageUtils.colorize(getCachedTexture("boat_item"), color, true);
		if (!textureMap.containsKey("boat_item")) {
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(boatItem.getImage()),
					mcreator.getFolderManager().getTextureFile(registryName + "_boat", TextureType.ITEM));
			textureMap.put("boat_item", new TextureHolder(workspace, registryName + "_boat"));
		}

		ImageIcon boatEntity = ImageUtils.colorize(getCachedTexture("boat_entity"), color, true);
		if (!textureMap.containsKey("boat_entity")) {
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(boatEntity.getImage()),
					mcreator.getFolderManager().getTextureFile(registryName + "_boat", TextureType.ENTITY));
			textureMap.put("boat_entity", new TextureHolder(workspace, registryName + "_boat"));
		}

		if (!textureMap.containsKey("chest_boat_item")) {
			ImageIcon chestBoatItem = ImageUtils.drawOver(boatItem, getCachedTexture("boat_item_chest_overlay"));
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(chestBoatItem.getImage()),
					mcreator.getFolderManager().getTextureFile(registryName + "_chest_boat", TextureType.ITEM));
			textureMap.put("chest_boat_item", new TextureHolder(workspace, registryName + "_chest_boat"));
		}

		if (!textureMap.containsKey("chest_boat_entity")) {
			ImageIcon chestBoatEntity = ImageUtils.drawOver(getCachedTexture("boat_entity_chest_overlay"), boatEntity,
					0, 0, 128, 64);
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(chestBoatEntity.getImage()),
					mcreator.getFolderManager().getTextureFile(registryName + "_chest_boat", TextureType.ENTITY));
			textureMap.put("chest_boat_entity", new TextureHolder(workspace, registryName + "_chest_boat"));
		}

		// We use element GUIs to get the default values for the elements
		Block logBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Log", ModElementType.BLOCK), false).getElementFromGUI();
		logBlock.name = readableName + " Log";
		logBlock.texture = textureMap.get("log_top");
		logBlock.textureTop = textureMap.get("log_top");
		logBlock.textureBack = textureMap.get("log_side");
		logBlock.textureFront = textureMap.get("log_side");
		logBlock.textureLeft = textureMap.get("log_side");
		logBlock.textureRight = textureMap.get("log_side");
		logBlock.particleTexture = textureMap.get("log_side");
		logBlock.renderType = 10; // normal
		logBlock.customModelName = "Normal";
		logBlock.soundOnStep = new StepSound(workspace, "WOOD");
		logBlock.hardness = 2.0 * factor;
		logBlock.resistance = 2.0 * Math.pow(factor, 0.8);
		logBlock.destroyTool = "axe";
		logBlock.noteBlockInstrument = new NoteBlockInstrument(workspace, "bass");
		logBlock.ignitedByLava = true;
		logBlock.flammability = 5;
		logBlock.fireSpreadSpeed = 5;
		logBlock.rotationMode = 5; // log rotation
		logBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, logBlock);

		Block woodBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Wood", ModElementType.BLOCK), false).getElementFromGUI();
		woodBlock.name = readableName + " Wood";
		woodBlock.texture = textureMap.get("log_side");
		woodBlock.renderType = 11; // single texture
		woodBlock.customModelName = "Single texture";
		woodBlock.soundOnStep = new StepSound(workspace, "WOOD");
		woodBlock.hardness = 2.0 * factor;
		woodBlock.resistance = 2.0 * Math.pow(factor, 0.8);
		woodBlock.destroyTool = "axe";
		woodBlock.noteBlockInstrument = new NoteBlockInstrument(workspace, "bass");
		woodBlock.ignitedByLava = true;
		woodBlock.flammability = 5;
		woodBlock.fireSpreadSpeed = 5;
		woodBlock.rotationMode = 5; // log rotation
		woodBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, woodBlock);

		Block strippedLogBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, "Stripped" + name + "Log", ModElementType.BLOCK), false).getElementFromGUI();
		strippedLogBlock.name = "Stripped " + readableName + " Log";
		strippedLogBlock.texture = textureMap.get("stripped_log_top");
		strippedLogBlock.textureTop = textureMap.get("stripped_log_top");
		strippedLogBlock.textureBack = textureMap.get("stripped_log_side");
		strippedLogBlock.textureFront = textureMap.get("stripped_log_side");
		strippedLogBlock.textureLeft = textureMap.get("stripped_log_side");
		strippedLogBlock.textureRight = textureMap.get("stripped_log_side");
		strippedLogBlock.particleTexture = textureMap.get("stripped_log_side");
		strippedLogBlock.renderType = 10; // normal
		strippedLogBlock.customModelName = "Normal";
		strippedLogBlock.soundOnStep = new StepSound(workspace, "WOOD");
		strippedLogBlock.hardness = 2.0 * factor;
		strippedLogBlock.resistance = 2.0 * Math.pow(factor, 0.8);
		strippedLogBlock.destroyTool = "axe";
		strippedLogBlock.noteBlockInstrument = new NoteBlockInstrument(workspace, "bass");
		strippedLogBlock.ignitedByLava = true;
		strippedLogBlock.flammability = 5;
		strippedLogBlock.fireSpreadSpeed = 5;
		strippedLogBlock.rotationMode = 5; // log rotation
		strippedLogBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, strippedLogBlock);

		Block strippedWoodBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, "Stripped" + name + "Wood", ModElementType.BLOCK), false).getElementFromGUI();
		strippedWoodBlock.name = "Stripped " + readableName + " Wood";
		strippedWoodBlock.texture = textureMap.get("stripped_log_side");
		strippedWoodBlock.renderType = 11; // single texture
		strippedWoodBlock.customModelName = "Single texture";
		strippedWoodBlock.soundOnStep = new StepSound(workspace, "WOOD");
		strippedWoodBlock.hardness = 2.0 * factor;
		strippedWoodBlock.resistance = 2.0 * Math.pow(factor, 0.8);
		strippedWoodBlock.destroyTool = "axe";
		strippedWoodBlock.noteBlockInstrument = new NoteBlockInstrument(workspace, "bass");
		strippedWoodBlock.ignitedByLava = true;
		strippedWoodBlock.flammability = 5;
		strippedWoodBlock.fireSpreadSpeed = 5;
		strippedWoodBlock.rotationMode = 5; // log rotation
		strippedWoodBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, strippedWoodBlock);

		// we update stripping results of log blocks *after* we add the stripped variants to the workspace
		logBlock.strippingResult = new MItemBlock(workspace, "CUSTOM:Stripped" + name + "Log");
		woodBlock.strippingResult = new MItemBlock(workspace, "CUSTOM:Stripped" + name + "Wood");
		workspace.getGenerator().generateElement(logBlock);
		workspace.getModElementManager().storeModElement(logBlock);
		workspace.getGenerator().generateElement(woodBlock);
		workspace.getModElementManager().storeModElement(woodBlock);

		Block planksBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Planks", ModElementType.BLOCK), false).getElementFromGUI();
		planksBlock.name = readableName + " Planks";
		planksBlock.texture = textureMap.get("planks");
		planksBlock.renderType = 11; // single texture
		planksBlock.customModelName = "Single texture";
		planksBlock.soundOnStep = new StepSound(workspace, "WOOD");
		planksBlock.hardness = 2.0 * factor;
		planksBlock.resistance = 3.0 * Math.pow(factor, 0.8);
		planksBlock.destroyTool = "axe";
		planksBlock.noteBlockInstrument = new NoteBlockInstrument(workspace, "bass");
		planksBlock.ignitedByLava = true;
		planksBlock.flammability = 20;
		planksBlock.fireSpreadSpeed = 5;
		planksBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, planksBlock);

		Block leavesBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Leaves", ModElementType.BLOCK), false).getElementFromGUI();
		leavesBlock.name = readableName + " Leaves";
		leavesBlock.blockBase = "Leaves";
		leavesBlock.hasTransparency = true;
		leavesBlock.texture = textureMap.get("leaves");
		leavesBlock.renderType = 11; // single texture
		leavesBlock.customModelName = "Single texture";
		leavesBlock.soundOnStep = new StepSound(workspace, "PLANT");
		leavesBlock.hardness = 0.2 * factor;
		leavesBlock.resistance = 0.2 * factor;
		leavesBlock.destroyTool = "hoe";
		leavesBlock.ignitedByLava = true;
		leavesBlock.flammability = 60;
		leavesBlock.fireSpreadSpeed = 30;
		leavesBlock.creativeTabs = List.of(new TabEntry(workspace, "DECORATIONS"));
		leavesBlock.reactionToPushing = "DESTROY";
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, leavesBlock);

		Block stairsBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Stairs", ModElementType.BLOCK), false).getElementFromGUI();
		stairsBlock.name = readableName + " Stairs";
		stairsBlock.blockBase = "Stairs";
		stairsBlock.texture = textureMap.get("planks");
		stairsBlock.textureTop = textureMap.get("planks");
		stairsBlock.textureFront = textureMap.get("planks");
		stairsBlock.renderType = 11; // single texture
		stairsBlock.customModelName = "Single texture";
		stairsBlock.soundOnStep = new StepSound(workspace, "WOOD");
		stairsBlock.hardness = 2 * factor;
		stairsBlock.resistance = 3 * factor;
		stairsBlock.destroyTool = "axe";
		stairsBlock.noteBlockInstrument = new NoteBlockInstrument(workspace, "bass");
		stairsBlock.ignitedByLava = true;
		stairsBlock.flammability = 20;
		stairsBlock.fireSpreadSpeed = 5;
		stairsBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, stairsBlock);

		Block slabBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Slab", ModElementType.BLOCK), false).getElementFromGUI();
		slabBlock.name = readableName + " Slab";
		slabBlock.blockBase = "Slab";
		slabBlock.texture = textureMap.get("planks");
		slabBlock.textureTop = textureMap.get("planks");
		slabBlock.textureFront = textureMap.get("planks");
		slabBlock.renderType = 11; // single texture
		slabBlock.customModelName = "Single texture";
		slabBlock.soundOnStep = new StepSound(workspace, "WOOD");
		slabBlock.hardness = 2 * factor;
		slabBlock.resistance = 3 * factor;
		slabBlock.destroyTool = "axe";
		slabBlock.noteBlockInstrument = new NoteBlockInstrument(workspace, "bass");
		slabBlock.ignitedByLava = true;
		slabBlock.flammability = 20;
		slabBlock.fireSpreadSpeed = 5;
		slabBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, slabBlock);

		Block fenceBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Fence", ModElementType.BLOCK), false).getElementFromGUI();
		fenceBlock.name = readableName + " Fence";
		fenceBlock.blockBase = "Fence";
		fenceBlock.texture = textureMap.get("planks");
		fenceBlock.renderType = 11; // single texture
		fenceBlock.customModelName = "Single texture";
		fenceBlock.soundOnStep = new StepSound(workspace, "WOOD");
		fenceBlock.hardness = 2 * factor;
		fenceBlock.resistance = 3 * factor;
		fenceBlock.destroyTool = "axe";
		fenceBlock.noteBlockInstrument = new NoteBlockInstrument(workspace, "bass");
		fenceBlock.ignitedByLava = true;
		fenceBlock.flammability = 20;
		fenceBlock.fireSpreadSpeed = 5;
		fenceBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, fenceBlock);

		Block fenceGateBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "FenceGate", ModElementType.BLOCK), false).getElementFromGUI();
		fenceGateBlock.name = readableName + " Fence Gate";
		fenceGateBlock.blockBase = "FenceGate";
		fenceGateBlock.texture = textureMap.get("planks");
		fenceGateBlock.renderType = 11; // single texture
		fenceGateBlock.customModelName = "Single texture";
		fenceGateBlock.soundOnStep = new StepSound(workspace, "WOOD");
		fenceGateBlock.hardness = 2 * factor;
		fenceGateBlock.resistance = 3 * factor;
		fenceGateBlock.destroyTool = "axe";
		fenceGateBlock.noteBlockInstrument = new NoteBlockInstrument(workspace, "bass");
		fenceGateBlock.ignitedByLava = true;
		fenceGateBlock.flammability = 20;
		fenceGateBlock.fireSpreadSpeed = 5;
		fenceGateBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, fenceGateBlock);

		Block doorBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Door", ModElementType.BLOCK), false).getElementFromGUI();
		doorBlock.name = readableName + " Door";
		doorBlock.blockBase = "Door";
		doorBlock.texture = textureMap.get("door_bottom");
		doorBlock.textureTop = textureMap.get("door_top");
		doorBlock.itemTexture = textureMap.get("door_item");
		doorBlock.renderType = 11; // single texture
		doorBlock.customModelName = "Single texture";
		doorBlock.hasTransparency = true;
		doorBlock.transparencyType = "CUTOUT";
		doorBlock.soundOnStep = new StepSound(workspace, "WOOD");
		doorBlock.hardness = 3 * factor;
		doorBlock.resistance = 3 * factor;
		doorBlock.destroyTool = "axe";
		doorBlock.noteBlockInstrument = new NoteBlockInstrument(workspace, "bass");
		doorBlock.ignitedByLava = true;
		doorBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, doorBlock);

		Block trapdoorBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Trapdoor", ModElementType.BLOCK), false).getElementFromGUI();
		trapdoorBlock.name = readableName + " Trapdoor";
		trapdoorBlock.blockBase = "TrapDoor";
		trapdoorBlock.texture = textureMap.get("trapdoor");
		trapdoorBlock.renderType = 11; // single texture
		trapdoorBlock.customModelName = "Single texture";
		trapdoorBlock.hasTransparency = true;
		trapdoorBlock.transparencyType = "CUTOUT";
		trapdoorBlock.soundOnStep = new StepSound(workspace, "WOOD");
		trapdoorBlock.hardness = 3 * factor;
		trapdoorBlock.resistance = 3 * factor;
		trapdoorBlock.destroyTool = "axe";
		trapdoorBlock.noteBlockInstrument = new NoteBlockInstrument(workspace, "bass");
		trapdoorBlock.ignitedByLava = true;
		trapdoorBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, trapdoorBlock);

		Block pressurePlateBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "PressurePlate", ModElementType.BLOCK), false).getElementFromGUI();
		pressurePlateBlock.name = readableName + " Pressure Plate";
		pressurePlateBlock.blockBase = "PressurePlate";
		pressurePlateBlock.texture = textureMap.get("planks");
		pressurePlateBlock.renderType = 11; // single texture
		pressurePlateBlock.customModelName = "Single texture";
		pressurePlateBlock.soundOnStep = new StepSound(workspace, "WOOD");
		pressurePlateBlock.hardness = 0.5 * factor;
		pressurePlateBlock.resistance = 0.5 * factor;
		pressurePlateBlock.destroyTool = "axe";
		pressurePlateBlock.noteBlockInstrument = new NoteBlockInstrument(workspace, "bass");
		pressurePlateBlock.ignitedByLava = true;
		pressurePlateBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		pressurePlateBlock.isNotColidable = true;
		pressurePlateBlock.reactionToPushing = "DESTROY";
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, pressurePlateBlock);

		Block buttonBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Button", ModElementType.BLOCK), false).getElementFromGUI();
		buttonBlock.name = readableName + " Button";
		buttonBlock.blockBase = "Button";
		buttonBlock.texture = textureMap.get("planks");
		buttonBlock.renderType = 11; // single texture
		buttonBlock.customModelName = "Single texture";
		buttonBlock.soundOnStep = new StepSound(workspace, "WOOD");
		buttonBlock.hardness = 0.5 * factor;
		buttonBlock.resistance = 0.5 * factor;
		buttonBlock.destroyTool = "axe";
		buttonBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		buttonBlock.isNotColidable = true;
		buttonBlock.reactionToPushing = "DESTROY";
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, buttonBlock);

		Block signBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Sign", ModElementType.BLOCK), false).getElementFromGUI();
		signBlock.name = readableName + " Sign";
		signBlock.blockBase = "Sign";
		signBlock.texture = textureMap.get("planks");
		signBlock.itemTexture = textureMap.get("sign_item");
		signBlock.signEntityTexture = textureMap.get("sign_entity");
		signBlock.renderType = 11; // single texture
		signBlock.customModelName = "Single texture";
		signBlock.maxStackSize = 16;
		signBlock.soundOnStep = new StepSound(workspace, "WOOD");
		signBlock.hardness = 1 * factor;
		signBlock.resistance = 1 * factor;
		signBlock.destroyTool = "axe";
		signBlock.noteBlockInstrument = new NoteBlockInstrument(workspace, "bass");
		signBlock.creativeTabs = List.of(new TabEntry(workspace, "TRANSPORTATION"));
		signBlock.isNotColidable = true;
		signBlock.ignitedByLava = true;
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, signBlock);

		Block hangingSignBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "HangingSign", ModElementType.BLOCK), false).getElementFromGUI();
		hangingSignBlock.name = readableName + " Hanging Sign";
		hangingSignBlock.blockBase = "HangingSign";
		hangingSignBlock.texture = textureMap.get("planks");
		hangingSignBlock.itemTexture = textureMap.get("hanging_sign_item");
		hangingSignBlock.signEntityTexture = textureMap.get("hanging_sign_entity");
		hangingSignBlock.signGUITexture = textureMap.get("hanging_sign_gui");
		hangingSignBlock.renderType = 11; // single texture
		hangingSignBlock.customModelName = "Single texture";
		hangingSignBlock.maxStackSize = 16;
		hangingSignBlock.soundOnStep = new StepSound(workspace, "HANGING_SIGN");
		hangingSignBlock.hardness = 1 * factor;
		hangingSignBlock.resistance = 1 * factor;
		hangingSignBlock.destroyTool = "axe";
		hangingSignBlock.noteBlockInstrument = new NoteBlockInstrument(workspace, "bass");
		hangingSignBlock.creativeTabs = List.of(new TabEntry(workspace, "TRANSPORTATION"));
		hangingSignBlock.isNotColidable = true;
		hangingSignBlock.ignitedByLava = true;
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, hangingSignBlock);

		// Boats
		SpecialEntity boat = (SpecialEntity) ModElementType.SPECIALENTITY.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Boat", ModElementType.SPECIALENTITY), false).getElementFromGUI();
		boat.name = readableName + " Boat";
		boat.entityType = "Boat";
		boat.entityTexture = textureMap.get("boat_entity");
		boat.itemTexture = textureMap.get("boat_item");
		boat.creativeTabs = List.of(new TabEntry(workspace, "TOOLS"));
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, boat);

		SpecialEntity chestBoat = (SpecialEntity) ModElementType.SPECIALENTITY.getModElementGUI(mcreator,
				new ModElement(workspace, name + "ChestBoat", ModElementType.SPECIALENTITY), false).getElementFromGUI();
		chestBoat.name = readableName + " Boat with Chest";
		chestBoat.entityType = "ChestBoat";
		chestBoat.entityTexture = textureMap.get("chest_boat_entity");
		chestBoat.itemTexture = textureMap.get("chest_boat_item");
		chestBoat.creativeTabs = List.of(new TabEntry(workspace, "TOOLS"));
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, chestBoat);

		// Item extension to make generated leaves compostable
		ItemExtension leavesComposting = (ItemExtension) ModElementType.ITEMEXTENSION.getModElementGUI(mcreator,
						new ModElement(workspace, name + "LeavesComposting", ModElementType.ITEMEXTENSION), false)
				.getElementFromGUI();
		leavesComposting.item = new MItemBlock(workspace, "CUSTOM:" + name + "Leaves");
		leavesComposting.compostLayerChance = 0.3;
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, leavesComposting);

		// Tags
		String planksEntry = "CUSTOM:" + name + "Planks";
		String logEntry = "CUSTOM:" + name + "Log";
		String woodEntry = "CUSTOM:" + name + "Wood";
		String strippedLogEntry = "CUSTOM:Stripped" + name + "Log";
		String strippedWoodEntry = "CUSTOM:Stripped" + name + "Wood";
		addTagEntries(workspace, TagType.BLOCKS, "mod:" + registryName + "_logs", logEntry, woodEntry, strippedLogEntry,
				strippedWoodEntry);
		addTagEntries(workspace, TagType.BLOCKS, "minecraft:logs_that_burn", "TAG:mod:" + registryName + "_logs");
		addTagEntries(workspace, TagType.BLOCKS, "minecraft:planks", planksEntry);
		addTagEntries(workspace, TagType.ITEMS, "mod:" + registryName + "_logs", logEntry, woodEntry, strippedLogEntry,
				strippedWoodEntry);
		addTagEntries(workspace, TagType.ITEMS, "minecraft:logs_that_burn", "TAG:mod:" + registryName + "_logs");
		addTagEntries(workspace, TagType.ITEMS, "minecraft:planks", planksEntry);

		// Recipes
		Recipe woodRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "WoodRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		woodRecipe.craftingBookCategory = "BUILDING";
		woodRecipe.group = "bark";
		woodRecipe.recipeSlots[0] = new MItemBlock(workspace, logEntry);
		woodRecipe.recipeSlots[1] = new MItemBlock(workspace, logEntry);
		woodRecipe.recipeSlots[3] = new MItemBlock(workspace, logEntry);
		woodRecipe.recipeSlots[4] = new MItemBlock(workspace, logEntry);
		woodRecipe.recipeReturnStack = new MItemBlock(workspace, woodEntry);
		woodRecipe.recipeRetstackSize = 3;
		woodRecipe.unlockingItems.add(new MItemBlock(workspace, logEntry));
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, woodRecipe);

		Recipe strippedWoodRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
						new ModElement(workspace, "Stripped" + name + "WoodRecipe", ModElementType.RECIPE), false)
				.getElementFromGUI();
		strippedWoodRecipe.craftingBookCategory = "BUILDING";
		strippedWoodRecipe.group = "bark";
		strippedWoodRecipe.recipeSlots[0] = new MItemBlock(workspace, strippedLogEntry);
		strippedWoodRecipe.recipeSlots[1] = new MItemBlock(workspace, strippedLogEntry);
		strippedWoodRecipe.recipeSlots[3] = new MItemBlock(workspace, strippedLogEntry);
		strippedWoodRecipe.recipeSlots[4] = new MItemBlock(workspace, strippedLogEntry);
		strippedWoodRecipe.recipeReturnStack = new MItemBlock(workspace, strippedWoodEntry);
		strippedWoodRecipe.recipeRetstackSize = 3;
		strippedWoodRecipe.unlockingItems.add(new MItemBlock(workspace, strippedLogEntry));
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, strippedWoodRecipe);

		Recipe planksLogRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "PlanksRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		planksLogRecipe.craftingBookCategory = "BUILDING";
		planksLogRecipe.group = "planks";
		planksLogRecipe.recipeSlots[4] = new MItemBlock(workspace,
				"TAG:" + workspace.getWorkspaceSettings().getModID() + ":" + registryName + "_logs");
		planksLogRecipe.recipeReturnStack = new MItemBlock(workspace, planksEntry);
		planksLogRecipe.recipeShapeless = true;
		planksLogRecipe.recipeRetstackSize = 4;
		planksLogRecipe.unlockingItems.add(new MItemBlock(workspace,
				"TAG:" + workspace.getWorkspaceSettings().getModID() + ":" + registryName + "_logs"));
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, planksLogRecipe);

		Recipe stairsRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "StairsRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		stairsRecipe.craftingBookCategory = "BUILDING";
		stairsRecipe.group = "wooden_stairs";
		stairsRecipe.recipeSlots[0] = new MItemBlock(workspace, planksEntry);
		stairsRecipe.recipeSlots[3] = new MItemBlock(workspace, planksEntry);
		stairsRecipe.recipeSlots[4] = new MItemBlock(workspace, planksEntry);
		stairsRecipe.recipeSlots[6] = new MItemBlock(workspace, planksEntry);
		stairsRecipe.recipeSlots[7] = new MItemBlock(workspace, planksEntry);
		stairsRecipe.recipeSlots[8] = new MItemBlock(workspace, planksEntry);
		stairsRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Stairs");
		stairsRecipe.recipeRetstackSize = 4;
		stairsRecipe.unlockingItems.add(new MItemBlock(workspace, planksEntry));
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, stairsRecipe);

		Recipe slabRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "SlabRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		slabRecipe.craftingBookCategory = "BUILDING";
		slabRecipe.group = "wooden_slab";
		slabRecipe.recipeSlots[6] = new MItemBlock(workspace, planksEntry);
		slabRecipe.recipeSlots[7] = new MItemBlock(workspace, planksEntry);
		slabRecipe.recipeSlots[8] = new MItemBlock(workspace, planksEntry);
		slabRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Slab");
		slabRecipe.recipeRetstackSize = 6;
		slabRecipe.unlockingItems.add(new MItemBlock(workspace, planksEntry));
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, slabRecipe);

		Recipe fenceRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "FenceRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		fenceRecipe.group = "wooden_fence";
		fenceRecipe.recipeSlots[3] = new MItemBlock(workspace, planksEntry);
		fenceRecipe.recipeSlots[4] = new MItemBlock(workspace, "Items.STICK");
		fenceRecipe.recipeSlots[5] = new MItemBlock(workspace, planksEntry);
		fenceRecipe.recipeSlots[6] = new MItemBlock(workspace, planksEntry);
		fenceRecipe.recipeSlots[7] = new MItemBlock(workspace, "Items.STICK");
		fenceRecipe.recipeSlots[8] = new MItemBlock(workspace, planksEntry);
		fenceRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Fence");
		fenceRecipe.recipeRetstackSize = 3;
		fenceRecipe.unlockingItems.add(new MItemBlock(workspace, planksEntry));
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, fenceRecipe);

		Recipe fenceGateRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "FenceGateRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		fenceGateRecipe.craftingBookCategory = "REDSTONE";
		fenceGateRecipe.group = "wooden_fence_gate";
		fenceGateRecipe.recipeSlots[3] = new MItemBlock(workspace, "Items.STICK");
		fenceGateRecipe.recipeSlots[4] = new MItemBlock(workspace, planksEntry);
		fenceGateRecipe.recipeSlots[5] = new MItemBlock(workspace, "Items.STICK");
		fenceGateRecipe.recipeSlots[6] = new MItemBlock(workspace, "Items.STICK");
		fenceGateRecipe.recipeSlots[7] = new MItemBlock(workspace, planksEntry);
		fenceGateRecipe.recipeSlots[8] = new MItemBlock(workspace, "Items.STICK");
		fenceGateRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "FenceGate");
		fenceGateRecipe.recipeRetstackSize = 1;
		fenceGateRecipe.unlockingItems.add(new MItemBlock(workspace, planksEntry));
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, fenceGateRecipe);

		Recipe doorRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "DoorRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		doorRecipe.craftingBookCategory = "REDSTONE";
		doorRecipe.group = "wooden_door";
		doorRecipe.recipeSlots[0] = new MItemBlock(workspace, planksEntry);
		doorRecipe.recipeSlots[1] = new MItemBlock(workspace, planksEntry);
		doorRecipe.recipeSlots[3] = new MItemBlock(workspace, planksEntry);
		doorRecipe.recipeSlots[4] = new MItemBlock(workspace, planksEntry);
		doorRecipe.recipeSlots[6] = new MItemBlock(workspace, planksEntry);
		doorRecipe.recipeSlots[7] = new MItemBlock(workspace, planksEntry);
		doorRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Door");
		doorRecipe.recipeRetstackSize = 3;
		doorRecipe.unlockingItems.add(new MItemBlock(workspace, planksEntry));
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, doorRecipe);

		Recipe trapdoorRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "TrapdoorRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		trapdoorRecipe.craftingBookCategory = "REDSTONE";
		trapdoorRecipe.group = "wooden_trapdoor";
		trapdoorRecipe.recipeSlots[0] = new MItemBlock(workspace, planksEntry);
		trapdoorRecipe.recipeSlots[1] = new MItemBlock(workspace, planksEntry);
		trapdoorRecipe.recipeSlots[2] = new MItemBlock(workspace, planksEntry);
		trapdoorRecipe.recipeSlots[3] = new MItemBlock(workspace, planksEntry);
		trapdoorRecipe.recipeSlots[4] = new MItemBlock(workspace, planksEntry);
		trapdoorRecipe.recipeSlots[5] = new MItemBlock(workspace, planksEntry);
		trapdoorRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Trapdoor");
		trapdoorRecipe.recipeRetstackSize = 2;
		trapdoorRecipe.unlockingItems.add(new MItemBlock(workspace, planksEntry));
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, trapdoorRecipe);

		Recipe pressurePlateRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
						new ModElement(workspace, name + "PressurePlateRecipe", ModElementType.RECIPE), false)
				.getElementFromGUI();
		pressurePlateRecipe.craftingBookCategory = "REDSTONE";
		pressurePlateRecipe.group = "wooden_pressure_plate";
		pressurePlateRecipe.recipeSlots[6] = new MItemBlock(workspace, planksEntry);
		pressurePlateRecipe.recipeSlots[7] = new MItemBlock(workspace, planksEntry);
		pressurePlateRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "PressurePlate");
		pressurePlateRecipe.recipeRetstackSize = 1;
		pressurePlateRecipe.unlockingItems.add(new MItemBlock(workspace, planksEntry));
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, pressurePlateRecipe);

		Recipe buttonRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "ButtonRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		buttonRecipe.craftingBookCategory = "REDSTONE";
		buttonRecipe.group = "wooden_button";
		buttonRecipe.recipeSlots[4] = new MItemBlock(workspace, planksEntry);
		buttonRecipe.recipeShapeless = true;
		buttonRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Button");
		buttonRecipe.recipeRetstackSize = 1;
		buttonRecipe.unlockingItems.add(new MItemBlock(workspace, planksEntry));
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, buttonRecipe);

		Recipe signRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "SignRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		signRecipe.craftingBookCategory = "MISC";
		signRecipe.group = "wooden_sign";
		signRecipe.recipeSlots[0] = new MItemBlock(workspace, planksEntry);
		signRecipe.recipeSlots[1] = new MItemBlock(workspace, planksEntry);
		signRecipe.recipeSlots[2] = new MItemBlock(workspace, planksEntry);
		signRecipe.recipeSlots[3] = new MItemBlock(workspace, planksEntry);
		signRecipe.recipeSlots[4] = new MItemBlock(workspace, planksEntry);
		signRecipe.recipeSlots[5] = new MItemBlock(workspace, planksEntry);
		signRecipe.recipeSlots[7] = new MItemBlock(workspace, "Items.STICK");
		signRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Sign");
		signRecipe.recipeRetstackSize = 3;
		signRecipe.unlockingItems.add(new MItemBlock(workspace, planksEntry));
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, signRecipe);

		Recipe hangingSignRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
						new ModElement(workspace, name + "HangingSignRecipe", ModElementType.RECIPE), false)
				.getElementFromGUI();
		hangingSignRecipe.craftingBookCategory = "MISC";
		hangingSignRecipe.group = "hanging_sign";
		hangingSignRecipe.recipeSlots[0] = new MItemBlock(workspace, "Blocks.CHAIN");
		hangingSignRecipe.recipeSlots[2] = new MItemBlock(workspace, "Blocks.CHAIN");
		hangingSignRecipe.recipeSlots[3] = new MItemBlock(workspace, strippedLogEntry);
		hangingSignRecipe.recipeSlots[4] = new MItemBlock(workspace, strippedLogEntry);
		hangingSignRecipe.recipeSlots[5] = new MItemBlock(workspace, strippedLogEntry);
		hangingSignRecipe.recipeSlots[6] = new MItemBlock(workspace, strippedLogEntry);
		hangingSignRecipe.recipeSlots[7] = new MItemBlock(workspace, strippedLogEntry);
		hangingSignRecipe.recipeSlots[8] = new MItemBlock(workspace, strippedLogEntry);
		hangingSignRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "HangingSign");
		hangingSignRecipe.recipeRetstackSize = 6;
		hangingSignRecipe.unlockingItems.add(new MItemBlock(workspace, strippedLogEntry));
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, hangingSignRecipe);

		Recipe boatRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "BoatRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		boatRecipe.craftingBookCategory = "MISC";
		boatRecipe.group = "boat";
		boatRecipe.recipeSlots[0] = new MItemBlock(workspace, planksEntry);
		boatRecipe.recipeSlots[2] = new MItemBlock(workspace, planksEntry);
		boatRecipe.recipeSlots[3] = new MItemBlock(workspace, planksEntry);
		boatRecipe.recipeSlots[4] = new MItemBlock(workspace, planksEntry);
		boatRecipe.recipeSlots[5] = new MItemBlock(workspace, planksEntry);
		boatRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Boat");
		boatRecipe.recipeRetstackSize = 1;
		boatRecipe.unlockingItems.add(new MItemBlock(workspace, planksEntry));
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, boatRecipe);

		Recipe chestBoatRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "ChestBoatRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		chestBoatRecipe.craftingBookCategory = "MISC";
		chestBoatRecipe.group = "chest_boat";
		chestBoatRecipe.recipeSlots[0] = new MItemBlock(workspace, "CUSTOM:" + name + "Boat");
		chestBoatRecipe.recipeSlots[1] = new MItemBlock(workspace, "Blocks.CHEST");
		chestBoatRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "ChestBoat");
		chestBoatRecipe.recipeShapeless = true;
		chestBoatRecipe.recipeRetstackSize = 1;
		chestBoatRecipe.unlockingItems.add(new MItemBlock(workspace, "CUSTOM:" + name + "Boat"));
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, chestBoatRecipe);

		return true;
	}

	private Map<String, TextureHolder> makeTextureMap() {
		HashMap<String, TextureHolder> map = new HashMap<>();

		addToTextureMap(map, logSideTexture, "log_side");
		addToTextureMap(map, logTopTexture, "log_top");
		addToTextureMap(map, strippedLogSideTexture, "stripped_log_side");
		addToTextureMap(map, strippedLogTopTexture, "stripped_log_top");
		addToTextureMap(map, planksTexture, "planks");
		addToTextureMap(map, leavesTexture, "leaves");
		addToTextureMap(map, doorTopTexture, "door_top");
		addToTextureMap(map, doorBottomTexture, "door_bottom");
		addToTextureMap(map, doorItemTexture, "door_item");
		addToTextureMap(map, trapdoorTexture, "trapdoor");
		addToTextureMap(map, signItemTexture, "sign_item");
		addToTextureMap(map, signEntityTexture, "sign_entity");
		addToTextureMap(map, hangingSignItemTexture, "hanging_sign_item");
		addToTextureMap(map, hangingSignEntityTexture, "hanging_sign_entity");
		addToTextureMap(map, hangingSignGUITexture, "hanging_sign_gui");
		addToTextureMap(map, boatItemTexture, "boat_item");
		addToTextureMap(map, boatEntityTexture, "boat_entity");
		addToTextureMap(map, chestBoatItemTexture, "chest_boat_item");
		addToTextureMap(map, chestBoatEntityTexture, "chest_boat_entity");

		return map;
	}

	public static boolean isSupported(GeneratorConfiguration gc) {
		return gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.RECIPE)
				!= GeneratorStats.CoverageStatus.NONE
				&& gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.BLOCK)
				!= GeneratorStats.CoverageStatus.NONE
				&& gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.SPECIALENTITY)
				!= GeneratorStats.CoverageStatus.NONE
				&& gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.ITEMEXTENSION)
				!= GeneratorStats.CoverageStatus.NONE;
	}

	public static BasicAction getAction(ActionRegistry actionRegistry) {
		return new BasicAction(actionRegistry, L10N.t("action.pack_tools.wood"),
				_ -> new WoodPackMakerTool(actionRegistry.getMCreator())) {
			@Override public boolean isEnabled() {
				return isSupported(actionRegistry.getMCreator().getGeneratorConfiguration());
			}
		}.setIcon(UIRES.get("16px.woodpack"));
	}

}
