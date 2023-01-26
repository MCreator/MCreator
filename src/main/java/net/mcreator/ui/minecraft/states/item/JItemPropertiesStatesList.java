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
import net.mcreator.ui.minecraft.states.JStateLabel;
import net.mcreator.ui.minecraft.states.PropertyData;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.IValidable;
import net.mcreator.ui.validation.validators.UniqueNameValidator;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class JItemPropertiesStatesList extends JEntriesList {

	private final List<JItemPropertiesListEntry> propertiesList = new ArrayList<>();
	private final List<JItemStatesListEntry> statesList = new ArrayList<>();
	private final AtomicInteger propertyId = new AtomicInteger(0);

	private final List<String> builtinPropertyNames;
	private final Map<String, PropertyData<?>> builtinProperties = new LinkedHashMap<>();

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

	private final JButton addProperty = new JButton(UIRES.get("16px.add.gif")) {
		@Override public String getName() {
			return "TechnicalButton";
		}
	};
	private final JButton addState = new JButton(UIRES.get("16px.add.gif")) {
		@Override public String getName() {
			return "TechnicalButton";
		}
	};

	public JItemPropertiesStatesList(MCreator mcreator, IHelpContext gui) {
		super(mcreator, new BorderLayout(), gui);

		Map<String, DataListEntry> properties = DataListLoader.loadDataMap("itemproperties");
		builtinPropertyNames = List.copyOf(properties.keySet());

		PropertyData.Logic logic = new PropertyData.Logic(""); // needed for hardcoded logic properties
		properties.values().stream().filter(e -> e.isSupportedInWorkspace(mcreator.getWorkspace())).forEach(e -> {
			if (e.getOther() instanceof Map<?, ?> other) { // only accept properties that have value bounds defined
				float min = Float.parseFloat((String) other.get("min"));
				float max = Float.parseFloat((String) other.get("max"));

				if ("Number".equals(e.getType())) {
					builtinProperties.put(e.getName(), new PropertyData.FloatNumber(e.getName(), min, max));
				} else if ("Logic".equals(e.getType())) {
					builtinProperties.put(e.getName(), new PropertyData.FloatNumber(e.getName(), min, max) {
						@Override public JComponent getComponent(MCreator mcreator, @Nullable Object value) {
							return logic.getComponent(mcreator, value != null && (Float) value == max);
						}

						@Override public Float getValue(JComponent component) {
							return logic.getValue(component) ? max : min;
						}
					});
				}
			}
		});

		setOpaque(false);
		propertyEntries.setLayout(new GridLayout(0, 1, 5, 5));
		propertyEntries.setOpaque(false);
		stateEntries.setLayout(new BoxLayout(stateEntries, BoxLayout.Y_AXIS));
		stateEntries.setOpaque(false);

		propertyEntries.addContainerListener(new ContainerAdapter() {
			@Override public void componentRemoved(ContainerEvent e) {
				if (e.getChild() instanceof Container c && c.getComponentCount() > 0
						&& c.getComponents()[0] instanceof JItemPropertiesListEntry entry) {
					PropertyData.FloatNumber data = entry.toPropertyData();
					Set<LinkedHashMap<PropertyData<?>, Object>> duplicateFilter = new HashSet<>();
					statesList.stream().toList().forEach(s -> {
						LinkedHashMap<PropertyData<?>, Object> stateMap = s.getStateLabel().getStateMap();
						stateMap.remove(data);
						if (stateMap.isEmpty() || !duplicateFilter.add(stateMap)) {
							statesList.remove(s);
							stateEntries.remove(s.getParent());
						} else {
							s.getStateLabel().setStateMap(stateMap);
						}
					});
					stateEntries.revalidate();
					stateEntries.repaint();
				}
			}
		});

		JPanel topbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topbar.setOpaque(false);

		addProperty.setText(L10N.t("elementgui.item.custom_properties.add"));
		addProperty.addActionListener(e -> addPropertiesEntry());
		topbar.add(addProperty);

		addState.setText(L10N.t("elementgui.item.custom_states.add"));
		addState.addActionListener(e -> editState(null)); // passing null here means a new entry should be created
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

	private JItemPropertiesListEntry addPropertiesEntry() {
		JItemPropertiesListEntry pe = new JItemPropertiesListEntry(mcreator, gui, propertyEntries, propertiesList,
				propertyId.incrementAndGet(), this::nameValidator, nameField -> statesList.forEach(
				s -> s.getStateLabel().rename(nameField.getCachedName(), nameField.getPropertyName())));
		registerEntryUI(pe);
		return pe;
	}

	private UniqueNameValidator nameValidator(Supplier<String> nameGetter) {
		return new UniqueNameValidator(L10N.t("elementgui.item.custom_property.validator"), nameGetter,
				() -> propertiesList.stream().map(e -> e.getNameField().getPropertyName()), builtinPropertyNames, null);
	}

	private JItemStatesListEntry addStatesEntry() {
		JItemStatesListEntry se = new JItemStatesListEntry(mcreator, gui, stateEntries, statesList,
				this::buildPropertiesList, this::editState);
		registerEntryUI(se);
		return se;
	}

	private void editState(JItemStatesListEntry entry) {
		if (new AggregatedValidationResult(propertiesList.stream().map(JItemPropertiesListEntry::getNameField)
				.toArray(IValidable[]::new)).validateIsErrorFree()) {
			LinkedHashMap<PropertyData<?>, Object> stateMap = new LinkedHashMap<>();
			if (entry != null) // copy state definition map if in editing mode
				stateMap.putAll(entry.getStateLabel().getStateMap());
			if (!StateEditorDialog.open(mcreator, buildPropertiesList(), stateMap, entry == null, "item/custom_state"))
				return;

			if (stateMap.isEmpty()) { // all properties were unchecked - not acceptable by items
				JOptionPane.showMessageDialog(mcreator, L10N.t("elementgui.item.custom_states.error_empty"),
						L10N.t("elementgui.item.custom_states.error_empty.title"), JOptionPane.ERROR_MESSAGE);
			} else if (statesList.stream() // check if any other entry defines the same state
					.anyMatch(s -> s != entry && s.getStateLabel().getStateMap().equals(stateMap))) {
				JOptionPane.showMessageDialog(mcreator, L10N.t("elementgui.item.custom_states.error_duplicate"),
						L10N.t("elementgui.item.custom_states.error_duplicate.title"), JOptionPane.ERROR_MESSAGE);
			} else { // all good, add new entry or update existing one
				(entry != null ? entry : addStatesEntry()).getStateLabel().setStateMap(stateMap);
			}
		} else {
			Toolkit.getDefaultToolkit().beep();
		}
	}

	private List<PropertyData<?>> buildPropertiesList() {
		List<PropertyData<?>> props = new ArrayList<>(builtinProperties.values());
		propertiesList.stream().map(JItemPropertiesListEntry::toPropertyData).forEach(props::add);
		return props;
	}

	public LinkedHashMap<String, Procedure> getProperties() {
		LinkedHashMap<String, Procedure> retVal = new LinkedHashMap<>();
		propertiesList.forEach(e -> retVal.put(e.getNameField().getPropertyName(), e.getEntry()));
		return retVal;
	}

	public void setProperties(LinkedHashMap<String, Procedure> properties) {
		properties.forEach(addPropertiesEntry()::setEntry);
	}

	public LinkedHashMap<String, Item.ModelEntry> getStates() {
		LinkedHashMap<String, Item.ModelEntry> retVal = new LinkedHashMap<>();
		statesList.forEach(e -> retVal.put(e.getStateLabel().getState(), e.getEntry()));
		return retVal;
	}

	public void setStates(LinkedHashMap<String, Item.ModelEntry> states) {
		Set<LinkedHashMap<PropertyData<?>, Object>> duplicateFilter = new HashSet<>();
		states.forEach((state, model) -> {
			LinkedHashMap<PropertyData<?>, Object> stateMap = JStateLabel.passStateToMap(state, buildPropertiesList());
			if (!stateMap.isEmpty() && duplicateFilter.add(stateMap))
				addStatesEntry().setEntry(state, model);
		});
	}

	public AggregatedValidationResult getValidationResult() {
		AggregatedValidationResult validationResult = new AggregatedValidationResult();
		propertiesList.forEach(validationResult::addValidationElement);
		statesList.forEach(validationResult::addValidationElement);
		return validationResult;
	}
}