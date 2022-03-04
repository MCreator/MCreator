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

import net.mcreator.element.types.LivingEntity;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.minecraft.JEntriesList;
import net.mcreator.ui.minecraft.spawntypes.JSpawnListEntry;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JEntityDataList extends JEntriesList {

	private final List<JEntityDataEntry> entryList = new ArrayList<>();

	private final JPanel entries = new JPanel();

	private final JButton add = new JButton(UIRES.get("16px.add.gif"));

	public JEntityDataList(MCreator mcreator, IHelpContext gui) {
		super(mcreator, new BorderLayout(), gui);
		setOpaque(false);

		JPanel topbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topbar.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));

		add.setText(L10N.t("elementgui.common.add_entry"));
		topbar.add(add);

		add("North", topbar);

		entries.setLayout(new BoxLayout(entries, BoxLayout.PAGE_AXIS));
		entries.setOpaque(false);

		add.addActionListener(e -> {
			JEntityDataEntry entry = new JEntityDataEntry(gui, entries, entryList);
			registerEntryUI(entry);
		});

		add("Center", new JScrollPane(PanelUtils.pullElementUp(entries)));

		setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 2),
				L10N.t("elementgui.living_entity.entity_data_entries"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));
		setPreferredSize(new Dimension(getPreferredSize().width, (int) (mcreator.getSize().height * 0.6)));
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		add.setEnabled(false);
	}

	public List<LivingEntity.EntityDataEntry> getEntries() {
		return entryList.stream().map(JEntityDataEntry::getEntry).filter(Objects::nonNull).collect(Collectors.toList());
	}

	public void setEntries(List<LivingEntity.EntityDataEntry> pool) {
		pool.forEach(e -> {
			JEntityDataEntry entry = new JEntityDataEntry(gui, entries, entryList);
			registerEntryUI(entry);
			entry.setEntry(e);
		});
	}

}
