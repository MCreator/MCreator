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

package net.mcreator.ui.minecraft;

import net.mcreator.element.ModElementType;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JItemListField;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ModElementListField extends JItemListField<String> {

	private final MCreator frame;
	private final ModElementType type;

	public ModElementListField(MCreator frame, ModElementType type) {
		this.frame = frame;
		this.type = type;
	}

	@Override protected List<String> getElementsToAdd() {
		JList<String> vlist = new JList<>(
				frame.getWorkspace().getModElements().stream().filter(e -> e.getType() == this.type)
						.map(ModElement::getName).toArray(String[]::new));

		int option = JOptionPane.showOptionDialog(frame, PanelUtils.northAndCenterElement(
				L10N.label("dialog.list_field.mod_element_message", type.getReadableName().toLowerCase(Locale.ENGLISH)),
				new JScrollPane(vlist)), L10N.t("dialog.list_field.mod_element_title"), JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, null, null);

		if (option == JOptionPane.OK_OPTION && vlist.getSelectedValue() != null) {
			return vlist.getSelectedValuesList();
		}
		return Collections.emptyList();
	}
}
