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

import com.google.gson.JsonElement;
import net.mcreator.preferences.PreferencesEntry;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.preferences.PreferencesSection;
import net.mcreator.preferences.entries.BooleanEntry;
import net.mcreator.preferences.entries.HiddenEntry;
import net.mcreator.preferences.entries.IntegerEntry;
import net.mcreator.preferences.entries.StringEntry;

import java.io.File;

public class HiddenSection extends PreferencesSection {

	public PreferencesEntry<IconSize> workspaceModElementIconSize;
	public BooleanEntry fullScreen;
	public IntegerEntry projectTreeSplitPos;
	public BooleanEntry workspaceSortAscending;
	public PreferencesEntry<SortType> workspaceSortOrder;
	public PreferencesEntry<File> java_home;
	public StringEntry uiTheme;
	public BooleanEntry enableJavaPlugins;
	public StringEntry lastWebsiteNewsRead;

	HiddenSection(String preferencesIdentifier) {
		super(preferencesIdentifier);

		workspaceModElementIconSize = addEntry(new HiddenEntry<>("workspaceModElementIconSize", IconSize.TILES) {
			@Override public void setValueFromJsonElement(JsonElement object) {
				this.value = IconSize.valueOf(object.getAsString());
			}

			@Override public JsonElement getSerializedValue() {
				return PreferencesManager.gson.toJsonTree(value, IconSize.class);
			}
		});
		fullScreen = addEntry(new BooleanEntry("fullScreen", false));
		projectTreeSplitPos = addEntry(new IntegerEntry("projectTreeSplitPos", 0));
		workspaceSortAscending = addEntry(new BooleanEntry("workspaceSortAscending", true));
		workspaceSortOrder = addEntry(new HiddenEntry<>("workspaceSortOrder", SortType.CREATED) {
			@Override public void setValueFromJsonElement(JsonElement object) {
				this.value = SortType.valueOf(object.getAsString());
			}

			@Override public JsonElement getSerializedValue() {
				return PreferencesManager.gson.toJsonTree(value, SortType.class);
			}
		});
		java_home = addEntry(new HiddenEntry<>("java_home", null) {
			@Override public void setValueFromJsonElement(JsonElement object) {
				this.value = new File(object.getAsString());
			}

			@Override public JsonElement getSerializedValue() {
				return PreferencesManager.gson.toJsonTree(value, File.class);
			}
		});
		uiTheme = addEntry(new StringEntry("uiTheme", "default_dark"));
		enableJavaPlugins = addEntry(new BooleanEntry("enableJavaPlugins", false));
		lastWebsiteNewsRead = addEntry(new StringEntry("lastWebsiteNewsRead", ""));
	}

	@Override public boolean isVisible() {
		return false;
	}

	@Override public String getSectionKey() {
		return "hidden";
	}

	public enum SortType {
		NAME, CREATED, TYPE
	}

	public enum IconSize {
		TILES, LARGE, MEDIUM, SMALL, LIST, DETAILS
	}

}
