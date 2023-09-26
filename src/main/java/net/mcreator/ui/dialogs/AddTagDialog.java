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

import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.types.Tag;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.validators.ResourceLocationValidator;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class AddTagDialog {

	public static String openAddTagDialog(Window parent, MCreator mcreator, @Nonnull String tagType,
			String... suggestions) {
		JPanel wrap = new JPanel(new GridLayout());
		VComboBox<String> tagName = new VComboBox<>();

		switch (tagType) {
		case "Items" -> wrap.setBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, Dependency.getColor("itemstack")));
		case "Blocks" -> wrap.setBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, Dependency.getColor("blockstate")));
		case "Entities" -> wrap.setBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, Dependency.getColor("entity")));
		case "Biomes" -> wrap.setBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, Dependency.getColor("world")));
		}

		tagName.setValidator(new ResourceLocationValidator<>(L10N.t("modelement.tag"), tagName, true));

		for (ModElement modElement : mcreator.getWorkspace().getModElements()) {
			if (modElement.getType() == ModElementType.TAG) {
				GeneratableElement ge = modElement.getGeneratableElement();
				if (ge instanceof Tag tag) {
					if (tag.type.equals(tagType))
						tagName.addItem(tag.getResourceLocation());
				}
			}
		}

		for (String suggestion : suggestions)
			tagName.addItem(suggestion);

		tagName.setEditable(true);
		tagName.getEditor().setItem("");
		tagName.enableRealtimeValidation();
		ComponentUtils.deriveFont(tagName, 16);

		wrap.add(tagName);

		int result = JOptionPane.showConfirmDialog(parent, PanelUtils.northAndCenterElement(
						L10N.label("dialog.item_selector.enter_tag_name." + tagType.toLowerCase(Locale.ENGLISH)), wrap, 5, 5),
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
