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

package net.mcreator.ui.dialogs.workspace;

import net.mcreator.generator.Generator;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;

import javax.swing.*;
import java.awt.*;

public class SpigotWorkspacePanel extends AbstractWorkspacePanel {

	public SpigotWorkspacePanel(Window parent) {
		super(parent);

		add(new JEmptyBox(20, 20));

		add(PanelUtils.westAndEastElement(new JLabel(L10N.t("dialog.new_workspace.spigot.display_name")),
				PanelUtils.join(workspaceDialogPanel.modName)));

		add(new JEmptyBox(5, 5));

		add(PanelUtils.westAndEastElement(new JLabel(L10N.t("dialog.new_workspace.spigot.plugin_name")),
				PanelUtils.join(workspaceDialogPanel.modID)));

		add(new JEmptyBox(5, 5));

		add(PanelUtils.westAndEastElement(new JLabel(L10N.t("dialog.new_workspace.spigot.generator")),
				PanelUtils.join(workspaceDialogPanel.generatorSelector)));

		add(new JEmptyBox(30, 30));

		add(PanelUtils.westAndEastElement(new JLabel(L10N.t("dialog.new_workspace.spigot.package")),
				PanelUtils.join(workspaceDialogPanel.packageName)));

		add(new JEmptyBox(30, 30));

		add(PanelUtils.westAndEastElement(new JLabel(L10N.t("dialog.new_workspace.spigot.folder")),
				PanelUtils.centerAndEastElement(workspaceFolder, selectWorkspaceFolder, 5, 5)));

		add(new JEmptyBox(30, 170));

		add(PanelUtils.join(FlowLayout.LEFT, new JLabel(UIRES.get("18px.info")), new JEmptyBox(0, 0),
				new JLabel(L10N.t("dialog.new_workspace.spigot.notice"))));

		validationGroup.addValidationElement(workspaceDialogPanel.modName);
		validationGroup.addValidationElement(workspaceDialogPanel.modID);
		validationGroup.addValidationElement(workspaceDialogPanel.packageName);
		validationGroup.addValidationElement(workspaceFolder);

		workspaceDialogPanel.setFlavorFilter(GeneratorFlavor.SPIGOT);

		workspaceDialogPanel.generator.removeAllItems();
		Generator.GENERATOR_CACHE.values().stream().filter(gc -> gc.getGeneratorFlavor() == GeneratorFlavor.SPIGOT)
				.forEach(workspaceDialogPanel.generator::addItem);

		GeneratorConfiguration generatorConfiguration = GeneratorConfiguration
				.getRecommendedGeneratorForFlavor(Generator.GENERATOR_CACHE.values(), GeneratorFlavor.SPIGOT);
		workspaceDialogPanel.generator.setSelectedItem(generatorConfiguration);
	}
}
