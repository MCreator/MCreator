/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

package net.mcreator.ui.minecraft.states.entity;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.entries.JSimpleEntriesList;
import net.mcreator.ui.dialogs.AddEntityPropertyDialog;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.minecraft.states.PropertyDataWithValue;

import javax.annotation.Nullable;
import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

public class JEntityDataList extends JSimpleEntriesList<JEntityDataEntry, PropertyDataWithValue<?>> {

	public JEntityDataList(MCreator mcreator, IHelpContext gui) {
		super(mcreator, gui);

		add.setText(L10N.t("elementgui.living_entity.entity_data_entries.add_entry"));

		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2),
				BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
						BorderFactory.createEmptyBorder(2, 2, 2, 2))));
	}

	@Override @Nullable
	protected JEntityDataEntry newEntry(JPanel parent, List<JEntityDataEntry> entryList, boolean userAction) {
		if (userAction) {
			PropertyDataWithValue<?> newEntry = AddEntityPropertyDialog.showDialog(mcreator,
					entryList.stream().map(JEntityDataEntry::getEntry).map(PropertyDataWithValue::property)
							.collect(Collectors.toList()));
			if (newEntry != null) {
				JEntityDataEntry dataEntry = new JEntityDataEntry(mcreator, gui, parent, entryList);
				dataEntry.setEntry(newEntry);
				return dataEntry;
			}
			return null;
		} else {
			return new JEntityDataEntry(mcreator, gui, parent, entryList);
		}
	}

}
