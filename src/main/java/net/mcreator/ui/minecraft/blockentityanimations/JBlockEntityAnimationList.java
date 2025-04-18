/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.ui.minecraft.blockentityanimations;

import net.mcreator.element.types.Block;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.entries.JSimpleEntriesList;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;

import javax.swing.*;
import java.util.List;

public class JBlockEntityAnimationList extends JSimpleEntriesList<JBlockEntityAnimationListEntry, Block.AnimationEntry> {

	public JBlockEntityAnimationList(MCreator mcreator, IHelpContext gui) {
		// TODO: translations

		super(mcreator, gui);

		add.setText(L10N.t("elementgui.living_entity.add_playable_animation"));

		setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2),
				BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
						BorderFactory.createEmptyBorder(2, 2, 2, 2))));
	}

	@Override
	protected JBlockEntityAnimationListEntry newEntry(JPanel parent, List<JBlockEntityAnimationListEntry> entryList,
			boolean userAction) {
		return new JBlockEntityAnimationListEntry(mcreator, gui, parent, entryList);
	}

}
