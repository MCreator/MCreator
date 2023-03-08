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
import net.mcreator.preferences.entries.PreferenceEntry;

import java.io.File;

public class Hidden {
	public PreferenceEntry<IconSize> workspaceModElementIconSize;
	public BooleanEntry fullScreen;
	public IntegerEntry projectTreeSplitPos;
	public BooleanEntry workspaceSortAscending;
	public PreferenceEntry<SortType> workspaceSortType;
	public PreferenceEntry<File> java_home;
	public PreferenceEntry<String> uiTheme;
	public BooleanEntry enableJavaPlugins;

	public Hidden() {
		workspaceModElementIconSize = Preferences.register(
				new PreferenceEntry<>("workspaceModElementIconSize", IconSize.TILES,
						Preferences.HIDDEN));
		fullScreen = Preferences.register(new BooleanEntry("fullScreen", false, Preferences.HIDDEN));
		projectTreeSplitPos = Preferences.register(new IntegerEntry("projectTreeSplitPos", 0, Preferences.HIDDEN));
		workspaceSortAscending = Preferences.register(
				new BooleanEntry("workspaceSortAscending", false, Preferences.HIDDEN));
		workspaceSortType = Preferences.register(
				new PreferenceEntry<>("workspaceSortType", SortType.CREATED,
						Preferences.HIDDEN));
		java_home = Preferences.register(new PreferenceEntry<>("java_home", null, Preferences.HIDDEN));
		uiTheme = Preferences.register(new PreferenceEntry<>("uiTheme", "default_dark", Preferences.HIDDEN));
		enableJavaPlugins = Preferences.register(new BooleanEntry("fullScreen", false, Preferences.HIDDEN));
	}

	public enum SortType {
		NAME, CREATED, TYPE, LOADORDER
	}

	public enum IconSize {
		TILES, LARGE, MEDIUM, SMALL, LIST, DETAILS
	}

}
