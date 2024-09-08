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
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JMinMaxSpinner;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.minecraft.states.PropertyData;
import net.mcreator.ui.minecraft.states.PropertyDataWithValue;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class JBlockPropertiesListEntry extends JPanel {

	private final MCreator mcreator;
	private PropertyData<?> data;
	private final JButton remove = new JButton(UIRES.get("16px.clear"));

	private final JLabel nameLabel = new JLabel(), typeLabel = new JLabel();
	private final JPanel boundsPane, defValuePane = new JPanel(new BorderLayout());
	private JComponent defaultValue;

	public JBlockPropertiesListEntry(JBlockPropertiesStatesList listPanel, IHelpContext gui, JPanel propertyEntries,
			List<JBlockPropertiesListEntry> propertiesList) {
		super(new BorderLayout(20, 5));
		this.mcreator = listPanel.getMCreator();

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		nameLabel.setToolTipText(nameLabel.getText());
		nameLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		nameLabel.setPreferredSize(new Dimension(0, 28));
		ComponentUtils.deriveFont(nameLabel, 16);

		JPanel namePane = new JPanel(new BorderLayout());
		namePane.setOpaque(false);
		namePane.add("North", HelpUtils.wrapWithHelpButton(gui.withEntry("block/custom_property_name"),
				L10N.label("elementgui.block.custom_property.name")));
		namePane.add("Center", nameLabel);
		namePane.setPreferredSize(new Dimension(240, 0));

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
		boundsPane.setPreferredSize(new Dimension(350, 0));
		boundsPane.add("North", HelpUtils.wrapWithHelpButton(gui.withEntry("block/custom_property_values"),
				L10N.label("elementgui.block.custom_property.values")));

		defValuePane.setOpaque(false);

		JPanel line = new JPanel(new BorderLayout());
		line.setLayout(new BoxLayout(line, BoxLayout.X_AXIS));
		line.add(namePane);
		line.add(typePane);
		line.add(boundsPane);
		line.add(PanelUtils.northAndCenterElement(
				HelpUtils.wrapWithHelpButton(gui.withEntry("block/custom_property_default_value"),
						L10N.label("elementgui.block.custom_property.default")),
				PanelUtils.join(FlowLayout.LEFT, defValuePane)));

		remove.setText(L10N.t("elementgui.block.custom_property.remove"));
		remove.addActionListener(e -> listPanel.removeProperty(this));
		add("West", line);
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

	@SuppressWarnings("unchecked") public Block.PropertyEntry getEntry() {
		Block.PropertyEntry entry = new Block.PropertyEntry();
		entry.data = new PropertyDataWithValue<>((PropertyData<Object>) data, data.getValue(defaultValue));
		return entry;
	}

	public void setEntry(Block.PropertyEntry entry) {
		setProperty(entry.data);
	}

	public void setProperty(PropertyDataWithValue<?> prop) {
		data = prop.property();
		nameLabel.setText(data.getName().replace("CUSTOM:", ""));
		nameLabel.setToolTipText(nameLabel.getText());
		typeLabel.setText(getTypeString(data));

		updatePropertyValues(prop);
	}

	private String getTypeString(PropertyData<?> data) {
		return switch (data) {
			case PropertyData.LogicType ignored -> "Logic";
			case PropertyData.IntegerType ignored -> "Integer";
			case null, default -> "Unknown";
		};
	}

	private void updatePropertyValues(PropertyDataWithValue<?> data) {
		defValuePane.add(defaultValue = data.property().getComponent(mcreator, data.value()));
		defaultValue.setEnabled(isEnabled());

		switch (data.property()) {
		case PropertyData.LogicType ignored -> {
			boundsPane.add("Center",
					PanelUtils.join(FlowLayout.LEFT, ComponentUtils.deriveFont(new JLabel("false, true"), 16)));
			defaultValue.setOpaque(false);
		}
		case PropertyData.IntegerType intProp -> {
			JMinMaxSpinner boundsInt = new JMinMaxSpinner(intProp.getMin(), intProp.getMax(), 0, Integer.MAX_VALUE, 1);
			boundsInt.setPreferredSize(new Dimension(300, 22));
			boundsInt.setEnabled(false);
			boundsPane.add("Center", PanelUtils.join(FlowLayout.LEFT, boundsInt));
			defaultValue.setPreferredSize(new Dimension(120, 28));
			defaultValue.setOpaque(false);
		}
		default -> {
		}
		}
	}

}