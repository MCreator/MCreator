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

package net.mcreator.ui.minecraft.states.entity;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.entries.JSimpleListEntry;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.states.PropertyData;
import net.mcreator.ui.minecraft.states.PropertyDataWithValue;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class JEntityDataEntry extends JSimpleListEntry<PropertyDataWithValue<?>> {

	private final MCreator mcreator;
	private PropertyData<?> data;

	private final JLabel nameLabel, typeLabel;
	private final JPanel defValuePane = new JPanel(new BorderLayout());
	private JComponent defaultValue;

	public JEntityDataEntry(MCreator mcreator, IHelpContext gui, JPanel parent, List<JEntityDataEntry> entryList) {
		super(parent, entryList);
		this.mcreator = mcreator;

		line.setLayout(new BorderLayout());
		line.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		nameLabel = new JLabel();
		nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		nameLabel.setPreferredSize(new Dimension(0, 28));
		ComponentUtils.deriveFont(nameLabel, 16);

		JPanel namePane = new JPanel(new BorderLayout());
		namePane.setOpaque(false);
		namePane.add("North", HelpUtils.wrapWithHelpButton(gui.withEntry("entity/data_name"),
				L10N.label("elementgui.living_entity.entity_data_entries.name")));
		namePane.add("Center", nameLabel);
		namePane.setPreferredSize(new Dimension(240, 0));

		typeLabel = new JLabel();
		typeLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		typeLabel.setPreferredSize(new Dimension(0, 28));
		ComponentUtils.deriveFont(typeLabel, 16);

		JPanel typePane = new JPanel(new BorderLayout());
		typePane.setOpaque(false);
		typePane.add("North", HelpUtils.wrapWithHelpButton(gui.withEntry("entity/data_type"),
				L10N.label("elementgui.living_entity.entity_data_entries.type")));
		typePane.add("Center", typeLabel);
		typePane.setPreferredSize(new Dimension(240, 0));
		line.add("West", PanelUtils.westAndEastElement(namePane, typePane));

		defValuePane.setOpaque(false);
		line.add("Center", PanelUtils.northAndCenterElement(
				HelpUtils.wrapWithHelpButton(gui.withEntry("entity/data_default_value"),
						L10N.label("elementgui.living_entity.entity_data_entries.default_value")),
				PanelUtils.join(FlowLayout.LEFT, defValuePane)));
	}

	@Override protected void setEntryEnabled(boolean enabled) {
		if (defaultValue != null)
			defaultValue.setEnabled(enabled);
	}

	@SuppressWarnings("unchecked") @Override public PropertyDataWithValue<?> getEntry() {
		return new PropertyDataWithValue<>((PropertyData<Object>) data, data.getValue(defaultValue));
	}

	@Override public void setEntry(PropertyDataWithValue<?> entry) {
		data = entry.property();
		nameLabel.setText(data.getName());
		typeLabel.setText(getTypeString(data));
		defValuePane.add(defaultValue = data.getComponent(mcreator, entry.value()));
		defaultValue.setEnabled(isEnabled());

		if (data instanceof PropertyData.IntegerType) {
			defaultValue.setPreferredSize(new Dimension(150, 28));
			defaultValue.setOpaque(false);
		} else if (data instanceof PropertyData.StringType) {
			((JTextField) defaultValue).setColumns(27);
			defaultValue.setPreferredSize(new Dimension(0, 28));
		} else {
			defaultValue.setOpaque(false);
		}
	}

	private String getTypeString(PropertyData<?> data) {
		if (data instanceof PropertyData.LogicType)
			return "Logic";
		else if (data instanceof PropertyData.StringType)
			return "String";
		else if (data instanceof PropertyData.IntegerType)
			return "Integer";
		else
			return "Unknown";
	}

}
