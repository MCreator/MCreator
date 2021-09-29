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

package net.mcreator.ui.dialogs;

import net.mcreator.ui.MCreator;
import net.mcreator.workspace.Workspace;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;

public class StringSelectorDialog extends ListSelectorDialog<String> {
	public StringSelectorDialog(MCreator mcreator, Function<Workspace, List<String>> entryProvider) {
		super(mcreator, entryProvider);
	}

	@Override Predicate<String> getFilter(String term) {
		return s -> s.toLowerCase(Locale.ENGLISH).contains(term.toLowerCase(Locale.ENGLISH));
	}

	public static String openSelectorDialog(MCreator mcreator, Function<Workspace, List<String>> entryProvider,
			String title, String message) {
		var stringSelector = new StringSelectorDialog(mcreator, entryProvider);
		stringSelector.message.setText(message);
		stringSelector.setTitle(title);
		stringSelector.setVisible(true);
		return stringSelector.list.getSelectedValue();
	}
}