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
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.DataListLoader;
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
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.RegistryNameValidator;
import net.mcreator.ui.validation.validators.UniqueNameValidator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class JItemPropertiesStatesList extends JEntriesList {

	private final List<JItemPropertiesListEntry> propertiesList;
	private final List<JItemStatesListEntry> statesList = new ArrayList<>();
	private final AtomicInteger propertyId = new AtomicInteger(0);

	private final List<String> builtinPropertyNames;
	private final Map<String, PropertyData<?, ?>> builtinProperties = new LinkedHashMap<>();

	private final JPanel propertyEntries = new JPanel() {
		@Override public void setEnabled(boolean enabled) {
			super.setEnabled(enabled);
			propertiesList.forEach(e -> e.setEnabled(enabled));
		}
	};
	private final JPanel stateEntries = new JPanel() {
		@Override public void setEnabled(boolean enabled) {
			super.setEnabled(enabled);
			statesList.forEach(e -> e.setEnabled(enabled));
		}
	};

	private final JButton addProperty = new JButton(UIRES.get("16px.add.gif"));
	private final JButton addState = new JButton(UIRES.get("16px.add.gif"));

	public JItemPropertiesStatesList(MCreator mcreator, IHelpContext gui) {
		super(mcreator, new BorderLayout(), gui);

		Map<String, DataListEntry> properties = DataListLoader.loadDataMap("itemproperties");
		builtinPropertyNames = List.copyOf(properties.keySet());
		properties.values().stream().filter(e -> e.isSupportedInWorkspace(mcreator.getWorkspace())).forEach(e -> {
			if ("Number".equals(e.getType())) {
				builtinProperties.put(e.getName(), new PropertyData.Float<>(e.getName(), Float.class, 0F, 1F));
			} else if ("Logic".equals(e.getType())) {
				builtinProperties.put(e.getName(), new PropertyData.Float<>(e.getName(), Boolean.class, 0F, 1F) {
					@Override public java.lang.Boolean toUIValue(Object value) {
						return parseObj(value.toString()) == 1F;
					}

					@Override public java.lang.Float fromUIValue(Object value) {
						return (boolean) value ? 1F : 0F;
					}
				});
			}
		});

		setOpaque(false);
		propertyEntries.setLayout(new GridLayout(0, 1, 5, 5));
		propertyEntries.setOpaque(false);
		stateEntries.setLayout(new BoxLayout(stateEntries, BoxLayout.Y_AXIS));
		stateEntries.setOpaque(false);

		propertiesList = new ArrayList<>() {
			@Override public boolean remove(Object o) {
				if (o instanceof JItemPropertiesListEntry entry) {
					stateEntries.setVisible(false);
					PropertyData<?, ?> data = buildPropertiesMap().get(entry.getNameField().getPropertyName());
					statesList.forEach(s -> {
						LinkedHashMap<PropertyData<?, ?>, Object> stateMap = s.getStateLabel().getStateMap();
						stateMap.remove(data);
						s.getStateLabel().setStateMap(stateMap);
					});
					Set<String> duplicateFilter = new HashSet<>();
					statesList.stream().toList().forEach(s -> {
						if (s.getStateLabel().getState() == null || s.getStateLabel().getState().equals("")
								|| !duplicateFilter.add(s.getStateLabel().getState()))
							s.removeState(stateEntries, statesList);
					});
					stateEntries.setVisible(true);
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

		JComponent merger = PanelUtils.gridElements(1, 0, left, right);
		merger.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

		add("North", PanelUtils.centerInPanel(
				HelpUtils.wrapWithHelpButton(gui.withEntry("common/custom_states"), topbar, SwingConstants.LEFT)));
		add("Center", merger);
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		addProperty.setEnabled(enabled);
		addState.setEnabled(enabled);

		propertyEntries.setEnabled(enabled);
		stateEntries.setEnabled(enabled);
	}

	public void reloadDataLists() {
		propertiesList.forEach(JItemPropertiesListEntry::reloadDataLists);
		statesList.forEach(JItemStatesListEntry::reloadDataLists);
	}

	private JItemPropertiesListEntry addPropertiesEntry(int propertyId) {
		JItemPropertiesListEntry pe = new JItemPropertiesListEntry(mcreator, gui, propertyEntries, propertiesList,
				propertyId);

		VTextField name = pe.getNameField().getTextField();
		UniqueNameValidator validator = new UniqueNameValidator(name,
				L10N.t("elementgui.item.custom_property.name_validator"),
				() -> propertiesList.stream().map(e -> e.getNameField().getPropertyName()), builtinPropertyNames,
				new RegistryNameValidator(name, L10N.t("elementgui.item.custom_property.name_validator")));
		name.setValidator(validator);
		name.enableRealtimeValidation();
		name.addKeyListener(new KeyAdapter() {
			@Override public void keyPressed(KeyEvent e) {
				if (!name.isEnabled())
					return;

				if (e.getKeyCode() == KeyEvent.VK_ENTER
						&& pe.getValidationStatus() == Validator.ValidationResult.PASSED) {
					String newName = name.getText();
					statesList.forEach(s -> s.getStateLabel().rename(pe.getNameField().getCachedName(), newName));
					pe.getNameField().finishRenaming();
					pe.getNameField().renameTo(newName);
				} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					pe.getNameField().finishRenaming();
				}
			}
		});

		registerEntryUI(pe);
		return pe;
	}

	private JItemStatesListEntry addStatesEntry() {
		JItemStatesListEntry se = new JItemStatesListEntry(mcreator, gui, stateEntries, statesList,
				buildPropertiesMap().values().stream()::toList, this::editState);
		registerEntryUI(se);
		return se;
	}

	private void editState(JItemStatesListEntry entry) {
		if (getValidationResult(false).validateIsErrorFree()) {
			LinkedHashMap<PropertyData<?, ?>, Object> stateMap = new LinkedHashMap<>();
			if (entry != null)
				stateMap.putAll(entry.getStateLabel().getStateMap());
			if (JOptionPane.OK_OPTION != StateEditorDialog.open(mcreator, buildPropertiesMap().values(), stateMap,
					entry == null, "item/custom_state"))
				return;

			if (stateMap.isEmpty()) { // all properties were unchecked - not acceptable by items
				JOptionPane.showMessageDialog(mcreator, L10N.t("elementgui.item.custom_states.error_empty"),
						L10N.t("elementgui.item.custom_states.error_empty.title"), JOptionPane.ERROR_MESSAGE);
			} else if (statesList.stream()
					.anyMatch(s -> s != entry && s.getStateLabel().getStateMap().equals(stateMap))) {
				JOptionPane.showMessageDialog(mcreator, L10N.t("elementgui.item.custom_states.error_duplicate"),
						L10N.t("elementgui.item.custom_states.error_duplicate.title"), JOptionPane.ERROR_MESSAGE);
			} else {
				(entry != null ? entry : addStatesEntry()).getStateLabel().setStateMap(stateMap);
			}
		} else {
			Toolkit.getDefaultToolkit().beep();
		}
	}

	private Map<String, PropertyData<?, ?>> buildPropertiesMap() {
		Map<String, PropertyData<?, ?>> props = new LinkedHashMap<>(builtinProperties);
		propertiesList.forEach(e -> props.put(e.getNameField().getPropertyName(),
				new PropertyData.Float<>(e.getNameField().getPropertyName(), Float.class, 0F, 1000001F)));
		return props;
	}

	public LinkedHashMap<String, Procedure> getProperties() {
		LinkedHashMap<String, Procedure> retVal = new LinkedHashMap<>();
		propertiesList.forEach(e -> retVal.put(e.getNameField().getPropertyName(), e.getEntry()));
		return retVal;
	}

	public void setProperties(LinkedHashMap<String, Procedure> properties) {
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

	public LinkedHashMap<String, Item.ModelEntry> getStates() {
		LinkedHashMap<String, Item.ModelEntry> retVal = new LinkedHashMap<>();
		statesList.forEach(e -> retVal.put(e.getStateLabel().getState(), e.getEntry()));
		return retVal;
	}

	public void setStates(LinkedHashMap<String, Item.ModelEntry> states) {
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