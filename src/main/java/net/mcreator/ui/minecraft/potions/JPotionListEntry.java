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

package net.mcreator.ui.minecraft.potions;

import net.mcreator.element.parts.EffectEntry;
import net.mcreator.element.types.Potion;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.entries.JSimpleListEntry;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.workspace.Workspace;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

public class JPotionListEntry extends JSimpleListEntry<Potion.CustomEffectEntry> {

	private final JSpinner duration = new JSpinner(new SpinnerNumberModel(3600, 1, 72000, 1));
	private final JSpinner amplifier = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
	private final JComboBox<String> effect = new JComboBox<>();
	private final JCheckBox ambient = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox showParticles = L10N.checkbox("elementgui.common.enable");

	private final Workspace workspace;

	public JPotionListEntry(MCreator mcreator, IHelpContext gui, JPanel parent, List<JPotionListEntry> entryList) {
		super(parent, entryList);

		this.workspace = mcreator.getWorkspace();

		ambient.setOpaque(false);
		showParticles.setOpaque(false);

		line.add(L10N.label("elementgui.potion.effect"));
		line.add(effect);

		line.add(HelpUtils.wrapWithHelpButton(gui.withEntry("potion/duration"),
				L10N.label("elementgui.potion.duration")));
		line.add(duration);

		line.add(HelpUtils.wrapWithHelpButton(gui.withEntry("potion/amplifier"),
				L10N.label("elementgui.potion.amplifier")));
		line.add(amplifier);

		line.add(HelpUtils.wrapWithHelpButton(gui.withEntry("potion/show_particles"),
				L10N.label("elementgui.potion.show_particles")));
		line.add(showParticles);

		line.add(
				HelpUtils.wrapWithHelpButton(gui.withEntry("potion/ambient"), L10N.label("elementgui.potion.ambient")));
		line.add(ambient);

		showParticles.setSelected(true);
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();

		ComboBoxUtil.updateComboBoxContents(effect,
				ElementUtil.loadAllPotionEffects(workspace).stream().map(DataListEntry::getName)
						.collect(Collectors.toList()), "SPEED");
	}

	@Override protected void setEntryEnabled(boolean enabled) {
		duration.setEnabled(enabled);
		amplifier.setEnabled(enabled);
		effect.setEnabled(enabled);
		ambient.setEnabled(enabled);
		showParticles.setEnabled(enabled);
	}

	@Override public Potion.CustomEffectEntry getEntry() {
		Potion.CustomEffectEntry entry = new Potion.CustomEffectEntry();
		entry.effect = new EffectEntry(workspace, (String) effect.getSelectedItem());
		entry.duration = (int) duration.getValue();
		entry.amplifier = (int) amplifier.getValue();
		entry.ambient = ambient.isSelected();
		entry.showParticles = showParticles.isSelected();
		return entry;
	}

	@Override public void setEntry(Potion.CustomEffectEntry e) {
		effect.setSelectedItem(e.effect.getUnmappedValue());
		duration.setValue(e.duration);
		amplifier.setValue(e.amplifier);
		ambient.setSelected(e.ambient);
		showParticles.setSelected(e.showParticles);
	}

}