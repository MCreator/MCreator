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
import net.mcreator.element.parts.TextureHolder;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.BasicAction;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.minecraft.TextureComboBox;
import net.mcreator.ui.minecraft.TextureSelectionButton;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.ModElementNameValidator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.Workspace;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class MaterialPackMakerTool extends AbstractPackMakerTool {

	private final VTextField name = new VTextField(25);
	private final JComboBox<String> type = new JComboBox<>(new String[] { "Gem based", "Dust based", "Ingot based" });
	private final JColor color;
	private final JSpinner power = new JSpinner(new SpinnerNumberModel(1, 0.1, 10, 0.1));

	private final TextureSelectionButton oreTexture;
	private final TextureSelectionButton blockTexture;
	private final TextureSelectionButton itemTexture;

	private final TextureSelectionButton pickaxeTexture;
	private final TextureSelectionButton axeTexture;
	private final TextureSelectionButton swordTexture;
	private final TextureSelectionButton shovelTexture;
	private final TextureSelectionButton hoeTexture;

	private final TextureSelectionButton helmetTexture;
	private final TextureSelectionButton chestplateTexture;
	private final TextureSelectionButton leggingsTexture;
	private final TextureSelectionButton bootsTexture;
	private final TextureComboBox armorTexture;

	private MaterialPackMakerTool(MCreator mcreator) {
		super(mcreator, "material_pack", UIRES.get("16px.materialpack").getImage());

		// Main properties page
		JPanel props = new JPanel(new GridLayout(4, 2, 5, 2));

		color = new JColor(mcreator, false, false);

		color.setColor(Theme.current().getInterfaceAccentColor());
		name.enableRealtimeValidation();

		props.add(L10N.label("dialog.tools.material_pack_name"));
		props.add(name);

		props.add(L10N.label("dialog.tools.material_pack_type"));
		props.add(type);

		props.add(L10N.label("dialog.tools.material_pack_color_accent"));
		props.add(color);

		props.add(L10N.label("dialog.tools.material_pack_power_factor"));
		props.add(power);

		name.setValidator(new ModElementNameValidator(mcreator.getWorkspace(), name,
				L10N.t("dialog.tools.material_pack_name_validator")));

		validableElements.addValidationElement(name);

		// Textures page
		JPanel oresSection = new JPanel(new GridLayout(1, 3, 50, 5));
		JPanel toolsSection = new JPanel(new GridLayout(2, 3, 50, 5));
		JPanel armorItemsSection = new JPanel(new GridLayout(2, 2, 75, 5));

		oreTexture = new TextureSelectionButton(mcreator, TextureType.BLOCK, 64);
		blockTexture = new TextureSelectionButton(mcreator, TextureType.BLOCK, 64);
		itemTexture = new TextureSelectionButton(mcreator, TextureType.ITEM, 64);

		pickaxeTexture = new TextureSelectionButton(mcreator, TextureType.ITEM, 64);
		axeTexture = new TextureSelectionButton(mcreator, TextureType.ITEM, 64);
		swordTexture = new TextureSelectionButton(mcreator, TextureType.ITEM, 64);
		shovelTexture = new TextureSelectionButton(mcreator, TextureType.ITEM, 64);
		hoeTexture = new TextureSelectionButton(mcreator, TextureType.ITEM, 64);

		helmetTexture = new TextureSelectionButton(mcreator, TextureType.ITEM, 64);
		chestplateTexture = new TextureSelectionButton(mcreator, TextureType.ITEM, 64);
		leggingsTexture = new TextureSelectionButton(mcreator, TextureType.ITEM, 64);
		bootsTexture = new TextureSelectionButton(mcreator, TextureType.ITEM, 64);
		armorTexture = new TextureComboBox(mcreator, TextureType.ARMOR, true);
		armorTexture.setAddPNGExtension(false);

		oresSection.add(PanelUtils.gridElements(1, 2, L10N.label("dialog.tools.ore_pack_textures.ore"),
				PanelUtils.totalCenterInPanel(oreTexture)));
		oresSection.add(PanelUtils.gridElements(1, 2, L10N.label("dialog.tools.ore_pack_textures.block"),
				PanelUtils.totalCenterInPanel(blockTexture)));
		oresSection.add(PanelUtils.gridElements(1, 2, L10N.label("dialog.tools.ore_pack_textures.item"),
				PanelUtils.totalCenterInPanel(itemTexture)));

		toolsSection.add(PanelUtils.gridElements(1, 2, L10N.label("dialog.tools.tool_pack_textures.pickaxe"),
				PanelUtils.totalCenterInPanel(pickaxeTexture)));
		toolsSection.add(PanelUtils.gridElements(1, 2, L10N.label("dialog.tools.tool_pack_textures.axe"),
				PanelUtils.totalCenterInPanel(axeTexture)));
		toolsSection.add(PanelUtils.gridElements(1, 2, L10N.label("dialog.tools.tool_pack_textures.sword"),
				PanelUtils.totalCenterInPanel(swordTexture)));
		toolsSection.add(PanelUtils.gridElements(1, 2, L10N.label("dialog.tools.tool_pack_textures.shovel"),
				PanelUtils.totalCenterInPanel(shovelTexture)));
		toolsSection.add(PanelUtils.gridElements(1, 2, L10N.label("dialog.tools.tool_pack_textures.hoe"),
				PanelUtils.totalCenterInPanel(hoeTexture)));
		toolsSection.add(new JLabel());

		armorItemsSection.add(PanelUtils.gridElements(1, 2, L10N.label("dialog.tools.armor_pack_textures.helmet"),
				PanelUtils.totalCenterInPanel(helmetTexture)));
		armorItemsSection.add(PanelUtils.gridElements(1, 2, L10N.label("dialog.tools.armor_pack_textures.chestplate"),
				PanelUtils.totalCenterInPanel(chestplateTexture)));
		armorItemsSection.add(PanelUtils.gridElements(1, 2, L10N.label("dialog.tools.armor_pack_textures.leggings"),
				PanelUtils.totalCenterInPanel(leggingsTexture)));
		armorItemsSection.add(PanelUtils.gridElements(1, 2, L10N.label("dialog.tools.armor_pack_textures.boots"),
				PanelUtils.totalCenterInPanel(bootsTexture)));

		JPanel armorSection = PanelUtils.column(5, PanelUtils.totalCenterInPanel(armorItemsSection),
				PanelUtils.row(25, L10N.label("dialog.tools.armor_pack_textures.armor"), armorTexture));
		oresSection = PanelUtils.centerInPanelPadding(oresSection, 10, 10);
		toolsSection = PanelUtils.centerInPanelPadding(toolsSection, 10, 10);
		armorSection = PanelUtils.centerInPanelPadding(armorSection, 10, 10);

		ComponentUtils.makeSection(oresSection, L10N.t("dialog.tools.material_pack_textures.ores_section"));
		ComponentUtils.makeSection(toolsSection, L10N.t("dialog.tools.material_pack_textures.tools_section"));
		ComponentUtils.makeSection(armorSection, L10N.t("dialog.tools.material_pack_textures.armor_section"));

		addPage(L10N.t("dialog.tools.pack_makers.properties"), props);
		addPage(L10N.t("dialog.tools.pack_makers.textures"), PanelUtils.column(15,
				PanelUtils.centerInPanel(L10N.label("dialog.tools.pack_makers.empty_textures_message")), oresSection,
				toolsSection, armorSection));

		this.add("Center", tabs);

		this.setSize(700, 500);
		this.setLocationRelativeTo(mcreator);
		this.setVisible(true);
	}

	@Override protected void generatePack(MCreator mcreator) {
		addMaterialPackToWorkspace(toGenerate, mcreator, mcreator.getWorkspace(), name.getText(),
				(String) Objects.requireNonNull(type.getSelectedItem()), color.getColor(), (Double) power.getValue(),
				makeTextureMap());
	}

	public static String[] getPackElementNames(String name, String type) {
		return Stream.of(OrePackMakerTool.getPackElementNames(name, type), ToolPackMakerTool.getPackElementNames(name),
				ArmorPackMakerTool.getPackElementNames(name)).flatMap(Arrays::stream).toArray(String[]::new);
	}

	public static boolean addMaterialPackToWorkspace(@Nullable List<GeneratableElement> generationQueue, MCreator mcreator,
			Workspace workspace, String name, String type, Color color, double factor,
			@Nonnull Map<String, TextureHolder> textureMap) {
		// intentionally attempt every sub-pack even if one fails (non-short-circuit &=), so the dialog path
		// still creates the remaining sub-packs when one is skipped due to a name conflict
		boolean success = OrePackMakerTool.addOrePackToWorkspace(generationQueue, mcreator, workspace, name, type,
				color, factor, textureMap);

		MItemBlock gem = new MItemBlock(workspace, "CUSTOM:" + OrePackMakerTool.getOreItemName(name, type));
		success &= ToolPackMakerTool.addToolPackToWorkspace(generationQueue, mcreator, workspace, name, gem, color,
				factor, textureMap);
		success &= ArmorPackMakerTool.addArmorPackToWorkspace(generationQueue, mcreator, workspace, name, gem, color,
				factor, textureMap);

		return success;
	}

	public static boolean isSupported(GeneratorConfiguration gc) {
		return gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.RECIPE)
				!= GeneratorStats.CoverageStatus.NONE
				&& gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.ITEM)
				!= GeneratorStats.CoverageStatus.NONE
				&& gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.BLOCK)
				!= GeneratorStats.CoverageStatus.NONE
				&& gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.TOOL)
				!= GeneratorStats.CoverageStatus.NONE
				&& gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.ARMOR)
				!= GeneratorStats.CoverageStatus.NONE;
	}

	private Map<String, TextureHolder> makeTextureMap() {
		HashMap<String, TextureHolder> map = new HashMap<>();

		addToTextureMap(map, oreTexture, "ore");
		addToTextureMap(map, blockTexture, "block");
		addToTextureMap(map, itemTexture, "item");

		addToTextureMap(map, pickaxeTexture, "pickaxe");
		addToTextureMap(map, axeTexture, "axe");
		addToTextureMap(map, swordTexture, "sword");
		addToTextureMap(map, shovelTexture, "shovel");
		addToTextureMap(map, hoeTexture, "hoe");

		addToTextureMap(map, helmetTexture, "helmet");
		addToTextureMap(map, chestplateTexture, "chestplate");
		addToTextureMap(map, leggingsTexture, "leggings");
		addToTextureMap(map, bootsTexture, "boots");
		addToTextureMap(map, armorTexture, "armor");

		return map;
	}

	public static BasicAction getAction(ActionRegistry actionRegistry) {
		return new BasicAction(actionRegistry, L10N.t("action.pack_tools.material"),
				_ -> new MaterialPackMakerTool(actionRegistry.getMCreator())) {
			@Override public boolean isEnabled() {
				return isSupported(actionRegistry.getMCreator().getGeneratorConfiguration());
			}
		}.setIcon(UIRES.get("16px.materialpack"));
	}

}
