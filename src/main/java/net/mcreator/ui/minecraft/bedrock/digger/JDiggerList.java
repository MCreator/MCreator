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
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.entries.JSimpleEntriesList;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;

import javax.annotation.Nullable;
import javax.swing.*;
import java.util.List;

public class JDiggerList extends JSimpleEntriesList<JDiggerEntry, BEItem.DiggerEntry> {

	public JDiggerList(MCreator mcreator, IHelpContext gui) {
		super(mcreator, gui);

		add.setText(L10N.t("elementgui.beitem.add_digger_entry"));

		ComponentUtils.borderWrap(this);
	}

	@Nullable @Override
	protected JDiggerEntry newEntry(JPanel parent, List<JDiggerEntry> entryList, boolean userAction) {
		return new JDiggerEntry(mcreator, gui, parent, entryList);
	}
}
