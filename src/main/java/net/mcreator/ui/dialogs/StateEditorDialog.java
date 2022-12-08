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

package net.mcreator.ui.dialogs;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.states.PropertyData;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class StateEditorDialog {

	/**
	 * Opens a dialog to edit values of passed properties list.
	 *
	 * @param parent       The workspace window in which this method was called.
	 * @param properties   Keys are property names, values store data of those properties.
	 * @param stateMap     The property-object map representation of state that should be edited.
	 * @param newState     Whether the state is just being created.
	 * @param helpPath     The path to the help context file used as dialog's tooltip.
	 * @return The dialog option the user chose after editing properties' values.
	 */
	public static int open(MCreator parent, Collection<PropertyData> properties,
			LinkedHashMap<PropertyData, Object> stateMap, boolean newState, String helpPath) {
		AtomicInteger retVal = new AtomicInteger(JOptionPane.CLOSED_OPTION);
		MCreatorDialog dialog = new MCreatorDialog(parent, L10N.t("dialog.state_editor.title"), true);

		Map<String, StatePart> entryMap = new HashMap<>();
		JPanel entries = new JPanel(new GridLayout(0, 1, 5, 5));
		entries.setOpaque(false);

		for (PropertyData data : properties) {
			JComponent component = generatePropertyComponent(data);
			if (component != null) {
				StatePart statePart = new StatePart(entries, data.getName(), component);
				if (stateMap.containsKey(data)) {
					if (!data.setValueOfComponent(statePart.entryComponent, stateMap.get(data)))
						setValueOfComponent(statePart.entryComponent, data, stateMap.get(data));
				} else {
					setValueOfComponent(statePart.entryComponent, data, null);
					if (!newState) // property is not used in this state
						statePart.useEntry.doClick();
				}
				entryMap.put(data.getName(), statePart);
			}
		}

		JPanel stateParts = new JPanel(new BorderLayout());
		stateParts.setOpaque(false);
		stateParts.add("Center", new JScrollPane(PanelUtils.pullElementUp(entries)));

		JButton ok = new JButton(newState ? L10N.t("dialog.state_editor.create") : L10N.t("dialog.state_editor.save"));
		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		dialog.getRootPane().setDefaultButton(ok);

		ok.addActionListener(e -> {
			stateMap.clear();
			for (PropertyData param : properties) {
				StatePart part = entryMap.get(param.getName());
				if (part.useEntry.isSelected()) {
					Object value = param.getValueFromComponent(part.entryComponent);
					if (value == null)
						value = getValueFromComponent(part.entryComponent, param);
					stateMap.put(param, value);
				}
			}
			retVal.set(JOptionPane.OK_OPTION);
			dialog.setVisible(false);
		});
		cancel.addActionListener(e -> {
			retVal.set(JOptionPane.CANCEL_OPTION);
			dialog.setVisible(false);
		});

		Component editor = HelpUtils.stackHelpTextAndComponent(IHelpContext.NONE.withEntry(helpPath),
				L10N.t("dialog.state_editor.header"), stateParts, 7);
		dialog.getContentPane().add("Center", PanelUtils.centerAndSouthElement(editor, PanelUtils.join(ok, cancel)));

		dialog.setSize(300, 400);
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);

		return retVal.get();
	}

	private static JComponent generatePropertyComponent(PropertyData param) {
		Object value = getDefaultValueForType(param.type());
		if (value != null) {
			if (param.type().equals(Boolean.class)) {
				JCheckBox box = new JCheckBox();
				box.setSelected((boolean) value);
				box.setText((boolean) value ? "True" : "False");
				box.addActionListener(e -> box.setText(box.isSelected() ? "True" : "False"));
				return box;
			} else if (param.type().equals(Integer.class)) {
				value = Math.max((int) param.min(), Math.min((int) param.max(), (Integer) value));
				JSpinner box = new JSpinner(
						new SpinnerNumberModel((int) value, (int) param.min(), (int) param.max(), 1));
				box.setPreferredSize(new Dimension(125, 22));
				return box;
			} else if (param.type().equals(Float.class)) {
				value = Math.max((float) param.min(), Math.min((float) param.max(), (float) value));
				JSpinner box = new JSpinner(
						new SpinnerNumberModel((float) value, (float) param.min(), (float) param.max(), 0.001));
				box.setPreferredSize(new Dimension(125, 22));
				return box;
			} else if (param.type().equals(String.class)) {
				JComboBox<String> box = new JComboBox<>(param.arrayData());
				box.setEditable(false);
				if (Arrays.asList(param.arrayData()).contains(value.toString()))
					box.setSelectedItem(value);
				return box;
			}
		}
		return null;
	}

	private static Object getValueFromComponent(JComponent component, PropertyData param) {
		if (component == null) {
			return getDefaultValueForType(param.type());
		} else if (param.type().equals(Boolean.class)) {
			return ((JCheckBox) component).isSelected();
		} else if (param.type().equals(Integer.class)) {
			return ((JSpinner) component).getValue();
		} else if (param.type().equals(Float.class)) {
			Number num = (Number) ((JSpinner) component).getValue();
			return Math.round(num.floatValue() * 1000) / 1000F;
		} else if (param.type().equals(String.class)) {
			return ((JComboBox<?>) component).getSelectedItem();
		}
		return null;
	}

	private static void setValueOfComponent(JComponent component, PropertyData param, Object value) {
		if (value == null)
			value = getDefaultValueForType(param.type());
		if (value != null && component != null) {
			if (param.type().equals(Boolean.class)) {
				((JCheckBox) component).setSelected(Boolean.parseBoolean(value.toString()));
			} else if (param.type().equals(Integer.class)) {
				((JSpinner) component).setValue(Math.max((Integer) param.min(),
						Math.min((Integer) param.max(), Integer.parseInt(value.toString()))));
			} else if (param.type().equals(Float.class)) {
				((JSpinner) component).setValue(Math.max((Float) param.min(),
						Math.min((Float) param.max(), Float.parseFloat(value.toString()))));
			} else if (param.type().equals(String.class)) {
				((JComboBox<?>) component).setSelectedItem(value);
			}
		}
	}

	private static Object getDefaultValueForType(Class<?> type) {
		if (type.equals(Boolean.class))
			return false;
		else if (type.equals(Integer.class))
			return 0;
		else if (type.equals(Float.class))
			return 0F;
		else if (type.equals(String.class))
			return "";
		return null;
	}

	private static class StatePart extends JPanel {

		private final JCheckBox useEntry = new JCheckBox();
		private final JComponent entryComponent;

		private StatePart(JPanel parent, String property, JComponent component) {
			super(new FlowLayout(FlowLayout.LEFT));
			entryComponent = component;

			JPanel settings = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			settings.setBackground(((Color) UIManager.get("MCreatorLAF.DARK_ACCENT")).brighter());
			settings.add(new JLabel(property));
			settings.add(new JLabel("="));
			settings.add(entryComponent);

			useEntry.setSelected(true);
			useEntry.setToolTipText(L10N.t("dialog.state_editor.use_entry"));
			useEntry.addActionListener(e -> {
				entryComponent.setEnabled(useEntry.isSelected());
				settings.setBackground(useEntry.isSelected() ?
						((Color) UIManager.get("MCreatorLAF.DARK_ACCENT")).brighter() :
						((Color) UIManager.get("MCreatorLAF.DARK_ACCENT")).darker());
			});

			add(useEntry);
			add(settings);

			parent.add(PanelUtils.expandHorizontally(this));
			parent.revalidate();
			parent.repaint();
		}
	}
}
