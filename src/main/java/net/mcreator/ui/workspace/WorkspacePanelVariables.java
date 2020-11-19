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

package net.mcreator.ui.workspace;

import net.mcreator.io.Transliteration;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.TransparentToolBar;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.SpinnerCellEditor;
import net.mcreator.ui.dialogs.NewVariableDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.SlickDarkScrollBarUI;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.optionpane.OptionPaneValidatior;
import net.mcreator.ui.validation.validators.JavaMemeberNameValidator;
import net.mcreator.util.DesktopUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.VariableElement;
import net.mcreator.workspace.elements.VariableElementType;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.*;
import java.awt.*;
import java.util.Arrays;

class WorkspacePanelVariables extends JPanel implements IReloadableFilterable {

	private final WorkspacePanel workspacePanel;
	private final TableRowSorter<TableModel> sorter;
	private final JTable elements;

	WorkspacePanelVariables(WorkspacePanel workspacePanel) {
		super(new BorderLayout(0, 5));
		setOpaque(false);

		this.workspacePanel = workspacePanel;

		elements = new JTable(new DefaultTableModel(
				new Object[] { L10N.t("workspace.variables.variable_name"), L10N.t("workspace.variables.variable_type"),
						L10N.t("workspace.variables.variable_scope"), L10N.t("workspace.variables.initial_value") },
				0) {
			@Override public boolean isCellEditable(int row, int column) {
				if (getValueAt(row, 1).toString().equals("ITEMSTACK") || getValueAt(row, 1).toString().equals("BLOCKSTATE"))
					return column != 3;
				return true;
			}

			@Override public void setValueAt(Object value, int row, int column) {
				Object oldVal = elements.getValueAt(row, column);
				if (value.equals(oldVal))
					return;

				if (column != 3) {
					int n = JOptionPane.showConfirmDialog(workspacePanel.mcreator,
							"<html>Are you sure that you want to change this variable?"
									+ "<br>If this variable is in use, this action might cause compilation errors."
									+ "<br>If you just created it, it is perfectly fine to change it.", "Confirmation",
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (n == JOptionPane.YES_OPTION) {
						super.setValueAt(value, row, column);
						if (column == 1) { // variable type has been changed
							VariableElementType type = (VariableElementType) getValueAt(row, column);
							if (type == VariableElementType.NUMBER) {
								elements.setValueAt("0", row, 3);
							} else if (type == VariableElementType.LOGIC) {
								elements.setValueAt("false", row, 3);
							} else if (type == VariableElementType.STRING) {
								elements.setValueAt("", row, 3);
							} else {
								elements.setValueAt("", row, 3);
							}
						}
					}
				} else {
					super.setValueAt(value, row, column);
				}
			}
		}) {
			@Override public TableCellEditor getCellEditor(int row, int column) {
				int modelColumn = convertColumnIndexToModel(column);
				if (modelColumn == 2) {
					return new DefaultCellEditor(new JComboBox<>(VariableElementType.Scope.values()));
				} else if (modelColumn == 1) {
					return new DefaultCellEditor(new JComboBox<>(
							new VariableElementType[] { VariableElementType.NUMBER, VariableElementType.LOGIC,
									VariableElementType.STRING, VariableElementType.ITEMSTACK}));
				} else if (modelColumn == 0) {
					VTextField name = new VTextField();
					name.enableRealtimeValidation();
					Validator validator = new JavaMemeberNameValidator(name, false);
					name.setValidator(() -> {
						String textname = Transliteration.transliterateString(name.getText());
						for (int i = 0; i < elements.getRowCount(); i++) {
							String nameinrow = (String) elements.getValueAt(i, 0);
							if (i != row && textname.equals(nameinrow))
								return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
										"This name already exists");
						}
						return validator.validate();
					});
					return new DefaultCellEditor(name) {
						@Override public boolean stopCellEditing() {
							return name.getValidationStatus().getValidationResultType()
									!= Validator.ValidationResultType.ERROR && super.stopCellEditing();
						}
					};
				} else if (modelColumn == 3) {
					VariableElementType data = (VariableElementType) elements.getValueAt(row, 1);
					if (data == VariableElementType.NUMBER) {
						JSpinner spinner = new JSpinner(
								new SpinnerNumberModel(0, -Double.MAX_VALUE, Double.MAX_VALUE, 0.1));
						return new SpinnerCellEditor(spinner);
					} else if (data == VariableElementType.LOGIC) {
						return new DefaultCellEditor(new JComboBox<>(new String[] { "true", "false" }));
					}
				}

				return super.getCellEditor(row, column);
			}

			@Override public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component component = super.prepareRenderer(renderer, row, column);
				if (column == 1) {
					VariableElementType value = (VariableElementType) getModel().getValueAt(row, column);
					if (value != null) {
						component.setForeground(new Color(value.getColor()).brighter());
					}
				} else {
					component.setForeground(elements.getForeground());
				}
				return component;
			}
		};

		sorter = new TableRowSorter<>(elements.getModel());
		elements.setRowSorter(sorter);
		elements.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		elements.setSelectionBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
		elements.setForeground(Color.white);
		elements.setSelectionForeground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		elements.setBorder(BorderFactory.createEmptyBorder());
		elements.setGridColor((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
		elements.setRowHeight(28);
		elements.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		ComponentUtils.deriveFont(elements, 13);

		JTableHeader header = elements.getTableHeader();
		header.setBackground((Color) UIManager.get("MCreatorLAF.MAIN_TINT"));
		header.setForeground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));

		JScrollPane sp = new JScrollPane(elements);
		sp.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		sp.getViewport().setOpaque(false);
		sp.getVerticalScrollBar().setUnitIncrement(11);
		sp.getVerticalScrollBar().setUI(new SlickDarkScrollBarUI((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"),
				(Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"), sp.getVerticalScrollBar()));
		sp.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));

		sp.setColumnHeaderView(null);

		JPanel holder = new JPanel(new BorderLayout());
		holder.setOpaque(false);
		holder.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		holder.add(sp);

		add("Center", holder);

		TransparentToolBar bar = new TransparentToolBar();
		bar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 0));

		JButton addvar = L10N.button("workspace.variables.add_new");
		addvar.setIcon(UIRES.get("16px.add.gif"));
		addvar.setContentAreaFilled(false);
		addvar.setOpaque(false);
		ComponentUtils.deriveFont(addvar, 12);
		addvar.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		bar.add(addvar);

		JButton delvar = L10N.button("workspace.variables.remove_selected");
		delvar.setIcon(UIRES.get("16px.delete.gif"));
		delvar.setContentAreaFilled(false);
		delvar.setOpaque(false);
		ComponentUtils.deriveFont(delvar, 12);
		delvar.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		bar.add(delvar);

		JButton help = L10N.button("workspace.variables.help");
		help.setIcon(UIRES.get("16px.info"));
		help.setContentAreaFilled(false);
		help.setOpaque(false);
		ComponentUtils.deriveFont(help, 12);
		help.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		bar.add(help);

		help.addActionListener(e -> DesktopUtils.browseSafe(MCreatorApplication.SERVER_DOMAIN + "/wiki/variables"));

		add("North", bar);

		addvar.addActionListener(e -> {
			VariableElement element = NewVariableDialog
					.showNewVariableDialog(workspacePanel.mcreator, true, new OptionPaneValidatior() {
								@Override public ValidationResult validate(JComponent component) {
									Validator validator = new JavaMemeberNameValidator((VTextField) component, false);
									String textname = Transliteration.transliterateString(((VTextField) component).getText());
									for (int i = 0; i < elements.getRowCount(); i++) {
										String nameinrow = (String) elements.getValueAt(i, 0);
										if (textname.equals(nameinrow))
											return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
													"This name already exists");
									}
									return validator.validate();
								}
							}, VariableElementType.LOGIC, VariableElementType.NUMBER, VariableElementType.STRING,
							VariableElementType.ITEMSTACK, VariableElementType.BLOCKSTATE);
			if (element != null) {
				workspacePanel.mcreator.getWorkspace().addVariableElement(element);
				reloadElements();
			}
		});

		delvar.addActionListener(e -> {
			if (elements.getSelectedRow() == -1)
				return;

			int n = JOptionPane.showConfirmDialog(workspacePanel.mcreator,
					"<html>Are you sure that you want to remove selected variables?"
							+ "<br>If this variable is in use, this action might cause compilation errors.",
					"Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (n == JOptionPane.YES_OPTION) {
				Arrays.stream(elements.getSelectedRows()).mapToObj(el -> (String) elements.getValueAt(el, 0))
						.forEach(el -> {
							VariableElement element = new VariableElement();
							element.setName(el);
							workspacePanel.mcreator.getWorkspace().removeVariableElement(element);
						});
				reloadElements();
			}
		});

		// save values on table edit, do it in another thread
		elements.getModel().addTableModelListener(e -> new Thread(() -> {
			if (e.getType() == TableModelEvent.UPDATE) {
				Workspace workspace = workspacePanel.mcreator.getWorkspace();
				workspace.getVariableElements().clear();
				for (int i = 0; i < elements.getModel().getRowCount(); i++) {
					VariableElement element = new VariableElement();
					element.setType((VariableElementType) elements.getValueAt(i, 1));
					element.setName(Transliteration.transliterateString((String) elements.getValueAt(i, 0)));
					element.setValue(elements.getValueAt(i, 3));
					element.setScope((VariableElementType.Scope) elements.getValueAt(i, 2));
					workspace.addVariableElement(element);
				}
			}
		}).start());

	}

	public void reloadElements() {
		int row = elements.getSelectedRow();

		DefaultTableModel model = (DefaultTableModel) elements.getModel();
		model.setRowCount(0);

		for (VariableElement variable : workspacePanel.mcreator.getWorkspace().getVariableElements()) {
			model.addRow(
					new Object[] { variable.getName(), variable.getType(), variable.getScope(), variable.getValue() });
		}
		refilterElements();

		try {
			elements.setRowSelectionInterval(row, row);
		} catch (Exception ignored) {
		}
	}

	public void refilterElements() {
		try {
			sorter.setRowFilter(RowFilter.regexFilter(workspacePanel.search.getText()));
		} catch (Exception ignored) {
		}
	}

}
