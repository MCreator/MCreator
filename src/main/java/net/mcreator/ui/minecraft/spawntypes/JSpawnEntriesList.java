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
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.component.entries.JSingleEntriesList;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JSpawnEntriesList extends JSingleEntriesList<JSpawnListEntry, Biome.SpawnEntry> {

	public JSpawnEntriesList(MCreator mcreator, IHelpContext gui) {
		super(mcreator, gui);

		add.setText(L10N.t("elementgui.spawnlist.add_entry"));
		add.addActionListener(e -> {
			JSpawnListEntry entry = new JSpawnListEntry(mcreator, gui, entries, entryList);
			registerEntryUI(entry);
		});

		setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.spawnlist.spawn_entries"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));
		setPreferredSize(new Dimension(getPreferredSize().width, (int) (mcreator.getSize().height * 0.6)));
	}

	@Override public List<Biome.SpawnEntry> getEntries() {
		return entryList.stream().map(JSpawnListEntry::getEntry).filter(Objects::nonNull).collect(Collectors.toList());
	}

	@Override public void setEntries(List<Biome.SpawnEntry> pool) {
		pool.forEach(e -> {
			JSpawnListEntry entry = new JSpawnListEntry(mcreator, gui, entries, entryList);
			registerEntryUI(entry);
			entry.setEntry(e);
		});
	}

}
