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
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.workspace.Workspace;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class JPotionListEntry extends JPanel {

	private final JSpinner duration = new JSpinner(new SpinnerNumberModel(3600, 1, 72000, 1));
	private final JSpinner amplifier = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
	private final JComboBox<String> effect = new JComboBox<>();
	private final JCheckBox ambient = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox showParticles = L10N.checkbox("elementgui.common.enable");

	private final Workspace workspace;

	public JPotionListEntry(MCreator mcreator, IHelpContext gui, JPanel parent, List<JPotionListEntry> entryList) {
		super(new FlowLayout(FlowLayout.LEFT));

		this.workspace = mcreator.getWorkspace();

		final JComponent container = PanelUtils.expandHorizontally(this);

		parent.add(container);
		entryList.add(this);

		ElementUtil.loadAllPotionEffects(workspace).forEach(e -> effect.addItem(e.getName()));

		add(L10N.label("elementgui.potion.effect"));
		add(effect);

		add(HelpUtils.wrapWithHelpButton(gui.withEntry("potion/duration"), L10N.label("elementgui.potion.duration")));
		add(duration);

		add(HelpUtils.wrapWithHelpButton(gui.withEntry("potion/amplifier"), L10N.label("elementgui.potion.amplifier")));
		add(amplifier);

		add(HelpUtils.wrapWithHelpButton(gui.withEntry("potion/show_particles"),
				L10N.label("elementgui.potion.show_particles")));
		add(showParticles);

		add(HelpUtils.wrapWithHelpButton(gui.withEntry("potion/ambient"), L10N.label("elementgui.potion.ambient")));
		add(ambient);

		showParticles.setSelected(true);

		JButton remove = new JButton(UIRES.get("16px.clear"));
		remove.setText(L10N.t("elementgui.potion.remove_entry"));
		remove.addActionListener(e -> {
			entryList.remove(this);
			parent.remove(container);
			parent.revalidate();
			parent.repaint();
		});
		add(remove);

		parent.revalidate();
		parent.repaint();
	}

	public Potion.CustomEffectEntry getEntry() {
		Potion.CustomEffectEntry entry = new Potion.CustomEffectEntry();
		entry.effect = new EffectEntry(workspace, (String) effect.getSelectedItem());
		entry.duration = (int) duration.getValue();
		entry.amplifier = (int) amplifier.getValue();
		entry.ambient = ambient.isSelected();
		entry.showParticles = showParticles.isSelected();
		return entry;
	}

	public void setEntry(Potion.CustomEffectEntry e) {
		effect.setSelectedItem(e.effect.getUnmappedValue());
		duration.setValue(e.duration);
		amplifier.setValue(e.amplifier);
		ambient.setSelected(e.ambient);
		showParticles.setSelected(e.showParticles);
	}
}