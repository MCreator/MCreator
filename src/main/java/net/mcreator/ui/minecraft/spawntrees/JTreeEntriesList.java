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

package net.mcreator.ui.minecraft.spawntrees;

import net.mcreator.element.types.Biome;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JTreeEntriesList extends JPanel {

	private final List<JTreeListEntry> entryList = new ArrayList<>();

	private final MCreator mcreator;

	private final JPanel entries = new JPanel(new GridLayout(0, 1, 5, 5));

	public JTreeEntriesList(MCreator mcreator) {
		super(new BorderLayout());
		setOpaque(false);

		this.mcreator = mcreator;

		JPanel topbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topbar.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));

		JButton add = new JButton(UIRES.get("16px.add.gif"));
		add.setText(L10N.t("elementgui.biome.add_tree"));
		topbar.add(add);

		add("North", topbar);

		entries.setOpaque(false);

		add.addActionListener(e -> new JTreeListEntry(mcreator, entries, entryList));

		add("Center", PanelUtils.pullElementUp(entries));

		setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 2),
				L10N.t("elementgui.biome.tree_entries"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));
	}

	public List<Biome.SpawnTree> getSpawns() {
		return entryList.stream().map(JTreeListEntry::getEntry).filter(Objects::nonNull).collect(Collectors.toList());
	}

	public void setSpawns(List<Biome.SpawnTree> pool) {
		pool.forEach(e -> new JTreeListEntry(mcreator, entries, entryList).setEntry(e));
	}
}
