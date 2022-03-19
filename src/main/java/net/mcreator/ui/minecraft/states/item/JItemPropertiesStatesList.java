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

		builtinProperties.put("damaged", builtinLogic);
		builtinProperties.put("damage", builtinNumber);
		builtinProperties.put("lefthanded", builtinLogic);
		builtinProperties.put("cooldown", builtinNumber);

		setOpaque(false);
		propertyEntries.setOpaque(false);
		stateEntries.setOpaque(false);

		propertyEntries.addContainerListener(new ContainerAdapter() {
			@Override public void componentRemoved(ContainerEvent e) {
				if (propertiesList.size() > 0) {
					Map<String, PropertyData> propertiesMap = buildPropertiesMap();
					statesList.forEach(s -> s.state.setText(Arrays.stream(s.state.getText().split(","))
							.filter(el -> propertiesMap.containsKey(el.split("=")[0]))
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

		pe.name.setValidator(new PropertyNameValidator(pe.name, "Property name",
				() -> propertiesList.stream().map(e -> e.name.getText()), builtinPropertyNames,
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

	private JItemStatesListEntry addStatesEntry() {
		JItemStatesListEntry se = new JItemStatesListEntry(mcreator, gui, stateEntries, statesList);
		se.edit.addActionListener(e -> editState(se));
		registerEntryUI(se);
		return se;
	}

	private void propertyRenamed(JItemPropertiesListEntry entry) {
		getValidationResult(false).validateIsErrorFree(); // this highlights all the property names errors
		statesList.forEach(s -> {
			int indexBuiltin = (int) Arrays.stream(s.state.getText().split(","))
					.filter(el -> builtinPropertyNames.contains(el.split("=")[0])).count();
			int indexCustom = propertiesList.stream()
					.filter(e -> ("," + s.state.getText()).contains("," + e.nameString + "=")).toList().indexOf(entry);
			s.propertyRenamed(entry.nameString, entry.name.getText(), indexBuiltin + indexCustom);
		});
		entry.nameString = entry.name.getText();
	}

	private void editState(JItemStatesListEntry entry) {
		if (getValidationResult(false).validateIsErrorFree()) {
			String newState = StateEditorDialog.open(mcreator,
					entry != null ? entry.state.getText() : StateEditorDialog.TOKEN_NEW, buildPropertiesMap(),
					"item/custom_state");
			if (newState.equals("")) // all properties were unchecked
				JOptionPane.showMessageDialog(mcreator, L10N.t("elementgui.item.custom_states.add.error_empty"),
						L10N.t("elementgui.item.custom_states.add.error_empty.title"), JOptionPane.ERROR_MESSAGE);
			else if (statesList.stream().anyMatch(el -> el != entry && el.state.getText().equals(newState)))
				JOptionPane.showMessageDialog(mcreator, L10N.t("elementgui.item.custom_states.add.error_duplicate"),
						L10N.t("elementgui.item.custom_states.add.error_duplicate.title"), JOptionPane.ERROR_MESSAGE);
			else if (!StateEditorDialog.isToken(newState)) // valid state was returned
				(entry != null ? entry : addStatesEntry()).state.setText(newState);
		} else {
			Toolkit.getDefaultToolkit().beep();
		}
	}

	private Map<String, PropertyData> buildPropertiesMap() {
		Map<String, PropertyData> props = new LinkedHashMap<>(builtinProperties);
		propertiesList.forEach(e -> props.put(e.name.getText(), customNumber));
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
		statesList.forEach(e -> retVal.put(e.state.getText(), e.getEntry()));
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