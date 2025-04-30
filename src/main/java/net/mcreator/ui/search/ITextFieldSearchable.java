/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

package net.mcreator.ui.search;

import javax.annotation.Nullable;
import javax.swing.text.JTextComponent;

public interface ITextFieldSearchable extends ISearchable {

	@Override default void search(@Nullable String searchTerm) {
		JTextComponent component = getSearchTextField();
		if (component == null) {
			return;
		}
		component.requestFocusInWindow();
		if (searchTerm != null) {
			component.setText(searchTerm);
		}
	}

	JTextComponent getSearchTextField();

}
