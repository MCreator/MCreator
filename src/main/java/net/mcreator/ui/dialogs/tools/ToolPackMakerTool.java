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
import net.mcreator.element.types.Recipe;
import net.mcreator.element.types.Tool;
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
import java.util.Collections;
import java.util.List;

public class ToolPackMakerTool extends AbstractPackMakerTool {

	private final VTextField name = new VTextField(25);
	private final JColor color;
	private final JSpinner power = new JSpinner(new SpinnerNumberModel(1, 0.1, 10, 0.1));
	private final MCItemHolder base;

	private ToolPackMakerTool(MCreator mcreator) {
		super(mcreator, "tool_pack", UIRES.get("16px.toolpack").getImage());

		JPanel props = new JPanel(new GridLayout(4, 2, 5, 2));

		color = new JColor(mcreator, false, false);
		base = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems).requireValue(
				"dialog.tools.tool_pack_base_item_validator");

		color.setColor(Theme.current().getInterfaceAccentColor());
		name.enableRealtimeValidation();

		props.add(L10N.label("dialog.tools.tool_pack_base_item"));
		props.add(PanelUtils.centerInPanel(base));

		base.addBlockSelectedListener(e -> {
			try {
				if (base.getBlock() != null) {
					color.setColor(ImageUtils.getAverageColor(
									ImageUtils.toBufferedImage(((ImageIcon) base.getIcon()).getImage())).brighter().brighter()
							.brighter());
					if (base.getBlock().getUnmappedValue().startsWith("CUSTOM:")) {
						name.setText(StringUtils.machineToReadableName(
								base.getBlock().getUnmappedValue().replace("CUSTOM:", "")).split(" ")[0]);
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

		name.setValidator(new ModElementNameValidator(mcreator.getWorkspace(), name,
				L10N.t("dialog.tools.tool_pack_name_validator")));

		validableElements.addValidationElement(name);
		validableElements.addValidationElement(base);

		this.add("Center", PanelUtils.centerInPanel(props));

		this.setSize(600, 290);
		this.setLocationRelativeTo(mcreator);
		this.setVisible(true);
	}

	@Override protected void generatePack(MCreator mcreator) {
		addToolPackToWorkspace(mcreator, mcreator.getWorkspace(), name.getText(), base.getBlock(), color.getColor(),
				(Double) power.getValue());
	}

	static void addToolPackToWorkspace(MCreator mcreator, Workspace workspace, String name, MItemBlock base,
			Color color, double factor) {
		if (!checkIfNamesAvailable(workspace, name + "Pickaxe", name + "Axe", name + "Sword",
				name + "Shovel", name + "Hoe", name + "PickaxeRecipe", name + "AxeRecipe", name + "SwordRecipe",
				name + "ShovelRecipe", name + "HoeRecipe"))
			return;

		String registryName = RegistryNameFixer.fromCamelCase(name);
		String readableName = StringUtils.machineToReadableName(name);

		// select folder the mod pack should be in
		FolderElement folder = mcreator instanceof ModMaker modMaker ?
				modMaker.getWorkspacePanel().currentFolder :
				null;

		// first we generate pickaxe texture
		ImageIcon pickaxe = ImageUtils.drawOver(getCachedTexture("tool_base_stick"),
				ImageUtils.colorize(getCachedTexture("tool_pickaxe"), color, true));
		String pickaxeTextureName = registryName + "_pickaxe";
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(pickaxe.getImage()), mcreator.getFolderManager()
				.getTextureFile(pickaxeTextureName, TextureType.ITEM));

		// then we generate axe texture
		ImageIcon axe = ImageUtils.drawOver(getCachedTexture("tool_base_stick"),
				ImageUtils.colorize(getCachedTexture("tool_axe"), color, true));
		String axeTextureName = registryName + "_axe";
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(axe.getImage()),
				mcreator.getFolderManager().getTextureFile(axeTextureName, TextureType.ITEM));

		// then we generate sword texture
		ImageIcon sword = ImageUtils.drawOver(getCachedTexture("tool_base_stick"),
				ImageUtils.colorize(getCachedTexture("tool_sword"), color, true));
		String swordTextureName = registryName + "_sword";
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(sword.getImage()),
				mcreator.getFolderManager().getTextureFile(swordTextureName, TextureType.ITEM));

		// then we generate shovel texture
		ImageIcon shovel = ImageUtils.drawOver(
				ImageUtils.drawOver(getCachedTexture("tool_base_stick"), getCachedTexture("tool_shovel_grip")),
				ImageUtils.colorize(getCachedTexture("tool_shovel_top"), color, true));
		String shovelTextureName = registryName + "_shovel";
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(shovel.getImage()),
				mcreator.getFolderManager().getTextureFile(shovelTextureName, TextureType.ITEM));

		// then we generate hoe texture
		ImageIcon hoe = ImageUtils.drawOver(getCachedTexture("tool_base_stick"),
				ImageUtils.colorize(getCachedTexture("tool_hoe"), color, true));
		String hoeTextureName = registryName + "_hoe";
		FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(hoe.getImage()),
				mcreator.getFolderManager().getTextureFile(hoeTextureName, TextureType.ITEM));

		// We use element GUIs to get the default values for the elements
		Tool pickaxeTool = (Tool) ModElementType.TOOL.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Pickaxe", ModElementType.TOOL), false).getElementFromGUI();
		pickaxeTool.name = readableName + " Pickaxe";
		pickaxeTool.texture = new TextureHolder(workspace, pickaxeTextureName);
		pickaxeTool.toolType = "Pickaxe";
		pickaxeTool.repairItems = Collections.singletonList(base);
		pickaxeTool.creativeTabs = List.of(new TabEntry(workspace, "TOOLS"));
		setParametersBasedOnFactorAndAddElement(mcreator, factor, pickaxeTool, folder);
		pickaxeTool.attackSpeed = (double) Math.round(1.2f * factor);

		Tool axeTool = (Tool) ModElementType.TOOL.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Axe", ModElementType.TOOL), false).getElementFromGUI();
		axeTool.name = readableName + " Axe";
		axeTool.texture = new TextureHolder(workspace, axeTextureName);
		axeTool.toolType = "Axe";
		axeTool.repairItems = Collections.singletonList(base);
		axeTool.creativeTabs = List.of(new TabEntry(workspace, "TOOLS"));
		setParametersBasedOnFactorAndAddElement(mcreator, factor, axeTool, folder);
		axeTool.damageVsEntity = (double) Math.round(9.0f * factor);
		axeTool.attackSpeed = (double) Math.round(0.9f * factor);

		Tool swordTool = (Tool) ModElementType.TOOL.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Sword", ModElementType.TOOL), false).getElementFromGUI();
		swordTool.name = readableName + " Sword";
		swordTool.texture = new TextureHolder(workspace, swordTextureName);
		swordTool.toolType = "Sword";
		swordTool.creativeTabs = List.of(new TabEntry(workspace, "COMBAT"));
		swordTool.repairItems = Collections.singletonList(base);
		setParametersBasedOnFactorAndAddElement(mcreator, factor, swordTool, folder);
		swordTool.damageVsEntity = (double) Math.round(6.0f * factor);
		swordTool.attackSpeed = (double) Math.round(1.6f * factor);

		Tool shovelTool = (Tool) ModElementType.TOOL.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Shovel", ModElementType.TOOL), false).getElementFromGUI();
		shovelTool.name = readableName + " Shovel";
		shovelTool.texture = new TextureHolder(workspace, shovelTextureName);
		shovelTool.toolType = "Spade";
		shovelTool.repairItems = Collections.singletonList(base);
		shovelTool.creativeTabs = List.of(new TabEntry(workspace, "TOOLS"));
		setParametersBasedOnFactorAndAddElement(mcreator, factor, shovelTool, folder);
		shovelTool.damageVsEntity = (double) Math.round(4.5f * factor);
		shovelTool.attackSpeed = (double) Math.round(1.0f * factor);

		Tool hoeTool = (Tool) ModElementType.TOOL.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Hoe", ModElementType.TOOL), false).getElementFromGUI();
		hoeTool.name = readableName + " Hoe";
		hoeTool.texture = new TextureHolder(workspace, hoeTextureName);
		hoeTool.toolType = "Hoe";
		hoeTool.repairItems = Collections.singletonList(base);
		hoeTool.creativeTabs = List.of(new TabEntry(workspace, "TOOLS"));
		setParametersBasedOnFactorAndAddElement(mcreator, factor, hoeTool, folder);
		hoeTool.damageVsEntity = (double) Math.round(1.0f * factor);

		Recipe pickaxeRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "PickaxeRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		pickaxeRecipe.craftingBookCategory = "EQUIPMENT";
		pickaxeRecipe.recipeSlots[0] = base;
		pickaxeRecipe.recipeSlots[1] = base;
		pickaxeRecipe.recipeSlots[2] = base;
		pickaxeRecipe.recipeSlots[4] = new MItemBlock(workspace, "Items.STICK");
		pickaxeRecipe.recipeSlots[7] = new MItemBlock(workspace, "Items.STICK");
		pickaxeRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Pickaxe");
		pickaxeRecipe.unlockingItems.add(base);
		addGeneratableElementToWorkspace(workspace, folder, pickaxeRecipe);

		Recipe axeRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "AxeRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		axeRecipe.craftingBookCategory = "EQUIPMENT";
		axeRecipe.recipeSlots[0] = base;
		axeRecipe.recipeSlots[1] = base;
		axeRecipe.recipeSlots[3] = base;
		axeRecipe.recipeSlots[4] = new MItemBlock(workspace, "Items.STICK");
		axeRecipe.recipeSlots[7] = new MItemBlock(workspace, "Items.STICK");
		axeRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Axe");
		axeRecipe.unlockingItems.add(base);
		addGeneratableElementToWorkspace(workspace, folder, axeRecipe);

		Recipe swordRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "SwordRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		swordRecipe.craftingBookCategory = "EQUIPMENT";
		swordRecipe.recipeSlots[1] = base;
		swordRecipe.recipeSlots[4] = base;
		swordRecipe.recipeSlots[7] = new MItemBlock(workspace, "Items.STICK");
		swordRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Sword");
		swordRecipe.unlockingItems.add(base);
		addGeneratableElementToWorkspace(workspace, folder, swordRecipe);

		Recipe shovelRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "ShovelRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		shovelRecipe.craftingBookCategory = "EQUIPMENT";
		shovelRecipe.recipeSlots[1] = base;
		shovelRecipe.recipeSlots[4] = new MItemBlock(workspace, "Items.STICK");
		shovelRecipe.recipeSlots[7] = new MItemBlock(workspace, "Items.STICK");
		shovelRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Shovel");
		shovelRecipe.unlockingItems.add(base);
		addGeneratableElementToWorkspace(workspace, folder, shovelRecipe);

		Recipe hoeRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "HoeRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		hoeRecipe.craftingBookCategory = "EQUIPMENT";
		hoeRecipe.recipeSlots[0] = base;
		hoeRecipe.recipeSlots[1] = base;
		hoeRecipe.recipeSlots[4] = new MItemBlock(workspace, "Items.STICK");
		hoeRecipe.recipeSlots[7] = new MItemBlock(workspace, "Items.STICK");
		hoeRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Hoe");
		hoeRecipe.unlockingItems.add(base);
		addGeneratableElementToWorkspace(workspace, folder, hoeRecipe);
	}

	private static void setParametersBasedOnFactorAndAddElement(MCreator mcreator, double factor, Tool tool,
			FolderElement folder) {
		if (factor < 0.5) {
			tool.blockDropsTier = "WOOD";
		} else if (factor < 1) {
			tool.blockDropsTier = "STONE";
		} else if (factor == 1) {
			tool.blockDropsTier = "IRON";
		} else {
			tool.blockDropsTier = "DIAMOND";
		}
		tool.efficiency = (double) Math.round(6.0f * Math.pow(factor, 0.6));
		tool.enchantability = (int) Math.round(14 * factor);
		tool.damageVsEntity = (double) Math.round(4.0f * factor);
		tool.usageCount = (int) Math.round(250 * Math.pow(factor, 1.4));
		tool.attackSpeed = (double) Math.round(3.0f * factor);
		addGeneratableElementToWorkspace(mcreator.getWorkspace(), folder, tool);
	}

	public static BasicAction getAction(ActionRegistry actionRegistry) {
		return new BasicAction(actionRegistry, L10N.t("action.pack_tools.tool"),
				e -> new ToolPackMakerTool(actionRegistry.getMCreator())) {
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
