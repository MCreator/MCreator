/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.ui.minecraft.states.block;

import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.DataListLoader;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.TechnicalButton;
import net.mcreator.ui.component.entries.JEntriesList;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.AddBlockPropertyDialog;
import net.mcreator.ui.dialogs.DataListSelectorDialog;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.minecraft.states.PropertyData;
import net.mcreator.ui.minecraft.states.PropertyDataWithValue;
import net.mcreator.ui.validation.AggregatedValidationResult;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class JBlockPropertiesStatesList extends JEntriesList {

	private final Supplier<Collection<String>> nonUserProvidedProperties;

	private final List<JBlockPropertiesListEntry> propertiesList = new ArrayList<>();

	private final JPanel propertyEntries = new JPanel();

	private final TechnicalButton addProperty = new TechnicalButton(UIRES.get("16px.add"));
	private final TechnicalButton addExisting = new TechnicalButton(UIRES.get("16px.add"));

	public JBlockPropertiesStatesList(MCreator mcreator, IHelpContext gui,
			Supplier<Collection<String>> nonUserProvidedProperties) {
		super(mcreator, new BorderLayout(0, 10), gui);
		this.nonUserProvidedProperties = nonUserProvidedProperties;

		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));

		propertyEntries.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		propertyEntries.setLayout(new GridLayout(0, 1, 5, 5));
		propertyEntries.setOpaque(false);

		addProperty.setText(L10N.t("elementgui.block.custom_properties.add"));
		addProperty.addActionListener(e -> createPropertiesEntry());
		addExisting.setText(L10N.t("elementgui.block.custom_properties.add_existing"));
		addExisting.addActionListener(e -> addPropertyFromDataList());

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

		JPanel mainContent = new JPanel(new BorderLayout());
		mainContent.setOpaque(false);
		mainContent.add("North", PanelUtils.join(FlowLayout.LEFT, 0, 5, addProperty, new JEmptyBox(5, 5), addExisting,
				new JEmptyBox(5, 5), HelpUtils.helpButton(gui.withEntry("block/block_states"))));
		mainContent.add("Center", scrollProperties);

		JPanel basePane = new JPanel(new GridLayout());
		basePane.setOpaque(false);
		basePane.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.block.custom_properties_states"), 0, 0, basePane.getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));
		basePane.add(mainContent);
		add("Center", basePane);
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		addProperty.setEnabled(enabled);
		addExisting.setEnabled(enabled);

		propertiesList.forEach(e -> e.setEnabled(enabled));

		propertyEntries.setEnabled(enabled);
	}

	String propertyRegistryName(PropertyData<?> data) {
		DataListEntry dle = DataListLoader.loadDataMap("blockstateproperties").get(data.getName());
		if (dle != null && dle.getOther() instanceof Map<?, ?> other && other.get("registry_name") != null)
			return (String) other.get("registry_name");
		return data.getName().replace("CUSTOM:", "");
	}

	private void createPropertiesEntry() {
		PropertyDataWithValue<?> newEntry = AddBlockPropertyDialog.showDialog(mcreator,
				() -> propertiesList.stream().map(e -> propertyRegistryName(e.getPropertyData()))
						.filter(Objects::nonNull), nonUserProvidedProperties);
		if (newEntry != null) {
			addPropertiesEntry(newEntry);
		}
	}

	private void addPropertyFromDataList() {
		DataListEntry property = DataListSelectorDialog.openSelectorDialog(mcreator,
				w -> DataListLoader.loadDataList("blockstateproperties"),
				L10N.t("elementgui.block.custom_properties.add_existing"),
				L10N.t("elementgui.block.custom_properties.add_existing.message"));
		if (property == null || !(property.getOther() instanceof Map<?, ?> other) || other.get("registry_name") == null)
			return;

		String registryName = (String) other.get("registry_name");
		for (JBlockPropertiesListEntry p : propertiesList) {
			if (registryName.equals(propertyRegistryName(p.getPropertyData()))) {
				JOptionPane.showMessageDialog(mcreator,
						L10N.t("elementgui.block.custom_properties.add.error_duplicate"),
						L10N.t("elementgui.block.custom_properties.add.error_duplicate.title"),
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		PropertyData<?> newProp;
		switch (property.getType()) {
		case "Logic" -> newProp = new PropertyData.LogicType(property.getName());
		case "Integer" -> {
			int min = Integer.parseInt((String) other.get("min"));
			int max = Integer.parseInt((String) other.get("max"));
			newProp = new PropertyData.IntegerType(property.getName(), min, max);
		}
		case "Enum" -> {
			String[] data = ((List<?>) other.get("values")).stream().map(Object::toString).toArray(String[]::new);
			newProp = new PropertyData.StringType(property.getName(), data);
		}
		case null, default -> {
			return;
		}
		}
		addPropertiesEntry(new PropertyDataWithValue<>(newProp, null));
	}

	private void addPropertiesEntry(@Nonnull PropertyDataWithValue<?> data) {
		JBlockPropertiesListEntry pe = new JBlockPropertiesListEntry(this, gui, propertyEntries, propertiesList);
		pe.setEntry(data);
		registerEntryUI(pe);
	}

	void removeProperty(JBlockPropertiesListEntry entry) {
		propertiesList.remove(entry);
		propertyEntries.remove(entry);
		propertyEntries.revalidate();
		propertyEntries.repaint();
	}

	public List<PropertyDataWithValue<?>> getProperties() {
		return propertiesList.stream().map(JBlockPropertiesListEntry::getEntry).collect(Collectors.toList());
	}

	public void setProperties(List<PropertyDataWithValue<?>> properties) {
		properties.forEach(this::addPropertiesEntry);
	}

	public AggregatedValidationResult getValidationResult() {
		AggregatedValidationResult validationResult = new AggregatedValidationResult.PASS();
		for (JBlockPropertiesListEntry entry : propertiesList) {
			String regName = propertyRegistryName(entry.getPropertyData());
			if (regName == null || nonUserProvidedProperties.get().stream().anyMatch(regName::equalsIgnoreCase)) {
				entry.setBorder(
						BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(204, 108, 108), 1),
								BorderFactory.createEmptyBorder(4, 4, 4, 4)));
				validationResult = new AggregatedValidationResult.FAIL(
						L10N.t("elementgui.block.custom_properties.error_overrides_provided"));
			} else {
				entry.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			}
		}
		return validationResult;
	}

}