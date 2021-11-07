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

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.util.Tuple;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JItemModelsList extends JPanel {

	private final List<JItemModelsListEntry> entryList = new ArrayList<>();

	private final MCreator mcreator;

	private final JPanel entries = new JPanel(new GridLayout(0, 1, 5, 5));

	private final JButton add = new JButton(UIRES.get("16px.add.gif"));

	public JItemModelsList(MCreator mcreator) {
		super(new BorderLayout());
		setOpaque(false);

		this.mcreator = mcreator;

		JPanel topbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topbar.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));

		add.setText(L10N.t("elementgui.item.custom_models.add"));
		topbar.add(add);

		add("North", topbar);

		entries.setOpaque(false);

		add.addActionListener(e -> new JItemModelsListEntry(mcreator, entries, entryList));

		add("Center", new JScrollPane(PanelUtils.pullElementUp(entries)));

		setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 2),
				L10N.t("elementgui.item.custom_models.name"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		add.setEnabled(enabled);
	}

	public void reloadDataLists() {
		entryList.forEach(JItemModelsListEntry::reloadDataLists);
	}

	public Map<Map<String, Float>, Tuple<String, Integer>> getModelsList() {
		Map<Map<String, Float>, Tuple<String, Integer>> retVal = new HashMap<>();
		entryList.forEach(e -> e.addEntry(retVal));
		return retVal;
	}

	public void setModelsList(Map<Map<String, Float>, Tuple<String, Integer>> properties) {
		properties.forEach((k, v) -> new JItemModelsListEntry(mcreator, entries, entryList).setEntry(k, v));
	}
}