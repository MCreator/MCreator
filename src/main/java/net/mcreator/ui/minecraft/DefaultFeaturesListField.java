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

import javax.swing.*;
import java.util.Collections;
import java.util.List;

public class DefaultFeaturesListField extends JItemListField<String> {

	private MCreator frame;

	public DefaultFeaturesListField(MCreator frame) {
		this.frame = frame;
	}

	@Override protected List<String> getElementsToAdd() {
		JList<String> vlist = new JList<>(ElementUtil.loadDefaultFeatures());
		int option = JOptionPane.showOptionDialog(frame, PanelUtils
						.northAndCenterElement(new JLabel("Select biome default features you would like to add to the list:"),
								new JScrollPane(vlist)), "Select biome default features", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, null, null);

		if (option == JOptionPane.OK_OPTION && vlist.getSelectedValue() != null) {
			return vlist.getSelectedValuesList();
		}
		return Collections.emptyList();
	}
}
