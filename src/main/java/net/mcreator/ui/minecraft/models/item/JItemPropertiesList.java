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

package net.mcreator.ui.minecraft.models.item;

import net.mcreator.element.parts.Procedure;
import net.mcreator.element.types.Item;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.IValidable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class JItemPropertiesList extends JPanel {

	private final List<JItemPropertiesListEntry> propertiesList = new ArrayList<>();
	private final List<JItemModelsListEntry> modelsList = new ArrayList<>();
	private final AtomicInteger propertyId = new AtomicInteger(0);

	private final MCreator mcreator;

	private final JPanel propertyEntries = new JPanel(new GridLayout(0, 1, 5, 5));
	private final JPanel modelEntries = new JPanel(new GridLayout(0, 1, 5, 5));

	private final JButton addProperty = new JButton(UIRES.get("16px.add.gif"));
	private final JButton addState = new JButton(UIRES.get("16px.add.gif"));

	public JItemPropertiesList(MCreator mcreator) {
		super(new BorderLayout());

		propertyEntries.setOpaque(false);
		modelEntries.setOpaque(false);

		this.mcreator = mcreator;

		JPanel topbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topbar.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));

		addProperty.setText(L10N.t("elementgui.item.custom_properties.add"));
		addProperty.addActionListener(e -> {
			propertyId.set(Math.max(propertiesList.size(), propertyId.get()) + 1);
			new JItemPropertiesListEntry(mcreator, propertyEntries, propertiesList, propertyId.get());
		});
		topbar.add(addProperty);

		addState.setText(L10N.t("elementgui.item.custom_models.add"));
		addState.addActionListener(e -> new JItemModelsListEntry(mcreator, modelEntries, modelsList));
		topbar.add(addState);

		JScrollPane left = new JScrollPane(PanelUtils.pullElementUp(propertyEntries));
		left.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 2),
				L10N.t("elementgui.item.custom_properties.name"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		JScrollPane right = new JScrollPane(PanelUtils.pullElementUp(modelEntries));
		right.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 2),
				L10N.t("elementgui.item.custom_models.name"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		add("North", topbar);
		add("Center", PanelUtils.gridElements(1, 0, left, right));
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		addProperty.setEnabled(enabled);
		addState.setEnabled(enabled);
	}

	public void reloadDataLists() {
		propertiesList.forEach(JItemPropertiesListEntry::reloadDataLists);
		modelsList.forEach(JItemModelsListEntry::reloadDataLists);
	}

	public Map<String, Procedure> getProperties() {
		Map<String, Procedure> retVal = new HashMap<>();
		propertiesList.forEach(e -> e.addEntry(retVal));
		return retVal;
	}

	public void setProperties(Map<String, Procedure> properties) {
		properties.forEach((k, v) -> {
			propertyId.set(Math.max(propertiesList.size(), propertyId.get()) + 1);
			new JItemPropertiesListEntry(mcreator, propertyEntries, propertiesList, propertyId.get()).setEntry(k, v);
		});
	}

	public Map<Map<String, Float>, Item.ModelEntry> getModelsList() {
		Map<Map<String, Float>, Item.ModelEntry> retVal = new HashMap<>();
		modelsList.forEach(e -> e.addEntry(retVal));
		return retVal;
	}

	public void setModelsList(Map<Map<String, Float>, Item.ModelEntry> models) {
		models.forEach((k, v) -> new JItemModelsListEntry(mcreator, modelEntries, modelsList).setEntry(k, v));
	}

	public AggregatedValidationResult getValidationResult() {
		return new AggregatedValidationResult(propertiesList.toArray(IValidable[]::new));
	}
}