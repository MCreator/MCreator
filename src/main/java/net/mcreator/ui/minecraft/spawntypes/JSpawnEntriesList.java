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

package net.mcreator.ui.minecraft.spawntypes;

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

public class JSpawnEntriesList extends JPanel {

	private final List<JSpawnListEntry> entryList = new ArrayList<>();

	private final MCreator mcreator;

	private final JPanel entries = new JPanel();

	private final JButton add = new JButton(UIRES.get("16px.add.gif"));

	public JSpawnEntriesList(MCreator mcreator) {
		super(new BorderLayout());
		setOpaque(false);

		this.mcreator = mcreator;

		JPanel topbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topbar.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));

		add.setText(L10N.t("elementgui.spawnlist.add_entry"));
		topbar.add(add);

		add("North", topbar);

		entries.setLayout(new BoxLayout(entries, BoxLayout.PAGE_AXIS));
		entries.setOpaque(false);

		add.addActionListener(e -> new JSpawnListEntry(mcreator, entries, entryList));

		add("Center", new JScrollPane(PanelUtils.pullElementUp(entries)));

		setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 2),
				L10N.t("elementgui.spawnlist.spawn_entries"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));
		setPreferredSize(new Dimension(getPreferredSize().width, (int) (mcreator.getSize().height * 0.6)));
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		add.setEnabled(false);
	}

	public List<Biome.SpawnEntry> getSpawns() {
		return entryList.stream().map(JSpawnListEntry::getEntry).filter(Objects::nonNull).collect(Collectors.toList());
	}

	public void setSpawns(List<Biome.SpawnEntry> pool) {
		pool.forEach(e -> new JSpawnListEntry(mcreator, entries, entryList).setEntry(e));
	}

}
