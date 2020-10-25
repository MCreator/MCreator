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

import net.mcreator.blockly.java.blocks.MCItemBlock;
import net.mcreator.element.ModElementType;
import net.mcreator.element.ModElementTypeRegistry;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.parts.Material;
import net.mcreator.element.parts.StepSound;
import net.mcreator.element.types.Block;
import net.mcreator.element.types.Recipe;
import net.mcreator.element.types.Tag;
import net.mcreator.element.types.Tool;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.io.FileIO;
import net.mcreator.io.ResourcePointer;
import net.mcreator.minecraft.ElementUtil;
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
import net.mcreator.ui.minecraft.MCItemHolder;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.ModElementNameValidator;
import net.mcreator.util.ListUtils;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

public class WoodPackMakerTool {

	private static void open(MCreator mcreator) {
		MCreatorDialog dialog = new MCreatorDialog(mcreator, L10N.t("dialog.tools.wood_pack_title"), true);
		dialog.setLayout(new BorderLayout(10, 10));

		dialog.setIconImage(UIRES.get("16px.woodpack").getImage());

		dialog.add("North", PanelUtils.centerInPanel(L10N.label("dialog.tools.wood_pack_info")));

		JPanel props = new JPanel(new GridLayout(4, 2, 5, 5));

		VTextField name = new VTextField(25);
		JColor color = new JColor(mcreator, false);
		JSpinner power = new JSpinner(new SpinnerNumberModel(1, 0.1, 10, 0.1));

		name.enableRealtimeValidation();

		props.add(L10N.label("dialog.tools.wood_pack_name"));
		props.add(name);

		props.add(L10N.label("dialog.tools.wood_pack_color_accent"));
		props.add(color);

		props.add(L10N.label("dialog.tools.wood_pack_power_factor"));
		props.add(power);

		name.setValidator(new ModElementNameValidator(mcreator.getWorkspace(), name));

		dialog.add("Center", PanelUtils.centerInPanel(props));
		JButton ok = L10N.button("dialog.tools.wood_pack_create");
		JButton canecel = L10N.button(UIManager.getString("OptionPane.cancelButtonText"));
		canecel.addActionListener(e -> dialog.setVisible(false));
		dialog.add("South", PanelUtils.join(ok, canecel));

		ok.addActionListener(e -> {
			if (name.getValidationStatus().getValidationResultType() != Validator.ValidationResultType.ERROR) {
				dialog.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				addWoodPackToWorkspace(mcreator, mcreator.getWorkspace(), name.getText(),
						color.getColor(), (Double) power.getValue());
				mcreator.mv.updateMods();
				dialog.setCursor(Cursor.getDefaultCursor());
				dialog.setVisible(false);
			}
		});

		dialog.setSize(600, 250);
		dialog.setLocationRelativeTo(mcreator);
		dialog.setVisible(true);
	}

	private static void addWoodPackToWorkspace(MCreator mcreator, Workspace workspace, String name,
			Color color, double factor) {
		// first we generate wood texture
		ImageIcon wood = ImageUtils.colorize(ImageMakerTexturesCache.CACHE.get(new ResourcePointer(
						"templates/textures/texturemaker/" + ListUtils
								.getRandomItem(Arrays.asList("log_side_1", "log_side_2", "log_side_3",
										"log_side_4", "log_side_5")) + ".png")), color,
				true);
		String woodTextureName = (name + "_log_side").toLowerCase(Locale.ENGLISH);
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(wood.getImage()),
				mcreator.getWorkspace().getFolderManager()
						.getBlockTextureFile(RegistryNameFixer.fix(woodTextureName)));

		//then we generate the missing log texture
		ImageIcon log = ImageUtils.colorize(ImageMakerTexturesCache.CACHE.get(new ResourcePointer(
						"templates/textures/texturemaker/log_top.png")), color, true);
		String logTextureName = (name + "_log_top").toLowerCase(Locale.ENGLISH);
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(log.getImage()),
				mcreator.getWorkspace().getFolderManager()
						.getBlockTextureFile(RegistryNameFixer.fix(logTextureName)));

		//then we generate the planks texture
		ImageIcon planks = ImageUtils.colorize(ImageMakerTexturesCache.CACHE.get(new ResourcePointer(
						"templates/textures/texturemaker/" + ListUtils
								.getRandomItem(Arrays.asList("planks_0", "planks_1")) + ".png")), color,
				true);
		String planksTextureName = (name + "_planks").toLowerCase(Locale.ENGLISH);
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(planks.getImage()),
				mcreator.getWorkspace().getFolderManager()
						.getBlockTextureFile(RegistryNameFixer.fix(planksTextureName)));

		//then we generate the leaves texture
		ImageIcon leaves = ImageUtils.colorize(ImageMakerTexturesCache.CACHE.get(new ResourcePointer(
						"templates/textures/texturemaker/" + ListUtils
								.getRandomItem(Arrays.asList("leaves_0", "leaves_1", "leaves_2", "leaves_3", "leaves_4",
										"leaves_5", "leaves_new1", "leaves_new2", "leaves2")) + ".png")), color,
				true);
		String leavesTextureName = (name + "_leaves").toLowerCase(Locale.ENGLISH);
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(leaves.getImage()),
				mcreator.getWorkspace().getFolderManager()
						.getBlockTextureFile(RegistryNameFixer.fix(leavesTextureName)));

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block woodBlock = (Block) ModElementTypeRegistry.REGISTRY.get(ModElementType.BLOCK)
				.getModElement(mcreator, new ModElement(workspace, name + "Wood", ModElementType.BLOCK), false)
				.getElementFromGUI();
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
		woodBlock.plantsGrowOn = true;
		woodBlock.flammability = (int) Math.round(5 * factor);
		mcreator.getWorkspace().getModElementManager().storeModElementPicture(woodBlock);
		mcreator.getWorkspace().addModElement(woodBlock.getModElement());
		mcreator.getWorkspace().getGenerator().generateElement(woodBlock);
		mcreator.getWorkspace().getModElementManager().storeModElement(woodBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block logBlock = (Block) ModElementTypeRegistry.REGISTRY.get(ModElementType.BLOCK)
				.getModElement(mcreator, new ModElement(workspace, name + "Log", ModElementType.BLOCK), false)
				.getElementFromGUI();
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
		logBlock.plantsGrowOn = true;
		logBlock.flammability = (int) Math.round(5 * factor);
		mcreator.getWorkspace().getModElementManager().storeModElementPicture(logBlock);
		mcreator.getWorkspace().addModElement(logBlock.getModElement());
		mcreator.getWorkspace().getGenerator().generateElement(logBlock);
		mcreator.getWorkspace().getModElementManager().storeModElement(logBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block planksBlock = (Block) ModElementTypeRegistry.REGISTRY.get(ModElementType.BLOCK)
				.getModElement(mcreator, new ModElement(workspace, name + "Planks", ModElementType.BLOCK), false)
				.getElementFromGUI();
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
		mcreator.getWorkspace().getModElementManager().storeModElementPicture(planksBlock);
		mcreator.getWorkspace().addModElement(planksBlock.getModElement());
		mcreator.getWorkspace().getGenerator().generateElement(planksBlock);
		mcreator.getWorkspace().getModElementManager().storeModElement(planksBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block leavesBlock = (Block) ModElementTypeRegistry.REGISTRY.get(ModElementType.BLOCK)
				.getModElement(mcreator, new ModElement(workspace, name + "Leaves", ModElementType.BLOCK), false)
				.getElementFromGUI();
		leavesBlock.name = name + " Leaves";
		leavesBlock.blockBase = "Leaves";
		leavesBlock.material = new Material(workspace, "LEAVES");
		leavesBlock.texture = leavesTextureName;
		leavesBlock.soundOnStep = new StepSound(workspace, "PLANT");
		leavesBlock.hardness = 0.2 * factor;
		leavesBlock.resistance = 0.2 * factor;
		leavesBlock.breakHarvestLevel = 0;
		leavesBlock.flammability = (int) Math.round(30 * factor);
		mcreator.getWorkspace().getModElementManager().storeModElementPicture(leavesBlock);
		mcreator.getWorkspace().addModElement(leavesBlock.getModElement());
		mcreator.getWorkspace().getGenerator().generateElement(leavesBlock);
		mcreator.getWorkspace().getModElementManager().storeModElement(leavesBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block stairsBlock = (Block) ModElementTypeRegistry.REGISTRY.get(ModElementType.BLOCK)
				.getModElement(mcreator, new ModElement(workspace, name + "Stairs", ModElementType.BLOCK), false)
				.getElementFromGUI();
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
		mcreator.getWorkspace().getModElementManager().storeModElementPicture(stairsBlock);
		mcreator.getWorkspace().addModElement(stairsBlock.getModElement());
		mcreator.getWorkspace().getGenerator().generateElement(stairsBlock);
		mcreator.getWorkspace().getModElementManager().storeModElement(stairsBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block slabBlock = (Block) ModElementTypeRegistry.REGISTRY.get(ModElementType.BLOCK)
				.getModElement(mcreator, new ModElement(workspace, name + "Slab", ModElementType.BLOCK), false)
				.getElementFromGUI();
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
		mcreator.getWorkspace().getModElementManager().storeModElementPicture(slabBlock);
		mcreator.getWorkspace().addModElement(slabBlock.getModElement());
		mcreator.getWorkspace().getGenerator().generateElement(slabBlock);
		mcreator.getWorkspace().getModElementManager().storeModElement(slabBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block fenceBlock = (Block) ModElementTypeRegistry.REGISTRY.get(ModElementType.BLOCK)
				.getModElement(mcreator, new ModElement(workspace, name + "Fence", ModElementType.BLOCK), false)
				.getElementFromGUI();
		fenceBlock.name = name + " Fence";
		fenceBlock.blockBase = "Fence";
		fenceBlock.material = new Material(workspace, "WOOD");
		fenceBlock.texture = planksTextureName;
		fenceBlock.soundOnStep = new StepSound(workspace, "WOOD");
		fenceBlock.hardness = 2 * factor;
		fenceBlock.resistance = 3 * factor;
		fenceBlock.breakHarvestLevel = 0;
		fenceBlock.flammability = (int) Math.round(5 * factor);
		mcreator.getWorkspace().getModElementManager().storeModElementPicture(fenceBlock);
		mcreator.getWorkspace().addModElement(fenceBlock.getModElement());
		mcreator.getWorkspace().getGenerator().generateElement(fenceBlock);
		mcreator.getWorkspace().getModElementManager().storeModElement(fenceBlock);

		// we use Block GUI to get default values for the block element (kinda hacky!)
		Block fenceGateBlock = (Block) ModElementTypeRegistry.REGISTRY.get(ModElementType.BLOCK)
				.getModElement(mcreator, new ModElement(workspace, name + "FenceGate", ModElementType.BLOCK), false)
				.getElementFromGUI();
		fenceGateBlock.name = name + " Fence Gate";
		fenceGateBlock.blockBase = "FenceGate";
		fenceGateBlock.material = new Material(workspace, "WOOD");
		fenceGateBlock.texture = planksTextureName;
		fenceGateBlock.soundOnStep = new StepSound(workspace, "WOOD");
		fenceGateBlock.hardness = 2 * factor;
		fenceGateBlock.resistance = 3 * factor;
		fenceGateBlock.breakHarvestLevel = 0;
		fenceGateBlock.flammability = (int) Math.round(5 * factor);
		mcreator.getWorkspace().getModElementManager().storeModElementPicture(fenceGateBlock);
		mcreator.getWorkspace().addModElement(fenceGateBlock.getModElement());
		mcreator.getWorkspace().getGenerator().generateElement(fenceGateBlock);
		mcreator.getWorkspace().getModElementManager().storeModElement(fenceGateBlock);

		//Tag - Items
		//Mainly used for recipes and loot tables
		Tag woodItemTag = (Tag) ModElementTypeRegistry.REGISTRY.get(ModElementType.TAG)
				.getModElement(mcreator, new ModElement(workspace, name + "ItemsTag", ModElementType.TAG), false)
				.getElementFromGUI();
		woodItemTag.namespace = "forge";
		woodItemTag.name = RegistryNameFixer.fix(name) + "_log";
		woodItemTag.type = "Items";
		woodItemTag.items = new ArrayList<>();
		woodItemTag.items.add(new MItemBlock(workspace, "CUSTOM:" + woodBlock.getModElement().getName()));
		woodItemTag.items.add(new MItemBlock(workspace, "CUSTOM:" + logBlock.getModElement().getName()));
		mcreator.getWorkspace().getModElementManager().storeModElementPicture(woodItemTag);
		mcreator.getWorkspace().addModElement(woodItemTag.getModElement());
		mcreator.getWorkspace().getGenerator().generateElement(woodItemTag);
		mcreator.getWorkspace().getModElementManager().storeModElement(woodItemTag);

		//Recipes
		Recipe woodRecipe = (Recipe) ModElementTypeRegistry.REGISTRY.get(ModElementType.RECIPE)
				.getModElement(mcreator, new ModElement(workspace, name + "WoodRecipe", ModElementType.RECIPE),
						false).getElementFromGUI();
		woodRecipe.recipeSlots[0] = new MItemBlock(workspace, "CUSTOM:" + logBlock.getModElement().getName());
		woodRecipe.recipeSlots[1] = new MItemBlock(workspace, "CUSTOM:" + logBlock.getModElement().getName());
		woodRecipe.recipeSlots[3] = new MItemBlock(workspace, "CUSTOM:" + logBlock.getModElement().getName());
		woodRecipe.recipeSlots[4] = new MItemBlock(workspace, "CUSTOM:" + logBlock.getModElement().getName());
		woodRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Wood");
		woodRecipe.recipeRetstackSize = 3;
		mcreator.getWorkspace().getModElementManager().storeModElementPicture(woodRecipe);
		mcreator.getWorkspace().addModElement(woodRecipe.getModElement());
		mcreator.getWorkspace().getGenerator().generateElement(woodRecipe);
		mcreator.getWorkspace().getModElementManager().storeModElement(woodRecipe);

		Recipe planksRecipe = (Recipe) ModElementTypeRegistry.REGISTRY.get(ModElementType.RECIPE)
				.getModElement(mcreator, new ModElement(workspace, name + "PlanksRecipe", ModElementType.RECIPE),
						false).getElementFromGUI();
		planksRecipe.recipeSlots[4] = new MItemBlock(workspace, "TAG:" + woodItemTag.namespace + ":" + woodItemTag.name);
		planksRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Planks");
		planksRecipe.recipeShapeless = true;
		planksRecipe.recipeRetstackSize = 4;
		mcreator.getWorkspace().getModElementManager().storeModElementPicture(planksRecipe);
		mcreator.getWorkspace().addModElement(planksRecipe.getModElement());
		mcreator.getWorkspace().getGenerator().generateElement(planksRecipe);
		mcreator.getWorkspace().getModElementManager().storeModElement(planksRecipe);

		Recipe stairsRecipe = (Recipe) ModElementTypeRegistry.REGISTRY.get(ModElementType.RECIPE)
				.getModElement(mcreator, new ModElement(workspace, name + "StairsRecipe", ModElementType.RECIPE),
						false).getElementFromGUI();
		stairsRecipe.recipeSlots[0] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		stairsRecipe.recipeSlots[3] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		stairsRecipe.recipeSlots[4] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		stairsRecipe.recipeSlots[6] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		stairsRecipe.recipeSlots[7] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		stairsRecipe.recipeSlots[8] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		stairsRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Stairs");
		stairsRecipe.recipeRetstackSize = 4;
		mcreator.getWorkspace().getModElementManager().storeModElementPicture(stairsRecipe);
		mcreator.getWorkspace().addModElement(stairsRecipe.getModElement());
		mcreator.getWorkspace().getGenerator().generateElement(stairsRecipe);
		mcreator.getWorkspace().getModElementManager().storeModElement(stairsRecipe);

		Recipe slabRecipe = (Recipe) ModElementTypeRegistry.REGISTRY.get(ModElementType.RECIPE)
				.getModElement(mcreator, new ModElement(workspace, name + "SlabRecipe", ModElementType.RECIPE),
						false).getElementFromGUI();
		slabRecipe.recipeSlots[6] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		slabRecipe.recipeSlots[7] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		slabRecipe.recipeSlots[8] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		slabRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Slab");
		slabRecipe.recipeRetstackSize = 6;
		mcreator.getWorkspace().getModElementManager().storeModElementPicture(slabRecipe);
		mcreator.getWorkspace().addModElement(slabRecipe.getModElement());
		mcreator.getWorkspace().getGenerator().generateElement(slabRecipe);
		mcreator.getWorkspace().getModElementManager().storeModElement(slabRecipe);

		Recipe fenceRecipe = (Recipe) ModElementTypeRegistry.REGISTRY.get(ModElementType.RECIPE)
				.getModElement(mcreator, new ModElement(workspace, name + "FenceRecipe", ModElementType.RECIPE),
						false).getElementFromGUI();
		fenceRecipe.recipeSlots[3] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		fenceRecipe.recipeSlots[4] = new MItemBlock(workspace, "Items.STICK");
		fenceRecipe.recipeSlots[5] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		fenceRecipe.recipeSlots[6] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		fenceRecipe.recipeSlots[7] = new MItemBlock(workspace, "Items.STICK");
		fenceRecipe.recipeSlots[8] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		fenceRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Fence");
		fenceRecipe.recipeRetstackSize = 3;
		mcreator.getWorkspace().getModElementManager().storeModElementPicture(fenceRecipe);
		mcreator.getWorkspace().addModElement(fenceRecipe.getModElement());
		mcreator.getWorkspace().getGenerator().generateElement(fenceRecipe);
		mcreator.getWorkspace().getModElementManager().storeModElement(fenceRecipe);

		Recipe fenceGateRecipe = (Recipe) ModElementTypeRegistry.REGISTRY.get(ModElementType.RECIPE)
				.getModElement(mcreator, new ModElement(workspace, name + "FenceGateRecipe", ModElementType.RECIPE),
						false).getElementFromGUI();
		fenceGateRecipe.recipeSlots[3] = new MItemBlock(workspace, "Items.STICK");
		fenceGateRecipe.recipeSlots[4] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		fenceGateRecipe.recipeSlots[5] = new MItemBlock(workspace, "Items.STICK");
		fenceGateRecipe.recipeSlots[6] = new MItemBlock(workspace, "Items.STICK");
		fenceGateRecipe.recipeSlots[7] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		fenceGateRecipe.recipeSlots[8] = new MItemBlock(workspace, "Items.STICK");
		fenceGateRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "FenceGate");
		fenceGateRecipe.recipeRetstackSize = 1;
		mcreator.getWorkspace().getModElementManager().storeModElementPicture(fenceGateRecipe);
		mcreator.getWorkspace().addModElement(fenceGateRecipe.getModElement());
		mcreator.getWorkspace().getGenerator().generateElement(fenceGateRecipe);
		mcreator.getWorkspace().getModElementManager().storeModElement(fenceGateRecipe);

		Recipe stickRecipe = (Recipe) ModElementTypeRegistry.REGISTRY.get(ModElementType.RECIPE)
				.getModElement(mcreator, new ModElement(workspace, name + "StickRecipe", ModElementType.RECIPE),
						false).getElementFromGUI();
		stickRecipe.recipeSlots[0] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		stickRecipe.recipeSlots[3] = new MItemBlock(workspace, "CUSTOM:" + planksBlock.getModElement().getName());
		stickRecipe.recipeReturnStack = new MItemBlock(workspace, "Items.STICK");
		stickRecipe.recipeRetstackSize = 4;
		mcreator.getWorkspace().getModElementManager().storeModElementPicture(stickRecipe);
		mcreator.getWorkspace().addModElement(stickRecipe.getModElement());
		mcreator.getWorkspace().getGenerator().generateElement(stickRecipe);
		mcreator.getWorkspace().getModElementManager().storeModElement(stickRecipe);
	}

	public static BasicAction getAction(ActionRegistry actionRegistry) {
		return new BasicAction(actionRegistry, "Create wood pack...", e -> open(actionRegistry.getMCreator())) {
			@Override public boolean isEnabled() {
				GeneratorConfiguration gc = actionRegistry.getMCreator().getWorkspace().getGenerator()
						.getGeneratorConfiguration();
				return gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.RECIPE)
						!= GeneratorStats.CoverageStatus.NONE
						&& gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.BLOCK)
						!= GeneratorStats.CoverageStatus.NONE
						&& gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.TAG)
						!= GeneratorStats.CoverageStatus.NONE;
			}
		}.setIcon(UIRES.get("16px.woodpack"));
	}

}
