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
import net.mcreator.ui.dialogs.MCreatorDialog;
import net.mcreator.ui.init.ImageMakerTexturesCache;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.ModElementNameValidator;
import net.mcreator.ui.variants.modmaker.ModMaker;
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

public class WoodPackMakerTool {

	private static void open(MCreator mcreator) {
		MCreatorDialog dialog = new MCreatorDialog(mcreator, L10N.t("dialog.tools.wood_pack_title"), true);
		dialog.setLayout(new BorderLayout(10, 10));

		dialog.setIconImage(UIRES.get("16px.woodpack").getImage());

		dialog.add("North", PanelUtils.centerInPanel(L10N.label("dialog.tools.wood_pack_info")));

		JPanel props = new JPanel(new GridLayout(4, 2, 5, 2));

		VTextField name = new VTextField(25);
		JColor color = new JColor(mcreator, false, false);
		JSpinner power = new JSpinner(new SpinnerNumberModel(1, 0.1, 10, 0.1));

		name.enableRealtimeValidation();

		props.add(L10N.label("dialog.tools.wood_pack_name"));
		props.add(name);

		props.add(L10N.label("dialog.tools.wood_pack_color_accent"));
		props.add(color);

		props.add(L10N.label("dialog.tools.wood_pack_power_factor"));
		props.add(power);

		name.setValidator(new ModElementNameValidator(mcreator.getWorkspace(), name,
				L10N.t("dialog.tools.wood_pack_name_validator")));

		dialog.add("Center", PanelUtils.centerInPanel(props));
		JButton ok = L10N.button("dialog.tools.wood_pack_create");
		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		cancel.addActionListener(e -> dialog.setVisible(false));
		dialog.add("South", PanelUtils.join(ok, cancel));

		ok.addActionListener(e -> {
			if (name.getValidationStatus().getValidationResultType() != Validator.ValidationResultType.ERROR) {
				dialog.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				addWoodPackToWorkspace(mcreator, mcreator.getWorkspace(), name.getText(), color.getColor(),
						(Double) power.getValue());
				mcreator.reloadWorkspaceTabContents();
				dialog.setCursor(Cursor.getDefaultCursor());
				dialog.setVisible(false);
			}
		});

		dialog.getRootPane().setDefaultButton(ok);
		dialog.setSize(600, 260);
		dialog.setLocationRelativeTo(mcreator);
		dialog.setVisible(true);
	}

	private static void addWoodPackToWorkspace(MCreator mcreator, Workspace workspace, String name, Color color,
			double factor) {
		String registryName = RegistryNameFixer.fromCamelCase(name);

		if (!PackMakerToolUtils.checkIfNamesAvailable(workspace, name + "Wood", name + "Log", name + "Planks",
				name + "Leaves", name + "Stairs", name + "Slab", name + "Fence", name + "FenceGate",
				name + "PressurePlate", name + "Button", name + "WoodRecipe", name + "PlanksRecipe",
				name + "StairsRecipe", name + "SlabRecipe", name + "FenceRecipe", name + "FenceGateRecipe",
				name + "PressurePlateRecipe", name + "ButtonRecipe"))
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
		String woodTextureName = (name + "_log_side").toLowerCase(Locale.ENGLISH);
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(wood.getImage()),
				mcreator.getFolderManager().getTextureFile(RegistryNameFixer.fix(woodTextureName), TextureType.BLOCK));

		//then we generate the missing log texture
		ImageIcon log = ImageUtils.colorize(
				ImageMakerTexturesCache.CACHE.get(new ResourcePointer("templates/textures/texturemaker/log_top.png")),
				color, true);
		String logTextureName = (name + "_log_top").toLowerCase(Locale.ENGLISH);
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(log.getImage()),
				mcreator.getFolderManager().getTextureFile(RegistryNameFixer.fix(logTextureName), TextureType.BLOCK));

		//then we generate the planks texture
		ImageIcon planks = ImageUtils.colorize(ImageMakerTexturesCache.CACHE.get(new ResourcePointer(
				"templates/textures/texturemaker/" + ListUtils.getRandomItem(Arrays.asList("planks_0", "planks_1"))
						+ ".png")), color, true);
		String planksTextureName = (name + "_planks").toLowerCase(Locale.ENGLISH);
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(planks.getImage()), mcreator.getFolderManager()
				.getTextureFile(RegistryNameFixer.fix(planksTextureName), TextureType.BLOCK));

		//then we generate the leaves texture
		ImageIcon leaves = ImageUtils.colorize(ImageMakerTexturesCache.CACHE.get(new ResourcePointer(
				"templates/textures/texturemaker/" + ListUtils.getRandomItem(
						Arrays.asList("leaves_0", "leaves_1", "leaves_2", "leaves_3", "leaves_4", "leaves_5",
								"leaves_new1", "leaves_new2", "leaves2")) + ".png")), color, true);
		String leavesTextureName = (name + "_leaves").toLowerCase(Locale.ENGLISH);
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(leaves.getImage()), mcreator.getFolderManager()
				.getTextureFile(RegistryNameFixer.fix(leavesTextureName), TextureType.BLOCK));

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block logBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Log", ModElementType.BLOCK), false).getElementFromGUI();
		logBlock.name = name + " Log";
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
		logBlock.noteBlockInstrument = "BASS";
		logBlock.ignitedByLava = true;
		logBlock.flammability = (int) Math.round(5 * factor);
		logBlock.rotationMode = 5; // log rotation
		logBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, logBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block woodBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Wood", ModElementType.BLOCK), false).getElementFromGUI();
		woodBlock.name = name + " Wood";
		woodBlock.texture = new TextureHolder(workspace, woodTextureName);
		woodBlock.renderType = 11; // single texture
		woodBlock.customModelName = "Single texture";
		woodBlock.soundOnStep = new StepSound(workspace, "WOOD");
		woodBlock.hardness = 2.0 * factor;
		woodBlock.resistance = 2.0 * Math.pow(factor, 0.8);
		woodBlock.destroyTool = "axe";
		woodBlock.noteBlockInstrument = "BASS";
		woodBlock.ignitedByLava = true;
		woodBlock.flammability = (int) Math.round(5 * factor);
		woodBlock.rotationMode = 5; // log rotation
		woodBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, woodBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block planksBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Planks", ModElementType.BLOCK), false).getElementFromGUI();
		planksBlock.name = name + " Planks";
		planksBlock.texture = new TextureHolder(workspace, planksTextureName);
		planksBlock.renderType = 11; // single texture
		planksBlock.customModelName = "Single texture";
		planksBlock.soundOnStep = new StepSound(workspace, "WOOD");
		planksBlock.hardness = 2.0 * factor;
		planksBlock.resistance = 3.0 * Math.pow(factor, 0.8);
		planksBlock.destroyTool = "axe";
		planksBlock.noteBlockInstrument = "BASS";
		planksBlock.ignitedByLava = true;
		planksBlock.flammability = (int) Math.round(5 * factor);
		planksBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, planksBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block leavesBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Leaves", ModElementType.BLOCK), false).getElementFromGUI();
		leavesBlock.name = name + " Leaves";
		leavesBlock.blockBase = "Leaves";
		leavesBlock.texture = new TextureHolder(workspace, leavesTextureName);
		leavesBlock.soundOnStep = new StepSound(workspace, "PLANT");
		leavesBlock.hardness = 0.2 * factor;
		leavesBlock.resistance = 0.2 * factor;
		leavesBlock.ignitedByLava = true;
		leavesBlock.flammability = (int) Math.round(30 * factor);
		leavesBlock.lightOpacity = 1;
		leavesBlock.creativeTabs = List.of(new TabEntry(workspace, "DECORATIONS"));
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, leavesBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block stairsBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Stairs", ModElementType.BLOCK), false).getElementFromGUI();
		stairsBlock.name = name + " Stairs";
		stairsBlock.blockBase = "Stairs";
		stairsBlock.texture = new TextureHolder(workspace, planksTextureName);
		stairsBlock.textureTop = new TextureHolder(workspace, planksTextureName);
		stairsBlock.textureFront = new TextureHolder(workspace, planksTextureName);
		stairsBlock.soundOnStep = new StepSound(workspace, "WOOD");
		stairsBlock.hardness = 3 * factor;
		stairsBlock.resistance = 2 * factor;
		stairsBlock.noteBlockInstrument = "BASS";
		stairsBlock.ignitedByLava = true;
		stairsBlock.flammability = (int) Math.round(5 * factor);
		stairsBlock.lightOpacity = 0;
		stairsBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, stairsBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block slabBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Slab", ModElementType.BLOCK), false).getElementFromGUI();
		slabBlock.name = name + " Slab";
		slabBlock.blockBase = "Slab";
		slabBlock.texture = new TextureHolder(workspace, planksTextureName);
		slabBlock.textureTop = new TextureHolder(workspace, planksTextureName);
		slabBlock.textureFront = new TextureHolder(workspace, planksTextureName);
		slabBlock.soundOnStep = new StepSound(workspace, "WOOD");
		slabBlock.hardness = 2 * factor;
		slabBlock.resistance = 3 * factor;
		slabBlock.noteBlockInstrument = "BASS";
		slabBlock.ignitedByLava = true;
		slabBlock.flammability = (int) Math.round(5 * factor);
		slabBlock.lightOpacity = 0;
		slabBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, slabBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block fenceBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Fence", ModElementType.BLOCK), false).getElementFromGUI();
		fenceBlock.name = name + " Fence";
		fenceBlock.blockBase = "Fence";
		fenceBlock.texture = new TextureHolder(workspace, planksTextureName);
		fenceBlock.soundOnStep = new StepSound(workspace, "WOOD");
		fenceBlock.hardness = 2 * factor;
		fenceBlock.resistance = 3 * factor;
		fenceBlock.noteBlockInstrument = "BASS";
		fenceBlock.ignitedByLava = true;
		fenceBlock.flammability = (int) Math.round(5 * factor);
		fenceBlock.lightOpacity = 0;
		fenceBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, fenceBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block fenceGateBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "FenceGate", ModElementType.BLOCK), false).getElementFromGUI();
		fenceGateBlock.name = name + " Fence Gate";
		fenceGateBlock.blockBase = "FenceGate";
		fenceGateBlock.texture = new TextureHolder(workspace, planksTextureName);
		fenceGateBlock.soundOnStep = new StepSound(workspace, "WOOD");
		fenceGateBlock.hardness = 2 * factor;
		fenceGateBlock.resistance = 3 * factor;
		fenceGateBlock.noteBlockInstrument = "BASS";
		fenceGateBlock.ignitedByLava = true;
		fenceGateBlock.flammability = (int) Math.round(5 * factor);
		fenceGateBlock.lightOpacity = 0;
		fenceGateBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, fenceGateBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block pressurePlateBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "PressurePlate", ModElementType.BLOCK), false).getElementFromGUI();
		pressurePlateBlock.name = name + " Pressure Plate";
		pressurePlateBlock.blockBase = "PressurePlate";
		pressurePlateBlock.texture = new TextureHolder(workspace, planksTextureName);
		pressurePlateBlock.soundOnStep = new StepSound(workspace, "WOOD");
		pressurePlateBlock.hardness = 2 * factor;
		pressurePlateBlock.resistance = 3 * factor;
		pressurePlateBlock.noteBlockInstrument = "BASS";
		pressurePlateBlock.ignitedByLava = true;
		pressurePlateBlock.flammability = (int) Math.round(5 * factor);
		pressurePlateBlock.lightOpacity = 0;
		pressurePlateBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, pressurePlateBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block buttonBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Button", ModElementType.BLOCK), false).getElementFromGUI();
		buttonBlock.name = name + " Button";
		buttonBlock.blockBase = "Button";
		buttonBlock.texture = new TextureHolder(workspace, planksTextureName);
		buttonBlock.soundOnStep = new StepSound(workspace, "WOOD");
		buttonBlock.hardness = 2 * factor;
		buttonBlock.resistance = 3 * factor;
		buttonBlock.lightOpacity = 0;
		buttonBlock.flammability = (int) Math.round(5 * factor);
		buttonBlock.creativeTabs = List.of(new TabEntry(workspace, "BUILDING_BLOCKS"));
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, buttonBlock);

		// Tags
		String planksEntry = "CUSTOM:" + name + "Planks";
		String logEntry = "CUSTOM:" + name + "Log";
		String woodEntry = "CUSTOM:" + name + "Wood";
		PackMakerToolUtils.addTagEntries(workspace, TagType.BLOCKS, "mod:" + registryName + "_logs", logEntry,
				woodEntry);
		PackMakerToolUtils.addTagEntries(workspace, TagType.BLOCKS, "minecraft:logs_that_burn",
				"TAG:mod:" + registryName + "_logs");
		PackMakerToolUtils.addTagEntries(workspace, TagType.BLOCKS, "minecraft:planks", planksEntry);
		PackMakerToolUtils.addTagEntries(workspace, TagType.ITEMS, "mod:" + registryName + "_logs", logEntry,
				woodEntry);
		PackMakerToolUtils.addTagEntries(workspace, TagType.ITEMS, "minecraft:logs_that_burn",
				"TAG:mod:" + registryName + "_logs");
		PackMakerToolUtils.addTagEntries(workspace, TagType.ITEMS, "minecraft:planks", planksEntry);

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
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, woodRecipe);

		Recipe planksLogRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "PlanksRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		planksLogRecipe.craftingBookCategory = "BUILDING";
		planksLogRecipe.group = "planks";
		planksLogRecipe.recipeSlots[4] = new MItemBlock(workspace,
				"TAG:" + workspace.getWorkspaceSettings().getModID() + ":" + registryName + "_logs");
		planksLogRecipe.recipeReturnStack = new MItemBlock(workspace, planksEntry);
		planksLogRecipe.recipeShapeless = true;
		planksLogRecipe.recipeRetstackSize = 4;
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, planksLogRecipe);

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
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, stairsRecipe);

		Recipe slabRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "SlabRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		slabRecipe.craftingBookCategory = "BUILDING";
		slabRecipe.group = "wooden_slab";
		slabRecipe.recipeSlots[6] = new MItemBlock(workspace, planksEntry);
		slabRecipe.recipeSlots[7] = new MItemBlock(workspace, planksEntry);
		slabRecipe.recipeSlots[8] = new MItemBlock(workspace, planksEntry);
		slabRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Slab");
		slabRecipe.recipeRetstackSize = 6;
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, slabRecipe);

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
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, fenceRecipe);

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
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, fenceGateRecipe);

		Recipe pressurePlateRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
						new ModElement(workspace, name + "PressurePlateRecipe", ModElementType.RECIPE), false)
				.getElementFromGUI();
		pressurePlateRecipe.craftingBookCategory = "REDSTONE";
		pressurePlateRecipe.group = "wooden_pressure_plate";
		pressurePlateRecipe.recipeSlots[6] = new MItemBlock(workspace, planksEntry);
		pressurePlateRecipe.recipeSlots[7] = new MItemBlock(workspace, planksEntry);
		pressurePlateRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "PressurePlate");
		pressurePlateRecipe.recipeRetstackSize = 1;
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, pressurePlateRecipe);

		Recipe buttonRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "ButtonRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		buttonRecipe.craftingBookCategory = "REDSTONE";
		buttonRecipe.group = "wooden_button";
		buttonRecipe.recipeSlots[4] = new MItemBlock(workspace, planksEntry);
		buttonRecipe.recipeShapeless = true;
		buttonRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Button");
		buttonRecipe.recipeRetstackSize = 1;
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, buttonRecipe);
	}

	public static BasicAction getAction(ActionRegistry actionRegistry) {
		return new BasicAction(actionRegistry, L10N.t("action.pack_tools.wood"),
				e -> open(actionRegistry.getMCreator())) {
			@Override public boolean isEnabled() {
				GeneratorConfiguration gc = actionRegistry.getMCreator().getGeneratorConfiguration();
				return gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.RECIPE)
						!= GeneratorStats.CoverageStatus.NONE
						&& gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.BLOCK)
						!= GeneratorStats.CoverageStatus.NONE;
			}
		}.setIcon(UIRES.get("16px.woodpack"));
	}

}
