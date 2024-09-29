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

import com.formdev.flatlaf.ui.FlatLineBorder;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JMinMaxSpinner;
import net.mcreator.ui.component.JStringListField;
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
import java.util.Objects;

public class JBlockPropertiesListEntry extends JPanel {

	private final JBlockPropertiesStatesList listPanel;
	private final MCreator mcreator;
	private PropertyData<?> data;
	private final JButton remove = new JButton(UIRES.get("16px.clear"));

	private final JLabel nameLabel = new JLabel(), typeLabel = new JLabel();
	private final JPanel boundsPane = new JPanel(new BorderLayout(0, 0));

	private final JPanel defaultValuePane = new JPanel(new BorderLayout(0, 0));
	private JComponent defaultValue;

	public JBlockPropertiesListEntry(JBlockPropertiesStatesList listPanel, IHelpContext gui, JPanel propertyEntries,
			List<JBlockPropertiesListEntry> propertiesList) {
		super(new BorderLayout(20, 5));
		this.listPanel = listPanel;
		this.mcreator = listPanel.getMCreator();

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		nameLabel.setToolTipText(nameLabel.getText());
		nameLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		nameLabel.setPreferredSize(new Dimension(0, 28));
		ComponentUtils.deriveFont(nameLabel, 16);

		JPanel namePane = new JPanel(new BorderLayout());
		namePane.add("North", HelpUtils.wrapWithHelpButton(gui.withEntry("block/custom_property_name"),
				L10N.label("elementgui.block.custom_property.name")));
		namePane.add("Center", nameLabel);
		namePane.setPreferredSize(new Dimension(240, 0));

		typeLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		typeLabel.setPreferredSize(new Dimension(0, 28));
		ComponentUtils.deriveFont(typeLabel, 16);

		JPanel typePane = new JPanel(new BorderLayout());
		typePane.add("North", HelpUtils.wrapWithHelpButton(gui.withEntry("block/custom_property_type"),
				L10N.label("elementgui.block.custom_property.type")));
		typePane.add("Center", typeLabel);
		typePane.setPreferredSize(new Dimension(160, 0));

		JPanel boundsPaneWrapper = new JPanel(new BorderLayout());
		boundsPaneWrapper.setPreferredSize(new Dimension(350, 0));
		boundsPaneWrapper.add("North", HelpUtils.wrapWithHelpButton(gui.withEntry("block/custom_property_values"),
				L10N.label("elementgui.block.custom_property.values")));
		boundsPaneWrapper.add("Center", boundsPane);

		JPanel line = new JPanel();
		line.setLayout(new BoxLayout(line, BoxLayout.X_AXIS));
		line.add(namePane);
		line.add(typePane);
		line.add(boundsPaneWrapper);
		line.add(PanelUtils.northAndCenterElement(
				HelpUtils.wrapWithHelpButton(gui.withEntry("block/custom_property_default_value"),
						L10N.label("elementgui.block.custom_property.default")),
				PanelUtils.join(FlowLayout.LEFT, defaultValuePane)));

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
		defaultValue.setEnabled(enabled);
	}

	public PropertyData<?> getPropertyData() {
		return data;
	}

	@SuppressWarnings("unchecked") public PropertyDataWithValue<?> getEntry() {
		return new PropertyDataWithValue<>((PropertyData<Object>) data, data.getValue(defaultValue));
	}

	public void setEntry(PropertyDataWithValue<?> prop) {
		data = prop.property();

		if (data.getName().startsWith("CUSTOM:"))
			nameLabel.setText(data.getName().replace("CUSTOM:", ""));
		else
			nameLabel.setText(Objects.requireNonNullElse(listPanel.propertyRegistryName(data), data.getName()));

		nameLabel.setToolTipText(nameLabel.getText());
		typeLabel.setText(getTypeString(data));

		defaultValuePane.removeAll();
		boundsPane.removeAll();

		defaultValuePane.add(defaultValue = data.getComponent(mcreator, prop.value()));
		defaultValue.setEnabled(isEnabled());

		switch (data) {
		case PropertyData.LogicType ignored -> boundsPane.add("Center",
				PanelUtils.join(FlowLayout.LEFT, ComponentUtils.deriveFont(new JLabel("false, true"), 16)));
		case PropertyData.IntegerType intProp -> {
			JMinMaxSpinner boundsInt = new JMinMaxSpinner(intProp.getMin(), intProp.getMax(), 0, Integer.MAX_VALUE, 1);
			boundsInt.setPreferredSize(new Dimension(300, 28));
			boundsInt.setEnabled(false);
			boundsPane.add("Center", PanelUtils.join(FlowLayout.LEFT, boundsInt));
			defaultValue.setPreferredSize(new Dimension(120, 28));
		}
		case PropertyData.StringType stringProp -> {
			JStringListField boundsString = new JStringListField(mcreator, null);
			boundsString.setPreferredSize(new Dimension(300, 28));
			boundsString.hideButtons();
			boundsString.disableItemCentering();
			boundsString.setEnabled(false);
			boundsString.setTextList(List.of(stringProp.getArrayData()));
			boundsString.setBorder(
					new FlatLineBorder(new Insets(2, 2, 2, 2), UIManager.getColor("Component.borderColor")));
			boundsPane.add("Center", PanelUtils.join(FlowLayout.LEFT, boundsString));
			defaultValue.setPreferredSize(new Dimension(180, 28));
		}
		default -> {
		}
		}
	}

	private static String getTypeString(PropertyData<?> data) {
		return switch (data) {
			case PropertyData.LogicType ignored -> "Logic";
			case PropertyData.IntegerType ignored -> "Integer";
			case PropertyData.StringType ignored -> "Enum";
			case null, default -> "Unknown";
		};
	}

}