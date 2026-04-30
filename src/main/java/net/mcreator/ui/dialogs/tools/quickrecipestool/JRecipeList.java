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

package net.mcreator.ui.dialogs.tools.quickrecipestool;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.entries.JSimpleEntriesList;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.validation.ValidationGroup;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class JRecipeList extends JSimpleEntriesList<JRecipeListEntry, RecipeListEntry> {

	private final ValidationGroup validableElements;

	public JRecipeList(MCreator mcreator, IHelpContext gui, ValidationGroup validableElements) {
		super(mcreator, gui);
		this.validableElements = validableElements;

		add.setText(L10N.t("dialog.tools.quick_recipes.add_recipe"));

		JRecipeListEntry entry = newEntry(entries, entryList, false);
		if (entry != null) {
			entryList.add(entry);
			entry.setEnabled(this.isEnabled());
			registerEntryUI(entry);
		}

		ComponentUtils.makeSection(this, L10N.t("dialog.tools.quick_recipes.recipes"));
	}

	@Override protected JRecipeListEntry newEntry(JPanel parent, List<JRecipeListEntry> entryList, boolean userAction) {
		return new JRecipeListEntry(mcreator, parent, entryList, validableElements);
	}
}
