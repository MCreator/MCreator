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

import net.mcreator.preferences.entries.BooleanEntry;
import net.mcreator.preferences.entries.IntegerEntry;
import net.mcreator.preferences.entries.StringEntry;

public class IDESection {
	public StringEntry editorTheme;
	public IntegerEntry fontSize;
	public BooleanEntry autocomplete;
	public StringEntry autocompleteMode;
	public BooleanEntry autocompleteDocWindow;
	public BooleanEntry lineNumbers;
	public BooleanEntry errorInfoEnable;

	IDESection() {
		editorTheme = PreferencesData.register(
				new StringEntry("editorTheme", "MCreator", PreferencesData.IDE, "MCreator", "Default", "Default-Alt",
						"Dark", "Eclipse", "Idea", "Monokai", "VS"));
		fontSize = PreferencesData.register(new IntegerEntry("fontSize", 12, PreferencesData.IDE, 5, 48));
		autocomplete = PreferencesData.register(new BooleanEntry("autocomplete", true, PreferencesData.IDE));
		autocompleteMode = PreferencesData.register(
				new StringEntry("autocompleteMode", "Smart", PreferencesData.IDE, "Manual", "Trigger on dot", "Smart"));
		autocompleteDocWindow = PreferencesData.register(new BooleanEntry("autocompleteDocWindow", true, PreferencesData.IDE));
		lineNumbers = PreferencesData.register(new BooleanEntry("lineNumbers", true, PreferencesData.IDE));
		errorInfoEnable = PreferencesData.register(new BooleanEntry("errorInfoEnable", true, PreferencesData.IDE));
	}

}
