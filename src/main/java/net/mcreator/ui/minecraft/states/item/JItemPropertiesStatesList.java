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
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.StateEditorDialog;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.minecraft.JEntriesList;
import net.mcreator.ui.minecraft.states.BuiltInPropertyData;
import net.mcreator.ui.minecraft.states.IPropertyData;
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
	private final Map<String, BuiltInPropertyData<?>> builtinProperties = new LinkedHashMap<>();

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
		super(mcreator, new GridLayout(), gui);
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));

		Map<String, DataListEntry> properties = DataListLoader.loadDataMap("itemproperties");
		builtinPropertyNames = List.copyOf(properties.keySet());

		PropertyData.Logic logic = new PropertyData.Logic(""); // needed for hardcoded logic properties
		for (DataListEntry entry : properties.values()) {
			PropertyData.FloatNumber builtin;
			if ("Number".equals(entry.getType()) && entry.getOther() instanceof Map<?, ?> other) {
				float min = Float.parseFloat((String) other.get("min"));
				float max = Float.parseFloat((String) other.get("max"));
				builtin = new PropertyData.FloatNumber(entry.getName(), min, max);
			} else if ("Logic".equals(entry.getType())) {
				builtin = new PropertyData.FloatNumber(entry.getName(), 0, 1) {
					@Override public JComponent getComponent(MCreator mcreator, @Nullable Object value) {
						return logic.getComponent(mcreator, value != null && (Float) value == 1);
					}

					@Override public Float getValue(JComponent component) {
						return logic.getValue(component) ? 1 : 0f;
					}
				};
			} else {
				continue;
			}
			builtinProperties.put(entry.getName(), new BuiltInPropertyData<>(builtin));
		}

		propertyEntries.setLayout(new GridLayout(0, 1, 5, 5));
		propertyEntries.setOpaque(false);
		stateEntries.setLayout(new BoxLayout(stateEntries, BoxLayout.Y_AXIS));
		stateEntries.setOpaque(false);

		propertyEntries.addContainerListener(new ContainerAdapter() {
			@Override public void componentRemoved(ContainerEvent e) {
				if (e.getChild() instanceof Container c && c.getComponentCount() > 0
						&& c.getComponents()[0] instanceof JItemPropertiesListEntry entry) {
					PropertyData.FloatNumber data = entry.toPropertyData();
					Set<LinkedHashMap<IPropertyData<?>, Object>> duplicateFilter = new HashSet<>();
					statesList.stream().toList().forEach(s -> {
						LinkedHashMap<IPropertyData<?>, Object> stateMap = s.getStateLabel().getStateMap();
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

		addProperty.setText(L10N.t("elementgui.item.custom_properties.add"));
		addState.setText(L10N.t("elementgui.item.custom_states.add"));

		addProperty.addActionListener(e -> addPropertiesEntry());
		addState.addActionListener(e -> editState(null)); // passing null here means a new entry should be created

		JScrollPane scrollProperties = new JScrollPane(PanelUtils.pullElementUp(propertyEntries)) {
			@Override protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setColor((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
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
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.item.custom_properties.title"), 0, 0, scrollProperties.getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));
		JPanel left = new JPanel(new BorderLayout());
		left.setOpaque(false);
		left.add("North", PanelUtils.join(FlowLayout.LEFT, 0, 5, addProperty, new JEmptyBox(5, 5),
				HelpUtils.helpButton(gui.withEntry("item/custom_states"))));
		left.add("Center", scrollProperties);
		add(left);

		JScrollPane scrollStates = new JScrollPane(PanelUtils.pullElementUp(stateEntries)) {
			@Override protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setColor((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
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
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.item.custom_states.title"), 0, 0, scrollStates.getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));
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

		propertyEntries.setEnabled(enabled);
		stateEntries.setEnabled(enabled);
	}

	public void reloadDataLists() {
		propertiesList.forEach(JItemPropertiesListEntry::reloadDataLists);
		statesList.forEach(JItemStatesListEntry::reloadDataLists);
	}

	private JItemPropertiesListEntry addPropertiesEntry() {
		JItemPropertiesListEntry pe = new JItemPropertiesListEntry(mcreator, gui, propertyEntries, propertiesList,
				propertyId.incrementAndGet(), this::nameValidator, entry -> statesList.forEach(s -> s.getStateLabel()
				.rename(entry.getNameField().getCachedName(), entry.getNameField().getPropertyName())));
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
			LinkedHashMap<IPropertyData<?>, Object> stateMap = StateEditorDialog.open(mcreator, buildPropertiesList(),
					entry == null ? null : entry.getStateLabel().getStateMap());
			if (stateMap == null)
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

	private List<IPropertyData<?>> buildPropertiesList() {
		List<IPropertyData<?>> props = new ArrayList<>();
		propertiesList.stream().map(JItemPropertiesListEntry::toPropertyData).forEach(props::add);
		props.addAll(builtinProperties.values());
		return props;
	}

	public LinkedHashMap<String, Procedure> getProperties() {
		LinkedHashMap<String, Procedure> retVal = new LinkedHashMap<>();
		propertiesList.forEach(e -> retVal.put(e.getNameField().getPropertyName(), e.getEntry()));
		return retVal;
	}

	public void setProperties(LinkedHashMap<String, Procedure> properties) {
		properties.forEach((name, value) -> addPropertiesEntry().setEntry(name, value));
	}

	public LinkedHashMap<String, Item.ModelEntry> getStates() {
		LinkedHashMap<String, Item.ModelEntry> retVal = new LinkedHashMap<>();
		statesList.forEach(e -> retVal.put(e.getStateLabel().getState(), e.getEntry()));
		return retVal;
	}

	public void setStates(LinkedHashMap<String, Item.ModelEntry> states) {
		Set<LinkedHashMap<IPropertyData<?>, Object>> duplicateFilter = new HashSet<>();
		states.forEach((state, model) -> {
			LinkedHashMap<IPropertyData<?>, Object> stateMap = IPropertyData.passStateToMap(state,
					buildPropertiesList());
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