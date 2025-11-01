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
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.ModElementNameValidator;
import net.mcreator.workspace.Workspace;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class MaterialPackMakerTool extends AbstractPackMakerTool {

	private final VTextField name = new VTextField(25);
	private final JComboBox<String> type = new JComboBox<>(new String[] { "Gem based", "Dust based", "Ingot based" });
	private final JColor color;
	private final JSpinner power = new JSpinner(new SpinnerNumberModel(1, 0.1, 10, 0.1));

	private MaterialPackMakerTool(MCreator mcreator) {
		super(mcreator, "material_pack", UIRES.get("16px.materialpack").getImage());

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

		this.add("Center", PanelUtils.centerInPanel(props));

		this.setSize(600, 300);
		this.setLocationRelativeTo(mcreator);
		this.setVisible(true);
	}

	@Override protected void generatePack(MCreator mcreator) {
		addMaterialPackToWorkspace(mcreator, mcreator.getWorkspace(), name.getText(),
				(String) Objects.requireNonNull(type.getSelectedItem()), color.getColor(), (Double) power.getValue());
	}

	public static void addMaterialPackToWorkspace(MCreator mcreator, Workspace workspace, String name, String type,
			Color color, double factor) {
		MItemBlock gem = OrePackMakerTool.addOrePackToWorkspace(mcreator, workspace, name, type, color, factor);
		ToolPackMakerTool.addToolPackToWorkspace(mcreator, workspace, name, gem, color, factor);
		ArmorPackMakerTool.addArmorPackToWorkspace(mcreator, workspace, name, gem, color, factor);
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

	public static BasicAction getAction(ActionRegistry actionRegistry) {
		return new BasicAction(actionRegistry, L10N.t("action.pack_tools.material"),
				e -> new MaterialPackMakerTool(actionRegistry.getMCreator())) {
			@Override public boolean isEnabled() {
				return isSupported(actionRegistry.getMCreator().getGeneratorConfiguration());
			}
		}.setIcon(UIRES.get("16px.materialpack"));
	}

}
