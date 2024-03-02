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
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.TechnicalButton;
import net.mcreator.ui.component.entries.JEntriesList;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.minecraft.states.JStateLabel;
import net.mcreator.ui.minecraft.states.PropertyData;
import net.mcreator.ui.minecraft.states.StateMap;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.optionpane.OptionPaneValidatior;
import net.mcreator.ui.validation.optionpane.VOptionPane;
import net.mcreator.ui.validation.validators.RegistryNameValidator;
import net.mcreator.ui.validation.validators.UniqueNameValidator;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

public class JItemPropertiesStatesList extends JEntriesList {

	private final List<JItemPropertiesListEntry> propertiesList = new ArrayList<>();
	private final List<JItemStatesListEntry> statesList = new ArrayList<>();

	private final Map<String, PropertyData<?>> builtinProperties = new LinkedHashMap<>();

	private final JPanel propertyEntries = new JPanel(), stateEntries = new JPanel();

	private final TechnicalButton addProperty = new TechnicalButton(UIRES.get("16px.add"));
	private final TechnicalButton addState = new TechnicalButton(UIRES.get("16px.add"));

	public JItemPropertiesStatesList(MCreator mcreator, IHelpContext gui) {
		super(mcreator, new GridLayout(), gui);
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));

		Map<String, DataListEntry> properties = DataListLoader.loadDataMap("itemproperties");
		List<String> builtinPropertyNames = List.copyOf(properties.keySet());

		for (DataListEntry entry : properties.values()) {
			if ("Number".equals(entry.getType()) && entry.getOther() instanceof Map<?, ?> other) {
				double min = Double.parseDouble((String) other.get("min"));
				double max = Double.parseDouble((String) other.get("max"));
				builtinProperties.put(entry.getName(), new PropertyData.NumberType(entry.getName(), min, max));
			} else if ("Logic".equals(entry.getType())) {
				builtinProperties.put(entry.getName(), new PropertyData.LogicType(entry.getName()));
			}
		}

		propertyEntries.setLayout(new GridLayout(0, 1, 5, 5));
		propertyEntries.setOpaque(false);
		stateEntries.setLayout(new GridLayout(0, 1, 5, 5));
		stateEntries.setOpaque(false);

		addProperty.setText(L10N.t("elementgui.item.custom_properties.add"));
		addProperty.addActionListener(e -> {
			String name = VOptionPane.showInputDialog(mcreator, L10N.t("elementgui.item.custom_properties.add.message"),
					L10N.t("elementgui.item.custom_properties.add.input"), null, new OptionPaneValidatior() {
						@Override public ValidationResult validate(JComponent component) {
							return new UniqueNameValidator(L10N.t("elementgui.item.custom_properties.add.input"),
									((VTextField) component)::getText,
									() -> propertiesList.stream().map(JItemPropertiesListEntry::getPropertyName),
									builtinPropertyNames, new RegistryNameValidator((VTextField) component,
									L10N.t("elementgui.item.custom_properties.add.input"))).setIsPresentOnList(false)
									.validate();
						}
					});
			if (name != null)
				addPropertiesEntry(name);
		});
		addState.setText(L10N.t("elementgui.item.custom_states.add"));
		addState.addActionListener(e -> addStatesEntry(true));

		JScrollPane scrollProperties = new JScrollPane(PanelUtils.pullElementUp(propertyEntries)) {
			@Override protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setColor(Theme.current().getAltBackgroundColor());
				g2d.setComposite(AlphaComposite.SrcOver.derive(0.45f));
				g2d.fillRect(0, 0, getWidth(), getHeight());
				g2d.dispose();
				super.paintComponent(g);
			}
		};
		scrollProperties.getVerticalScrollBar().setUnitIncrement(15);
		scrollProperties.setOpaque(false);
		scrollProperties.getViewport().setOpaque(false);
		scrollProperties.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.item.custom_properties.title"), 0, 0, scrollProperties.getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));
		JPanel left = new JPanel(new BorderLayout());
		left.setOpaque(false);
		left.add("North", PanelUtils.join(FlowLayout.LEFT, 0, 5, addProperty, new JEmptyBox(5, 5),
				HelpUtils.helpButton(gui.withEntry("item/custom_states"))));
		left.add("Center", scrollProperties);
		add(left);

		JScrollPane scrollStates = new JScrollPane(PanelUtils.pullElementUp(stateEntries)) {
			@Override protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setColor(Theme.current().getAltBackgroundColor());
				g2d.setComposite(AlphaComposite.SrcOver.derive(0.45f));
				g2d.fillRect(0, 0, getWidth(), getHeight());
				g2d.dispose();
				super.paintComponent(g);
			}
		};
		scrollStates.getVerticalScrollBar().setUnitIncrement(15);
		scrollStates.setOpaque(false);
		scrollStates.getViewport().setOpaque(false);
		scrollStates.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.item.custom_states.title"), 0, 0, scrollStates.getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));
		JPanel right = new JPanel(new BorderLayout());
		right.setOpaque(false);
		right.add("North", PanelUtils.join(FlowLayout.LEFT, 0, 5, addState, new JEmptyBox(5, 5),
				HelpUtils.helpButton(gui.withEntry("item/custom_states"))));
		right.add("Center", scrollStates);
		add(right);
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		addProperty.setEnabled(enabled);
		addState.setEnabled(enabled);

		propertiesList.forEach(e -> e.setEnabled(enabled));
		statesList.forEach(e -> e.setEnabled(enabled));

		propertyEntries.setEnabled(enabled);
		stateEntries.setEnabled(enabled);
	}

	@Override public void reloadDataLists() {
		propertiesList.forEach(JItemPropertiesListEntry::reloadDataLists);
		statesList.forEach(JItemStatesListEntry::reloadDataLists);
	}

	private JItemPropertiesListEntry addPropertiesEntry(String name) {
		JItemPropertiesListEntry pe = new JItemPropertiesListEntry(this, gui, propertyEntries, propertiesList, name);
		registerEntryUI(pe);
		return pe;
	}

	private List<PropertyData<?>> getPropertiesList() {
		List<PropertyData<?>> props = new ArrayList<>();
		for (JItemPropertiesListEntry entry : propertiesList) {
			if (entry.getValidationStatus().getValidationResultType() != Validator.ValidationResultType.PASSED)
				return null; // indicates that some property names are not defined correctly

			props.add(entry.toPropertyData());
		}
		props.addAll(builtinProperties.values());
		return props;
	}

	private JItemStatesListEntry addStatesEntry(boolean initState) {
		JStateLabel stateLabel = new JStateLabel(mcreator, this::getPropertiesList,
				() -> statesList.stream().map(JItemStatesListEntry::getStateLabel)).setNumberMatchType(
				JStateLabel.NumberMatchType.GREATER_OR_EQUAL);
		if (initState && !stateLabel.editState())
			return null;

		JItemStatesListEntry se = new JItemStatesListEntry(mcreator, gui, stateEntries, statesList, stateLabel);
		registerEntryUI(se);
		return se;
	}

	void removeProperty(JItemPropertiesListEntry entry) {
		propertiesList.remove(entry);
		propertyEntries.remove(entry);
		propertyEntries.revalidate();
		propertyEntries.repaint();

		PropertyData.NumberType data = entry.toPropertyData();

		Set<StateMap> duplicateFilter = new HashSet<>();
		Iterator<JItemStatesListEntry> iterator = statesList.iterator();
		while (iterator.hasNext()) {
			JItemStatesListEntry s = iterator.next();
			StateMap stateMap = s.getStateLabel().getStateMap();
			stateMap.remove(data);
			if (stateMap.isEmpty() || !duplicateFilter.add(stateMap)) { // if state map is empty or duplicate is found
				iterator.remove(); // remove the JItemStatesListEntry
				stateEntries.remove(s);
			} else {
				s.getStateLabel().setStateMap(stateMap);
			}
		}

		stateEntries.revalidate();
		stateEntries.repaint();
	}

	public Map<String, Procedure> getProperties() {
		Map<String, Procedure> retVal = new LinkedHashMap<>();
		propertiesList.forEach(e -> retVal.put(e.getPropertyName(), e.getSelectedProcedure()));
		return retVal;
	}

	public void setProperties(Map<String, Procedure> properties) {
		properties.forEach((name, value) -> addPropertiesEntry(name).setSelectedProcedure(value));
	}

	public List<Item.StateEntry> getStates() {
		List<Item.StateEntry> retVal = new ArrayList<>();
		statesList.forEach(e -> retVal.add(e.getEntry()));
		return retVal;
	}

	public void setStates(List<Item.StateEntry> states) {
		states.forEach(state -> Objects.requireNonNull(addStatesEntry(false)).setEntry(state));
	}

	public AggregatedValidationResult getValidationResult() {
		AggregatedValidationResult validationResult = new AggregatedValidationResult();
		propertiesList.forEach(validationResult::addValidationElement);
		statesList.forEach(validationResult::addValidationElement);
		return validationResult;
	}

}