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

package net.mcreator.ui.minecraft.entitydata;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.MCreatorDialog;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.minecraft.JEntriesList;
import net.mcreator.ui.minecraft.states.DefaultPropertyValue;
import net.mcreator.ui.minecraft.states.PropertyData;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.JavaMemberNameValidator;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JEntityDataList extends JEntriesList {

	private final List<JEntityDataEntry<?>> entryList = new ArrayList<>();

	private final JPanel entries = new JPanel();

	private final JButton add = new JButton(UIRES.get("16px.add.gif"));

	public JEntityDataList(MCreator mcreator, IHelpContext gui) {
		super(mcreator, new BorderLayout(), gui);
		setOpaque(false);

		JPanel topbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topbar.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));

		add.setText(L10N.t("elementgui.common.add_entry"));
		topbar.add(add);

		add("North", topbar);

		entries.setLayout(new BoxLayout(entries, BoxLayout.PAGE_AXIS));
		entries.setOpaque(false);

		add.addActionListener(e -> showNewEntryDialog());

		add("Center", new JScrollPane(PanelUtils.pullElementUp(entries)));

		setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 2),
				L10N.t("elementgui.living_entity.entity_data_entries"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));
		setPreferredSize(new Dimension(getPreferredSize().width, (int) (mcreator.getSize().height * 0.6)));
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		add.setEnabled(enabled);
	}

	private void showNewEntryDialog() {
		MCreatorDialog dialog = new MCreatorDialog(mcreator,
				L10N.t("elementgui.living_entity.entity_data_entries.add_entry.title"), true);

		VTextField name = new VTextField(20);
		name.setValidator(new JavaMemberNameValidator(name, false));
		name.enableRealtimeValidation();
		JComboBox<String> type = new JComboBox<>(new String[] { "Number", "Logic", "String" });

		JButton ok = new JButton(UIManager.getString("OptionPane.okButtonText"));
		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		dialog.getRootPane().setDefaultButton(ok);

		ok.addActionListener(e -> {
			if (name.getValidationStatus().getValidationResultType() == Validator.ValidationResultType.PASSED) {
				dialog.setVisible(false);
				PropertyData<?> data = switch (Objects.requireNonNullElse((String) type.getSelectedItem(), "Number")) {
					case "Logic" -> new PropertyData.LogicType(name.getText());
					case "String" -> new PropertyData.StringType(name.getText());
					default -> new PropertyData.IntegerType(name.getText());
				};
				JEntityDataEntry<?> entry = new JEntityDataEntry<>(mcreator, gui, entries, entryList, data);
				registerEntryUI(entry);
			}
		});
		cancel.addActionListener(e -> dialog.setVisible(false));

		dialog.getContentPane().add("Center", PanelUtils.totalCenterInPanel(PanelUtils.gridElements(2, 2, 50, 20,
				L10N.label("elementgui.living_entity.entity_data_entries.add_entry.name"), name,
				L10N.label("elementgui.living_entity.entity_data_entries.add_entry.type"), type)));
		dialog.getContentPane().add("South", PanelUtils.join(ok, cancel));
		dialog.setSize(360, 180);
		dialog.setLocationRelativeTo(mcreator);
		dialog.setVisible(true);
	}

	public List<DefaultPropertyValue<?>> getEntries() {
		return entryList.stream().map(JEntityDataEntry::getEntry).collect(Collectors.toList());
	}

	public void setEntries(List<DefaultPropertyValue<?>> pool) {
		pool.forEach(e -> {
			JEntityDataEntry<?> entry = new JEntityDataEntry<>(mcreator, gui, entries, entryList, e.property());
			registerEntryUI(entry);
			entry.setEntry(e.defaultValue());
		});
	}

}
