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
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StateEditorDialog {

	public static String open(MCreator mcreator, String initialState, Map<String, PropertyData> properties,
			String elementType) {
		AtomicReference<String> retVal = new AtomicReference<>(initialState);
		MCreatorDialog dialog = new MCreatorDialog(mcreator, L10N.t("dialog.state_editor.title"), true);
		dialog.getContentPane().setLayout(new BorderLayout());

		List<StateEntry> entryList = new ArrayList<>();
		JPanel entries = new JPanel(new GridLayout(0, 1, 5, 5));
		entries.setOpaque(false);

		Map<String, Object> values = initialState != null && !initialState.equals("") ?
				Stream.of(initialState.split(","))
						.collect(Collectors.toMap(e -> e.split("=")[0], e -> e.split("=")[1])) :
				Collections.emptyMap();
		properties.forEach((name, data) -> {
			JComponent component = generatePropertyComponent(data);
			if (component != null) {
				StateEntry stateEntry = new StateEntry(entries, entryList, name, component);
				if (!values.isEmpty() && values.containsKey(stateEntry.property)) {
					if (!data.setValueOfComponent(stateEntry.entryComponent, values.get(stateEntry.property)))
						setValueOfComponent(stateEntry.entryComponent, data, values.get(stateEntry.property));
				} else {
					setValueOfComponent(stateEntry.entryComponent, data, null);
					if (initialState != null && !initialState.equals("")) // property is declared as not used
						stateEntry.useEntry.doClick();
				}
			}
		});

		JPanel stateList = new JPanel(new BorderLayout());
		stateList.setOpaque(false);
		stateList.setPreferredSize(new Dimension(270, 340));
		stateList.add("Center", new JScrollPane(PanelUtils.pullElementUp(entries)));

		JButton ok = new JButton(UIManager.getString("OptionPane.okButtonText"));
		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		dialog.getRootPane().setDefaultButton(ok);

		ok.addActionListener(e -> {
			StringJoiner joiner = new StringJoiner(",");
			entryList.stream().filter(el -> el.useEntry.isSelected()).forEach(el -> {
				Optional<PropertyData> prop = properties.entrySet().stream()
						.filter(element -> element.getKey().equals(el.property)).map(Map.Entry::getValue).findFirst();
				if (prop.isPresent()) {
					Object value = prop.get().getValueFromComponent(el.entryComponent);
					if (value == null)
						value = getValueFromComponent(el.entryComponent, prop.get().type());
					joiner.add(el.property + "=" + value);
				}
			});
			retVal.set(joiner.toString());
			dialog.setVisible(false);
		});
		cancel.addActionListener(e -> dialog.setVisible(false));

		JComponent editor = PanelUtils.centerAndSouthElement(stateList, PanelUtils.join(ok, cancel));
		dialog.getContentPane().add("Center",
				HelpUtils.combineHelpTextAndComponent(IHelpContext.NONE.withEntry(elementType + "/custom_state"),
						L10N.label("dialog.state_editor.header"), editor, 7));

		dialog.setSize(300, 400);
		dialog.setLocationRelativeTo(mcreator);
		dialog.setVisible(true);

		return retVal.get();
	}

	private static JComponent generatePropertyComponent(PropertyData param) {
		Object value = getDefaultValueForType(param.type());
		if (value != null) {
			if (param.type().equals(boolean.class) || param.type().equals(Boolean.class)) {
				JCheckBox box = new JCheckBox();
				if ((boolean) value) {
					box.setSelected(true);
					box.setText("True");
				} else {
					box.setSelected(false);
					box.setText("False");
				}
				box.addActionListener(e -> box.setText(box.isSelected() ? "True" : "False"));
				return box;
			} else if (param.type().equals(int.class) || param.type().equals(Integer.class)) {
				value = Math.max((int) param.min(), Math.min((int) param.max(), (Integer) value));
				JSpinner box = new JSpinner(
						new SpinnerNumberModel((int) value, (int) param.min(), (int) param.max(), 1));
				box.setPreferredSize(new Dimension(125, 22));
				return box;
			} else if (param.type().equals(float.class) || param.type().equals(Float.class)) {
				value = Math.max((float) param.min(), Math.min((float) param.max(), (float) value));
				JSpinner box = new JSpinner(
						new SpinnerNumberModel((float) value, (float) param.min(), (float) param.max(), 0.001));
				box.setPreferredSize(new Dimension(125, 22));
				return box;
			} else if (param.type().equals(String.class) && param.arrayData() != null) {
				JComboBox<String> box = new JComboBox<>(param.arrayData());
				box.setEditable(false);
				box.setSelectedItem(value);
				return box;
			}
		}
		return null;
	}

	private static Object getValueFromComponent(JComponent component, Class<?> type) {
		if (component == null)
			return getDefaultValueForType(type);
		else if (type.equals(boolean.class) || type.equals(Boolean.class))
			return ((JCheckBox) component).isSelected();
		else if (type.equals(int.class) || type.equals(Integer.class) || type.equals(float.class) || type.equals(
				Float.class))
			return ((JSpinner) component).getValue();
		else if (type.equals(String.class))
			return ((JComboBox<?>) component).getSelectedItem();
		return null;
	}

	private static void setValueOfComponent(JComponent component, PropertyData property, Object value) {
		if (value == null)
			value = getDefaultValueForType(property.type());
		if (value != null && component != null) {
			if (property.type().equals(boolean.class) || property.type().equals(Boolean.class))
				((JCheckBox) component).setSelected((boolean) value);
			else if (property.type().equals(int.class) || property.type().equals(Integer.class))
				((JSpinner) component).setValue(Math.max((Integer) property.min(),
						Math.min((Integer) property.max(), Integer.parseInt(value.toString()))));
			else if (property.type().equals(float.class) || property.type().equals(Float.class))
				((JSpinner) component).setValue(Math.max((Float) property.min(),
						Math.min((Float) property.max(), Float.parseFloat(value.toString()))));
			else if (property.type().equals(String.class))
				((JComboBox<?>) component).setSelectedItem(value);
		}
	}

	private static Object getDefaultValueForType(Class<?> type) {
		if (type.equals(boolean.class) || type.equals(Boolean.class))
			return false;
		else if (type.equals(int.class) || type.equals(Integer.class))
			return 0;
		else if (type.equals(float.class) || type.equals(Float.class))
			return 0F;
		else if (type.equals(String.class))
			return "";
		return null;
	}

	private static class StateEntry extends JPanel {

		private final JCheckBox useEntry = new JCheckBox();
		private final String property;
		private final JComponent entryComponent;

		private StateEntry(JPanel parent, List<StateEntry> entryList, String name, JComponent component) {
			super(new FlowLayout(FlowLayout.LEFT));
			property = name;
			entryComponent = component;

			useEntry.setSelected(true);

			final JComponent container = PanelUtils.expandHorizontally(this);

			parent.add(container);
			entryList.add(this);

			JPanel settings = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			settings.setBackground(((Color) UIManager.get("MCreatorLAF.DARK_ACCENT")).brighter());
			settings.add(new JLabel(property));
			settings.add(new JLabel("="));
			settings.add(entryComponent);

			add(useEntry);
			add(settings);

			useEntry.addActionListener(e -> {
				entryComponent.setEnabled(useEntry.isSelected());
				settings.setBackground(useEntry.isSelected() ?
						((Color) UIManager.get("MCreatorLAF.DARK_ACCENT")).brighter() :
						((Color) UIManager.get("MCreatorLAF.DARK_ACCENT")).darker());
			});

			parent.revalidate();
			parent.repaint();
		}
	}

}

