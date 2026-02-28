/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

package net.mcreator.ui.minecraft.bedrock.digger;

import net.mcreator.element.types.bedrock.BEItem;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.entries.JSimpleListEntry;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.MCItemHolder;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class JDiggerEntry extends JSimpleListEntry<BEItem.DiggerEntry> {

	private final MCItemHolder block;
	private final JSpinner speed = new JSpinner(new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1));

	public JDiggerEntry(MCreator mcreator, IHelpContext gui, JPanel parent,
			List<? extends JSimpleListEntry<BEItem.DiggerEntry>> entryList) {
		super(parent, entryList);

		block = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndTags, true);

		line.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		line.add(HelpUtils.wrapWithHelpButton(gui.withEntry("beitem/digger_block"),
				L10N.label("elementgui.beitem.digger_block")));
		line.add(block);

		line.add(HelpUtils.wrapWithHelpButton(gui.withEntry("beitem/digger_speed"),
				L10N.label("elementgui.beitem.digger_speed")));
		line.add(speed);
	}

	@Override protected void setEntryEnabled(boolean enabled) {
		block.setEnabled(enabled);
		speed.setEnabled(enabled);
	}

	@Override public BEItem.DiggerEntry getEntry() {
		BEItem.DiggerEntry entry = new BEItem.DiggerEntry();
		entry.block = block.getBlock();
		entry.speed = (int) speed.getValue();

		return entry;
	}

	@Override public void setEntry(BEItem.DiggerEntry entry) {
		block.setBlock(entry.block);
		speed.setValue(entry.speed);
	}
}
