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

import net.mcreator.minecraft.TagType;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.validators.NamespaceValidator;
import net.mcreator.ui.validation.validators.ResourceLocationValidator;
import net.mcreator.workspace.elements.TagElement;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class NewTagDialog {

	public static TagElement showNewTagDialog(MCreator mcreator) {
		JPanel panel = new JPanel(new GridLayout(3, 2, 10, 2));

		JComboBox<TagType> type = new JComboBox<>(TagType.values());
		VComboBox<String> namespace = new VComboBox<>(new String[] { "minecraft", "mod", "forge", "neoforge", "c" });
		VComboBox<String> name = new VComboBox<>();

		type.setPreferredSize(new Dimension(250, 38));

		name.setValidator(new ResourceLocationValidator<>(L10N.t("dialog.tags.tag"), name, false));
		name.enableRealtimeValidation();
		name.addItem("tag");
		name.addItem("category/tag");
		name.addItem("tick");
		name.addItem("load");
		name.addItem("logs");
		name.addItem("beacon_base_blocks");
		name.setEditable(true);
		name.setOpaque(false);

		namespace.setValidator(new NamespaceValidator<>(namespace));
		namespace.enableRealtimeValidation();
		namespace.setEditable(true);

		name.addActionListener(e -> {
			if (Objects.equals(name.getSelectedItem(), "tick") || Objects.equals(name.getSelectedItem(), "load")) {
				namespace.setSelectedItem("minecraft");
				type.setSelectedItem(TagType.FUNCTIONS);
			}
		});

		panel.add(L10N.label("dialog.tags.tag_type"));
		panel.add(type);
		panel.add(L10N.label("dialog.tags.tag_namespace"));
		panel.add(namespace);
		panel.add(L10N.label("dialog.tags.tag_name"));
		panel.add(name);

		ValidationGroup group = new ValidationGroup();
		group.addValidationElement(name);
		group.addValidationElement(namespace);

		int option = JOptionPane.showConfirmDialog(mcreator, panel, L10N.t("dialog.tags.new_title"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null);
		if (option == JOptionPane.OK_OPTION) {
			if (group.validateIsErrorFree()) {
				TagElement retval = new TagElement((TagType) type.getSelectedItem(),
						namespace.getEditor().getItem().toString() + ":" + name.getEditor().getItem().toString());
				if (mcreator.getWorkspace().getTagElements().containsKey(retval)) {
					JOptionPane.showMessageDialog(mcreator, L10N.t("dialog.tags.tag_exists"),
							L10N.t("dialog.tags.new_title"), JOptionPane.ERROR_MESSAGE);
				} else {
					return retval;
				}
			} else {
				JOptionPane.showMessageDialog(mcreator,
						"<html>" + String.join("<br>- ", group.getValidationProblemMessages()),
						L10N.t("dialog.tags.new_title"), JOptionPane.ERROR_MESSAGE);
			}
		}

		return null;
	}

}
