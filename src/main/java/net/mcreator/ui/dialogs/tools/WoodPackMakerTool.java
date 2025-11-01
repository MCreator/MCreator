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
import net.mcreator.element.parts.StepSound;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.parts.TextureHolder;
import net.mcreator.element.types.Block;
import net.mcreator.element.types.Recipe;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.io.FileIO;
import net.mcreator.io.ResourcePointer;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.minecraft.TagType;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.BasicAction;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.ImageMakerTexturesCache;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.ModElementNameValidator;
import net.mcreator.ui.variants.modmaker.ModMaker;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.ListUtils;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WoodPackMakerTool extends AbstractPackMakerTool {

	private final VTextField name = new VTextField(25);
	private final JColor color;
	private final JSpinner power = new JSpinner(new SpinnerNumberModel(1, 0.1, 10, 0.1));

	private WoodPackMakerTool(MCreator mcreator) {
		super(mcreator, "wood_pack", UIRES.get("16px.woodpack").getImage());

		JPanel props = new JPanel(new GridLayout(4, 2, 5, 2));

		color = new JColor(mcreator, false, false);
		name.enableRealtimeValidation();

		props.add(L10N.label("dialog.tools.wood_pack_name"));
		props.add(name);

		props.add(L10N.label("dialog.tools.wood_pack_color_accent"));
		props.add(color);

		props.add(L10N.label("dialog.tools.wood_pack_power_factor"));
		props.add(power);

		name.setValidator(new ModElementNameValidator(mcreator.getWorkspace(), name,
				L10N.t("dialog.tools.wood_pack_name_validator")));

		validableElements.addValidationElement(name);

		this.add("Center", PanelUtils.centerInPanel(props));

		this.setSize(600, 260);
		this.setLocationRelativeTo(mcreator);
		this.setVisible(true);
	}

	@Override protected void generatePack(MCreator mcreator) {
		addWoodPackToWorkspace(mcreator, mcreator.getWorkspace(), name.getText(), color.getColor(),
				(Double) power.getValue());
	}

	public static void addWoodPackToWorkspace(MCreator mcreator, Workspace workspace, String name, Color color,
			double factor) {
		String registryName = RegistryNameFixer.fromCamelCase(name);
		String readableName = StringUtils.machineToReadableName(name);

		// Use a slightly desaturated, darker color for stripped log textures
		float[] colorHSB = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
		Color strippedColor = Color.getHSBColor(colorHSB[0], colorHSB[1] * 0.9f, colorHSB[2] * 0.85f);

		if (!checkIfNamesAvailable(workspace, name + "Wood", name + "Log",
				"Stripped" + name + "Wood", "Stripped" + name + "Log", name + "Planks", name + "Leaves",
				name + "Stairs", name + "Slab", name + "Fence", name + "FenceGate", name + "Door", name + "Trapdoor",
				name + "PressurePlate", name + "Button", name + "WoodRecipe", "Stripped" + name + "WoodRecipe",
				name + "PlanksRecipe", name + "StairsRecipe", name + "SlabRecipe", name + "FenceRecipe",
				name + "FenceGateRecipe", name + "DoorRecipe", name + "TrapdoorRecipe", name + "PressurePlateRecipe",
				name + "ButtonRecipe"))
			return;

		// select folder the mod pack should be in
		FolderElement folder = mcreator instanceof ModMaker modMaker ?
				modMaker.getWorkspacePanel().currentFolder :
				null;

		// first we generate wood texture
		ImageIcon wood = ImageUtils.colorize(ImageMakerTexturesCache.CACHE.get(new ResourcePointer(
						"templates/textures/texturemaker/" + ListUtils.getRandomItem(
								Arrays.asList("log_side_1", "log_side_2", "log_side_3", "log_side_4", "log_side_5")) + ".png")),
				color, true);
		String woodTextureName = registryName + "_log";
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(wood.getImage()),
				mcreator.getFolderManager().getTextureFile(woodTextureName, TextureType.BLOCK));

		//then we generate the missing log texture
		ImageIcon log = ImageUtils.colorize(
				ImageMakerTexturesCache.CACHE.get(new ResourcePointer("templates/textures/texturemaker/log_top.png")),
				color, true);
		String logTextureName = registryName + "_log_top";
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(log.getImage()),
				mcreator.getFolderManager().getTextureFile(logTextureName, TextureType.BLOCK));

		// then we generate the stripped log side texture
		ImageIcon strippedLogSide = ImageUtils.colorize(ImageMakerTexturesCache.CACHE.get(
				new ResourcePointer("templates/textures/texturemaker/stripped_log_side.png")), strippedColor, true);
		String strippedLogSideTextureName = "stripped_" + registryName + "_log";
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(strippedLogSide.getImage()),
				mcreator.getFolderManager().getTextureFile(strippedLogSideTextureName, TextureType.BLOCK));

		// then we generate the stripped log top texture
		ImageIcon strippedLogTop = ImageUtils.drawOver(log, ImageUtils.colorize(ImageMakerTexturesCache.CACHE.get(
						new ResourcePointer("templates/textures/texturemaker/stripped_log_top_outside.png")), strippedColor,
				true));
		String strippedLogTopTextureName = "stripped_" + registryName + "_log_top";
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(strippedLogTop.getImage()),
				mcreator.getFolderManager().getTextureFile(strippedLogTopTextureName, TextureType.BLOCK));

		//then we generate the planks texture
		ImageIcon planks = ImageUtils.colorize(ImageMakerTexturesCache.CACHE.get(new ResourcePointer(
				"templates/textures/texturemaker/" + ListUtils.getRandomItem(Arrays.asList("planks_0", "planks_1"))
						+ ".png")), color, true);
		String planksTextureName = registryName + "_planks";
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(planks.getImage()),
				mcreator.getFolderManager().getTextureFile(planksTextureName, TextureType.BLOCK));

		//then we generate the leaves texture
		ImageIcon leaves = ImageUtils.colorize(ImageMakerTexturesCache.CACHE.get(new ResourcePointer(
				"templates/textures/texturemaker/" + ListUtils.getRandomItem(
						Arrays.asList("leaves_0", "leaves_1", "leaves_2", "leaves_3", "leaves_4", "leaves_5",
								"leaves_new1", "leaves_new2", "leaves2")) + ".png")), color, true);
		String leavesTextureName = registryName + "_leaves";
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(leaves.getImage()),
				mcreator.getFolderManager().getTextureFile(leavesTextureName, TextureType.BLOCK));

		// Generate door and trapdoor textures (matching textures have the same suffix)
		int doorSuffix = new Random().nextInt(2) + 1;

		ImageIcon doorBottom = ImageUtils.colorize(ImageMakerTexturesCache.CACHE.get(
						new ResourcePointer("templates/textures/texturemaker/door_bottom_" + doorSuffix + ".png")), color,
				true);
		doorBottom = ImageUtils.drawOver(doorBottom, ImageMakerTexturesCache.CACHE.get(
				new ResourcePointer("templates/textures/texturemaker/door_hinges_bottom.png")));
		String doorBottomTextureName = registryName + "_door_bottom";
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(doorBottom.getImage()),
				mcreator.getFolderManager().getTextureFile(doorBottomTextureName, TextureType.BLOCK));

		ImageIcon doorTop = ImageUtils.colorize(ImageMakerTexturesCache.CACHE.get(
				new ResourcePointer("templates/textures/texturemaker/door_top_" + doorSuffix + ".png")), color, true);
		doorTop = ImageUtils.drawOver(doorTop, ImageMakerTexturesCache.CACHE.get(
				new ResourcePointer("templates/textures/texturemaker/door_hinges_top.png")));
		String doorTopTextureName = registryName + "_door_top";
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(doorTop.getImage()),
				mcreator.getFolderManager().getTextureFile(doorTopTextureName, TextureType.BLOCK));

		ImageIcon doorItem = ImageUtils.colorize(ImageMakerTexturesCache.CACHE.get(
				new ResourcePointer("templates/textures/texturemaker/door_item_" + doorSuffix + ".png")), color, true);
		String doorItemTextureName = registryName + "_door_item";
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(doorItem.getImage()),
				mcreator.getFolderManager().getTextureFile(doorItemTextureName, TextureType.ITEM));

		ImageIcon trapdoor = ImageUtils.colorize(ImageMakerTexturesCache.CACHE.get(
				new ResourcePointer("templates/textures/texturemaker/trapdoor_" + doorSuffix + ".png")), color, true);
		String trapdoorTextureName = registryName + "_trapdoor";
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(trapdoor.getImage()),
				mcreator.getFolderManager().getTextureFile(trapdoorTextureName, TextureType.BLOCK));

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block logBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Log", ModElementType.BLOCK), false).getElementFromGUI();
		logBlock.name = readableName + " Log";
		logBlock.texture = new TextureHolder(workspace, logTextureName);
		logBlock.textureTop = new TextureHolder(workspace, logTextureName);
		logBlock.textureBack = new TextureHolder(workspace, woodTextureName);
		logBlock.textureFront = new TextureHolder(workspace, woodTextureName);
		logBlock.textureLeft = new TextureHolder(workspace, woodTextureName);
		logBlock.textureRight = new TextureHolder(workspace, woodTextureName);
		logBlock.renderType = 10; // normal
		logBlock.customModelName = "Normal";
		logBlock.soundOnStep = new StepSound(workspace, "WOOD");
		logBlock.hardness = 2.0 * factor;
		logBlock.resistance = 2.0 * Math.pow(factor, 0.8);
		logBlock.destroyTool = "axe";
		logBlock.noteBlockInstrument = "bass";
		logBlock.ignitedByLava = true;
		logBlock.flammability = 5;
		logBlock.fireSpreadSpeed = 5;
		logBlock.rotationMode = 5; // log rotation
		logBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		addGeneratableElementToWorkspace(workspace, folder, logBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block woodBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Wood", ModElementType.BLOCK), false).getElementFromGUI();
		woodBlock.name = readableName + " Wood";
		woodBlock.texture = new TextureHolder(workspace, woodTextureName);
		woodBlock.renderType = 11; // single texture
		woodBlock.customModelName = "Single texture";
		woodBlock.soundOnStep = new StepSound(workspace, "WOOD");
		woodBlock.hardness = 2.0 * factor;
		woodBlock.resistance = 2.0 * Math.pow(factor, 0.8);
		woodBlock.destroyTool = "axe";
		woodBlock.noteBlockInstrument = "bass";
		woodBlock.ignitedByLava = true;
		woodBlock.flammability = 5;
		woodBlock.fireSpreadSpeed = 5;
		woodBlock.rotationMode = 5; // log rotation
		woodBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		addGeneratableElementToWorkspace(workspace, folder, woodBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block strippedLogBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, "Stripped" + name + "Log", ModElementType.BLOCK), false).getElementFromGUI();
		strippedLogBlock.name = "Stripped " + readableName + " Log";
		strippedLogBlock.texture = new TextureHolder(workspace, strippedLogTopTextureName);
		strippedLogBlock.textureTop = new TextureHolder(workspace, strippedLogTopTextureName);
		strippedLogBlock.textureBack = new TextureHolder(workspace, strippedLogSideTextureName);
		strippedLogBlock.textureFront = new TextureHolder(workspace, strippedLogSideTextureName);
		strippedLogBlock.textureLeft = new TextureHolder(workspace, strippedLogSideTextureName);
		strippedLogBlock.textureRight = new TextureHolder(workspace, strippedLogSideTextureName);
		strippedLogBlock.renderType = 10; // normal
		strippedLogBlock.customModelName = "Normal";
		strippedLogBlock.soundOnStep = new StepSound(workspace, "WOOD");
		strippedLogBlock.hardness = 2.0 * factor;
		strippedLogBlock.resistance = 2.0 * Math.pow(factor, 0.8);
		strippedLogBlock.destroyTool = "axe";
		strippedLogBlock.noteBlockInstrument = "bass";
		strippedLogBlock.ignitedByLava = true;
		strippedLogBlock.flammability = 5;
		strippedLogBlock.fireSpreadSpeed = 5;
		strippedLogBlock.rotationMode = 5; // log rotation
		strippedLogBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		addGeneratableElementToWorkspace(workspace, folder, strippedLogBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block strippedWoodBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, "Stripped" + name + "Wood", ModElementType.BLOCK), false).getElementFromGUI();
		strippedWoodBlock.name = "Stripped " + readableName + " Wood";
		strippedWoodBlock.texture = new TextureHolder(workspace, strippedLogSideTextureName);
		strippedWoodBlock.renderType = 11; // single texture
		strippedWoodBlock.customModelName = "Single texture";
		strippedWoodBlock.soundOnStep = new StepSound(workspace, "WOOD");
		strippedWoodBlock.hardness = 2.0 * factor;
		strippedWoodBlock.resistance = 2.0 * Math.pow(factor, 0.8);
		strippedWoodBlock.destroyTool = "axe";
		strippedWoodBlock.noteBlockInstrument = "bass";
		strippedWoodBlock.ignitedByLava = true;
		strippedWoodBlock.flammability = 5;
		strippedWoodBlock.fireSpreadSpeed = 5;
		strippedWoodBlock.rotationMode = 5; // log rotation
		strippedWoodBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		addGeneratableElementToWorkspace(workspace, folder, strippedWoodBlock);

		// we update stripping results of log blocks *after* we add the stripped variants to the workspace
		logBlock.strippingResult = new MItemBlock(workspace, "CUSTOM:Stripped" + name + "Log");
		woodBlock.strippingResult = new MItemBlock(workspace, "CUSTOM:Stripped" + name + "Wood");
		workspace.getGenerator().generateElement(logBlock);
		workspace.getModElementManager().storeModElement(logBlock);
		workspace.getGenerator().generateElement(woodBlock);
		workspace.getModElementManager().storeModElement(woodBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block planksBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Planks", ModElementType.BLOCK), false).getElementFromGUI();
		planksBlock.name = readableName + " Planks";
		planksBlock.texture = new TextureHolder(workspace, planksTextureName);
		planksBlock.renderType = 11; // single texture
		planksBlock.customModelName = "Single texture";
		planksBlock.soundOnStep = new StepSound(workspace, "WOOD");
		planksBlock.hardness = 2.0 * factor;
		planksBlock.resistance = 3.0 * Math.pow(factor, 0.8);
		planksBlock.destroyTool = "axe";
		planksBlock.noteBlockInstrument = "bass";
		planksBlock.ignitedByLava = true;
		planksBlock.flammability = 20;
		planksBlock.fireSpreadSpeed = 5;
		planksBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		addGeneratableElementToWorkspace(workspace, folder, planksBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block leavesBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Leaves", ModElementType.BLOCK), false).getElementFromGUI();
		leavesBlock.name = readableName + " Leaves";
		leavesBlock.blockBase = "Leaves";
		leavesBlock.hasTransparency = true;
		leavesBlock.texture = new TextureHolder(workspace, leavesTextureName);
		leavesBlock.soundOnStep = new StepSound(workspace, "PLANT");
		leavesBlock.hardness = 0.2 * factor;
		leavesBlock.resistance = 0.2 * factor;
		leavesBlock.ignitedByLava = true;
		leavesBlock.flammability = 60;
		leavesBlock.fireSpreadSpeed = 30;
		leavesBlock.lightOpacity = 1;
		leavesBlock.creativeTabs = List.of(new TabEntry(workspace, "DECORATIONS"));
		leavesBlock.reactionToPushing = "DESTROY";
		addGeneratableElementToWorkspace(workspace, folder, leavesBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block stairsBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Stairs", ModElementType.BLOCK), false).getElementFromGUI();
		stairsBlock.name = readableName + " Stairs";
		stairsBlock.blockBase = "Stairs";
		stairsBlock.texture = new TextureHolder(workspace, planksTextureName);
		stairsBlock.textureTop = new TextureHolder(workspace, planksTextureName);
		stairsBlock.textureFront = new TextureHolder(workspace, planksTextureName);
		stairsBlock.soundOnStep = new StepSound(workspace, "WOOD");
		stairsBlock.hardness = 2 * factor;
		stairsBlock.resistance = 3 * factor;
		stairsBlock.destroyTool = "axe";
		stairsBlock.noteBlockInstrument = "bass";
		stairsBlock.ignitedByLava = true;
		stairsBlock.flammability = 20;
		stairsBlock.fireSpreadSpeed = 5;
		stairsBlock.lightOpacity = 0;
		stairsBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		addGeneratableElementToWorkspace(workspace, folder, stairsBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block slabBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Slab", ModElementType.BLOCK), false).getElementFromGUI();
		slabBlock.name = readableName + " Slab";
		slabBlock.blockBase = "Slab";
		slabBlock.texture = new TextureHolder(workspace, planksTextureName);
		slabBlock.textureTop = new TextureHolder(workspace, planksTextureName);
		slabBlock.textureFront = new TextureHolder(workspace, planksTextureName);
		slabBlock.soundOnStep = new StepSound(workspace, "WOOD");
		slabBlock.hardness = 2 * factor;
		slabBlock.resistance = 3 * factor;
		slabBlock.destroyTool = "axe";
		slabBlock.noteBlockInstrument = "bass";
		slabBlock.ignitedByLava = true;
		slabBlock.flammability = 20;
		slabBlock.fireSpreadSpeed = 5;
		slabBlock.lightOpacity = 0;
		slabBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		addGeneratableElementToWorkspace(workspace, folder, slabBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block fenceBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Fence", ModElementType.BLOCK), false).getElementFromGUI();
		fenceBlock.name = readableName + " Fence";
		fenceBlock.blockBase = "Fence";
		fenceBlock.texture = new TextureHolder(workspace, planksTextureName);
		fenceBlock.soundOnStep = new StepSound(workspace, "WOOD");
		fenceBlock.hardness = 2 * factor;
		fenceBlock.resistance = 3 * factor;
		fenceBlock.destroyTool = "axe";
		fenceBlock.noteBlockInstrument = "bass";
		fenceBlock.ignitedByLava = true;
		fenceBlock.flammability = 20;
		fenceBlock.fireSpreadSpeed = 5;
		fenceBlock.lightOpacity = 0;
		fenceBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		addGeneratableElementToWorkspace(workspace, folder, fenceBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block fenceGateBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "FenceGate", ModElementType.BLOCK), false).getElementFromGUI();
		fenceGateBlock.name = readableName + " Fence Gate";
		fenceGateBlock.blockBase = "FenceGate";
		fenceGateBlock.texture = new TextureHolder(workspace, planksTextureName);
		fenceGateBlock.soundOnStep = new StepSound(workspace, "WOOD");
		fenceGateBlock.hardness = 2 * factor;
		fenceGateBlock.resistance = 3 * factor;
		fenceGateBlock.destroyTool = "axe";
		fenceGateBlock.noteBlockInstrument = "bass";
		fenceGateBlock.ignitedByLava = true;
		fenceGateBlock.flammability = 20;
		fenceGateBlock.fireSpreadSpeed = 5;
		fenceGateBlock.lightOpacity = 0;
		fenceGateBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		addGeneratableElementToWorkspace(workspace, folder, fenceGateBlock);

		Block doorBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Door", ModElementType.BLOCK), false).getElementFromGUI();
		doorBlock.name = readableName + " Door";
		doorBlock.blockBase = "Door";
		doorBlock.texture = new TextureHolder(workspace, doorBottomTextureName);
		doorBlock.textureTop = new TextureHolder(workspace, doorTopTextureName);
		doorBlock.itemTexture = new TextureHolder(workspace, doorItemTextureName);
		doorBlock.hasTransparency = true;
		doorBlock.transparencyType = "CUTOUT";
		doorBlock.soundOnStep = new StepSound(workspace, "WOOD");
		doorBlock.hardness = 3 * factor;
		doorBlock.resistance = 3 * factor;
		doorBlock.destroyTool = "axe";
		doorBlock.noteBlockInstrument = "bass";
		doorBlock.ignitedByLava = true;
		doorBlock.lightOpacity = 0;
		doorBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		addGeneratableElementToWorkspace(workspace, folder, doorBlock);

		Block trapdoorBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Trapdoor", ModElementType.BLOCK), false).getElementFromGUI();
		trapdoorBlock.name = readableName + " Trapdoor";
		trapdoorBlock.blockBase = "TrapDoor";
		trapdoorBlock.texture = new TextureHolder(workspace, trapdoorTextureName);
		trapdoorBlock.hasTransparency = true;
		trapdoorBlock.transparencyType = "CUTOUT";
		trapdoorBlock.soundOnStep = new StepSound(workspace, "WOOD");
		trapdoorBlock.hardness = 3 * factor;
		trapdoorBlock.resistance = 3 * factor;
		trapdoorBlock.destroyTool = "axe";
		trapdoorBlock.noteBlockInstrument = "bass";
		trapdoorBlock.ignitedByLava = true;
		trapdoorBlock.lightOpacity = 0;
		trapdoorBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		addGeneratableElementToWorkspace(workspace, folder, trapdoorBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block pressurePlateBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "PressurePlate", ModElementType.BLOCK), false).getElementFromGUI();
		pressurePlateBlock.name = readableName + " Pressure Plate";
		pressurePlateBlock.blockBase = "PressurePlate";
		pressurePlateBlock.texture = new TextureHolder(workspace, planksTextureName);
		pressurePlateBlock.soundOnStep = new StepSound(workspace, "WOOD");
		pressurePlateBlock.hardness = 0.5 * factor;
		pressurePlateBlock.resistance = 0.5 * factor;
		pressurePlateBlock.destroyTool = "axe";
		pressurePlateBlock.noteBlockInstrument = "bass";
		pressurePlateBlock.ignitedByLava = true;
		pressurePlateBlock.lightOpacity = 0;
		pressurePlateBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		pressurePlateBlock.isNotColidable = true;
		pressurePlateBlock.reactionToPushing = "DESTROY";
		addGeneratableElementToWorkspace(workspace, folder, pressurePlateBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block buttonBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Button", ModElementType.BLOCK), false).getElementFromGUI();
		buttonBlock.name = readableName + " Button";
		buttonBlock.blockBase = "Button";
		buttonBlock.texture = new TextureHolder(workspace, planksTextureName);
		buttonBlock.soundOnStep = new StepSound(workspace, "WOOD");
		buttonBlock.hardness = 0.5 * factor;
		buttonBlock.resistance = 0.5 * factor;
		buttonBlock.destroyTool = "axe";
		buttonBlock.lightOpacity = 0;
		buttonBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		buttonBlock.isNotColidable = true;
		buttonBlock.reactionToPushing = "DESTROY";
		addGeneratableElementToWorkspace(workspace, folder, buttonBlock);

		// Tags
		String planksEntry = "CUSTOM:" + name + "Planks";
		String logEntry = "CUSTOM:" + name + "Log";
		String woodEntry = "CUSTOM:" + name + "Wood";
		String strippedLogEntry = "CUSTOM:Stripped" + name + "Log";
		String strippedWoodEntry = "CUSTOM:Stripped" + name + "Wood";
		addTagEntries(workspace, TagType.BLOCKS, "mod:" + registryName + "_logs", logEntry,
				woodEntry, strippedLogEntry, strippedWoodEntry);
		addTagEntries(workspace, TagType.BLOCKS, "minecraft:logs_that_burn",
				"TAG:mod:" + registryName + "_logs");
		addTagEntries(workspace, TagType.BLOCKS, "minecraft:planks", planksEntry);
		addTagEntries(workspace, TagType.ITEMS, "mod:" + registryName + "_logs", logEntry, woodEntry,
				strippedLogEntry, strippedWoodEntry);
		addTagEntries(workspace, TagType.ITEMS, "minecraft:logs_that_burn",
				"TAG:mod:" + registryName + "_logs");
		addTagEntries(workspace, TagType.ITEMS, "minecraft:planks", planksEntry);

		//Recipes
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
		addGeneratableElementToWorkspace(workspace, folder, woodRecipe);

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
		addGeneratableElementToWorkspace(workspace, folder, strippedWoodRecipe);

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
		addGeneratableElementToWorkspace(workspace, folder, planksLogRecipe);

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
		addGeneratableElementToWorkspace(workspace, folder, stairsRecipe);

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
		addGeneratableElementToWorkspace(workspace, folder, slabRecipe);

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
		addGeneratableElementToWorkspace(workspace, folder, fenceRecipe);

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
		addGeneratableElementToWorkspace(workspace, folder, fenceGateRecipe);

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
		addGeneratableElementToWorkspace(workspace, folder, doorRecipe);

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
		addGeneratableElementToWorkspace(workspace, folder, trapdoorRecipe);

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
		addGeneratableElementToWorkspace(workspace, folder, pressurePlateRecipe);

		Recipe buttonRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "ButtonRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		buttonRecipe.craftingBookCategory = "REDSTONE";
		buttonRecipe.group = "wooden_button";
		buttonRecipe.recipeSlots[4] = new MItemBlock(workspace, planksEntry);
		buttonRecipe.recipeShapeless = true;
		buttonRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Button");
		buttonRecipe.recipeRetstackSize = 1;
		buttonRecipe.unlockingItems.add(new MItemBlock(workspace, planksEntry));
		addGeneratableElementToWorkspace(workspace, folder, buttonRecipe);
	}

	public static boolean isSupported(GeneratorConfiguration gc) {
		return gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.RECIPE)
				!= GeneratorStats.CoverageStatus.NONE
				&& gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.BLOCK)
				!= GeneratorStats.CoverageStatus.NONE;
	}

	public static BasicAction getAction(ActionRegistry actionRegistry) {
		return new BasicAction(actionRegistry, L10N.t("action.pack_tools.wood"),
				e -> new WoodPackMakerTool(actionRegistry.getMCreator())) {
			@Override public boolean isEnabled() {
				return isSupported(actionRegistry.getMCreator().getGeneratorConfiguration());
			}
		}.setIcon(UIRES.get("16px.woodpack"));
	}

}
