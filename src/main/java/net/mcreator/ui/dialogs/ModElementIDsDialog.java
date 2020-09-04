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

package net.mcreator.ui.dialogs;

import net.mcreator.element.GeneratableElement;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.RegistryNameValidator;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ModElementIDsDialog {

	public static ModElement openModElementIDDialog(MCreator mcreator, ModElement modElement) {
		if (modElement.isCodeLocked()) {
			JOptionPane.showMessageDialog(mcreator, "<html>This mod element has locked code!<br>"
							+ "When code is locked, MCreator can't alter its source code.", "Mod element code is locked",
					JOptionPane.ERROR_MESSAGE);

			return null;
		}

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

		VTextField registryName = new VTextField();
		JComponent reghol = PanelUtils.join(new JLabel("Registry name: "), registryName);
		reghol.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 2),
				"Registry name (not all mod elements use it)", 0, 0, reghol.getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		panel.add(PanelUtils.centerInPanel(new JLabel(
				"<html><b><font color=#F08080>Use this tool with caution!<br><br></font>Changing IDs can break your existing world saves and cause<br>"
						+ "conflicts with other mod element from your mod!")));

		registryName.setPreferredSize(new Dimension(250, 32));

		panel.add(new JEmptyBox(10, 10));

		registryName.setValidator(new RegistryNameValidator(registryName, "Registry name"));
		registryName.enableRealtimeValidation();

		panel.add(reghol);

		JPanel ids = new JPanel();
		ids.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 2),
				"Mod element numerical IDs (not all mod elements use them)", 0, 0, reghol.getFont().deriveFont(12.0f),
				Color.white));

		ids.setLayout(new BorderLayout());

		Map<Integer, JSpinner> idspinners = new HashMap<>();

		if (!modElement.getIDMap().isEmpty()) {
			int offset = mcreator.getWorkspace().getGenerator()
					.getStartIDFor(modElement.getType().getBaseType().name().toLowerCase(Locale.ENGLISH));
			if (offset != -1) {
				ids.add("North", PanelUtils.maxMargin(new JLabel(
								"<html><small>" + modElement.getType().getReadableName()
										+ " mod element type has IDs offset for " + offset
										+ ".<br>Your ID + this offset is the final ID used in Minecraft."), 5, false, true,
						false, false));
			}

			JPanel idsmap = new JPanel(new GridLayout(modElement.getIDMap().size(), 2));
			for (Map.Entry<Integer, Integer> mapping : modElement.getIDMap().entrySet()) {
				idsmap.add(new JLabel("ID mapping #" + mapping.getKey() + ": "));
				JSpinner id = new JSpinner(
						new SpinnerNumberModel((int) mapping.getValue(), Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
				idspinners.put(mapping.getKey(), id);
				idsmap.add(id);
			}

			ids.add("Center", PanelUtils.centerInPanel(idsmap));
		} else {
			ids.add(new JLabel("This mod element does not have any numerical IDs registered"));
		}

		panel.add(new JEmptyBox(20, 20));

		panel.add(ids);

		panel.add(new JEmptyBox(20, 20));

		registryName.setText(modElement.getRegistryName());

		int option = JOptionPane.showConfirmDialog(mcreator, panel, modElement.getName() + " - IDs and registry names",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (option == JOptionPane.OK_OPTION) {
			if (!modElement.getIDMap().isEmpty()) { // if this mod element has id map, write changes
				for (Map.Entry<Integer, JSpinner> spinnerEntry : idspinners.entrySet()) {
					modElement.setIDAt(spinnerEntry.getKey(), (Integer) spinnerEntry.getValue().getValue());
				}
			}

			boolean regenerateCode = false;

			// check if registry name has been changed
			if (!registryName.getText().equals(modElement.getRegistryName())) {
				if (registryName.getValidationStatus().getValidationResultType()
						== Validator.ValidationResultType.ERROR) { // if invalid registry name
					JOptionPane.showMessageDialog(mcreator, "<html>The registry name you entered is not valid!<br>"
							+ "The changes have not been saved.", "Invalid registry name", JOptionPane.ERROR_MESSAGE);
					return null;
				}

				option = JOptionPane.showConfirmDialog(mcreator,
						"<html><b>You have changed registry name of your mod element!</b><br><br>"
								+ "If this mod element is referenced in other mod element types, especially achievements or recipes,<br>"
								+ "you may need to rebuild code to update references to the new name.<br>"
								+ "<br>Do you want to start code rebuild now?", "Changed registry name",
						JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);

				if (option == JOptionPane.YES_OPTION) {
					regenerateCode = true;
				}
			}

			mcreator.getWorkspace().getGenerator().removeElementFilesAndLangKeys(
					modElement); // we remove current files as new ones will be made for the new registry name
			modElement.setRegistryName(registryName.getText()); // set new name
			GeneratableElement generatableElement = modElement.getGeneratableElement();
			mcreator.getWorkspace().getGenerator().generateElement(
					generatableElement); // regenerate mod element code to use new registry id and to regenerate resource files, ...

			if (regenerateCode) {
				mcreator.actionRegistry.regenerateCode.doAction();
			}

			return modElement;
		}

		return null;
	}

}
