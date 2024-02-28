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

import net.mcreator.io.Transliteration;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.entries.JSimpleEntriesList;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.MCreatorDialog;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.minecraft.states.PropertyData;
import net.mcreator.ui.minecraft.states.PropertyDataWithValue;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.JavaMemberNameValidator;
import net.mcreator.ui.validation.validators.UniqueNameValidator;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class JEntityDataList extends JSimpleEntriesList<JEntityDataEntry, PropertyDataWithValue<?>> {

	public JEntityDataList(MCreator mcreator, IHelpContext gui) {
		super(mcreator, gui);

		add.setText(L10N.t("elementgui.living_entity.entity_data_entries.add_entry"));

		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2),
				BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
						BorderFactory.createEmptyBorder(2, 2, 2, 2))));
	}

	@Override protected JEntityDataEntry newEntry(JPanel parent, List<JEntityDataEntry> entryList, boolean userAction) {
		if (userAction) {
			AtomicReference<JEntityDataEntry> entry = new AtomicReference<>();

			MCreatorDialog dialog = new MCreatorDialog(mcreator,
					L10N.t("elementgui.living_entity.entity_data_entries.add_entry.title"), true);

			VTextField name = new VTextField(24);
			name.setPreferredSize(new Dimension(0, 28));
			UniqueNameValidator validator = new UniqueNameValidator(L10N.t("workspace.variables.variable_name"),
					name::getText, () -> entryList.stream().map(e -> e.getEntry().property().getName()),
					new JavaMemberNameValidator(name, false));
			validator.setIsPresentOnList(false);
			name.setValidator(validator);
			name.enableRealtimeValidation();

			JComboBox<String> type = new JComboBox<>(new String[] { "Integer", "Logic", "String" });

			JButton ok = new JButton(UIManager.getString("OptionPane.okButtonText"));
			JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
			dialog.getRootPane().setDefaultButton(ok);

			ok.addActionListener(e -> {
				if (name.getValidationStatus().getValidationResultType() != Validator.ValidationResultType.ERROR) {
					dialog.setVisible(false);
					String property = Transliteration.transliterateString(name.getText());
					JEntityDataEntry dataEntry = new JEntityDataEntry(mcreator, gui, parent, entryList);
					if ("Integer".equals(type.getSelectedItem())) {
						dataEntry.setEntry(new PropertyDataWithValue<>(new PropertyData.IntegerType(property), null));
					} else if ("Logic".equals(type.getSelectedItem())) {
						dataEntry.setEntry(new PropertyDataWithValue<>(new PropertyData.LogicType(property), null));
					} else if ("String".equals(type.getSelectedItem())) {
						dataEntry.setEntry(new PropertyDataWithValue<>(new PropertyData.StringType(property), null));
					}
					entry.set(dataEntry);
				}
			});
			cancel.addActionListener(e -> dialog.setVisible(false));

			JComponent main = PanelUtils.gridElements(2, 2, 2, 2,
					L10N.label("elementgui.living_entity.entity_data_entries.add_entry.name"), name,
					L10N.label("elementgui.living_entity.entity_data_entries.add_entry.type"), type);
			main.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

			dialog.getContentPane().add("Center", main);
			dialog.getContentPane().add("South", PanelUtils.join(ok, cancel));
			dialog.pack();
			dialog.setLocationRelativeTo(mcreator);
			dialog.setVisible(true);

			return entry.get();
		} else {
			return new JEntityDataEntry(mcreator, gui, parent, entryList);
		}
	}

}
