/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.validators.ResourceLocationValidator;

import javax.swing.*;
import java.awt.*;

public class AddTagDialog {

	public static String openAddTagDialog(Window parent, String... suggestions) {
		VComboBox<String> tagName = new VComboBox<>();

		tagName.setValidator(new ResourceLocationValidator<>(L10N.t("modelement.tag"), tagName, true));

		tagName.addItem("");

		for (String suggestion : suggestions)
			tagName.addItem(suggestion);

		tagName.setEditable(true);
		tagName.setOpaque(false);
		tagName.enableRealtimeValidation();
		ComponentUtils.deriveFont(tagName, 16);

		int result = JOptionPane.showConfirmDialog(parent,
				PanelUtils.northAndCenterElement(L10N.label("dialog.item_selector.enter_tag_name"), tagName),
				L10N.t("dialog.item_selector.use_tag"), JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			if (tagName.getValidationStatus().getValidationResultType() != Validator.ValidationResultType.ERROR) {
				return tagName.getSelectedItem();
			} else {
				JOptionPane.showMessageDialog(parent, tagName.getValidationStatus().getMessage(),
						L10N.t("dialog.item_selector.error_invalid_tag_name_title"), JOptionPane.ERROR_MESSAGE);
			}
		}

		return null;
	}

}
