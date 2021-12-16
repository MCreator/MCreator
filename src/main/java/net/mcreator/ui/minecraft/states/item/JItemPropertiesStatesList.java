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

import net.mcreator.element.parts.Procedure;
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
import net.mcreator.ui.validation.validators.PropertyNameValidator;
import net.mcreator.ui.validation.validators.RegistryNameValidator;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JItemPropertiesStatesList extends JEntriesList {

	private final List<JItemPropertiesListEntry> propertiesList = new ArrayList<>();
	private final List<JItemStatesListEntry> statesList = new ArrayList<>();
	private final AtomicInteger propertyId = new AtomicInteger(0);

	private final List<String> builtinPropertyNames = List.of("damaged", "damage", "lefthanded", "cooldown",
			"custom_model_data");
	private final Map<String, PropertyData> builtinProperties = new LinkedHashMap<>();
	private final PropertyData customNumber = new PropertyData(Float.class, 0F, 1000000F, null);

	private final JPanel propertyEntries = new JPanel(new GridLayout(0, 1, 5, 5));
	private final JPanel stateEntries = new JPanel(new GridLayout(0, 1, 5, 5));

	private final JButton addProperty = new JButton(UIRES.get("16px.add.gif"));
	private final JButton addState = new JButton(UIRES.get("16px.add.gif"));

	public JItemPropertiesStatesList(MCreator mcreator, IHelpContext gui) {
		super(mcreator, new BorderLayout(), gui);

		PropertyData builtinNumber = new PropertyData(Float.class, 0F, 1F, null);
		PropertyData builtinLogic = new PropertyData(Boolean.class, null, null, null) {
			@Override public Object getValueFromComponent(JComponent component) {
				return component instanceof JCheckBox check && check.isSelected() ? 1F : 0F;
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

		builtinProperties.put("damaged", builtinLogic);
		builtinProperties.put("damage", builtinNumber);
		builtinProperties.put("lefthanded", builtinLogic);
		builtinProperties.put("cooldown", builtinNumber);

		propertyEntries.setOpaque(false);
		stateEntries.setOpaque(false);

		propertyEntries.addContainerListener(new ContainerAdapter() {
			@Override public void componentRemoved(ContainerEvent e) {
				if (propertiesList.size() > 0) {
					statesList.forEach(s -> s.state.setText(Stream.of(s.state.getText().split(","))
							.filter(el -> getPropertiesMap().entrySet().stream()
									.anyMatch(p -> p.getKey().equals(el.split("=")[0])))
							.collect(Collectors.joining(","))));
					Set<String> duplicates = new HashSet<>(); // when states are trimmed, we remove possible duplicates
					statesList.stream().toList().forEach(entry -> {
						if (entry.state.getText() == null || entry.state.getText().equals("") || !duplicates.add(
								entry.state.getText()))
							entry.removeState(stateEntries, statesList);
					});
				}
			}
		});

		JPanel topbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topbar.setOpaque(false);

		addProperty.setText(L10N.t("elementgui.item.custom_properties.add"));
		addProperty.addActionListener(e -> {
			propertyId.set(Math.max(propertiesList.size(), propertyId.get()) + 1);
			addPropertiesEntry(propertyId.get());
		});
		topbar.add(addProperty);

		addState.setText(L10N.t("elementgui.item.custom_states.add"));
		addState.addActionListener(e -> {
			if (getValidationResult(false).validateIsErrorFree()) {
				String state = StateEditorDialog.open(mcreator, "", getPropertiesMap(), "item");
				if (state == null || state.equals(""))
					JOptionPane.showMessageDialog(mcreator, L10N.t("elementgui.item.custom_states.add.error_empty"),
							L10N.t("elementgui.item.custom_states.add.error_empty.title"), JOptionPane.ERROR_MESSAGE);
				else if (statesList.stream().anyMatch(el -> el.state.getText().equals(state)))
					JOptionPane.showMessageDialog(mcreator, L10N.t("elementgui.item.custom_states.add.error_duplicate"),
							L10N.t("elementgui.item.custom_states.add.error_duplicate.title"),
							JOptionPane.ERROR_MESSAGE);
				else
					addStatesEntry(state);
			}
		});
		topbar.add(addState);

		JScrollPane left = new JScrollPane(PanelUtils.pullElementUp(propertyEntries));
		left.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 2),
				L10N.t("elementgui.item.custom_properties.title"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		JScrollPane right = new JScrollPane(PanelUtils.pullElementUp(stateEntries));
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

		if (!enabled) {
			propertiesList.stream().toList().forEach(e -> e.removeProperty(propertyEntries, propertiesList));
			statesList.stream().toList().forEach(e -> e.removeState(stateEntries, statesList));
		}
		addProperty.setEnabled(enabled);
		addState.setEnabled(enabled);
	}

	public void reloadDataLists() {
		propertiesList.forEach(JItemPropertiesListEntry::reloadDataLists);
		statesList.forEach(JItemStatesListEntry::reloadDataLists);
	}

	private JItemPropertiesListEntry addPropertiesEntry(int propertyId) {
		JItemPropertiesListEntry pe = new JItemPropertiesListEntry(mcreator, gui, propertyEntries, propertiesList,
				propertyId);

		pe.name.setValidator(new PropertyNameValidator(pe.name, "Property name",
				() -> propertiesList.stream().map(e -> e.name.getText()), () -> builtinPropertyNames,
				new RegistryNameValidator(pe.name, "Property name")));
		pe.name.enableRealtimeValidation();
		pe.name.getDocument().addDocumentListener(new DocumentListener() {
			@Override public void insertUpdate(DocumentEvent e) {
				propertyRenamed(pe);
			}

			@Override public void removeUpdate(DocumentEvent e) {
				propertyRenamed(pe);
			}

			@Override public void changedUpdate(DocumentEvent e) {
				propertyRenamed(pe);
			}
		});

		registerEntryUI(pe);
		return pe;
	}

	private JItemStatesListEntry addStatesEntry(String state) {
		JItemStatesListEntry se = new JItemStatesListEntry(mcreator, gui, stateEntries, statesList, state, e -> {
			if (getValidationResult(false).validateIsErrorFree())
				e.state.setText(StateEditorDialog.open(mcreator, e.state.getText(), getPropertiesMap(), "item"));
		});

		registerEntryUI(se);
		return se;
	}

	private void propertyRenamed(JItemPropertiesListEntry property) {
		getValidationResult(false).validateIsErrorFree(); // this highlights all the property names errors
		statesList.forEach(e -> {
			int builtinPropertiesFound = (int) Stream.of(e.state.getText().split(","))
					.filter(el -> builtinProperties.containsKey(el.split("=")[0])).count();
			e.propertyRenamed(property.nameString, property.name.getText(),
					propertiesList.indexOf(property) + builtinPropertiesFound);
		});
		property.nameString = property.name.getText();
	}

	private Map<String, PropertyData> getPropertiesMap() {
		Map<String, PropertyData> props = new LinkedHashMap<>(builtinProperties);
		propertiesList.forEach(e -> props.put(e.name.getText(), customNumber));
		return props;
	}

	public Map<String, Procedure> getProperties() {
		Map<String, Procedure> retVal = new LinkedHashMap<>();
		propertiesList.forEach(e -> e.addEntry(retVal));
		return retVal;
	}

	public void setProperties(Map<String, Procedure> properties) {
		properties.forEach((k, v) -> {
			propertyId.set(Math.max(propertiesList.size(), propertyId.get()) + 1);
			if (k.startsWith("property")) {
				try {
					propertyId.set(Math.max(propertyId.get(), Integer.parseInt(k.substring("property".length()))));
				} catch (NumberFormatException ignored) {
				}
			}
			addPropertiesEntry(propertyId.get()).setEntry(k, v);
		});
	}

	public Map<Map<String, Float>, Item.ModelEntry> getStates() {
		Map<Map<String, Float>, Item.ModelEntry> retVal = new LinkedHashMap<>();
		statesList.forEach(e -> e.addEntry(retVal));
		return retVal;
	}

	public void setStates(Map<Map<String, Float>, Item.ModelEntry> states) {
		states.forEach((k, v) -> {
			String stateString = k.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue())
					.collect(Collectors.joining(","));
			addStatesEntry(stateString).setEntry(k, v);
		});
	}

	public AggregatedValidationResult getValidationResult(boolean includeStates) {
		AggregatedValidationResult validationResult = new AggregatedValidationResult();
		propertiesList.forEach(validationResult::addValidationElement);
		if (includeStates)
			statesList.forEach(validationResult::addValidationElement);
		return validationResult;
	}
}