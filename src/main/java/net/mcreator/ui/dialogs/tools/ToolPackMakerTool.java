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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToolPackMakerTool extends AbstractPackMakerTool {

	private final VTextField name = new VTextField(25);
	private final JColor color;
	private final JSpinner power = new JSpinner(new SpinnerNumberModel(1, 0.1, 10, 0.1));
	private final MCItemHolder base;

	private final TextureSelectionButton pickaxeTexture;
	private final TextureSelectionButton axeTexture;
	private final TextureSelectionButton swordTexture;
	private final TextureSelectionButton shovelTexture;
	private final TextureSelectionButton hoeTexture;

	private ToolPackMakerTool(MCreator mcreator) {
		super(mcreator, "tool_pack", UIRES.get("16px.toolpack").getImage());

		// Main properties page
		JPanel props = new JPanel(new GridLayout(4, 2, 5, 2));

		color = new JColor(mcreator, false, false);
		base = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems).requireValue(
				"dialog.tools.tool_pack_base_item_validator");

		color.setColor(Theme.current().getInterfaceAccentColor());
		name.enableRealtimeValidation();

		props.add(L10N.label("dialog.tools.tool_pack_base_item"));
		props.add(PanelUtils.centerInPanel(base));

		base.addBlockSelectedListener(_ -> {
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

		// Textures page
		JPanel texturesPanel = new JPanel(new GridLayout(3, 2, 50, 5));

		pickaxeTexture = new TextureSelectionButton(mcreator, TextureType.ITEM, 64);
		axeTexture = new TextureSelectionButton(mcreator, TextureType.ITEM, 64);
		swordTexture = new TextureSelectionButton(mcreator, TextureType.ITEM, 64);
		shovelTexture = new TextureSelectionButton(mcreator, TextureType.ITEM, 64);
		hoeTexture = new TextureSelectionButton(mcreator, TextureType.ITEM, 64);

		texturesPanel.add(PanelUtils.gridElements(1, 2, L10N.label("dialog.tools.tool_pack_textures.pickaxe"),
				PanelUtils.totalCenterInPanel(pickaxeTexture)));
		texturesPanel.add(PanelUtils.gridElements(1, 2, L10N.label("dialog.tools.tool_pack_textures.axe"),
				PanelUtils.totalCenterInPanel(axeTexture)));
		texturesPanel.add(PanelUtils.gridElements(1, 2, L10N.label("dialog.tools.tool_pack_textures.sword"),
				PanelUtils.totalCenterInPanel(swordTexture)));
		texturesPanel.add(PanelUtils.gridElements(1, 2, L10N.label("dialog.tools.tool_pack_textures.shovel"),
				PanelUtils.totalCenterInPanel(shovelTexture)));
		texturesPanel.add(PanelUtils.gridElements(1, 2, L10N.label("dialog.tools.tool_pack_textures.hoe"),
				PanelUtils.totalCenterInPanel(hoeTexture)));
		texturesPanel.add(new JLabel());

		addPage(L10N.t("dialog.tools.pack_makers.properties"), props);
		addPage(L10N.t("dialog.tools.pack_makers.textures"), PanelUtils.column(15,
				PanelUtils.centerInPanel(L10N.label("dialog.tools.pack_makers.empty_textures_message")),
				texturesPanel));

		this.add("Center", tabs);

		this.setSize(600, 420);
		this.setLocationRelativeTo(mcreator);
		this.setVisible(true);
	}

	@Override protected void generatePack(MCreator mcreator) {
		addToolPackToWorkspace(toGenerate, mcreator, mcreator.getWorkspace(), name.getText(), base.getBlock(),
				color.getColor(), (Double) power.getValue(), makeTextureMap());
	}

	public static String[] getPackElementNames(String name) {
		return new String[] { name + "Pickaxe", name + "Axe", name + "Sword", name + "Shovel", name + "Hoe",
				name + "PickaxeRecipe", name + "AxeRecipe", name + "SwordRecipe", name + "ShovelRecipe",
				name + "HoeRecipe" };
	}

	public static boolean addToolPackToWorkspace(@Nullable List<GeneratableElement> generationQueue, MCreator mcreator,
			Workspace workspace, String name, MItemBlock base, Color color, double factor,
			@Nonnull Map<String, TextureHolder> textureMap) {
		if (!checkIfNamesAvailable(workspace, getPackElementNames(name)))
			return false;

		String registryName = RegistryNameFixer.fromCamelCase(name);
		String readableName = StringUtils.machineToReadableName(name);

		// select folder the mod pack should be in
		FolderElement folder = mcreator instanceof ModMaker modMaker ?
				modMaker.getWorkspacePanel().currentFolder :
				null;

		// first we generate pickaxe texture
		if (!textureMap.containsKey("pickaxe")) {
			ImageIcon pickaxe = baseAndColoredOverlay("tool_base_stick", "tool_pickaxe", color);
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(pickaxe.getImage()),
					mcreator.getFolderManager().getTextureFile(registryName + "_pickaxe", TextureType.ITEM));
			textureMap.put("pickaxe", new TextureHolder(workspace, registryName + "_pickaxe"));
		}

		// then we generate axe texture
		if (!textureMap.containsKey("axe")) {
			ImageIcon axe = baseAndColoredOverlay("tool_base_stick", "tool_axe", color);
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(axe.getImage()),
					mcreator.getFolderManager().getTextureFile(registryName + "_axe", TextureType.ITEM));
			textureMap.put("axe", new TextureHolder(workspace, registryName + "_axe"));
		}

		// then we generate sword texture
		if (!textureMap.containsKey("sword")) {
			ImageIcon axe = baseAndColoredOverlay("tool_base_stick", "tool_sword", color);
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(axe.getImage()),
					mcreator.getFolderManager().getTextureFile(registryName + "_sword", TextureType.ITEM));
			textureMap.put("sword", new TextureHolder(workspace, registryName + "_sword"));
		}

		// then we generate shovel texture
		if (!textureMap.containsKey("shovel")) {
			ImageIcon shovel = ImageUtils.drawOver(
					ImageUtils.drawOver(getCachedTexture("tool_base_stick"), getCachedTexture("tool_shovel_grip")),
					ImageUtils.colorize(getCachedTexture("tool_shovel_top"), color, true));
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(shovel.getImage()),
					mcreator.getFolderManager().getTextureFile(registryName + "_shovel", TextureType.ITEM));
			textureMap.put("shovel", new TextureHolder(workspace, registryName + "_shovel"));
		}

		// then we generate hoe texture
		if (!textureMap.containsKey("hoe")) {
			ImageIcon axe = baseAndColoredOverlay("tool_base_stick", "tool_hoe", color);
			FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(axe.getImage()),
					mcreator.getFolderManager().getTextureFile(registryName + "_hoe", TextureType.ITEM));
			textureMap.put("hoe", new TextureHolder(workspace, registryName + "_hoe"));
		}

		// We use element GUIs to get the default values for the elements
		Tool pickaxeTool = (Tool) ModElementType.TOOL.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Pickaxe", ModElementType.TOOL), false).getElementFromGUI();
		pickaxeTool.name = readableName + " Pickaxe";
		pickaxeTool.texture = textureMap.get("pickaxe");
		pickaxeTool.toolType = "Pickaxe";
		pickaxeTool.repairItems = Collections.singletonList(base);
		pickaxeTool.creativeTabs = List.of(new TabEntry(workspace, "TOOLS"));
		setParametersBasedOnFactorAndAddElement(generationQueue, mcreator, factor, pickaxeTool, folder);
		pickaxeTool.attackSpeed = (double) Math.round(1.2f * factor);

		Tool axeTool = (Tool) ModElementType.TOOL.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Axe", ModElementType.TOOL), false).getElementFromGUI();
		axeTool.name = readableName + " Axe";
		axeTool.texture = textureMap.get("axe");
		axeTool.toolType = "Axe";
		axeTool.repairItems = Collections.singletonList(base);
		axeTool.creativeTabs = List.of(new TabEntry(workspace, "TOOLS"));
		setParametersBasedOnFactorAndAddElement(generationQueue, mcreator, factor, axeTool, folder);
		axeTool.damageVsEntity = (double) Math.round(9.0f * factor);
		axeTool.attackSpeed = (double) Math.round(0.9f * factor);

		Tool swordTool = (Tool) ModElementType.TOOL.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Sword", ModElementType.TOOL), false).getElementFromGUI();
		swordTool.name = readableName + " Sword";
		swordTool.texture = textureMap.get("sword");
		swordTool.toolType = "Sword";
		swordTool.creativeTabs = List.of(new TabEntry(workspace, "COMBAT"));
		swordTool.repairItems = Collections.singletonList(base);
		setParametersBasedOnFactorAndAddElement(generationQueue, mcreator, factor, swordTool, folder);
		swordTool.damageVsEntity = (double) Math.round(6.0f * factor);
		swordTool.attackSpeed = (double) Math.round(1.6f * factor);

		Tool shovelTool = (Tool) ModElementType.TOOL.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Shovel", ModElementType.TOOL), false).getElementFromGUI();
		shovelTool.name = readableName + " Shovel";
		shovelTool.texture = textureMap.get("shovel");
		shovelTool.toolType = "Spade";
		shovelTool.repairItems = Collections.singletonList(base);
		shovelTool.creativeTabs = List.of(new TabEntry(workspace, "TOOLS"));
		setParametersBasedOnFactorAndAddElement(generationQueue, mcreator, factor, shovelTool, folder);
		shovelTool.damageVsEntity = (double) Math.round(4.5f * factor);
		shovelTool.attackSpeed = (double) Math.round(1.0f * factor);

		Tool hoeTool = (Tool) ModElementType.TOOL.getModElementGUI(mcreator,
				new ModElement(workspace, name + "Hoe", ModElementType.TOOL), false).getElementFromGUI();
		hoeTool.name = readableName + " Hoe";
		hoeTool.texture = textureMap.get("hoe");
		hoeTool.toolType = "Hoe";
		hoeTool.repairItems = Collections.singletonList(base);
		hoeTool.creativeTabs = List.of(new TabEntry(workspace, "TOOLS"));
		setParametersBasedOnFactorAndAddElement(generationQueue, mcreator, factor, hoeTool, folder);
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
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, pickaxeRecipe);

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
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, axeRecipe);

		Recipe swordRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "SwordRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		swordRecipe.craftingBookCategory = "EQUIPMENT";
		swordRecipe.recipeSlots[1] = base;
		swordRecipe.recipeSlots[4] = base;
		swordRecipe.recipeSlots[7] = new MItemBlock(workspace, "Items.STICK");
		swordRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Sword");
		swordRecipe.unlockingItems.add(base);
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, swordRecipe);

		Recipe shovelRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "ShovelRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		shovelRecipe.craftingBookCategory = "EQUIPMENT";
		shovelRecipe.recipeSlots[1] = base;
		shovelRecipe.recipeSlots[4] = new MItemBlock(workspace, "Items.STICK");
		shovelRecipe.recipeSlots[7] = new MItemBlock(workspace, "Items.STICK");
		shovelRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Shovel");
		shovelRecipe.unlockingItems.add(base);
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, shovelRecipe);

		Recipe hoeRecipe = (Recipe) ModElementType.RECIPE.getModElementGUI(mcreator,
				new ModElement(workspace, name + "HoeRecipe", ModElementType.RECIPE), false).getElementFromGUI();
		hoeRecipe.craftingBookCategory = "EQUIPMENT";
		hoeRecipe.recipeSlots[0] = base;
		hoeRecipe.recipeSlots[1] = base;
		hoeRecipe.recipeSlots[4] = new MItemBlock(workspace, "Items.STICK");
		hoeRecipe.recipeSlots[7] = new MItemBlock(workspace, "Items.STICK");
		hoeRecipe.recipeReturnStack = new MItemBlock(workspace, "CUSTOM:" + name + "Hoe");
		hoeRecipe.unlockingItems.add(base);
		addGeneratableElementToWorkspace(generationQueue, workspace, folder, hoeRecipe);

		return true;
	}

	private static void setParametersBasedOnFactorAndAddElement(@Nullable List<GeneratableElement> generationQueue,
			MCreator mcreator, double factor, Tool tool, FolderElement folder) {
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
		addGeneratableElementToWorkspace(generationQueue, mcreator.getWorkspace(), folder, tool);
	}

	private Map<String, TextureHolder> makeTextureMap() {
		HashMap<String, TextureHolder> map = new HashMap<>();

		addToTextureMap(map, pickaxeTexture, "pickaxe");
		addToTextureMap(map, axeTexture, "axe");
		addToTextureMap(map, swordTexture, "sword");
		addToTextureMap(map, shovelTexture, "shovel");
		addToTextureMap(map, hoeTexture, "hoe");

		return map;
	}

	public static boolean isSupported(GeneratorConfiguration gc) {
		return gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.RECIPE)
				!= GeneratorStats.CoverageStatus.NONE
				&& gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.TOOL)
				!= GeneratorStats.CoverageStatus.NONE;
	}

	public static BasicAction getAction(ActionRegistry actionRegistry) {
		return new BasicAction(actionRegistry, L10N.t("action.pack_tools.tool"),
				_ -> new ToolPackMakerTool(actionRegistry.getMCreator())) {
			@Override public boolean isEnabled() {
				return isSupported(actionRegistry.getMCreator().getGeneratorConfiguration());
			}
		}.setIcon(UIRES.get("16px.toolpack"));
	}

}
