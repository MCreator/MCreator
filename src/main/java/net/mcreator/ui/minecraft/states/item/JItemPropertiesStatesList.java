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

package net.mcreator.ui.minecraft.states.item;

import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.types.Item;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.StateEditorDialog;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.minecraft.JEntriesList;
import net.mcreator.ui.minecraft.states.PropertyData;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.validators.RegistryNameValidator;
import net.mcreator.ui.validation.validators.UniqueNameValidator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class JItemPropertiesStatesList extends JEntriesList {

	private final List<JItemPropertiesListEntry> propertiesList;
	private final List<JItemStatesListEntry> statesList = new ArrayList<>();
	private final AtomicInteger propertyId = new AtomicInteger(0);

	private final List<String> builtinPropertyNames = List.of("damaged", "damage", "lefthanded", "cooldown",
			"custom_model_data");
	private final Map<String, PropertyData> builtinProperties = new LinkedHashMap<>();

	private final JPanel propertyEntries = new JPanel(new GridLayout(0, 1, 5, 5));
	private final JPanel stateEntries = new JPanel();

	private final JButton addProperty = new JButton(UIRES.get("16px.add.gif"));
	private final JButton addState = new JButton(UIRES.get("16px.add.gif"));

	public JItemPropertiesStatesList(MCreator mcreator, IHelpContext gui) {
		super(mcreator, new BorderLayout(), gui);

		Function<String, PropertyData> builtinNumber = name -> new PropertyData(name, Float.class, 0F, 1F, null);
		Function<String, PropertyData> builtinLogic = name -> new PropertyData(name, Boolean.class, null, null, null) {
			@Override public Object getValueFromComponent(JComponent component) {
				if (component instanceof JCheckBox check)
					return check.isSelected() ? 1F : 0F;
				return super.getValueFromComponent(component);
			}

			@Override public boolean setValueOfComponent(JComponent component, Object value) {
				if (component instanceof JCheckBox check) {
					check.setSelected(Float.parseFloat(value.toString()) == 1F);
					check.setText(check.isSelected() ? "True" : "False");
					return true;
				}
				return super.setValueOfComponent(component, value);
			}
		};

		builtinProperties.put("damaged", builtinLogic.apply("damaged"));
		builtinProperties.put("damage", builtinNumber.apply("damage"));
		builtinProperties.put("lefthanded", builtinLogic.apply("lefthanded"));
		builtinProperties.put("cooldown", builtinNumber.apply("cooldown"));

		setOpaque(false);
		propertyEntries.setOpaque(false);
		stateEntries.setOpaque(false);
		stateEntries.setLayout(new BoxLayout(stateEntries, BoxLayout.Y_AXIS));

		propertiesList = new ArrayList<>() {
			@Override public boolean remove(Object o) {
				if (o instanceof JItemPropertiesListEntry entry) {
					if (!isEmpty()) {
						PropertyData data = buildPropertiesMap().get(entry.name.getText());
						statesList.forEach(s -> {
							s.getStateMap().remove(data);
							s.refreshState();
						});
						Set<String> duplicateFilter = new HashSet<>();
						statesList.stream().toList().forEach(s -> {
							if (s.getState() == null || s.getState().equals("") || !duplicateFilter.add(s.getState()))
								s.removeState(stateEntries, statesList);
						});
					} else {
						statesList.stream().toList().forEach(s -> s.removeState(stateEntries, statesList));
					}
				}
				return super.remove(o);
			}
		};

		JPanel topbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topbar.setOpaque(false);

		addProperty.setText(L10N.t("elementgui.item.custom_properties.add"));
		addProperty.addActionListener(e -> {
			propertyId.set(Math.max(propertiesList.size(), propertyId.get()) + 1);
			addPropertiesEntry(propertyId.get());
		});
		topbar.add(addProperty);

		addState.setText(L10N.t("elementgui.item.custom_states.add"));
		addState.addActionListener(e -> editState(null));
		topbar.add(addState);

		JScrollPane left = new JScrollPane(PanelUtils.pullElementUp(propertyEntries));
		left.setOpaque(false);
		left.getViewport().setOpaque(false);
		left.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 2),
				L10N.t("elementgui.item.custom_properties.title"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		JScrollPane right = new JScrollPane(PanelUtils.pullElementUp(stateEntries));
		right.setOpaque(false);
		right.getViewport().setOpaque(false);
		right.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 2),
				L10N.t("elementgui.item.custom_states.title"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		add("North", PanelUtils.centerInPanel(
				HelpUtils.wrapWithHelpButton(gui.withEntry("common/custom_states"), topbar, SwingConstants.LEFT)));
		add("Center", PanelUtils.gridElements(1, 0, left, right));
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		addProperty.setEnabled(enabled);
		addState.setEnabled(enabled);

		propertiesList.forEach(e -> e.setEnabled(enabled));
		statesList.forEach(e -> e.setEnabled(enabled));
	}

	public void reloadDataLists() {
		propertiesList.forEach(JItemPropertiesListEntry::reloadDataLists);
		statesList.forEach(JItemStatesListEntry::reloadDataLists);
	}

	private JItemPropertiesListEntry addPropertiesEntry(int propertyId) {
		JItemPropertiesListEntry pe = new JItemPropertiesListEntry(mcreator, gui, propertyEntries, propertiesList,
				propertyId);

		UniqueNameValidator validator = new UniqueNameValidator(pe.name,
				L10N.t("elementgui.item.custom_property.name_validator"),
				() -> propertiesList.stream().map(e -> e.name.getText()), builtinPropertyNames,
				new RegistryNameValidator(pe.name, L10N.t("elementgui.item.custom_property.name_validator")));
		pe.name.setValidator(validator);
		pe.name.enableRealtimeValidation();
		pe.name.addKeyListener(new KeyAdapter() {
			@Override public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER
						&& pe.name.getValidationStatus() == Validator.ValidationResult.PASSED) {
					String newName = pe.name.getText();
					statesList.forEach(s -> s.rename(pe.nameString, newName));
					pe.rename.requestFocus();
					pe.name.setText(pe.nameString = newName);
				} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					pe.rename.requestFocus();
				}
			}
		});

		registerEntryUI(pe);
		return pe;
	}

	private JItemStatesListEntry addStatesEntry() {
		JItemStatesListEntry se = new JItemStatesListEntry(mcreator, gui, stateEntries, statesList,
				() -> buildPropertiesMap().values().stream().toList(), this::editState);
		registerEntryUI(se);
		return se;
	}

	private void editState(JItemStatesListEntry entry) {
		if (getValidationResult(false).validateIsErrorFree()) {
			LinkedHashMap<PropertyData, Object> stateMap = new LinkedHashMap<>();
			if (entry != null)
				stateMap.putAll(entry.getStateMap());
			if (JOptionPane.OK_OPTION != StateEditorDialog.open(mcreator, buildPropertiesMap(), stateMap, entry == null,
					"item/custom_state"))
				return;

			if (stateMap.isEmpty()) // all properties were unchecked
				JOptionPane.showMessageDialog(mcreator, L10N.t("elementgui.item.custom_states.error_empty"),
						L10N.t("elementgui.item.custom_states.error_empty.title"), JOptionPane.ERROR_MESSAGE);
			else if (statesList.stream().anyMatch(s -> s != entry && s.getStateMap().equals(stateMap)))
				JOptionPane.showMessageDialog(mcreator, L10N.t("elementgui.item.custom_states.error_duplicate"),
						L10N.t("elementgui.item.custom_states.error_duplicate.title"), JOptionPane.ERROR_MESSAGE);
			else
				(entry != null ? entry : addStatesEntry()).setStateMap(stateMap);
		} else {
			Toolkit.getDefaultToolkit().beep();
		}
	}

	private Map<String, PropertyData> buildPropertiesMap() {
		Map<String, PropertyData> props = new LinkedHashMap<>(builtinProperties);
		propertiesList.forEach(
				e -> props.put(e.name.getText(), new PropertyData(e.name.getText(), Float.class, 0F, 1000000F, null)));
		return props;
	}

	public Map<String, Procedure> getProperties() {
		Map<String, Procedure> retVal = new LinkedHashMap<>();
		propertiesList.forEach(e -> retVal.put(e.name.getText(), e.getEntry()));
		return retVal;
	}

	public void setProperties(Map<String, Procedure> properties) {
		properties.forEach((name, value) -> {
			propertyId.set(Math.max(propertiesList.size(), propertyId.get()) + 1);
			if (name.startsWith("property")) {
				try {
					propertyId.set(Math.max(propertyId.get(), Integer.parseInt(name.substring("property".length()))));
				} catch (NumberFormatException ignored) {
				}
			}
			addPropertiesEntry(propertyId.get()).setEntry(name, value);
		});
	}

	public Map<String, Item.ModelEntry> getStates() {
		Map<String, Item.ModelEntry> retVal = new LinkedHashMap<>();
		statesList.forEach(e -> retVal.put(e.getState(), e.getEntry()));
		return retVal;
	}

	public void setStates(Map<String, Item.ModelEntry> states) {
		states.forEach((state, model) -> addStatesEntry().setEntry(state, model));
	}

	public AggregatedValidationResult getValidationResult(boolean includeStates) {
		AggregatedValidationResult validationResult = new AggregatedValidationResult();
		propertiesList.forEach(validationResult::addValidationElement);
		if (includeStates)
			statesList.forEach(validationResult::addValidationElement);
		return validationResult;
	}
}