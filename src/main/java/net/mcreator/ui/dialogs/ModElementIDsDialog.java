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
import net.mcreator.ui.init.L10N;
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
			JOptionPane.showMessageDialog(mcreator, L10N.t("dialog.modelement_id.info_message"),
					L10N.t("dialog.modelement_id.info_message_title"), JOptionPane.ERROR_MESSAGE);

			return null;
		}

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

		VTextField registryName = new VTextField();
		JComponent reghol = PanelUtils.join(L10N.label("dialog.modelement_id.registry_name"), registryName);
		reghol.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 2),
				L10N.t("dialog.modelement_id.registry_name_info"), 0, 0, reghol.getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		panel.add(PanelUtils.centerInPanel(L10N.label("dialog.modelement_id.use_caution_warn")));

		registryName.setPreferredSize(new Dimension(250, 32));

		panel.add(new JEmptyBox(10, 10));

		registryName.setValidator(
				new RegistryNameValidator(registryName, L10N.t("dialog.modelement_id.registry_name_validator")));
		registryName.enableRealtimeValidation();

		panel.add(reghol);

		JPanel ids = new JPanel();
		ids.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 2),
				L10N.t("dialog.modelement_id.numerical_id"), 0, 0, reghol.getFont().deriveFont(12.0f), Color.white));

		ids.setLayout(new BorderLayout());

		Map<Integer, JSpinner> idspinners = new HashMap<>();

		if (!modElement.getIDMap().isEmpty()) {
			int offset = mcreator.getGenerator()
					.getStartIDFor(modElement.getType().getBaseType().name().toLowerCase(Locale.ENGLISH));
			if (offset != -1) {
				ids.add("North", PanelUtils.maxMargin(
						L10N.label("dialog.modelement_id.id_offset", modElement.getType().getReadableName(), offset), 5,
						false, true, false, false));
			}

			JPanel idsmap = new JPanel(new GridLayout(modElement.getIDMap().size(), 2));
			for (Map.Entry<Integer, Integer> mapping : modElement.getIDMap().entrySet()) {
				idsmap.add(L10N.label("dialog.modelement_id.id_mapping", mapping.getKey()));
				JSpinner id = new JSpinner(
						new SpinnerNumberModel((int) mapping.getValue(), Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
				idspinners.put(mapping.getKey(), id);
				idsmap.add(id);
			}

			ids.add("Center", PanelUtils.centerInPanel(idsmap));
		} else {
			ids.add(L10N.label("dialog.modelement_id.error_no_numerical_id"));
		}

		panel.add(new JEmptyBox(20, 20));

		panel.add(ids);

		panel.add(new JEmptyBox(20, 20));

		registryName.setText(modElement.getRegistryName());

		int option = JOptionPane.showConfirmDialog(mcreator, panel,
				L10N.t("dialog.modelement_id.id_and_registry_names", modElement.getName()),
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
					JOptionPane.showMessageDialog(mcreator, L10N.t("dialog.modelement_id.invalid_registry_name"),
							L10N.t("dialog.modelement_id.invalid_registry_name_title"), JOptionPane.ERROR_MESSAGE);
					return null;
				}

				option = JOptionPane.showConfirmDialog(mcreator, L10N.t("dialog.modelement_id.registry_name_changed"),
						L10N.t("dialog.modelement_id.registry_name_changed_title"), JOptionPane.YES_NO_OPTION,
						JOptionPane.PLAIN_MESSAGE);

				if (option == JOptionPane.YES_OPTION) {
					regenerateCode = true;
				}
			}

			mcreator.getGenerator().removeElementFilesAndLangKeys(
					modElement); // we remove current files as new ones will be made for the new registry name
			modElement.setRegistryName(registryName.getText()); // set new name
			GeneratableElement generatableElement = modElement.getGeneratableElement();
			mcreator.getGenerator().generateElement(
					generatableElement); // regenerate mod element code to use new registry id and to regenerate resource files, ...

			if (regenerateCode) {
				mcreator.actionRegistry.regenerateCode.doAction();
			}

			return modElement;
		}

		return null;
	}

}
