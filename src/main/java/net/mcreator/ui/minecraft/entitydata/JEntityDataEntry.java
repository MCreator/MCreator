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

package net.mcreator.ui.minecraft.entitydata;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.minecraft.states.DefaultPropertyValue;
import net.mcreator.ui.minecraft.states.PropertyData;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class JEntityDataEntry<T> extends JPanel {

	private final MCreator mcreator;
	private final PropertyData<T> data;

	private final JPanel defValuePane = new JPanel();
	private JComponent defaultValue;

	public JEntityDataEntry(MCreator mcreator, IHelpContext gui, JPanel parent, List<JEntityDataEntry<?>> entryList,
			PropertyData<T> data) {
		super(new BorderLayout());
		this.mcreator = mcreator;
		this.data = data;
		this.defaultValue = data.getComponent(mcreator, null);

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		final JComponent container = PanelUtils.expandHorizontally(this);

		parent.add(container);
		entryList.add(this);

		JLabel nameLabel = new JLabel(data.getName());
		nameLabel.setPreferredSize(new Dimension(0, 28));
		ComponentUtils.deriveFont(nameLabel, 16);

		JPanel namePane = new JPanel(new BorderLayout());
		namePane.setOpaque(false);
		namePane.add("North", HelpUtils.wrapWithHelpButton(gui.withEntry("entity/data_name"),
				L10N.label("elementgui.living_entity.entity_data_entries.name")));
		namePane.add("Center", nameLabel);
		namePane.setPreferredSize(new Dimension(240, 0));

		JLabel typeLabel = new JLabel(switch (data.getClass().getSimpleName()) {
			case "LogicType" -> "Logic";
			case "StringType" -> "String";
			default -> "Number";
		});
		typeLabel.setPreferredSize(new Dimension(0, 28));
		ComponentUtils.deriveFont(typeLabel, 16);

		JPanel typePane = new JPanel(new BorderLayout());
		typePane.setOpaque(false);
		typePane.add("North", HelpUtils.wrapWithHelpButton(gui.withEntry("entity/data_type"),
				L10N.label("elementgui.living_entity.entity_data_entries.type")));
		typePane.add("Center", typeLabel);
		typePane.setPreferredSize(new Dimension(80, 0));
		add("West", PanelUtils.westAndEastElement(namePane, typePane));

		defValuePane.add(defaultValue);
		add("Center", PanelUtils.join(FlowLayout.LEFT, PanelUtils.northAndCenterElement(
				HelpUtils.wrapWithHelpButton(gui.withEntry("entity/data_default_value"),
						L10N.label("elementgui.living_entity.entity_data_entries.default_value")), defValuePane)));

		JButton remove = new JButton(UIRES.get("16px.clear"));
		remove.setText(L10N.t("elementgui.common.remove_entry"));
		remove.addActionListener(e -> {
			entryList.remove(this);
			parent.remove(container);
			parent.revalidate();
			parent.repaint();
		});
		add("East", PanelUtils.centerInPanel(remove));

		parent.revalidate();
		parent.repaint();
	}

	public DefaultPropertyValue<?> getEntry() {
		return new DefaultPropertyValue<>(data, data.getValue(defaultValue));
	}

	public void setEntry(Object defaultValue) {
		defValuePane.removeAll();
		defValuePane.add(this.defaultValue = data.getComponent(mcreator, defaultValue));
	}
}
