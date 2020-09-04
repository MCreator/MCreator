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
import net.mcreator.ui.init.UIRES;

import javax.swing.*;
import java.awt.*;

public class AddonWorkspacePanel extends AbstractWorkspacePanel {

	public AddonWorkspacePanel(Window parent) {
		super(parent);

		add(new JEmptyBox(20, 20));

		add(PanelUtils.westAndEastElement(new JLabel("<html>Add-on display name:<br><small>This name is shown in-game"),
				PanelUtils.join(workspaceDialogPanel.modName)));

		add(new JEmptyBox(5, 5));

		add(PanelUtils.westAndEastElement(new JLabel("<html>Addon ID:<br><small>Used for add-on identification"),
				PanelUtils.join(workspaceDialogPanel.modID)));

		add(new JEmptyBox(5, 5));

		add(PanelUtils.westAndEastElement(
				new JLabel("<html>Minecraft version (generator):<br><small>Target Bedrock Edition version"),
				PanelUtils.join(workspaceDialogPanel.generatorSelector)));

		add(new JEmptyBox(30, 30));

		add(PanelUtils.westAndEastElement(new JLabel("Workspace folder:      "),
				PanelUtils.centerAndEastElement(workspaceFolder, selectWorkspaceFolder, 5, 5)));

		add(new JEmptyBox(30, 204));

		add(PanelUtils.join(FlowLayout.LEFT, new JLabel(UIRES.get("18px.warning")), new JEmptyBox(0, 0), new JLabel(
				"<html><font color='#aaaaaa' size=2>Add-Ons are currently experimental features both in Minecraft and MCreator so the feature<br>"
						+ "coverage is limited.")));

		add(PanelUtils.join(FlowLayout.LEFT, new JLabel(UIRES.get("18px.info")), new JEmptyBox(0, 0), new JLabel(
				"<html><font color='#aaaaaa' size=2>Add-Ons are mods for Minecraft Bedrock Edition and do not require any additional software<br>"
						+ "or modloader installed.")));

		validationGroup.addValidationElement(workspaceDialogPanel.modName);
		validationGroup.addValidationElement(workspaceDialogPanel.modID);
		validationGroup.addValidationElement(workspaceFolder);

		workspaceDialogPanel.setFlavorFilter(GeneratorFlavor.ADDON);

		workspaceDialogPanel.generator.removeAllItems();
		Generator.GENERATOR_CACHE.values().stream().filter(gc -> gc.getGeneratorFlavor() == GeneratorFlavor.ADDON)
				.forEach(workspaceDialogPanel.generator::addItem);

		GeneratorConfiguration generatorConfiguration = GeneratorConfiguration
				.getRecommendedGeneratorForFlavor(Generator.GENERATOR_CACHE.values(), GeneratorFlavor.ADDON);
		workspaceDialogPanel.generator.setSelectedItem(generatorConfiguration);
	}
}
