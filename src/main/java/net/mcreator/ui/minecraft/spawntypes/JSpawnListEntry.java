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

import net.mcreator.element.parts.EntityEntry;
import net.mcreator.element.types.Biome;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JMinMaxSpinner;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.entries.JSimpleListEntry;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.workspace.Workspace;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

public class JSpawnListEntry extends JSimpleListEntry<Biome.SpawnEntry> {

	private final JSpinner spawningProbability = new JSpinner(new SpinnerNumberModel(20, 1, 1000, 1));
	private final JMinMaxSpinner numberOfMobsPerGroup = new JMinMaxSpinner(4, 4, 1, 1000, 1);
	private final JComboBox<String> mobSpawningType = new SearchableComboBox<>(
			ElementUtil.getDataListAsStringArray("mobspawntypes"));
	private final JComboBox<String> entityType = new SearchableComboBox<>();

	private final Workspace workspace;

	public JSpawnListEntry(MCreator mcreator, IHelpContext gui, JPanel parent, List<JSpawnListEntry> entryList) {
		super(parent, entryList);

		this.workspace = mcreator.getWorkspace();

		numberOfMobsPerGroup.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Theme.current().getAltBackgroundColor()),
				BorderFactory.createEmptyBorder(2, 2, 2, 2)));
		numberOfMobsPerGroup.setAllowEqualValues(true);

		line.add(L10N.label("dialog.spawn_list_entry.entity"));
		line.add(entityType);

		line.add(HelpUtils.wrapWithHelpButton(gui.withEntry("entity/spawn_type"),
				L10N.label("dialog.spawn_list_entry.type")));
		line.add(mobSpawningType);

		line.add(HelpUtils.wrapWithHelpButton(gui.withEntry("entity/spawn_weight"),
				L10N.label("dialog.spawn_list_entry.weight")));
		line.add(spawningProbability);

		line.add(HelpUtils.wrapWithHelpButton(gui.withEntry("entity/spawn_group_size"),
				L10N.label("dialog.spawn_list_entry.group_size")));
		line.add(numberOfMobsPerGroup);
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();

		ComboBoxUtil.updateComboBoxContents(entityType,
				ElementUtil.loadAllSpawnableEntities(workspace).stream().map(DataListEntry::getName)
						.collect(Collectors.toList()));
	}

	@Override protected void setEntryEnabled(boolean enabled) {
		spawningProbability.setEnabled(enabled);
		numberOfMobsPerGroup.setEnabled(enabled);
		mobSpawningType.setEnabled(enabled);
		entityType.setEnabled(enabled);
	}

	@Override public Biome.SpawnEntry getEntry() {
		Biome.SpawnEntry entry = new Biome.SpawnEntry();
		entry.entity = new EntityEntry(workspace, (String) entityType.getSelectedItem());
		entry.spawnType = (String) mobSpawningType.getSelectedItem();
		entry.weight = (int) spawningProbability.getValue();
		entry.minGroup = numberOfMobsPerGroup.getIntMinValue();
		entry.maxGroup = numberOfMobsPerGroup.getIntMaxValue();
		return entry;
	}

	@Override public void setEntry(Biome.SpawnEntry e) {
		entityType.setSelectedItem(e.entity.getUnmappedValue());
		mobSpawningType.setSelectedItem(e.spawnType);
		spawningProbability.setValue(e.weight);
		numberOfMobsPerGroup.setMinValue(e.minGroup);
		numberOfMobsPerGroup.setMaxValue(e.maxGroup);
	}

}
