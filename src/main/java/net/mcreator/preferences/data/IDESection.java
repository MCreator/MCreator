/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.preferences.data;

import net.mcreator.preferences.PreferencesSection;
import net.mcreator.preferences.entries.BooleanEntry;
import net.mcreator.preferences.entries.IntegerEntry;
import net.mcreator.preferences.entries.StringEntry;

public class IDESection extends PreferencesSection {

	public StringEntry editorTheme;
	public IntegerEntry fontSize;
	public BooleanEntry autocomplete;
	public StringEntry autocompleteMode;
	public BooleanEntry autocompleteDocWindow;
	public BooleanEntry lineNumbers;
	public BooleanEntry errorInfoEnable;

	IDESection(String preferencesIdentifier) {
		super(preferencesIdentifier);
		
		editorTheme = addEntry(
				new StringEntry("editorTheme", "MCreator", "MCreator", "Default", "Default-Alt",
						"Dark", "Eclipse", "Idea", "Monokai", "VS"));
		fontSize = addEntry(new IntegerEntry("fontSize", 12, 5, 48));
		autocomplete = addEntry(new BooleanEntry("autocomplete", true));
		autocompleteMode = addEntry(
				new StringEntry("autocompleteMode", "Smart", "Manual", "Trigger on dot", "Smart"));
		autocompleteDocWindow = addEntry(new BooleanEntry("autocompleteDocWindow", true));
		lineNumbers = addEntry(new BooleanEntry("lineNumbers", true));
		errorInfoEnable = addEntry(new BooleanEntry("errorInfoEnable", true));
	}

	@Override public String getSectionKey() {
		return "ide";
	}

}
