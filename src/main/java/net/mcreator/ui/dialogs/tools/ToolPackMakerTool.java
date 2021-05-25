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
import net.mcreator.element.ModElementTypeRegistry;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.types.Recipe;
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
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.FolderElement;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.Locale;

public class ToolPackMakerTool {

	private static void open(MCreator mcreator) {
		MCreatorDialog dialog = new MCreatorDialog(mcreator, L10N.t("dialog.tools.tool_pack_title"), true);
		dialog.setLayout(new BorderLayout(10, 10));

		dialog.setIconImage(UIRES.get("16px.toolpack").getImage());

		dialog.add("North", PanelUtils.centerInPanel(L10N.label("dialog.tools.tool_pack_info")));

		JPanel props = new JPanel(new GridLayout(4, 2, 5, 5));

		VTextField name = new VTextField(25);
		JColor color = new JColor(mcreator);
		JSpinner power = new JSpinner(new SpinnerNumberModel(1, 0.1, 10, 0.1));
		MCItemHolder base = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);

		color.setColor((Color) UIManager.get("MCreatorLAF.MAIN_TINT"));
		name.enableRealtimeValidation();

		props.add(L10N.label("dialog.tools.tool_pack_base_item"));
		props.add(PanelUtils.centerInPanel(base));

		base.setBlockSelectedListener(e -> {
			try {
				if (base.getBlock() != null) {
					color.setColor(ImageUtils
							.getAverageColor(ImageUtils.toBufferedImage(((ImageIcon) base.getIcon()).getImage()))
							.brighter().brighter().brighter());
					if (base.getBlock().getUnmappedValue().startsWith("CUSTOM:")) {
						name.setText(StringUtils
								.machineToReadableName(base.getBlock().getUnmappedValue().replace("CUSTOM:", ""))
								.split(" ")[0]);
					}
				}
			} catch (Exception ignored) {
			}
		});

		props.add(L10N.label("dialog.tools.tool_pack_name"));
		props.add(name);

		props.add(L10N.label("dialog.tools.tool_pack_color_accent"));
		props.add(color);

		props.add(L10N.label("dialog.tools.tool_pack_power_factor"));
		props.add(power);

		name.setValidator(new ModElementNameValidator(mcreator.getWorkspace(), name));

		dialog.add("Center", PanelUtils.centerInPanel(props));
		JButton ok = L10N.button("dialog.tools.tool_pack_create");
		JButton canecel = L10N.button(UIManager.getString("OptionPane.cancelButtonText"));
		canecel.addActionListener(e -> dialog.setVisible(false));
		dialog.add("South", PanelUtils.join(ok, canecel));

		ok.addActionListener(e -> {
			if (name.getValidationStatus().getValidationResultType() != Validator.ValidationResultType.ERROR) {
				dialog.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				addToolPackToWorkspace(mcreator, mcreator.getWorkspace(), name.getText(), base.getBlock(),
						color.getColor(), (Double) power.getValue());
				mcreator.mv.updateMods();
				dialog.setCursor(Cursor.getDefaultCursor());
				dialog.setVisible(false);
			}
		});

		dialog.setSize(600, 280);
		dialog.setLocationRelativeTo(mcreator);
		dialog.setVisible(true);
	}

	static void addToolPackToWorkspace(MCreator mcreator, Workspace workspace, String name, MItemBlock base,
			Color color, double factor) {
		// select folder the mod pack should be in
		FolderElement folder = null;
		if (!mcreator.mv.currentFolder.equals(mcreator.getWorkspace().getFoldersRoot()))
			folder = mcreator.mv.currentFolder;

		// first we generate pickaxe texture
		ImageIcon pickaxe = ImageUtils.drawOver(ImageMakerTexturesCache.CACHE
				.get(new ResourcePointer("templates/textures/texturemaker/tool_base_stick.png")), ImageUtils.colorize(
				ImageMakerTexturesCache.CACHE
						.get(new ResourcePointer("templates/textures/texturemaker/tool_pickaxe.png")), color, true));
		String pickaxeTextureName = (name + "_pickaxe").toLowerCase(Locale.ENGLISH);
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(pickaxe.getImage()),
				mcreator.getFolderManager().getTextureFileFromType(RegistryNameFixer.fix(pickaxeTextureName), "item"));

		// then we generate axe texture
		ImageIcon axe = ImageUtils.drawOver(ImageMakerTexturesCache.CACHE
				.get(new ResourcePointer("templates/textures/texturemaker/tool_base_stick.png")), ImageUtils.colorize(
				ImageMakerTexturesCache.CACHE.get(new ResourcePointer("templates/textures/texturemaker/tool_axe.png")),
				color, true));
		String axeTextureName = (name + "_axe").toLowerCase(Locale.ENGLISH);
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(axe.getImage()),
				mcreator.getFolderManager().getTextureFileFromType(RegistryNameFixer.fix(axeTextureName), "item"));

		// then we generate sword texture
		ImageIcon sword = ImageUtils.drawOver(ImageMakerTexturesCache.CACHE
				.get(new ResourcePointer("templates/textures/texturemaker/tool_base_stick.png")), ImageUtils.colorize(
				ImageMakerTexturesCache.CACHE
						.get(new ResourcePointer("templates/textures/texturemaker/tool_sword.png")), color, true));
		String swordTextureName = (name + "_sword").toLowerCase(Locale.ENGLISH);
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(sword.getImage()),
				mcreator.getFolderManager().getTextureFileFromType(RegistryNameFixer.fix(swordTextureName), "item"));

		// then we generate sword texture
		ImageIcon shovel = ImageUtils.drawOver(ImageUtils.drawOver(ImageMakerTexturesCache.CACHE
						.get(new ResourcePointer("templates/textures/texturemaker/tool_base_stick.png")),
				ImageMakerTexturesCache.CACHE
						.get(new ResourcePointer("templates/textures/texturemaker/tool_shovel_grip.png"))), ImageUtils
				.colorize(ImageMakerTexturesCache.CACHE
						.get(new ResourcePointer("templates/textures/texturemaker/tool_shovel_top.png")), color, true));
		String shovelTextureName = (name + "_shovel").toLowerCase(Locale.ENGLISH);
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(shovel.getImage()),
				mcreator.getFolderManager().getTextureFileFromType(RegistryNameFixer.fix(shovelTextureName), "item"));

		// then we generate hoe texture
		ImageIcon hoe = ImageUtils.drawOver(ImageMakerTexturesCache.CACHE
				.get(new ResourcePointer("templates/textures/texturemaker/tool_base_stick.png")), ImageUtils.colorize(
				ImageMakerTexturesCache.CACHE.get(new ResourcePointer("templates/textures/texturemaker/tool_hoe.png")),
				color, true));
		String hoeTextureName = (name + "_hoe").toLowerCase(Locale.ENGLISH);
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(hoe.getImage()),
				mcreator.getFolderManager().getTextureFileFromType(RegistryNameFixer.fix(hoeTextureName), "item"));

		// we use Tool GUI to get default values for the block element (kinda hacky!)
		Tool pickaxeTool = (Tool) ModElementTypeRegistry.REGISTRY.get(ModElementType.TOOL)
				.getModElement(mcreator, new ModElement(workspace, name + "Pickaxe", ModElementType.TOOL), false)
				.getElementFromGUI();
		pickaxeTool.name = name + " Pickaxe";
		pickaxeTool.texture = pickaxeTextureName;
		pickaxeTool.toolType = "Pickaxe";
		pickaxeTool.repairItems = Collections.singletonList(base);
		setParametersBasedOnFactorAndAddElement(mcreator, factor, pickaxeTool, folder);

		// we use Tool GUI to get default values for the block element (kinda hacky!)
		Tool axeTool = (Tool) ModElementTypeRegistry.REGISTRY.get(ModElementType.TOOL)
				.getModElement(mcreator, new ModElement(workspace, name + "Axe", ModElementType.TOOL), false)
				.getElementFromGUI();
		axeTool.name = name + " Axe";
		axeTool.texture = axeTextureName;
		axeTool.toolType = "Axe";
		axeTool.repairItems = Collections.singletonList(base);
		setParametersBasedOnFactorAndAddElement(mcreator, factor, axeTool, folder);
		axeTool.damageVsEntity = (double) Math.round(9.0f * factor);

		// we use Tool GUI to get default values for the block element (kinda hacky!)
		Tool swordTool = (Tool) ModElementTypeRegistry.REGISTRY.get(ModElementType.TOOL)
				.getModElement(mcreator, new ModElement(workspace, name + "Sword", ModElementType.TOOL), false)
				.getElementFromGUI();
		swordTool.name = name + " Sword";
		swordTool.texture = swordTextureName;
		swordTool.toolType = "Sword";
		swordTool.creativeTab = new TabEntry(mcreator.getWorkspace(), "COMBAT");
		swordTool.repairItems = Collections.singletonList(base);
		setParametersBasedOnFactorAndAddElement(mcreator, factor, swordTool, folder);
		swordTool.damageVsEntity = (double) Math.round(6.0f * factor);

		// we use Tool GUI to get default values for the block element (kinda hacky!)
		Tool shovelTool = (Tool) ModElementTypeRegistry.REGISTRY.get(ModElementType.TOOL)
				.getModElement(mcreator, new ModElement(workspace, name + "Shovel", ModElementType.TOOL), false)
				.getElementFromGUI();
		shovelTool.name = name + " Shovel";
		shovelTool.texture = shovelTextureName;
		shovelTool.toolType = "Spade";
		shovelTool.repairItems = Collections.singletonList(base);
		setParametersBasedOnFactorAndAddElement(mcreator, factor, shovelTool, folder);

		// we use Tool GUI to get default values for the block element (kinda hacky!)
		Tool hoeTool = (Tool) ModElementTypeRegistry.REGISTRY.get(ModElementType.TOOL)
				.getModElement(mcreator, new ModElement(workspace, name + "Hoe", ModElementType.TOOL), false)
				.getElementFromGUI();
		hoeTool.name = name + " Hoe";
		hoeTool.texture = hoeTextureName;
		hoeTool.toolType = "Hoe";
		hoeTool.repairItems = Collections.singletonList(base);
		setParametersBasedOnFactorAndAddElement(mcreator, factor, hoeTool, folder);

		Recipe pickaxeRecipe = (Recipe) ModElementTypeRegistry.REGISTRY.get(ModElementType.RECIPE)
				.getModElement(mcreator, new ModElement(workspace, name + "PickaxeRecipe", ModElementType.RECIPE),
						false).getElementFromGUI();
		pickaxeRecipe.recipeSlots[0] = base;
		pickaxeRecipe.recipeSlots[1] = base;
		pickaxeRecipe.recipeSlots[2] = base;
		pickaxeRecipe.recipeSlots[4] = new MItemBlock(workspace, "Items.STICK");
		pickaxeRecipe.recipeSlots[7] = new MItemBlock(workspace, "Items.STICK");
		pickaxeRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Pickaxe");

		pickaxeRecipe.getModElement().setParentFolder(folder);
		mcreator.getModElementManager().storeModElementPicture(pickaxeRecipe);
		mcreator.getWorkspace().addModElement(pickaxeRecipe.getModElement());
		mcreator.getGenerator().generateElement(pickaxeRecipe);
		mcreator.getModElementManager().storeModElement(pickaxeRecipe);

		Recipe axeRecipe = (Recipe) ModElementTypeRegistry.REGISTRY.get(ModElementType.RECIPE)
				.getModElement(mcreator, new ModElement(workspace, name + "AxeRecipe", ModElementType.RECIPE), false)
				.getElementFromGUI();
		axeRecipe.recipeSlots[0] = base;
		axeRecipe.recipeSlots[1] = base;
		axeRecipe.recipeSlots[3] = base;
		axeRecipe.recipeSlots[4] = new MItemBlock(workspace, "Items.STICK");
		axeRecipe.recipeSlots[7] = new MItemBlock(workspace, "Items.STICK");
		axeRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Axe");

		axeRecipe.getModElement().setParentFolder(folder);
		mcreator.getModElementManager().storeModElementPicture(axeRecipe);
		mcreator.getWorkspace().addModElement(axeRecipe.getModElement());
		mcreator.getGenerator().generateElement(axeRecipe);
		mcreator.getModElementManager().storeModElement(axeRecipe);

		Recipe swordRecipe = (Recipe) ModElementTypeRegistry.REGISTRY.get(ModElementType.RECIPE)
				.getModElement(mcreator, new ModElement(workspace, name + "SwordRecipe", ModElementType.RECIPE), false)
				.getElementFromGUI();
		swordRecipe.recipeSlots[1] = base;
		swordRecipe.recipeSlots[4] = base;
		swordRecipe.recipeSlots[7] = new MItemBlock(workspace, "Items.STICK");
		swordRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Sword");

		swordRecipe.getModElement().setParentFolder(folder);
		mcreator.getModElementManager().storeModElementPicture(swordRecipe);
		mcreator.getWorkspace().addModElement(swordRecipe.getModElement());
		mcreator.getGenerator().generateElement(swordRecipe);
		mcreator.getModElementManager().storeModElement(swordRecipe);

		Recipe shovelRecipe = (Recipe) ModElementTypeRegistry.REGISTRY.get(ModElementType.RECIPE)
				.getModElement(mcreator, new ModElement(workspace, name + "ShovelRecipe", ModElementType.RECIPE), false)
				.getElementFromGUI();
		shovelRecipe.recipeSlots[1] = base;
		shovelRecipe.recipeSlots[4] = new MItemBlock(workspace, "Items.STICK");
		shovelRecipe.recipeSlots[7] = new MItemBlock(workspace, "Items.STICK");
		shovelRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Shovel");

		shovelRecipe.getModElement().setParentFolder(folder);
		mcreator.getModElementManager().storeModElementPicture(shovelRecipe);
		mcreator.getWorkspace().addModElement(shovelRecipe.getModElement());
		mcreator.getGenerator().generateElement(shovelRecipe);
		mcreator.getModElementManager().storeModElement(shovelRecipe);

		Recipe hoeRecipe = (Recipe) ModElementTypeRegistry.REGISTRY.get(ModElementType.RECIPE)
				.getModElement(mcreator, new ModElement(workspace, name + "HoeRecipe", ModElementType.RECIPE), false)
				.getElementFromGUI();
		hoeRecipe.recipeSlots[0] = base;
		hoeRecipe.recipeSlots[1] = base;
		hoeRecipe.recipeSlots[4] = new MItemBlock(workspace, "Items.STICK");
		hoeRecipe.recipeSlots[7] = new MItemBlock(workspace, "Items.STICK");
		hoeRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Hoe");

		hoeRecipe.getModElement().setParentFolder(folder);
		mcreator.getModElementManager().storeModElementPicture(hoeRecipe);
		mcreator.getWorkspace().addModElement(hoeRecipe.getModElement());
		mcreator.getGenerator().generateElement(hoeRecipe);
		mcreator.getModElementManager().storeModElement(hoeRecipe);
	}

	private static void setParametersBasedOnFactorAndAddElement(MCreator mcreator, double factor, Tool tool,
			FolderElement folder) {
		tool.harvestLevel = (int) Math.round(2 * factor);
		tool.efficiency = (double) Math.round(6.0f * Math.pow(factor, 0.6));
		tool.enchantability = (int) Math.round(14 * factor);
		tool.damageVsEntity = (double) Math.round(2.0f * factor);
		tool.usageCount = (int) Math.round(250 * Math.pow(factor, 1.4));

		tool.getModElement().setParentFolder(folder);
		mcreator.getModElementManager().storeModElementPicture(tool);
		mcreator.getWorkspace().addModElement(tool.getModElement());
		mcreator.getGenerator().generateElement(tool);
		mcreator.getModElementManager().storeModElement(tool);
	}

	public static BasicAction getAction(ActionRegistry actionRegistry) {
		return new BasicAction(actionRegistry, L10N.t("action.pack_tools.tool"),
				e -> open(actionRegistry.getMCreator())) {
			@Override public boolean isEnabled() {
				GeneratorConfiguration gc = actionRegistry.getMCreator().getGeneratorConfiguration();
				return gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.RECIPE)
						!= GeneratorStats.CoverageStatus.NONE
						&& gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.TOOL)
						!= GeneratorStats.CoverageStatus.NONE;
			}
		}.setIcon(UIRES.get("16px.toolpack"));
	}

}
