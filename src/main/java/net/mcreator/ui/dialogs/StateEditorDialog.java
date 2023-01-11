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
	 * @param parent     The workspace window in which this method was called.
	 * @param properties Keys are property names, values store data of those properties.
	 * @param stateMap   The property-object map representation of state that should be edited.
	 * @param newState   Whether the state is just being created.
	 * @param helpPath   The path to the help context file used as dialog's tooltip.
	 * @return The dialog option the user chose after editing properties' values.
	 */
	public static int open(MCreator parent, Collection<PropertyData<?, ?>> properties,
			LinkedHashMap<PropertyData<?, ?>, Object> stateMap, boolean newState, String helpPath) {
		AtomicInteger retVal = new AtomicInteger(JOptionPane.CLOSED_OPTION);
		MCreatorDialog dialog = new MCreatorDialog(parent, L10N.t("dialog.state_editor.title"), true);

		Map<PropertyData<?, ?>, StatePart> entryMap = new HashMap<>();
		JPanel entries = new JPanel(new GridLayout(0, 1, 5, 5));
		entries.setOpaque(false);

		for (PropertyData<?, ?> param : properties) {
			JComponent component = generatePropertyComponent(param);
			if (component != null) {
				setValueOfComponent(component, param, stateMap.get(param));
				StatePart statePart = new StatePart(entries, param.getName(), component);
				statePart.useEntry.setSelected(stateMap.containsKey(param) || newState);
				entryMap.put(param, statePart);
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
			for (PropertyData<?, ?> param : properties) {
				StatePart part = entryMap.get(param);
				if (part.useEntry.isSelected())
					stateMap.put(param, getValueFromComponent(part.entryComponent, param));
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

	private static JComponent generatePropertyComponent(PropertyData<?, ?> param) {
		Object value = getDefaultValueForType(param.uiType());
		if (value == null)
			return null;

		if (param.uiType() == Boolean.class) {
			return new JCheckBox("", (boolean) value) {
				@Override public String getText() {
					return isSelected() ? "True" : "False";
				}
			};
		} else if (param.uiType() == Integer.class) {
			value = Math.max(param.min(), Math.min(param.max(), (Integer) value));
			JSpinner box = new JSpinner(new SpinnerNumberModel((int) value, (int) param.min(), (int) param.max(), 1));
			box.setPreferredSize(new Dimension(105, 22));
			return box;
		} else if (param.uiType() == Float.class) {
			value = Math.max(param.min(), Math.min(param.max(), (float) value));
			JSpinner box = new JSpinner(
					new SpinnerNumberModel((float) value, (float) param.min(), (float) param.max(), 0.001));
			box.setPreferredSize(new Dimension(130, 22));
			return box;
		} else if (param.uiType() == String.class) {
			JComboBox<String> box = new JComboBox<>(param.arrayData());
			box.setEditable(false);
			box.setSelectedIndex(Math.max(0, Arrays.asList(param.arrayData()).indexOf(value.toString())));
			return box;
		}
		return null;
	}

	private static Object getValueFromComponent(JComponent component, PropertyData<?, ?> param) {
		Object value = null;
		if (component == null) {
			value = getDefaultValueForType(param.uiType());
		} else if (param.uiType() == Boolean.class) {
			value = ((JCheckBox) component).isSelected();
		} else if (param.uiType() == Integer.class) {
			value = ((JSpinner) component).getValue();
		} else if (param.uiType() == Float.class) {
			Number num = (Number) ((JSpinner) component).getValue();
			value = Math.round(num.floatValue() * 1000) / 1000F;
		} else if (param.uiType() == String.class) {
			value = ((JComboBox<?>) component).getSelectedItem();
		}
		return value == null ? null : param.fromUIValue(value);
	}

	private static void setValueOfComponent(JComponent component, PropertyData<?, ?> param, Object value) {
		value = value != null ? param.toUIValue(value) : getDefaultValueForType(param.uiType());
		if (value == null)
			return;

		if (param.uiType() == Boolean.class) {
			((JCheckBox) component).setSelected(Boolean.parseBoolean(value.toString()));
		} else if (param.uiType() == Integer.class) {
			((JSpinner) component).setValue(
					Math.max(param.min(), Math.min(param.max(), Integer.parseInt(value.toString()))));
		} else if (param.uiType() == Float.class) {
			((JSpinner) component).setValue(
					Math.max(param.min(), Math.min(param.max(), Float.parseFloat(value.toString()))));
		} else if (param.uiType() == String.class) {
			((JComboBox<?>) component).setSelectedItem(value);
		}
	}

	private static Object getDefaultValueForType(Class<?> type) {
		if (type == Boolean.class)
			return false;
		else if (type == Integer.class)
			return 0;
		else if (type == Float.class)
			return 0F;
		else if (type == String.class)
			return "";
		return null;
	}

	private static final class StatePart extends JPanel {

		private final JCheckBox useEntry = new JCheckBox();
		private final JComponent entryComponent;

		private StatePart(JPanel parent, String property, JComponent component) {
			super(new FlowLayout(FlowLayout.LEFT));
			entryComponent = component;

			JPanel settings = new JPanel();
			settings.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
			settings.add(new JLabel(property));
			settings.add(new JLabel("="));
			settings.add(entryComponent);

			useEntry.setSelected(true);
			useEntry.setToolTipText(L10N.t("dialog.state_editor.use_entry"));
			useEntry.addChangeListener(e -> {
				entryComponent.setEnabled(useEntry.isSelected());
				settings.setBackground(useEntry.isSelected() ?
						(Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT") :
						(Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
			});

			add(useEntry);
			add(settings);

			parent.add(PanelUtils.expandHorizontally(this));
			parent.revalidate();
			parent.repaint();
		}
	}
}
