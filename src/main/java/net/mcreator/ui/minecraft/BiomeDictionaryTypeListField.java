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

import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JItemListField;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

public class BiomeDictionaryTypeListField extends JItemListField<String> {

	private final MCreator frame;

	public BiomeDictionaryTypeListField(MCreator frame) {
		this.frame = frame;
	}

	@Override protected List<String> getElementsToAdd() {
		JList<String> vlist = new JList<>(ElementUtil.loadBiomeDictionaryTypes());
		int option = JOptionPane.showOptionDialog(frame, PanelUtils
						.northAndCenterElement(L10N.label("dialog.list_field.biome_dictionary_message"),
								new JScrollPane(vlist)), L10N.t("dialog.list_field.biome_dictionary_title"), JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, null, null);

		if (option == JOptionPane.OK_OPTION && vlist.getSelectedValue() != null) {
			return vlist.getSelectedValuesList();
		}
		return Collections.emptyList();
	}
}
