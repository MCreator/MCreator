/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
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

package net.mcreator.ui.minecraft.jsonvalues;

import net.mcreator.element.parts.EntityEntry;
import net.mcreator.element.types.Biome;
import net.mcreator.element.types.Json;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.minecraft.spawntypes.JSpawnListEntry;
import net.mcreator.workspace.Workspace;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class JJsonListValue extends JPanel {
	private final JSpinner name = new JSpinner(new SpinnerNumberModel(20, 1, 1000, 1));
	private final JComboBox<String> type = new JComboBox<>(
			new String[] { "boolean", "Direction", "double", "int", "Itemstack", "String"});

	private final Workspace workspace;

	public JJsonListValue(MCreator mcreator, JPanel parent, List<JJsonListValue> valueList) {
		super(new FlowLayout(FlowLayout.LEFT));

		this.workspace = mcreator.getWorkspace();

		final JComponent container = PanelUtils.expandHorizontally(this);

		parent.add(container);
		valueList.add(this);

		add(new JLabel("Value: "));
		add(name);

		add(new JLabel("Type: "));
		add(type);


		JButton remove = new JButton(UIRES.get("16px.clear"));
		remove.setText("Remove this value");
		remove.addActionListener(e -> {
			valueList.remove(this);
			parent.remove(container);
			parent.revalidate();
			parent.repaint();
		});
		add(remove);

		parent.revalidate();
		parent.repaint();
	}

	public Json.Value getValue() {
		Json.Value value = new Json.Value();
		value.name = (String) name.getValue();
		value.type = (String) type.getSelectedItem();
		return value;
	}

	public void setValue(Json.Value v) {
		name.setValue(v.name);
		type.setSelectedItem(v.type);
	}
}
