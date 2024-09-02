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

import net.mcreator.element.types.Block;
import net.mcreator.ui.component.JMinMaxSpinner;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.minecraft.states.PropertyData;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class JBlockPropertiesListEntry extends JPanel {

	private PropertyData<?> data;

	private final JLabel nameLabel, typeLabel;
	private final JPanel boundsPane;
	private final JButton remove = new JButton(UIRES.get("16px.clear"));

	public JBlockPropertiesListEntry(JBlockPropertiesStatesList listPanel, IHelpContext gui, JPanel propertyEntries,
			List<JBlockPropertiesListEntry> propertiesList, PropertyData<?> data) {
		super(new BorderLayout(20, 5));
		this.data = data;

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		nameLabel = new JLabel(data.getName().replace("CUSTOM:", ""));
		nameLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		nameLabel.setPreferredSize(new Dimension(0, 28));
		ComponentUtils.deriveFont(nameLabel, 16);

		JPanel namePane = new JPanel(new BorderLayout());
		namePane.setOpaque(false);
		namePane.add("North", HelpUtils.wrapWithHelpButton(gui.withEntry("block/custom_property_name"),
				L10N.label("elementgui.block.custom_property.name")));
		namePane.add("Center", nameLabel);
		namePane.setPreferredSize(new Dimension(240, 0));

		typeLabel = new JLabel(getTypeString(data));
		typeLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		typeLabel.setPreferredSize(new Dimension(0, 28));
		ComponentUtils.deriveFont(typeLabel, 16);

		JPanel typePane = new JPanel(new BorderLayout());
		typePane.setOpaque(false);
		typePane.add("North", HelpUtils.wrapWithHelpButton(gui.withEntry("block/custom_property_type"),
				L10N.label("elementgui.block.custom_property.type")));
		typePane.add("Center", typeLabel);
		typePane.setPreferredSize(new Dimension(160, 0));

		boundsPane = new JPanel(new BorderLayout());
		boundsPane.setOpaque(false);
		boundsPane.add("North", HelpUtils.wrapWithHelpButton(gui.withEntry("block/custom_property_values"),
				L10N.label("elementgui.block.custom_property.values")));
		updatePropertyValues(data);

		remove.setText(L10N.t("elementgui.block.custom_property.remove"));
		remove.addActionListener(e -> listPanel.removeProperty(this));
		add("West", PanelUtils.westAndEastElement(namePane, typePane));
		add("Center", boundsPane);
		add("East", PanelUtils.pullElementUp(remove));

		propertiesList.add(this);
		propertyEntries.add(this);
		propertyEntries.revalidate();
		propertyEntries.repaint();
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		remove.setEnabled(enabled);
	}

	public PropertyData<?> getPropertyData() {
		return data;
	}

	public Block.PropertyEntry getEntry() {
		Block.PropertyEntry entry = new Block.PropertyEntry();
		entry.property = data;
		return entry;
	}

	public void setEntry(Block.PropertyEntry entry) {
		data = entry.property;
		nameLabel.setText(data.getName());
		typeLabel.setText(getTypeString(data));

		updatePropertyValues(data);
	}

	private String getTypeString(PropertyData<?> data) {
		return switch (data) {
			case PropertyData.LogicType ignored -> "Logic";
			case PropertyData.IntegerType ignored -> "Integer";
			case null, default -> "Unknown";
		};
	}

	private void updatePropertyValues(PropertyData<?> data) {
		switch (data) {
		case PropertyData.LogicType ignored -> boundsPane.add("Center",
				PanelUtils.join(FlowLayout.LEFT, ComponentUtils.deriveFont(new JLabel("false, true"), 16)));
		case PropertyData.IntegerType intProp -> {
			JMinMaxSpinner boundsInt = new JMinMaxSpinner(intProp.getMin(), intProp.getMax(), 0, Integer.MAX_VALUE, 1);
			boundsInt.setPreferredSize(new Dimension(300, 22));
			boundsInt.setEnabled(false);
			boundsPane.add("Center", PanelUtils.join(FlowLayout.LEFT, boundsInt));
		}
		default -> {
		}
		}
	}

}