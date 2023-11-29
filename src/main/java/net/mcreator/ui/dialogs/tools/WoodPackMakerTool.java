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
import net.mcreator.element.parts.Material;
import net.mcreator.element.parts.StepSound;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.types.Block;
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
				mcreator.mv.reloadElementsInCurrentTab();
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
		if (!PackMakerToolUtils.checkIfNamesAvailable(workspace, name + "Wood", name + "Log", name + "Planks",
				name + "Leaves", name + "Stairs", name + "Slab", name + "Fence", name + "FenceGate",
				name + "PressurePlate", name + "Button", name + "WoodRecipe", name + "PlanksLogRecipe",
				name + "PlanksWoodRecipe", name + "StairsRecipe", name + "SlabRecipe", name + "FenceRecipe",
				name + "FenceGateRecipe", name + "PressurePlateRecipe", name + "ButtonRecipe", name + "StickRecipe"))
			return;

		// select folder the mod pack should be in
		FolderElement folder = mcreator.mv.currentFolder;

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
		Block woodBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Wood", ModElementType.BLOCK), false).getElementFromGUI();
		woodBlock.name = name + " Wood";
		woodBlock.material = new Material(workspace, "WOOD");
		woodBlock.texture = woodTextureName;
		woodBlock.renderType = 11; // single texture
		woodBlock.customModelName = "Single texture";
		woodBlock.soundOnStep = new StepSound(workspace, "WOOD");
		woodBlock.hardness = 2.0 * factor;
		woodBlock.resistance = 2.0 * Math.pow(factor, 0.8);
		woodBlock.destroyTool = "axe";
		woodBlock.breakHarvestLevel = 0;
		woodBlock.flammability = (int) Math.round(5 * factor);
		woodBlock.rotationMode = 5; // log rotation
		woodBlock.creativeTab = new TabEntry(workspace, "BUILDING_BLOCKS");
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, woodBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block logBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Log", ModElementType.BLOCK), false).getElementFromGUI();
		logBlock.name = name + " Log";
		logBlock.material = new Material(workspace, "WOOD");
		logBlock.texture = logTextureName;
		logBlock.textureTop = logTextureName;
		logBlock.textureBack = woodTextureName;
		logBlock.textureFront = woodTextureName;
		logBlock.textureLeft = woodTextureName;
		logBlock.textureRight = woodTextureName;
		logBlock.renderType = 10; // normal
		logBlock.customModelName = "Normal";
		logBlock.soundOnStep = new StepSound(workspace, "WOOD");
		logBlock.hardness = 2.0 * factor;
		logBlock.resistance = 2.0 * Math.pow(factor, 0.8);
		logBlock.destroyTool = "axe";
		logBlock.breakHarvestLevel = 0;
		logBlock.flammability = (int) Math.round(5 * factor);
		logBlock.rotationMode = 5; // log rotation
		logBlock.creativeTab = new TabEntry(workspace, "BUILDING_BLOCKS");
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, logBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block planksBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Planks", ModElementType.BLOCK), false).getElementFromGUI();
		planksBlock.name = name + " Planks";
		planksBlock.material = new Material(workspace, "WOOD");
		planksBlock.texture = planksTextureName;
		planksBlock.renderType = 11; // single texture
		planksBlock.customModelName = "Single texture";
		planksBlock.soundOnStep = new StepSound(workspace, "WOOD");
		planksBlock.hardness = 2.0 * factor;
		planksBlock.resistance = 3.0 * Math.pow(factor, 0.8);
		planksBlock.destroyTool = "axe";
		planksBlock.breakHarvestLevel = 0;
		planksBlock.flammability = (int) Math.round(5 * factor);
		planksBlock.creativeTab = new TabEntry(workspace, "BUILDING_BLOCKS");
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, planksBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block leavesBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Leaves", ModElementType.BLOCK), false).getElementFromGUI();
		leavesBlock.name = name + " Leaves";
		leavesBlock.blockBase = "Leaves";
		leavesBlock.material = new Material(workspace, "LEAVES");
		leavesBlock.texture = leavesTextureName;
		leavesBlock.soundOnStep = new StepSound(workspace, "PLANT");
		leavesBlock.hardness = 0.2 * factor;
		leavesBlock.resistance = 0.2 * factor;
		leavesBlock.breakHarvestLevel = 0;
		leavesBlock.flammability = (int) Math.round(30 * factor);
		leavesBlock.lightOpacity = 1;
		leavesBlock.creativeTab = new TabEntry(workspace, "DECORATIONS");
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, leavesBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block stairsBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Stairs", ModElementType.BLOCK), false).getElementFromGUI();
		stairsBlock.name = name + " Stairs";
		stairsBlock.blockBase = "Stairs";
		stairsBlock.material = new Material(workspace, "WOOD");
		stairsBlock.texture = planksTextureName;
		stairsBlock.textureTop = planksTextureName;
		stairsBlock.textureFront = planksTextureName;
		stairsBlock.soundOnStep = new StepSound(workspace, "WOOD");
		stairsBlock.hardness = 3 * factor;
		stairsBlock.resistance = 2 * factor;
		stairsBlock.breakHarvestLevel = 0;
		stairsBlock.flammability = (int) Math.round(5 * factor);
		stairsBlock.lightOpacity = 0;
		stairsBlock.creativeTab = new TabEntry(workspace, "BUILDING_BLOCKS");
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, stairsBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block slabBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Slab", ModElementType.BLOCK), false).getElementFromGUI();
		slabBlock.name = name + " Slab";
		slabBlock.blockBase = "Slab";
		slabBlock.material = new Material(workspace, "WOOD");
		slabBlock.texture = planksTextureName;
		slabBlock.textureTop = planksTextureName;
		slabBlock.textureFront = planksTextureName;
		slabBlock.soundOnStep = new StepSound(workspace, "WOOD");
		slabBlock.hardness = 2 * factor;
		slabBlock.resistance = 3 * factor;
		slabBlock.breakHarvestLevel = 0;
		slabBlock.flammability = (int) Math.round(5 * factor);
		slabBlock.lightOpacity = 0;
		slabBlock.creativeTab = new TabEntry(workspace, "BUILDING_BLOCKS");
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, slabBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block fenceBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Fence", ModElementType.BLOCK), false).getElementFromGUI();
		fenceBlock.name = name + " Fence";
		fenceBlock.blockBase = "Fence";
		fenceBlock.material = new Material(workspace, "WOOD");
		fenceBlock.texture = planksTextureName;
		fenceBlock.soundOnStep = new StepSound(workspace, "WOOD");
		fenceBlock.hardness = 2 * factor;
		fenceBlock.resistance = 3 * factor;
		fenceBlock.breakHarvestLevel = 0;
		fenceBlock.flammability = (int) Math.round(5 * factor);
		fenceBlock.lightOpacity = 0;
		fenceBlock.creativeTab = new TabEntry(workspace, "BUILDING_BLOCKS");
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, fenceBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block fenceGateBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "FenceGate", ModElementType.BLOCK), false).getElementFromGUI();
		fenceGateBlock.name = name + " Fence Gate";
		fenceGateBlock.blockBase = "FenceGate";
		fenceGateBlock.material = new Material(workspace, "WOOD");
		fenceGateBlock.texture = planksTextureName;
		fenceGateBlock.soundOnStep = new StepSound(workspace, "WOOD");
		fenceGateBlock.hardness = 2 * factor;
		fenceGateBlock.resistance = 3 * factor;
		fenceGateBlock.breakHarvestLevel = 0;
		fenceGateBlock.flammability = (int) Math.round(5 * factor);
		fenceGateBlock.lightOpacity = 0;
		fenceGateBlock.creativeTab = new TabEntry(workspace, "BUILDING_BLOCKS");
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, fenceGateBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block pressurePlateBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "PressurePlate", ModElementType.BLOCK), false).getElementFromGUI();
		pressurePlateBlock.name = name + " Pressure Plate";
		pressurePlateBlock.blockBase = "PressurePlate";
		pressurePlateBlock.material = new Material(workspace, "WOOD");
		pressurePlateBlock.texture = planksTextureName;
		pressurePlateBlock.soundOnStep = new StepSound(workspace, "WOOD");
		pressurePlateBlock.hardness = 2 * factor;
		pressurePlateBlock.resistance = 3 * factor;
		pressurePlateBlock.breakHarvestLevel = 0;
		pressurePlateBlock.flammability = (int) Math.round(5 * factor);
		pressurePlateBlock.lightOpacity = 0;
		pressurePlateBlock.creativeTab = new TabEntry(workspace, "BUILDING_BLOCKS");
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, pressurePlateBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block buttonBlock = (Block) ModElementType.BLOCK.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Button", ModElementType.BLOCK), false).getElementFromGUI();
		buttonBlock.name = name + " Button";
		buttonBlock.blockBase = "Button";
		buttonBlock.material = new Material(workspace, "WOOD");
		buttonBlock.texture = planksTextureName;
		buttonBlock.soundOnStep = new StepSound(workspace, "WOOD");
		buttonBlock.hardness = 2 * factor;
		buttonBlock.resistance = 3 * factor;
		buttonBlock.breakHarvestLevel = 0;
		buttonBlock.lightOpacity = 0;
		buttonBlock.flammability = (int) Math.round(5 * factor);
		buttonBlock.creativeTab = new TabEntry(workspace, "BUILDING_BLOCKS");
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, buttonBlock);

		//Recipes
		Recipe woodRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "WoodRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		woodRecipe.craftingBookCategory = "BUILDING";
		woodRecipe.group = "bark";
		woodRecipe.recipeSlots[0] = new MItemBlock(workspace, "CUSTOM:" + logBlock.getModElement().getName());
		woodRecipe.recipeSlots[1] = new MItemBlock(workspace, "CUSTOM:" + logBlock.getModElement().getName());
		woodRecipe.recipeSlots[3] = new MItemBlock(workspace, "CUSTOM:" + logBlock.getModElement().getName());
		woodRecipe.recipeSlots[4] = new MItemBlock(workspace, "CUSTOM:" + logBlock.getModElement().getName());
		woodRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Wood");
		woodRecipe.recipeRetstackSize = 3;
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, woodRecipe);

		Recipe planksLogRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "PlanksLogRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		planksLogRecipe.craftingBookCategory = "BUILDING";
		planksLogRecipe.group = "planks";
		planksLogRecipe.recipeSlots[4] = new MItemBlock(workspace, "CUSTOM:" + logBlock.getModElement().getName());
		planksLogRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Planks");
		planksLogRecipe.recipeShapeless = true;
		planksLogRecipe.recipeRetstackSize = 4;
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, planksLogRecipe);

		Recipe planksWoodRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "PlanksWoodRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		planksWoodRecipe.craftingBookCategory = "BUILDING";
		planksWoodRecipe.group = "planks";
		planksWoodRecipe.recipeSlots[4] = new MItemBlock(workspace, "CUSTOM:" + woodBlock.getModElement().getName());
		planksWoodRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Planks");
		planksWoodRecipe.recipeShapeless = true;
		planksWoodRecipe.recipeRetstackSize = 4;
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, planksWoodRecipe);

		Recipe stairsRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "StairsRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		stairsRecipe.craftingBookCategory = "BUILDING";
		stairsRecipe.group = "wooden_stairs";
		stairsRecipe.recipeSlots[0] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		stairsRecipe.recipeSlots[3] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		stairsRecipe.recipeSlots[4] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		stairsRecipe.recipeSlots[6] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		stairsRecipe.recipeSlots[7] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		stairsRecipe.recipeSlots[8] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		stairsRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Stairs");
		stairsRecipe.recipeRetstackSize = 4;
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, stairsRecipe);

		Recipe slabRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "SlabRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		slabRecipe.craftingBookCategory = "BUILDING";
		slabRecipe.group = "wooden_slab";
		slabRecipe.recipeSlots[6] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		slabRecipe.recipeSlots[7] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		slabRecipe.recipeSlots[8] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		slabRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Slab");
		slabRecipe.recipeRetstackSize = 6;
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, slabRecipe);

		Recipe fenceRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "FenceRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		fenceRecipe.group = "wooden_fence";
		fenceRecipe.recipeSlots[3] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		fenceRecipe.recipeSlots[4] = new MItemBlock(workspace, "Items.STICK");
		fenceRecipe.recipeSlots[5] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		fenceRecipe.recipeSlots[6] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		fenceRecipe.recipeSlots[7] = new MItemBlock(workspace, "Items.STICK");
		fenceRecipe.recipeSlots[8] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		fenceRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Fence");
		fenceRecipe.recipeRetstackSize = 3;
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, fenceRecipe);

		Recipe fenceGateRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "FenceGateRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		fenceGateRecipe.craftingBookCategory = "REDSTONE";
		fenceGateRecipe.group = "wooden_fence_gate";
		fenceGateRecipe.recipeSlots[3] = new MItemBlock(workspace, "Items.STICK");
		fenceGateRecipe.recipeSlots[4] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		fenceGateRecipe.recipeSlots[5] = new MItemBlock(workspace, "Items.STICK");
		fenceGateRecipe.recipeSlots[6] = new MItemBlock(workspace, "Items.STICK");
		fenceGateRecipe.recipeSlots[7] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		fenceGateRecipe.recipeSlots[8] = new MItemBlock(workspace, "Items.STICK");
		fenceGateRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "FenceGate");
		fenceGateRecipe.recipeRetstackSize = 1;
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, fenceGateRecipe);

		Recipe pressurePlateRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
						new ModElement(workspace, name + "PressurePlateRecipe", ModElementType.RECIPE), false)
				.getElementFromGUI();
		pressurePlateRecipe.craftingBookCategory = "REDSTONE";
		pressurePlateRecipe.group = "wooden_pressure_plate";
		pressurePlateRecipe.recipeSlots[6] = new MItemBlock(workspace,
				"CUSTOM:" + planksBlock.getModElement().getName());
		pressurePlateRecipe.recipeSlots[7] = new MItemBlock(workspace,
				"CUSTOM:" + planksBlock.getModElement().getName());
		pressurePlateRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "PressurePlate");
		pressurePlateRecipe.recipeRetstackSize = 1;
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, pressurePlateRecipe);

		Recipe buttonRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "ButtonRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		buttonRecipe.craftingBookCategory = "REDSTONE";
		buttonRecipe.group = "wooden_button";
		buttonRecipe.recipeSlots[4] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		buttonRecipe.recipeShapeless = true;
		buttonRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Button");
		buttonRecipe.recipeRetstackSize = 1;
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, buttonRecipe);

		Recipe stickRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "StickRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		stickRecipe.group = "sticks";
		stickRecipe.recipeSlots[0] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		stickRecipe.recipeSlots[3] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		stickRecipe.recipeReturnStack = new MItemBlock(workspace, "Items.STICK");
		stickRecipe.recipeRetstackSize = 4;
		PackMakerToolUtils.addGeneratableElementToWorkspace(workspace, folder, stickRecipe);
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
