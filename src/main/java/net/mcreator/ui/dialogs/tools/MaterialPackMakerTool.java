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
import net.mcreator.element.parts.TextureHolder;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.BasicAction;
import net.mcreator.ui.component.JColor;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
		JTabbedPane tabPanel = new JTabbedPane();

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
		JPanel texturesPanel = new JPanel(new GridLayout(8, 4, 5, 2));

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

		texturesPanel.add(L10N.label("dialog.tools.ore_pack_textures.ore"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(oreTexture));

		texturesPanel.add(L10N.label("dialog.tools.ore_pack_textures.block"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(blockTexture));

		texturesPanel.add(L10N.label("dialog.tools.ore_pack_textures.item"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(itemTexture));

		texturesPanel.add(new JLabel());
		texturesPanel.add(new JLabel());

		texturesPanel.add(L10N.label("dialog.tools.tool_pack_textures.pickaxe"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(pickaxeTexture));

		texturesPanel.add(L10N.label("dialog.tools.tool_pack_textures.axe"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(axeTexture));

		texturesPanel.add(L10N.label("dialog.tools.tool_pack_textures.sword"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(swordTexture));

		texturesPanel.add(L10N.label("dialog.tools.tool_pack_textures.shovel"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(shovelTexture));

		texturesPanel.add(L10N.label("dialog.tools.tool_pack_textures.hoe"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(hoeTexture));

		texturesPanel.add(new JLabel());
		texturesPanel.add(new JLabel());

		texturesPanel.add(L10N.label("dialog.tools.armor_pack_textures.helmet"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(helmetTexture));

		texturesPanel.add(L10N.label("dialog.tools.armor_pack_textures.chestplate"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(chestplateTexture));

		texturesPanel.add(L10N.label("dialog.tools.armor_pack_textures.leggings"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(leggingsTexture));

		texturesPanel.add(L10N.label("dialog.tools.armor_pack_textures.boots"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(bootsTexture));

		texturesPanel.add(L10N.label("dialog.tools.armor_pack_textures.armor"));
		texturesPanel.add(PanelUtils.totalCenterInPanel(armorTexture));

		texturesPanel.add(new JLabel());
		texturesPanel.add(new JLabel());

		tabPanel.add(L10N.t("dialog.tools.pack_makers.properties"), PanelUtils.totalCenterInPanel(props));
		tabPanel.add(L10N.t("dialog.tools.pack_makers.textures"), PanelUtils.totalCenterInPanel(texturesPanel));

		this.add("Center", PanelUtils.centerInPanel(tabPanel));

		this.setSize(600, 300);
		this.setLocationRelativeTo(mcreator);
		this.setVisible(true);
	}

	@Override protected void generatePack(MCreator mcreator) {
		addMaterialPackToWorkspace(this, mcreator, mcreator.getWorkspace(), name.getText(),
				(String) Objects.requireNonNull(type.getSelectedItem()), color.getColor(), (Double) power.getValue(),
				makeTextureMap());
	}

	public static void addMaterialPackToWorkspace(@Nullable AbstractPackMakerTool packMaker, MCreator mcreator,
			Workspace workspace, String name, String type, Color color, double factor,
			@Nonnull Map<String, TextureHolder> textureMap) {
		MItemBlock gem = OrePackMakerTool.addOrePackToWorkspace(packMaker, mcreator, workspace, name, type, color,
				factor, textureMap);
		ToolPackMakerTool.addToolPackToWorkspace(packMaker, mcreator, workspace, name, gem, color, factor, textureMap);
		ArmorPackMakerTool.addArmorPackToWorkspace(packMaker, mcreator, workspace, name, gem, color, factor,
				textureMap);
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
