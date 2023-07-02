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
import net.mcreator.ui.component.util.*;
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
import net.mcreator.ui.validation.validators.UniqueNameValidator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JEntityDataList extends JEntriesList {

	private final DefaultTableModel entriesModel;
	private final JTable entries;

	public JEntityDataList(MCreator mcreator, IHelpContext gui) {
		super(mcreator, new BorderLayout(), gui);
		setOpaque(false);

		entriesModel = new DefaultTableModel(new Object[] { L10N.t("elementgui.living_entity.entity_data_entries.name"),
				L10N.t("elementgui.living_entity.entity_data_entries.type"),
				L10N.t("elementgui.living_entity.entity_data_entries.default_value") }, 0) {
			@Override public boolean isCellEditable(int row, int column) {
				return column == 2;
			}

			@Override public void setValueAt(Object value, int row, int column) {
				if (column == 2 && !entries.getValueAt(row, column).equals(value))
					super.setValueAt(value, row, column);
			}
		};
		entries = new JTable(entriesModel) {
			@Override public TableCellEditor getCellEditor(int row, int column) {
				if (convertColumnIndexToModel(column) == 2) {
					String name = (String) getValueAt(row, 0);
					Object value = convertValue(row);
					switch ((String) getValueAt(row, 1)) {
					case "Number" -> {
						return new SpinnerCellEditor(
								(JSpinner) new PropertyData.IntegerType(name).getComponent(mcreator, value));
					}
					case "Logic" -> {
						return new CheckBoxCellEditor(
								(JCheckBox) new PropertyData.LogicType(name).getComponent(mcreator, value));
					}
					case "String" -> {
						return new DefaultCellEditor(
								(JTextField) new PropertyData.StringType(name).getComponent(mcreator, value));
					}
					}
				}

				return super.getCellEditor(row, column);
			}
		};
		entries.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		entries.setSelectionBackground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
		entries.setSelectionForeground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		entries.setBorder(BorderFactory.createEmptyBorder());
		entries.setGridColor((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
		entries.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		entries.setRowHeight(30);
		entries.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		ComponentUtils.deriveFont(entries, 14);

		JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topBar.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));

		add.setText(L10N.t("elementgui.living_entity.entity_data_entries.add_entry"));
		add.addActionListener(e -> showNewEntryDialog());
		topBar.add(add);

		JButton remove = new JButton(UIRES.get("16px.clear"));
		remove.setText(L10N.t("elementgui.living_entity.entity_data_entries.remove_entry"));
		remove.addActionListener(e -> {
			if (entries.getSelectedRow() != -1)
				entriesModel.removeRow(entries.getSelectedRow());
		});
		topBar.add(remove);

		add("North", topBar);
		add("Center", new JScrollPane(entries));

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
		JComboBox<String> type = new JComboBox<>(new String[] { "Number", "Logic", "String" });

		UniqueNameValidator validator = new UniqueNameValidator(L10N.t("workspace.variables.variable_name"),
				name::getText, () -> TableUtil.getColumnContents(entries, 0).stream(),
				new JavaMemberNameValidator(name, false));
		validator.setIsPresentOnList(false);
		name.setValidator(validator);
		name.enableRealtimeValidation();

		JButton ok = new JButton(UIManager.getString("OptionPane.okButtonText"));
		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		dialog.getRootPane().setDefaultButton(ok);

		ok.addActionListener(e -> {
			if (name.getValidationStatus().getValidationResultType() == Validator.ValidationResultType.PASSED) {
				dialog.setVisible(false);
				String selType = Objects.requireNonNullElse((String) type.getSelectedItem(), "Number");
				entriesModel.addRow(new Object[] { name.getText(), selType, (switch (selType) {
					case "Logic" -> new PropertyData.LogicType(name.getText());
					case "String" -> new PropertyData.StringType(name.getText());
					default -> new PropertyData.IntegerType(name.getText());
				}).getDefaultValue() + "" });
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

	private Object convertValue(int row) {
		Object value = entries.getValueAt(row, 2);
		return switch ((String) entries.getValueAt(row, 1)) {
			case "Number" -> Integer.parseInt(value.toString());
			case "Logic" -> Boolean.parseBoolean(value.toString());
			default -> value;
		};
	}

	public List<DefaultPropertyValue<?>> getEntries() {
		List<DefaultPropertyValue<?>> retVal = new ArrayList<>();
		for (int i = 0; i < entriesModel.getRowCount(); i++) {
			String name = (String) entries.getValueAt(i, 0);
			PropertyData<?> data = switch ((String) entries.getValueAt(i, 1)) {
				case "Logic" -> new PropertyData.LogicType(name);
				case "String" -> new PropertyData.StringType(name);
				default -> new PropertyData.IntegerType(name);
			};
			retVal.add(DefaultPropertyValue.create(data, convertValue(i)));
		}
		return retVal;
	}

	public void setEntries(List<DefaultPropertyValue<?>> pool) {
		pool.forEach(e -> entriesModel.addRow(
				new Object[] { e.property().getName(), switch (e.property().getClass().getSimpleName()) {
					case "LogicType" -> "Logic";
					case "StringType" -> "String";
					default -> "Number";
				}, e.defaultValue() + "" }));
	}

}
